package com.ruoyi.crm.domain.dto.wecom;

import com.alibaba.fastjson2.JSONObject;

/**
 * 单条企微客户详情（对齐 sevencrm WecomStaffCustomerDetailResult 核心字段）
 */
public class WecomMyCustomersDetailItem
{
    private String externalUserid;
    private JSONObject detail;
    private String crmContactId;
    private String crmLeadId;
    private Integer errcode;
    private String errmsg;

    public String getExternalUserid()
    {
        return externalUserid;
    }

    public void setExternalUserid(String externalUserid)
    {
        this.externalUserid = externalUserid;
    }

    public JSONObject getDetail()
    {
        return detail;
    }

    public void setDetail(JSONObject detail)
    {
        this.detail = detail;
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
}
