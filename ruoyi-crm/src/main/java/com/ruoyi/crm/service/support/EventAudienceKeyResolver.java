package com.ruoyi.crm.service.support;

import org.springframework.stereotype.Component;

import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.domain.dto.AudienceKeyHints;

/**
 * 受众去重键解析，与 event-audience-backend-plan §6.4 一致
 */
@Component
public class EventAudienceKeyResolver
{
    public String resolve(AudienceKeyHints hints)
    {
        if (hints == null)
        {
            return null;
        }
        if (StringUtils.isNotEmpty(hints.getContactId()))
        {
            return "contact:" + hints.getContactId();
        }
        String mobile = PhoneNormalizer.normalize(hints.getMobilePhone());
        if (StringUtils.isNotEmpty(mobile))
        {
            return "mobile:" + mobile;
        }
        if (StringUtils.isNotEmpty(hints.getUnionid()))
        {
            return "unionid:" + hints.getUnionid();
        }
        if (StringUtils.isNotEmpty(hints.getLeadId()))
        {
            return "h5:" + hints.getLeadId();
        }
        if (StringUtils.isNotEmpty(hints.getWecomExternalUserid()))
        {
            return "wecom:" + hints.getWecomExternalUserid();
        }
        if (StringUtils.isNotEmpty(hints.getWechatMpOpenid()))
        {
            return "mp:" + hints.getWechatMpOpenid();
        }
        return null;
    }

    public String resolveFromMergedGroup(String contactId, String mobilePhone, String unionid,
            String leadId, String wecomExternalUserid, String wechatMpOpenid)
    {
        return resolve(AudienceKeyHints.create()
                .contactId(contactId)
                .mobilePhone(mobilePhone)
                .unionid(unionid)
                .leadId(leadId)
                .wecomExternalUserid(wecomExternalUserid)
                .wechatMpOpenid(wechatMpOpenid));
    }
}
