package com.ruoyi.crm.service.support;

/**
 * 活动报名 lead 解析（自动化 skip 与 H5 upsert 共用）
 */
public final class OutreachLeadResolveSupport
{
    private OutreachLeadResolveSupport()
    {
    }

    public static String normalizeLeadStatus(String status)
    {
        if (status == null)
        {
            return "";
        }
        return status.trim();
    }

    public static boolean isInitialLeadStatus(String status)
    {
        String normalized = normalizeLeadStatus(status);
        return "Initial".equalsIgnoreCase(normalized) || "initial".equals(normalized);
    }

    public static boolean isTerminalLeadStatus(String status)
    {
        if (isInitialLeadStatus(status))
        {
            return false;
        }
        String normalized = normalizeLeadStatus(status);
        return !normalized.isEmpty();
    }
}
