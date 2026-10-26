package com.ruoyi.crm.wecom;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.crm.domain.CrmWecomConfig;
import com.ruoyi.crm.domain.CrmWecomKfConfig;
import com.ruoyi.crm.domain.CrmWecomKfConversation;
import com.ruoyi.crm.domain.CrmWecomKfMessage;
import com.ruoyi.crm.domain.CrmWecomKfSyncCursor;
import com.ruoyi.crm.domain.dto.InboxAgentInboundPayload;
import com.ruoyi.crm.domain.enums.InboxChannel;
import com.ruoyi.crm.event.ActivityEventProducer;
import com.ruoyi.crm.mapper.CrmWecomConfigMapper;
import com.ruoyi.crm.mapper.CrmWecomKfConfigMapper;
import com.ruoyi.crm.mapper.CrmWecomKfConversationMapper;
import com.ruoyi.crm.mapper.CrmWecomKfSyncCursorMapper;
import com.ruoyi.crm.service.IInboxAgentInboundDispatchService;
import com.ruoyi.crm.service.IWecomKfMessageService;
import com.ruoyi.crm.service.InboxChannelGateService;
import com.ruoyi.crm.service.support.TenantDataSourceSupport;
import com.ruoyi.crm.wecom.realtime.WecomKfInboxEventPublisher;
import com.ruoyi.crm.wecom.realtime.WecomKfInboxSsePayloads;

/**
 * 微信客服消息拉取、双端落库、会话 upsert 与 Webhook 转发。
 * <p>
 * 回调明文 XML 由 {@link #handleFromCallbackXml(String)} 入口处理。
 * access_token 通过 {@link WecomAccessTokenService} 获取（复用自建应用的 Secret）。
 */
@Service
public class WecomKfMessageService
{
    private static final Logger log = LoggerFactory.getLogger(WecomKfMessageService.class);

    private static final int SYNC_LIMIT = 1000;

    /** origin=3 客户发送；其余视为出站（坐席/系统/工作流） */
    private static final int ORIGIN_CUSTOMER = 3;

    @Autowired
    private WecomTencentApiClient apiClient;

    @Autowired
    private WecomAccessTokenService accessTokenService;

    @Autowired
    private CrmWecomConfigMapper wecomConfigMapper;

    @Autowired
    private CrmWecomKfConfigMapper kfConfigMapper;

    @Autowired
    private IWecomKfMessageService wecomKfMessageService;

    @Autowired
    private CrmWecomKfConversationMapper conversationMapper;

    @Autowired
    private CrmWecomKfSyncCursorMapper syncCursorMapper;

    @Autowired
    private ActivityEventProducer activityEventProducer;

    @Autowired
    private WecomIdentityResolutionSupport identityResolutionSupport;

    @Autowired
    private InboxChannelGateService inboxChannelGateService;

    @Autowired
    private WecomKfInboxEventPublisher inboxEventPublisher;

    @Autowired
    private IInboxAgentInboundDispatchService inboxAgentInboundDispatchService;

    @Autowired(required = false)
    private com.ruoyi.crm.service.support.ContactServiceSummaryHooks contactServiceSummaryHooks;

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * 从回调明文 XML 中提取 KF 事件并处理。由 {@code CrmWecomCallbackController} 调用。
     */
    public void handleFromCallbackXml(String plainXml)
    {
        if (plainXml == null || plainXml.isEmpty())
        {
            return;
        }
        String event = getCdataOrText(plainXml, "Event");
        if (!"kf_msg_or_event".equals(event))
        {
            return;
        }

        String callbackToken = getCdataOrText(plainXml, "Token");
        String openKfId = getCdataOrText(plainXml, "OpenKfId");
        String toUserName = getCdataOrText(plainXml, "ToUserName");

        if (callbackToken == null || callbackToken.isEmpty() || openKfId == null || openKfId.isEmpty())
        {
            log.warn("WecomKf handleFromCallbackXml: missing Token or OpenKfId");
            return;
        }
        if (toUserName == null || toUserName.isEmpty())
        {
            log.warn("WecomKf handleFromCallbackXml: missing ToUserName (corpId)");
            return;
        }

        String corpId = toUserName.trim();

        CrmWecomKfConfig kfCfg = kfConfigMapper.selectByCorpId(corpId);
        if (kfCfg == null || kfCfg.getEnabled() == null || kfCfg.getEnabled() != 1)
        {
            log.info("WecomKf: KF not enabled for corpId={}", corpId);
            return;
        }

        if (!isOpenKfidAllowed(kfCfg, openKfId.trim()))
        {
            log.info("WecomKf: open_kfid={} not in enabled openKfids, skip. corpId={}", openKfId, corpId);
            return;
        }

        CrmWecomConfig wecomCfg = wecomConfigMapper.selectByCorpId(corpId);
        if (wecomCfg == null)
        {
            log.warn("WecomKf: no wecom config for corpId={}", corpId);
            return;
        }

        pullAndForward(wecomCfg, kfCfg, callbackToken.trim(), openKfId.trim());
    }

    void pullAndForward(CrmWecomConfig wecomCfg, CrmWecomKfConfig kfCfg, String callbackToken, String openKfId)
    {
        String accessToken = accessTokenService.getAgentAccessToken(wecomCfg);
        String corpId = wecomCfg.getCorpId();
        String cursorKey = openKfId != null ? openKfId : "";

        CrmWecomKfSyncCursor cursorRow = ensureCursor(corpId, cursorKey);
        Date enabledAt = cursorRow.getEnabledAt() != null ? cursorRow.getEnabledAt() : new Date();
        String cursor = cursorRow.getCursor() != null ? cursorRow.getCursor() : "";

        boolean hasMore = true;
        int totalProcessed = 0;
        String lastSuccessfulCursor = cursor;

        while (hasMore)
        {
            JSONObject res = apiClient.kfSyncMsg(accessToken, cursor, callbackToken, openKfId, SYNC_LIMIT);

            log.info("WecomKf sync_msg RAW response (cursor={}): {}", cursor, res.toJSONString());

            int errcode = res.getIntValue("errcode");
            if (errcode != 0)
            {
                log.warn("WecomKf sync_msg errcode={} errmsg={} corpId={} cursor={}",
                        errcode, res.getString("errmsg"), corpId, cursor);
                return;
            }

            JSONArray msgList = res.getJSONArray("msg_list");
            if (msgList == null || msgList.isEmpty())
            {
                log.info("WecomKf sync_msg OK, no more messages. corpId={}", corpId);
                String next = res.getString("next_cursor");
                if (StringUtils.hasText(next))
                {
                    lastSuccessfulCursor = next;
                    syncCursorMapper.updateCursor(corpId, cursorKey, next);
                }
                break;
            }

            log.info("WecomKf sync_msg OK, got {} messages (cursor={}). corpId={}",
                    msgList.size(), cursor, corpId);

            for (int i = 0; i < msgList.size(); i++)
            {
                JSONObject msg = msgList.getJSONObject(i);
                if (processSyncedMessage(wecomCfg, kfCfg, accessToken, msg, enabledAt))
                {
                    totalProcessed++;
                }
            }

            String nextCursor = res.getString("next_cursor");
            if (StringUtils.hasText(nextCursor))
            {
                lastSuccessfulCursor = nextCursor;
                syncCursorMapper.updateCursor(corpId, cursorKey, nextCursor);
            }

            int hm = res.getIntValue("has_more");
            hasMore = (hm == 1);
            if (hasMore)
            {
                if (!StringUtils.hasText(nextCursor))
                {
                    log.warn("WecomKf sync_msg has_more=1 but next_cursor is empty, stopping. corpId={}",
                            corpId);
                    break;
                }
                cursor = nextCursor;
                log.info("WecomKf sync_msg has_more=1, next_cursor={}, continue. corpId={}",
                        cursor, corpId);
            }
        }

        log.info("WecomKf pullAndForward finished, processed={}, lastCursor={}, corpId={}",
                totalProcessed, lastSuccessfulCursor, corpId);
    }

    private boolean processSyncedMessage(CrmWecomConfig wecomCfg, CrmWecomKfConfig kfCfg,
            String accessToken, JSONObject msg, Date enabledAt)
    {
        String msgid = msg.getString("msgid");
        String externalUserid = msg.getString("external_userid");
        String openKfidFromMsg = msg.getString("open_kfid");
        String msgtype = msg.getString("msgtype");
        int origin = msg.getIntValue("origin");
        Long sendTimeSec = msg.getLong("send_time");

        if (!StringUtils.hasText(msgid) || !StringUtils.hasText(externalUserid)
                || !StringUtils.hasText(openKfidFromMsg))
        {
            log.warn("WecomKf: skip message missing msgid/external_userid/open_kfid: {}", msg);
            return false;
        }

        if (!isOpenKfidAllowed(kfCfg, openKfidFromMsg))
        {
            log.info("WecomKf: skip msgid={} open_kfid not allowed", msgid);
            return false;
        }

        // event 类无客户会话主键时跳过
        if ("event".equals(msgtype) && !StringUtils.hasText(externalUserid))
        {
            return false;
        }

        if (sendTimeSec != null && enabledAt != null)
        {
            long enabledEpoch = enabledAt.getTime() / 1000L;
            if (sendTimeSec < enabledEpoch)
            {
                log.info("WecomKf: skip msgid={} send_time={} before enabled_at={}",
                        msgid, sendTimeSec, enabledEpoch);
                return false;
            }
        }

        boolean inbound = (origin == ORIGIN_CUSTOMER);
        int direction = inbound ? 1 : 2;
        String preview = buildPreview(msgtype, msg);
        String content = extractContent(msgtype, msg, preview);
        String mediaId = extractMediaId(msgtype, msg);
        String picUrl = extractPicUrl(msgtype, msg);

        CrmWecomKfMessage record = new CrmWecomKfMessage();
        record.setCorpId(wecomCfg.getCorpId());
        record.setOpenKfid(openKfidFromMsg);
        record.setExternalUserid(externalUserid);
        record.setMsgid(msgid);
        record.setMsgtype(msgtype != null ? msgtype : "text");
        record.setContent(content);
        record.setDirection(direction);
        record.setSendTime(sendTimeSec);
        record.setRawJson(msg.toJSONString());
        record.setMediaId(mediaId);
        record.setPicUrl(picUrl);
        record.setCreatedAt(new Date());

        boolean isNew = wecomKfMessageService.insertMessage(record);
        if (!isNew)
        {
            log.info("WecomKf msgid already processed (db dedup): {}", msgid);
            return false;
        }

        Date lastAt = sendTimeSec != null ? new Date(sendTimeSec * 1000L) : new Date();
        CrmWecomKfConversation conv = new CrmWecomKfConversation();
        conv.setCorpId(wecomCfg.getCorpId());
        conv.setOpenKfid(openKfidFromMsg);
        conv.setExternalUserid(externalUserid);
        conv.setLastMessage(preview);
        conv.setLastMessageAt(lastAt);
        String contactId = null;
        try
        {
            contactId = identityResolutionSupport.resolveContactId(externalUserid, null);
            if (StringUtils.hasText(contactId))
            {
                conv.setContactId(contactId);
            }
        }
        catch (Exception e)
        {
            log.debug("WecomKf: resolve contact failed: {}", e.getMessage());
        }

        if (inbound)
        {
            conversationMapper.upsertOnInbound(conv);
        }
        else
        {
            conversationMapper.upsertOnOutbound(conv);
        }

        if (contactServiceSummaryHooks != null && StringUtils.hasText(contactId))
        {
            contactServiceSummaryHooks.onMessageForContact(
                    TenantDataSourceSupport.currentTenantKey(), contactId);
        }

        CrmWecomKfConversation saved = conversationMapper.selectByOpenKfidExternalUserid(
                openKfidFromMsg, externalUserid);
        if (saved != null)
        {
            record.setConversationId(saved.getId());
        }

        // SSE / Agent / Push 须包在 shouldFanOutToInbox 内；渠道关闭仍落库
        if (shouldFanOutToInbox())
        {
            String tenantId = TenantDataSourceSupport.currentTenantKey();
            inboxEventPublisher.publishMessageCreated(tenantId,
                    WecomKfInboxSsePayloads.messageCreated(record, saved));
            if (saved != null)
            {
                inboxEventPublisher.publishConversationUpdated(tenantId,
                        WecomKfInboxSsePayloads.conversationUpdated(saved));
            }
            scheduleInboxAgentIfNeeded(tenantId, inbound, msgtype, content, record, saved);
        }

        if (inbound && "text".equals(msgtype) && StringUtils.hasText(content))
        {
            if (StringUtils.hasText(kfCfg.getWebhookUrl()))
            {
                JSONObject payload = new JSONObject();
                payload.put("external_userid", externalUserid);
                payload.put("open_kfid", openKfidFromMsg);
                payload.put("msgtype", "text");
                payload.put("content", content);
                payload.put("inbound_msgid", msgid);
                payload.put("send_time", sendTimeSec);
                payload.put("access_token", accessToken);
                postToWebhook(kfCfg.getWebhookUrl(), payload);
            }
            try
            {
                long st = sendTimeSec != null ? sendTimeSec : System.currentTimeMillis() / 1000;
                activityEventProducer.produceWecomMessageReceived(
                        externalUserid, contactId, msgid, msgtype, content, st);
            }
            catch (Exception e)
            {
                log.warn("WecomKf: failed to produce Activity for msgid={}: {}", msgid, e.getMessage());
            }
        }

        return true;
    }

    /**
     * 文本入站落库后，门闩通过则异步通知 Next Inbox Agent。
     * hostedByAi=false / 非文本 / canReply=0 / 无会话 → skip。
     */
    private void scheduleInboxAgentIfNeeded(String tenantId, boolean inbound, String msgtype, String content,
            CrmWecomKfMessage record, CrmWecomKfConversation saved)
    {
        if (!shouldDispatchInboxAgent(inbound, msgtype, content, record, saved))
        {
            return;
        }
        try
        {
            InboxAgentInboundPayload payload = new InboxAgentInboundPayload();
            payload.setChannel(InboxAgentInboundPayload.CHANNEL_WECOM_KF);
            payload.setConversationId(saved.getId());
            payload.setInboundMessageId(record.getId());
            payload.setAccountId(saved.getOpenKfid());
            payload.setPeerId(saved.getExternalUserid());
            payload.setMsgType("text");
            payload.setText(content);
            payload.setKeywordAutoReplied(false);
            inboxAgentInboundDispatchService.scheduleDispatch(tenantId, payload);
        }
        catch (Exception e)
        {
            log.warn("WecomKf: schedule Inbox AI dispatch failed openKfid={} conversationId={}: {}",
                    saved.getOpenKfid(), saved.getId(), e.getMessage());
        }
    }

    /**
     * Inbox Agent 入站门闩（渠道开关由 {@link #shouldFanOutToInbox()} 外层保证）。
     */
    static boolean shouldDispatchInboxAgent(boolean inbound, String msgtype, String content,
            CrmWecomKfMessage record, CrmWecomKfConversation saved)
    {
        if (!inbound || !"text".equals(msgtype) || !StringUtils.hasText(content)
                || record == null || record.getId() == null
                || saved == null || saved.getId() == null)
        {
            return false;
        }
        if (Boolean.FALSE.equals(saved.getHostedByAi()))
        {
            return false;
        }
        if (saved.getCanReply() != null && saved.getCanReply() == 0)
        {
            return false;
        }
        return true;
    }

    /**
     * 渠道关闭时仍落库，但不向 Inbox SSE / Agent / Push 扇出（对齐 WhatsApp / 公众号）。
     * P2 接入实时能力时统一经此判断。
     */
    boolean shouldFanOutToInbox()
    {
        String tenantId = TenantDataSourceSupport.currentTenantKey();
        if (!StringUtils.hasText(tenantId))
        {
            return false;
        }
        return inboxChannelGateService.isEnabled(tenantId, InboxChannel.WECOM_KF);
    }

    private CrmWecomKfSyncCursor ensureCursor(String corpId, String openKfid)
    {
        String key = openKfid != null ? openKfid : "";
        CrmWecomKfSyncCursor existing = syncCursorMapper.selectByCorpOpenKfid(corpId, key);
        if (existing != null)
        {
            return existing;
        }
        CrmWecomKfSyncCursor row = new CrmWecomKfSyncCursor();
        row.setCorpId(corpId);
        row.setOpenKfid(key);
        row.setCursor("");
        row.setEnabledAt(new Date());
        syncCursorMapper.upsertEnsure(row);
        CrmWecomKfSyncCursor created = syncCursorMapper.selectByCorpOpenKfid(corpId, key);
        return created != null ? created : row;
    }

    static boolean isOpenKfidAllowed(CrmWecomKfConfig kfCfg, String openKfid)
    {
        if (kfCfg == null || !StringUtils.hasText(openKfid))
        {
            return false;
        }
        String raw = kfCfg.getOpenKfids();
        if (!StringUtils.hasText(raw) || "[]".equals(raw.trim()))
        {
            return true;
        }
        try
        {
            List<String> list = JSON.parseArray(raw, String.class);
            if (list == null || list.isEmpty())
            {
                return true;
            }
            Set<String> set = new HashSet<>();
            for (String s : list)
            {
                if (StringUtils.hasText(s))
                {
                    set.add(s.trim());
                }
            }
            return set.isEmpty() || set.contains(openKfid.trim());
        }
        catch (Exception e)
        {
            log.warn("WecomKf: parse openKfids failed: {}", e.getMessage());
            return true;
        }
    }

    static String buildPreview(String msgtype, JSONObject msg)
    {
        if (msgtype == null)
        {
            return "[消息]";
        }
        switch (msgtype)
        {
            case "text":
                JSONObject text = msg.getJSONObject("text");
                String c = text != null ? text.getString("content") : null;
                if (!StringUtils.hasText(c))
                {
                    return "";
                }
                return c.length() > 200 ? c.substring(0, 200) : c;
            case "image":
                return "[图片]";
            case "voice":
                return "[语音]";
            case "video":
                return "[视频]";
            case "file":
                return "[文件]";
            case "location":
                return "[位置]";
            case "link":
                return "[链接]";
            case "business_card":
                return "[名片]";
            case "miniprogram":
                return "[小程序]";
            case "msgmenu":
                return "[菜单消息]";
            case "event":
                return "[事件]";
            default:
                return "[" + msgtype + "]";
        }
    }

    static String extractContent(String msgtype, JSONObject msg, String preview)
    {
        if ("text".equals(msgtype))
        {
            JSONObject text = msg.getJSONObject("text");
            if (text != null && StringUtils.hasText(text.getString("content")))
            {
                return text.getString("content");
            }
        }
        return preview;
    }

    /**
     * 从 sync_msg 载荷提取临时素材 media_id（image/voice/video/file）。
     */
    static String extractMediaId(String msgtype, JSONObject msg)
    {
        if (msgtype == null || msg == null)
        {
            return null;
        }
        String key;
        switch (msgtype)
        {
            case "image":
            case "voice":
            case "video":
            case "file":
                key = msgtype;
                break;
            default:
                return null;
        }
        JSONObject obj = msg.getJSONObject(key);
        if (obj == null)
        {
            return null;
        }
        String mediaId = obj.getString("media_id");
        return StringUtils.hasText(mediaId) ? mediaId.trim() : null;
    }

    static String extractPicUrl(String msgtype, JSONObject msg)
    {
        if (!"image".equals(msgtype) || msg == null)
        {
            return null;
        }
        JSONObject image = msg.getJSONObject("image");
        if (image == null)
        {
            return null;
        }
        String picUrl = image.getString("pic_url");
        if (!StringUtils.hasText(picUrl))
        {
            picUrl = image.getString("url");
        }
        return StringUtils.hasText(picUrl) ? picUrl.trim() : null;
    }

    /**
     * 临时素材下载代理：校验 openKfid 归属，透传企微 media/get 二进制。
     */
    public void streamTemporaryMedia(String openKfid, String mediaId, HttpServletResponse response)
            throws IOException
    {
        if (!StringUtils.hasText(openKfid) || !StringUtils.hasText(mediaId))
        {
            throw new ServiceException("缺少 openKfid 或 mediaId", HttpStatus.BAD_REQUEST);
        }

        CrmWecomKfConfig kfCfg = kfConfigMapper.selectFirst();
        if (kfCfg == null || kfCfg.getEnabled() == null || kfCfg.getEnabled() != 1)
        {
            throw new ServiceException("微信客服未启用", HttpStatus.FORBIDDEN);
        }
        if (!isOpenKfidAllowed(kfCfg, openKfid.trim()))
        {
            throw new ServiceException("openKfid 不属于当前租户配置", HttpStatus.FORBIDDEN);
        }

        CrmWecomConfig wecomCfg = null;
        if (StringUtils.hasText(kfCfg.getCorpId()))
        {
            wecomCfg = wecomConfigMapper.selectByCorpId(kfCfg.getCorpId());
        }
        if (wecomCfg == null)
        {
            wecomCfg = wecomConfigMapper.selectFirst();
        }
        if (wecomCfg == null)
        {
            throw new ServiceException("未找到企业微信配置", HttpStatus.BAD_REQUEST);
        }

        WecomMediaFetchResult result = fetchTemporaryMedia(wecomCfg, mediaId.trim());
        if (!result.isSuccess())
        {
            throw mapMediaFetchError(result);
        }

        response.setContentType(result.getContentType());
        response.setHeader("Cache-Control", "private, max-age=3600");
        response.getOutputStream().write(result.getBody());
    }

    /**
     * 上传临时素材供出站 image/file 客服消息使用。
     */
    public Map<String, Object> uploadTemporaryMedia(String openKfid, String type, MultipartFile file)
            throws IOException
    {
        if (!StringUtils.hasText(openKfid) || file == null || file.isEmpty())
        {
            throw new ServiceException("缺少 openKfid 或 file", HttpStatus.BAD_REQUEST);
        }
        String mediaType = StringUtils.hasText(type) ? type.trim().toLowerCase() : "image";
        if (!"image".equals(mediaType) && !"file".equals(mediaType))
        {
            throw new ServiceException("type 仅支持 image/file", HttpStatus.BAD_REQUEST);
        }

        CrmWecomKfConfig kfCfg = kfConfigMapper.selectFirst();
        if (kfCfg == null || kfCfg.getEnabled() == null || kfCfg.getEnabled() != 1)
        {
            throw new ServiceException("微信客服未启用", HttpStatus.FORBIDDEN);
        }
        if (!isOpenKfidAllowed(kfCfg, openKfid.trim()))
        {
            throw new ServiceException("openKfid 不属于当前租户配置", HttpStatus.FORBIDDEN);
        }

        CrmWecomConfig wecomCfg = null;
        if (StringUtils.hasText(kfCfg.getCorpId()))
        {
            wecomCfg = wecomConfigMapper.selectByCorpId(kfCfg.getCorpId());
        }
        if (wecomCfg == null)
        {
            wecomCfg = wecomConfigMapper.selectFirst();
        }
        if (wecomCfg == null)
        {
            throw new ServiceException("未找到企业微信配置", HttpStatus.BAD_REQUEST);
        }

        String contentType = file.getContentType() != null ? file.getContentType().toLowerCase() : "";
        String original = file.getOriginalFilename() != null ? file.getOriginalFilename().toLowerCase() : "";
        if ("image".equals(mediaType))
        {
            boolean okMime = contentType.contains("jpeg") || contentType.contains("jpg")
                    || contentType.contains("png") || contentType.contains("gif")
                    || original.endsWith(".jpg") || original.endsWith(".jpeg")
                    || original.endsWith(".png") || original.endsWith(".gif");
            if (!okMime)
            {
                throw new ServiceException("图片仅支持 jpeg/png/gif", HttpStatus.BAD_REQUEST);
            }
            if (file.getSize() > 10L * 1024L * 1024L)
            {
                throw new ServiceException("图片大小不能超过 10MB", HttpStatus.BAD_REQUEST);
            }
        }
        else if (file.getSize() > 20L * 1024L * 1024L)
        {
            throw new ServiceException("文件大小不能超过 20MB", HttpStatus.BAD_REQUEST);
        }

        String suffix = original.contains(".") ? original.substring(original.lastIndexOf('.')) : ".bin";
        File temp = Files.createTempFile("wecom-kf-media-", suffix).toFile();
        try
        {
            file.transferTo(temp);
            WecomMediaUploadResult result = uploadMediaWithTokenRetry(wecomCfg, mediaType, temp);
            if (result == null || !StringUtils.hasText(result.getMediaId()) || result.getErrcode() != 0)
            {
                String errmsg = result != null ? result.getErrmsg() : "empty response";
                int code = result != null ? result.getErrcode() : -1;
                throw new ServiceException("企微临时素材上传失败: errcode=" + code + " " + errmsg, 502);
            }
            String mediaId = result.getMediaId();
            Map<String, Object> data = new HashMap<>();
            data.put("mediaId", mediaId);
            data.put("msgType", mediaType);
            data.put("url", "/crm/wecom/kf/messages/media?openKfid=" + openKfid.trim() + "&mediaId=" + mediaId);
            return data;
        }
        finally
        {
            if (temp.exists() && !temp.delete())
            {
                temp.deleteOnExit();
            }
        }
    }

    private WecomMediaUploadResult uploadMediaWithTokenRetry(CrmWecomConfig wecomCfg, String mediaType, File file)
    {
        String token = accessTokenService.getAgentAccessToken(wecomCfg);
        WecomMediaUploadResult result = apiClient.uploadMedia(token, mediaType, file);
        if (result != null && (WecomTencentApiClient.isAccessTokenInvalid(result.getErrcode())
                || result.getErrcode() == 40001))
        {
            accessTokenService.invalidate(wecomCfg.getCorpId());
            token = accessTokenService.getAgentAccessToken(wecomCfg);
            result = apiClient.uploadMedia(token, mediaType, file);
        }
        return result;
    }

    private WecomMediaFetchResult fetchTemporaryMedia(CrmWecomConfig wecomCfg, String mediaId)
    {
        String token = accessTokenService.getAgentAccessToken(wecomCfg);
        WecomMediaFetchResult result = apiClient.getTemporaryMedia(token, mediaId);
        if (!result.isSuccess() && result.isTokenInvalid())
        {
            accessTokenService.invalidate(wecomCfg.getCorpId());
            token = accessTokenService.getAgentAccessToken(wecomCfg);
            result = apiClient.getTemporaryMedia(token, mediaId);
        }
        return result;
    }

    private static ServiceException mapMediaFetchError(WecomMediaFetchResult result)
    {
        String msg = "微信客服临时素材获取失败: errcode=" + result.getErrcode()
                + " errmsg=" + result.getErrmsg();
        if (result.isInvalidMediaId())
        {
            return new ServiceException(msg, HttpStatus.NOT_FOUND);
        }
        return new ServiceException(msg, 502);
    }

    private void postToWebhook(String webhookUrl, JSONObject payload)
    {
        try
        {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> entity = new HttpEntity<>(payload.toJSONString(), headers);
            ResponseEntity<String> resp = restTemplate.postForEntity(webhookUrl, entity, String.class);

            if (resp.getStatusCode().is2xxSuccessful())
            {
                log.info("WecomKf webhook forwarded OK: {}", webhookUrl);
            }
            else
            {
                log.warn("WecomKf webhook forwarded non-2xx: {} status={}", webhookUrl, resp.getStatusCodeValue());
            }
        }
        catch (Exception e)
        {
            log.warn("WecomKf webhook forward failed url={}: {}", webhookUrl, e.getMessage());
        }
    }

    static String getCdataOrText(String xml, String tag)
    {
        Pattern p = Pattern.compile(
                "<" + tag + "><!\\[CDATA\\[(.*?)\\]\\]></" + tag + ">|<" + tag + ">([^<]*)</" + tag + ">",
                Pattern.DOTALL);
        Matcher m = p.matcher(xml);
        if (m.find())
        {
            String v = m.group(1) != null ? m.group(1) : m.group(2);
            return v != null ? v.trim() : null;
        }
        return null;
    }
}
