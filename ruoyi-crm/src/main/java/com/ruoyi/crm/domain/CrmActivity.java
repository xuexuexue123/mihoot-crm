package com.ruoyi.crm.domain;

import java.util.List;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 业务活动 / Activity Timeline 对象 crm_activity
 * 
 * @author luohd
 * @date 2024-01-21
 */
public class CrmActivity extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 编号 */
    private String activityId;

    /** 名称 */
    @Excel(name = "名称")
    private String activitySubject;

    /** 活动类型 */
    @Excel(name = "活动类型")
    private String communicationType;

    /** 日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date startedDate;


    /** 指派人 */
    @Excel(name = "指派人")
    private String assignedTo;

    /** 指派人ID */
    @Excel(name = "指派人ID")
    private String assignedToid;

    /** 状态 */
    @Excel(name = "状态")
    private String statusDetail;

    /** 联系人 */
    @Excel(name = "联系人")
    private String contacts;

    /** 内部员工 */
    @Excel(name = "内部员工")
    private String internalStaff;

    /** 关联的任务 */
    @Excel(name = "关联的任务")
    private String tasks;

    /** 结果 */
    @Excel(name = "结果")
    private String outcome;

    /** 备注 */
    @Excel(name = "备注")
    private String comments;


    private Long userId;

    private Long deptId;

    /** 任务信息 */
    private List<CrmTask> crmTaskList;

    // ========================
    // Activity Timeline 字段（Subject/Event 语义模型）
    // ========================

    /** pulseId（兼容 crm_pulse 迁移） */
    private String pulseId;

    /** 父编号（兼容 crm_pulse："0"=timeline, "1"=notes） */
    private String parentId;

    /** 顺序 */
    private Long orderId;

    /** 旧 pulse 事件类型（逐步废弃） */
    private String pulseType;

    /** 主体类型: Lead|Contact|Account|Opportunity */
    private String subjectType;

    /** 主体ID */
    private String subjectId;

    /** 事件类型: lead.created|ai.scored|payment.deposit 等 */
    private String eventType;

    /** 事件中文名称 */
    private String eventName;

    /** 重要性: 10-30普通, 40-60重要, 70-100关键里程碑 */
    private Integer importance;

    /** 事件结束时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endedDate;

    /** 事件来源: manual|system|workflow|agent|douyin|wecom|... */
    private String source;

    /** 结构化扩展信息 */
    private String payload;

    /** 事件状态: pending|in_progress|completed|cancelled */
    private String status;

    /** 父事件ID，支持事件嵌套 */
    private String parentEventId;

    /** 事件来源业务对象类型（兼容旧查询） */
    private String relateType;

    /** 事件来源业务对象ID（兼容旧查询） */
    private String relateId;

    /** 关联对象显示名 */
    private String relateName;

    /** 文本摘要 */
    private String messageData;

    /** 头像 */
    private String avatar;

    /** 创建者ID */
    private String creatorId;

    /** Activity Hub: 事件大类 channel|crm|ai|engagement|conversion */
    private String eventCategory;

    /** Activity Hub: 渠道来源 wecom|wechat_mp|wechat_miniapp|douyin|manual|agent */
    private String channel;

    /** Activity Hub: 渠道侧原始事件ID，用于幂等去重 */
    private String channelEventId;

    /** Activity Hub: 是否已被 customer_metrics 聚合消费 */
    private Boolean isAggregated;

    /** Activity Hub: 聚合消费时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date aggregatedAt;

    /** Activity Hub: 技术上下文元数据（设备/IP/UTM），JSONB */
    private String context;

    // ========================
    // 查询参数（非持久化字段）
    // ========================

    /** 最低重要性 */
    private Integer importanceMin;

    /** 起始时间（ISO 字符串，用于查询传参） */
    private String startedDateFrom;

    /** 截止时间（ISO 字符串，用于查询传参） */
    private String startedDateTo;

    /** 是否包含子 Subject 继承事件 */
    private Boolean includeChildSubjects;

    // ========================
    // Getter / Setter
    // ========================

    public void setActivityId(String activityId) 
    {
        this.activityId = activityId;
    }

    public String getActivityId() 
    {
        return activityId;
    }
    public void setActivitySubject(String activitySubject) 
    {
        this.activitySubject = activitySubject;
    }

    public String getActivitySubject() 
    {
        return activitySubject;
    }
    public void setCommunicationType(String communicationType) 
    {
        this.communicationType = communicationType;
    }

    public String getCommunicationType() 
    {
        return communicationType;
    }
    public void setStartedDate(Date startedDate) 
    {
        this.startedDate = startedDate;
    }

    public Date getStartedDate() 
    {
        return startedDate;
    }
    public void setAssignedTo(String assignedTo) 
    {
        this.assignedTo = assignedTo;
    }

    public String getAssignedTo() 
    {
        return assignedTo;
    }
    public void setAssignedToid(String assignedToid) 
    {
        this.assignedToid = assignedToid;
    }

    public String getAssignedToid() 
    {
        return assignedToid;
    }
    public void setStatusDetail(String statusDetail) 
    {
        this.statusDetail = statusDetail;
    }

    public String getStatusDetail() 
    {
        return statusDetail;
    }
    public void setContacts(String contacts) 
    {
        this.contacts = contacts;
    }

    public String getContacts() 
    {
        return contacts;
    }
    public void setInternalStaff(String internalStaff) 
    {
        this.internalStaff = internalStaff;
    }

    public String getInternalStaff() 
    {
        return internalStaff;
    }
    public void setTasks(String tasks) 
    {
        this.tasks = tasks;
    }

    public String getTasks() 
    {
        return tasks;
    }
    public void setOutcome(String outcome) 
    {
        this.outcome = outcome;
    }

    public String getOutcome() 
    {
        return outcome;
    }
    public void setComments(String comments) 
    {
        this.comments = comments;
    }

    public String getComments() 
    {
        return comments;
    }

    public List<CrmTask> getCrmTaskList()
    {
        return crmTaskList;
    }

    public void setCrmTaskList(List<CrmTask> crmTaskList)
    {
        this.crmTaskList = crmTaskList;
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

    // ---- Timeline getter/setter ----

    public String getPulseId() { return pulseId; }
    public void setPulseId(String pulseId) { this.pulseId = pulseId; }

    public String getParentId() { return parentId; }
    public void setParentId(String parentId) { this.parentId = parentId; }

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    public String getPulseType() { return pulseType; }
    public void setPulseType(String pulseType) { this.pulseType = pulseType; }

    public String getSubjectType() { return subjectType; }
    public void setSubjectType(String subjectType) { this.subjectType = subjectType; }

    public String getSubjectId() { return subjectId; }
    public void setSubjectId(String subjectId) { this.subjectId = subjectId; }

    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }

    public String getEventName() { return eventName; }
    public void setEventName(String eventName) { this.eventName = eventName; }

    public Integer getImportance() { return importance; }
    public void setImportance(Integer importance) { this.importance = importance; }

    public Date getEndedDate() { return endedDate; }
    public void setEndedDate(Date endedDate) { this.endedDate = endedDate; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public String getPayload() { return payload; }
    public void setPayload(String payload) { this.payload = payload; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getParentEventId() { return parentEventId; }
    public void setParentEventId(String parentEventId) { this.parentEventId = parentEventId; }

    public String getRelateType() { return relateType; }
    public void setRelateType(String relateType) { this.relateType = relateType; }

    public String getRelateId() { return relateId; }
    public void setRelateId(String relateId) { this.relateId = relateId; }

    public String getRelateName() { return relateName; }
    public void setRelateName(String relateName) { this.relateName = relateName; }

    public String getMessageData() { return messageData; }
    public void setMessageData(String messageData) { this.messageData = messageData; }

    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }

    public String getCreatorId() { return creatorId; }
    public void setCreatorId(String creatorId) { this.creatorId = creatorId; }

    // ---- Activity Hub 字段 getter/setter ----

    public String getEventCategory() { return eventCategory; }
    public void setEventCategory(String eventCategory) { this.eventCategory = eventCategory; }

    public String getChannel() { return channel; }
    public void setChannel(String channel) { this.channel = channel; }

    public String getChannelEventId() { return channelEventId; }
    public void setChannelEventId(String channelEventId) { this.channelEventId = channelEventId; }

    public Boolean getIsAggregated() { return isAggregated; }
    public void setIsAggregated(Boolean isAggregated) { this.isAggregated = isAggregated; }

    public Date getAggregatedAt() { return aggregatedAt; }
    public void setAggregatedAt(Date aggregatedAt) { this.aggregatedAt = aggregatedAt; }

    public String getContext() { return context; }
    public void setContext(String context) { this.context = context; }

    // ---- 查询参数 getter/setter ----

    public Integer getImportanceMin() { return importanceMin; }
    public void setImportanceMin(Integer importanceMin) { this.importanceMin = importanceMin; }

    public String getStartedDateFrom() { return startedDateFrom; }
    public void setStartedDateFrom(String startedDateFrom) { this.startedDateFrom = startedDateFrom; }

    public String getStartedDateTo() { return startedDateTo; }
    public void setStartedDateTo(String startedDateTo) { this.startedDateTo = startedDateTo; }

    public Boolean getIncludeChildSubjects() { return includeChildSubjects; }
    public void setIncludeChildSubjects(Boolean includeChildSubjects) { this.includeChildSubjects = includeChildSubjects; }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("activityId", getActivityId())
            .append("activitySubject", getActivitySubject())
            .append("communicationType", getCommunicationType())
            .append("startedDate", getStartedDate())
            .append("assignedTo", getAssignedTo())
            .append("assignedToid", getAssignedToid())
            .append("statusDetail", getStatusDetail())
            .append("contacts", getContacts())
            .append("internalStaff", getInternalStaff())
            .append("tasks", getTasks())
            .append("outcome", getOutcome())
            .append("comments", getComments())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("crmTaskList", getCrmTaskList())
            .append("pulseId", getPulseId())
            .append("subjectType", getSubjectType())
            .append("subjectId", getSubjectId())
            .append("eventType", getEventType())
            .append("eventName", getEventName())
            .append("importance", getImportance())
            .append("source", getSource())
            .append("status", getStatus())
            .append("parentEventId", getParentEventId())
            .toString();
    }
}
