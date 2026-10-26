package com.ruoyi.ems.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 项目风险对象 sys_re_pmrisk
 * 
 * @author luohd
 * @date 2023-05-19
 */
public class SysRePmrisk extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 编码 */
    private String riskId;

    /** 项目Id */
    @Excel(name = "项目Id")
    private String projectId;

    /** 风险描述 */
    @Excel(name = "风险描述")
    private String riskTitle;

    /** 发生概率 */
    @Excel(name = "发生概率")
    private String riskRatio;

    /** 影响程度 */
    @Excel(name = "影响程度")
    private String impactLevel;

    /** 风险等级 */
    @Excel(name = "风险等级")
    private String riskLevel;

    /** 响应计划 */
    @Excel(name = "响应计划")
    private String riskPlan;

    /** 责任人 */
    @Excel(name = "责任人")
    private String owner;

    /** 状态 */
    @Excel(name = "状态")
    private Long status;

    /** 项目名 */
    @Excel(name = "项目名")
    private String projectName;

    public void setRiskId(String riskId) 
    {
        this.riskId = riskId;
    }

    public String getRiskId() 
    {
        return riskId;
    }
    public void setProjectId(String projectId) 
    {
        this.projectId = projectId;
    }

    public String getProjectId() 
    {
        return projectId;
    }
    public void setRiskTitle(String riskTitle) 
    {
        this.riskTitle = riskTitle;
    }

    public String getRiskTitle() 
    {
        return riskTitle;
    }
    public void setRiskRatio(String riskRatio) 
    {
        this.riskRatio = riskRatio;
    }

    public String getRiskRatio() 
    {
        return riskRatio;
    }
    public void setImpactLevel(String impactLevel) 
    {
        this.impactLevel = impactLevel;
    }

    public String getImpactLevel() 
    {
        return impactLevel;
    }
    public void setRiskLevel(String riskLevel) 
    {
        this.riskLevel = riskLevel;
    }

    public String getRiskLevel() 
    {
        return riskLevel;
    }
    public void setRiskPlan(String riskPlan) 
    {
        this.riskPlan = riskPlan;
    }

    public String getRiskPlan() 
    {
        return riskPlan;
    }
    public void setOwner(String owner) 
    {
        this.owner = owner;
    }

    public String getOwner() 
    {
        return owner;
    }
    public void setStatus(Long status) 
    {
        this.status = status;
    }

    public Long getStatus() 
    {
        return status;
    }

    public void setProjectName(String projectName)
    {
        this.projectName = projectName;
    }

    public String getProjectName()
    {
        return projectName;
    }
    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("riskId", getRiskId())
            .append("projectId", getProjectId())
            .append("riskTitle", getRiskTitle())
            .append("riskRatio", getRiskRatio())
            .append("impactLevel", getImpactLevel())
            .append("riskLevel", getRiskLevel())
            .append("riskPlan", getRiskPlan())
            .append("owner", getOwner())
            .append("status", getStatus())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
