package com.ruoyi.crm.domain.dto.wecom;

/**
 * 保存微信客服租户配置。
 * <p>
 * Secret / Token / EncodingAESKey 复用自建应用（crm_wecom_config），前端只需传入 KF 特有字段。
 */
public class WecomKfConfigSaveRequest
{
    private String webhookUrl;
    /** JSON 数组 ["wkxxx","wkyyy"] */
    private String openKfids;
    private Integer enabled;
    /** 绑定的工作流 ID（可选） */
    private String workflowId;

    public String getWebhookUrl()
    {
        return webhookUrl;
    }

    public void setWebhookUrl(String webhookUrl)
    {
        this.webhookUrl = webhookUrl;
    }

    public String getOpenKfids()
    {
        return openKfids;
    }

    public void setOpenKfids(String openKfids)
    {
        this.openKfids = openKfids;
    }

    public Integer getEnabled()
    {
        return enabled;
    }

    public void setEnabled(Integer enabled)
    {
        this.enabled = enabled;
    }

    public String getWorkflowId()
    {
        return workflowId;
    }

    public void setWorkflowId(String workflowId)
    {
        this.workflowId = workflowId;
    }
}
