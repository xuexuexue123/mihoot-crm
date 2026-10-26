package com.ruoyi.crm.domain.dto.wecom;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 企业微信「联系客户统计」API 响应体。
 * 对应 get_user_behavior_data 接口的完整返回。
 */
public class WecomBehaviorDataResponse
{
    private Integer errcode;

    private String errmsg;

    @JsonProperty("behavior_data")
    private List<WecomBehaviorDataItem> behaviorData;

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

    @JsonProperty("behavior_data")
    public List<WecomBehaviorDataItem> getBehaviorData()
    {
        return behaviorData;
    }

    @JsonProperty("behavior_data")
    public void setBehaviorData(List<WecomBehaviorDataItem> behaviorData)
    {
        this.behaviorData = behaviorData;
    }
}
