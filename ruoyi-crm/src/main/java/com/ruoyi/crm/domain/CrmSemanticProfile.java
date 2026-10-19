package com.ruoyi.crm.domain;

import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 租户语义层配置 crm_semantic_profile
 */
public class CrmSemanticProfile extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private String profileId;
    private String tenantId;
    private String industryKey;
    private String enabled;
    private Integer profileVersion;
    private String profileJson;
    private String delFlag;

    public String getProfileId()
    {
        return profileId;
    }

    public void setProfileId(String profileId)
    {
        this.profileId = profileId;
    }

    public String getTenantId()
    {
        return tenantId;
    }

    public void setTenantId(String tenantId)
    {
        this.tenantId = tenantId;
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

    public String getDelFlag()
    {
        return delFlag;
    }

    public void setDelFlag(String delFlag)
    {
        this.delFlag = delFlag;
    }
}
