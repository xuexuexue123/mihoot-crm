package com.ruoyi.crm.domain.vo;

import java.util.Map;

/**
 * POST /crm/record-trigger/match 响应 data[] 元素
 */
public class RecordTriggerMatchVO
{
    private String workflowId;

    private String nodeId;

    private String tenant;

    private Map<String, Object> nodeData;

    private WebhookCrmAuthVO crmAuth;

    public String getWorkflowId()
    {
        return workflowId;
    }

    public void setWorkflowId(String workflowId)
    {
        this.workflowId = workflowId;
    }

    public String getNodeId()
    {
        return nodeId;
    }

    public void setNodeId(String nodeId)
    {
        this.nodeId = nodeId;
    }

    public String getTenant()
    {
        return tenant;
    }

    public void setTenant(String tenant)
    {
        this.tenant = tenant;
    }

    public Map<String, Object> getNodeData()
    {
        return nodeData;
    }

    public void setNodeData(Map<String, Object> nodeData)
    {
        this.nodeData = nodeData;
    }

    public WebhookCrmAuthVO getCrmAuth()
    {
        return crmAuth;
    }

    public void setCrmAuth(WebhookCrmAuthVO crmAuth)
    {
        this.crmAuth = crmAuth;
    }
}
