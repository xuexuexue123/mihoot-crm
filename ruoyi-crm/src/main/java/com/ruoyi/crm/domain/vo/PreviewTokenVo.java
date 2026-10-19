package com.ruoyi.crm.domain.vo;

public class PreviewTokenVo
{
    private String token;
    private String expiresAt;

    public PreviewTokenVo()
    {
    }

    public PreviewTokenVo(String token, String expiresAt)
    {
        this.token = token;
        this.expiresAt = expiresAt;
    }

    public String getToken()
    {
        return token;
    }

    public void setToken(String token)
    {
        this.token = token;
    }

    public String getExpiresAt()
    {
        return expiresAt;
    }

    public void setExpiresAt(String expiresAt)
    {
        this.expiresAt = expiresAt;
    }
}
