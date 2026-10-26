package com.ruoyi.crm.service.support;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.exception.ServiceException;

/**
 * 分群规则 AST 校验（深度与节点数限制）
 */
public final class SegmentRuleValidator
{
    public static final int MAX_DEPTH = 6;
    public static final int MAX_NODES = 100;

    private SegmentRuleValidator()
    {
    }

    public static void validate(Object ruleJson)
    {
        NodeCounter counter = new NodeCounter();
        validateNode(toJsonObject(ruleJson), 1, counter);
        if (counter.count > MAX_NODES)
        {
            throw new ServiceException("规则节点数超过上限 " + MAX_NODES);
        }
    }

    private static void validateNode(JSONObject node, int depth, NodeCounter counter)
    {
        if (node == null || node.isEmpty())
        {
            return;
        }
        counter.count++;
        if (depth > MAX_DEPTH)
        {
            throw new ServiceException("规则深度超过上限 " + MAX_DEPTH);
        }
        if (node.containsKey("op"))
        {
            String op = node.getString("op");
            JSONArray children = node.getJSONArray("children");
            if (children == null || children.isEmpty())
            {
                throw new ServiceException("分支节点缺少 children");
            }
            if ("NOT".equalsIgnoreCase(op) && children.size() != 1)
            {
                throw new ServiceException("NOT 节点只能有一个子节点");
            }
            for (int i = 0; i < children.size(); i++)
            {
                validateNode(children.getJSONObject(i), depth + 1, counter);
            }
            return;
        }
        String source = node.getString("source");
        String field = node.getString("field");
        String operator = node.getString("operator");
        if (source == null || field == null || operator == null)
        {
            throw new ServiceException("条件节点缺少 source/field/operator");
        }
        SegmentFieldDefinition definition = SegmentFieldRegistry.get(source, field);
        if (definition == null)
        {
            throw new ServiceException("非法字段");
        }
        if (!definition.getOperators().contains(operator))
        {
            throw new ServiceException("非法算子");
        }
        if (SegmentFieldRegistry.SOURCE_METRIC.equals(source)
                && SegmentFieldRegistry.FIELD_WECHAT_MP_MENU_ARTICLE_VIEWED.equals(field))
        {
            WechatMpMenuClickSegmentValidator.parseMenuRef(node.get("value"));
        }
    }

    public static JSONObject toJsonObject(Object ruleJson)
    {
        if (ruleJson == null)
        {
            return null;
        }
        if (ruleJson instanceof JSONObject)
        {
            return (JSONObject) ruleJson;
        }
        if (ruleJson instanceof String)
        {
            String text = ((String) ruleJson).trim();
            if (text.isEmpty())
            {
                return null;
            }
            return JSONObject.parseObject(text);
        }
        return JSON.parseObject(JSON.toJSONString(ruleJson));
    }

    private static final class NodeCounter
    {
        private int count;
    }
}
