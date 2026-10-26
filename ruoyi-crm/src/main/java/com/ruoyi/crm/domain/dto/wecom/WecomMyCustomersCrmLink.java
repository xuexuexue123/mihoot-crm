package com.ruoyi.crm.domain.dto.wecom;

/**
 * 我的企微客户与 CRM 关联摘要
 */
public class WecomMyCustomersCrmLink
{
    private String crmContactId;
    private String crmLeadId;

    public WecomMyCustomersCrmLink()
    {
    }

    public WecomMyCustomersCrmLink(String crmContactId, String crmLeadId)
    {
        this.crmContactId = crmContactId;
        this.crmLeadId = crmLeadId;
    }

    public String getCrmContactId()
    {
        return crmContactId;
    }

    public void setCrmContactId(String crmContactId)
    {
        this.crmContactId = crmContactId;
    }

    public String getCrmLeadId()
    {
        return crmLeadId;
    }

    public void setCrmLeadId(String crmLeadId)
    {
        this.crmLeadId = crmLeadId;
    }
}
