package com.ruoyi.crm.wechatmp.service;

import java.time.LocalDateTime;
import java.util.Date;

import javax.xml.parsers.DocumentBuilderFactory;

import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.crm.domain.dto.ActivityEventDTO;
import com.ruoyi.crm.event.ActivityEventProducer;
import com.ruoyi.crm.service.ICrmCustomerIdentityService;
import com.ruoyi.crm.service.support.TenantDataSourceSupport;
import com.ruoyi.crm.service.nurture.support.EventNurtureHookSupport;
import com.ruoyi.crm.wechatmp.automation.EventMpOutreachContext;
import com.ruoyi.crm.wechatmp.automation.EventMpRegistrationAutomationWorker;
import com.ruoyi.crm.wechatmp.domain.ActivityUnmatched;
import com.ruoyi.crm.wechatmp.domain.CrmWechatMpMessage;
import com.ruoyi.crm.wechatmp.domain.CrmWechatMpPublishTask;
import com.ruoyi.crm.wechatmp.dto.WechatMpIdentityEnrichContext;
import com.ruoyi.crm.wechatmp.mapper.ActivityUnmatchedMapper;
import com.ruoyi.crm.wechatmp.mapper.CrmWechatMpMenuKeyLabelMapper;
import com.ruoyi.crm.wechatmp.mapper.CrmWechatMpPublishTaskMapper;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * 微信公众号事件生产者
 * 将 WxMpXmlMessage 标准化为 ActivityEventDTO，完成 Identity Resolution，
 * 通过现有 ActivityEventProducer 接入 Activity Hub。
 *
 * @author luohd
 * @date 2026-06-18
 */
@Slf4j
@Service
public class WechatMpEventProducer
{
    @Autowired
    private ActivityEventProducer activityEventProducer;

    @Autowired
    private ICrmCustomerIdentityService identityService;

    @Autowired
    private ActivityUnmatchedMapper unmatchedMapper;

    @Autowired
    private WechatMpCallbackSideEffects callbackSideEffects;

    @Autowired
    private IWechatMpMessageService messageService;

    @Autowired
    private CrmWechatMpMenuKeyLabelMapper menuKeyLabelMapper;

    @Autowired
    private CrmWechatMpPublishTaskMapper publishTaskMapper;

    @Autowired
    private IWechatMpMassService massService;

    @Autowired
    private EventMpRegistrationAutomationWorker registrationAutomationWorker;

    @Autowired
    private EventNurtureHookSupport nurtureHookSupport;

    @Autowired
    private WechatMpIdentityEnrichWorker identityEnrichWorker;

    /**
     * 从微信公众号回调解析的消息生产 Activity 事件
     *
     * @param wxMessage 已解析的微信消息
     * @param rawXml    原始 XML（用于保存 unmatched 的 raw_payload）
     * @return 若写入了收件箱入站消息则返回该消息，否则 null
     */
    public CrmWechatMpMessage produceFromWxMessage(WxMpXmlMessage wxMessage, String rawXml, String appId)
    {
        if (wxMessage == null)
        {
            return null;
        }

        // 尽早写入收件箱，避免后续 Activity 处理异常导致 inbound 丢失
        CrmWechatMpMessage inbound = null;
        if (appId != null)
        {
            inbound = messageService.saveInboundReturning(appId, wxMessage);
        }

        try
        {
            String eventType = resolveEventType(wxMessage);
            if (eventType == null)
            {
                log.debug("Unmapped WeChat MP message: msgType={}, event={}",
                        wxMessage.getMsgType(), wxMessage.getEvent());
                return inbound;
            }

            // PUBLISHJOBFINISH 回调：更新发布任务状态
            if ("PUBLISHJOBFINISH".equals(wxMessage.getEvent()))
            {
                handlePublishJobFinish(rawXml, appId);
            }

            // MASSSENDJOBFINISH 回调：回写官方群发任务统计
            if ("MASSSENDJOBFINISH".equals(wxMessage.getEvent()))
            {
                handleMassSendJobFinish(rawXml, appId, wxMessage);
            }

            String openId = wxMessage.getFromUser();
            String contactId = identityService.findContactByIdentity("wechat_mp_openid", openId);

            ActivityEventDTO dto = new ActivityEventDTO();
            dto.setChannel("wechat_mp");
            dto.setSource("wechat_mp_callback");
            dto.setChannelEventId(wxMessage.getMsgId() != null
                    ? "mp_" + wxMessage.getMsgId()
                    : "mp_" + wxMessage.getCreateTime() + "_" + openId);
            dto.setEventType(eventType);
            dto.setEventCategory(resolveEventCategory(eventType));
            dto.setImportance(resolveImportance(eventType));
            dto.setMessageData(resolveMessageData(eventType, wxMessage));
            dto.setPayload(buildPayload(wxMessage));
            dto.setContext(buildContext(wxMessage, appId));
            dto.setStartedDate(wxMessage.getCreateTime() != null
                    ? new Date(wxMessage.getCreateTime() * 1000L)
                    : new Date());

            enrichMenuName(appId, wxMessage, dto);
            callbackSideEffects.enrichQrcodeAttribution(appId, wxMessage, dto);
            maybeTriggerRegistrationAutomation(wxMessage, appId, dto, openId);

            boolean syncWroteUnmatched = false;
            Long unmatchedId = null;

            if (contactId != null)
            {
                dto.setSubjectType("Contact");
                dto.setSubjectId(contactId);
                activityEventProducer.produce(dto);
            }
            else
            {
                activityEventProducer.produce(dto);
                unmatchedId = saveUnmatched(openId, dto.getChannelEventId(), rawXml, appId, eventType);
                syncWroteUnmatched = unmatchedId != null;
            }

            if (WechatMpIdentityEnrichWorker.shouldEnrich(eventType, wxMessage))
            {
                identityEnrichWorker.scheduleImmediately(
                        buildEnrichContext(wxMessage, appId, eventType, rawXml, contactId,
                                syncWroteUnmatched, unmatchedId, dto.getChannelEventId()));
            }
        }
        catch (Exception e)
        {
            log.error("Failed to produce activity event from WeChat MP message: openId={}",
                    wxMessage.getFromUser(), e);
        }
        return inbound;
    }

    private WechatMpIdentityEnrichContext buildEnrichContext(WxMpXmlMessage wxMessage, String appId,
            String eventType, String rawXml, String syncContactId, boolean syncWroteUnmatched,
            Long unmatchedId, String channelEventId)
    {
        WechatMpIdentityEnrichContext ctx = new WechatMpIdentityEnrichContext();
        ctx.setTenantId(TenantDataSourceSupport.currentTenantKey());
        ctx.setAppId(appId);
        ctx.setOpenId(wxMessage.getFromUser());
        ctx.setChannelEventId(channelEventId);
        ctx.setEventType(eventType);
        ctx.setRawXml(rawXml);
        ctx.setXmlUnionId(wxMessage.getUnionId());
        ctx.setSyncContactId(syncContactId);
        ctx.setSyncWroteUnmatched(syncWroteUnmatched);
        ctx.setUnmatchedId(unmatchedId);
        return ctx;
    }

    // ========================
    // 事件类型映射
    // ========================

    private String resolveEventType(WxMpXmlMessage msg)
    {
        if ("event".equals(msg.getMsgType()))
        {
            switch (msg.getEvent())
            {
                case "subscribe":   return "channel.wechat_mp.follow";
                case "unsubscribe": return "channel.wechat_mp.unfollow";
                case "CLICK":
                case "VIEW":        return "channel.wechat_mp.menu.click";
                case "SCAN":        return "channel.wechat_mp.scan";
                case "TEMPLATESENDJOBFINISH": return "channel.wechat_mp.message.sent";
                case "LOCATION":    return "channel.wechat_mp.location";
                case "MASSSENDJOBFINISH": return "channel.wechat_mp.mass.sent";
                case "PUBLISHJOBFINISH": return "channel.wechat_mp.publish.finish";
            }
        }
        if ("text".equals(msg.getMsgType()))
        {
            return "channel.wechat_mp.message.received";
        }
        if ("image".equals(msg.getMsgType()))
        {
            return "channel.wechat_mp.message.received";
        }
        return null;
    }

    private String resolveEventCategory(String eventType)
    {
        if (eventType == null) return "channel";
        if (eventType.contains("message")) return "engagement";
        if (eventType.contains("follow") || eventType.contains("unfollow")) return "channel";
        if (eventType.contains("scan")) return "engagement";
        return "channel";
    }

    private int resolveImportance(String eventType)
    {
        if (eventType == null) return 30;
        if (eventType.contains("follow")) return 80;
        if (eventType.contains("unfollow")) return 50;
        if (eventType.contains("message.received")) return 20;
        if (eventType.contains("message.sent")) return 30;
        if (eventType.contains("scan")) return 40;
        if (eventType.contains("menu.click")) return 30;
        return 30;
    }

    private String resolveMessageData(String eventType, WxMpXmlMessage msg)
    {
        if ("channel.wechat_mp.follow".equals(eventType))
        {
            return "关注公众号";
        }
        if ("channel.wechat_mp.unfollow".equals(eventType))
        {
            return "取消关注公众号";
        }
        if ("channel.wechat_mp.scan".equals(eventType))
        {
            return "扫码事件";
        }
        if ("channel.wechat_mp.menu.click".equals(eventType))
        {
            return "点击菜单: " + (msg.getEventKey() != null ? msg.getEventKey() : "");
        }
        if ("channel.wechat_mp.message.received".equals(eventType))
        {
            if ("text".equals(msg.getMsgType()) && msg.getContent() != null)
            {
                return "收到消息: " + (msg.getContent().length() > 50
                        ? msg.getContent().substring(0, 50) + "..." : msg.getContent());
            }
            return "收到" + msg.getMsgType() + "消息";
        }
        if ("channel.wechat_mp.message.sent".equals(eventType))
        {
            return "模板消息发送完成";
        }
        return eventType;
    }

    private String buildPayload(WxMpXmlMessage msg)
    {
        JSONObject json = new JSONObject();
        json.put("open_id", msg.getFromUser());
        json.put("to_user", msg.getToUser());
        json.put("msg_type", msg.getMsgType());
        json.put("event", msg.getEvent());
        json.put("event_key", msg.getEventKey());
        json.put("ticket", msg.getTicket());
        json.put("msg_id", msg.getMsgId());
        if (msg.getContent() != null)
        {
            json.put("content", msg.getContent());
        }
        return json.toString();
    }

    private String buildContext(WxMpXmlMessage msg, String appId)
    {
        JSONObject json = new JSONObject();
        if (appId != null)
        {
            json.put("app_id", appId);
        }
        if (msg.getFromUser() != null)
        {
            json.put("open_id", msg.getFromUser());
        }
        json.put("msg_type", msg.getMsgType());
        json.put("create_time", msg.getCreateTime());
        return json.toString();
    }

    // ========================
    // 未匹配记录写入
    // ========================

    private void maybeTriggerRegistrationAutomation(WxMpXmlMessage wxMessage, String appId,
            ActivityEventDTO dto, String openId)
    {
        if (!"event".equals(wxMessage.getMsgType()) || !"subscribe".equals(wxMessage.getEvent()))
        {
            return;
        }
        if (!StringUtils.hasText(dto.getContext()) || !StringUtils.hasText(openId))
        {
            return;
        }
        JSONObject ctx = JSONObject.parseObject(dto.getContext());
        String crmEventId = ctx.getString("crm_event_id");
        if (!StringUtils.hasText(crmEventId))
        {
            return;
        }
        String tenantId = TenantDataSourceSupport.currentTenantKey();
        if (!StringUtils.hasText(tenantId))
        {
            return;
        }
        EventMpOutreachContext outreachCtx = new EventMpOutreachContext();
        outreachCtx.setTenantId(tenantId);
        outreachCtx.setAppId(appId);
        outreachCtx.setOpenId(openId);
        outreachCtx.setCrmEventId(crmEventId);
        nurtureHookSupport.onMpSubscribeAttributed(outreachCtx);
        if (!nurtureHookSupport.shouldSkipLegacyMpAutomation(tenantId, crmEventId))
        {
            registrationAutomationWorker.scheduleAttributed(outreachCtx);
        }
    }

    private void enrichMenuName(String appId, WxMpXmlMessage msg, ActivityEventDTO dto)
    {
        if (!"channel.wechat_mp.menu.click".equals(dto.getEventType()) || msg.getEventKey() == null)
        {
            return;
        }
        String menuName = menuKeyLabelMapper.selectNameByKey(appId, msg.getEventKey());
        if (menuName == null)
        {
            return;
        }
        JSONObject payload = JSONObject.parseObject(dto.getPayload());
        payload.put("menu_key", msg.getEventKey());
        payload.put("menu_name", menuName);
        dto.setPayload(payload.toString());
    }

    // ========================
    // 发布结果回调处理
    // ========================

    /**
     * 处理 PUBLISHJOBFINISH 回调事件，解析原始 XML 中的 PublishEventInfo
     * 并更新 crm_wechat_mp_publish_task 表。
     */
    private void handlePublishJobFinish(String rawXml, String appId)
    {
        try
        {
            log.info("PUBLISHJOBFINISH raw XML: {}", rawXml);
            
            Document doc = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder()
                    .parse(new InputSource(new java.io.StringReader(rawXml)));

            String publishId = getXmlChildText(doc, "publish_id");
            if (!StringUtils.hasText(publishId))
            {
                log.warn("PUBLISHJOBFINISH missing publish_id in rawXml");
                return;
            }

            String publishStatusStr = getXmlChildText(doc, "publish_status");
            int publishStatus = StringUtils.hasText(publishStatusStr)
                    ? Integer.parseInt(publishStatusStr) : -1;

            String articleId = getXmlChildText(doc, "article_id");
            String articleUrl = getXmlChildText(doc, "article_url");

            // 收集 fail_idx（可能有多个，仅在 PublishEventInfo 下出现）
            StringBuilder failIdxs = new StringBuilder();
            NodeList failIdxNodes = doc.getElementsByTagName("fail_idx");
            for (int i = 0; i < failIdxNodes.getLength(); i++)
            {
                if (failIdxs.length() > 0)
                {
                    failIdxs.append(",");
                }
                failIdxs.append(failIdxNodes.item(i).getTextContent());
            }

            CrmWechatMpPublishTask task = new CrmWechatMpPublishTask();
            task.setPublishId(publishId);
            task.setPublishStatus(publishStatus);
            task.setArticleId(StringUtils.hasText(articleId) ? articleId : null);
            task.setArticleUrl(StringUtils.hasText(articleUrl) ? articleUrl : null);
            task.setFailIdxs(failIdxs.length() > 0 ? failIdxs.toString() : null);
            publishTaskMapper.updateByPublishId(task);

            log.info("PUBLISHJOBFINISH updated: publishId={}, status={}, articleId={}",
                    publishId, publishStatus, articleId);
        }
        catch (Exception e)
        {
            log.error("Failed to handle PUBLISHJOBFINISH callback for appId={}", appId, e);
        }
    }

    /**
     * 处理 MASSSENDJOBFINISH：按 MsgID 回写 crm_wechat_mp_mass_task（幂等）。
     */
    private void handleMassSendJobFinish(String rawXml, String appId, WxMpXmlMessage wxMessage)
    {
        try
        {
            Document doc = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder()
                    .parse(new InputSource(new java.io.StringReader(rawXml)));

            String msgId = getXmlChildText(doc, "MsgID");
            if (!StringUtils.hasText(msgId))
            {
                msgId = getXmlChildText(doc, "MsgId");
            }
            if (!StringUtils.hasText(msgId) && wxMessage != null && wxMessage.getMsgId() != null)
            {
                msgId = String.valueOf(wxMessage.getMsgId());
            }
            if (!StringUtils.hasText(msgId))
            {
                log.warn("MASSSENDJOBFINISH missing MsgID in rawXml appId={}", appId);
                return;
            }

            String status = getXmlChildText(doc, "Status");
            Integer totalCount = parseXmlInt(getXmlChildText(doc, "TotalCount"));
            Integer filterCount = parseXmlInt(getXmlChildText(doc, "FilterCount"));
            Integer sentCount = parseXmlInt(getXmlChildText(doc, "SentCount"));
            Integer errorCount = parseXmlInt(getXmlChildText(doc, "ErrorCount"));

            massService.applyMassSendJobFinish(appId, msgId, status,
                    totalCount, filterCount, sentCount, errorCount);
        }
        catch (Exception e)
        {
            log.error("Failed to handle MASSSENDJOBFINISH callback for appId={}", appId, e);
        }
    }

    private static Integer parseXmlInt(String text)
    {
        if (!StringUtils.hasText(text))
        {
            return null;
        }
        try
        {
            return Integer.parseInt(text.trim());
        }
        catch (NumberFormatException e)
        {
            return null;
        }
    }

    /**
     * 从 XML Document 中提取指定标签名的文本内容（从根元素开始的全局搜索）。
     */
    private static String getXmlChildText(Document doc, String tagName)
    {
        NodeList nodes = doc.getElementsByTagName(tagName);
        if (nodes.getLength() > 0)
        {
            return nodes.item(0).getTextContent();
        }
        return null;
    }

    private Long saveUnmatched(String openId, String pulseId, String rawXml, String appId, String eventType)
    {
        ActivityUnmatched unmatched = new ActivityUnmatched();
        unmatched.setPulseId(pulseId);
        unmatched.setIdentityType("wechat_mp_openid");
        unmatched.setIdentityValue(openId);
        unmatched.setUnionId(null);
        unmatched.setUnionIdSource(null);
        unmatched.setAppId(appId);
        unmatched.setMaxRetries(12);
        if ("channel.wechat_mp.follow".equals(eventType) || "channel.wechat_mp.scan".equals(eventType))
        {
            unmatched.setNextRetryAt(LocalDateTime.now().plusSeconds(30));
        }
        else
        {
            unmatched.setNextRetryAt(LocalDateTime.now().plusMinutes(5));
        }
        unmatched.setRawPayload(rawXml);
        try
        {
            unmatchedMapper.insert(unmatched);
            return unmatched.getId();
        }
        catch (Exception e)
        {
            log.warn("Failed to insert unmatched record for openId={}, pulseId={}",
                    openId, pulseId, e);
            return null;
        }
    }
}
