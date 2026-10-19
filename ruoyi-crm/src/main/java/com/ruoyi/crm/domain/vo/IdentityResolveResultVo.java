package com.ruoyi.crm.domain.vo;

import java.io.Serializable;

/**
 * Identity resolve 结果，含匹配来源。
 */
public class IdentityResolveResultVo implements Serializable
{
    private static final long serialVersionUID = 1L;

    private String contactId;
    private String leadId;
    private String matchedVia;
    private String identityType;
    private String identityValue;

    public boolean isMatched()
    {
        return contactId != null || leadId != null;
    }

    public String getIdentityType()
    {
        return identityType;
    }

    public void setIdentityType(String identityType)
    {
        this.identityType = identityType;
    }

    public String getIdentityValue()
    {
        return identityValue;
    }

    public void setIdentityValue(String identityValue)
    {
        this.identityValue = identityValue;
    }

    public String getContactId()
    {
        return contactId;
    }

    public void setContactId(String contactId)
    {
        this.contactId = contactId;
    }

    public String getLeadId()
    {
        return leadId;
    }

    public void setLeadId(String leadId)
    {
        this.leadId = leadId;
    }

    public String getMatchedVia()
    {
        return matchedVia;
    }

    public void setMatchedVia(String matchedVia)
    {
        this.matchedVia = matchedVia;
    }
}
