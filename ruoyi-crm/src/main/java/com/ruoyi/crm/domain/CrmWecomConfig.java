package com.ruoyi.crm.domain;

import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 企业微信租户配置（每租户库单行）
 */
public class CrmWecomConfig extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long id;
    private String corpId;
    private String agentId;
    /** AES 密文，不落日志 */
    private String agentSecretEnc;
    private String callbackToken;
    private String encodingAesKey;
    private Boolean connected;
    private String lastError;
    private Boolean welcomeEnabled;
    private String welcomeText;
    /** sn_userid 未命中时兜底负责人 */
    private Long defaultLeadUserId;
    /** 可选；为空则用负责人部门 */
    private Long defaultLeadDeptId;
    /** 仅用于 MyBatis update，非表字段 */
    private Boolean defaultLeadOwnerDirty;

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

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

    public String getAgentSecretEnc()
    {
        return agentSecretEnc;
    }

    public void setAgentSecretEnc(String agentSecretEnc)
    {
        this.agentSecretEnc = agentSecretEnc;
    }

    public String getCallbackToken()
    {
        return callbackToken;
    }

    public void setCallbackToken(String callbackToken)
    {
        this.callbackToken = callbackToken;
    }

    public String getEncodingAesKey()
    {
        return encodingAesKey;
    }

    public void setEncodingAesKey(String encodingAesKey)
    {
        this.encodingAesKey = encodingAesKey;
    }

    public Boolean getConnected()
    {
        return connected;
    }

    public void setConnected(Boolean connected)
    {
        this.connected = connected;
    }

    public String getLastError()
    {
        return lastError;
    }

    public void setLastError(String lastError)
    {
        this.lastError = lastError;
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

    public Boolean getDefaultLeadOwnerDirty()
    {
        return defaultLeadOwnerDirty;
    }

    public void setDefaultLeadOwnerDirty(Boolean defaultLeadOwnerDirty)
    {
        this.defaultLeadOwnerDirty = defaultLeadOwnerDirty;
    }
}
