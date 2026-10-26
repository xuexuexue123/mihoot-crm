package com.ruoyi.crm.service.support;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;

/**
 * 分群规则 AST → 参数化 SQL WHERE 编译器
 */
@Component
public class SegmentRuleCompiler
{
    @Autowired
    private WechatMpMenuClickSegmentValidator menuClickSegmentValidator;

    @Autowired
    private WechatMpMenuClickSegmentResolver menuClickSegmentResolver;

    @Autowired
    private TagSegmentResolver tagSegmentResolver;

    public CompiledSegmentQuery compile(Object ruleJson)
    {
        SegmentRuleValidator.validate(ruleJson);
        menuClickSegmentValidator.validateRule(ruleJson);
        JSONObject node = SegmentRuleValidator.toJsonObject(ruleJson);
        SegmentSqlParamBinder binder = new SegmentSqlParamBinder();
        String predicate = compileNode(node, binder);
        if (StringUtils.isBlank(predicate))
        {
            predicate = "TRUE";
        }
        return new CompiledSegmentQuery(predicate, binder.getParams());
    }

    private String compileNode(JSONObject node, SegmentSqlParamBinder binder)
    {
        if (node == null || node.isEmpty())
        {
            return "TRUE";
        }
        if (node.containsKey("op"))
        {
            return compileBranch(node, binder);
        }
        return compileLeaf(node, binder);
    }

    private String compileBranch(JSONObject node, SegmentSqlParamBinder binder)
    {
        String op = node.getString("op");
        JSONArray children = node.getJSONArray("children");
        if (children == null || children.isEmpty())
        {
            return "TRUE";
        }
        if ("NOT".equalsIgnoreCase(op))
        {
            String child = compileNode(children.getJSONObject(0), binder);
            return "NOT (" + child + ")";
        }
        String joiner = "OR".equalsIgnoreCase(op) ? " OR " : " AND ";
        List<String> parts = new ArrayList<>();
        for (int i = 0; i < children.size(); i++)
        {
            parts.add("(" + compileNode(children.getJSONObject(i), binder) + ")");
        }
        return String.join(joiner, parts);
    }

    private String compileLeaf(JSONObject node, SegmentSqlParamBinder binder)
    {
        String source = node.getString("source");
        String field = node.getString("field");
        String operator = node.getString("operator");
        Object value = node.get("value");

        SegmentFieldDefinition definition = SegmentFieldRegistry.get(source, field);
        if (definition == null)
        {
            throw new ServiceException("非法字段");
        }
        if (!definition.getOperators().contains(operator))
        {
            throw new ServiceException("非法算子");
        }

        if (SegmentFieldRegistry.TYPE_WECHAT_MP_MENU.equals(definition.getFieldType()))
        {
            return menuClickSegmentResolver.compile(operator, value, binder);
        }
        if (SegmentFieldRegistry.SOURCE_TAG.equals(source))
        {
            return tagSegmentResolver.compile(operator, value, binder);
        }
        if (SegmentFieldRegistry.SOURCE_REACHABILITY.equals(source))
        {
            return compileReachability(definition, operator, binder);
        }
        if (SegmentFieldRegistry.SOURCE_ATTRIBUTE.equals(source))
        {
            String inner = compileColumnPredicate("c." + definition.getPhysicalColumn(), definition.getFieldType(),
                    operator, value, binder);
            return "EXISTS (SELECT 1 FROM crm_contact c WHERE c.contact_id = m.contact_id AND " + inner + ")";
        }
        String column = "m." + definition.getPhysicalColumn();
        return compileColumnPredicate(column, definition.getFieldType(), operator, value, binder);
    }

    private String compileReachability(SegmentFieldDefinition definition, String operator, SegmentSqlParamBinder binder)
    {
        String existsSql = "EXISTS (SELECT 1 FROM crm_customer_identity i "
                + "WHERE i.contact_id = m.contact_id AND i.identity_type = " + binder.bind(definition.getField()) + ")";
        if (SegmentOperator.NOT_EXISTS.equals(operator))
        {
            return "NOT " + existsSql;
        }
        if (SegmentOperator.EXISTS.equals(operator))
        {
            return existsSql;
        }
        throw new ServiceException("非法算子");
    }

    private String compileColumnPredicate(String column, String fieldType, String operator, Object value,
            SegmentSqlParamBinder binder)
    {
        switch (operator)
        {
            case SegmentOperator.IS_NULL:
                return column + " IS NULL";
            case SegmentOperator.IS_NOT_NULL:
                return column + " IS NOT NULL";
            case SegmentOperator.IN:
                return column + " IN (" + binder.bindList(asObjectList(value)) + ")";
            case SegmentOperator.NOT_IN:
                return column + " NOT IN (" + binder.bindList(asObjectList(value)) + ")";
            case SegmentOperator.BETWEEN:
                List<Object> range = asObjectList(value);
                if (range.size() != 2)
                {
                    throw new ServiceException("between 需要两个值");
                }
                return column + " BETWEEN " + binder.bind(convertValue(range.get(0), fieldType))
                        + " AND " + binder.bind(convertValue(range.get(1), fieldType));
            case SegmentOperator.CONTAINS:
                return column + " ILIKE " + binder.bind("%" + String.valueOf(value) + "%");
            case SegmentOperator.WITHIN_LAST_DAYS:
                return column + " >= now() - (" + binder.bind(asInteger(value)) + " || ' days')::interval";
            case SegmentOperator.BEFORE_DAYS_AGO:
                return column + " < now() - (" + binder.bind(asInteger(value)) + " || ' days')::interval";
            case SegmentOperator.EQ:
                return column + " = " + binder.bind(convertValue(value, fieldType));
            case SegmentOperator.NE:
                return column + " <> " + binder.bind(convertValue(value, fieldType));
            case SegmentOperator.GT:
                return column + " > " + binder.bind(convertValue(value, fieldType));
            case SegmentOperator.GTE:
                return column + " >= " + binder.bind(convertValue(value, fieldType));
            case SegmentOperator.LT:
                return column + " < " + binder.bind(convertValue(value, fieldType));
            case SegmentOperator.LTE:
                return column + " <= " + binder.bind(convertValue(value, fieldType));
            default:
                throw new ServiceException("非法算子");
        }
    }

    private Object convertValue(Object value, String fieldType)
    {
        if (value == null)
        {
            return null;
        }
        if (SegmentFieldRegistry.TYPE_NUMBER.equals(fieldType))
        {
            if (value instanceof Number)
            {
                return value;
            }
            return new BigDecimal(String.valueOf(value));
        }
        return String.valueOf(value);
    }

    private Integer asInteger(Object value)
    {
        if (value instanceof Number)
        {
            return ((Number) value).intValue();
        }
        return Integer.valueOf(String.valueOf(value));
    }

    private List<Object> asObjectList(Object value)
    {
        if (value == null)
        {
            throw new ServiceException("缺少条件值");
        }
        List<Object> items = new ArrayList<>();
        if (value instanceof JSONArray)
        {
            JSONArray array = (JSONArray) value;
            for (int i = 0; i < array.size(); i++)
            {
                items.add(array.get(i));
            }
            return items;
        }
        if (value instanceof List)
        {
            items.addAll((List<?>) value);
            return items;
        }
        if (value instanceof Object[])
        {
            Object[] array = (Object[]) value;
            for (Object item : array)
            {
                items.add(item);
            }
            return items;
        }
        throw new ServiceException("条件值格式错误");
    }
}
