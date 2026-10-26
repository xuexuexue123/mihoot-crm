package com.ruoyi.crm.webchat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.ruoyi.crm.service.ICrmCustomerIdentityService;
import com.ruoyi.crm.service.support.IdentityNormalizer;

/**
 * Web Chat visitor_id Identity Resolution 与 bind 共用逻辑。
 */
@Component
public class WebchatIdentityResolutionSupport
{
    private static final Logger log = LoggerFactory.getLogger(WebchatIdentityResolutionSupport.class);

    public static final String SOURCE_WEBCHAT_WIDGET = "webchat_widget";

    public static final String SOURCE_WEBCHAT_INBOX = "webchat_inbox";

    @Autowired
    private ICrmCustomerIdentityService identityService;

    public String resolveContactId(String visitorId)
    {
        if (!StringUtils.hasText(visitorId))
        {
            return null;
        }
        String trimmed = visitorId.trim();
        return identityService.findContactByIdentity(
                IdentityNormalizer.TYPE_WEBCHAT_VISITOR_ID, trimmed);
    }

    public void bindVisitorIfNeeded(String visitorId, String contactId)
    {
        bindVisitorIfNeeded(visitorId, contactId, SOURCE_WEBCHAT_WIDGET);
    }

    public void bindVisitorIfNeeded(String visitorId, String contactId, String source)
    {
        if (!StringUtils.hasText(visitorId) || !StringUtils.hasText(contactId))
        {
            return;
        }
        String trimmed = visitorId.trim();
        if (identityService.findContactByIdentity(IdentityNormalizer.TYPE_WEBCHAT_VISITOR_ID, trimmed) != null)
        {
            return;
        }
        String effectiveSource = StringUtils.hasText(source) ? source : SOURCE_WEBCHAT_WIDGET;
        try
        {
            identityService.bindIdentity(contactId, IdentityNormalizer.TYPE_WEBCHAT_VISITOR_ID, trimmed,
                    effectiveSource);
        }
        catch (Exception e)
        {
            log.warn("Failed to bind webchat_visitor_id {} to contact {}: {}",
                    trimmed, contactId, e.getMessage());
            identityService.recordIdentityConflict(IdentityNormalizer.TYPE_WEBCHAT_VISITOR_ID, trimmed,
                    contactId, effectiveSource);
        }
    }
}
