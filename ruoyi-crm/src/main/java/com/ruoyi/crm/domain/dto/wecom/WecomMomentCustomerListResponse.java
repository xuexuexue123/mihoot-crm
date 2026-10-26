package com.ruoyi.crm.domain.dto.wecom;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 查询朋友圈可见客户/发送结果响应（#3 get_moment_customer_list / #4 get_moment_send_result）。
 */
public class WecomMomentCustomerListResponse
{
    private Integer errcode;
    private String errmsg;

    @JsonProperty("next_cursor")
    private String nextCursor;

    @JsonProperty("customer_list")
    private List<WecomMomentCustomerItem> customerList;

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

    public String getNextCursor()
    {
        return nextCursor;
    }

    public void setNextCursor(String nextCursor)
    {
        this.nextCursor = nextCursor;
    }

    public List<WecomMomentCustomerItem> getCustomerList()
    {
        return customerList;
    }

    public void setCustomerList(List<WecomMomentCustomerItem> customerList)
    {
        this.customerList = customerList;
    }
}
