package com.ruoyi.crm.service.support;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Data Table 常量
 */
public final class DataTableConstants
{
    public static final String COL_ID = "_id";
    public static final String COL_CREATED_AT = "_createdAt";
    public static final String COL_UPDATED_AT = "_updatedAt";

    public static final String TYPE_STRING = "STRING";
    public static final String TYPE_NUMBER = "NUMBER";
    public static final String TYPE_BOOLEAN = "BOOLEAN";
    public static final String TYPE_DATE = "DATE";
    public static final String TYPE_JSON = "JSON";
    public static final String TYPE_MULTI_SELECT = "MULTI_SELECT";
    public static final String TYPE_SELECT = "SELECT";
    public static final String TYPE_URL = "URL";

    public static final String COMBINE_AND = "AND";
    public static final String COMBINE_OR = "OR";

    public static final int DEFAULT_LIST_LIMIT = 100;
    public static final int MAX_LIST_LIMIT = 1000;

    private static final Set<String> SYSTEM_COLUMNS = Collections.unmodifiableSet(new HashSet<>(
            Arrays.asList(COL_ID, COL_CREATED_AT, COL_UPDATED_AT)));

    private DataTableConstants()
    {
    }

    public static boolean isSystemColumn(String name)
    {
        return name != null && SYSTEM_COLUMNS.contains(name);
    }

    public static Set<String> systemColumnNames()
    {
        return SYSTEM_COLUMNS;
    }
}
