package com.ruoyi.crm.whatsapp;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * WhatsApp 入站媒体持久化：Java 下载 Meta 后上传到 Next BFF（租户 AliOSS 集成）。
 */
@ConfigurationProperties(prefix = "whatsapp.media-persist")
public class WhatsAppMediaPersistProperties
{
    /** 关闭则跳过异步持久化，展示仍走 Meta 代理。 */
    private boolean enabled = true;

    /** sevencrm origin，无尾斜杠；默认同 app.public-url */
    private String nextAppUrl = "";

    /**
     * 与 Next {@code RECORD_TRIGGER_BFF_SECRET}/{@code WEBHOOK_BFF_SECRET} 一致；
     * 默认同 record-trigger.bff-secret。
     */
    private String bffSecret = "";

    public boolean isEnabled()
    {
        return enabled;
    }

    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }

    public String getNextAppUrl()
    {
        return nextAppUrl;
    }

    public void setNextAppUrl(String nextAppUrl)
    {
        this.nextAppUrl = nextAppUrl;
    }

    public String getBffSecret()
    {
        return bffSecret;
    }

    public void setBffSecret(String bffSecret)
    {
        this.bffSecret = bffSecret;
    }

    public boolean isReady()
    {
        return enabled
                && nextAppUrl != null && !nextAppUrl.trim().isEmpty()
                && bffSecret != null && !bffSecret.trim().isEmpty();
    }
}
