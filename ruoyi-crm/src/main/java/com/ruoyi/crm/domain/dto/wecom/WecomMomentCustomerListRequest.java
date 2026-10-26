package com.ruoyi.crm.domain.dto.wecom;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 查询朋友圈可见客户/发送结果请求（#3 get_moment_customer_list / #4 get_moment_send_result）。
 */
public class WecomMomentCustomerListRequest
{
    @JsonProperty("moment_id")
    private String momentId;

    private String userid;
    private String cursor;
    private Integer limit;

    public String getMomentId()
    {
        return momentId;
    }

    public void setMomentId(String momentId)
    {
        this.momentId = momentId;
    }

    public String getUserid()
    {
        return userid;
    }

    public void setUserid(String userid)
    {
        this.userid = userid;
    }

    public String getCursor()
    {
        return cursor;
    }

    public void setCursor(String cursor)
    {
        this.cursor = cursor;
    }

    public Integer getLimit()
    {
        return limit;
    }

    public void setLimit(Integer limit)
    {
        this.limit = limit;
    }
}
