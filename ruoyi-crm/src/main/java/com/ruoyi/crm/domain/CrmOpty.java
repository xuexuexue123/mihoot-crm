package com.ruoyi.crm.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 销售机会对象 crm_opty
 * 
 * @author luohd
 * @date 2024-02-27
 */
public class CrmOpty extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 编号 */
    private String optyId;

    /** 机会类型 */
    @Excel(name = "机会类型")
    private String optyType;

    /** 机构编号 */
//    @Excel(name = "机构编号")
    private String orgId;

    /** 客户编号 */
//    @Excel(name = "客户编号")
    private String accountId;

    /** 编号 */
//    @Excel(name = "编号")
    private String leadId;

    /** 名称 */
    @Excel(name = "名称")
    private String leadName;

    /** 名称 */
    @Excel(name = "名称")
    private String name;

    /** 机构名称 */
    @Excel(name = "机构名称")
    private String fullName;

    /** 客户名称 */
    @Excel(name = "客户名称")
    private String accountName;

    /** 联系人编号 */
    @Excel(name = "联系人编号")
    private String contactId;

    /** 联系人 */
//    @Excel(name = "联系人")
    private String contactName;

    /** 客户经理 */
    @Excel(name = "客户经理")
    private String owner;

    /** 金额 */
    @Excel(name = "金额")
    private BigDecimal amount;

    /** 销售类型 */
    @Excel(name = "销售类型")
    private String salesType;

    /** 业务阶段 */
    @Excel(name = "业务阶段")
    private String stage;

    /** 赢单概率 */
    @Excel(name = "赢单概率")
    private BigDecimal winProbability;

    /** 预告类型 */
    @Excel(name = "预告类型")
    private String forecastCategory;

    /** 电话 */
    @Excel(name = "电话")
    private String workPhone;

    /** 移动电话 */
    @Excel(name = "移动电话")
    private String mobilePhone;

    /** 邮箱 */
    @Excel(name = "邮箱")
    private String workEmail;

    /** 来源 */
    @Excel(name = "来源")
    private String optySource;

    /** 评价 */
    @Excel(name = "评价")
    private String optyRating;

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

    /** 描述 */
    @Excel(name = "描述")
    private String description;

    /** 是否必赢 （Y是 N否） */
    @Excel(name = "是否必赢 ", readConverterExp = "Y=是,N=否")
    private String isMustwin;

    /** 成交时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "成交时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date closeDate;

    /** 合同截止日 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "合同截止日", width = 30, dateFormat = "yyyy-MM-dd")
    private Date contractEnddate;

    /** 成交原因 */
    @Excel(name = "成交原因")
    private String closeReason;

    /** 具体原因 */
    @Excel(name = "具体原因")
    private String reasonType;

    /** 成交备注 */
    @Excel(name = "成交备注")
    private String closeComment;

    /** 自定义 */
    @Excel(name = "自定义")
    private String customField;

    /** 开始日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "开始日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date startDate;

    /** 截止日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "截止日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date endDate;


    private Long userId;

    private Long deptId;

    /** 身份证ID */
    @Excel(name = "身份证ID")
    private String sid;

    /** 合作伙伴 */
    @Excel(name = "合作伙伴")
    private String partner;

    /** 项目logo */
    @Excel(name = "项目logo")
    private String avatar;

    /** 项目编号 */
    @Excel(name = "项目编号")
    private String code;

    private String[] stages;

    /** 立项结论 */
    @Excel(name = "立项结论")
    private String initialStatus;

    /** 尽调结论 */
    @Excel(name = "尽调结论")
    private String ddStatus;

    /** 投资决策 */
    @Excel(name = "投资决策")
    private String investDecision;

    /** 项目流程 */
    @Excel(name = "项目流程")
    private String optyProcess;

    /** 治疗项目编码（字典 crm_treatment_item.dict_value，如 implant） */
    private String treatmentItemCode;

    /** 病例分组 ID */
    private String caseGroupId;

    /** 赢单依据 */
    private String closedWonBasis;

    /** 已收首付金额 */
    private BigDecimal downPaymentAmount;

    /** 治疗协议签署时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date agreementSignedAt;


    public void setOptyId(String optyId) 
    {
        this.optyId = optyId;
    }

    public String getOptyId() 
    {
        return optyId;
    }
    public void setOptyType(String optyType) 
    {
        this.optyType = optyType;
    }

    public String getOptyType() 
    {
        return optyType;
    }
    public void setOrgId(String orgId) 
    {
        this.orgId = orgId;
    }

    public String getOrgId() 
    {
        return orgId;
    }
    public void setAccountId(String accountId) 
    {
        this.accountId = accountId;
    }

    public String getAccountId() 
    {
        return accountId;
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
    public void setName(String name) 
    {
        this.name = name;
    }

    public String getName() 
    {
        return name;
    }
    public void setFullName(String fullName) 
    {
        this.fullName = fullName;
    }

    public String getFullName() 
    {
        return fullName;
    }
    public void setAccountName(String accountName) 
    {
        this.accountName = accountName;
    }

    public String getAccountName() 
    {
        return accountName;
    }
    public void setContactId(String contactId) 
    {
        this.contactId = contactId;
    }

    public String getContactId() 
    {
        return contactId;
    }
    public void setContactName(String contactName) 
    {
        this.contactName = contactName;
    }

    public String getContactName() 
    {
        return contactName;
    }
    public void setOwner(String owner) 
    {
        this.owner = owner;
    }

    public String getOwner() 
    {
        return owner;
    }
    public void setAmount(BigDecimal amount) 
    {
        this.amount = amount;
    }

    public BigDecimal getAmount() 
    {
        return amount;
    }
    public void setSalesType(String salesType) 
    {
        this.salesType = salesType;
    }

    public String getSalesType() 
    {
        return salesType;
    }
    public void setStage(String stage) 
    {
        this.stage = stage;
    }

    public String getStage() 
    {
        return stage;
    }
    public void setWinProbability(BigDecimal winProbability) 
    {
        this.winProbability = winProbability;
    }

    public BigDecimal getWinProbability() 
    {
        return winProbability;
    }
    public void setForecastCategory(String forecastCategory) 
    {
        this.forecastCategory = forecastCategory;
    }

    public String getForecastCategory() 
    {
        return forecastCategory;
    }
    public void setWorkPhone(String workPhone) 
    {
        this.workPhone = workPhone;
    }

    public String getWorkPhone() 
    {
        return workPhone;
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
    public void setOptySource(String optySource) 
    {
        this.optySource = optySource;
    }

    public String getOptySource() 
    {
        return optySource;
    }
    public void setOptyRating(String optyRating) 
    {
        this.optyRating = optyRating;
    }

    public String getOptyRating() 
    {
        return optyRating;
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
    public void setDescription(String description) 
    {
        this.description = description;
    }

    public String getDescription() 
    {
        return description;
    }
    public void setIsMustwin(String isMustwin) 
    {
        this.isMustwin = isMustwin;
    }

    public String getIsMustwin() 
    {
        return isMustwin;
    }
    public void setCloseDate(Date closeDate) 
    {
        this.closeDate = closeDate;
    }

    public Date getCloseDate() 
    {
        return closeDate;
    }
    public void setContractEnddate(Date contractEnddate) 
    {
        this.contractEnddate = contractEnddate;
    }

    public Date getContractEnddate() 
    {
        return contractEnddate;
    }
    public void setCloseReason(String closeReason) 
    {
        this.closeReason = closeReason;
    }

    public String getCloseReason() 
    {
        return closeReason;
    }
    public void setReasonType(String reasonType) 
    {
        this.reasonType = reasonType;
    }

    public String getReasonType() 
    {
        return reasonType;
    }
    public void setCloseComment(String closeComment) 
    {
        this.closeComment = closeComment;
    }

    public String getCloseComment() 
    {
        return closeComment;
    }
    public void setCustomField(String customField) 
    {
        this.customField = customField;
    }

    public String getCustomField() 
    {
        return customField;
    }
    public void setStartDate(Date startDate) 
    {
        this.startDate = startDate;
    }

    public Date getStartDate() 
    {
        return startDate;
    }
    public void setEndDate(Date endDate) 
    {
        this.endDate = endDate;
    }

    public Date getEndDate() 
    {
        return endDate;
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

    public void setSid(String sid)
    {
        this.sid = sid;
    }

    public String getSid()
    {
        return sid;
    }

    public void setPartner(String partner)
    {
        this.partner = partner;
    }

    public String getPartner()
    {
        return partner;
    }
    public void setAvatar(String avatar)
    {
        this.avatar = avatar;
    }

    public String getAvatar()
    {
        return avatar;
    }
    public void setCode(String code)
    {
        this.code = code;
    }

    public String getCode()
    {
        return code;
    }

    public String[] getStages() {
        return stages;
    }

    public void setStages(String[] stages) {
        this.stages = stages;
    }
    public void setInitialStatus(String initialStatus)
    {
        this.initialStatus = initialStatus;
    }

    public String getInitialStatus()
    {
        return initialStatus;
    }
    public void setDdStatus(String ddStatus)
    {
        this.ddStatus = ddStatus;
    }

    public String getDdStatus()
    {
        return ddStatus;
    }
    public void setInvestDecision(String investDecision)
    {
        this.investDecision = investDecision;
    }

    public String getInvestDecision()
    {
        return investDecision;
    }

    public void setOptyProcess(String optyProcess)
    {
        this.optyProcess = optyProcess;
    }

    public String getOptyProcess()
    {
        return optyProcess;
    }

    public String getTreatmentItemCode()
    {
        return treatmentItemCode;
    }

    public void setTreatmentItemCode(String treatmentItemCode)
    {
        this.treatmentItemCode = treatmentItemCode;
    }

    public String getCaseGroupId()
    {
        return caseGroupId;
    }

    public void setCaseGroupId(String caseGroupId)
    {
        this.caseGroupId = caseGroupId;
    }

    public String getClosedWonBasis()
    {
        return closedWonBasis;
    }

    public void setClosedWonBasis(String closedWonBasis)
    {
        this.closedWonBasis = closedWonBasis;
    }

    public BigDecimal getDownPaymentAmount()
    {
        return downPaymentAmount;
    }

    public void setDownPaymentAmount(BigDecimal downPaymentAmount)
    {
        this.downPaymentAmount = downPaymentAmount;
    }

    public Date getAgreementSignedAt()
    {
        return agreementSignedAt;
    }

    public void setAgreementSignedAt(Date agreementSignedAt)
    {
        this.agreementSignedAt = agreementSignedAt;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("optyId", getOptyId())
            .append("optyType", getOptyType())
            .append("orgId", getOrgId())
            .append("accountId", getAccountId())
            .append("leadId", getLeadId())
            .append("leadName", getLeadName())
            .append("name", getName())
            .append("fullName", getFullName())
            .append("accountName", getAccountName())
            .append("contactId", getContactId())
            .append("contactName", getContactName())
            .append("owner", getOwner())
            .append("amount", getAmount())
            .append("salesType", getSalesType())
            .append("stage", getStage())
            .append("winProbability", getWinProbability())
            .append("forecastCategory", getForecastCategory())
            .append("workPhone", getWorkPhone())
            .append("mobilePhone", getMobilePhone())
            .append("workEmail", getWorkEmail())
            .append("optySource", getOptySource())
            .append("optyRating", getOptyRating())
            .append("website", getWebsite())
            .append("territory", getTerritory())
            .append("industry", getIndustry())
            .append("employeeCount", getEmployeeCount())
            .append("revenue", getRevenue())
            .append("description", getDescription())
            .append("isMustwin", getIsMustwin())
            .append("closeDate", getCloseDate())
            .append("contractEnddate", getContractEnddate())
            .append("closeReason", getCloseReason())
            .append("reasonType", getReasonType())
            .append("closeComment", getCloseComment())
            .append("customField", getCustomField())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("startDate", getStartDate())
            .append("endDate", getEndDate())
            .append("optyProcess", getOptyProcess())
            .append("treatmentItemCode", getTreatmentItemCode())
            .append("caseGroupId", getCaseGroupId())
            .append("closedWonBasis", getClosedWonBasis())
            .append("downPaymentAmount", getDownPaymentAmount())
            .append("agreementSignedAt", getAgreementSignedAt())
            .toString();
    }
}
