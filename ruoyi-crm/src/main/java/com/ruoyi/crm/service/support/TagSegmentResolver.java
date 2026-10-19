package com.ruoyi.crm.service.support;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.alibaba.fastjson2.JSONArray;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;

/**
 * 分群标签条件编译：按 crm_tag_ids(c.tag::text) 匹配 dict_value
 */
@Component
public class TagSegmentResolver
{
    public static final String FIELD_CRM_TAG = "crmTag";

    public String compile(String operator, Object value, SegmentSqlParamBinder binder)
    {
        String idsExpr = "crm_tag_ids(c.tag::text)";
        switch (operator)
        {
            case SegmentOperator.IS_NULL:
                return wrapExists("cardinality(" + idsExpr + ") = 0");
            case SegmentOperator.IS_NOT_NULL:
                return wrapExists("cardinality(" + idsExpr + ") > 0");
            case SegmentOperator.IN:
                return wrapExists(idsExpr + " && ARRAY[" + bindStringArray(value, binder) + "]::text[]");
            case SegmentOperator.NOT_IN:
                return "NOT (" + wrapExists(idsExpr + " && ARRAY[" + bindStringArray(value, binder) + "]::text[]") + ")";
            case SegmentOperator.CONTAINS_ALL:
                return wrapExists(idsExpr + " @> ARRAY[" + bindStringArray(value, binder) + "]::text[]");
            default:
                throw new ServiceException("非法算子", HttpStatus.BAD_REQUEST);
        }
    }

    private String wrapExists(String predicate)
    {
        return "EXISTS (SELECT 1 FROM crm_contact c WHERE c.contact_id = m.contact_id AND " + predicate + ")";
    }

    private String bindStringArray(Object value, SegmentSqlParamBinder binder)
    {
        List<String> values = asStringList(value);
        if (values.isEmpty())
        {
            throw new ServiceException("SEGMENT_INVALID_RULE: tag 条件 value 须为非空字符串数组",
                    HttpStatus.BAD_REQUEST);
        }
        List<String> placeholders = new ArrayList<>(values.size());
        for (String item : values)
        {
            placeholders.add(binder.bind(item));
        }
        return String.join(", ", placeholders);
    }

    private List<String> asStringList(Object value)
    {
        if (value == null)
        {
            throw new ServiceException("SEGMENT_INVALID_RULE: 缺少 tag 条件值", HttpStatus.BAD_REQUEST);
        }
        List<String> items = new ArrayList<>();
        if (value instanceof JSONArray)
        {
            JSONArray array = (JSONArray) value;
            for (int i = 0; i < array.size(); i++)
            {
                Object item = array.get(i);
                if (!(item instanceof String) || StringUtils.isBlank((String) item))
                {
                    throw new ServiceException("SEGMENT_INVALID_RULE: tag 条件 value 元素须为非空字符串",
                            HttpStatus.BAD_REQUEST);
                }
                items.add(((String) item).trim());
            }
            return items;
        }
        if (value instanceof List)
        {
            for (Object item : (List<?>) value)
            {
                if (!(item instanceof String) || StringUtils.isBlank((String) item))
                {
                    throw new ServiceException("SEGMENT_INVALID_RULE: tag 条件 value 元素须为非空字符串",
                            HttpStatus.BAD_REQUEST);
                }
                items.add(((String) item).trim());
            }
            return items;
        }
        if (value instanceof String[])
        {
            for (String item : (String[]) value)
            {
                if (StringUtils.isBlank(item))
                {
                    throw new ServiceException("SEGMENT_INVALID_RULE: tag 条件 value 元素须为非空字符串",
                            HttpStatus.BAD_REQUEST);
                }
                items.add(item.trim());
            }
            return items;
        }
        throw new ServiceException("SEGMENT_INVALID_RULE: tag 条件 value 须为字符串数组",
                HttpStatus.BAD_REQUEST);
    }
}
