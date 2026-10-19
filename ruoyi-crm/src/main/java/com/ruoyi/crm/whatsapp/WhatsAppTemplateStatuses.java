package com.ruoyi.crm.whatsapp;

/**
 * WhatsApp 模板 status 归一化与校验（Meta 多为 APPROVED，本地统一小写）。
 */
public final class WhatsAppTemplateStatuses
{
    private WhatsAppTemplateStatuses()
    {
    }

    public static String normalize(String status)
    {
        return status == null ? null : status.trim().toLowerCase();
    }

    public static boolean isApproved(String status)
    {
        return status != null && "approved".equalsIgnoreCase(status.trim());
    }
}
