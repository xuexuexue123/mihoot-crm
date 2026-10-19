package com.ruoyi.crm.domain;

import java.math.BigDecimal;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 客户对象 crm_account
 * 
 * @author luohd
 * @date 2024-01-30
 */
public class CrmAccount extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 编号 */
    private String accountId;

    /** 客户类型 */
    @Excel(name = "客户类型")
    private String accountType;

    /** 编号 */
//    @Excel(name = "编号")
    private String leadId;

    /** 线索名 */
    @Excel(name = "线索名")
    private String leadName;

    /** 机构编号 */
//    @Excel(name = "机构编号")
    private String orgId;

    /** 名称 */
    @Excel(name = "名称")
    private String name;

    /** 简称 */
    @Excel(name = "简称")
    private String shortName;

    /** 编号 */
    @Excel(name = "编号")
    private String code;

    /** 机构名称 */
    @Excel(name = "机构名称")
    private String fullName;

    /** 行业 */
    @Excel(name = "行业")
    private String industry;

    /** 电话 */
    @Excel(name = "电话")
    private String phoneNumber;

    /** 区域 */
    @Excel(name = "区域")
    private String territory;

    /** 客户经理 */
    @Excel(name = "客户经理")
    private String owner;

    /** 员工数 */
    @Excel(name = "员工数")
    private Long employeeCount;

    /** 网址 */
    @Excel(name = "网址")
    private String website;

    /** 活动机会数 */
    @Excel(name = "活动机会数")
    private Long activeOptyCount;

    /** 联系人数 */
    @Excel(name = "联系人数")
    private Long activeContactCount;

    /** 线索数 */
    @Excel(name = "线索数")
    private Long activeLeadCount;

    /** 活动机会金额 */
    @Excel(name = "活动机会金额")
    private BigDecimal activeOptyAmount;

    /** 营业收入 */
    @Excel(name = "营业收入")
    private BigDecimal revenue;

    /** 是否目标 （Y是 N否） */
    @Excel(name = "是否目标 ", readConverterExp = "Y=是,N=否")
    private String isTarget;

    /** 状态 */
    @Excel(name = "状态")
    private String status;

    /** 描述 */
    @Excel(name = "描述")
    private String description;

    /** 用户ID */
    @Excel(name = "用户ID")
    private Long userId;

    /** 部门ID */
    @Excel(name = "部门ID")
    private Long deptId;

    /** 注册地 */
    @Excel(name = "注册地")
    private String location;

    /** 地址 */
    @Excel(name = "地址")
    private String address;

    /** 基金币种 */
    @Excel(name = "币种")
    private String currency;

    /** 自定义 */
    @Excel(name = "自定义")
    private String customField;

    /** 不活跃数 */
    @Excel(name = "不活跃数")
    private Long inactiveOptyCount;


    public void setAccountId(String accountId) 
    {
        this.accountId = accountId;
    }

    public String getAccountId() 
    {
        return accountId;
    }
    public void setAccountType(String accountType) 
    {
        this.accountType = accountType;
    }

    public String getAccountType() 
    {
        return accountType;
    }
    public void setLeadId(String leadId) 
    {
        this.leadId = leadId;
    }

    public String getLeadId() 
    {
        return leadId;
    }
    public void setLeadName(String leadName) 
    {
        this.leadName = leadName;
    }

    public String getLeadName() 
    {
        return leadName;
    }
    public void setOrgId(String orgId) 
    {
        this.orgId = orgId;
    }

    public String getOrgId() 
    {
        return orgId;
    }
    public void setName(String name) 
    {
        this.name = name;
    }

    public String getName() 
    {
        return name;
    }
    public void setShortName(String shortName) 
    {
        this.shortName = shortName;
    }

    public String getShortName() 
    {
        return shortName;
    }

    public void setCode(String code)
    {
        this.code = code;
    }

    public String getCode()
    {
        return code;
    }

    public void setFullName(String fullName) 
    {
        this.fullName = fullName;
    }

    public String getFullName() 
    {
        return fullName;
    }
    public void setIndustry(String industry) 
    {
        this.industry = industry;
    }

    public String getIndustry() 
    {
        return industry;
    }
    public void setPhoneNumber(String phoneNumber) 
    {
        this.phoneNumber = phoneNumber;
    }

    public String getPhoneNumber() 
    {
        return phoneNumber;
    }
    public void setTerritory(String territory) 
    {
        this.territory = territory;
    }

    public String getTerritory() 
    {
        return territory;
    }
    public void setOwner(String owner) 
    {
        this.owner = owner;
    }

    public String getOwner() 
    {
        return owner;
    }
    public void setEmployeeCount(Long employeeCount) 
    {
        this.employeeCount = employeeCount;
    }

    public Long getEmployeeCount() 
    {
        return employeeCount;
    }
    public void setWebsite(String website) 
    {
        this.website = website;
    }

    public String getWebsite() 
    {
        return website;
    }
    public void setActiveOptyCount(Long activeOptyCount) 
    {
        this.activeOptyCount = activeOptyCount;
    }

    public Long getActiveOptyCount() 
    {
        return activeOptyCount;
    }
    public void setActiveContactCount(Long activeContactCount) 
    {
        this.activeContactCount = activeContactCount;
    }

    public Long getActiveContactCount() 
    {
        return activeContactCount;
    }
    public void setActiveLeadCount(Long activeLeadCount) 
    {
        this.activeLeadCount = activeLeadCount;
    }

    public Long getActiveLeadCount() 
    {
        return activeLeadCount;
    }
    public void setActiveOptyAmount(BigDecimal activeOptyAmount) 
    {
        this.activeOptyAmount = activeOptyAmount;
    }

    public BigDecimal getActiveOptyAmount() 
    {
        return activeOptyAmount;
    }
    public void setRevenue(BigDecimal revenue) 
    {
        this.revenue = revenue;
    }

    public BigDecimal getRevenue() 
    {
        return revenue;
    }
    public void setIsTarget(String isTarget) 
    {
        this.isTarget = isTarget;
    }

    public String getIsTarget() 
    {
        return isTarget;
    }
    public void setStatus(String status) 
    {
        this.status = status;
    }

    public String getStatus() 
    {
        return status;
    }
    public void setDescription(String description) 
    {
        this.description = description;
    }

    public String getDescription() 
    {
        return description;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getDeptId() {
        return deptId;
    }

    public void setDeptId(Long deptId) {
        this.deptId = deptId;
    }

    public void setLocation(String location)
    {
        this.location = location;
    }

    public String getLocation()
    {
        return location;
    }
    public void setAddress(String address)
    {
        this.address = address;
    }

    public String getAddress()
    {
        return address;
    }
    public void setCurrency(String currency)
    {
        this.currency = currency;
    }

    public String getCurrency()
    {
        return currency;
    }
    public void setCustomField(String customField)
    {
        this.customField = customField;
    }

    public String getCustomField()
    {
        return customField;
    }
    public void setInactiveOptyCount(Long inactiveOptyCount)
    {
        this.inactiveOptyCount = inactiveOptyCount;
    }

    public Long getInactiveOptyCount()
    {
        return inactiveOptyCount;
    }
    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("accountId", getAccountId())
            .append("accountType", getAccountType())
            .append("leadId", getLeadId())
            .append("leadName", getLeadName())
            .append("orgId", getOrgId())
            .append("name", getName())
            .append("shortName", getShortName())
            .append("fullName", getFullName())
            .append("industry", getIndustry())
            .append("phoneNumber", getPhoneNumber())
            .append("territory", getTerritory())
            .append("owner", getOwner())
            .append("employeeCount", getEmployeeCount())
            .append("website", getWebsite())
            .append("activeOptyCount", getActiveOptyCount())
            .append("activeContactCount", getActiveContactCount())
            .append("activeLeadCount", getActiveLeadCount())
            .append("activeOptyAmount", getActiveOptyAmount())
            .append("revenue", getRevenue())
            .append("isTarget", getIsTarget())
            .append("status", getStatus())
            .append("description", getDescription())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
