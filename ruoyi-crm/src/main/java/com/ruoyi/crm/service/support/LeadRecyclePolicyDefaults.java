package com.ruoyi.crm.service.support;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * 线索回收机制种子默认与允许枚举。
 */
public final class LeadRecyclePolicyDefaults
{
    public static final int IDLE_TO_POOL_HOURS = 72;
    public static final int MAX_HOLD_COUNT = 100;
    public static final boolean RECYCLE_ENABLED = true;
    public static final String MATCH_MODE_ANY = "any";
    public static final String OWNER_KEEP_LEAD = "keep_lead_owner";
    public static final String OWNER_CURRENT_USER = "current_user";
    public static final String OWNER_SPECIFIED_USER = "specified_user";

    public static final List<String> DEFAULT_LEAD_MATCH_FIELDS =
            Collections.unmodifiableList(Arrays.asList("mobilePhone", "wecomExternalUserid"));
    public static final List<String> DEFAULT_CONTACT_MATCH_FIELDS =
            Collections.unmodifiableList(Arrays.asList("mobilePhone", "wecomExternalUserid"));

    public static final Set<String> ALLOWED_LEAD_MATCH_FIELDS = Collections.unmodifiableSet(
            new LinkedHashSet<>(Arrays.asList(
                    "mobilePhone", "wecomExternalUserid", "workPhone", "workEmail", "wechat", "wechat_id")));
    public static final Set<String> ALLOWED_CONTACT_MATCH_FIELDS = Collections.unmodifiableSet(
            new LinkedHashSet<>(Arrays.asList(
                    "mobilePhone", "wecomExternalUserid", "workEmail", "personalEmail",
                    "wechat_unionid", "wechat_mp_openid")));
    public static final Set<String> ALLOWED_OWNER_MODES = Collections.unmodifiableSet(
            new LinkedHashSet<>(Arrays.asList(OWNER_KEEP_LEAD, OWNER_CURRENT_USER, OWNER_SPECIFIED_USER)));

    public static final String REASON_HOLD_LIMIT = "hold_limit_exceeded";
    public static final String REASON_CLAIM_DAILY_LIMIT = "claim_daily_limit";

    private LeadRecyclePolicyDefaults()
    {
    }

    public static int resolveIdleHours(Integer idleToPoolHours, Integer protectDays)
    {
        if (idleToPoolHours != null && idleToPoolHours > 0)
        {
            return idleToPoolHours;
        }
        if (protectDays != null && protectDays > 0)
        {
            return protectDays * 24;
        }
        return IDLE_TO_POOL_HOURS;
    }

    public static int resolveMaxHold(Integer maxHoldCount)
    {
        if (maxHoldCount != null && maxHoldCount > 0)
        {
            return maxHoldCount;
        }
        return MAX_HOLD_COUNT;
    }

    public static int protectDaysFromHours(int hours)
    {
        if (hours <= 0)
        {
            return 1;
        }
        return (hours + 23) / 24;
    }
}
