package com.ruoyi.crm.mobile.push;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Inbox 移动推送（FCM HTTP v1）配置。
 */
@ConfigurationProperties(prefix = "mobile-push")
public class MobilePushProperties
{
    /** 关闭时不调用 FCM；设备注册 API 仍可用。 */
    private boolean enabled = false;

    private final Fcm fcm = new Fcm();

    public boolean isEnabled()
    {
        return enabled;
    }

    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }

    public Fcm getFcm()
    {
        return fcm;
    }

    public boolean isReady()
    {
        return enabled
                && fcm.getProjectId() != null && !fcm.getProjectId().trim().isEmpty()
                && fcm.getCredentials() != null && !fcm.getCredentials().trim().isEmpty();
    }

    public static class Fcm
    {
        private String projectId = "";
        /** Service Account JSON 全文，或本地文件路径。 */
        private String credentials = "";

        public String getProjectId()
        {
            return projectId;
        }

        public void setProjectId(String projectId)
        {
            this.projectId = projectId;
        }

        public String getCredentials()
        {
            return credentials;
        }

        public void setCredentials(String credentials)
        {
            this.credentials = credentials;
        }
    }
}
