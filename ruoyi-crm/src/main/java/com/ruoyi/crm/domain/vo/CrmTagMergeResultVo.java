package com.ruoyi.crm.domain.vo;

/**
 * merge 结果
 */
public class CrmTagMergeResultVo
{
    private int updatedContacts;
    private int updatedLeads;

    public CrmTagMergeResultVo()
    {
    }

    public CrmTagMergeResultVo(int updatedContacts, int updatedLeads)
    {
        this.updatedContacts = updatedContacts;
        this.updatedLeads = updatedLeads;
    }

    public int getUpdatedContacts()
    {
        return updatedContacts;
    }

    public void setUpdatedContacts(int updatedContacts)
    {
        this.updatedContacts = updatedContacts;
    }

    public int getUpdatedLeads()
    {
        return updatedLeads;
    }

    public void setUpdatedLeads(int updatedLeads)
    {
        this.updatedLeads = updatedLeads;
    }
}
