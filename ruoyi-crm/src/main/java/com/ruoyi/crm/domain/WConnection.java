package com.ruoyi.crm.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * WConnection对象 w_connection
 * 
 * @author luohd
 * @date 2026-05-19
 */
public class WConnection extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private String id;

    @Excel(name = "工作流ID")
    private String workflowId;

    @Excel(name = "源节点ID")
    private String fromNodeId;

    @Excel(name = "目标节点ID")
    private String toNodeId;

    @Excel(name = "源输出")
    private String fromOutput;

    @Excel(name = "目标输入")
    private String toInput;

    @Excel(name = "类型")
    private String type;

    @Excel(name = "标签")
    private String label;

    private String data;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date updatedAt;

    public void setId(String id)
    {
        this.id = id;
    }

    public String getId()
    {
        return id;
    }

    public void setWorkflowId(String workflowId)
    {
        this.workflowId = workflowId;
    }

    public String getWorkflowId()
    {
        return workflowId;
    }

    public void setFromNodeId(String fromNodeId)
    {
        this.fromNodeId = fromNodeId;
    }

    public String getFromNodeId()
    {
        return fromNodeId;
    }

    public void setToNodeId(String toNodeId)
    {
        this.toNodeId = toNodeId;
    }

    public String getToNodeId()
    {
        return toNodeId;
    }

    public void setFromOutput(String fromOutput)
    {
        this.fromOutput = fromOutput;
    }

    public String getFromOutput()
    {
        return fromOutput;
    }

    public void setToInput(String toInput)
    {
        this.toInput = toInput;
    }

    public String getToInput()
    {
        return toInput;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public String getType()
    {
        return type;
    }

    public void setLabel(String label)
    {
        this.label = label;
    }

    public String getLabel()
    {
        return label;
    }

    public void setData(String data)
    {
        this.data = data;
    }

    public String getData()
    {
        return data;
    }

    public void setCreatedAt(Date createdAt)
    {
        this.createdAt = createdAt;
    }

    public Date getCreatedAt()
    {
        return createdAt;
    }

    public void setUpdatedAt(Date updatedAt)
    {
        this.updatedAt = updatedAt;
    }

    public Date getUpdatedAt()
    {
        return updatedAt;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("workflowId", getWorkflowId())
            .append("fromNodeId", getFromNodeId())
            .append("toNodeId", getToNodeId())
            .append("fromOutput", getFromOutput())
            .append("toInput", getToInput())
            .append("type", getType())
            .append("label", getLabel())
            .append("data", getData())
            .append("createdAt", getCreatedAt())
            .append("updatedAt", getUpdatedAt())
            .toString();
    }
}
