package com.ruoyi.crm.wechatmp.service.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletResponse;

import org.springframework.web.multipart.MultipartFile;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.domain.dto.ActivityEventDTO;
import com.ruoyi.crm.domain.enums.InboxChannel;
import com.ruoyi.crm.event.ActivityEventProducer;
import com.ruoyi.crm.service.InboxChannelGateService;
import com.ruoyi.crm.service.ICrmCustomerIdentityService;
import com.ruoyi.crm.service.IInboxAgentChannelService;
import com.ruoyi.crm.service.support.TenantDataSourceSupport;
import com.ruoyi.crm.mobile.push.InboxMobilePushHook;
import com.ruoyi.crm.wechatmp.WechatMpMediaFetchResult;
import com.ruoyi.crm.wechatmp.WechatMpMessageNewsBuilder;
import com.ruoyi.crm.wechatmp.WechatMpTencentApiClient;
import com.ruoyi.crm.wechatmp.audience.FanAudienceRuleValidator;
import com.ruoyi.crm.wechatmp.domain.CrmWechatMpConfig;
import com.ruoyi.crm.wechatmp.domain.CrmWechatMpFan;
import com.ruoyi.crm.wechatmp.domain.CrmWechatMpMessage;
import com.ruoyi.crm.wechatmp.domain.CrmWechatMpMessagePreset;
import com.ruoyi.crm.wechatmp.dto.WechatMpMessagePresetRenderRequest;
import com.ruoyi.crm.wechatmp.dto.WechatMpMessagePresetRenderResult;
import com.ruoyi.crm.wechatmp.dto.WechatMpMessageReplyRequest;
import com.ruoyi.crm.wechatmp.dto.WechatMpMessageTemplateRequest;
import com.ruoyi.crm.wechatmp.mapper.CrmWechatMpConfigMapper;
import com.ruoyi.crm.wechatmp.mapper.CrmWechatMpFanMapper;
import com.ruoyi.crm.wechatmp.mapper.CrmWechatMpMessageMapper;
import com.ruoyi.crm.service.support.SurveyOutreachLogService;
import com.ruoyi.crm.wechatmp.domain.CrmWechatMpConversation;
import com.ruoyi.crm.wechatmp.service.EventMpAudienceOutreachService;
import com.ruoyi.crm.wechatmp.service.EventWechatMpAppIdResolver;
import com.ruoyi.crm.wechatmp.realtime.WechatMpInboxEventPublisher;
import com.ruoyi.crm.wechatmp.realtime.WechatMpInboxSsePayloads;
import com.ruoyi.crm.wechatmp.service.IWechatMpConversationService;
import com.ruoyi.crm.wechatmp.service.IWechatMpMessagePresetService;
import com.ruoyi.crm.wechatmp.service.IWechatMpMessageService;
import com.ruoyi.crm.wechatmp.service.WechatMpTokenService;
import com.ruoyi.framework.web.service.PermissionService;

import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;

@Service
public class WechatMpMessageServiceImpl implements IWechatMpMessageService
{
    @Autowired
    private CrmWechatMpMessageMapper messageMapper;

    @Autowired
    private CrmWechatMpConfigMapper configMapper;

    @Autowired
    private WechatMpTokenService tokenService;

    @Autowired
    private WechatMpTencentApiClient tencentApiClient;

    @Autowired
    private ActivityEventProducer activityEventProducer;

    @Autowired
    private ICrmCustomerIdentityService identityService;

    @Autowired
    private EventMpAudienceOutreachService audienceOutreachService;

    @Autowired
    private SurveyOutreachLogService surveyOutreachLogService;

    @Autowired
    private FanAudienceRuleValidator fanAudienceRuleValidator;

    @Autowired
    private CrmWechatMpFanMapper fanMapper;

    @Autowired
    private EventWechatMpAppIdResolver eventWechatMpAppIdResolver;

    @Autowired
    private IWechatMpMessagePresetService presetService;

    @Autowired
    private IWechatMpConversationService conversationService;

    @Autowired
    private WechatMpInboxEventPublisher inboxEventPublisher;

    @Autowired
    private PermissionService permissionService;

    @Autowired(required = false)
    private InboxMobilePushHook inboxMobilePushHook;

    @Autowired
    private InboxChannelGateService inboxChannelGateService;

    @Override
    public List<Map<String, Object>> listConversations(String appId)
    {
        return messageMapper.selectConversations(appId);
    }

    @Override
    public List<CrmWechatMpMessage> listMessages(String appId, String openId)
    {
        return messageMapper.selectByConversation(appId, openId);
    }

    @Override
    public void reply(WechatMpMessageReplyRequest req)
    {
        replyInternal(req, false);
    }

    @Override
    public void replyAsAi(WechatMpMessageReplyRequest req)
    {
        replyInternal(req, true);
    }

    private void replyInternal(WechatMpMessageReplyRequest req, boolean asAiAgent)
    {
        if (!asAiAgent)
        {
            assertOwnerGateIfInbox(req.getAppId(), req.getOpenId());
        }
        assertCustomMessageWindow(req.getAppId(), req.getOpenId());
        assertServiceAccount(req.getAppId());
        String token = tokenService.getAccessToken(req.getAppId());
        String msgType = req.getMsgType() != null ? req.getMsgType() : "text";
        JSONObject body;
        if ("news".equals(msgType))
        {
            JSONArray articles = WechatMpMessageNewsBuilder.parseArticles(req.getContent());
            WechatMpMessageNewsBuilder.validateArticlesFromJsonArray(articles);
            body = WechatMpMessageNewsBuilder.buildCustomMessageBody(req.getOpenId(), articles);
        }
        else if ("image".equals(msgType))
        {
            String mediaId = resolveMediaIdFromContent(req.getContent());
            if (StringUtils.isBlank(mediaId))
            {
                throw new ServiceException("image 消息需要 mediaId", HttpStatus.BAD_REQUEST);
            }
            body = new JSONObject();
            body.put("touser", req.getOpenId());
            body.put("msgtype", "image");
            JSONObject image = new JSONObject();
            image.put("media_id", mediaId);
            body.put("image", image);
        }
        else
        {
            body = new JSONObject();
            body.put("touser", req.getOpenId());
            body.put("msgtype", msgType);
            JSONObject text = new JSONObject();
            text.put("content", req.getContent());
            body.put("text", text);
        }
        JSONObject res = tencentApiClient.sendCustomMessage(token, body);
        if (res != null && res.containsKey("errcode") && res.getIntValue("errcode") != 0)
        {
            String errmsg = res.getString("errmsg");
            publishReplyFailed(req.getAppId(), req.getOpenId(), errmsg);
            throw new ServiceException("客服消息发送失败: " + errmsg);
        }
        String mediaId = "image".equals(msgType) ? resolveMediaIdFromContent(req.getContent()) : null;
        String persistContent = "image".equals(msgType) ? null : req.getContent();
        String source = asAiAgent ? IInboxAgentChannelService.SOURCE_AI_INBOX_AGENT : null;
        saveOutbound(req.getAppId(), req.getOpenId(), msgType, persistContent, null, null, null, mediaId, source);
    }

    @Override
    public Long sendTemplate(WechatMpMessageTemplateRequest req)
    {
        String preferredAppId = resolveTemplateAppId(req);
        AppIdAlignment alignment = alignAppIdWithOpenId(preferredAppId, req.getOpenId());
        applyAlignmentToRequest(req, alignment);
        String appId = req.getAppId();
        assertOwnerGateIfInbox(appId, req.getOpenId());

        String token = tokenService.getAccessToken(appId);
        JSONObject body = new JSONObject();
        body.put("touser", req.getOpenId());
        body.put("template_id", req.getTemplateId());
        if (req.getUrl() != null)
        {
            body.put("url", req.getUrl());
        }
        if (req.getData() != null)
        {
            body.put("data", req.getData());
        }
        JSONObject res = tencentApiClient.sendTemplateMessage(token, body);
        if (res != null && res.containsKey("errcode") && res.getIntValue("errcode") != 0)
        {
            String errmsg = res.getString("errmsg");
            if (errmsg != null && errmsg.contains("invalid openid"))
            {
                throw new ServiceException("wechat_mp_openid_invalid: openId 与公众号不匹配或未关注", HttpStatus.BAD_REQUEST);
            }
            if (errmsg != null && errmsg.contains("invalid template_id"))
            {
                throw new ServiceException("wechat_mp_template_app_mismatch: 模板不属于当前公众号", HttpStatus.BAD_REQUEST);
            }
            throw new ServiceException("模板消息发送失败: " + errmsg);
        }
        Long messageId = saveOutbound(appId, req.getOpenId(), "template", null, req.getTemplateId(), null,
                req.getSurveyId());
        if (StringUtils.isNotBlank(req.getEventId()))
        {
            audienceOutreachService.recordSent(
                    TenantDataSourceSupport.currentTenantKey(),
                    req.getEventId(),
                    appId,
                    req.getOpenId(),
                    req.getPresetId(),
                    messageId);
        }
        if (StringUtils.isNotBlank(req.getSurveyId()))
        {
            String channel = StringUtils.isNotBlank(req.getOutreachChannel())
                    ? req.getOutreachChannel()
                    : SurveyOutreachLogService.CHANNEL_MP_TEMPLATE;
            surveyOutreachLogService.recordSent(
                    req.getSurveyId(), channel, "openid", req.getOpenId(),
                    req.getPresetId() != null ? String.valueOf(req.getPresetId()) : null);
        }
        return messageId;
    }

    /**
     * 行级 wechatMpAppId &gt; 活动默认 &gt; 请求 appId。
     */
    private String resolveTemplateAppId(WechatMpMessageTemplateRequest req)
    {
        if (StringUtils.isNotBlank(req.getWechatMpAppId()))
        {
            return req.getWechatMpAppId().trim();
        }
        if (StringUtils.isNotBlank(req.getEventId()))
        {
            String resolved = eventWechatMpAppIdResolver.resolve(req.getEventId());
            if (StringUtils.isNotBlank(resolved))
            {
                return resolved;
            }
        }
        if (StringUtils.isNotBlank(req.getAppId()))
        {
            return req.getAppId().trim();
        }
        throw new ServiceException("appId 不能为空", HttpStatus.BAD_REQUEST);
    }

    /**
     * 当 openId 在粉丝库中仅归属一个已关注公众号时，解析实际 appId。
     */
    private AppIdAlignment alignAppIdWithOpenId(String preferredAppId, String openId)
    {
        if (StringUtils.isBlank(openId) || StringUtils.isBlank(preferredAppId))
        {
            return new AppIdAlignment(preferredAppId, preferredAppId);
        }
        CrmWechatMpFan fan = fanMapper.selectByOpenId(preferredAppId, openId);
        if (fan != null && Boolean.TRUE.equals(fan.getSubscribe()))
        {
            return new AppIdAlignment(preferredAppId, preferredAppId);
        }
        List<CrmWechatMpFan> subscribed = fanMapper.selectSubscribedByOpenId(openId);
        if (subscribed == null || subscribed.isEmpty())
        {
            return new AppIdAlignment(preferredAppId, preferredAppId);
        }
        if (subscribed.size() == 1)
        {
            return new AppIdAlignment(preferredAppId, subscribed.get(0).getAppId());
        }
        throw new ServiceException("wechat_mp_openid_ambiguous", HttpStatus.BAD_REQUEST);
    }

    private void applyAlignmentToRequest(WechatMpMessageTemplateRequest req, AppIdAlignment alignment)
    {
        if (!alignment.isCorrected())
        {
            req.setAppId(alignment.getEffectiveAppId());
            validatePresetMatchesAppId(req, alignment.getEffectiveAppId());
            return;
        }

        if (req.getPresetId() != null)
        {
            CrmWechatMpMessagePreset preset = presetService.getById(req.getPresetId());
            if (!alignment.getEffectiveAppId().equals(preset.getAppId()))
            {
                throw new ServiceException("wechat_mp_template_app_mismatch", HttpStatus.BAD_REQUEST);
            }
            WechatMpMessagePresetRenderRequest renderRequest = new WechatMpMessagePresetRenderRequest();
            renderRequest.setEventId(req.getEventId());
            renderRequest.setOpenId(req.getOpenId());
            renderRequest.setAppId(alignment.getEffectiveAppId());
            renderRequest.setTenantId(TenantDataSourceSupport.currentTenantKey());
            WechatMpMessagePresetRenderResult rendered = presetService.render(req.getPresetId(), renderRequest);
            req.setAppId(rendered.getAppId());
            req.setTemplateId(rendered.getTemplateId());
            req.setUrl(rendered.getUrl());
            req.setData(rendered.getData());
            return;
        }

        throw new ServiceException("wechat_mp_app_id_mismatch", HttpStatus.BAD_REQUEST);
    }

    private void validatePresetMatchesAppId(WechatMpMessageTemplateRequest req, String appId)
    {
        if (req.getPresetId() == null || StringUtils.isBlank(appId))
        {
            return;
        }
        CrmWechatMpMessagePreset preset = presetService.getById(req.getPresetId());
        if (!appId.equals(preset.getAppId()))
        {
            throw new ServiceException("wechat_mp_template_app_mismatch", HttpStatus.BAD_REQUEST);
        }
    }

    private static final class AppIdAlignment
    {
        private final String preferredAppId;
        private final String effectiveAppId;

        private AppIdAlignment(String preferredAppId, String effectiveAppId)
        {
            this.preferredAppId = preferredAppId;
            this.effectiveAppId = effectiveAppId;
        }

        private String getEffectiveAppId()
        {
            return effectiveAppId;
        }

        private boolean isCorrected()
        {
            return !preferredAppId.equals(effectiveAppId);
        }
    }

    @Override
    public void streamTemporaryMedia(String appId, String mediaId, HttpServletResponse response) throws IOException
    {
        if (StringUtils.isBlank(appId) || StringUtils.isBlank(mediaId))
        {
            throw new ServiceException("缺少 appId 或 mediaId", HttpStatus.BAD_REQUEST);
        }
        fanAudienceRuleValidator.validateAppId(appId.trim());

        WechatMpMediaFetchResult result = fetchTemporaryMedia(appId.trim(), mediaId.trim());
        if (!result.isSuccess())
        {
            throw mapMediaFetchError(result);
        }

        response.setContentType(result.getContentType());
        response.setHeader("Cache-Control", "private, max-age=3600");
        response.getOutputStream().write(result.getBody());
    }

    private WechatMpMediaFetchResult fetchTemporaryMedia(String appId, String mediaId)
    {
        String token = tokenService.getAccessToken(appId);
        WechatMpMediaFetchResult result = tencentApiClient.getTemporaryMedia(token, mediaId);
        if (!result.isSuccess() && result.isTokenInvalid())
        {
            token = tokenService.refreshAccessToken(appId);
            result = tencentApiClient.getTemporaryMedia(token, mediaId);
        }
        return result;
    }

    private ServiceException mapMediaFetchError(WechatMpMediaFetchResult result)
    {
        String msg = "微信临时素材获取失败: errcode=" + result.getErrcode()
                + " errmsg=" + result.getErrmsg();
        if (result.isInvalidMediaId())
        {
            return new ServiceException(msg, HttpStatus.NOT_FOUND);
        }
        return new ServiceException(msg, 502);
    }

    @Override
    public void saveInbound(String appId, WxMpXmlMessage wxMessage)
    {
        saveInboundReturning(appId, wxMessage);
    }

    @Override
    public CrmWechatMpMessage saveInboundReturning(String appId, WxMpXmlMessage wxMessage)
    {
        if (wxMessage == null || !"text".equals(wxMessage.getMsgType()) && !"image".equals(wxMessage.getMsgType()))
        {
            return null;
        }
        CrmWechatMpMessage msg = new CrmWechatMpMessage();
        msg.setAppId(appId);
        msg.setOpenId(wxMessage.getFromUser());
        msg.setDirection("inbound");
        msg.setMsgType(wxMessage.getMsgType());
        msg.setContent(wxMessage.getContent());
        msg.setMediaId(wxMessage.getMediaId());
        msg.setPicUrl(wxMessage.getPicUrl());
        msg.setWxMsgId(wxMessage.getMsgId() != null ? "mp_" + wxMessage.getMsgId() : null);
        msg.setCreateTime(wxMessage.getCreateTime() != null
                ? new Date(wxMessage.getCreateTime() * 1000L) : DateUtils.getNowDate());
        messageMapper.insert(msg);
        conversationService.upsertOnInbound(appId, msg.getOpenId(), msg.getMsgType(), msg.getContent(),
                msg.getCreateTime());
        publishMessageEvents(msg, true);
        if (inboxMobilePushHook != null)
        {
            String tenantId = TenantDataSourceSupport.currentTenantKey();
            CrmWechatMpConversation conv = conversationService.findByAppOpenId(appId, msg.getOpenId());
            inboxMobilePushHook.scheduleWechatMpInbound(tenantId, msg, conv);
        }
        return msg;
    }

    private Long saveOutbound(String appId, String openId, String msgType, String content,
            String templateId, String wxMsgId, String surveyId)
    {
        return saveOutbound(appId, openId, msgType, content, templateId, wxMsgId, surveyId, null, null);
    }

    private Long saveOutbound(String appId, String openId, String msgType, String content,
            String templateId, String wxMsgId, String surveyId, String mediaId)
    {
        return saveOutbound(appId, openId, msgType, content, templateId, wxMsgId, surveyId, mediaId, null);
    }

    private Long saveOutbound(String appId, String openId, String msgType, String content,
            String templateId, String wxMsgId, String surveyId, String mediaId, String source)
    {
        CrmWechatMpMessage msg = new CrmWechatMpMessage();
        msg.setAppId(appId);
        msg.setOpenId(openId);
        msg.setDirection("outbound");
        msg.setMsgType(msgType);
        msg.setContent(content);
        msg.setMediaId(mediaId);
        msg.setTemplateId(templateId);
        msg.setWxMsgId(wxMsgId);
        msg.setSource(source);
        msg.setCreateTime(DateUtils.getNowDate());
        messageMapper.insert(msg);
        conversationService.upsertOnOutbound(appId, openId, msgType, content, msg.getCreateTime());

        ActivityEventDTO dto = new ActivityEventDTO();
        dto.setChannel("wechat_mp");
        dto.setSource("wechat_mp_api");
        dto.setEventType("channel.wechat_mp.message.sent");
        dto.setEventCategory("engagement");
        dto.setImportance(30);
        dto.setChannelEventId("mp_out_" + System.currentTimeMillis() + "_" + openId);
        dto.setMessageData("发送消息");
        JSONObject payload = new JSONObject();
        payload.put("open_id", openId);
        payload.put("msg_type", msgType);
        if (StringUtils.isNotBlank(surveyId))
        {
            payload.put("surveyId", surveyId);
        }
        dto.setPayload(payload.toString());
        JSONObject ctx = new JSONObject();
        ctx.put("app_id", appId);
        dto.setContext(ctx.toString());
        String contactId = identityService.findContactByIdentity("wechat_mp_openid", openId);
        if (contactId != null)
        {
            dto.setSubjectType("Contact");
            dto.setSubjectId(contactId);
        }
        activityEventProducer.produce(dto);
        publishMessageEvents(msg, true);
        return msg.getId();
    }

    private void publishMessageEvents(CrmWechatMpMessage msg, boolean sentStatus)
    {
        if (msg == null)
        {
            return;
        }
        String tenantId = TenantDataSourceSupport.currentTenantKey();
        if (!StringUtils.isNotBlank(tenantId))
        {
            return;
        }
        if (!inboxChannelGateService.isEnabled(tenantId, InboxChannel.WECHAT_MP))
        {
            return;
        }
        CrmWechatMpConversation conv = conversationService.findByAppOpenId(msg.getAppId(), msg.getOpenId());
        Map<String, Object> payload = WechatMpInboxSsePayloads.messageCreated(msg, conv);
        inboxEventPublisher.publishMessageCreated(tenantId, payload);
        if (conv != null)
        {
            inboxEventPublisher.publishConversationUpdated(tenantId,
                    WechatMpInboxSsePayloads.conversationUpdated(conv));
        }
        if (sentStatus && "outbound".equals(msg.getDirection()))
        {
            Long cid = conv != null ? conv.getId() : null;
            inboxEventPublisher.publishMessageStatus(tenantId,
                    WechatMpInboxSsePayloads.messageStatus(msg.getAppId(), cid,
                            msg.getId() != null ? String.valueOf(msg.getId()) : null,
                            "sent", null, null));
        }
    }

    private void publishReplyFailed(String appId, String openId, String errmsg)
    {
        String tenantId = TenantDataSourceSupport.currentTenantKey();
        if (!StringUtils.isNotBlank(tenantId))
        {
            return;
        }
        if (!inboxChannelGateService.isEnabled(tenantId, InboxChannel.WECHAT_MP))
        {
            return;
        }
        CrmWechatMpConversation conv = conversationService.findByAppOpenId(appId, openId);
        Long cid = conv != null ? conv.getId() : null;
        inboxEventPublisher.publishMessageStatus(tenantId,
                WechatMpInboxSsePayloads.messageStatus(appId, cid, null, "failed", null, errmsg));
    }

    private void assertServiceAccount(String appId)
    {
        CrmWechatMpConfig cfg = configMapper.selectByAppId(appId);
        if (cfg != null && "subscription".equals(cfg.getAccountType()))
        {
            throw new ServiceException("MP_CAPABILITY_NOT_SUPPORTED: 订阅号不支持客服消息");
        }
    }

    private void assertCustomMessageWindow(String appId, String openId)
    {
        CrmWechatMpMessage last = messageMapper.selectLastInbound(appId, openId);
        if (last == null || last.getCreateTime() == null)
        {
            throw new ServiceException("MP_CUSTOM_MSG_WINDOW_EXPIRED: 无用户主动消息记录");
        }
        long hours = TimeUnit.MILLISECONDS.toHours(System.currentTimeMillis() - last.getCreateTime().getTime());
        if (hours >= 48)
        {
            throw new ServiceException("MP_CUSTOM_MSG_WINDOW_EXPIRED: 已超过 48 小时客服消息窗口");
        }
    }

    /**
     * Upload temporary media for KF custom messages.
     */
    @Override
    public Map<String, Object> uploadTemporaryMedia(String appId, String type, MultipartFile file) throws IOException
    {
        if (StringUtils.isBlank(appId) || file == null || file.isEmpty())
        {
            throw new ServiceException("缺少 appId 或 file", HttpStatus.BAD_REQUEST);
        }
        String mediaType = StringUtils.isNotBlank(type) ? type.trim().toLowerCase() : "image";
        if ("file".equals(mediaType))
        {
            throw new ServiceException("MP_CAPABILITY_NOT_SUPPORTED: 客服消息不支持通用文件类型", HttpStatus.BAD_REQUEST);
        }
        if (!"image".equals(mediaType))
        {
            throw new ServiceException("暂仅支持 type=image", HttpStatus.BAD_REQUEST);
        }
        fanAudienceRuleValidator.validateAppId(appId.trim());

        String contentType = file.getContentType() != null ? file.getContentType().toLowerCase() : "";
        String original = file.getOriginalFilename() != null ? file.getOriginalFilename().toLowerCase() : "";
        boolean okMime = contentType.contains("jpeg") || contentType.contains("jpg")
                || contentType.contains("png") || contentType.contains("gif")
                || original.endsWith(".jpg") || original.endsWith(".jpeg")
                || original.endsWith(".png") || original.endsWith(".gif");
        if (!okMime)
        {
            throw new ServiceException("图片仅支持 jpeg/png/gif", HttpStatus.BAD_REQUEST);
        }
        if (file.getSize() > 1024L * 1024L)
        {
            throw new ServiceException("图片大小不能超过 1MB", HttpStatus.BAD_REQUEST);
        }

        File temp = Files.createTempFile("mp-media-", original.contains(".") ? original.substring(original.lastIndexOf('.')) : ".bin").toFile();
        try
        {
            file.transferTo(temp);
            String token = tokenService.getAccessToken(appId.trim());
            JSONObject res = tencentApiClient.uploadTemporaryMedia(token, "image", temp);
            if (res != null && tencentApiClient.isTokenInvalid(res))
            {
                token = tokenService.refreshAccessToken(appId.trim());
                res = tencentApiClient.uploadTemporaryMedia(token, "image", temp);
            }
            if (res == null || StringUtils.isBlank(res.getString("media_id")))
            {
                String errmsg = res != null ? res.getString("errmsg") : "empty response";
                throw new ServiceException("微信临时素材上传失败: " + errmsg, 502);
            }
            String mediaId = res.getString("media_id");
            Map<String, Object> data = new HashMap<>();
            data.put("mediaId", mediaId);
            data.put("msgType", "image");
            data.put("url", "/crm/wechat_mp/messages/media?appId=" + appId.trim() + "&mediaId=" + mediaId);
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

    static String resolveMediaIdFromContent(String content)
    {
        if (StringUtils.isBlank(content))
        {
            return null;
        }
        String trimmed = content.trim();
        if (trimmed.startsWith("{"))
        {
            try
            {
                JSONObject obj = JSONObject.parseObject(trimmed);
                if (obj != null)
                {
                    String mid = obj.getString("media_id");
                    if (StringUtils.isBlank(mid))
                    {
                        mid = obj.getString("mediaId");
                    }
                    return mid;
                }
            }
            catch (Exception ignored)
            {
            }
        }
        return trimmed;
    }

    /**
     * 坐席收件箱：仅会话负责人可人工 reply/template。
     * 仅持有旧营销权限 {@code crm:wechat_mp:message}、无 conversations 时放行（营销页兼容）。
     */
    private void assertOwnerGateIfInbox(String appId, String openId)
    {
        if (!permissionService.hasPermi("crm:wechat_mp:conversations"))
        {
            return;
        }
        CrmWechatMpConversation conv = conversationService.findByAppOpenId(appId, openId);
        conversationService.assertOwnedByCurrentUser(conv);
    }
}
