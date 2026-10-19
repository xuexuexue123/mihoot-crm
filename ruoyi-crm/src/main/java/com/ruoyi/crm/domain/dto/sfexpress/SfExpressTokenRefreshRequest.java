package com.ruoyi.crm.domain.dto.sfexpress;

/**
 * 刷新顺丰 OAuth2 accessToken
 */
public class SfExpressTokenRefreshRequest
{
    /** true 强制向顺丰换票；false/省略时仅在临近过期时刷新 */
    private Boolean force;

    public Boolean getForce()
    {
        return force;
    }

    public void setForce(Boolean force)
    {
        this.force = force;
    }
}
