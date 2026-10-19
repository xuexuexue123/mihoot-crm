package com.ruoyi.crm.domain.vo;

/**
 * 小程序线索查建响应
 */
public class MiniappLeadUpsertResultVo
{
    private String leadId;
    private String contactId;
    /** created | updated | linked_contact */
    private String resolution;

    public static MiniappLeadUpsertResultVo of(String leadId, String contactId, String resolution)
    {
        MiniappLeadUpsertResultVo vo = new MiniappLeadUpsertResultVo();
        vo.setLeadId(leadId);
        vo.setContactId(contactId);
        vo.setResolution(resolution);
        return vo;
    }

    public String getLeadId()
    {
        return leadId;
    }

    public void setLeadId(String leadId)
    {
        this.leadId = leadId;
    }

    public String getContactId()
    {
        return contactId;
    }

    public void setContactId(String contactId)
    {
        this.contactId = contactId;
    }

    public String getResolution()
    {
        return resolution;
    }

    public void setResolution(String resolution)
    {
        this.resolution = resolution;
    }
}
