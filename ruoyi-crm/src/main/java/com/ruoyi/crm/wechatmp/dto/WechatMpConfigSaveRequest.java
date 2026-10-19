package com.ruoyi.crm.wechatmp.dto;

/**
 * 保存微信公众号租户配置（Secret 新建必填，更新可省略）
 */
public class WechatMpConfigSaveRequest
{
    private String appId;
    /** 新建必填；更新时省略表示保留原 Secret */
    private String secret;
    private String token;
    private String encodingAesKey;
    private Boolean verifySignature;

    public String getAppId()
    {
        return appId;
    }

    public void setAppId(String appId)
    {
        this.appId = appId;
    }

    public String getSecret()
    {
        return secret;
    }

    public void setSecret(String secret)
    {
        this.secret = secret;
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

    public Boolean getVerifySignature()
    {
        return verifySignature;
    }

    public void setVerifySignature(Boolean verifySignature)
    {
        this.verifySignature = verifySignature;
    }
}
