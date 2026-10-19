package com.ruoyi.crm.domain.dto.wecom;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 企业微信「客户群列表」请求体。
 * 字段命名与企微 API 一致：status_filter / owner_filter.userid_list / cursor / limit。
 */
public class WecomGroupChatListRequest
{
    @JsonProperty("status_filter")
    private Integer statusFilter;

    @JsonProperty("owner_filter")
    private OwnerFilter ownerFilter;

    @JsonProperty("cursor")
    private String cursor;

    @JsonProperty("limit")
    private Integer limit;

    /**
     * 群主过滤。
     */
    public static class OwnerFilter
    {
        @JsonProperty("userid_list")
        private List<String> useridList;

        public List<String> getUseridList()
        {
            return useridList;
        }

        public void setUseridList(List<String> useridList)
        {
            this.useridList = useridList;
        }
    }

    public Integer getStatusFilter()
    {
        return statusFilter;
    }

    public void setStatusFilter(Integer statusFilter)
    {
        this.statusFilter = statusFilter;
    }

    public OwnerFilter getOwnerFilter()
    {
        return ownerFilter;
    }

    public void setOwnerFilter(OwnerFilter ownerFilter)
    {
        this.ownerFilter = ownerFilter;
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
