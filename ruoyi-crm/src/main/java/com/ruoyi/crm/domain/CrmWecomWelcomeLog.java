package com.ruoyi.crm.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 企业微信欢迎语发送/跳过审计
 */
public class CrmWecomWelcomeLog extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long id;
    private String externalUserid;
    private String followUserid;
    private String state;
    private String welcomeTextUsed;
    private String textSource;
    private Integer errcode;
    private String errmsg;
    private String skippedReason;

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getExternalUserid()
    {
        return externalUserid;
    }

    public void setExternalUserid(String externalUserid)
    {
        this.externalUserid = externalUserid;
    }

    public String getFollowUserid()
    {
        return followUserid;
    }

    public void setFollowUserid(String followUserid)
    {
        this.followUserid = followUserid;
    }

    public String getState()
    {
        return state;
    }

    public void setState(String state)
    {
        this.state = state;
    }

    public String getWelcomeTextUsed()
    {
        return welcomeTextUsed;
    }

    public void setWelcomeTextUsed(String welcomeTextUsed)
    {
        this.welcomeTextUsed = welcomeTextUsed;
    }

    public String getTextSource()
    {
        return textSource;
    }

    public void setTextSource(String textSource)
    {
        this.textSource = textSource;
    }

    public Integer getErrcode()
    {
        return errcode;
    }

    public void setErrcode(Integer errcode)
    {
        this.errcode = errcode;
    }

    public String getErrmsg()
    {
        return errmsg;
    }

    public void setErrmsg(String errmsg)
    {
        this.errmsg = errmsg;
    }

    public String getSkippedReason()
    {
        return skippedReason;
    }

    public void setSkippedReason(String skippedReason)
    {
        this.skippedReason = skippedReason;
    }

    @JsonProperty("createdAt")
    public java.util.Date getCreatedAt()
    {
        return getCreateTime();
    }
}
