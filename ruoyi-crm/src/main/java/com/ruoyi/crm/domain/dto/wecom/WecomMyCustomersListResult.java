package com.ruoyi.crm.domain.dto.wecom;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * GET /crm/wecom/my-customers/list 响应 data
 */
public class WecomMyCustomersListResult
{
    private List<String> externalUserid = new ArrayList<>();
    private Integer errcode;
    private String errmsg;
    /** external_userid -> { crmContactId, crmLeadId } */
    private Map<String, WecomMyCustomersCrmLink> crmLinks;

    public List<String> getExternalUserid()
    {
        return externalUserid;
    }

    public void setExternalUserid(List<String> externalUserid)
    {
        this.externalUserid = externalUserid;
    }

    public Integer getErrcode()
    {
        return errcode;
    }

    public void setErrcode(Integer errcode)
    {
        this.errcode = errcode;
    }

    public String getErrmsg()
    {
        return errmsg;
    }

    public void setErrmsg(String errmsg)
    {
        this.errmsg = errmsg;
    }

    public Map<String, WecomMyCustomersCrmLink> getCrmLinks()
    {
        return crmLinks;
    }

    public void setCrmLinks(Map<String, WecomMyCustomersCrmLink> crmLinks)
    {
        this.crmLinks = crmLinks;
    }
}
