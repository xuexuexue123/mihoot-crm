package com.ruoyi.crm.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 机构对象 crm_org
 * 
 * @author luohd
 * @date 2024-02-22
 */
public class CrmOrg extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 编号 */
    private String orgId;

    /** 组织logo */
    @Excel(name = "组织logo")
    private String avatar;

    /** 组织名称 */
    @Excel(name = "组织名称")
    private String fullName;

    /** 类型 */
    @Excel(name = "类型")
    private String category;

    /** 注册资本 */
    @Excel(name = "注册资本")
    private BigDecimal registerCapital;

    /** 法人代表 */
    @Excel(name = "法人代表")
    private String representative;


    /** 统一社会信用代码 */
    @Excel(name = "统一社会信用代码")
    private String uuid;

    /** 成立日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "成立日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date startupDate;

    /** 核准日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "核准日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date approvedDate;

    /** 营业起始日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "营业起始日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date operStartDate;

    /** 登记机关 */
    @Excel(name = "登记机关")
    private String registerBureau;

    /** 登记状态 */
    @Excel(name = "登记状态")
    private String registerStatus;

    /** 登记地址 */
    @Excel(name = "登记地址")
    private String registerAddress;

    /** 简称 */
    @Excel(name = "简称")
    private String shortName;

    /** 上层名称 */
    @Excel(name = "上层名称")
    private String parentOrg;

    /** 网站 */
    @Excel(name = "网站")
    private String website;

    /** 上市代码 */
    @Excel(name = "上市代码")
    private String tickerSymbol;

    /** 电话 */
    @Excel(name = "电话")
    private String phoneNumber;

    /** 行业 */
    @Excel(name = "行业")
    private String industry;

    /** 区域 */
    @Excel(name = "区域")
    private String territory;

    /** 客户经理 */
    @Excel(name = "客户经理")
    private String owner;

    /** 员工数 */
    @Excel(name = "员工数")
    private Long employeeCount;

    /** 活动客户数 */
    @Excel(name = "活动客户数")
    private Long activeAccountCount;

    /** 活动机会数 */
    @Excel(name = "活动机会数")
    private Long activeOptyCount;

    /** 活动机会金额 */
    @Excel(name = "活动机会金额")
    private BigDecimal activeOptyAmount;

    /** 营业收入 */
    @Excel(name = "营业收入")
    private BigDecimal revenue;

    /** 经营范围 */
    @Excel(name = "经营范围")
    private String registerComments;

    /** 是否目标 （Y是 N否） */
    @Excel(name = "是否目标 ", readConverterExp = "Y=是,N=否")
    private String isTarget;

    /** 是否客户 （Y是 N否） */
    @Excel(name = "是否客户 ", readConverterExp = "Y=是,N=否")
    private String isCustomer;

    /** 状态 */
    @Excel(name = "状态")
    private String status;

    /** 自定义字段 */
    @Excel(name = "自定义字段")
    private String customField;

    private Long userId;

    private Long deptId;
    /** 资源 */
    @Excel(name = "资源")
    private String resources;

    /** 需求 */
    @Excel(name = "需求")
    private String requirement;

    /** 标签 */
    @Excel(name = "标签")
    private String tag;

    /** 英文名 */
    @Excel(name = "英文名")
    private String englishName;

    /** 股东信息 */
    @Excel(name = "股东信息")
    private String stakeholders;

    /** 变更信息 */
    @Excel(name = "变更信息")
    private String changes;

    /** 资源需求说明 */
    @Excel(name = "资源需求说明")
    private String rrDescription;

    /** 创始人 */
    @Excel(name = "创始人")
    private String funders;

    /** 创始人职位 */
    @Excel(name = "创始人职位")
    private String fundersTitle;

    /** 融资轮数 */
    @Excel(name = "融资轮数")
    private Long numberOfFundingRounds;

    /** 最后轮次 */
    @Excel(name = "最后轮次")
    private String lastFundingType;

    /** 最后融资日 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "最后融资日", width = 30, dateFormat = "yyyy-MM-dd")
    private Date lastFundingDate;

    /** 最后融资金额 */
    @Excel(name = "最后融资金额")
    private BigDecimal lastFundingAmount;

    /** 总融资额 */
    @Excel(name = "总融资额")
    private BigDecimal totalFundingAmount;

    /** 币种 */
    @Excel(name = "币种")
    private String fundingCurrency;

    /** 一句话介绍 */
    @Excel(name = "一句话介绍")
    private String shortDescription;

    /** 最后融资币种 */
    @Excel(name = "最后融资币种")
    private String lastFundingCurrency;

    /** 前五投资者 */
    @Excel(name = "前五投资者")
    private String topFiveInvestors;

    /** 领投人数 */
    @Excel(name = "领投人数")
    private Long numberOfLeadInvestors;

    /** 投资人数 */
    @Excel(name = "投资人数")
    private Long numberOfInvestors;

    /** 收购数 */
    @Excel(name = "收购数")
    private Long numberOfAcquisitions;

    /** 收购状态 */
    @Excel(name = "收购状态")
    private String acquisitionStatus;

    /** IPO状态 */
    @Excel(name = "IPO状态")
    private String ipoStatus;

    /** IPO日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "IPO日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date ipoDate;

    /** 投资者流程 */
    @Excel(name = "投资者流程")
    private String lpProcess;

    /** 投资人阶段 */
    @Excel(name = "投资人阶段")
    private String stage;

    /** 最新估值 */
    @Excel(name = "最新估值")
    private BigDecimal latestValue;

    public void setOrgId(String orgId) 
    {
        this.orgId = orgId;
    }

    public String getOrgId() 
    {
        return orgId;
    }
    public void setUuid(String uuid) 
    {
        this.uuid = uuid;
    }

    public String getUuid() 
    {
        return uuid;
    }
    public void setCategory(String category) 
    {
        this.category = category;
    }

    public String getCategory() 
    {
        return category;
    }
    public void setRegisterCapital(BigDecimal registerCapital) 
    {
        this.registerCapital = registerCapital;
    }

    public BigDecimal getRegisterCapital() 
    {
        return registerCapital;
    }
    public void setFullName(String fullName) 
    {
        this.fullName = fullName;
    }

    public String getFullName() 
    {
        return fullName;
    }
    public void setRepresentative(String representative) 
    {
        this.representative = representative;
    }

    public String getRepresentative() 
    {
        return representative;
    }
    public void setStartupDate(Date startupDate) 
    {
        this.startupDate = startupDate;
    }

    public Date getStartupDate() 
    {
        return startupDate;
    }
    public void setApprovedDate(Date approvedDate) 
    {
        this.approvedDate = approvedDate;
    }

    public Date getApprovedDate() 
    {
        return approvedDate;
    }
    public void setOperStartDate(Date operStartDate) 
    {
        this.operStartDate = operStartDate;
    }

    public Date getOperStartDate() 
    {
        return operStartDate;
    }
    public void setRegisterBureau(String registerBureau) 
    {
        this.registerBureau = registerBureau;
    }

    public String getRegisterBureau() 
    {
        return registerBureau;
    }
    public void setRegisterStatus(String registerStatus) 
    {
        this.registerStatus = registerStatus;
    }

    public String getRegisterStatus() 
    {
        return registerStatus;
    }
    public void setRegisterAddress(String registerAddress) 
    {
        this.registerAddress = registerAddress;
    }

    public String getRegisterAddress() 
    {
        return registerAddress;
    }
    public void setShortName(String shortName) 
    {
        this.shortName = shortName;
    }

    public String getShortName() 
    {
        return shortName;
    }
    public void setParentOrg(String parentOrg) 
    {
        this.parentOrg = parentOrg;
    }

    public String getParentOrg() 
    {
        return parentOrg;
    }
    public void setWebsite(String website) 
    {
        this.website = website;
    }

    public String getWebsite() 
    {
        return website;
    }
    public void setAvatar(String avatar) 
    {
        this.avatar = avatar;
    }

    public String getAvatar() 
    {
        return avatar;
    }
    public void setTickerSymbol(String tickerSymbol) 
    {
        this.tickerSymbol = tickerSymbol;
    }

    public String getTickerSymbol() 
    {
        return tickerSymbol;
    }
    public void setPhoneNumber(String phoneNumber) 
    {
        this.phoneNumber = phoneNumber;
    }

    public String getPhoneNumber() 
    {
        return phoneNumber;
    }
    public void setIndustry(String industry) 
    {
        this.industry = industry;
    }

    public String getIndustry() 
    {
        return industry;
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
    public void setActiveAccountCount(Long activeAccountCount) 
    {
        this.activeAccountCount = activeAccountCount;
    }

    public Long getActiveAccountCount() 
    {
        return activeAccountCount;
    }
    public void setActiveOptyCount(Long activeOptyCount) 
    {
        this.activeOptyCount = activeOptyCount;
    }

    public Long getActiveOptyCount() 
    {
        return activeOptyCount;
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
    public void setRegisterComments(String registerComments) 
    {
        this.registerComments = registerComments;
    }

    public String getRegisterComments() 
    {
        return registerComments;
    }
    public void setIsTarget(String isTarget) 
    {
        this.isTarget = isTarget;
    }

    public String getIsTarget() 
    {
        return isTarget;
    }
    public void setIsCustomer(String isCustomer) 
    {
        this.isCustomer = isCustomer;
    }

    public String getIsCustomer() 
    {
        return isCustomer;
    }
    public void setStatus(String status) 
    {
        this.status = status;
    }

    public String getStatus() 
    {
        return status;
    }
    public void setCustomField(String customField) 
    {
        this.customField = customField;
    }

    public String getCustomField() 
    {
        return customField;
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

    public void setResources(String resources)
    {
        this.resources = resources;
    }

    public String getResources()
    {
        return resources;
    }
    public void setRequirement(String requirement)
    {
        this.requirement = requirement;
    }

    public String getRequirement()
    {
        return requirement;
    }
    public void setTag(String tag)
    {
        this.tag = tag;
    }

    public String getTag()
    {
        return tag;
    }
    public void setEnglishName(String englishName)
    {
        this.englishName = englishName;
    }

    public String getEnglishName()
    {
        return englishName;
    }
    public void setStakeholders(String stakeholders)
    {
        this.stakeholders = stakeholders;
    }

    public String getStakeholders()
    {
        return stakeholders;
    }
    public void setChanges(String changes)
    {
        this.changes = changes;
    }

    public String getChanges()
    {
        return changes;
    }
    public void setRrDescription(String rrDescription)
    {
        this.rrDescription = rrDescription;
    }

    public String getRrDescription()
    {
        return rrDescription;
    }

    public void setFunders(String funders)
    {
        this.funders = funders;
    }

    public String getFunders()
    {
        return funders;
    }
    public void setFundersTitle(String fundersTitle)
    {
        this.fundersTitle = fundersTitle;
    }

    public String getFundersTitle()
    {
        return fundersTitle;
    }
    public void setNumberOfFundingRounds(Long numberOfFundingRounds)
    {
        this.numberOfFundingRounds = numberOfFundingRounds;
    }

    public Long getNumberOfFundingRounds()
    {
        return numberOfFundingRounds;
    }
    public void setLastFundingType(String lastFundingType)
    {
        this.lastFundingType = lastFundingType;
    }

    public String getLastFundingType()
    {
        return lastFundingType;
    }
    public void setLastFundingDate(Date lastFundingDate)
    {
        this.lastFundingDate = lastFundingDate;
    }

    public Date getLastFundingDate()
    {
        return lastFundingDate;
    }
    public void setLastFundingAmount(BigDecimal lastFundingAmount)
    {
        this.lastFundingAmount = lastFundingAmount;
    }

    public BigDecimal getLastFundingAmount()
    {
        return lastFundingAmount;
    }
    public void setTotalFundingAmount(BigDecimal totalFundingAmount)
    {
        this.totalFundingAmount = totalFundingAmount;
    }

    public BigDecimal getTotalFundingAmount()
    {
        return totalFundingAmount;
    }
    public void setFundingCurrency(String fundingCurrency)
    {
        this.fundingCurrency = fundingCurrency;
    }

    public String getFundingCurrency()
    {
        return fundingCurrency;
    }
    public void setShortDescription(String shortDescription)
    {
        this.shortDescription = shortDescription;
    }

    public String getShortDescription()
    {
        return shortDescription;
    }
    public void setLastFundingCurrency(String lastFundingCurrency)
    {
        this.lastFundingCurrency = lastFundingCurrency;
    }

    public String getLastFundingCurrency()
    {
        return lastFundingCurrency;
    }
    public void setTopFiveInvestors(String topFiveInvestors)
    {
        this.topFiveInvestors = topFiveInvestors;
    }

    public String getTopFiveInvestors()
    {
        return topFiveInvestors;
    }
    public void setNumberOfLeadInvestors(Long numberOfLeadInvestors)
    {
        this.numberOfLeadInvestors = numberOfLeadInvestors;
    }

    public Long getNumberOfLeadInvestors()
    {
        return numberOfLeadInvestors;
    }
    public void setNumberOfInvestors(Long numberOfInvestors)
    {
        this.numberOfInvestors = numberOfInvestors;
    }

    public Long getNumberOfInvestors()
    {
        return numberOfInvestors;
    }
    public void setNumberOfAcquisitions(Long numberOfAcquisitions)
    {
        this.numberOfAcquisitions = numberOfAcquisitions;
    }

    public Long getNumberOfAcquisitions()
    {
        return numberOfAcquisitions;
    }
    public void setAcquisitionStatus(String acquisitionStatus)
    {
        this.acquisitionStatus = acquisitionStatus;
    }

    public String getAcquisitionStatus()
    {
        return acquisitionStatus;
    }
    public void setIpoStatus(String ipoStatus)
    {
        this.ipoStatus = ipoStatus;
    }

    public String getIpoStatus()
    {
        return ipoStatus;
    }
    public void setIpoDate(Date ipoDate)
    {
        this.ipoDate = ipoDate;
    }

    public Date getIpoDate()
    {
        return ipoDate;
    }

    public void setLpProcess(String lpProcess)
    {
        this.lpProcess = lpProcess;
    }

    public String getLpProcess()
    {
        return lpProcess;
    }

    public void setStage(String stage)
    {
        this.stage = stage;
    }

    public String getStage()
    {
        return stage;
    }

    public void setLatestValue(BigDecimal latestValue)
    {
        this.latestValue = latestValue;
    }

    public BigDecimal getLatestValue()
    {
        return latestValue;
    }
    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("orgId", getOrgId())
            .append("uuid", getUuid())
            .append("category", getCategory())
            .append("registerCapital", getRegisterCapital())
            .append("fullName", getFullName())
            .append("representative", getRepresentative())
            .append("startupDate", getStartupDate())
            .append("approvedDate", getApprovedDate())
            .append("operStartDate", getOperStartDate())
            .append("registerBureau", getRegisterBureau())
            .append("registerStatus", getRegisterStatus())
            .append("registerAddress", getRegisterAddress())
            .append("shortName", getShortName())
            .append("parentOrg", getParentOrg())
            .append("website", getWebsite())
            .append("avatar", getAvatar())
            .append("tickerSymbol", getTickerSymbol())
            .append("phoneNumber", getPhoneNumber())
            .append("industry", getIndustry())
            .append("territory", getTerritory())
            .append("owner", getOwner())
            .append("employeeCount", getEmployeeCount())
            .append("activeAccountCount", getActiveAccountCount())
            .append("activeOptyCount", getActiveOptyCount())
            .append("activeOptyAmount", getActiveOptyAmount())
            .append("revenue", getRevenue())
            .append("registerComments", getRegisterComments())
            .append("isTarget", getIsTarget())
            .append("isCustomer", getIsCustomer())
            .append("status", getStatus())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("customField", getCustomField())
            .toString();
    }
}
