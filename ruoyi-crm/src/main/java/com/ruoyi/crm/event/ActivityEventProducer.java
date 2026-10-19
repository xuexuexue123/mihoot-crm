package com.ruoyi.crm.event;

import java.math.BigDecimal;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.crm.domain.CrmActivity;
import com.ruoyi.crm.domain.CrmAppointment;
import com.ruoyi.crm.domain.CrmBooking;
import com.ruoyi.crm.domain.CrmOpty;
import com.ruoyi.crm.domain.CrmWecomCallbackEvent;
import com.ruoyi.crm.domain.dto.ActivityEventDTO;
import com.ruoyi.crm.service.ICrmActivityService;
import com.ruoyi.crm.service.ICrmCustomerIdentityService;
import com.ruoyi.crm.service.ICustomerMetricsService;
import com.ruoyi.crm.service.support.OutreachRegistrationTokenService;
import com.ruoyi.crm.wecom.WecomExternalContactProfileResolver;
import com.ruoyi.crm.wecom.WecomIdentityResolutionSupport;
import com.ruoyi.crm.wecom.WecomStateParseResult;
import com.ruoyi.crm.wecom.WecomStateParser;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * Activity Event Producer — 统一事件生产入口
 *
 * 职责：
 * 1. 将各渠道原始事件映射为 ActivityEventDTO
 * 2. Identity Resolution（external_userid/openid/unionid → contact_id）
 * 3. 构建 CrmActivity 并写入（幂等）
 * 4. 触发 customer_metrics 增量更新
 *
 * @author luohd
 * @date 2026-06-16
 */
@Slf4j
@Service
public class ActivityEventProducer
{
    @Autowired
    private ICrmCustomerIdentityService identityService;

    @Autowired
    private ICrmActivityService activityService;

    @Autowired
    private ICustomerMetricsService customerMetricsService;

    @Autowired
    private WecomStateParser wecomStateParser;

    @Autowired
    private WecomExternalContactProfileResolver profileResolver;

    @Autowired
    private WecomIdentityResolutionSupport wecomIdentityResolutionSupport;

    @Autowired
    private com.ruoyi.crm.service.support.LeadFollowupAnchorSupport leadFollowupAnchorSupport;

    // ========================
    // 企业微信回调 → ActivityEventDTO
    // ========================

    /**
     * 将企业微信回调事件映射为 ActivityEventDTO
     *
     * @param callbackEvent 企微回调事件（已解析）
     * @param contactId     通过 Identity Resolution 解析得到的 contact_id（可为 null）
     * @return ActivityEventDTO，无法映射返回 null
     */
    public ActivityEventDTO fromWecomCallback(CrmWecomCallbackEvent callbackEvent, String contactId)
    {
        return fromWecomCallback(callbackEvent, contactId, null);
    }

    public ActivityEventDTO fromWecomCallback(CrmWecomCallbackEvent callbackEvent, String contactId, String unionId)
    {
        if (callbackEvent == null)
        {
            return null;
        }

        String changeType = callbackEvent.getChangeType();
        String event = callbackEvent.getEvent();

        // change_external_chat 的 ChangeType 是子类型（create/update/dismiss），
        // 需用 Event 字段做分派，ChangeType 做子类型区分。
        // 群事件通常没有 external_userid，跳过 Identity Resolution，直接返回。
        if ("change_external_chat".equals(event))
        {
            if (changeType == null)
            {
                return null;
            }
            ActivityEventDTO dto = new ActivityEventDTO();
            dto.setChannel("wecom");
            dto.setSource("wecom_callback");
            dto.setChannelEventId("wecom_" + callbackEvent.getId());
            dto.setEventCategory("channel");
            dto.setPayload(buildWecomGroupPayload(callbackEvent));
            dto.setStartedDate(new Date());

            switch (changeType)
            {
                case "create":
                    dto.setEventType("channel.wecom.group.created");
                    dto.setImportance(40);
                    dto.setMessageData("客户群创建");
                    break;
                case "update":
                    String updateDetail = callbackEvent.getUpdateDetail();
                    if (updateDetail != null && updateDetail.contains("add_member"))
                    {
                        dto.setEventType("channel.wecom.group.joined");
                        dto.setImportance(60);
                        dto.setMessageData("客户入群");
                    }
                    else if (updateDetail != null && updateDetail.contains("del_member"))
                    {
                        dto.setEventType("channel.wecom.group.quit");
                        dto.setImportance(50);
                        dto.setMessageData("客户退群");
                    }
                    else
                    {
                        dto.setEventType("channel.wecom.group.changed");
                        dto.setImportance(30);
                        dto.setMessageData("客户群变更");
                    }
                    break;
                case "dismiss":
                    dto.setEventType("channel.wecom.group.dismissed");
                    dto.setImportance(40);
                    dto.setMessageData("客户群解散");
                    break;
                default:
                    log.debug("Unmapped WeCom chat changeType: {}", changeType);
                    return null;
            }
            return dto;
        }

        // change_external_tag 的 ChangeType 是子类型（create/update/delete/shuffle），
        // 同样用 Event + ChangeType 双层分派。
        if ("change_external_tag".equals(event))
        {
            if (changeType == null)
            {
                return null;
            }
            ActivityEventDTO dto = new ActivityEventDTO();
            dto.setChannel("wecom");
            dto.setSource("wecom_callback");
            dto.setChannelEventId("wecom_" + callbackEvent.getId());
            dto.setEventCategory("channel");
            dto.setPayload(buildWecomTagPayload(callbackEvent));
            dto.setStartedDate(new Date());

            switch (changeType)
            {
                case "create":
                    dto.setEventType("channel.wecom.tag.created");
                    dto.setImportance(20);
                    dto.setMessageData("企业标签创建");
                    break;
                case "update":
                    dto.setEventType("channel.wecom.tag.changed");
                    dto.setImportance(20);
                    dto.setMessageData("企业标签变更");
                    break;
                case "delete":
                    dto.setEventType("channel.wecom.tag.deleted");
                    dto.setImportance(20);
                    dto.setMessageData("企业标签删除");
                    break;
                case "shuffle":
                    dto.setEventType("channel.wecom.tag.shuffled");
                    dto.setImportance(20);
                    dto.setMessageData("企业标签重排");
                    break;
                default:
                    log.debug("Unmapped WeCom tag changeType: {}", changeType);
                    return null;
            }
            return dto;
        }

        // change_external_contact 事件：ChangeType 即 add/del 等，沿用原有 switch
        if (changeType == null)
        {
            return null;
        }

        ActivityEventDTO dto = new ActivityEventDTO();
        dto.setChannel("wecom");
        dto.setSource("wecom_callback");

        // channel_event_id = corp_id + event_id 组合（或者直接使用 db id）
        dto.setChannelEventId("wecom_" + callbackEvent.getId());

        // 事件映射
        switch (changeType)
        {
            case "add_external_contact":
            case "add_half_external_contact":
            case "add_follow_user":
                dto.setEventType("channel.wecom.added");
                dto.setEventCategory("channel");
                dto.setImportance(80);
                dto.setMessageData("添加企业微信");
                WecomStateParseResult parsed = wecomStateParser.parse(callbackEvent.getState());
                dto.setPayload(buildWecomAddedPayload(callbackEvent, parsed, unionId));
                if (parsed.getCrmEventId() != null)
                {
                    JSONObject ctx = new JSONObject();
                    ctx.put("crm_event_id", parsed.getCrmEventId());
                    dto.setContext(ctx.toJSONString());
                }
                break;

            case "del_external_contact":
            case "del_follow_user":
                dto.setEventType("channel.wecom.deleted");
                dto.setEventCategory("channel");
                dto.setImportance(50);
                dto.setMessageData("删除企业微信");
                dto.setPayload(buildWecomDeletedPayload(callbackEvent));
                break;

            default:
                log.debug("Unmapped WeCom changeType: {}", changeType);
                return null;
        }

        // Identity Resolution
        if (contactId != null)
        {
            dto.setSubjectType("Contact");
            dto.setSubjectId(contactId);
        }
        else
        {
            String externalUserid = callbackEvent.getExternalUserid();
            if (StringUtils.hasText(unionId))
            {
                String resolved = identityService.findContactByIdentity("wechat_unionid", unionId);
                if (resolved != null)
                {
                    dto.setSubjectType("Contact");
                    dto.setSubjectId(resolved);
                }
            }
            if (dto.getSubjectId() == null && externalUserid != null && !externalUserid.isEmpty())
            {
                String resolvedContactId = identityService.findContactByIdentity(
                        "wecom_external_userid", externalUserid);
                if (resolvedContactId != null)
                {
                    dto.setSubjectType("Contact");
                    dto.setSubjectId(resolvedContactId);
                }
                else
                {
                    log.warn("Unable to resolve contact_id for external_userid={}", externalUserid);
                }
            }
        }

        // 事件发生时间
        // callbackEvent 没有直接的时间字段，使用当前时间
        dto.setStartedDate(new Date());

        return dto;
    }

    // ========================
    // 统一事件生产入口
    // ========================

    /**
     * 从企业微信回调原始 XML 生产 Activity 事件（供 Controller 调用）
     * 解析 XML → 映射事件类型 → Identity Resolution → 写入并触发 Metrics
     *
     * @param plainXml 企业微信回调解密后的明文 XML
     */
    public void produceFromWecomCallbackXml(String plainXml)
    {
        if (plainXml == null || plainXml.isEmpty())
        {
            return;
        }

        try
        {
            // 解析 XML 提取必要字段
            String event = extractXmlValue(plainXml, "Event");
            String changeType = extractXmlValue(plainXml, "ChangeType");
            String externalUserid = extractXmlValue(plainXml, "ExternalUserID");
            if (externalUserid == null)
            {
                externalUserid = extractXmlValue(plainXml, "ExternalUserId");
            }
            String userId = extractXmlValue(plainXml, "UserID");
            String chatId = extractXmlValue(plainXml, "ChatId");
            String state = extractXmlValue(plainXml, "State");
            String source = extractXmlValue(plainXml, "Source");
            String updateDetail = extractXmlValue(plainXml, "UpdateDetail");
            String tagId = extractXmlValue(plainXml, "Id");
            String tagType = extractXmlValue(plainXml, "TagType");

            // 只处理 change_external_contact 和 change_external_chat 事件
            if (!"change_external_contact".equals(event)
                    && !"change_external_chat".equals(event)
                    && !"change_external_tag".equals(event))
            {
                log.debug("Skipping non-contact event: event={}", event);
                return;
            }

            if (changeType == null)
            {
                log.debug("Skipping event without ChangeType");
                return;
            }

            // add 类事件由 CrmWecomExternalContactServiceImpl 在 Lead/Contact 落库后统一产出
            if (isWecomAddContactChangeType(changeType))
            {
                log.debug("Skipping add contact activity in XML producer; handled by external contact service");
                return;
            }

            // 构建临时事件对象
            CrmWecomCallbackEvent callbackEvent = new CrmWecomCallbackEvent();
            callbackEvent.setId(System.currentTimeMillis()); // 临时 ID 用于生成 channel_event_id
            callbackEvent.setEvent(event);
            callbackEvent.setChangeType(changeType);
            callbackEvent.setUserId(userId);
            callbackEvent.setExternalUserid(externalUserid);
            callbackEvent.setChatId(chatId);
            callbackEvent.setState(state);
            callbackEvent.setSource(source);
            callbackEvent.setUpdateDetail(updateDetail);
            callbackEvent.setTagId(tagId);
            callbackEvent.setTagType(tagType);

            // Identity Resolution（add 类事件可经 externalcontact/get 获得 unionid）
            String unionId = null;
            if (isWecomAddContactChangeType(changeType)
                    && externalUserid != null && !externalUserid.isEmpty())
            {
                unionId = profileResolver.fetch(externalUserid)
                        .map(p -> p.getUnionId())
                        .orElse(null);
            }

            String contactId = null;
            if (externalUserid != null && !externalUserid.isEmpty())
            {
                contactId = wecomIdentityResolutionSupport.resolveContactId(externalUserid, unionId);
            }

            // 映射并生产事件
            ActivityEventDTO dto = fromWecomCallback(callbackEvent, contactId, unionId);
            if (dto != null)
            {
                // 使用唯一 channel_event_id（基于 corpId + externalUserid + changeType + timestamp 组合）
                dto.setChannelEventId("wecom_" + externalUserid + "_" + changeType + "_"
                        + System.currentTimeMillis() / 1000);
                produce(dto);
            }
        }
        catch (Exception e)
        {
            log.error("Failed to produce activity event from WeCom XML", e);
        }
    }

    /**
     * 企微回调新建 Lead 后写入 {@code lead.created}（subject=Lead）。
     */
    public void produceWecomLeadCreated(String leadId, String leadName, String externalUserid,
            String followUserid, String state, WecomStateParseResult parsed, String contactId, String unionId)
    {
        if (!StringUtils.hasText(leadId))
        {
            return;
        }
        ActivityEventDTO dto = new ActivityEventDTO();
        dto.setChannel("wecom");
        dto.setChannelEventId("lead_created_" + leadId);
        dto.setEventType("lead.created");
        dto.setEventCategory("crm");
        dto.setImportance(40);
        dto.setSubjectType("Lead");
        dto.setSubjectId(leadId);
        dto.setSource("wecom_callback");
        dto.setMessageData("线索创建（企业微信）");
        dto.setStartedDate(new Date());
        dto.setCreateBy("wecom-callback");
        dto.setRelateType("业务线索");
        dto.setRelateId(leadId);
        dto.setRelateName(leadName);
        dto.setPayload(buildWecomLeadCreatedPayload(externalUserid, followUserid, state, parsed, contactId, unionId));
        produce(dto);
    }

    /**
     * 官网在线预约新建 Lead 后写入 {@code lead.created}（subject=Lead）。
     */
    public void produceWebsiteLeadCreated(String leadId, String leadName, String leadSource, String pagePath)
    {
        if (!StringUtils.hasText(leadId))
        {
            return;
        }
        ActivityEventDTO dto = new ActivityEventDTO();
        dto.setChannel("website");
        dto.setChannelEventId("lead_created_" + leadId);
        dto.setEventType("lead.created");
        dto.setEventCategory("crm");
        dto.setImportance(40);
        dto.setSubjectType("Lead");
        dto.setSubjectId(leadId);
        dto.setSource("website_register");
        dto.setMessageData("线索创建（官网注册）");
        dto.setStartedDate(new Date());
        dto.setCreateBy("website-register");
        dto.setRelateType("业务线索");
        dto.setRelateId(leadId);
        dto.setRelateName(leadName);
        JSONObject payload = new JSONObject();
        if (StringUtils.hasText(leadSource))
        {
            payload.put("lead_source", leadSource);
        }
        if (StringUtils.hasText(pagePath))
        {
            payload.put("page_path", pagePath);
        }
        dto.setPayload(payload.toString());
        produce(dto);
    }

    /**
     * 活动 H5 报名新建 Lead 后写入 {@code lead.created}（subject=Lead）。
     */
    public void produceOutreachLeadCreated(String leadId, String leadName, String eventId, String eventName)
    {
        if (!StringUtils.hasText(leadId))
        {
            return;
        }
        ActivityEventDTO dto = new ActivityEventDTO();
        dto.setChannel("outreach");
        dto.setChannelEventId("lead_created_" + leadId);
        dto.setEventType("lead.created");
        dto.setEventCategory("crm");
        dto.setImportance(40);
        dto.setSubjectType("Lead");
        dto.setSubjectId(leadId);
        dto.setSource("outreach_registration");
        dto.setMessageData("线索创建（活动报名）");
        dto.setStartedDate(new Date());
        dto.setCreateBy("outreach-registration");
        dto.setRelateType("业务线索");
        dto.setRelateId(leadId);
        dto.setRelateName(leadName);
        JSONObject payload = new JSONObject();
        if (StringUtils.hasText(eventId))
        {
            payload.put("event_id", eventId);
        }
        if (StringUtils.hasText(eventName))
        {
            payload.put("event_name", eventName);
        }
        dto.setPayload(payload.toString());
        produce(dto);
    }

    /**
     * 企微加好友 {@code channel.wecom.added}（在 external_contact 落库后调用，含完整 subject 与 follow 快照）。
     */
    public void produceWecomAdded(String externalUserid, String followUserid, String state,
            WecomStateParseResult parsed, String unionId,
            String subjectType, String subjectId,
            Integer addWay, Date addTime)
    {
        produceWecomAdded(externalUserid, followUserid, state, parsed, unionId,
                subjectType, subjectId, addWay, addTime, null, null);
    }

    /**
     * 企微加好友 {@code channel.wecom.added}（在 external_contact 落库后调用，含完整 subject 与 follow 快照）。
     */
    public void produceWecomAdded(String externalUserid, String followUserid, String state,
            WecomStateParseResult parsed, String unionId,
            String subjectType, String subjectId,
            Integer addWay, Date addTime, String resolution, String mergedLeadId)
    {
        if (!StringUtils.hasText(subjectId) || !StringUtils.hasText(subjectType))
        {
            log.warn("Skip channel.wecom.added: missing subject externalUserid={}", externalUserid);
            return;
        }
        long addEpoch = addTime != null ? addTime.getTime() / 1000L : System.currentTimeMillis() / 1000L;
        ActivityEventDTO dto = new ActivityEventDTO();
        dto.setChannel("wecom");
        dto.setChannelEventId("wecom:add:" + externalUserid + ":" + followUserid + ":" + addEpoch);
        dto.setEventType("channel.wecom.added");
        dto.setEventCategory("channel");
        dto.setImportance(80);
        dto.setSubjectType(subjectType);
        dto.setSubjectId(subjectId);
        dto.setSource("wecom_callback");
        dto.setMessageData("添加企业微信");
        dto.setStartedDate(addTime != null ? addTime : new Date());
        dto.setCreateBy("wecom-callback");
        dto.setPayload(buildWecomAddedPayload(externalUserid, followUserid, state, parsed, unionId, addWay, addTime,
                resolution, mergedLeadId));
        if (parsed != null && parsed.getCrmEventId() != null)
        {
            JSONObject ctx = new JSONObject();
            ctx.put("crm_event_id", parsed.getCrmEventId());
            dto.setContext(ctx.toJSONString());
        }
        produce(dto);
    }

    /**
     * Lead 转化为 Contact 后写入 {@code lead.converted}（subject=Contact）。
     */
    public void produceLeadConverted(String leadId, String contactId, String sourceChannel,
            String trigger, boolean merged, String eventId, String wecomState)
    {
        if (!StringUtils.hasText(contactId))
        {
            return;
        }
        ActivityEventDTO dto = new ActivityEventDTO();
        dto.setChannel("crm");
        dto.setChannelEventId("lead_converted_" + leadId + "_" + contactId);
        dto.setEventType("lead.converted");
        dto.setEventCategory("crm");
        dto.setImportance(80);
        dto.setSubjectType("Contact");
        dto.setSubjectId(contactId);
        dto.setSource("lead_convert");
        dto.setMessageData("线索转化");
        dto.setStartedDate(new Date());
        dto.setCreateBy("lead-convert");
        dto.setRelateType("联系人");
        dto.setRelateId(contactId);
        JSONObject payload = new JSONObject();
        payload.put("converted_contact_id", contactId);
        payload.put("lead_id", leadId);
        if (StringUtils.hasText(sourceChannel))
        {
            payload.put("source_channel", sourceChannel);
        }
        if (StringUtils.hasText(trigger))
        {
            payload.put("trigger", trigger);
        }
        payload.put("merged", merged);
        if (StringUtils.hasText(eventId))
        {
            payload.put("event_id", eventId);
        }
        if (StringUtils.hasText(wecomState))
        {
            payload.put("wecom_state", wecomState);
        }
        dto.setPayload(payload.toString());
        produce(dto);
    }

    /**
     * @deprecated 使用 {@link #produceLeadConverted(String, String, String, String, boolean, String, String)}
     */
    public void produceLeadConverted(String leadId, String contactId, String sourceChannel,
            String trigger, boolean merged)
    {
        produceLeadConverted(leadId, contactId, sourceChannel, trigger, merged, null, null);
    }

    /**
     * 商机创建后写入 {@code opportunity.created}（subject=Contact）。
     */
    public void produceOpportunityCreated(CrmOpty opty)
    {
        if (opty == null || !StringUtils.hasText(opty.getContactId()))
        {
            return;
        }
        Date createdAt = opty.getCreateTime() != null ? opty.getCreateTime() : new Date();
        ActivityEventDTO dto = new ActivityEventDTO();
        dto.setChannel("crm");
        dto.setChannelEventId("opty_created_" + opty.getOptyId());
        dto.setEventType("opportunity.created");
        dto.setEventCategory("crm");
        dto.setImportance(70);
        dto.setSubjectType("Contact");
        dto.setSubjectId(opty.getContactId());
        dto.setSource("opportunity_create");
        dto.setStartedDate(createdAt);
        dto.setCreateBy(resolveActivityOperator());
        dto.setRelateType("Opportunity");
        dto.setRelateId(opty.getOptyId());
        dto.setRelateName(opty.getName());
        dto.setMessageData(buildOpportunityCreatedMessage(opty));
        dto.setPayload(buildOpportunityCreatedPayload(opty));
        produce(dto);
    }

    /**
     * 商机赢单后写入 {@code opportunity.won}（subject=Contact）。
     */
    public void produceOpportunityWon(CrmOpty opty)
    {
        if (opty == null || !StringUtils.hasText(opty.getContactId()))
        {
            return;
        }
        Date wonDate = opty.getCloseDate() != null ? opty.getCloseDate() : new Date();
        ActivityEventDTO dto = new ActivityEventDTO();
        dto.setChannel("crm");
        dto.setChannelEventId("opty_won_" + opty.getOptyId());
        dto.setEventType("opportunity.won");
        dto.setEventCategory("crm");
        dto.setImportance(100);
        dto.setSubjectType("Contact");
        dto.setSubjectId(opty.getContactId());
        dto.setSource("opportunity_won");
        dto.setStartedDate(wonDate);
        dto.setCreateBy(resolveActivityOperator());
        dto.setRelateType("Opportunity");
        dto.setRelateId(opty.getOptyId());
        dto.setRelateName(opty.getName());
        dto.setMessageData(buildOpportunityWonMessage(opty));
        dto.setPayload(buildOpportunityWonPayload(opty, wonDate));
        produce(dto);
    }

    /**
     * 预约未到诊：写入 appointment.noshow。
     *
     * @return sourceRef / channelEventId，用于回访规则幂等
     */
    public String produceAppointmentNoshow(CrmAppointment appointment)
    {
        if (appointment == null || !StringUtils.hasText(appointment.getAppointmentId()))
        {
            return null;
        }
        String sourceRef = "appt_noshow_" + appointment.getAppointmentId();
        ActivityEventDTO dto = new ActivityEventDTO();
        dto.setChannel("crm");
        dto.setChannelEventId(sourceRef);
        dto.setEventType("appointment.noshow");
        dto.setEventCategory("crm");
        dto.setImportance(50);
        String relateType = appointment.getRelateType() == null ? "" : appointment.getRelateType();
        boolean lead = "Lead".equalsIgnoreCase(relateType) || "业务线索".equals(relateType);
        dto.setSubjectType(lead ? "Lead" : "Contact");
        dto.setSubjectId(appointment.getRelateId());
        dto.setSource("appointment_noshow");
        dto.setStartedDate(new Date());
        dto.setCreateBy(resolveActivityOperator());
        dto.setRelateType(appointment.getRelateType());
        dto.setRelateId(appointment.getRelateId());
        dto.setRelateName(appointment.getRelateName());
        dto.setMessageData("预约未到诊");
        JSONObject payload = new JSONObject();
        payload.put("appointment_id", appointment.getAppointmentId());
        payload.put("status", appointment.getAppointmentStatus());
        dto.setPayload(payload.toString());
        produce(dto);
        return sourceRef;
    }

    /**
     * 上门预约未到诊：写入 appointment.noshow；payload.appointmentId = booking_id。
     *
     * @return sourceRef / channelEventId，用于回访规则幂等
     */
    public String produceBookingNoshow(CrmBooking booking)
    {
        if (booking == null || !StringUtils.hasText(booking.getBookingId()))
        {
            return null;
        }
        String sourceRef = "booking_noshow_" + booking.getBookingId();
        ActivityEventDTO dto = new ActivityEventDTO();
        dto.setChannel("crm");
        dto.setChannelEventId(sourceRef);
        dto.setEventType("appointment.noshow");
        dto.setEventCategory("crm");
        dto.setImportance(50);
        boolean hasContact = StringUtils.hasText(booking.getContactId());
        dto.setSubjectType(hasContact ? "Contact" : "Lead");
        dto.setSubjectId(hasContact ? booking.getContactId() : booking.getLeadId());
        dto.setSource("booking_noshow");
        dto.setStartedDate(new Date());
        dto.setCreateBy(resolveActivityOperator());
        dto.setRelateType(hasContact ? "联系人" : "业务线索");
        dto.setRelateId(hasContact ? booking.getContactId() : booking.getLeadId());
        dto.setRelateName(booking.getBookingName());
        dto.setMessageData("预约未到诊");
        JSONObject payload = new JSONObject();
        JSONObject appointment = new JSONObject();
        appointment.put("appointmentId", booking.getBookingId());
        appointment.put("scheduledAt", booking.getStartTime() == null ? null : booking.getStartTime().toInstant().toString());
        appointment.put("status", "noshow");
        payload.put("appointment", appointment);
        if (hasContact)
        {
            JSONObject contact = new JSONObject();
            contact.put("contactId", booking.getContactId());
            contact.put("name", booking.getBookingName());
            payload.put("contact", contact);
            if (StringUtils.hasText(booking.getLeadId()))
            {
                JSONObject lead = new JSONObject();
                lead.put("leadId", booking.getLeadId());
                lead.put("name", booking.getBookingName());
                payload.put("lead", lead);
            }
            else
            {
                payload.put("lead", null);
            }
        }
        else
        {
            JSONObject lead = new JSONObject();
            lead.put("leadId", booking.getLeadId());
            lead.put("name", booking.getBookingName());
            payload.put("lead", lead);
            payload.put("contact", null);
        }
        dto.setPayload(payload.toString());
        produce(dto);
        return sourceRef;
    }

    /**
     * 上门预约完成（到诊）：写入 appointment.completed；payload.appointmentId = booking_id。
     * 幂等：channelEventId = booking_completed_{bookingId}（ON CONFLICT 跳过重复）。
     *
     * @return sourceRef / channelEventId
     */
    public String produceBookingCompleted(CrmBooking booking)
    {
        if (booking == null || !StringUtils.hasText(booking.getBookingId()))
        {
            return null;
        }
        String sourceRef = "booking_completed_" + booking.getBookingId();
        Date completedAt = new Date();
        ActivityEventDTO dto = new ActivityEventDTO();
        dto.setChannel("crm");
        dto.setChannelEventId(sourceRef);
        dto.setPulseId(booking.getBookingId());
        dto.setEventType("appointment.completed");
        dto.setEventCategory("appointment");
        dto.setEventName("预约完成");
        dto.setImportance(50);
        boolean hasContact = StringUtils.hasText(booking.getContactId());
        dto.setSubjectType(hasContact ? "Contact" : "Lead");
        dto.setSubjectId(hasContact ? booking.getContactId() : booking.getLeadId());
        dto.setSource("booking_completed");
        dto.setStartedDate(completedAt);
        dto.setCreateBy(resolveActivityOperator());
        dto.setRelateType(hasContact ? "联系人" : "业务线索");
        dto.setRelateId(hasContact ? booking.getContactId() : booking.getLeadId());
        dto.setRelateName(booking.getBookingName());
        dto.setMessageData("预约完成");
        if (booking.getUserId() != null)
        {
            dto.setUserId(booking.getUserId());
        }
        if (booking.getDeptId() != null)
        {
            dto.setDeptId(booking.getDeptId());
        }
        JSONObject payload = new JSONObject();
        JSONObject appointment = new JSONObject();
        appointment.put("appointmentId", booking.getBookingId());
        appointment.put("scheduledAt", booking.getStartTime() == null ? null : booking.getStartTime().toInstant().toString());
        appointment.put("status", "completed");
        appointment.put("completedAt", completedAt.toInstant().toString());
        payload.put("appointment", appointment);
        if (hasContact)
        {
            JSONObject contact = new JSONObject();
            contact.put("contactId", booking.getContactId());
            contact.put("name", booking.getBookingName());
            payload.put("contact", contact);
            if (StringUtils.hasText(booking.getLeadId()))
            {
                JSONObject lead = new JSONObject();
                lead.put("leadId", booking.getLeadId());
                lead.put("name", booking.getBookingName());
                payload.put("lead", lead);
            }
            else
            {
                payload.put("lead", null);
            }
        }
        else
        {
            JSONObject lead = new JSONObject();
            lead.put("leadId", booking.getLeadId());
            lead.put("name", booking.getBookingName());
            payload.put("lead", lead);
            payload.put("contact", null);
        }
        dto.setPayload(payload.toString());
        produce(dto);
        return sourceRef;
    }

    /**
     * 线索归属变更：领取 / 指派 / 企微自动分配
     */
    public void produceLeadAssigned(String leadId, String leadName, Long assigneeUserId, String assigneeName,
            String assigneeSnUserid, String trigger)
    {
        if (!StringUtils.hasText(leadId))
        {
            return;
        }
        ActivityEventDTO dto = new ActivityEventDTO();
        dto.setChannel("crm");
        dto.setChannelEventId("lead_assigned_" + leadId + "_" + System.currentTimeMillis() / 1000);
        dto.setEventType("lead.assigned");
        dto.setEventCategory("crm");
        dto.setImportance(50);
        dto.setSubjectType("Lead");
        dto.setSubjectId(leadId);
        dto.setSource("lead_pool");
        dto.setMessageData("线索归属");
        dto.setStartedDate(new Date());
        dto.setCreateBy(resolveActivityOperator());
        dto.setRelateType("业务线索");
        dto.setRelateId(leadId);
        dto.setRelateName(leadName);
        JSONObject payload = new JSONObject();
        if (assigneeUserId != null)
        {
            payload.put("assigned_toid", String.valueOf(assigneeUserId));
        }
        if (StringUtils.hasText(assigneeName))
        {
            payload.put("assigned_to", assigneeName);
        }
        if (StringUtils.hasText(assigneeSnUserid))
        {
            payload.put("assignee_sn_userid", assigneeSnUserid);
        }
        if (StringUtils.hasText(trigger))
        {
            payload.put("trigger", trigger);
        }
        dto.setPayload(payload.toString());
        produce(dto);
    }

    /**
     * 渠道进线命中未转化线索，不新建第二行。
     */
    public void produceLeadDuplicated(String leadId, String matchedField, String sourceChannel)
    {
        if (!StringUtils.hasText(leadId))
        {
            return;
        }
        ActivityEventDTO dto = new ActivityEventDTO();
        dto.setChannel(StringUtils.hasText(sourceChannel) ? sourceChannel : "crm");
        dto.setChannelEventId("lead_duplicated_" + leadId + "_" + System.currentTimeMillis() / 1000);
        dto.setEventType("lead.duplicated");
        dto.setEventCategory("crm");
        dto.setImportance(40);
        dto.setSubjectType("Lead");
        dto.setSubjectId(leadId);
        dto.setSource("lead_dedupe");
        dto.setMessageData("线索重复");
        dto.setStartedDate(new Date());
        dto.setCreateBy(resolveActivityOperator());
        dto.setRelateType("业务线索");
        dto.setRelateId(leadId);
        JSONObject payload = new JSONObject();
        payload.put("matched_field", matchedField);
        dto.setPayload(payload.toJSONString());
        produce(dto);
    }

    /**
     * 线索释放到公海
     */
    public void produceLeadReleased(String leadId, String leadName, String reason, String trigger)
    {
        if (!StringUtils.hasText(leadId))
        {
            return;
        }
        ActivityEventDTO dto = new ActivityEventDTO();
        dto.setChannel("crm");
        dto.setChannelEventId("lead_released_" + leadId + "_" + System.currentTimeMillis() / 1000);
        dto.setEventType("lead.released");
        dto.setEventCategory("crm");
        dto.setImportance(40);
        dto.setSubjectType("Lead");
        dto.setSubjectId(leadId);
        dto.setSource("lead_pool");
        dto.setMessageData("线索释放");
        dto.setStartedDate(new Date());
        dto.setCreateBy(resolveActivityOperator());
        dto.setRelateType("业务线索");
        dto.setRelateId(leadId);
        dto.setRelateName(leadName);
        JSONObject payload = new JSONObject();
        if (StringUtils.hasText(reason))
        {
            payload.put("reason", reason);
        }
        if (StringUtils.hasText(trigger))
        {
            payload.put("trigger", trigger);
        }
        dto.setPayload(payload.toString());
        produce(dto);
    }

    /**
     * 老客户换渠道触达（企微添加已知 Contact）：不建 Lead，仅记一条「复触」Activity（subject=Contact）。
     */
    public void produceWecomContactRetouch(String contactId, String externalUserid,
            String followUserid, String state, WecomStateParseResult parsed, String unionId)
    {
        if (!StringUtils.hasText(contactId))
        {
            return;
        }
        ActivityEventDTO dto = new ActivityEventDTO();
        dto.setChannel("wecom");
        dto.setChannelEventId("wecom_retouch_" + externalUserid + "_" + System.currentTimeMillis() / 1000);
        dto.setEventType("channel.wecom.re_touch");
        dto.setEventCategory("channel");
        dto.setImportance(60);
        dto.setSubjectType("Contact");
        dto.setSubjectId(contactId);
        dto.setSource("wecom_callback");
        dto.setMessageData("老客户企业微信复触");
        dto.setStartedDate(new Date());
        dto.setCreateBy("wecom-callback");
        dto.setPayload(buildWecomLeadCreatedPayload(externalUserid, followUserid, state, parsed, contactId, unionId));
        produce(dto);
    }

    /**
     * 问卷答卷完成（subject=Contact）。
     */
    public void produceSurveyCompleted(String contactId, String surveyId, String surveyTitle,
            String respondentId, String outreachChannel, Date completedAt)
    {
        if (!StringUtils.hasText(contactId) || !StringUtils.hasText(respondentId))
        {
            return;
        }
        ActivityEventDTO dto = new ActivityEventDTO();
        dto.setChannel("wecom");
        dto.setChannelEventId("survey_completed_" + respondentId);
        dto.setEventType("engagement.survey.completed");
        dto.setEventCategory("engagement");
        dto.setImportance(60);
        dto.setSubjectType("Contact");
        dto.setSubjectId(contactId);
        dto.setSource("system");
        dto.setMessageData("完成问卷");
        dto.setStartedDate(completedAt != null ? completedAt : new Date());
        dto.setCreateBy("system");
        dto.setRelateType("survey");
        dto.setRelateId(surveyId);
        dto.setRelateName(surveyTitle);
        dto.setPayload(buildSurveyCompletedPayload(surveyId, surveyTitle, respondentId, outreachChannel));
        produce(dto);
    }

    /**
     * Lead 评估问卷首次完成（subject=Lead；survey_json 空→非空）。
     * channelEventId 固定为 {@code lead_assessment_{leadId}}，转化迁移后仍幂等。
     */
    public void produceAssessmentCompleted(String leadId, String leadName, Date completedAt)
    {
        produceAssessmentCompleted(leadId, leadName, completedAt, "crm", null);
    }

    /**
     * Lead 评估问卷首次完成（可指定 channel / templateId，供认定外投使用）。
     */
    public void produceAssessmentCompleted(String leadId, String leadName, Date completedAt,
            String channel, String templateId)
    {
        if (!StringUtils.hasText(leadId))
        {
            return;
        }
        boolean qualifyQr = OutreachRegistrationTokenService.CHANNEL_QUALIFY_QR.equals(channel);
        ActivityEventDTO dto = new ActivityEventDTO();
        dto.setChannel(StringUtils.hasText(channel) ? channel : "crm");
        dto.setChannelEventId("lead_assessment_" + leadId);
        dto.setEventType("engagement.assessment.completed");
        dto.setEventCategory("engagement");
        dto.setImportance(qualifyQr ? 70 : 60);
        dto.setSubjectType("Lead");
        dto.setSubjectId(leadId);
        dto.setSource("system");
        dto.setEventName(qualifyQr ? "完成认定评估" : "完成评估");
        dto.setMessageData(qualifyQr ? "完成认定评估" : "完成评估");
        dto.setStartedDate(completedAt != null ? completedAt : new Date());
        dto.setCreateBy("system");
        dto.setRelateType("业务线索");
        dto.setRelateId(leadId);
        dto.setRelateName(leadName);
        dto.setPayload(buildAssessmentCompletedPayload(leadId, channel, templateId));
        produce(dto);
    }

    /**
     * 市场活动报名成功（subject=Contact 或 Lead）。
     * channelEventId 固定为 {@code event:registration:{registrationId}}，与主体无关，转化迁移后仍幂等。
     *
     * @param channel Activity channel：H5=website；CRM/walk-in=manual
     */
    public void produceEventRegistrationSubmitted(String subjectType, String subjectId, String channel,
            String eventId, String eventName, String registrationId, String sessionId,
            String registrationSource, String registrationStatus, Date registeredAt,
            String appId, String openId)
    {
        if (!StringUtils.hasText(subjectType) || !StringUtils.hasText(subjectId)
                || !StringUtils.hasText(registrationId) || !StringUtils.hasText(eventId))
        {
            return;
        }
        String displayEventName = StringUtils.hasText(eventName) ? eventName : eventId;
        ActivityEventDTO dto = new ActivityEventDTO();
        dto.setChannel(StringUtils.hasText(channel) ? channel : "website");
        dto.setChannelEventId("event:registration:" + registrationId);
        dto.setEventType("event.registration.submitted");
        dto.setEventCategory("conversion");
        dto.setImportance(70);
        dto.setSubjectType(subjectType);
        dto.setSubjectId(subjectId);
        dto.setSource("system");
        dto.setEventName("活动报名");
        dto.setMessageData("报名了「" + displayEventName + "」");
        dto.setStartedDate(registeredAt != null ? registeredAt : new Date());
        dto.setCreateBy("system");
        dto.setRelateType("Event");
        dto.setRelateId(eventId);
        dto.setRelateName(eventName);
        dto.setPayload(buildEventRegistrationSubmittedPayload(eventId, eventName, registrationId,
                sessionId, registrationSource, registrationStatus));
        JSONObject context = new JSONObject();
        if (StringUtils.hasText(appId))
        {
            context.put("app_id", appId);
        }
        if (StringUtils.hasText(openId))
        {
            context.put("open_id", openId);
        }
        if (!context.isEmpty())
        {
            dto.setContext(context.toJSONString());
        }
        produce(dto);
    }

    /**
     * 将 ActivityEventDTO 写入 crm_activity（幂等），并触发 Metrics 增量更新
     *
     * @param event 事件 DTO
     */
    public void produce(ActivityEventDTO event)
    {
        if (event == null || event.getEventType() == null)
        {
            log.debug("Skip empty event");
            return;
        }

        try
        {
            CrmActivity activity = buildActivity(event);
            int rows = activityService.insertTimelineActivity(activity);
            if (rows == 0)
            {
                log.debug("Duplicate event skipped: channel={}, channelEventId={}",
                        event.getChannel(), event.getChannelEventId());
                return;
            }

            // 触发 Metrics 增量更新
            if (event.getSubjectId() != null && "Contact".equals(event.getSubjectType()))
            {
                BigDecimal amount = extractAmountFromPayload(event.getPayload());
                customerMetricsService.incrementMetric(
                        event.getSubjectId(), event.getEventType(), event.getChannel(), amount);
            }

            leadFollowupAnchorSupport.onActivityProduced(event, activity);

            log.info("Activity event produced: eventType={}, subjectType={}, subjectId={}",
                    event.getEventType(), event.getSubjectType(), event.getSubjectId());
        }
        catch (Exception e)
        {
            log.error("Failed to produce activity event: eventType={}, channel={}",
                    event.getEventType(), event.getChannel(), e);
        }
    }

    // ========================
    // 内部工具方法
    // ========================

    private static String resolveActivityOperator()
    {
        try
        {
            String username = SecurityUtils.getUsername();
            if (StringUtils.hasText(username))
            {
                return username;
            }
        }
        catch (Exception ignored)
        {
            // 企微回调等无登录态
        }
        return "lead-pool";
    }

    private CrmActivity buildActivity(ActivityEventDTO event)
    {
        CrmActivity activity = new CrmActivity();
        activity.setEventType(event.getEventType());
        activity.setEventName(StringUtils.hasText(event.getEventName())
                ? event.getEventName() : event.getMessageData());
        activity.setEventCategory(event.getEventCategory());
        activity.setChannel(event.getChannel());
        activity.setChannelEventId(event.getChannelEventId());
        activity.setSubjectType(event.getSubjectType());
        activity.setSubjectId(event.getSubjectId());
        activity.setImportance(event.getImportance());
        activity.setStartedDate(event.getStartedDate());
        activity.setSource(event.getSource());
        activity.setPayload(event.getPayload());
        activity.setContext(event.getContext());
        activity.setMessageData(event.getMessageData());
        activity.setStatus("completed");
        activity.setCreateBy(event.getCreateBy() != null ? event.getCreateBy() : "system");
        activity.setRelateType(event.getRelateType());
        activity.setRelateId(event.getRelateId());
        activity.setRelateName(event.getRelateName());
        if (event.getUserId() != null)
        {
            activity.setUserId(event.getUserId());
        }
        if (event.getDeptId() != null)
        {
            activity.setDeptId(event.getDeptId());
        }
        if (StringUtils.hasText(event.getPulseId()))
        {
            activity.setPulseId(event.getPulseId());
        }
        return activity;
    }

    private static String buildWecomLeadCreatedPayload(String externalUserid, String followUserid, String state,
            WecomStateParseResult parsed, String contactId, String unionId)
    {
        JSONObject json = new JSONObject();
        json.put("lead_source", parsed != null && StringUtils.hasText(parsed.getLeadSource())
                ? parsed.getLeadSource() : "wecom");
        if (StringUtils.hasText(externalUserid))
        {
            json.put("external_userid", externalUserid);
        }
        if (StringUtils.hasText(followUserid))
        {
            json.put("staff_userid", followUserid);
        }
        if (StringUtils.hasText(state))
        {
            json.put("state", state);
        }
        if (StringUtils.hasText(contactId))
        {
            json.put("contact_id", contactId);
        }
        if (StringUtils.hasText(unionId))
        {
            json.put("unionid", unionId);
        }
        if (parsed != null)
        {
            if (parsed.getCrmEventId() != null)
            {
                json.put("crm_event_id", parsed.getCrmEventId());
            }
            if (parsed.getChannel() != null)
            {
                json.put("channel", parsed.getChannel());
            }
        }
        return json.toString();
    }

    private String buildWecomAddedPayload(String externalUserid, String followUserid, String state,
            WecomStateParseResult parsed, String unionId, Integer addWay, Date addTime)
    {
        return buildWecomAddedPayload(externalUserid, followUserid, state, parsed, unionId, addWay, addTime,
                null, null);
    }

    private String buildWecomAddedPayload(String externalUserid, String followUserid, String state,
            WecomStateParseResult parsed, String unionId, Integer addWay, Date addTime,
            String resolution, String mergedLeadId)
    {
        JSONObject json = new JSONObject();
        json.put("staff_userid", followUserid);
        json.put("state", state);
        json.put("source", "wecom_callback");
        int effectiveAddWay = addWay != null ? addWay.intValue() : 1;
        json.put("add_way", effectiveAddWay);
        Date effectiveAddTime = addTime != null ? addTime : new Date();
        json.put("add_time", DateTimeFormatter.ISO_INSTANT.format(
                effectiveAddTime.toInstant().atOffset(ZoneOffset.UTC)));
        if (StringUtils.hasText(externalUserid))
        {
            json.put("external_userid", externalUserid);
        }
        if (StringUtils.hasText(unionId))
        {
            json.put("unionid", unionId);
        }
        if (parsed != null)
        {
            if (parsed.getCrmEventId() != null)
            {
                json.put("crm_event_id", parsed.getCrmEventId());
            }
            if (parsed.getChannel() != null)
            {
                json.put("channel", parsed.getChannel());
            }
        }
        if (StringUtils.hasText(resolution))
        {
            json.put("resolution", resolution);
        }
        if (StringUtils.hasText(mergedLeadId))
        {
            json.put("merged_lead_id", mergedLeadId);
        }
        return json.toString();
    }

    private String buildWecomAddedPayload(CrmWecomCallbackEvent event, WecomStateParseResult parsed, String unionId)
    {
        return buildWecomAddedPayload(event.getExternalUserid(), event.getUserId(), event.getState(),
                parsed, unionId, null, new Date());
    }

    private static boolean isWecomAddContactChangeType(String changeType)
    {
        return "add_external_contact".equals(changeType)
                || "add_half_external_contact".equals(changeType)
                || "add_follow_user".equals(changeType);
    }

    private String buildWecomDeletedPayload(CrmWecomCallbackEvent event)
    {
        JSONObject json = new JSONObject();
        json.put("staff_userid", event.getUserId());
        return json.toString();
    }

    private String buildWecomGroupPayload(CrmWecomCallbackEvent event)
    {
        JSONObject json = new JSONObject();
        json.put("chat_id", event.getChatId());
        json.put("update_detail", event.getUpdateDetail());
        return json.toString();
    }

    private String buildWecomTagPayload(CrmWecomCallbackEvent event)
    {
        JSONObject json = new JSONObject();
        json.put("tag_id", event.getTagId());
        json.put("tag_type", event.getTagType());
        return json.toString();
    }

    private static String buildSurveyCompletedPayload(String surveyId, String surveyTitle,
            String respondentId, String outreachChannel)
    {
        JSONObject json = new JSONObject();
        json.put("survey_id", surveyId);
        if (StringUtils.hasText(surveyTitle))
        {
            json.put("survey_title", surveyTitle);
        }
        json.put("respondent_id", respondentId);
        if (StringUtils.hasText(outreachChannel))
        {
            json.put("outreach_channel", outreachChannel);
        }
        return json.toString();
    }

    private static String buildAssessmentCompletedPayload(String leadId)
    {
        return buildAssessmentCompletedPayload(leadId, null, null);
    }

    private static String buildAssessmentCompletedPayload(String leadId, String channel, String templateId)
    {
        JSONObject json = new JSONObject();
        json.put("lead_id", leadId);
        if (OutreachRegistrationTokenService.CHANNEL_QUALIFY_QR.equals(channel))
        {
            json.put("channel", channel);
            if (StringUtils.hasText(templateId))
            {
                json.put("template_id", templateId);
            }
        }
        else
        {
            json.put("source", "lead.survey_json");
        }
        return json.toString();
    }

    private static String buildEventRegistrationSubmittedPayload(String eventId, String eventName,
            String registrationId, String sessionId, String registrationSource, String registrationStatus)
    {
        JSONObject json = new JSONObject();
        json.put("event_id", eventId);
        if (StringUtils.hasText(eventName))
        {
            json.put("event_name", eventName);
        }
        json.put("registration_id", registrationId);
        if (StringUtils.hasText(sessionId))
        {
            json.put("session_id", sessionId);
        }
        if (StringUtils.hasText(registrationSource))
        {
            json.put("registration_source", registrationSource);
        }
        if (StringUtils.hasText(registrationStatus))
        {
            json.put("registration_status", registrationStatus);
        }
        return json.toString();
    }

    private static String buildOpportunityCreatedMessage(CrmOpty opty)
    {
        if (StringUtils.hasText(opty.getName()))
        {
            return "创建商机：" + opty.getName();
        }
        return "创建商机";
    }

    private static String buildOpportunityCreatedPayload(CrmOpty opty)
    {
        JSONObject json = new JSONObject();
        json.put("opty_id", opty.getOptyId());
        if (StringUtils.hasText(opty.getName()))
        {
            json.put("name", opty.getName());
        }
        if (opty.getAmount() != null)
        {
            json.put("amount", opty.getAmount());
        }
        if (StringUtils.hasText(opty.getStage()))
        {
            json.put("stage", opty.getStage());
        }
        if (StringUtils.hasText(opty.getOptySource()))
        {
            json.put("opty_source", opty.getOptySource());
        }
        if (StringUtils.hasText(opty.getLeadId()))
        {
            json.put("lead_id", opty.getLeadId());
        }
        return json.toString();
    }

    private static String buildOpportunityWonMessage(CrmOpty opty)
    {
        String name = StringUtils.hasText(opty.getName()) ? opty.getName() : "商机";
        if (opty.getAmount() == null)
        {
            return name;
        }
        return name + " (¥" + opty.getAmount().stripTrailingZeros().toPlainString() + ")";
    }

    private static String buildOpportunityWonPayload(CrmOpty opty, Date wonDate)
    {
        JSONObject json = new JSONObject();
        json.put("opty_id", opty.getOptyId());
        if (opty.getAmount() != null)
        {
            json.put("amount", opty.getAmount());
        }
        if (StringUtils.hasText(opty.getClosedWonBasis()))
        {
            json.put("closed_won_basis", opty.getClosedWonBasis());
        }
        if (wonDate != null)
        {
            json.put("won_date", DateTimeFormatter.ISO_INSTANT.format(
                    wonDate.toInstant().atOffset(ZoneOffset.UTC)));
        }
        return json.toString();
    }

    /**
     * 从事件 payload JSON 中提取 amount 字段
     */
    private static BigDecimal extractAmountFromPayload(String payload)
    {
        if (payload == null)
        {
            return null;
        }
        try
        {
            JSONObject json = JSONObject.parseObject(payload);
            if (json.containsKey("amount"))
            {
                return json.getBigDecimal("amount");
            }
        }
        catch (Exception ignored)
        {
            // payload 解析失败或不含 amount，忽略
        }
        return null;
    }

    /**
     * 企微客服消息收到后写入 {@code channel.wecom.message.received}（subject=Contact）。
     *
     * @param externalUserid 发消息的外部联系人 ID
     * @param contactId      通过 Identity Resolution 解析得到的 contact_id（可为 null）
     * @param msgid          消息唯一 ID，用于 channelEventId 去重
     * @param msgtype        消息类型（text/image/voice 等）
     * @param content        消息文本内容（可截断）
     * @param sendTime       消息发送时间（Unix 秒）
     */
    public void produceWecomMessageReceived(String externalUserid, String contactId,
            String msgid, String msgtype, String content, long sendTime)
    {
        ActivityEventDTO dto = new ActivityEventDTO();
        dto.setChannel("wecom");
        dto.setChannelEventId("wecom:msg:" + msgid);
        dto.setEventType("channel.wecom.message.received");
        dto.setEventCategory("channel");
        dto.setImportance(40);
        dto.setSource("wecom_kf_callback");
        dto.setMessageData(resolveMessageData(msgtype, content));
        dto.setStartedDate(new Date(sendTime * 1000L));
        dto.setCreateBy("wecom-callback");

        if (StringUtils.hasText(contactId))
        {
            dto.setSubjectType("Contact");
            dto.setSubjectId(contactId);
        }
        else if (StringUtils.hasText(externalUserid))
        {
            String resolved = identityService.findContactByIdentity(
                    "wecom_external_userid", externalUserid);
            if (resolved != null)
            {
                dto.setSubjectType("Contact");
                dto.setSubjectId(resolved);
            }
        }

        JSONObject payload = new JSONObject();
        payload.put("external_userid", externalUserid);
        payload.put("msgtype", msgtype);
        payload.put("msgid", msgid);
        payload.put("send_time", sendTime);
        if (StringUtils.hasText(content))
        {
            String safeContent = content.length() > 200 ? content.substring(0, 200) : content;
            payload.put("content", safeContent);
        }
        dto.setPayload(payload.toString());

        produce(dto);
    }

    /**
     * 企微客服发送消息后写入 {@code channel.wecom.message.sent}（subject=Contact）。
     * 由 {@code CrmWecomKfMessageController.recordReply()} 在回复记录入库后调用。
     *
     * @param externalUserid 消息接收方（外部联系人 ID）
     * @param msgid          消息唯一 ID（可为 null，回退时间戳去重）
     * @param msgtype        消息类型（text/image/voice 等）
     * @param content        消息文本内容（可截断）
     * @param sendTime       消息发送时间（Unix 秒）
     */
    public void produceWecomMessageSent(String externalUserid, String msgid,
            String msgtype, String content, long sendTime)
    {
        ActivityEventDTO dto = new ActivityEventDTO();
        dto.setChannel("wecom");
        String channelEventId = StringUtils.hasText(msgid)
                ? "wecom:msg:sent:" + msgid
                : "wecom:msg:sent:" + externalUserid + ":" + sendTime;
        dto.setChannelEventId(channelEventId);
        dto.setEventType("channel.wecom.message.sent");
        dto.setEventCategory("channel");
        dto.setImportance(40);
        dto.setSource("wecom_kf_callback");
        dto.setMessageData(resolveSentMessageData(msgtype, content));
        dto.setStartedDate(new Date(sendTime * 1000L));
        dto.setCreateBy("wecom-callback");

        if (StringUtils.hasText(externalUserid))
        {
            String resolved = identityService.findContactByIdentity(
                    "wecom_external_userid", externalUserid);
            if (resolved != null)
            {
                dto.setSubjectType("Contact");
                dto.setSubjectId(resolved);
            }
        }

        JSONObject payload = new JSONObject();
        payload.put("external_userid", externalUserid);
        payload.put("msgtype", msgtype);
        if (StringUtils.hasText(msgid))
        {
            payload.put("msgid", msgid);
        }
        payload.put("send_time", sendTime);
        if (StringUtils.hasText(content))
        {
            String safeContent = content.length() > 200 ? content.substring(0, 200) : content;
            payload.put("content", safeContent);
        }
        dto.setPayload(payload.toString());

        produce(dto);
    }

    /**
     * 根据消息类型生成可读摘要
     */
    private static String resolveMessageData(String msgtype, String content)
    {
        if ("image".equals(msgtype))
        {
            return "收到图片消息";
        }
        if ("voice".equals(msgtype))
        {
            return "收到语音消息";
        }
        if ("video".equals(msgtype))
        {
            return "收到视频消息";
        }
        if ("file".equals(msgtype))
        {
            return "收到文件消息";
        }
        if (StringUtils.hasText(content))
        {
            return content.length() > 50 ? content.substring(0, 50) + "…" : content;
        }
        return "收到客服消息";
    }

    /**
     * 根据消息类型生成已发送消息可读摘要
     */
    private static String resolveSentMessageData(String msgtype, String content)
    {
        if ("image".equals(msgtype))
        {
            return "发送图片消息";
        }
        if ("voice".equals(msgtype))
        {
            return "发送语音消息";
        }
        if ("video".equals(msgtype))
        {
            return "发送视频消息";
        }
        if ("file".equals(msgtype))
        {
            return "发送文件消息";
        }
        if (StringUtils.hasText(content))
        {
            return content.length() > 50 ? content.substring(0, 50) + "…" : content;
        }
        return "发送客服消息";
    }

    /**
     * Contact 服务摘要生成成功 → ai.summary_generated
     */
    public void produceAiSummaryGenerated(String contactId,
            com.ruoyi.crm.domain.CrmContactServiceSummary summary, String trigger,
            java.util.Set<String> channels)
    {
        if (!StringUtils.hasText(contactId) || summary == null)
        {
            return;
        }
        ActivityEventDTO dto = new ActivityEventDTO();
        dto.setChannel("ai");
        dto.setChannelEventId("ai:summary:" + contactId + ":v" + summary.getVersion());
        dto.setEventType("ai.summary_generated");
        dto.setEventCategory("ai");
        dto.setSubjectType("Contact");
        dto.setSubjectId(contactId);
        dto.setImportance(50);
        dto.setSource("contact_service_summary");
        dto.setStartedDate(new Date());
        dto.setCreateBy("system");

        String readable = com.ruoyi.crm.service.impl.ContactServiceSummaryServiceImpl.formatReadable(
                com.ruoyi.crm.service.support.ContactServiceSummaryWorker.toSections(summary));
        dto.setMessageData(readable);

        JSONObject payload = new JSONObject();
        payload.put("summary_text", readable);
        payload.put("model", summary.getModel());
        payload.put("trigger", trigger);
        payload.put("version", summary.getVersion());
        payload.put("contact_id", contactId);
        if (channels != null)
        {
            payload.put("channels", channels);
        }
        JSONObject structured = new JSONObject();
        structured.put("currentNeed", summary.getCurrentNeed());
        structured.put("confirmedFacts", summary.getConfirmedFacts());
        structured.put("commitmentsTodos", summary.getCommitmentsTodos());
        structured.put("risksSentiment", summary.getRisksSentiment());
        JSONObject lastTouch = new JSONObject();
        lastTouch.put("channel", summary.getLastTouchChannel());
        lastTouch.put("summary", summary.getLastTouchSummary());
        if (summary.getLastTouchAt() != null)
        {
            lastTouch.put("at", summary.getLastTouchAt());
        }
        structured.put("lastTouch", lastTouch);
        payload.put("structured", structured);
        dto.setPayload(payload.toJSONString());

        produce(dto);
    }

    /**
     * 从 XML 中提取指定标签的值（简单正则匹配，不依赖 XML 解析器）
     */
    private String extractXmlValue(String xml, String tagName)
    {
        if (xml == null || tagName == null) return null;
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(
                "<" + tagName + "><!\\[CDATA\\[(.*?)\\]\\]></" + tagName + ">");
        java.util.regex.Matcher matcher = pattern.matcher(xml);
        if (matcher.find())
        {
            return matcher.group(1);
        }
        // 非 CDATA 格式
        pattern = java.util.regex.Pattern.compile(
                "<" + tagName + ">(.*?)</" + tagName + ">");
        matcher = pattern.matcher(xml);
        if (matcher.find())
        {
            return matcher.group(1);
        }
        return null;
    }
}
