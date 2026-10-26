package com.ruoyi.crm.domain.vo;

/**
 * 活动 H5 报名链接签发响应
 */
public class EventRegistrationUrlVo
{
    private String registrationUrl;

    private String expiresAt;

    public String getRegistrationUrl()
    {
        return registrationUrl;
    }

    public void setRegistrationUrl(String registrationUrl)
    {
        this.registrationUrl = registrationUrl;
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
