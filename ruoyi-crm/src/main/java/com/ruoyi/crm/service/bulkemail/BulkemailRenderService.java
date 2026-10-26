package com.ruoyi.crm.service.bulkemail;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.domain.CrmBulkemail;
import com.ruoyi.crm.domain.CrmCampaignContacts;
import com.ruoyi.crm.domain.CrmContact;
import com.ruoyi.crm.domain.dto.BulkemailRenderResult;

/**
 * 邮件 subject + HTML 渲染（P1 legacy + P2 merge tags）
 */
@Component
public class BulkemailRenderService
{
    @Autowired
    private BulkemailMergeTagSupport mergeTagSupport;

    /**
     * @deprecated 使用 {@link #render(String, String, BulkemailRenderContext)}
     */
    public String render(String emailContent, CrmCampaignContacts contact, CrmBulkemail campaign, String tenantId)
    {
        boolean personalizationEnabled = BulkemailMetadataSupport.isPersonalizationEnabled(campaign);
        BulkemailRenderContext ctx = new BulkemailRenderContext(campaign, contact, null, tenantId, personalizationEnabled);
        BulkemailRenderResult result = render(campaign != null ? campaign.getSubject() : null, emailContent, ctx);
        return result.getEmailContent();
    }

    public BulkemailRenderResult render(String subject, String emailContent, BulkemailRenderContext ctx)
    {
        String outSubject = subject != null ? subject : "";
        String outHtml = emailContent != null ? emailContent : "";
        if (ctx == null)
        {
            return new BulkemailRenderResult(outSubject, outHtml);
        }
        outSubject = mergeTagSupport.applyP1Tags(outSubject, ctx);
        outHtml = mergeTagSupport.applyP1Tags(outHtml, ctx);
        if (ctx.isPersonalizationEnabled())
        {
            Map<String, String> vars = mergeTagSupport.buildP2Variables(ctx);
            outSubject = mergeTagSupport.applyP2Tags(outSubject, vars);
            outHtml = mergeTagSupport.applyP2Tags(outHtml, vars);
        }
        return new BulkemailRenderResult(outSubject, outHtml);
    }

    public BulkemailRenderContext buildContext(CrmBulkemail campaign, CrmCampaignContacts campaignContact,
            CrmContact crmContact, String tenantId, Boolean personalizationOverride)
    {
        boolean enabled = personalizationOverride != null
                ? personalizationOverride.booleanValue()
                : BulkemailMetadataSupport.isPersonalizationEnabled(campaign);
        return new BulkemailRenderContext(campaign, campaignContact, crmContact, tenantId, enabled);
    }
}
