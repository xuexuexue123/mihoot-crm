package com.ruoyi.ems.domain;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * casetype对象 sys_re_casetype
 *
 * @author luohd
 * @date 2022-12-15
 */
public class SysCasetype extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** id */
    private Long id;

    /** case编码 */
    @Excel(name = "case编码")
    private String caseTypeId;

    /** 排序 */
    @Excel(name = "排序")
    private Long caseSort;

    /** 标签 */
    @Excel(name = "标签")
    private String caseLabel;

    /** case名称 */
    @Excel(name = "case名称")
    private String caseName;

    /** case版本 */
    @Excel(name = "case版本")
    private String caseVersion;

    /** case应用id */
    @Excel(name = "case应用id")
    private String caseApplicationId;

    /** 默认的紧急度 */
    @Excel(name = "默认的紧急度")
    private Long caseUrgency;

    /** case前缀 */
    @Excel(name = "case前缀")
    private String casePrefix;

    /** case图标 */
    @Excel(name = "case图标")
    private String caseIcon;

    /** 状态（0正常 1停用） */
    @Excel(name = "状态", readConverterExp = "0=正常,1=停用")
    private String caseAvailability;

    /** case状态 */
    @Excel(name = "case状态")
    private String caseStatusWork;

    /** case描述 */
    @Excel(name = "case描述")
    private String caseDescription;

    /** 初始值 */
    @Excel(name = "初始值")
    private String initialData;

    /** 跟踪值 */
    @Excel(name = "跟踪值")
    private Long fieldTracking;

    /** 初始值 */
    @Excel(name = "邮件启动账户")
    private String emailInitialAccount;

    /** 目标时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date goal;

    /** 截止日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date deadline;

    /** 工作列表通知 */
    @Excel(name = "工作列表通知")
    private Long worklistNotify;

    /** 工作组通知 */
    @Excel(name = "工作组通知")
    private Long workgroupNotify;


    /** stage信息 */
    private List<SysStage> sysStageList;

    /** stage信息 */
    private List<SysDatatype> sysDatatypeList;

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId()
    {
        return id;
    }
    public void setCaseTypeId(String caseTypeId)
    {
        this.caseTypeId = caseTypeId;
    }

    public String getCaseTypeId()
    {
        return caseTypeId;
    }
    public void setCaseSort(Long caseSort)
    {
        this.caseSort = caseSort;
    }

    public Long getCaseSort()
    {
        return caseSort;
    }
    public void setCaseLabel(String caseLabel)
    {
        this.caseLabel = caseLabel;
    }

    public String getCaseLabel()
    {
        return caseLabel;
    }
    public void setCaseName(String caseName)
    {
        this.caseName = caseName;
    }

    public String getCaseName()
    {
        return caseName;
    }
    public void setCaseVersion(String caseVersion)
    {
        this.caseVersion = caseVersion;
    }

    public String getCaseVersion()
    {
        return caseVersion;
    }
    public void setCaseApplicationId(String caseApplicationId)
    {
        this.caseApplicationId = caseApplicationId;
    }

    public String getCaseApplicationId()
    {
        return caseApplicationId;
    }
    public void setCaseUrgency(Long caseUrgency)
    {
        this.caseUrgency = caseUrgency;
    }

    public Long getCaseUrgency()
    {
        return caseUrgency;
    }
    public void setCasePrefix(String casePrefix)
    {
        this.casePrefix = casePrefix;
    }

    public String getCasePrefix()
    {
        return casePrefix;
    }
    public void setCaseIcon(String caseIcon)
    {
        this.caseIcon = caseIcon;
    }

    public String getCaseIcon()
    {
        return caseIcon;
    }
    public void setCaseAvailability(String caseAvailability)
    {
        this.caseAvailability = caseAvailability;
    }

    public String getCaseAvailability()
    {
        return caseAvailability;
    }
    public void setCaseStatusWork(String caseStatusWork)
    {
        this.caseStatusWork = caseStatusWork;
    }

    public String getCaseStatusWork()
    {
        return caseStatusWork;
    }
    public void setCaseDescription(String caseDescription)
    {
        this.caseDescription = caseDescription;
    }

    public String getCaseDescription()
    {
        return caseDescription;
    }

    public List<SysStage> getSysStageList()
    {
        return sysStageList;
    }

    public void setSysStageList(List<SysStage> sysStageList)
    {
        this.sysStageList = sysStageList;
    }

    public List<SysDatatype> getSysDatatypeList()
    {
        return sysDatatypeList;
    }

    public void setSysDatatypeList(List<SysDatatype> sysDatatypeList)
    {
        this.sysDatatypeList = sysDatatypeList;
    }

    public String getInitialData() {
        return initialData;
    }

    public void setInitialData(String initialData) {
        this.initialData = initialData;
    }

    public Long getFieldTracking() {
        return fieldTracking;
    }

    public void setFieldTracking(Long fieldTracking) {
        this.fieldTracking = fieldTracking;
    }

    public String getEmailInitialAccount() {
        return emailInitialAccount;
    }

    public void setEmailInitialAccount(String emailInitialAccount) {
        this.emailInitialAccount = emailInitialAccount;
    }

    public Date getGoal() {
        return goal;
    }

    public void setGoal(Date goal) {
        this.goal = goal;
    }

    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    public Long getWorklistNotify() {
        return worklistNotify;
    }

    public void setWorklistNotify(Long worklistNotify) {
        this.worklistNotify = worklistNotify;
    }

    public Long getWorkgroupNotify() {
        return workgroupNotify;
    }

    public void setWorkgroupNotify(Long workgroupNotify) {
        this.workgroupNotify = workgroupNotify;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("caseTypeId", getCaseTypeId())
                .append("caseSort", getCaseSort())
                .append("caseLabel", getCaseLabel())
                .append("caseName", getCaseName())
                .append("caseVersion", getCaseVersion())
                .append("caseApplicationId", getCaseApplicationId())
                .append("caseUrgency", getCaseUrgency())
                .append("casePrefix", getCasePrefix())
                .append("caseIcon", getCaseIcon())
                .append("caseAvailability", getCaseAvailability())
                .append("caseStatusWork", getCaseStatusWork())
                .append("createBy", getCreateBy())
                .append("createTime", getCreateTime())
                .append("updateBy", getUpdateBy())
                .append("updateTime", getUpdateTime())
                .append("caseDescription", getCaseDescription())
                .append("sysStageList", getSysStageList())
                .append("initialData", getInitialData())
                .append("fieldTracking", getFieldTracking())
                .append("emailInitialAccount", getEmailInitialAccount())
                .append("goal", getGoal())
                .append("deadline", getDeadline())
                .append("worklistNotify", getWorklistNotify())
                .append("workgroupNotify", getWorkgroupNotify())
                .toString();
    }
}
