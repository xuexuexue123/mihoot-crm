package com.ruoyi.crm.wechatmp.audience;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.crm.service.support.SegmentOperator;
import com.ruoyi.crm.service.support.SegmentRuleValidator;
import com.ruoyi.crm.wechatmp.audience.dto.FanWechatMpArticleRef;
import com.ruoyi.crm.wechatmp.audience.dto.FanWechatMpMenuRef;
import com.ruoyi.crm.wechatmp.audience.dto.FanWechatMpQrcodeRef;
import com.ruoyi.crm.wechatmp.audience.dto.FanWechatMpTagRef;
import com.ruoyi.crm.wechatmp.mapper.CrmWechatMpConfigMapper;

/**
 * 粉丝受众 filterJson AST 校验
 */
@Component
public class FanAudienceRuleValidator
{
    @Autowired
    private CrmWechatMpConfigMapper wechatMpConfigMapper;

    public void validateAppId(String appId)
    {
        if (!StringUtils.hasText(appId))
        {
            throw invalid("缺少 appId");
        }
        if (wechatMpConfigMapper.selectByAppId(appId.trim()) == null)
        {
            throw new ServiceException("appId 不属于当前租户", HttpStatus.FORBIDDEN);
        }
    }

    public void validateFilterJson(Object filterJson)
    {
        JSONObject root = SegmentRuleValidator.toJsonObject(filterJson);
        if (root == null || root.isEmpty())
        {
            throw invalid("filterJson 不能为空");
        }
        LeafCounter counter = new LeafCounter();
        validateNode(root, 1, counter);
        if (counter.leafCount == 0)
        {
            throw invalid("filterJson 至少包含一个条件");
        }
        if (counter.nodeCount > SegmentRuleValidator.MAX_NODES)
        {
            throw invalid("规则节点数超过上限 " + SegmentRuleValidator.MAX_NODES);
        }
    }

    private void validateNode(JSONObject node, int depth, LeafCounter counter)
    {
        if (node == null || node.isEmpty())
        {
            return;
        }
        counter.nodeCount++;
        if (depth > SegmentRuleValidator.MAX_DEPTH)
        {
            throw invalid("规则深度超过上限 " + SegmentRuleValidator.MAX_DEPTH);
        }
        if (node.containsKey("op"))
        {
            String op = node.getString("op");
            JSONArray children = node.getJSONArray("children");
            if (children == null || children.isEmpty())
            {
                throw invalid("分支节点缺少 children");
            }
            if ("NOT".equalsIgnoreCase(op) && children.size() != 1)
            {
                throw invalid("NOT 节点只能有一个子节点");
            }
            for (int i = 0; i < children.size(); i++)
            {
                validateNode(children.getJSONObject(i), depth + 1, counter);
            }
            return;
        }
        counter.leafCount++;
        String field = node.getString("field");
        String operator = node.getString("operator");
        if (!StringUtils.hasText(field) || !StringUtils.hasText(operator))
        {
            throw invalid("条件节点缺少 field/operator");
        }
        FanAudienceFieldDefinition definition = FanAudienceFieldRegistry.get(field);
        if (definition == null)
        {
            throw invalid("非法字段: " + field);
        }
        if (!definition.getOperators().contains(operator))
        {
            throw invalid("非法算子: " + operator);
        }
        validateLeafValue(definition, operator, node.get("value"));
    }

    private void validateLeafValue(FanAudienceFieldDefinition definition, String operator, Object value)
    {
        String type = definition.getType();
        if (FanAudienceFieldRegistry.TYPE_BOOLEAN.equals(type))
        {
            if (value != null)
            {
                throw invalid(definition.getField() + " 的 value 应为 null");
            }
            return;
        }
        if (FanAudienceFieldRegistry.TYPE_DATETIME.equals(type))
        {
            validateDateTimeValue(operator, value);
            return;
        }
        if (FanAudienceFieldRegistry.TYPE_WECHAT_MP_MENU.equals(type))
        {
            parseMenuRef(operator, value);
            return;
        }
        if (FanAudienceFieldRegistry.TYPE_WECHAT_MP_ARTICLE.equals(type))
        {
            parseArticleRef(operator, value);
            return;
        }
        if (FanAudienceFieldRegistry.TYPE_WECHAT_MP_QRCODE.equals(type))
        {
            parseQrcodeRef(operator, value);
        }
        if (FanAudienceFieldRegistry.TYPE_WECHAT_MP_TAG.equals(type))
        {
            parseTagRef(operator, value);
        }
    }

    private void validateDateTimeValue(String operator, Object value)
    {
        if (SegmentOperator.WITHIN_LAST_DAYS.equals(operator) || SegmentOperator.BEFORE_DAYS_AGO.equals(operator))
        {
            if (value == null)
            {
                throw invalid("缺少天数");
            }
            return;
        }
        if (SegmentOperator.BETWEEN.equals(operator))
        {
            if (!(value instanceof JSONArray) || ((JSONArray) value).size() != 2)
            {
                throw invalid("between 需要 [from, to] 两个 ISO 日期");
            }
        }
    }

    public FanWechatMpMenuRef parseMenuRef(String operator, Object value)
    {
        if (!SegmentOperator.EXISTS.equals(operator) && !SegmentOperator.NOT_EXISTS.equals(operator))
        {
            throw invalid("menuClicked operator 仅支持 exists / not_exists");
        }
        FanWechatMpMenuRef ref = toMenuRef(value);
        if (!StringUtils.hasText(ref.getMenuKey()))
        {
            throw invalid("menuKey 不能为空");
        }
        return ref;
    }

    public FanWechatMpArticleRef parseArticleRef(String operator, Object value)
    {
        if (!SegmentOperator.EXISTS.equals(operator) && !SegmentOperator.NOT_EXISTS.equals(operator))
        {
            throw invalid("articleRead operator 仅支持 exists / not_exists");
        }
        FanWechatMpArticleRef ref = toArticleRef(value);
        if (!StringUtils.hasText(ref.getUrl()) && !StringUtils.hasText(ref.getArticleId()))
        {
            throw invalid("articleRead 至少提供 url 或 articleId");
        }
        return ref;
    }

    public FanWechatMpQrcodeRef parseQrcodeRef(String operator, Object value)
    {
        if (!SegmentOperator.EXISTS.equals(operator) && !SegmentOperator.NOT_EXISTS.equals(operator))
        {
            throw invalid("scanFromQrcode operator 仅支持 exists / not_exists");
        }
        FanWechatMpQrcodeRef ref = toQrcodeRef(value);
        if (ref.getQrcodeId() == null && !StringUtils.hasText(ref.getSceneStr()))
        {
            throw invalid("scanFromQrcode 至少提供 qrcodeId 或 sceneStr");
        }
        return ref;
    }

    public FanWechatMpTagRef parseTagRef(String operator, Object value)
    {
        if (!SegmentOperator.EXISTS.equals(operator) && !SegmentOperator.NOT_EXISTS.equals(operator))
        {
            throw invalid("tagId operator 仅支持 exists / not_exists");
        }
        FanWechatMpTagRef ref = toTagRef(value);
        if (ref.getTagId() == null || ref.getTagId() <= 0)
        {
            throw invalid("tagId 必须为正整数");
        }
        return ref;
    }

    public static FanWechatMpMenuRef toMenuRef(Object value)
    {
        if (value == null)
        {
            throw invalid("value 不能为空");
        }
        if (value instanceof FanWechatMpMenuRef)
        {
            return (FanWechatMpMenuRef) value;
        }
        JSONObject json = value instanceof JSONObject
                ? (JSONObject) value
                : JSON.parseObject(JSON.toJSONString(value));
        FanWechatMpMenuRef ref = new FanWechatMpMenuRef();
        ref.setMenuKey(json.getString("menuKey"));
        ref.setMenuName(json.getString("menuName"));
        ref.setMenuPath(json.getString("menuPath"));
        return ref;
    }

    public static FanWechatMpArticleRef toArticleRef(Object value)
    {
        if (value == null)
        {
            throw invalid("value 不能为空");
        }
        if (value instanceof FanWechatMpArticleRef)
        {
            return (FanWechatMpArticleRef) value;
        }
        JSONObject json = value instanceof JSONObject
                ? (JSONObject) value
                : JSON.parseObject(JSON.toJSONString(value));
        FanWechatMpArticleRef ref = new FanWechatMpArticleRef();
        ref.setArticleId(json.getString("articleId"));
        ref.setTitle(json.getString("title"));
        ref.setUrl(json.getString("url"));
        return ref;
    }

    public static FanWechatMpQrcodeRef toQrcodeRef(Object value)
    {
        if (value == null)
        {
            throw invalid("value 不能为空");
        }
        if (value instanceof FanWechatMpQrcodeRef)
        {
            return (FanWechatMpQrcodeRef) value;
        }
        JSONObject json = value instanceof JSONObject
                ? (JSONObject) value
                : JSON.parseObject(JSON.toJSONString(value));
        FanWechatMpQrcodeRef ref = new FanWechatMpQrcodeRef();
        ref.setQrcodeId(json.getLong("qrcodeId"));
        ref.setSceneStr(json.getString("sceneStr"));
        ref.setName(json.getString("name"));
        return ref;
    }

    public static FanWechatMpTagRef toTagRef(Object value)
    {
        if (value == null)
        {
            throw invalid("value 不能为空");
        }
        if (value instanceof FanWechatMpTagRef)
        {
            return (FanWechatMpTagRef) value;
        }
        JSONObject json = value instanceof JSONObject
                ? (JSONObject) value
                : JSON.parseObject(JSON.toJSONString(value));
        FanWechatMpTagRef ref = new FanWechatMpTagRef();
        ref.setTagId(json.getLong("tagId"));
        ref.setTagName(json.getString("tagName"));
        return ref;
    }

    private static ServiceException invalid(String detail)
    {
        return new ServiceException("FAN_AUDIENCE_INVALID: " + detail, HttpStatus.BAD_REQUEST);
    }

    private static final class LeafCounter
    {
        private int nodeCount;
        private int leafCount;
    }
}
