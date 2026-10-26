package com.ruoyi.ems.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 应用对象 sys_re_application
 * 
 * @author luohd
 * @date 2023-03-28
 */
public class SysReApplication extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 编码 */
    private String pyId;

    /** 中文名 */
    @Excel(name = "中文名")
    private String pyLabel;

    /** 名称 */
    @Excel(name = "名称")
    private String pyName;

    /** 组织 */
    @Excel(name = "组织")
    private Long organizationId;

    /** 业务描述 */
    @Excel(name = "业务描述")
    private String description;

    /** 业务目标 */
    private String businessObjectives;

    public void setPyId(String pyId) 
    {
        this.pyId = pyId;
    }

    public String getPyId() 
    {
        return pyId;
    }
    public void setPyLabel(String pyLabel) 
    {
        this.pyLabel = pyLabel;
    }

    public String getPyLabel() 
    {
        return pyLabel;
    }
    public void setPyName(String pyName) 
    {
        this.pyName = pyName;
    }

    public String getPyName() 
    {
        return pyName;
    }
    public void setOrganizationId(Long organizationId)
    {
        this.organizationId = organizationId;
    }

    public Long getOrganizationId()
    {
        return organizationId;
    }
    public void setDescription(String description) 
    {
        this.description = description;
    }

    public String getDescription() 
    {
        return description;
    }
    public void setBusinessObjectives(String businessObjectives) 
    {
        this.businessObjectives = businessObjectives;
    }

    public String getBusinessObjectives() 
    {
        return businessObjectives;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("pyId", getPyId())
            .append("pyLabel", getPyLabel())
            .append("pyName", getPyName())
            .append("organizationId", getOrganizationId())
            .append("description", getDescription())
            .append("businessObjectives", getBusinessObjectives())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
