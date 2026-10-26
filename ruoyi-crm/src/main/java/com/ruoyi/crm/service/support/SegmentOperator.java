package com.ruoyi.crm.service.support;

/**
 * 分群规则算子
 */
public final class SegmentOperator
{
    public static final String EQ = "=";
    public static final String NE = "!=";
    public static final String GT = ">";
    public static final String GTE = ">=";
    public static final String LT = "<";
    public static final String LTE = "<=";
    public static final String BETWEEN = "between";
    public static final String IS_NULL = "is_null";
    public static final String IS_NOT_NULL = "is_not_null";
    public static final String IN = "in";
    public static final String NOT_IN = "not_in";
    public static final String CONTAINS = "contains";
    public static final String CONTAINS_ALL = "contains_all";
    public static final String WITHIN_LAST_DAYS = "within_last_days";
    public static final String BEFORE_DAYS_AGO = "before_days_ago";
    public static final String EXISTS = "exists";
    public static final String NOT_EXISTS = "not_exists";

    private SegmentOperator()
    {
    }
}
