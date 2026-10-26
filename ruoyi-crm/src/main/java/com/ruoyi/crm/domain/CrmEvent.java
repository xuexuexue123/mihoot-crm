package com.ruoyi.crm.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 业务事件对象 crm_event
 * 
 * @author luohd
 * @date 2025-03-15
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CrmEvent extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 编号 */
    private String eventId;

    /** 类型 */
    @Excel(name = "类型")
    private String eventType;

    /** 名称 */
    @Excel(name = "名称")
    private String subject;

    /** 负责人 */
    @Excel(name = "负责人")
    private String owner;

    /** 联系人编号 */
    @Excel(name = "联系人编号")
    private String contactId;

    /** 联系人名 */
    @Excel(name = "联系人名")
    private String contactName;

    /** 阶段 */
    @Excel(name = "阶段")
    private String stage;

    /** 移动电话 */
    @Excel(name = "移动电话")
    private String mobilePhone;

    /** 电话 */
    @Excel(name = "电话")
    private String workPhone;

    /** 邮箱 */
    @Excel(name = "邮箱")
    private String workEmail;

    /** 来源 */
    @Excel(name = "来源")
    private String eventSource;

    /** 网址 */
    @Excel(name = "网址")
    private String website;

    /** 行业 */
    @Excel(name = "行业")
    private String industry;

    /** 预算 */
    @Excel(name = "预算")
    private BigDecimal budget;

    /** 已花费 */
    @Excel(name = "已花费")
    private BigDecimal paid;

    /** 余额 */
    @Excel(name = "余额")
    private BigDecimal balance;

    /** 地址 */
    @Excel(name = "地址")
    private String location;

    /** 活动介绍（Markdown格式） */
    @Excel(name = "活动介绍")
    private String description;

    /** 活动议程（Markdown格式） */
    @Excel(name = "活动议程")
    private String agenda;

    /** 活动语言 */
    @Excel(name = "活动语言")
    private String language;

    /** 开始时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Excel(name = "开始时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    /** 转化时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Excel(name = "转化时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;

    /** 用户ID */
    @Excel(name = "用户ID")
    private Long userId;

    /** 部门ID */
    @Excel(name = "部门ID")
    private Long deptId;

    /**  */
    @Excel(name = "")
    private String status;

    /**  */
    @Excel(name = "")
    private String avatar;

    /** 报名表单来源 EMS Survey 模版 ID */
    private String registrationTemplateId;

    /** 报名表单 SurveyJS content 快照 */
    private String registrationFormJson;

    /** 本次请求是否显式更新报名表单配置（含清空为 null） */
    private transient Boolean registrationFormTouched;

    /** 公众号报名自动化：0=关 1=开 */
    private String mpRegistrationAutomationEnabled;

    /** 报名模板消息预设 ID */
    private Long mpRegistrationPresetId;

    /** 关注后延迟秒数 */
    private Integer mpRegistrationDelaySeconds;

    /** 同活动已有 lead 则跳过：0=否 1=是 */
    private String mpRegistrationSkipIfLeadExists;

    /** 本活动默认微信公众号 AppID */
    @JsonAlias("wechat_mp_app_id")
    private String wechatMpAppId;

    /** 本次请求是否显式更新 wechatMpAppId（含清空为 null） */
    private transient Boolean wechatMpAppIdTouched;

    /** 是否开放 H5/CRM 报名 */
    private Boolean registrationEnabled;

    /** 活动级报名截止时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date registrationDeadline;

    /** 满员时是否进入候补 */
    private Boolean waitlistEnabled;

    /** 是否启用现场签到 */
    private Boolean checkInEnabled;

    /** 允许无预约现场补录并签到 */
    private Boolean allowWalkIn;

    /** 默认场次 ID */
    private String defaultSessionId;

    /** 活动默认门店（自动转化 orgId 回退） */
    private String defaultOrgId;

    /** 报名时自动转化（P2） */
    private Boolean autoConvertOnRegistration;

    public void setEventId(String eventId) 
    {
        this.eventId = eventId;
    }

    public String getEventId() 
    {
        return eventId;
    }
    public void setEventType(String eventType) 
    {
        this.eventType = eventType;
    }

    public String getEventType() 
    {
        return eventType;
    }
    public void setSubject(String subject) 
    {
        this.subject = subject;
    }

    public String getSubject() 
    {
        return subject;
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
    public void setMobilePhone(String mobilePhone) 
    {
        this.mobilePhone = mobilePhone;
    }

    public String getMobilePhone() 
    {
        return mobilePhone;
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
    public void setEventSource(String eventSource) 
    {
        this.eventSource = eventSource;
    }

    public String getEventSource() 
    {
        return eventSource;
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
    public void setBudget(BigDecimal budget) 
    {
        this.budget = budget;
    }

    public BigDecimal getBudget() 
    {
        return budget;
    }
    public void setPaid(BigDecimal paid) 
    {
        this.paid = paid;
    }

    public BigDecimal getPaid() 
    {
        return paid;
    }
    public void setBalance(BigDecimal balance) 
    {
        this.balance = balance;
    }

    public BigDecimal getBalance() 
    {
        return balance;
    }
    public void setLocation(String location) 
    {
        this.location = location;
    }

    public String getLocation() 
    {
        return location;
    }
    public void setDescription(String description) 
    {
        this.description = description;
    }

    public String getDescription() 
    {
        return description;
    }
    public void setAgenda(String agenda)
    {
        this.agenda = agenda;
    }

    public String getAgenda()
    {
        return agenda;
    }
    public void setLanguage(String language)
    {
        this.language = language;
    }

    public String getLanguage()
    {
        return language;
    }
    public void setStartTime(Date startTime) 
    {
        this.startTime = startTime;
    }

    public Date getStartTime() 
    {
        return startTime;
    }
    public void setEndTime(Date endTime) 
    {
        this.endTime = endTime;
    }

    public Date getEndTime() 
    {
        return endTime;
    }
    public void setUserId(Long userId) 
    {
        this.userId = userId;
    }

    public Long getUserId() 
    {
        return userId;
    }
    public void setDeptId(Long deptId) 
    {
        this.deptId = deptId;
    }

    public Long getDeptId() 
    {
        return deptId;
    }
    public void setStatus(String status) 
    {
        this.status = status;
    }

    public String getStatus() 
    {
        return status;
    }
    public void setAvatar(String avatar) 
    {
        this.avatar = avatar;
    }

    public String getAvatar() 
    {
        return avatar;
    }

    public void setRegistrationTemplateId(String registrationTemplateId)
    {
        this.registrationTemplateId = registrationTemplateId;
    }

    public String getRegistrationTemplateId()
    {
        return registrationTemplateId;
    }

    public void setRegistrationFormJson(String registrationFormJson)
    {
        this.registrationFormJson = registrationFormJson;
    }

    public String getRegistrationFormJson()
    {
        return registrationFormJson;
    }

    public void setRegistrationFormTouched(Boolean registrationFormTouched)
    {
        this.registrationFormTouched = registrationFormTouched;
    }

    public Boolean getRegistrationFormTouched()
    {
        return registrationFormTouched;
    }

    @JsonProperty("mpRegistrationAutomationEnabled")
    public Boolean getMpRegistrationAutomationEnabled()
    {
        return "1".equals(mpRegistrationAutomationEnabled);
    }

    @JsonProperty("mpRegistrationAutomationEnabled")
    public void setMpRegistrationAutomationEnabled(Boolean enabled)
    {
        if (enabled == null)
        {
            this.mpRegistrationAutomationEnabled = null;
            return;
        }
        this.mpRegistrationAutomationEnabled = Boolean.TRUE.equals(enabled) ? "1" : "0";
    }

    public String getMpRegistrationAutomationEnabledRaw()
    {
        return mpRegistrationAutomationEnabled;
    }

    public void setMpRegistrationAutomationEnabledRaw(String mpRegistrationAutomationEnabled)
    {
        this.mpRegistrationAutomationEnabled = mpRegistrationAutomationEnabled;
    }

    public Long getMpRegistrationPresetId()
    {
        return mpRegistrationPresetId;
    }

    public void setMpRegistrationPresetId(Long mpRegistrationPresetId)
    {
        this.mpRegistrationPresetId = mpRegistrationPresetId;
    }

    @com.fasterxml.jackson.annotation.JsonIgnore
    public Integer getMpRegistrationDelaySeconds()
    {
        return mpRegistrationDelaySeconds;
    }

    @com.fasterxml.jackson.annotation.JsonGetter("mpRegistrationDelaySeconds")
    public Integer getMpRegistrationDelaySecondsJson()
    {
        return mpRegistrationDelaySeconds != null ? mpRegistrationDelaySeconds : 0;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("mpRegistrationDelaySeconds")
    public void setMpRegistrationDelaySeconds(Integer mpRegistrationDelaySeconds)
    {
        this.mpRegistrationDelaySeconds = mpRegistrationDelaySeconds;
    }

    @JsonProperty("mpRegistrationSkipIfLeadExists")
    public Boolean getMpRegistrationSkipIfLeadExists()
    {
        return !"0".equals(mpRegistrationSkipIfLeadExists);
    }

    @JsonProperty("mpRegistrationSkipIfLeadExists")
    public void setMpRegistrationSkipIfLeadExists(Boolean skip)
    {
        if (skip == null)
        {
            this.mpRegistrationSkipIfLeadExists = null;
            return;
        }
        this.mpRegistrationSkipIfLeadExists = Boolean.FALSE.equals(skip) ? "0" : "1";
    }

    public String getMpRegistrationSkipIfLeadExistsRaw()
    {
        return mpRegistrationSkipIfLeadExists;
    }

    public void setMpRegistrationSkipIfLeadExistsRaw(String mpRegistrationSkipIfLeadExists)
    {
        this.mpRegistrationSkipIfLeadExists = mpRegistrationSkipIfLeadExists;
    }

    public String getWechatMpAppId()
    {
        return wechatMpAppId;
    }

    public void setWechatMpAppId(String wechatMpAppId)
    {
        this.wechatMpAppId = wechatMpAppId;
    }

    public Boolean getWechatMpAppIdTouched()
    {
        return wechatMpAppIdTouched;
    }

    public void setWechatMpAppIdTouched(Boolean wechatMpAppIdTouched)
    {
        this.wechatMpAppIdTouched = wechatMpAppIdTouched;
    }

    @JsonProperty("registrationEnabled")
    public Boolean getRegistrationEnabled()
    {
        return registrationEnabled == null || registrationEnabled;
    }

    @JsonProperty("registrationEnabled")
    public void setRegistrationEnabled(Boolean registrationEnabled)
    {
        this.registrationEnabled = registrationEnabled;
    }

    public Date getRegistrationDeadline()
    {
        return registrationDeadline;
    }

    public void setRegistrationDeadline(Date registrationDeadline)
    {
        this.registrationDeadline = registrationDeadline;
    }

    @JsonProperty("waitlistEnabled")
    public Boolean getWaitlistEnabled()
    {
        return Boolean.TRUE.equals(waitlistEnabled);
    }

    @JsonProperty("waitlistEnabled")
    public void setWaitlistEnabled(Boolean waitlistEnabled)
    {
        this.waitlistEnabled = waitlistEnabled;
    }

    @JsonProperty("checkInEnabled")
    public Boolean getCheckInEnabled()
    {
        return checkInEnabled == null || checkInEnabled;
    }

    @JsonProperty("checkInEnabled")
    public void setCheckInEnabled(Boolean checkInEnabled)
    {
        this.checkInEnabled = checkInEnabled;
    }

    @JsonProperty("allowWalkIn")
    public Boolean getAllowWalkIn()
    {
        return allowWalkIn == null || allowWalkIn;
    }

    @JsonProperty("allowWalkIn")
    public void setAllowWalkIn(Boolean allowWalkIn)
    {
        this.allowWalkIn = allowWalkIn;
    }

    public String getDefaultSessionId()
    {
        return defaultSessionId;
    }

    public void setDefaultSessionId(String defaultSessionId)
    {
        this.defaultSessionId = defaultSessionId;
    }

    public String getDefaultOrgId()
    {
        return defaultOrgId;
    }

    public void setDefaultOrgId(String defaultOrgId)
    {
        this.defaultOrgId = defaultOrgId;
    }

    public Boolean getAutoConvertOnRegistration()
    {
        return autoConvertOnRegistration;
    }

    public void setAutoConvertOnRegistration(Boolean autoConvertOnRegistration)
    {
        this.autoConvertOnRegistration = autoConvertOnRegistration;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("eventId", getEventId())
            .append("eventType", getEventType())
            .append("subject", getSubject())
            .append("owner", getOwner())
            .append("contactId", getContactId())
            .append("contactName", getContactName())
            .append("stage", getStage())
            .append("mobilePhone", getMobilePhone())
            .append("workPhone", getWorkPhone())
            .append("workEmail", getWorkEmail())
            .append("eventSource", getEventSource())
            .append("website", getWebsite())
            .append("industry", getIndustry())
            .append("budget", getBudget())
            .append("paid", getPaid())
            .append("balance", getBalance())
            .append("location", getLocation())
            .append("description", getDescription())
            .append("agenda", getAgenda())
            .append("language", getLanguage())
            .append("startTime", getStartTime())
            .append("endTime", getEndTime())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("userId", getUserId())
            .append("deptId", getDeptId())
            .append("status", getStatus())
            .append("avatar", getAvatar())
            .append("registrationTemplateId", getRegistrationTemplateId())
            .append("registrationFormJson", getRegistrationFormJson())
            .append("mpRegistrationAutomationEnabled", getMpRegistrationAutomationEnabled())
            .append("mpRegistrationPresetId", getMpRegistrationPresetId())
            .append("mpRegistrationDelaySeconds", getMpRegistrationDelaySeconds())
            .append("mpRegistrationSkipIfLeadExists", getMpRegistrationSkipIfLeadExists())
            .append("wechatMpAppId", getWechatMpAppId())
            .toString();
    }
}
