package com.ruoyi.crm.domain;

import java.util.Date;

/**
 * 官网访客会话（对应 crm_website_visitor_session）
 */
public class CrmWebsiteVisitorSession
{
    private String id;
    private String sessionId;
    private String subjectType;
    private String subjectId;
    private Date boundAt;
    private String visitorTokenHash;
    private Date createTime;
    private Date updateTime;

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getSessionId()
    {
        return sessionId;
    }

    public void setSessionId(String sessionId)
    {
        this.sessionId = sessionId;
    }

    public String getSubjectType()
    {
        return subjectType;
    }

    public void setSubjectType(String subjectType)
    {
        this.subjectType = subjectType;
    }

    public String getSubjectId()
    {
        return subjectId;
    }

    public void setSubjectId(String subjectId)
    {
        this.subjectId = subjectId;
    }

    public Date getBoundAt()
    {
        return boundAt;
    }

    public void setBoundAt(Date boundAt)
    {
        this.boundAt = boundAt;
    }

    public String getVisitorTokenHash()
    {
        return visitorTokenHash;
    }

    public void setVisitorTokenHash(String visitorTokenHash)
    {
        this.visitorTokenHash = visitorTokenHash;
    }

    public Date getCreateTime()
    {
        return createTime;
    }

    public void setCreateTime(Date createTime)
    {
        this.createTime = createTime;
    }

    public Date getUpdateTime()
    {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime)
    {
        this.updateTime = updateTime;
    }

    public boolean isBound()
    {
        return subjectId != null && !subjectId.isEmpty();
    }
}
