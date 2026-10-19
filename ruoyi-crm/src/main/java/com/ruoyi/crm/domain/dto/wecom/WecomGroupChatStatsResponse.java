package com.ruoyi.crm.domain.dto.wecom;

import java.util.List;

/**
 * 企业微信「群聊数据统计」API 响应体。
 * 对应 statistic_group_by_day 接口的完整返回。
 */
public class WecomGroupChatStatsResponse
{
    private Integer errcode;

    private String errmsg;

    private List<GroupChatDayItem> items;

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

    public List<GroupChatDayItem> getItems()
    {
        return items;
    }

    public void setItems(List<GroupChatDayItem> items)
    {
        this.items = items;
    }
}
