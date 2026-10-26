package com.ruoyi.crm.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.crm.domain.vo.WorkflowEdgeVO;
import com.ruoyi.crm.domain.vo.WorkflowNodeVO;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * WWorkflow对象 w_workflow
 * 
 * @author luohd
 * @date 2026-05-19
 */
public class WWorkflow extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    private String id;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private String name;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private Date createdAt;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private Date updatedAt;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private String userId;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private String organizationId;

    /** 派生：任一 MANUAL_TRIGGER publish.assistant 已 complete */
    private Boolean publishAssistant;

    /** 派生：任一 MANUAL_TRIGGER publish.wecomSidebar 已 complete */
    private Boolean publishSidebar;

    /** true=模板蓝图，false=可执行实例 */
    private Boolean isTemplate;

    /** 流程使用场景说明（非发布渠道 description） */
    private String description;

    /** React Flow 节点列表（查询时组装，非表字段） */
    private List<WorkflowNodeVO> nodes = new ArrayList<>();

    /** React Flow 边列表（查询时组装，非表字段） */
    private List<WorkflowEdgeVO> edges = new ArrayList<>();

    public void setId(String id)
    {
        this.id = id;
    }

    public String getId() 
    {
        return id;
    }
    public void setName(String name) 
    {
        this.name = name;
    }

    public String getName() 
    {
        return name;
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
    public void setUserId(String userId) 
    {
        this.userId = userId;
    }

    public String getUserId() 
    {
        return userId;
    }
    public void setOrganizationId(String organizationId) 
    {
        this.organizationId = organizationId;
    }

    public String getOrganizationId() 
    {
        return organizationId;
    }

    public Boolean getPublishAssistant()
    {
        return publishAssistant;
    }

    public void setPublishAssistant(Boolean publishAssistant)
    {
        this.publishAssistant = publishAssistant;
    }

    public Boolean getPublishSidebar()
    {
        return publishSidebar;
    }

    public void setPublishSidebar(Boolean publishSidebar)
    {
        this.publishSidebar = publishSidebar;
    }

    public Boolean getIsTemplate()
    {
        return isTemplate;
    }

    public void setIsTemplate(Boolean isTemplate)
    {
        this.isTemplate = isTemplate;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public List<WorkflowNodeVO> getNodes()
    {
        return nodes;
    }

    public void setNodes(List<WorkflowNodeVO> nodes)
    {
        this.nodes = nodes != null ? nodes : new ArrayList<>();
    }

    public List<WorkflowEdgeVO> getEdges()
    {
        return edges;
    }

    public void setEdges(List<WorkflowEdgeVO> edges)
    {
        this.edges = edges != null ? edges : new ArrayList<>();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("name", getName())
            .append("createdAt", getCreatedAt())
            .append("updatedAt", getUpdatedAt())
            .append("userId", getUserId())
            .append("organizationId", getOrganizationId())
            .append("publishAssistant", getPublishAssistant())
            .append("publishSidebar", getPublishSidebar())
            .append("isTemplate", getIsTemplate())
            .append("description", getDescription())
            .toString();
    }
}
