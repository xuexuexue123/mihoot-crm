package com.ruoyi.crm.domain;

import java.math.BigDecimal;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 合作伙伴对象 crm_partner
 * 
 * @author luohd
 * @date 2024-03-28
 */
public class CrmPartner extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 编号 */
    private String partnerId;

    /** 名称 */
    @Excel(name = "名称")
    private String name;

    /** 客户经理 */
    @Excel(name = "客户经理")
    private String owner;

    /** 客户经理ID */
    @Excel(name = "客户经理ID")
    private String ownerId;

    /** 联系人 */
    @Excel(name = "联系人")
    private String contactName;

    /** 移动电话 */
    @Excel(name = "移动电话")
    private String mobilePhone;

    /** 邮箱 */
    @Excel(name = "邮箱")
    private String workEmail;

    /** 网址 */
    @Excel(name = "网址")
    private String website;

    /** 区域 */
    @Excel(name = "区域")
    private String territory;

    /** 行业 */
    @Excel(name = "行业")
    private String industry;

    /** 员工数 */
    @Excel(name = "员工数")
    private Long employeeCount;

    /** 营业收入 */
    @Excel(name = "营业收入")
    private BigDecimal revenue;

    /** 漏斗中金额 */
    @Excel(name = "漏斗中金额")
    private BigDecimal pipelineOpen;

    /** 已赢单金额 */
    @Excel(name = "已赢单金额")
    private BigDecimal won;

    /** 任务金额 */
    @Excel(name = "任务金额")
    private BigDecimal quota;

    /** 任务完成比 */
    @Excel(name = "任务完成比")
    private BigDecimal quotaAttained;

    /** 描述 */
    @Excel(name = "描述")
    private String description;

    /** 删除标志 */
    private Long delFlag;

    public void setPartnerId(String partnerId) 
    {
        this.partnerId = partnerId;
    }

    public String getPartnerId() 
    {
        return partnerId;
    }
    public void setName(String name) 
    {
        this.name = name;
    }

    public String getName() 
    {
        return name;
    }
    public void setOwner(String owner) 
    {
        this.owner = owner;
    }

    public String getOwner() 
    {
        return owner;
    }
    public void setOwnerId(String ownerId) 
    {
        this.ownerId = ownerId;
    }

    public String getOwnerId() 
    {
        return ownerId;
    }
    public void setContactName(String contactName) 
    {
        this.contactName = contactName;
    }

    public String getContactName() 
    {
        return contactName;
    }
    public void setMobilePhone(String mobilePhone) 
    {
        this.mobilePhone = mobilePhone;
    }

    public String getMobilePhone() 
    {
        return mobilePhone;
    }
    public void setWorkEmail(String workEmail) 
    {
        this.workEmail = workEmail;
    }

    public String getWorkEmail() 
    {
        return workEmail;
    }
    public void setWebsite(String website) 
    {
        this.website = website;
    }

    public String getWebsite() 
    {
        return website;
    }
    public void setTerritory(String territory) 
    {
        this.territory = territory;
    }

    public String getTerritory() 
    {
        return territory;
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
    public void setPipelineOpen(BigDecimal pipelineOpen) 
    {
        this.pipelineOpen = pipelineOpen;
    }

    public BigDecimal getPipelineOpen() 
    {
        return pipelineOpen;
    }
    public void setWon(BigDecimal won) 
    {
        this.won = won;
    }

    public BigDecimal getWon() 
    {
        return won;
    }
    public void setQuota(BigDecimal quota) 
    {
        this.quota = quota;
    }

    public BigDecimal getQuota() 
    {
        return quota;
    }
    public void setQuotaAttained(BigDecimal quotaAttained) 
    {
        this.quotaAttained = quotaAttained;
    }

    public BigDecimal getQuotaAttained() 
    {
        return quotaAttained;
    }
    public void setDescription(String description) 
    {
        this.description = description;
    }

    public String getDescription() 
    {
        return description;
    }
    public void setDelFlag(Long delFlag) 
    {
        this.delFlag = delFlag;
    }

    public Long getDelFlag() 
    {
        return delFlag;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("partnerId", getPartnerId())
            .append("name", getName())
            .append("owner", getOwner())
            .append("ownerId", getOwnerId())
            .append("contactName", getContactName())
            .append("mobilePhone", getMobilePhone())
            .append("workEmail", getWorkEmail())
            .append("website", getWebsite())
            .append("territory", getTerritory())
            .append("industry", getIndustry())
            .append("employeeCount", getEmployeeCount())
            .append("revenue", getRevenue())
            .append("pipelineOpen", getPipelineOpen())
            .append("won", getWon())
            .append("quota", getQuota())
            .append("quotaAttained", getQuotaAttained())
            .append("description", getDescription())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("delFlag", getDelFlag())
            .toString();
    }
}
