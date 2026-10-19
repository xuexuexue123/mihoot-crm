package com.ruoyi.crm.wecom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.ruoyi.crm.service.ICrmCustomerIdentityService;

/**
 * 企微 external_userid / unionid Identity Resolution 与 bind 共用逻辑。
 */
@Component
public class WecomIdentityResolutionSupport
{
    private static final Logger log = LoggerFactory.getLogger(WecomIdentityResolutionSupport.class);

    public static final String SOURCE_WECOM_CALLBACK = "wecom_callback";

    public static final String SOURCE_LEAD_CONVERT = "lead_convert";

    @Autowired
    private ICrmCustomerIdentityService identityService;

    @Autowired
    private UnmatchedActivityBackfillSupport unmatchedActivityBackfillSupport;

    /**
     * unionid 优先，其次 wecom_external_userid。
     */
    public String resolveContactId(String externalUserid, String unionId)
    {
        if (StringUtils.hasText(unionId))
        {
            String viaUnion = identityService.findContactByIdentity("wechat_unionid", unionId.trim());
            if (viaUnion != null)
            {
                return viaUnion;
            }
        }
        if (StringUtils.hasText(externalUserid))
        {
            return identityService.findContactByIdentity("wecom_external_userid", externalUserid.trim());
        }
        return null;
    }

    /**
     * 将企微渠道身份绑定到 Contact（幂等，冲突仅 warn）。
     */
    public void bindWecomIdentities(String externalUserid, String unionId, String contactId)
    {
        bindWecomIdentities(externalUserid, unionId, contactId, SOURCE_WECOM_CALLBACK);
    }

    /**
     * 将企微渠道身份绑定到 Contact，并记录指定 source。
     */
    public void bindWecomIdentities(String externalUserid, String unionId, String contactId, String source)
    {
        if (!StringUtils.hasText(contactId))
        {
            return;
        }
        bindExternalUseridIfNeeded(externalUserid, contactId, source);
        bindUnionIdIfNeeded(unionId, contactId, source);
    }

    public void bindExternalUseridIfNeeded(String externalUserid, String contactId)
    {
        bindExternalUseridIfNeeded(externalUserid, contactId, SOURCE_WECOM_CALLBACK);
    }

    public void bindExternalUseridIfNeeded(String externalUserid, String contactId, String source)
    {
        if (!StringUtils.hasText(externalUserid) || !StringUtils.hasText(contactId))
        {
            return;
        }
        if (identityService.findContactByIdentity("wecom_external_userid", externalUserid.trim()) != null)
        {
            return;
        }
        String effectiveSource = StringUtils.hasText(source) ? source : SOURCE_WECOM_CALLBACK;
        try
        {
            identityService.bindIdentity(contactId, "wecom_external_userid", externalUserid.trim(),
                    effectiveSource);
        }
        catch (Exception e)
        {
            log.warn("Failed to bind wecom_external_userid {} to contact {}: {}",
                    externalUserid, contactId, e.getMessage());
            identityService.recordIdentityConflict("wecom_external_userid", externalUserid.trim(),
                    contactId, effectiveSource);
        }
    }

    public void bindUnionIdIfNeeded(String unionId, String contactId)
    {
        bindUnionIdIfNeeded(unionId, contactId, SOURCE_WECOM_CALLBACK);
    }

    public void bindUnionIdIfNeeded(String unionId, String contactId, String source)
    {
        if (!StringUtils.hasText(unionId) || !StringUtils.hasText(contactId))
        {
            return;
        }
        String effectiveSource = StringUtils.hasText(source) ? source : SOURCE_WECOM_CALLBACK;
        try
        {
            identityService.bindIdentity(contactId, "wechat_unionid", unionId.trim(), effectiveSource);
            unmatchedActivityBackfillSupport.backfillForUnionId(unionId.trim(), contactId.trim());
        }
        catch (Exception e)
        {
            log.warn("UnionID {} already bound to another contact, skip: {}", unionId, e.getMessage());
            identityService.recordIdentityConflict("wechat_unionid", unionId.trim(),
                    contactId, effectiveSource);
        }
    }
}
