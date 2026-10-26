package com.ruoyi.crm.service.support;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 编译后的分群查询（参数化 SQL）
 */
public class CompiledSegmentQuery
{
    private final String whereClause;
    private final List<Object> params;

    public CompiledSegmentQuery(String whereClause, List<Object> params)
    {
        this.whereClause = whereClause;
        this.params = params == null ? Collections.emptyList() : Collections.unmodifiableList(new ArrayList<>(params));
    }

    public String getWhereClause()
    {
        return whereClause;
    }

    public List<Object> getParams()
    {
        return params;
    }

    public String countSql()
    {
        return "SELECT COUNT(*) FROM customer_metrics m WHERE " + whereClause;
    }

    public String membersSql()
    {
        return "SELECT m.contact_id, c.name, c.mobile_phone "
                + "FROM customer_metrics m "
                + "LEFT JOIN crm_contact c ON c.contact_id = m.contact_id "
                + "WHERE " + whereClause + " "
                + "ORDER BY m.contact_id";
    }
}
