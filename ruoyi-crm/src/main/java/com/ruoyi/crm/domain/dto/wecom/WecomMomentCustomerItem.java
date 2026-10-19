package com.ruoyi.crm.domain.dto.wecom;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 可见客户/发送结果客户项（#3 get_moment_customer_list / #4 get_moment_send_result）。
 */
public class WecomMomentCustomerItem
{
    private String userid;

    @JsonProperty("external_userid")
    private String externalUserid;

    public String getUserid()
    {
        return userid;
    }

    public void setUserid(String userid)
    {
        this.userid = userid;
    }

    public String getExternalUserid()
    {
        return externalUserid;
    }

    public void setExternalUserid(String externalUserid)
    {
        this.externalUserid = externalUserid;
    }
}
