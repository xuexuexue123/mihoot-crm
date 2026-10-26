package com.ruoyi.crm.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

/**
 * 语义层配置 VO
 */
public class SemanticProfileVO
{
    private String profileId;
    private String industryKey;
    private String enabled;
    private Integer profileVersion;
    private String profileJson;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    public String getProfileId()
    {
        return profileId;
    }

    public void setProfileId(String profileId)
    {
        this.profileId = profileId;
    }

    public String getIndustryKey()
    {
        return industryKey;
    }

    public void setIndustryKey(String industryKey)
    {
        this.industryKey = industryKey;
    }

    public String getEnabled()
    {
        return enabled;
    }

    public void setEnabled(String enabled)
    {
        this.enabled = enabled;
    }

    public Integer getProfileVersion()
    {
        return profileVersion;
    }

    public void setProfileVersion(Integer profileVersion)
    {
        this.profileVersion = profileVersion;
    }

    public String getProfileJson()
    {
        return profileJson;
    }

    public void setProfileJson(String profileJson)
    {
        this.profileJson = profileJson;
    }

    public Date getUpdateTime()
    {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime)
    {
        this.updateTime = updateTime;
    }
}
