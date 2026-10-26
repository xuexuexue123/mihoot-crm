package com.ruoyi.crm.wechatminiapp.dto;

/**
 * 保存微信小程序租户配置（Secret 新建必填，更新可省略以保留原密文）
 */
public class WechatMiniappConfigSaveRequest
{
    private String appId;
    /** 新建必填；更新时省略表示保留原 Secret */
    private String secret;
    private String name;

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

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }
}
