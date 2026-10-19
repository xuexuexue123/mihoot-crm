package com.ruoyi.crm.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 联系人对象 crm_contact
 * 
 * @author luohd
 * @date 2024-02-08
 */
public class CrmContact extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 编号 */
    private String contactId;

    /** 机构编号 */
    @Excel(name = "机构编号")
    private String orgId;

    /** 机构编号 */
    @Excel(name = "机构编号")
    private String accountId;

    /** 姓名 */
    @Excel(name = "姓名")
    private String name;

    /** 机构名称 */
    @Excel(name = "机构名称")
    private String fullName;

    /** 客户名称 */
    @Excel(name = "客户名称")
    private String accountName;

    /** 称呼 */
    @Excel(name = "称呼")
    private String salutation;

    /** 职位 */
    @Excel(name = "职位")
    private String title;

    /** 婚姻状态 */
    @Excel(name = "婚姻状态")
    private String maritalStatus;

    /** 生日 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "生日", width = 30, dateFormat = "yyyy-MM-dd")
    private Date birthdate;

    /** 年龄 */
    @Excel(name = "年龄")
    private Long age;

    /** 电话 */
    @Excel(name = "电话")
    private String workPhone;

    /** 邮箱 */
    @Excel(name = "邮箱")
    private String workEmail;

    /** 性别 */
    @Excel(name = "性别")
    private String gender;

    /** 个人邮箱 */
    @Excel(name = "个人邮箱")
    private String personalEmail;

    /** 手机 */
    @Excel(name = "手机")
    private String mobilePhone;

    /** 类型 */
    @Excel(name = "类型")
    private String clientType;

    /** 区域 */
    @Excel(name = "区域")
    private String territory;

    /** 客户经理 */
    @Excel(name = "客户经理")
    private String owner;

    /** 级别 */
    @Excel(name = "级别")
    private String managementLevel;

    /** 部门 */
    @Excel(name = "部门")
    private String department;

    /** 汇报人 */
    @Excel(name = "汇报人")
    private String reportsTo;

    /** 汇报人ID */
    @Excel(name = "汇报人ID")
    private String reportsToId;

    /** 倾向性 */
    @Excel(name = "倾向性")
    private String favorabilityRating;

    /** 影响力 */
    @Excel(name = "影响力")
    private String influenceRating;

    /** 助理 */
    @Excel(name = "助理")
    private String assistant;

    /** 助理电话 */
    @Excel(name = "助理电话")
    private String assistantPhone;

    /** 助理邮箱 */
    @Excel(name = "助理邮箱")
    private String assistantEmail;

    /** 允许电话 （Y是 N否） */
    @Excel(name = "允许电话 ", readConverterExp = "Y=是,N=否")
    private String allowCall;

    /** 允许邮件 （Y是 N否） */
    @Excel(name = "允许邮件 ", readConverterExp = "Y=是,N=否")
    private String allowEmail;

    /** 是否客户 （Y是 N否） */
    @Excel(name = "是否客户 ", readConverterExp = "Y=是,N=否")
    private String isCustomer;

    /** 机构客户 （Y是 N否） */
    @Excel(name = "机构客户 ", readConverterExp = "Y=是,N=否")
    private String isOrgCustomer;

    /** 工作职责 */
    @Excel(name = "工作职责")
    private String responsibility;

    /** 活动机会数 */
    @Excel(name = "活动机会数")
    private Long activeOptyCount;

    /** 线索数 */
    @Excel(name = "线索数")
    private Long activeLeadCount;

    /** 活动机会金额 */
    @Excel(name = "活动机会金额")
    private BigDecimal activeOptyAmount;

    /** 状态 */
    @Excel(name = "状态")
    private String status;

    /** 工作年限 */
    @Excel(name = "工作年限")
    private Long workingYears;

    /** 是否活跃 */
    @Excel(name = "是否活跃")
    private String isActive;

    /** 学历 */
    @Excel(name = "学历")
    private String degree;

    /** 专业 */
    @Excel(name = "专业")
    private String major;

    /** 籍贯 */
    @Excel(name = "籍贯")
    private String origin;

    /** 微信 */
    @Excel(name = "微信")
    private String wechat;

    /** 来源地 */
    @Excel(name = "来源地")
    private String location;

    /** 毕业院校 */
    @Excel(name = "毕业院校")
    private String school;

    private Long userId;

    private Long deptId;
    /** 自定义 */
//    @Excel(name = "自定义")
    private String customField;

    /** 身份证ID */
    @Excel(name = "身份证ID")
    private String sid;

    /** 转化来源 Lead ID */
    private String sourceLeadId;

    /** 资源 */
    @Excel(name = "资源")
    private String resources;

    /** 需求 */
    @Excel(name = "需求")
    private String requirement;

    /** RR说明 */
    @Excel(name = "RR说明")
    private String rrDescription;

    /** 名片 */
    @Excel(name = "名片")
    private String nameCard;

    /** 头像 */
    @Excel(name = "头像")
    private String avatar;

    /**  */
    @Excel(name = "")
    private String tag;

    /** 优先级 */
    @Excel(name = "优先级")
    private Long priority;

    /** 终身初始来源（与可变 origin 分离） */
    private String originalSource;

    /** 首诊门店 */
    private String firstVisitClinicId;

    /** 影像门户直链 */
    private String imagingPortalUrl;

    /** 第三方影像患者标识 */
    private String imagingPatientExternalId;

    /** 首次就诊时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date firstVisitAt;

    /** 最近就诊时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastVisitAt;

    /** 累计就诊次数 */
    private Integer visitCount;

    /** 获客成本（CAC 摊销累计） */
    private BigDecimal acquisitionCost;

    /** 企业微信外部联系人ID */
    private String wecomExternalUserid;

    /** 企业微信跟进成员 userid */
    private String wecomFollowUserid;

    /** 微信客服客户昵称 */
    private String wecomName;

    /** 微信客服客户头像 URL */
    private String wecomAvatar;

    /** 微信客服客户性别（0=未知, 1=男, 2=女） */
    private Integer wecomGender;

    /** 微信客服客户类型 */
    private Integer wecomType;

    public void setContactId(String contactId) 
    {
        this.contactId = contactId;
    }

    public String getContactId() 
    {
        return contactId;
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
    public void setSalutation(String salutation) 
    {
        this.salutation = salutation;
    }

    public String getSalutation() 
    {
        return salutation;
    }
    public void setTitle(String title) 
    {
        this.title = title;
    }

    public String getTitle() 
    {
        return title;
    }
    public void setMaritalStatus(String maritalStatus) 
    {
        this.maritalStatus = maritalStatus;
    }

    public String getMaritalStatus() 
    {
        return maritalStatus;
    }
    public void setBirthdate(Date birthdate) 
    {
        this.birthdate = birthdate;
    }

    public Date getBirthdate() 
    {
        return birthdate;
    }
    public void setAge(Long age) 
    {
        this.age = age;
    }

    public Long getAge() 
    {
        return age;
    }
    public void setWorkPhone(String workPhone) 
    {
        this.workPhone = workPhone;
    }

    public String getWorkPhone() 
    {
        return workPhone;
    }
    public void setWorkEmail(String workEmail) 
    {
        this.workEmail = workEmail;
    }

    public String getWorkEmail() 
    {
        return workEmail;
    }
    public void setGender(String gender) 
    {
        this.gender = gender;
    }

    public String getGender() 
    {
        return gender;
    }
    public void setPersonalEmail(String personalEmail) 
    {
        this.personalEmail = personalEmail;
    }

    public String getPersonalEmail() 
    {
        return personalEmail;
    }
    public void setMobilePhone(String mobilePhone) 
    {
        this.mobilePhone = mobilePhone;
    }

    public String getMobilePhone() 
    {
        return mobilePhone;
    }
    public void setClientType(String clientType) 
    {
        this.clientType = clientType;
    }

    public String getClientType() 
    {
        return clientType;
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
    public void setManagementLevel(String managementLevel) 
    {
        this.managementLevel = managementLevel;
    }

    public String getManagementLevel() 
    {
        return managementLevel;
    }
    public void setDepartment(String department) 
    {
        this.department = department;
    }

    public String getDepartment() 
    {
        return department;
    }
    public void setReportsTo(String reportsTo) 
    {
        this.reportsTo = reportsTo;
    }

    public String getReportsTo() 
    {
        return reportsTo;
    }
    public void setReportsToId(String reportsToId) 
    {
        this.reportsToId = reportsToId;
    }

    public String getReportsToId() 
    {
        return reportsToId;
    }
    public void setFavorabilityRating(String favorabilityRating) 
    {
        this.favorabilityRating = favorabilityRating;
    }

    public String getFavorabilityRating() 
    {
        return favorabilityRating;
    }
    public void setInfluenceRating(String influenceRating) 
    {
        this.influenceRating = influenceRating;
    }

    public String getInfluenceRating() 
    {
        return influenceRating;
    }
    public void setAssistant(String assistant) 
    {
        this.assistant = assistant;
    }

    public String getAssistant() 
    {
        return assistant;
    }
    public void setAssistantPhone(String assistantPhone) 
    {
        this.assistantPhone = assistantPhone;
    }

    public String getAssistantPhone() 
    {
        return assistantPhone;
    }
    public void setAssistantEmail(String assistantEmail) 
    {
        this.assistantEmail = assistantEmail;
    }

    public String getAssistantEmail() 
    {
        return assistantEmail;
    }
    public void setAllowCall(String allowCall) 
    {
        this.allowCall = allowCall;
    }

    public String getAllowCall() 
    {
        return allowCall;
    }
    public void setAllowEmail(String allowEmail) 
    {
        this.allowEmail = allowEmail;
    }

    public String getAllowEmail() 
    {
        return allowEmail;
    }
    public void setIsCustomer(String isCustomer) 
    {
        this.isCustomer = isCustomer;
    }

    public String getIsCustomer() 
    {
        return isCustomer;
    }
    public void setIsOrgCustomer(String isOrgCustomer) 
    {
        this.isOrgCustomer = isOrgCustomer;
    }

    public String getIsOrgCustomer() 
    {
        return isOrgCustomer;
    }
    public void setResponsibility(String responsibility) 
    {
        this.responsibility = responsibility;
    }

    public String getResponsibility() 
    {
        return responsibility;
    }
    public void setActiveOptyCount(Long activeOptyCount) 
    {
        this.activeOptyCount = activeOptyCount;
    }

    public Long getActiveOptyCount() 
    {
        return activeOptyCount;
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
    public void setStatus(String status) 
    {
        this.status = status;
    }

    public String getStatus() 
    {
        return status;
    }
    public void setWorkingYears(Long workingYears) 
    {
        this.workingYears = workingYears;
    }

    public Long getWorkingYears() 
    {
        return workingYears;
    }
    public void setIsActive(String isActive) 
    {
        this.isActive = isActive;
    }

    public String getIsActive() 
    {
        return isActive;
    }
    public void setDegree(String degree) 
    {
        this.degree = degree;
    }

    public String getDegree() 
    {
        return degree;
    }
    public void setMajor(String major) 
    {
        this.major = major;
    }

    public String getMajor() 
    {
        return major;
    }
    public void setOrigin(String origin) 
    {
        this.origin = origin;
    }

    public String getOrigin() 
    {
        return origin;
    }
    public void setWechat(String wechat) 
    {
        this.wechat = wechat;
    }

    public String getWechat() 
    {
        return wechat;
    }
    public void setLocation(String location) 
    {
        this.location = location;
    }

    public String getLocation() 
    {
        return location;
    }
    public void setSchool(String school) 
    {
        this.school = school;
    }

    public String getSchool() 
    {
        return school;
    }
    public void setTag(String tag) 
    {
        this.tag = tag;
    }

    public String getTag() 
    {
        return tag;
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
    public void setCustomField(String customField)
    {
        this.customField = customField;
    }

    public String getCustomField()
    {
        return customField;
    }
    public void setSid(String sid)
    {
        this.sid = sid;
    }

    public String getSid()
    {
        return sid;
    }

    public String getSourceLeadId()
    {
        return sourceLeadId;
    }

    public void setSourceLeadId(String sourceLeadId)
    {
        this.sourceLeadId = sourceLeadId;
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
    public void setRrDescription(String rrDescription)
    {
        this.rrDescription = rrDescription;
    }

    public String getRrDescription()
    {
        return rrDescription;
    }
    public void setNameCard(String nameCard)
    {
        this.nameCard = nameCard;
    }

    public String getNameCard()
    {
        return nameCard;
    }
    public void setAvatar(String avatar)
    {
        this.avatar = avatar;
    }

    public String getAvatar()
    {
        return avatar;
    }

    public void setPriority(Long priority)
    {
        this.priority = priority;
    }

    public Long getPriority()
    {
        return priority;
    }

    public String getOriginalSource()
    {
        return originalSource;
    }

    public void setOriginalSource(String originalSource)
    {
        this.originalSource = originalSource;
    }

    public String getFirstVisitClinicId()
    {
        return firstVisitClinicId;
    }

    public void setFirstVisitClinicId(String firstVisitClinicId)
    {
        this.firstVisitClinicId = firstVisitClinicId;
    }

    public String getImagingPortalUrl()
    {
        return imagingPortalUrl;
    }

    public void setImagingPortalUrl(String imagingPortalUrl)
    {
        this.imagingPortalUrl = imagingPortalUrl;
    }

    public String getImagingPatientExternalId()
    {
        return imagingPatientExternalId;
    }

    public void setImagingPatientExternalId(String imagingPatientExternalId)
    {
        this.imagingPatientExternalId = imagingPatientExternalId;
    }

    public Date getFirstVisitAt()
    {
        return firstVisitAt;
    }

    public void setFirstVisitAt(Date firstVisitAt)
    {
        this.firstVisitAt = firstVisitAt;
    }

    public Date getLastVisitAt()
    {
        return lastVisitAt;
    }

    public void setLastVisitAt(Date lastVisitAt)
    {
        this.lastVisitAt = lastVisitAt;
    }

    public Integer getVisitCount()
    {
        return visitCount;
    }

    public void setVisitCount(Integer visitCount)
    {
        this.visitCount = visitCount;
    }

    public BigDecimal getAcquisitionCost()
    {
        return acquisitionCost;
    }

    public void setAcquisitionCost(BigDecimal acquisitionCost)
    {
        this.acquisitionCost = acquisitionCost;
    }

    public String getWecomExternalUserid()
    {
        return wecomExternalUserid;
    }

    public void setWecomExternalUserid(String wecomExternalUserid)
    {
        this.wecomExternalUserid = wecomExternalUserid;
    }

    public String getWecomFollowUserid()
    {
        return wecomFollowUserid;
    }

    public void setWecomFollowUserid(String wecomFollowUserid)
    {
        this.wecomFollowUserid = wecomFollowUserid;
    }

    public String getWecomName()
    {
        return wecomName;
    }

    public void setWecomName(String wecomName)
    {
        this.wecomName = wecomName;
    }

    public String getWecomAvatar()
    {
        return wecomAvatar;
    }

    public void setWecomAvatar(String wecomAvatar)
    {
        this.wecomAvatar = wecomAvatar;
    }

    public Integer getWecomGender()
    {
        return wecomGender;
    }

    public void setWecomGender(Integer wecomGender)
    {
        this.wecomGender = wecomGender;
    }

    public Integer getWecomType()
    {
        return wecomType;
    }

    public void setWecomType(Integer wecomType)
    {
        this.wecomType = wecomType;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("contactId", getContactId())
            .append("orgId", getOrgId())
            .append("accountId", getAccountId())
            .append("name", getName())
            .append("fullName", getFullName())
            .append("accountName", getAccountName())
            .append("salutation", getSalutation())
            .append("title", getTitle())
            .append("maritalStatus", getMaritalStatus())
            .append("birthdate", getBirthdate())
            .append("age", getAge())
            .append("workPhone", getWorkPhone())
            .append("workEmail", getWorkEmail())
            .append("gender", getGender())
            .append("personalEmail", getPersonalEmail())
            .append("mobilePhone", getMobilePhone())
            .append("clientType", getClientType())
            .append("territory", getTerritory())
            .append("owner", getOwner())
            .append("managementLevel", getManagementLevel())
            .append("department", getDepartment())
            .append("reportsTo", getReportsTo())
            .append("reportsToId", getReportsToId())
            .append("favorabilityRating", getFavorabilityRating())
            .append("influenceRating", getInfluenceRating())
            .append("assistant", getAssistant())
            .append("assistantPhone", getAssistantPhone())
            .append("assistantEmail", getAssistantEmail())
            .append("allowCall", getAllowCall())
            .append("allowEmail", getAllowEmail())
            .append("isCustomer", getIsCustomer())
            .append("isOrgCustomer", getIsOrgCustomer())
            .append("responsibility", getResponsibility())
            .append("activeOptyCount", getActiveOptyCount())
            .append("activeLeadCount", getActiveLeadCount())
            .append("activeOptyAmount", getActiveOptyAmount())
            .append("status", getStatus())
            .append("workingYears", getWorkingYears())
            .append("isActive", getIsActive())
            .append("degree", getDegree())
            .append("major", getMajor())
            .append("origin", getOrigin())
            .append("originalSource", getOriginalSource())
            .append("firstVisitClinicId", getFirstVisitClinicId())
            .append("imagingPortalUrl", getImagingPortalUrl())
            .append("imagingPatientExternalId", getImagingPatientExternalId())
            .append("firstVisitAt", getFirstVisitAt())
            .append("lastVisitAt", getLastVisitAt())
            .append("visitCount", getVisitCount())
            .append("acquisitionCost", getAcquisitionCost())
            .append("wecomExternalUserid", getWecomExternalUserid())
            .append("wecomName", getWecomName())
            .append("wecomAvatar", getWecomAvatar())
            .append("wecomGender", getWecomGender())
            .append("wecomType", getWecomType())
            .append("wechat", getWechat())
            .append("location", getLocation())
            .append("school", getSchool())
            .append("tag", getTag())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
