package com.ruoyi.crm.service.support;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruoyi.common.utils.StringUtils;

import com.ruoyi.crm.domain.CrmLead;
import com.ruoyi.crm.mapper.CrmLeadMapper;
import com.ruoyi.crm.wechatmp.domain.CrmWechatMpFan;
import com.ruoyi.crm.wechatmp.mapper.CrmWechatMpFanMapper;

/**
 * 活动报名 lead 解析（自动化 skip 与 H5 upsert 共用）
 */
@Service
public class OutreachEventLeadResolveService
{
    @Autowired
    private CrmLeadMapper crmLeadMapper;

    @Autowired
    private CrmWechatMpFanMapper fanMapper;

    public CrmLead resolveLeadForEvent(String eventId, String openId, String appId)
    {
        if (!StringUtils.isNotEmpty(eventId) || !StringUtils.isNotEmpty(openId))
        {
            return null;
        }
        CrmLead byOpenId = crmLeadMapper.selectBySidAndMpOpenId(eventId, openId, appId);
        if (byOpenId != null)
        {
            return byOpenId;
        }
        if (StringUtils.isNotEmpty(appId))
        {
            CrmWechatMpFan fan = fanMapper.selectByOpenId(appId, openId);
            if (fan != null && StringUtils.isNotEmpty(fan.getContactId()))
            {
                CrmLead query = new CrmLead();
                query.setSid(eventId);
                query.setContactId(fan.getContactId());
                java.util.List<CrmLead> leads = crmLeadMapper.selectCrmLeadList(query);
                if (leads != null && !leads.isEmpty())
                {
                    return leads.get(0);
                }
            }
        }
        return null;
    }

    public CrmLead resolveExistingLead(String eventId, String mobilePhone, String openId, String appId)
    {
        if (StringUtils.isNotEmpty(mobilePhone))
        {
            String normalized = PhoneNormalizer.normalize(mobilePhone);
            String phone = StringUtils.isNotEmpty(normalized) ? normalized : mobilePhone.trim();
            CrmLead byPhone = crmLeadMapper.selectBySidAndMobilePhone(eventId, phone);
            if (byPhone != null)
            {
                return byPhone;
            }
        }
        return resolveLeadForEvent(eventId, openId, appId);
    }
}
