package com.ruoyi.crm.whatsapp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.ruoyi.crm.service.ICrmCustomerIdentityService;
import com.ruoyi.crm.service.support.IdentityNormalizer;

/**
 * WhatsApp wa_id / 手机号 Identity Resolution 与 bind 共用逻辑。
 * 对齐 docs/whatsapp-contact-resolve-backend-plan.md 与企微 {@code WecomIdentityResolutionSupport}。
 */
@Component
public class WhatsappIdentityResolutionSupport
{
    private static final Logger log = LoggerFactory.getLogger(WhatsappIdentityResolutionSupport.class);

    public static final String SOURCE_WHATSAPP_WEBHOOK = "whatsapp_webhook";

    public static final String SOURCE_WHATSAPP_SIDEBAR = "whatsapp_sidebar";

    @Autowired
    private ICrmCustomerIdentityService identityService;

    /**
     * wa_id → contactId；未命中返回 null。
     * 命中手机号时内部补绑 whatsapp_wa_id；冲突不抛出，以权威 wa_id 绑定为准。
     */
    public String resolveContactId(String waId)
    {
        if (!StringUtils.hasText(waId))
        {
            return null;
        }
        String trimmed = waId.trim();

        String viaWa = identityService.findContactByIdentity(
                IdentityNormalizer.TYPE_WHATSAPP_WA_ID, trimmed);
        if (viaWa != null)
        {
            return viaWa;
        }

        String phone = IdentityNormalizer.normalizeMobile(trimmed);
        if (!StringUtils.hasText(phone))
        {
            return null;
        }

        String viaPhone = identityService.findContactByIdentity(
                IdentityNormalizer.TYPE_WHATSAPP_PHONE, phone);
        if (viaPhone == null)
        {
            viaPhone = identityService.findContactByIdentity(
                    IdentityNormalizer.TYPE_MOBILE_PHONE, phone);
        }
        if (viaPhone == null)
        {
            return null;
        }

        bindWaIdIfNeeded(trimmed, viaPhone, SOURCE_WHATSAPP_WEBHOOK);

        // bind 冲突时以已存在的 wa_id 绑定为准（可能是另一 Contact）
        String afterBind = identityService.findContactByIdentity(
                IdentityNormalizer.TYPE_WHATSAPP_WA_ID, trimmed);
        if (afterBind != null)
        {
            return afterBind;
        }
        return viaPhone;
    }

    /**
     * 将 whatsapp_wa_id 绑定到 Contact（幂等；冲突仅 warn + 记冲突表）。
     */
    public void bindWaIdIfNeeded(String waId, String contactId)
    {
        bindWaIdIfNeeded(waId, contactId, SOURCE_WHATSAPP_WEBHOOK);
    }

    public void bindWaIdIfNeeded(String waId, String contactId, String source)
    {
        if (!StringUtils.hasText(waId) || !StringUtils.hasText(contactId))
        {
            return;
        }
        String trimmed = waId.trim();
        if (identityService.findContactByIdentity(IdentityNormalizer.TYPE_WHATSAPP_WA_ID, trimmed) != null)
        {
            return;
        }
        String effectiveSource = StringUtils.hasText(source) ? source : SOURCE_WHATSAPP_WEBHOOK;
        try
        {
            identityService.bindIdentity(contactId, IdentityNormalizer.TYPE_WHATSAPP_WA_ID, trimmed,
                    effectiveSource);
        }
        catch (Exception e)
        {
            log.warn("Failed to bind whatsapp_wa_id {} to contact {}: {}",
                    trimmed, contactId, e.getMessage());
            identityService.recordIdentityConflict(IdentityNormalizer.TYPE_WHATSAPP_WA_ID, trimmed,
                    contactId, effectiveSource);
        }
    }
}
