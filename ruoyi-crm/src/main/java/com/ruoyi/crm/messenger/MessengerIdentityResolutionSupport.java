package com.ruoyi.crm.messenger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.ruoyi.crm.service.ICrmCustomerIdentityService;
import com.ruoyi.crm.service.support.IdentityNormalizer;

/**
 * Messenger PSID Identity Resolution 与 bind 共用逻辑。
 */
@Component
public class MessengerIdentityResolutionSupport
{
    private static final Logger log = LoggerFactory.getLogger(MessengerIdentityResolutionSupport.class);

    public static final String SOURCE_MESSENGER_WEBHOOK = "messenger_webhook";

    public static final String SOURCE_MESSENGER_SIDEBAR = "messenger_sidebar";

    @Autowired
    private ICrmCustomerIdentityService identityService;

    /**
     * psid → contactId；未命中返回 null。
     */
    public String resolveContactId(String psid)
    {
        if (!StringUtils.hasText(psid))
        {
            return null;
        }
        String trimmed = psid.trim();
        return identityService.findContactByIdentity(
                IdentityNormalizer.TYPE_MESSENGER_PSID, trimmed);
    }

    /**
     * 将 messenger_psid 绑定到 Contact（幂等；冲突仅 warn + 记冲突表）。
     */
    public void bindPsidIfNeeded(String psid, String contactId)
    {
        bindPsidIfNeeded(psid, contactId, SOURCE_MESSENGER_WEBHOOK);
    }

    public void bindPsidIfNeeded(String psid, String contactId, String source)
    {
        if (!StringUtils.hasText(psid) || !StringUtils.hasText(contactId))
        {
            return;
        }
        String trimmed = psid.trim();
        if (identityService.findContactByIdentity(IdentityNormalizer.TYPE_MESSENGER_PSID, trimmed) != null)
        {
            return;
        }
        String effectiveSource = StringUtils.hasText(source) ? source : SOURCE_MESSENGER_WEBHOOK;
        try
        {
            identityService.bindIdentity(contactId, IdentityNormalizer.TYPE_MESSENGER_PSID, trimmed,
                    effectiveSource);
        }
        catch (Exception e)
        {
            log.warn("Failed to bind messenger_psid {} to contact {}: {}",
                    trimmed, contactId, e.getMessage());
            identityService.recordIdentityConflict(IdentityNormalizer.TYPE_MESSENGER_PSID, trimmed,
                    contactId, effectiveSource);
        }
    }
}
