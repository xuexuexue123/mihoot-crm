package com.ruoyi.crm.domain.dto;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAlias;

/**
 * 小程序 BFF：按手机号幂等 upsert 线索
 */
public class MiniappLeadUpsertRequest
{
    private String mobilePhone;
    private String leadName;
    /** 默认 wechat_miniapp；可覆盖为细分来源（亦接受 JSON 字段 leadSource） */
    @JsonAlias("leadSource")
    private String sourceChannel;
    private String remark;
    /** 合并进 custom_field（顾问已填非空关键字段不覆盖） */
    private Map<String, Object> profileJson;

    public String getMobilePhone()
    {
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone)
    {
        this.mobilePhone = mobilePhone;
    }

    public String getLeadName()
    {
        return leadName;
    }

    public void setLeadName(String leadName)
    {
        this.leadName = leadName;
    }

    public String getSourceChannel()
    {
        return sourceChannel;
    }

    public void setSourceChannel(String sourceChannel)
    {
        this.sourceChannel = sourceChannel;
    }

    public String getRemark()
    {
        return remark;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
    }

    public Map<String, Object> getProfileJson()
    {
        return profileJson;
    }

    public void setProfileJson(Map<String, Object> profileJson)
    {
        this.profileJson = profileJson;
    }
}
