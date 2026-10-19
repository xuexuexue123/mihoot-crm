package com.ruoyi.crm.domain;

import java.math.BigDecimal;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 竞争对手对象 crm_competitor
 * 
 * @author luohd
 * @date 2024-02-07
 */
public class CrmCompetitor extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 编号 */
    private String competitorId;

    /** 名称 */
    @Excel(name = "名称")
    private String competitorName;

    /** 网址 */
    @Excel(name = "网址")
    private String website;

    /** 行业 */
    @Excel(name = "行业")
    private String industry;

    /** 员工数 */
    @Excel(name = "员工数")
    private Long employeeCount;

    /** 营业收入 */
    @Excel(name = "营业收入")
    private BigDecimal revenue;

    /** 描述 */
    @Excel(name = "描述")
    private String description;

    public void setCompetitorId(String competitorId) 
    {
        this.competitorId = competitorId;
    }

    public String getCompetitorId() 
    {
        return competitorId;
    }
    public void setCompetitorName(String competitorName) 
    {
        this.competitorName = competitorName;
    }

    public String getCompetitorName() 
    {
        return competitorName;
    }
    public void setWebsite(String website) 
    {
        this.website = website;
    }

    public String getWebsite() 
    {
        return website;
    }
    public void setIndustry(String industry) 
    {
        this.industry = industry;
    }

    public String getIndustry() 
    {
        return industry;
    }
    public void setEmployeeCount(Long employeeCount) 
    {
        this.employeeCount = employeeCount;
    }

    public Long getEmployeeCount() 
    {
        return employeeCount;
    }
    public void setRevenue(BigDecimal revenue) 
    {
        this.revenue = revenue;
    }

    public BigDecimal getRevenue() 
    {
        return revenue;
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
            .append("competitorId", getCompetitorId())
            .append("competitorName", getCompetitorName())
            .append("website", getWebsite())
            .append("industry", getIndustry())
            .append("employeeCount", getEmployeeCount())
            .append("revenue", getRevenue())
            .append("description", getDescription())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
