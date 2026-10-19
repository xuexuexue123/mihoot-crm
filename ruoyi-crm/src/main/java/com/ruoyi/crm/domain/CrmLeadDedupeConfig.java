package com.ruoyi.crm.domain;

import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 租户线索/客户查重配置 crm_lead_dedupe_config（一库一行）
 */
public class CrmLeadDedupeConfig extends BaseEntity
{
    private String configId;
    private String leadMatchFields;
    private String contactMatchFields;
    private String matchMode;

    public String getConfigId()
    {
        return configId;
    }

    public void setConfigId(String configId)
    {
        this.configId = configId;
    }

    public String getLeadMatchFields()
    {
        return leadMatchFields;
    }

    public void setLeadMatchFields(String leadMatchFields)
    {
        this.leadMatchFields = leadMatchFields;
    }

    public String getContactMatchFields()
    {
        return contactMatchFields;
    }

    public void setContactMatchFields(String contactMatchFields)
    {
        this.contactMatchFields = contactMatchFields;
    }

    public String getMatchMode()
    {
        return matchMode;
    }

    public void setMatchMode(String matchMode)
    {
        this.matchMode = matchMode;
    }
}
