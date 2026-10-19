package com.ruoyi.crm.wechatmp.audience;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.crm.service.support.SegmentOperator;
import com.ruoyi.crm.service.support.SegmentRuleValidator;
import com.ruoyi.crm.wechatmp.audience.support.FanActivityArticleReadResolver;
import com.ruoyi.crm.wechatmp.audience.support.FanActivityMenuClickResolver;
import com.ruoyi.crm.wechatmp.audience.support.FanActivityScanResolver;
import com.ruoyi.crm.wechatmp.audience.support.FanAudienceScopeHelper;
import com.ruoyi.crm.wechatmp.audience.support.FanInteractionActivityResolver;
import com.ruoyi.crm.wechatmp.audience.support.FanTableConditionResolver;
import com.ruoyi.crm.wechatmp.audience.support.FanTagAudienceResolver;

@Component
public class FanAudienceCompiler
{
    @Autowired
    private FanAudienceRuleValidator ruleValidator;

    @Autowired
    private FanTableConditionResolver fanTableConditionResolver;

    @Autowired
    private FanActivityMenuClickResolver menuClickResolver;

    @Autowired
    private FanActivityArticleReadResolver articleReadResolver;

    @Autowired
    private FanActivityScanResolver scanResolver;

    @Autowired
    private FanAudienceScopeHelper scopeHelper;

    @Autowired
    private FanInteractionActivityResolver interactionActivityResolver;

    @Autowired
    private FanTagAudienceResolver tagResolver;

    public FanAudienceCompileResult compile(String appId, Object filterJson, FanAudienceCompileOptions options)
    {
        ruleValidator.validateAppId(appId);
        ruleValidator.validateFilterJson(filterJson);
        JSONObject root = SegmentRuleValidator.toJsonObject(filterJson);
        String trimmedAppId = appId.trim();
        Set<String> openIds = compileNode(root, trimmedAppId);
        int skipCount = 0;
        if (options != null && options.isApplySubscribedDefaultForSend())
        {
            if (!hasSubscribedExistsConstraint(root))
            {
                Set<String> subscribed = scopeHelper.loadSubscribedOpenIds(trimmedAppId);
                Set<String> before = new HashSet<>(openIds);
                openIds.retainAll(subscribed);
                skipCount = before.size() - openIds.size();
            }
        }
        return new FanAudienceCompileResult(openIds, skipCount);
    }

    private Set<String> compileNode(JSONObject node, String appId)
    {
        if (node == null || node.isEmpty())
        {
            return new LinkedHashSet<>();
        }
        if (node.containsKey("op"))
        {
            return compileBranch(node, appId);
        }
        return compileLeaf(node, appId);
    }

    private Set<String> compileBranch(JSONObject node, String appId)
    {
        String op = node.getString("op");
        JSONArray children = node.getJSONArray("children");
        if (children == null || children.isEmpty())
        {
            return new LinkedHashSet<>();
        }
        if ("NOT".equalsIgnoreCase(op))
        {
            Set<String> child = compileNode(children.getJSONObject(0), appId);
            Set<String> universe = scopeHelper.loadSubscribedOpenIds(appId);
            universe.removeAll(child);
            return universe;
        }
        Set<String> result = null;
        boolean isOr = "OR".equalsIgnoreCase(op);
        for (int i = 0; i < children.size(); i++)
        {
            Set<String> childSet = compileNode(children.getJSONObject(i), appId);
            if (result == null)
            {
                result = new LinkedHashSet<>(childSet);
                continue;
            }
            if (isOr)
            {
                result.addAll(childSet);
            }
            else
            {
                result.retainAll(childSet);
            }
        }
        return result == null ? new LinkedHashSet<>() : result;
    }

    private Set<String> compileLeaf(JSONObject node, String appId)
    {
        String field = node.getString("field");
        String operator = node.getString("operator");
        Object value = node.get("value");
        if (FanAudienceFieldRegistry.FIELD_MENU_CLICKED.equals(field))
        {
            return menuClickResolver.resolve(appId, operator, value);
        }
        if (FanAudienceFieldRegistry.FIELD_ARTICLE_READ.equals(field))
        {
            return articleReadResolver.resolve(appId, operator, value);
        }
        if (FanAudienceFieldRegistry.FIELD_SCAN_FROM_QRCODE.equals(field))
        {
            return scanResolver.resolve(appId, operator, value);
        }
        if (FanAudienceFieldRegistry.FIELD_LAST_INTERACTION_WITHIN_48H.equals(field))
        {
            return interactionActivityResolver.resolve(appId, operator);
        }
        if (FanAudienceFieldRegistry.FIELD_TAG_ID.equals(field))
        {
            return tagResolver.resolve(appId, operator, value);
        }
        if (FanAudienceFieldRegistry.FIELD_SUBSCRIBED.equals(field)
                || FanAudienceFieldRegistry.FIELD_SUBSCRIBE_TIME.equals(field)
                || FanAudienceFieldRegistry.FIELD_HAS_CONTACT.equals(field))
        {
            return fanTableConditionResolver.resolve(appId, field, operator, value);
        }
        throw new ServiceException("非法字段");
    }

    private boolean hasSubscribedExistsConstraint(JSONObject node)
    {
        if (node == null || node.isEmpty())
        {
            return false;
        }
        if (node.containsKey("op"))
        {
            JSONArray children = node.getJSONArray("children");
            if (children == null)
            {
                return false;
            }
            for (int i = 0; i < children.size(); i++)
            {
                if (hasSubscribedExistsConstraint(children.getJSONObject(i)))
                {
                    return true;
                }
            }
            return false;
        }
        return FanAudienceFieldRegistry.FIELD_SUBSCRIBED.equals(node.getString("field"))
                && SegmentOperator.EXISTS.equals(node.getString("operator"));
    }
}
