package com.ruoyi.crm.wechatmp.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.crm.domain.CrmContact;
import com.ruoyi.crm.domain.CrmLead;
import com.ruoyi.crm.mapper.CrmContactMapper;
import com.ruoyi.crm.mapper.CrmLeadMapper;
import com.ruoyi.crm.service.support.MpLeadIdentitySupport;
import com.ruoyi.crm.wechatmp.domain.ActivityUnmatched;
import com.ruoyi.crm.wechatmp.domain.CrmWechatMpFan;
import com.ruoyi.crm.wechatmp.dto.WechatMpUserProfile;
import com.ruoyi.crm.wechatmp.mapper.ActivityUnmatchedMapper;
import com.ruoyi.crm.wechatmp.mapper.CrmWechatMpFanMapper;

/**
 * Lead 转化为 Contact 后，闭环公众号身份（unionid identity + fan contact_id + unmatched Activity）。
 */
@Component
public class MpLeadConvertSyncSupport
{
    private static final Logger log = LoggerFactory.getLogger(MpLeadConvertSyncSupport.class);

    @Autowired
    private CrmLeadMapper crmLeadMapper;

    @Autowired
    private CrmContactMapper crmContactMapper;

    @Autowired
    private CrmWechatMpFanMapper fanMapper;

    @Autowired
    private ActivityUnmatchedMapper unmatchedMapper;

    @Autowired
    private WechatMpIdentityResolutionSupport mpIdentityResolutionSupport;

    @Autowired
    private WechatMpUserProfileResolver userProfileResolver;

    /**
     * Lead 转 Contact 后最佳努力同步公众号数据；失败不抛异常。
     */
    public void syncAfterLeadConverted(String leadId, String contactId)
    {
        if (!StringUtils.hasText(leadId) || !StringUtils.hasText(contactId))
        {
            return;
        }
        try
        {
            CrmLead lead = crmLeadMapper.selectCrmLeadByLeadId(leadId.trim());
            CrmContact contact = crmContactMapper.selectCrmContactByContactId(contactId.trim());
            if (lead == null || contact == null)
            {
                return;
            }

            MpOpenIdContext ctx = resolveOpenIdContext(lead);
            if (ctx == null || !StringUtils.hasText(ctx.openId))
            {
                return;
            }

            String openId = ctx.openId.trim();
            String appId = StringUtils.hasText(ctx.appId) ? ctx.appId.trim() : null;
            String contactIdTrimmed = contactId.trim();

            CrmWechatMpFan fan = resolveFan(openId, appId);
            if (fan != null && !StringUtils.hasText(appId))
            {
                appId = fan.getAppId();
            }

            List<ActivityUnmatched> pendingUnmatched = unmatchedMapper.selectPendingByOpenId(openId, appId);
            String unionId = resolveUnionId(openId, appId, fan, pendingUnmatched);

            mpIdentityResolutionSupport.bindOpenIdIfNeeded(
                    openId, contactIdTrimmed, WechatMpIdentityResolutionSupport.SOURCE_LEAD_CONVERT);
            if (StringUtils.hasText(unionId))
            {
                mpIdentityResolutionSupport.bindUnionIdIfNeeded(
                        unionId, contactIdTrimmed, WechatMpIdentityResolutionSupport.SOURCE_LEAD_CONVERT);
            }

            upsertFanContactId(openId, appId, fan, unionId, contactIdTrimmed);

            if (pendingUnmatched != null)
            {
                for (ActivityUnmatched record : pendingUnmatched)
                {
                    if (record == null)
                    {
                        continue;
                    }
                    mpIdentityResolutionSupport.markUnmatchedMatched(
                            record, contactIdTrimmed, record.getPulseId(), false);
                }
            }
        }
        catch (Exception e)
        {
            log.warn("MP lead convert sync failed leadId={} contactId={}: {}",
                    leadId, contactId, e.getMessage());
        }
    }

    private MpOpenIdContext resolveOpenIdContext(CrmLead lead)
    {
        String openId = lead.getWechatMpOpenid();
        String appId = lead.getWechatMpAppId();
        if (StringUtils.hasText(openId))
        {
            MpOpenIdContext ctx = new MpOpenIdContext();
            ctx.openId = openId;
            ctx.appId = appId;
            return ctx;
        }

        JSONObject customField = MpLeadIdentitySupport.parseCustomField(lead.getCustomField());
        openId = customField.getString(MpLeadIdentitySupport.CUSTOM_FIELD_OPENID);
        if (!StringUtils.hasText(openId))
        {
            return null;
        }
        MpOpenIdContext ctx = new MpOpenIdContext();
        ctx.openId = openId;
        ctx.appId = customField.getString(MpLeadIdentitySupport.CUSTOM_FIELD_APP_ID);
        return ctx;
    }

    private CrmWechatMpFan resolveFan(String openId, String appId)
    {
        if (StringUtils.hasText(appId))
        {
            return fanMapper.selectByOpenId(appId, openId);
        }
        List<CrmWechatMpFan> subscribed = fanMapper.selectSubscribedByOpenId(openId);
        if (subscribed == null || subscribed.isEmpty())
        {
            return null;
        }
        return subscribed.get(0);
    }

    private String resolveUnionId(String openId, String appId, CrmWechatMpFan fan,
            List<ActivityUnmatched> pendingUnmatched)
    {
        if (fan != null && StringUtils.hasText(fan.getUnionId()))
        {
            return fan.getUnionId().trim();
        }
        if (pendingUnmatched != null)
        {
            for (ActivityUnmatched record : pendingUnmatched)
            {
                if (record != null && StringUtils.hasText(record.getUnionId()))
                {
                    return record.getUnionId().trim();
                }
            }
        }
        if (StringUtils.hasText(appId))
        {
            return userProfileResolver.resolve(appId, openId)
                    .map(WechatMpUserProfile::getUnionId)
                    .filter(StringUtils::hasText)
                    .map(String::trim)
                    .orElse(null);
        }
        return null;
    }

    private void upsertFanContactId(String openId, String appId, CrmWechatMpFan fan,
            String unionId, String contactId)
    {
        if (!StringUtils.hasText(appId))
        {
            return;
        }
        CrmWechatMpFan patch = fan != null ? fan : new CrmWechatMpFan();
        patch.setAppId(appId);
        patch.setOpenId(openId);
        patch.setContactId(contactId);
        if (StringUtils.hasText(unionId))
        {
            patch.setUnionId(unionId);
        }
        patch.setLastSyncAt(DateUtils.getNowDate());
        try
        {
            fanMapper.upsert(patch);
        }
        catch (Exception e)
        {
            log.warn("Failed to upsert fan contact_id appId={} openId={}: {}", appId, openId, e.getMessage());
        }
    }

    private static final class MpOpenIdContext
    {
        private String openId;
        private String appId;
    }
}
