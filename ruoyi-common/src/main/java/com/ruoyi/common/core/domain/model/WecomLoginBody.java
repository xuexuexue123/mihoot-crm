package com.ruoyi.common.core.domain.model;

/**
 * 企业微信登录请求体
 *
 * @author ruoyi
 */
public class WecomLoginBody
{
    /**
     * 企业微信 OAuth 授权码
     */
    private String code;

    /**
     * 登录 state（防 CSRF，原样传回）
     */
    private String state;

    /**
     * 租户标识
     */
    private String tenant;

    public String getCode()
    {
        return code;
    }

    public void setCode(String code)
    {
        this.code = code;
    }

    public String getState()
    {
        return state;
    }

    public void setState(String state)
    {
        this.state = state;
    }

    public String getTenant()
    {
        return tenant;
    }

    public void setTenant(String tenant)
    {
        this.tenant = tenant;
    }
}
