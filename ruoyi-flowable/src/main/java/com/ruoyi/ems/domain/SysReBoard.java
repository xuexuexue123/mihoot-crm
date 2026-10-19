package com.ruoyi.ems.domain;

import com.ruoyi.common.core.domain.entity.SysRole;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

import java.util.List;

/**
 * 项目阶段对象 sys_re_board
 * 
 * @author luohd
 * @date 2023-04-07
 */
public class SysReBoard extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 编码 */
    private String pyId;

    /** 业务Id */
    @Excel(name = "业务Id")
    private String projectId;

    /** 中文名 */
    @Excel(name = "中文名")
    private String pyLabel;

    /** 颜色 */
    @Excel(name = "颜色")
    private String headerColor;

    /** 是否归档 */
    @Excel(name = "是否归档")
    private Boolean archived;

    /** 排序 */
    @Excel(name = "排序")
    private Long stageSort;

    private List<SysReWorktask> items;

    public void setPyId(String pyId) 
    {
        this.pyId = pyId;
    }

    public String getPyId() 
    {
        return pyId;
    }
    public void setProjectId(String projectId) 
    {
        this.projectId = projectId;
    }

    public String getProjectId() 
    {
        return projectId;
    }
    public void setPyLabel(String pyLabel) 
    {
        this.pyLabel = pyLabel;
    }

    public String getPyLabel() 
    {
        return pyLabel;
    }
    public void setHeaderColor(String headerColor) 
    {
        this.headerColor = headerColor;
    }

    public String getHeaderColor() 
    {
        return headerColor;
    }
    public void setArchived(Boolean archived) 
    {
        this.archived = archived;
    }

    public Boolean getArchived() 
    {
        return archived;
    }

    public void setStageSort(Long stageSort)
    {
        this.stageSort = stageSort;
    }

    public Long getStageSort()
    {
        return stageSort;
    }

    public List<SysReWorktask> getItems() {
        return items;
    }

    public void setItems(List<SysReWorktask> items) {
        this.items = items;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("pyId", getPyId())
            .append("projectId", getProjectId())
            .append("pyLabel", getPyLabel())
            .append("headerColor", getHeaderColor())
            .append("archived", getArchived())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
                .append("stageSort", getStageSort())
            .toString();
    }
}
