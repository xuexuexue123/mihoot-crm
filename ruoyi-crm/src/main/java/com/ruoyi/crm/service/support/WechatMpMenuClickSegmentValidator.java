package com.ruoyi.crm.service.support;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.crm.domain.dto.SegmentWechatMpMenuRef;
import com.ruoyi.crm.wechatmp.mapper.CrmWechatMpConfigMapper;

/**
 * 分群规则：公众号菜单点击条件校验
 */
@Component
public class WechatMpMenuClickSegmentValidator
{
    @Autowired
    private CrmWechatMpConfigMapper wechatMpConfigMapper;

    public void validateRule(Object ruleJson)
    {
        validateNode(SegmentRuleValidator.toJsonObject(ruleJson));
    }

    public SegmentWechatMpMenuRef parseAndValidateMenuRef(String operator, Object value)
    {
        if (!SegmentOperator.EXISTS.equals(operator) && !SegmentOperator.NOT_EXISTS.equals(operator))
        {
            throw invalidRule("operator 仅支持 exists / not_exists");
        }
        SegmentWechatMpMenuRef ref = parseMenuRef(value);
        if (!StringUtils.hasText(ref.getAppId()))
        {
            throw invalidRule("appId 不能为空");
        }
        if (!StringUtils.hasText(ref.getMenuKey()))
        {
            throw invalidRule("menuKey 不能为空");
        }
        if (wechatMpConfigMapper.selectByAppId(ref.getAppId().trim()) == null)
        {
            throw invalidRule("appId 不属于当前租户已配置公众号: " + ref.getAppId());
        }
        ref.setAppId(ref.getAppId().trim());
        ref.setMenuKey(ref.getMenuKey().trim());
        return ref;
    }

    public static SegmentWechatMpMenuRef parseMenuRef(Object value)
    {
        if (value == null)
        {
            throw invalidRule("value 不能为空");
        }
        JSONObject json;
        if (value instanceof JSONObject)
        {
            json = (JSONObject) value;
        }
        else if (value instanceof SegmentWechatMpMenuRef)
        {
            return (SegmentWechatMpMenuRef) value;
        }
        else
        {
            json = JSON.parseObject(JSON.toJSONString(value));
        }
        if (json == null || json.isEmpty())
        {
            throw invalidRule("value 必须为对象");
        }
        SegmentWechatMpMenuRef ref = new SegmentWechatMpMenuRef();
        ref.setAppId(json.getString("appId"));
        ref.setMenuKey(json.getString("menuKey"));
        ref.setMenuName(json.getString("menuName"));
        ref.setMenuPath(json.getString("menuPath"));
        return ref;
    }

    private void validateNode(JSONObject node)
    {
        if (node == null || node.isEmpty())
        {
            return;
        }
        if (node.containsKey("op"))
        {
            JSONArray children = node.getJSONArray("children");
            if (children == null)
            {
                return;
            }
            for (int i = 0; i < children.size(); i++)
            {
                validateNode(children.getJSONObject(i));
            }
            return;
        }
        String source = node.getString("source");
        String field = node.getString("field");
        if (SegmentFieldRegistry.SOURCE_METRIC.equals(source)
                && SegmentFieldRegistry.FIELD_WECHAT_MP_MENU_ARTICLE_VIEWED.equals(field))
        {
            parseAndValidateMenuRef(node.getString("operator"), node.get("value"));
        }
    }

    private static ServiceException invalidRule(String detail)
    {
        return new ServiceException("SEGMENT_INVALID_RULE: " + detail, HttpStatus.BAD_REQUEST);
    }
}
