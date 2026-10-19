package com.ruoyi.crm.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 机会任务对象 crm_optytask
 * 
 * @author luohd
 * @date 2024-03-05
 */
public class CrmOptytask extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 编码 */
    private String taskId;

    /** 机会编码 */
    @Excel(name = "机会编码")
    private String optyId;

    /** 名称 */
    @Excel(name = "名称")
    private String optyName;

    /** 阶段编号 */
    @Excel(name = "阶段编号")
    private String stageId;

    /** 阶段名称 */
    @Excel(name = "阶段名称")
    private String stageName;

    /** 任务名称 */
    @Excel(name = "任务名称")
    private String taskName;

    /** 流程ID */
    @Excel(name = "流程ID")
    private String casetypeId;

    /** 是否完成 */
    @Excel(name = "是否完成")
    private Long taskChecked;

    /** 排序 */
    @Excel(name = "排序")
    private Long taskOrder;

    /** 描述 */
    @Excel(name = "描述")
    private String description;

    public void setTaskId(String taskId) 
    {
        this.taskId = taskId;
    }

    public String getTaskId() 
    {
        return taskId;
    }
    public void setOptyId(String optyId) 
    {
        this.optyId = optyId;
    }

    public String getOptyId() 
    {
        return optyId;
    }
    public void setOptyName(String optyName) 
    {
        this.optyName = optyName;
    }

    public String getOptyName() 
    {
        return optyName;
    }
    public void setStageId(String stageId) 
    {
        this.stageId = stageId;
    }

    public String getStageId() 
    {
        return stageId;
    }
    public void setStageName(String stageName) 
    {
        this.stageName = stageName;
    }

    public String getStageName() 
    {
        return stageName;
    }
    public void setTaskName(String taskName) 
    {
        this.taskName = taskName;
    }

    public String getTaskName() 
    {
        return taskName;
    }
    public void setCasetypeId(String casetypeId) 
    {
        this.casetypeId = casetypeId;
    }

    public String getCasetypeId() 
    {
        return casetypeId;
    }
    public void setTaskChecked(Long taskChecked) 
    {
        this.taskChecked = taskChecked;
    }

    public Long getTaskChecked() 
    {
        return taskChecked;
    }
    public void setTaskOrder(Long taskOrder) 
    {
        this.taskOrder = taskOrder;
    }

    public Long getTaskOrder() 
    {
        return taskOrder;
    }
    public void setDescription(String description) 
    {
        this.description = description;
    }

    public String getDescription() 
    {
        return description;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("taskId", getTaskId())
            .append("optyId", getOptyId())
            .append("optyName", getOptyName())
            .append("stageId", getStageId())
            .append("stageName", getStageName())
            .append("taskName", getTaskName())
            .append("casetypeId", getCasetypeId())
            .append("taskChecked", getTaskChecked())
            .append("taskOrder", getTaskOrder())
            .append("description", getDescription())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
