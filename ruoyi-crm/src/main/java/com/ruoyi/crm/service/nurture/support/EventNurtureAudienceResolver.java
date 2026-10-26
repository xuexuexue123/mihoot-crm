package com.ruoyi.crm.service.nurture.support;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.domain.dto.EventNurtureAudienceSnapshot;
import com.ruoyi.crm.domain.vo.EventAudienceRowVo;
import com.ruoyi.crm.service.IEventAudienceService;

@Component
public class EventNurtureAudienceResolver
{
    @Autowired
    private IEventAudienceService eventAudienceService;

    public EventNurtureAudienceSnapshot resolve(String tenantId, String eventId, String audienceKey)
    {
        if (StringUtils.isBlank(audienceKey))
        {
            return null;
        }
        EventAudienceRowVo row = eventAudienceService.findByAudienceKey(tenantId, eventId, audienceKey);
        if (row == null)
        {
            return null;
        }
        return toSnapshot(row);
    }

    public EventNurtureAudienceSnapshot resolveByLeadId(String tenantId, String eventId, String leadId)
    {
        if (StringUtils.isBlank(leadId))
        {
            return null;
        }
        EventAudienceRowVo row = eventAudienceService.findByLeadId(tenantId, eventId, leadId);
        return row != null ? toSnapshot(row) : null;
    }

    public static EventNurtureAudienceSnapshot fromMpSubscribe(String openId, String appId)
    {
        EventNurtureAudienceSnapshot snapshot = new EventNurtureAudienceSnapshot();
        snapshot.setAudienceKey("mp:" + openId);
        snapshot.setWechatMpOpenid(openId);
        snapshot.setWechatMpAppId(appId);
        snapshot.setAudienceStatus("channel_only");
        return snapshot;
    }

    public static EventNurtureAudienceSnapshot toSnapshot(EventAudienceRowVo row)
    {
        EventNurtureAudienceSnapshot snapshot = new EventNurtureAudienceSnapshot();
        snapshot.setAudienceKey(row.getAudienceKey());
        snapshot.setLeadId(row.getLeadId());
        snapshot.setContactId(row.getContactId());
        snapshot.setWechatMpOpenid(row.getWechatMpOpenid());
        snapshot.setWechatMpAppId(row.getWechatMpAppId());
        snapshot.setWecomExternalUserid(row.getWecomExternalUserid());
        snapshot.setDisplayName(row.getDisplayName());
        snapshot.setMobilePhone(row.getMobilePhone());
        snapshot.setAudienceStatus(row.getAudienceStatus());
        return snapshot;
    }
}
