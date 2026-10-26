package com.ruoyi.crm.domain.dto.wecom;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 企业微信「联系客户统计」请求体。
 * 字段命名与企微 API 一致：userid / partyid / start_time / end_time。
 */
public class WecomBehaviorDataRequest
{
    /** 成员ID列表，最多100个 */
    private List<String> userid;

    /** 部门ID列表，最多100个 */
    private List<Integer> partyid;

    /** 数据起始时间（Unix 秒级时间戳） */
    private Long startTime;

    /** 数据结束时间（Unix 秒级时间戳） */
    private Long endTime;

    public List<String> getUserid()
    {
        return userid;
    }

    public void setUserid(List<String> userid)
    {
        this.userid = userid;
    }

    public List<Integer> getPartyid()
    {
        return partyid;
    }

    public void setPartyid(List<Integer> partyid)
    {
        this.partyid = partyid;
    }

    @JsonProperty("start_time")
    public Long getStartTime()
    {
        return startTime;
    }

    @JsonProperty("start_time")
    public void setStartTime(Long startTime)
    {
        this.startTime = startTime;
    }

    @JsonProperty("end_time")
    public Long getEndTime()
    {
        return endTime;
    }

    @JsonProperty("end_time")
    public void setEndTime(Long endTime)
    {
        this.endTime = endTime;
    }
}
