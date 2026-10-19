package com.ruoyi.crm.service.bulkemail;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ruoyi.common.utils.HmacUtil;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.domain.CrmBulkemail;
import com.ruoyi.crm.domain.CrmCampaignContacts;
import com.ruoyi.crm.domain.CrmContact;
import com.ruoyi.crm.wechatmp.support.WechatMpMessagePresetRenderSupport;

/**
 * P2 merge tag 变量与替换
 */
@Component
public class BulkemailMergeTagSupport
{
    private static final Pattern P2_TAG_PATTERN = Pattern.compile("\\{\\{([a-zA-Z0-9_]+)\\}\\}");

    @Autowired
    private BulkemailTenantBrandingService tenantBrandingService;

    public Map<String, String> buildP2Variables(BulkemailRenderContext ctx)
    {
        Map<String, String> vars = new HashMap<>();
        String contactName = resolveContactName(ctx);
        String workEmail = resolveWorkEmail(ctx);
        vars.put("contactName", contactName);
        vars.put("workEmail", workEmail);
        vars.put("clinicName", tenantBrandingService.getClinicName(ctx.getTenantId()));
        vars.put("unsubscribeUrl", "#");
        return vars;
    }

    public String applyP2Tags(String template, Map<String, String> vars)
    {
        if (StringUtils.isBlank(template))
        {
            return template;
        }
        String rendered = WechatMpMessagePresetRenderSupport.substitute(template, vars);
        Matcher matcher = P2_TAG_PATTERN.matcher(rendered);
        StringBuffer sb = new StringBuffer();
        while (matcher.find())
        {
            matcher.appendReplacement(sb, "");
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    public String applyP1Tags(String template, BulkemailRenderContext ctx)
    {
        if (StringUtils.isBlank(template))
        {
            return template;
        }
        CrmCampaignContacts contact = ctx.getCampaignContact();
        CrmBulkemail campaign = ctx.getCampaign();
        String contactName = resolveContactName(ctx);
        String email = resolveWorkEmail(ctx);
        String bulkId = campaign != null ? nullToEmpty(campaign.getBulkId()) : "";
        String token = "";
        if (StringUtils.isNotBlank(email) && StringUtils.isNotBlank(bulkId)
                && StringUtils.isNotBlank(ctx.getTenantId()))
        {
            token = HmacUtil.generateTrackingToken(email, bulkId, ctx.getTenantId());
        }
        String html = template;
        html = replaceAll(html, "<<CONTACT_NAME>>", contactName);
        html = replaceAll(html, "&lt;&lt;CONTACT_NAME&gt;&gt;", contactName);
        html = replaceAll(html, "<<EMAIL_ADDRESS>>", email);
        html = replaceAll(html, "<<CAMPAIGN_ID>>", bulkId);
        html = replaceAll(html, "<<TOKEN_KEY>>", token);
        if (contact != null && StringUtils.isBlank(contact.getContactName()) && StringUtils.isNotBlank(contactName))
        {
            // no-op; kept for symmetry with legacy behavior
        }
        return html;
    }

    private static String resolveContactName(BulkemailRenderContext ctx)
    {
        CrmContact crmContact = ctx.getCrmContact();
        if (crmContact != null && StringUtils.isNotBlank(crmContact.getName()))
        {
            return crmContact.getName();
        }
        CrmCampaignContacts cc = ctx.getCampaignContact();
        if (cc != null && StringUtils.isNotBlank(cc.getContactName()))
        {
            return cc.getContactName();
        }
        return "";
    }

    private static String resolveWorkEmail(BulkemailRenderContext ctx)
    {
        CrmContact crmContact = ctx.getCrmContact();
        if (crmContact != null && StringUtils.isNotBlank(crmContact.getWorkEmail()))
        {
            return crmContact.getWorkEmail();
        }
        CrmCampaignContacts cc = ctx.getCampaignContact();
        if (cc != null && StringUtils.isNotBlank(cc.getEmail()))
        {
            return cc.getEmail();
        }
        return "";
    }

    private static String replaceAll(String source, String target, String value)
    {
        return source.replace(target, value != null ? value : "");
    }

    private static String nullToEmpty(String value)
    {
        return value != null ? value : "";
    }
}
