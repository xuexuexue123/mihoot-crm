package com.ruoyi.crm.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 业务线索对象 crm_lead
 * 
 * @author luohd
 * @date 2024-01-30
 */
public class CrmLead extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 编号 */
    private String leadId;

    /** 线索类型 */
//    @Excel(name = "线索类型")
    private String leadType;

    /** 机会编号 */
//    @Excel(name = "机会编号")
    private String optyId;

    /** 机会名称 */
    @Excel(name = "lead.optyName")
    private String optyName;

    /** 机构编号 */
//    @Excel(name = "机构编号")
    private String orgId;

    /** 名称 */
    @Excel(name = "lead.name")
    private String leadName;

    /** 机构名称 */
    @Excel(name = "lead.fullName")
    private String fullName;

    /** 客户经理 */
    @Excel(name = "lead.owner")
    private String owner;

    /** 联系人编号 */
//    @Excel(name = "联系人编号")
    private String contactId;

    /** 联系人名 */
    @Excel(name = "lead.contactName")
    private String contactName;

    /** 业务阶段 */
    @Excel(name = "lead.stage", dictType = "crm_opty_stage")
    private String stage;

    /** 电话 */
    @Excel(name = "lead.workPhone")
    private String workPhone;

    /** 移动电话 */
    @Excel(name = "lead.mobilePhone")
    private String mobilePhone;

    /** 邮箱 */
    @Excel(name = "lead.workEmail")
    private String workEmail;

    /** 线索来源 */
    @Excel(name = "lead.leadSource", dictType = "crm_lead_source")
    private String leadSource;

    /** 线索评价 */
    @Excel(name = "lead.leadRating", dictType = "crm_lead_rating")
    private String leadRating;

    /** 网址 */
    @Excel(name = "lead.website")
    private String website;

    /** 区域 */
    @Excel(name = "lead.territory")
    private String territory;

    /** 行业 */
    @Excel(name = "lead.industry", dictType = "crm_lead_industry")
    private String industry;

    /** 员工数 */
    @Excel(name = "lead.employeeCount")
    private Long employeeCount;

    /** 营业收入 */
    @Excel(name = "lead.revenue")
    private BigDecimal revenue;

    /** 描述 */
    @Excel(name = "lead.description")
    private String description;

    /** 不活跃数 */
    @Excel(name = "lead.daysInactive")
    private Long daysInactive;

    /** 合作伙伴 */
    @Excel(name = "lead.partner")
    private String partner;

    /** 转化时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "lead.convertTime", width = 30, dateFormat = "yyyy-MM-dd")
    private Date convertTime;
    private Long userId;

    private Long deptId;

    /** 省份 */
    @Excel(name = "lead.province")
    private String province;

    /** 城市 */
    @Excel(name = "lead.city")
    private String city;

    /** 活动ID */
    private String sid;

    /** 自定义 */
//    @Excel(name = "自定义")
    private String customField;

    /** 公众号 openid（活动报名 token 归因） */
    private String wechatMpOpenid;

    /** 公众号 appId（与 wechatMpOpenid 成对） */
    private String wechatMpAppId;

    /** 企业微信外部联系人ID */
    private String wecomExternalUserid;

    /** 企业微信跟进成员userid */
    private String wecomFollowUserid;

    /** 企微活码 state（查询时 join crm_wecom_external_contact，非表列） */
    private String wecomState;

    /** 创始人数 */
//    @Excel(name = "创始人数")
    private Long numberOfFounders;

    /** 创始人 */
//    @Excel(name = "创始人")
    private String funders;

    /** 职位分工 */
//    @Excel(name = "职位分工")
    private String fundersTitle;

    /** 融资轮数 */
//    @Excel(name = "融资轮数")
    private Long numberOfFundingRounds;

    /** 最后轮次 */
//    @Excel(name = "最后轮次")
    private String lastFundingType;

    /** 总融资额 */
//    @Excel(name = "总融资额")
    private BigDecimal totalFundingAmount;

    /** 币种 */
//    @Excel(name = "币种")
    private String fundingCurrency;

    /** 成立日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "lead.foundedAt", width = 30, dateFormat = "yyyy-MM-dd")
    private Date foundedAt;

    /** 运营状态 */
//    @Excel(name = "运营状态")
    private String status;

    /** 最后一次有效跟进时间（白名单事件） */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastFollowupAt;

    /** 最近一次领取/指派时间；领取本身不刷新 lastFollowupAt */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date assignedAt;

    /** 一句话介绍 */
//    @Excel(name = "一句话介绍")
    private String shortDescription;

    /** 查询条件 开始日期 **/
    private Date startDate;

    /** 查询条件 结束日期 **/
    private Date endDate;

    /** 查询条件 范围开始值 **/
    private BigDecimal rangeStart;

    /** 查询条件 范围结束值 **/
    private BigDecimal rangeEnd;

    /** 头像 */
//    @Excel(name = "头像")
    private String avatar;

    /**  */
//    @Excel(name = "")
    private String tag;

    /** 调查结果 */
//    @Excel(name = "调查结果")
    private String surveyJson;

    /** API 请求：营销释放到公海 */
    private Boolean releaseToPool;

    /** 响应：已转化归档标记 */
    private Boolean archived;

    public Boolean getReleaseToPool()
    {
        return releaseToPool;
    }

    public void setReleaseToPool(Boolean releaseToPool)
    {
        this.releaseToPool = releaseToPool;
    }

    public Boolean getArchived()
    {
        return archived;
    }

    public void setArchived(Boolean archived)
    {
        this.archived = archived;
    }

    public void setLeadId(String leadId) 
    {
        this.leadId = leadId;
    }

    public String getLeadId() 
    {
        return leadId;
    }
    public void setLeadType(String leadType) 
    {
        this.leadType = leadType;
    }

    public String getLeadType() 
    {
        return leadType;
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
    public void setOrgId(String orgId) 
    {
        this.orgId = orgId;
    }

    public String getOrgId() 
    {
        return orgId;
    }
    public void setLeadName(String leadName) 
    {
        this.leadName = leadName;
    }

    public String getLeadName() 
    {
        return leadName;
    }
    public void setFullName(String fullName) 
    {
        this.fullName = fullName;
    }

    public String getFullName() 
    {
        return fullName;
    }
    public void setOwner(String owner) 
    {
        this.owner = owner;
    }

    public String getOwner() 
    {
        return owner;
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
    public void setStage(String stage) 
    {
        this.stage = stage;
    }

    public String getStage() 
    {
        return stage;
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
    public void setLeadSource(String leadSource) 
    {
        this.leadSource = leadSource;
    }

    public String getLeadSource() 
    {
        return leadSource;
    }
    public void setLeadRating(String leadRating) 
    {
        this.leadRating = leadRating;
    }

    public String getLeadRating() 
    {
        return leadRating;
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
    public void setDaysInactive(Long daysInactive) 
    {
        this.daysInactive = daysInactive;
    }

    public Long getDaysInactive() 
    {
        return daysInactive;
    }
    public void setPartner(String partner) 
    {
        this.partner = partner;
    }

    public String getPartner() 
    {
        return partner;
    }
    public void setConvertTime(Date convertTime) 
    {
        this.convertTime = convertTime;
    }

    public Date getConvertTime() 
    {
        return convertTime;
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
    public void setProvince(String province)
    {
        this.province = province;
    }

    public String getProvince()
    {
        return province;
    }
    public void setCity(String city)
    {
        this.city = city;
    }

    public String getCity()
    {
        return city;
    }
    public void setSid(String sid)
    {
        this.sid = sid;
    }

    public String getSid()
    {
        return sid;
    }
    public void setCustomField(String customField)
    {
        this.customField = customField;
    }

    public String getCustomField()
    {
        return customField;
    }

    public void setWechatMpOpenid(String wechatMpOpenid)
    {
        this.wechatMpOpenid = wechatMpOpenid;
    }

    public String getWechatMpOpenid()
    {
        return wechatMpOpenid;
    }

    public void setWechatMpAppId(String wechatMpAppId)
    {
        this.wechatMpAppId = wechatMpAppId;
    }

    public String getWechatMpAppId()
    {
        return wechatMpAppId;
    }

    public void setWecomExternalUserid(String wecomExternalUserid)
    {
        this.wecomExternalUserid = wecomExternalUserid;
    }

    public String getWecomExternalUserid()
    {
        return wecomExternalUserid;
    }

    public void setWecomFollowUserid(String wecomFollowUserid)
    {
        this.wecomFollowUserid = wecomFollowUserid;
    }

    public String getWecomFollowUserid()
    {
        return wecomFollowUserid;
    }

    public void setWecomState(String wecomState)
    {
        this.wecomState = wecomState;
    }

    public String getWecomState()
    {
        return wecomState;
    }

    public void setNumberOfFounders(Long numberOfFounders)
    {
        this.numberOfFounders = numberOfFounders;
    }

    public Long getNumberOfFounders()
    {
        return numberOfFounders;
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

    public void setFoundedAt(Date foundedAt)
    {
        this.foundedAt = foundedAt;
    }

    public Date getFoundedAt()
    {
        return foundedAt;
    }
    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getStatus()
    {
        return status;
    }

    public Date getLastFollowupAt()
    {
        return lastFollowupAt;
    }

    public void setLastFollowupAt(Date lastFollowupAt)
    {
        this.lastFollowupAt = lastFollowupAt;
    }

    public Date getAssignedAt()
    {
        return assignedAt;
    }

    public void setAssignedAt(Date assignedAt)
    {
        this.assignedAt = assignedAt;
    }
    public void setShortDescription(String shortDescription)
    {
        this.shortDescription = shortDescription;
    }

    public String getShortDescription()
    {
        return shortDescription;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public BigDecimal getRangeStart() {
        return rangeStart;
    }

    public void setRangeStart(BigDecimal rangeStart) {
        this.rangeStart = rangeStart;
    }

    public BigDecimal getRangeEnd() {
        return rangeEnd;
    }

    public void setRangeEnd(BigDecimal rangeEnd) {
        this.rangeEnd = rangeEnd;
    }

    public void setAvatar(String avatar)
    {
        this.avatar = avatar;
    }

    public String getAvatar()
    {
        return avatar;
    }

    public void setTag(String tag)
    {
        this.tag = tag;
    }

    public String getTag()
    {
        return tag;
    }
    public void setSurveyJson(String surveyJson)
    {
        this.surveyJson = surveyJson;
    }

    public String getSurveyJson()
    {
        return surveyJson;
    }
    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("leadId", getLeadId())
            .append("leadType", getLeadType())
            .append("optyId", getOptyId())
            .append("optyName", getOptyName())
            .append("orgId", getOrgId())
            .append("leadName", getLeadName())
            .append("fullName", getFullName())
            .append("owner", getOwner())
            .append("contactId", getContactId())
            .append("contactName", getContactName())
            .append("stage", getStage())
            .append("workPhone", getWorkPhone())
            .append("mobilePhone", getMobilePhone())
            .append("workEmail", getWorkEmail())
            .append("leadSource", getLeadSource())
            .append("leadRating", getLeadRating())
            .append("website", getWebsite())
            .append("territory", getTerritory())
            .append("industry", getIndustry())
            .append("employeeCount", getEmployeeCount())
            .append("revenue", getRevenue())
            .append("description", getDescription())
            .append("daysInactive", getDaysInactive())
            .append("partner", getPartner())
            .append("convertTime", getConvertTime())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
