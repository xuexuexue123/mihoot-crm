package com.ruoyi.crm.service.support;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.ruoyi.common.exception.ServiceException;

/**
 * 分群 SQL 编译参数绑定器
 */
public class SegmentSqlParamBinder
{
    private final List<Object> params = new ArrayList<>();

    public String bind(Object value)
    {
        params.add(value);
        return "?";
    }

    public String bindList(List<Object> values)
    {
        if (values == null || values.isEmpty())
        {
            throw new ServiceException("缺少条件值");
        }
        List<String> placeholders = new ArrayList<>();
        for (Object value : values)
        {
            placeholders.add(bind(value));
        }
        return String.join(", ", placeholders);
    }

    public List<Object> getParams()
    {
        return Collections.unmodifiableList(params);
    }
}
