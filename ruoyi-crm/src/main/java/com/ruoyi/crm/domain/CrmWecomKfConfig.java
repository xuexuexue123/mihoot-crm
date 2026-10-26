package com.ruoyi.crm.domain;

import java.util.Date;

/**
 * 微信客服租户配置（每租户库单行）。
 * <p>
 * Secret / Token / EncodingAESKey 复用自建应用（crm_wecom_config），本表仅存储 KF 特有的 webhook 转发和启用开关。
 */
public class CrmWecomKfConfig
{
    private Long id;
    private String corpId;
    private String webhookUrl;
    /** JSON 数组文本，如 ["wkxxx","wkyyy"] */
    private String openKfids;
    private Integer enabled;
    /** 绑定的工作流 ID */
    private String workflowId;
    private Date createdAt;
    private Date updatedAt;

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

    public Date getCreatedAt()
    {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt)
    {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt()
    {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt)
    {
        this.updatedAt = updatedAt;
    }
}
