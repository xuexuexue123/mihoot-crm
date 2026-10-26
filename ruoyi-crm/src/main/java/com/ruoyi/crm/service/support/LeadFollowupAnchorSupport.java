package com.ruoyi.crm.service.support;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.crm.domain.CrmActivity;
import com.ruoyi.crm.domain.CrmLead;
import com.ruoyi.crm.domain.CrmTask;
import com.ruoyi.crm.domain.dto.ActivityEventDTO;
import com.ruoyi.crm.mapper.CrmLeadMapper;

/**
 * 有效跟进锚点：仅白名单事件刷新 crm_lead.last_followup_at。
 */
@Component
public class LeadFollowupAnchorSupport
{
    private static final Logger log = LoggerFactory.getLogger(LeadFollowupAnchorSupport.class);

    private static final Set<String> EVENT_WHITELIST = new HashSet<>(Arrays.asList(
            "channel.wecom.message.sent",
            "channel.wecom.message.received",
            "channel.wechat_mp.message.sent",
            "channel.wechat_mp.message.received",
            "whatsapp.message.sent",
            "whatsapp.message.received",
            "messenger.message.sent",
            "messenger.message.received",
            "webchat.message.sent",
            "webchat.message.received"));

    private static final Set<String> COMM_WHITELIST = new HashSet<>(Arrays.asList(
            "call", "outbound", "inbound", "外呼", "来电", "电话",
            "visit", "拜访", "短信", "sms", "message", "微信"));

    @Autowired
    private CrmLeadMapper crmLeadMapper;

    public void onActivityProduced(ActivityEventDTO event, CrmActivity activity)
    {
        if (event == null)
        {
            return;
        }
        if (isAiHosted(event.getPayload()))
        {
            return;
        }
        if (!isWhitelistedEvent(event.getEventType()))
        {
            return;
        }
        Date at = event.getStartedDate() != null ? event.getStartedDate() : new Date();
        String leadId = resolveLeadId(event.getSubjectType(), event.getSubjectId(),
                event.getRelateType(), event.getRelateId(), event.getPayload());
        touch(leadId, at);
    }

    public void onManualActivityInserted(CrmActivity activity)
    {
        if (activity == null)
        {
            return;
        }
        if (isWhitelistedEvent(activity.getEventType()) || isWhitelistedCommunication(activity.getCommunicationType()))
        {
            Date at = activity.getStartedDate() != null ? activity.getStartedDate() : new Date();
            String leadId = resolveLeadId(activity.getSubjectType(), activity.getSubjectId(),
                    activity.getRelateType(), activity.getRelateId(), activity.getPayload());
            touch(leadId, at);
        }
    }

    public void onTaskCompleted(CrmTask previous, CrmTask updated)
    {
        if (updated == null || updated.getRelatedToid() == null)
        {
            return;
        }
        if (!isLeadRelated(updated.getRelatedTotype()))
        {
            return;
        }
        boolean nowDone = isCompleted(updated);
        boolean wasDone = isCompleted(previous);
        if (!nowDone || wasDone)
        {
            return;
        }
        Date at = updated.getCompletedDate() != null ? updated.getCompletedDate() : new Date();
        touch(updated.getRelatedToid().trim(), at);
    }

    public void onLeadStageChanged(CrmLead previous, CrmLead updated)
    {
        if (previous == null || updated == null || updated.getLeadId() == null)
        {
            return;
        }
        String before = previous.getStage() == null ? "" : previous.getStage().trim();
        String after = updated.getStage() == null ? "" : updated.getStage().trim();
        if (before.equals(after) || after.isEmpty())
        {
            return;
        }
        touch(updated.getLeadId(), new Date());
    }

    public void touch(String leadId, Date at)
    {
        if (!StringUtils.hasText(leadId) || at == null)
        {
            return;
        }
        try
        {
            crmLeadMapper.touchLastFollowupAt(leadId.trim(), at);
        }
        catch (Exception ex)
        {
            log.debug("touch last_followup_at skipped leadId={}: {}", leadId, ex.getMessage());
        }
    }

    static boolean isWhitelistedEvent(String eventType)
    {
        return eventType != null && EVENT_WHITELIST.contains(eventType.trim());
    }

    static boolean isWhitelistedCommunication(String communicationType)
    {
        if (!StringUtils.hasText(communicationType))
        {
            return false;
        }
        String raw = communicationType.trim().toLowerCase();
        if (raw.contains("备注") || raw.contains("note") || raw.contains("tag"))
        {
            return false;
        }
        for (String token : COMM_WHITELIST)
        {
            if (raw.contains(token.toLowerCase()))
            {
                return true;
            }
        }
        return false;
    }

    private String resolveLeadId(String subjectType, String subjectId, String relateType, String relateId,
            String payload)
    {
        if (isLeadRelated(subjectType) && StringUtils.hasText(subjectId))
        {
            return subjectId.trim();
        }
        if (isLeadRelated(relateType) && StringUtils.hasText(relateId))
        {
            return relateId.trim();
        }
        if (StringUtils.hasText(subjectId) && ("Contact".equalsIgnoreCase(subjectType) || "联系人".equals(subjectType)))
        {
            CrmLead byContact = crmLeadMapper.selectAssignedByContactId(subjectId.trim());
            if (byContact != null)
            {
                return byContact.getLeadId();
            }
        }
        String externalUserid = extractPayloadString(payload, "external_userid", "wecomExternalUserid");
        if (StringUtils.hasText(externalUserid))
        {
            CrmLead byWecom = crmLeadMapper.selectAssignedByWecomExternalUserid(externalUserid.trim());
            if (byWecom != null)
            {
                return byWecom.getLeadId();
            }
        }
        return null;
    }

    private static boolean isLeadRelated(String type)
    {
        if (!StringUtils.hasText(type))
        {
            return false;
        }
        String t = type.trim();
        return "Lead".equalsIgnoreCase(t) || "业务线索".equals(t) || "lead".equalsIgnoreCase(t);
    }

    private static boolean isCompleted(CrmTask task)
    {
        if (task == null)
        {
            return false;
        }
        if (task.getCompletedDate() != null)
        {
            return true;
        }
        String detail = task.getStatusDetail();
        if (!StringUtils.hasText(detail))
        {
            return false;
        }
        String n = detail.trim().toLowerCase();
        return n.contains("complete") || n.contains("完成") || n.contains("closed") || "done".equals(n);
    }

    private static boolean isAiHosted(String payload)
    {
        if (!StringUtils.hasText(payload))
        {
            return false;
        }
        try
        {
            JSONObject json = JSON.parseObject(payload);
            if (json == null)
            {
                return false;
            }
            Object hosted = json.get("hosted_by_ai");
            if (hosted == null)
            {
                hosted = json.get("hostedByAi");
            }
            return Boolean.TRUE.equals(hosted) || "true".equalsIgnoreCase(String.valueOf(hosted));
        }
        catch (Exception ex)
        {
            return false;
        }
    }

    private static String extractPayloadString(String payload, String... keys)
    {
        if (!StringUtils.hasText(payload) || keys == null)
        {
            return null;
        }
        try
        {
            JSONObject json = JSON.parseObject(payload);
            if (json == null)
            {
                return null;
            }
            for (String key : keys)
            {
                String value = json.getString(key);
                if (StringUtils.hasText(value))
                {
                    return value;
                }
            }
        }
        catch (Exception ignored)
        {
        }
        return null;
    }
}
