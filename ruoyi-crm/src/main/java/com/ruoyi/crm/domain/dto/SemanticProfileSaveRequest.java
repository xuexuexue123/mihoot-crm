package com.ruoyi.crm.domain.dto;

/**
 * 语义层配置 Upsert 请求体
 */
public class SemanticProfileSaveRequest
{
    private String industryKey;
    private String enabled;
    private Integer profileVersion;
    private String profileJson;

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
}
