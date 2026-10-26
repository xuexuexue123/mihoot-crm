package com.ruoyi.crm.service.support;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.domain.CrmLead;
import com.ruoyi.crm.domain.vo.LeadConvertConfigVo;
import com.ruoyi.crm.mapper.CrmActivityMapper;
import com.ruoyi.crm.service.ICrmCustomerIdentityService;
import com.ruoyi.crm.service.ICrmLeadConvertConfigService;
import com.ruoyi.crm.wechatmp.domain.CrmWechatMpFan;
import com.ruoyi.crm.wechatmp.mapper.CrmWechatMpFanMapper;
import com.ruoyi.crm.wechatmp.service.WechatMpIdentityResolutionSupport;

/**
 * H5 活动报名身份解析：Contact 优先，其次 Lead upsert/新建
 */
@Service
public class OutreachRegistrationIdentityResolver
{
    @Autowired
    private CrmActivityMapper activityMapper;

    @Autowired
    private CrmWechatMpFanMapper fanMapper;

    @Autowired
    private ICrmCustomerIdentityService identityService;

    @Autowired
    private WechatMpIdentityResolutionSupport mpIdentityResolutionSupport;

    @Autowired
    private ICrmLeadConvertConfigService leadConvertConfigService;

    @Autowired
    private OutreachEventLeadResolveService leadResolveService;

    public OutreachRegistrationIdentityResolution resolve(String tenantId, String eventId,
            String mobilePhone, String openId, String appId)
    {
        if (StringUtils.isNotBlank(openId) && StringUtils.isNotBlank(eventId))
        {
            String contactFromActivity = activityMapper.selectLatestFollowContactIdByEventAndOpenId(eventId, openId);
            if (StringUtils.isNotBlank(contactFromActivity))
            {
                return OutreachRegistrationIdentityResolution.contactKnown(contactFromActivity, "activity_follow");
            }
        }

        if (StringUtils.isNotBlank(openId))
        {
            String contactFromFan = resolveContactFromFan(openId, appId);
            if (StringUtils.isNotBlank(contactFromFan))
            {
                return OutreachRegistrationIdentityResolution.contactKnown(contactFromFan, "mp_fan");
            }

            String unionId = resolveUnionId(openId, appId);
            if (StringUtils.isNotBlank(unionId))
            {
                String contactFromUnion = identityService.findContactByIdentity("wechat_unionid", unionId);
                if (StringUtils.isNotBlank(contactFromUnion))
                {
                    return OutreachRegistrationIdentityResolution.contactKnown(contactFromUnion, "unionid");
                }
            }

            String contactFromOpenId = identityService.findContactByIdentity("wechat_mp_openid", openId);
            if (StringUtils.isNotBlank(contactFromOpenId))
            {
                return OutreachRegistrationIdentityResolution.contactKnown(contactFromOpenId, "mp_openid");
            }
        }

        if (isMergeOnDuplicatePhone(tenantId) && StringUtils.isNotBlank(mobilePhone))
        {
            String normalized = PhoneNormalizer.normalize(mobilePhone);
            String phone = StringUtils.isNotBlank(normalized) ? normalized : mobilePhone.trim();
            String contactFromPhone = identityService.findContactByIdentity("mobile_phone", phone);
            if (StringUtils.isNotBlank(contactFromPhone))
            {
                return OutreachRegistrationIdentityResolution.contactKnown(contactFromPhone, "mobile");
            }
        }

        CrmLead existingLead = leadResolveService.resolveExistingLead(eventId, mobilePhone, openId, appId);
        if (existingLead != null)
        {
            return OutreachRegistrationIdentityResolution.leadUpsert(existingLead);
        }

        return OutreachRegistrationIdentityResolution.createNewLead();
    }

    private String resolveContactFromFan(String openId, String appId)
    {
        if (StringUtils.isBlank(appId))
        {
            return null;
        }
        CrmWechatMpFan fan = fanMapper.selectByOpenId(appId, openId);
        if (fan == null)
        {
            return null;
        }
        if (StringUtils.isNotBlank(fan.getContactId()))
        {
            return fan.getContactId();
        }
        return mpIdentityResolutionSupport.resolveFinalContactId(openId, fan.getUnionId(), null);
    }

    private String resolveUnionId(String openId, String appId)
    {
        if (StringUtils.isBlank(appId))
        {
            return null;
        }
        CrmWechatMpFan fan = fanMapper.selectByOpenId(appId, openId);
        return fan != null ? fan.getUnionId() : null;
    }

    private boolean isMergeOnDuplicatePhone(String tenantId)
    {
        LeadConvertConfigVo config = leadConvertConfigService.getConfig(tenantId);
        return config != null && Boolean.TRUE.equals(config.getMergeOnDuplicatePhone());
    }
}
