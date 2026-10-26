package com.ruoyi.crm.service.support;

import com.ruoyi.common.utils.StringUtils;

/**
 * 线索可见性与 status 归一
 */
public final class AudienceLeadVisibility
{
    private AudienceLeadVisibility()
    {
    }

    public static String normalizeStatus(String status)
    {
        if (status == null)
        {
            return "";
        }
        return status.trim().toLowerCase();
    }

    public static boolean isInitialStatus(String status)
    {
        return "initial".equals(normalizeStatus(status));
    }

    /** 与 {@code CrmLeadConvertServiceImpl} 转化回写一致 */
    public static final String STATUS_CONVERTED = "Converted";

    /** 公海 */
    public static final String STATUS_OPEN = "Open";

    /** 销售线索池 pendinglist 可见状态 */
    public static final String STATUS_ASSIGNED = "Assigned";

    public static boolean isOpenStatus(String status)
    {
        return "open".equals(normalizeStatus(status));
    }

    public static boolean isAssignedStatus(String status)
    {
        return "assigned".equals(normalizeStatus(status));
    }

    public static boolean isConvertedStatus(String status)
    {
        return "converted".equals(normalizeStatus(status));
    }

    /** Open + Assigned，排除 Converted / Initial */
    public static boolean isPendingStatus(String status)
    {
        return isOpenStatus(status) || isAssignedStatus(status);
    }

    /**
     * 镜像 selectCrmLeadPendingList：status IN (Open, Assigned)
     */
    public static boolean isVisibleInPendingList(String status)
    {
        return isPendingStatus(status);
    }
}
