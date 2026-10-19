package com.ruoyi.crm.service.bulkemail;

import com.ruoyi.crm.domain.CrmBulkemail;
import com.ruoyi.crm.domain.CrmCampaignContacts;
import com.ruoyi.crm.domain.CrmContact;

/**
 * 邮件渲染上下文
 */
public class BulkemailRenderContext
{
    private final CrmBulkemail campaign;
    private final CrmCampaignContacts campaignContact;
    private final CrmContact crmContact;
    private final String tenantId;
    private final boolean personalizationEnabled;

    public BulkemailRenderContext(CrmBulkemail campaign, CrmCampaignContacts campaignContact,
            CrmContact crmContact, String tenantId, boolean personalizationEnabled)
    {
        this.campaign = campaign;
        this.campaignContact = campaignContact;
        this.crmContact = crmContact;
        this.tenantId = tenantId;
        this.personalizationEnabled = personalizationEnabled;
    }

    public CrmBulkemail getCampaign()
    {
        return campaign;
    }

    public CrmCampaignContacts getCampaignContact()
    {
        return campaignContact;
    }

    public CrmContact getCrmContact()
    {
        return crmContact;
    }

    public String getTenantId()
    {
        return tenantId;
    }

    public boolean isPersonalizationEnabled()
    {
        return personalizationEnabled;
    }
}
