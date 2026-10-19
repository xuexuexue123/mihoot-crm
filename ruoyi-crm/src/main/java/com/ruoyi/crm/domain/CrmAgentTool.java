package com.ruoyi.crm.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * Agent Tool对象 crm_agent_tool
 * 
 * @author luohd
 * @date 2025-06-05
 */
public class CrmAgentTool extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** ID */
    private Long id;

    /** 标识 */
    @Excel(name = "标识")
    private String agentId;

    /** 名称 */
    @Excel(name = "名称")
    private String name;

    /** 描述 */
    @Excel(name = "描述")
    private String description;

    /** 分类 */
    @Excel(name = "分类")
    private String category;

    /** 模型名称 */
    @Excel(name = "模型名称")
    private String llmModel;

    /** 工具期望的输入示例 */
    @Excel(name = "工具期望的输入示例")
    private String inputExample;

    /** 工具预期的输出示例 */
    @Excel(name = "工具预期的输出示例")
    private String outputExample;

    /** 提示模板 */
    @Excel(name = "提示模板")
    private String promptTemplate;

    /** 是否启用 */
    @Excel(name = "是否启用")
    private Integer enabled;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date createdAt;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date updatedAt;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setAgentId(String agentId) 
    {
        this.agentId = agentId;
    }

    public String getAgentId() 
    {
        return agentId;
    }
    public void setName(String name) 
    {
        this.name = name;
    }

    public String getName() 
    {
        return name;
    }
    public void setDescription(String description) 
    {
        this.description = description;
    }

    public String getDescription() 
    {
        return description;
    }
    public void setCategory(String category) 
    {
        this.category = category;
    }

    public String getCategory() 
    {
        return category;
    }
    public void setLlmModel(String llmModel) 
    {
        this.llmModel = llmModel;
    }

    public String getLlmModel() 
    {
        return llmModel;
    }
    public void setInputExample(String inputExample) 
    {
        this.inputExample = inputExample;
    }

    public String getInputExample() 
    {
        return inputExample;
    }
    public void setOutputExample(String outputExample) 
    {
        this.outputExample = outputExample;
    }

    public String getOutputExample() 
    {
        return outputExample;
    }
    public void setPromptTemplate(String promptTemplate) 
    {
        this.promptTemplate = promptTemplate;
    }

    public String getPromptTemplate() 
    {
        return promptTemplate;
    }
    public void setEnabled(Integer enabled) 
    {
        this.enabled = enabled;
    }

    public Integer getEnabled() 
    {
        return enabled;
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
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("agentId", getAgentId())
            .append("name", getName())
            .append("description", getDescription())
            .append("category", getCategory())
            .append("llmModel", getLlmModel())
            .append("inputExample", getInputExample())
            .append("outputExample", getOutputExample())
            .append("promptTemplate", getPromptTemplate())
            .append("enabled", getEnabled())
            .append("createdAt", getCreatedAt())
            .append("updatedAt", getUpdatedAt())
            .toString();
    }
}
