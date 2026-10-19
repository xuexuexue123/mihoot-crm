package com.ruoyi.crm.domain.dto.wecom;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 获取朋友圈发表列表响应（#1 get_moment_list）。
 */
public class WecomMomentListResponse
{
    private Integer errcode;
    private String errmsg;

    @JsonProperty("next_cursor")
    private String nextCursor;

    @JsonProperty("moment_list")
    private List<WecomMomentItem> momentList;

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

    public List<WecomMomentItem> getMomentList()
    {
        return momentList;
    }

    public void setMomentList(List<WecomMomentItem> momentList)
    {
        this.momentList = momentList;
    }
}
