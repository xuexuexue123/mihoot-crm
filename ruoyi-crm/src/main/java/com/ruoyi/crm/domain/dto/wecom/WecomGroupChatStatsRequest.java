package com.ruoyi.crm.domain.dto.wecom;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 企业微信「群聊数据统计」请求体。
 * 字段命名与企微 API 一致：owner_filter.userid_list / day_begin_time / day_end_time。
 */
public class WecomGroupChatStatsRequest
{
    /** 群主过滤 */
    @JsonProperty("owner_filter")
    private OwnerFilter ownerFilter;

    /** 起始日期 Unix 秒级时间戳 */
    @JsonProperty("day_begin_time")
    private Long dayBeginTime;

    /** 结束日期 Unix 秒级时间戳 */
    @JsonProperty("day_end_time")
    private Long dayEndTime;

    /**
     * 群主过滤。
     */
    public static class OwnerFilter
    {
        /** 群主 userid 列表，最多 100 个 */
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

    @JsonProperty("owner_filter")
    public OwnerFilter getOwnerFilter()
    {
        return ownerFilter;
    }

    @JsonProperty("owner_filter")
    public void setOwnerFilter(OwnerFilter ownerFilter)
    {
        this.ownerFilter = ownerFilter;
    }

    @JsonProperty("day_begin_time")
    public Long getDayBeginTime()
    {
        return dayBeginTime;
    }

    @JsonProperty("day_begin_time")
    public void setDayBeginTime(Long dayBeginTime)
    {
        this.dayBeginTime = dayBeginTime;
    }

    @JsonProperty("day_end_time")
    public Long getDayEndTime()
    {
        return dayEndTime;
    }

    @JsonProperty("day_end_time")
    public void setDayEndTime(Long dayEndTime)
    {
        this.dayEndTime = dayEndTime;
    }
}
