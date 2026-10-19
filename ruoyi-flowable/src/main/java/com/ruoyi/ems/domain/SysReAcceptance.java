package com.ruoyi.ems.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 任务验收对象 sys_re_acceptance
 *
 * @author luohd
 * @date 2023-05-23
 */
public class SysReAcceptance extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 编码 */
    private String acceptId;

    /** 项目编号 */
    @Excel(name = "项目编号")
    private String projectId;

    /** 任务编号 */
    @Excel(name = "任务编号")
    private String taskId;

    /** 是否完成 */
    @Excel(name = "是否完成")
    private Long taskChecked;

    /** 描述 */
    @Excel(name = "描述")
    private String description;

    /** 排序 */
    @Excel(name = "排序")
    private Long taskOrder;

    public void setAcceptId(String acceptId)
    {
        this.acceptId = acceptId;
    }

    public String getAcceptId()
    {
        return acceptId;
    }
    public void setProjectId(String projectId)
    {
        this.projectId = projectId;
    }

    public String getProjectId()
    {
        return projectId;
    }
    public void setTaskId(String taskId)
    {
        this.taskId = taskId;
    }

    public String getTaskId()
    {
        return taskId;
    }
    public void setTaskChecked(Long taskChecked)
    {
        this.taskChecked = taskChecked;
    }

    public Long getTaskChecked()
    {
        return taskChecked;
    }
    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getDescription()
    {
        return description;
    }
    public void setTaskOrder(Long taskOrder)
    {
        this.taskOrder = taskOrder;
    }

    public Long getTaskOrder()
    {
        return taskOrder;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
                .append("acceptId", getAcceptId())
                .append("projectId", getProjectId())
                .append("taskId", getTaskId())
                .append("taskChecked", getTaskChecked())
                .append("description", getDescription())
                .append("createBy", getCreateBy())
                .append("createTime", getCreateTime())
                .append("updateBy", getUpdateBy())
                .append("updateTime", getUpdateTime())
                .append("taskOrder", getTaskOrder())
                .toString();
    }
}
