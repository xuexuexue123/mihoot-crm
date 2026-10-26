package com.ruoyi.crm.domain.dto.wecom;

/**
 * 保存企业微信租户配置（Secret 新建必填，更新可省略）
 */
public class WecomConfigSaveRequest
{
    private String corpId;
    private String agentId;
    /** 新建必填；更新时省略表示保留原 Secret */
    private String agentSecret;
    private String token;
    private String encodingAesKey;
    private Boolean welcomeEnabled;
    private String welcomeText;
    /** 回调建线索默认负责人 sys_user.user_id */
    private Long defaultLeadUserId;
    /** 可选；为空则用负责人部门 */
    private Long defaultLeadDeptId;
    /** 传 true 时清空默认线索负责人配置 */
    private Boolean clearDefaultLeadOwner;

    public String getCorpId()
    {
        return corpId;
    }

    public void setCorpId(String corpId)
    {
        this.corpId = corpId;
    }

    public String getAgentId()
    {
        return agentId;
    }

    public void setAgentId(String agentId)
    {
        this.agentId = agentId;
    }

    public String getAgentSecret()
    {
        return agentSecret;
    }

    public void setAgentSecret(String agentSecret)
    {
        this.agentSecret = agentSecret;
    }

    public String getToken()
    {
        return token;
    }

    public void setToken(String token)
    {
        this.token = token;
    }

    public String getEncodingAesKey()
    {
        return encodingAesKey;
    }

    public void setEncodingAesKey(String encodingAesKey)
    {
        this.encodingAesKey = encodingAesKey;
    }

    public Boolean getWelcomeEnabled()
    {
        return welcomeEnabled;
    }

    public void setWelcomeEnabled(Boolean welcomeEnabled)
    {
        this.welcomeEnabled = welcomeEnabled;
    }

    public String getWelcomeText()
    {
        return welcomeText;
    }

    public void setWelcomeText(String welcomeText)
    {
        this.welcomeText = welcomeText;
    }

    public Long getDefaultLeadUserId()
    {
        return defaultLeadUserId;
    }

    public void setDefaultLeadUserId(Long defaultLeadUserId)
    {
        this.defaultLeadUserId = defaultLeadUserId;
    }

    public Long getDefaultLeadDeptId()
    {
        return defaultLeadDeptId;
    }

    public void setDefaultLeadDeptId(Long defaultLeadDeptId)
    {
        this.defaultLeadDeptId = defaultLeadDeptId;
    }

    public Boolean getClearDefaultLeadOwner()
    {
        return clearDefaultLeadOwner;
    }

    public void setClearDefaultLeadOwner(Boolean clearDefaultLeadOwner)
    {
        this.clearDefaultLeadOwner = clearDefaultLeadOwner;
    }
}
