package com.ruoyi.crm.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 受访者对象 crm_respondents
 * 
 * @author luohd
 * @date 2025-03-29
 */
public class CrmRespondents extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 编号 */
    private String id;

    /** 关联ID */
    @Excel(name = "关联ID")
    private String relateId;

    /** 关联名称 */
    @Excel(name = "关联名称")
    private String relateName;

    /** 关联ID */
    @Excel(name = "关联ID")
    private String contactId;

    /** session id */
    @Excel(name = "session id")
    private String sessionId;

    /** 额外信息 */
    @Excel(name = "额外信息")
    private String metadata;

    /** 描述 */
    @Excel(name = "描述")
    private String description;

    /** 状态 */
    @Excel(name = "状态")
    private String status;

    /** 调查问题 */
    @Excel(name = "调查问题")
    private String surveyJson;

    /** 开始时间 */
    @JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    @Excel(name = "开始时间", width = 30, dateFormat = "yyyy-MM-dd hh:mm:ss")
    private Date startedAt;

    /** 完成时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "完成时间", width = 30, dateFormat = "yyyy-MM-dd hh:mm:ss")
    private Date completedAt;

    public void setId(String id) 
    {
        this.id = id;
    }

    public String getId() 
    {
        return id;
    }
    public void setRelateId(String relateId) 
    {
        this.relateId = relateId;
    }

    public String getRelateId() 
    {
        return relateId;
    }
    public void setRelateName(String relateName) 
    {
        this.relateName = relateName;
    }

    public String getRelateName() 
    {
        return relateName;
    }
    public void setContactId(String contactId) 
    {
        this.contactId = contactId;
    }

    public String getContactId() 
    {
        return contactId;
    }
    public void setSessionId(String sessionId) 
    {
        this.sessionId = sessionId;
    }

    public String getSessionId() 
    {
        return sessionId;
    }
    public void setMetadata(String metadata) 
    {
        this.metadata = metadata;
    }

    public String getMetadata() 
    {
        return metadata;
    }
    public void setDescription(String description) 
    {
        this.description = description;
    }

    public String getDescription() 
    {
        return description;
    }
    public void setStatus(String status) 
    {
        this.status = status;
    }

    public String getStatus() 
    {
        return status;
    }
    public void setSurveyJson(String surveyJson) 
    {
        this.surveyJson = surveyJson;
    }

    public String getSurveyJson() 
    {
        return surveyJson;
    }
    public void setStartedAt(Date startedAt) 
    {
        this.startedAt = startedAt;
    }

    public Date getStartedAt() 
    {
        return startedAt;
    }
    public void setCompletedAt(Date completedAt) 
    {
        this.completedAt = completedAt;
    }

    public Date getCompletedAt() 
    {
        return completedAt;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("relateId", getRelateId())
            .append("relateName", getRelateName())
            .append("contactId", getContactId())
            .append("sessionId", getSessionId())
            .append("metadata", getMetadata())
            .append("description", getDescription())
            .append("status", getStatus())
            .append("surveyJson", getSurveyJson())
            .append("startedAt", getStartedAt())
            .append("completedAt", getCompletedAt())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
