package com.ruoyi.crm.service.support;

import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.domain.CrmLead;
import com.ruoyi.crm.service.ICrmCustomerIdentityService;

/**
 * 活动报名与公众号 openid 归因
 */
public final class MpLeadIdentitySupport
{
    public static final String CUSTOM_FIELD_OPENID = "wechat_mp_openid";
    public static final String CUSTOM_FIELD_APP_ID = "wechat_mp_app_id";

    private MpLeadIdentitySupport()
    {
    }

    public static void applyMpOpenIdColumns(CrmLead lead, String openId, String appId)
    {
        if (lead == null || StringUtils.isBlank(openId) || StringUtils.isBlank(appId))
        {
            return;
        }
        lead.setWechatMpOpenid(openId);
        lead.setWechatMpAppId(appId);
    }

    public static void bindMpIdentity(ICrmCustomerIdentityService identityService,
            CrmLead lead, String openId, String appId)
    {
        if (lead == null || StringUtils.isBlank(openId) || StringUtils.isBlank(appId))
        {
            return;
        }
        applyMpOpenIdColumns(lead, openId, appId);
        if (StringUtils.isNotBlank(lead.getContactId()))
        {
            try
            {
                identityService.bindIdentity(lead.getContactId(), "wechat_mp_openid",
                        openId, "event_registration");
            }
            catch (RuntimeException e)
            {
                // 已绑定到其他 Contact：记录冲突供人工合并，不再静默丢弃
                identityService.recordIdentityConflict("wechat_mp_openid", openId,
                        lead.getContactId(), "event_registration");
            }
        }
    }

    public static JSONObject parseCustomField(String customField)
    {
        if (StringUtils.isBlank(customField))
        {
            return new JSONObject();
        }
        try
        {
            JSONObject obj = JSONObject.parseObject(customField);
            return obj != null ? obj : new JSONObject();
        }
        catch (Exception ex)
        {
            return new JSONObject();
        }
    }

    public static void mergeCustomField(CrmLead target, String incomingCustomField)
    {
        if (StringUtils.isBlank(incomingCustomField))
        {
            return;
        }
        JSONObject existing = parseCustomField(target.getCustomField());
        JSONObject incoming = parseCustomField(incomingCustomField);
        existing.putAll(incoming);
        target.setCustomField(existing.toJSONString());
    }
}
