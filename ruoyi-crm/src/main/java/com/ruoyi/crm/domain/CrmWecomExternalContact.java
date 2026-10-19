package com.ruoyi.crm.domain;

import java.util.Date;

import com.ruoyi.common.core.domain.BaseEntity;

public class CrmWecomExternalContact extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long id;
    private String externalUserid;
    private String followUserid;
    private String state;
    private String crmLeadId;
    private String crmContactId;
    private String crmEventId;
    private String rawJson;

    /** 跟进人对客户的备注名 */
    private String remark;

    /** 添加来源 */
    private Integer addWay;

    /** 跟进人打的标签（JSON） */
    private String tags;

    /** 跟进人描述 */
    private String description;

    /** 操作人 userid（执行添加操作的人，可能不等于 followUserid） */
    private String operUserid;

    /** 企微侧客户添加时间 */
    private Date followCreateTime;

    /** 备注手机号列表（JSON 数组字符串） */
    private String remarkMobiles;

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

    public String getCrmLeadId()
    {
        return crmLeadId;
    }

    public void setCrmLeadId(String crmLeadId)
    {
        this.crmLeadId = crmLeadId;
    }

    public String getCrmContactId()
    {
        return crmContactId;
    }

    public void setCrmContactId(String crmContactId)
    {
        this.crmContactId = crmContactId;
    }

    public String getCrmEventId()
    {
        return crmEventId;
    }

    public void setCrmEventId(String crmEventId)
    {
        this.crmEventId = crmEventId;
    }

    public String getRawJson()
    {
        return rawJson;
    }

    public void setRawJson(String rawJson)
    {
        this.rawJson = rawJson;
    }

    public String getRemark()
    {
        return remark;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
    }

    public Integer getAddWay()
    {
        return addWay;
    }

    public void setAddWay(Integer addWay)
    {
        this.addWay = addWay;
    }

    public String getTags()
    {
        return tags;
    }

    public void setTags(String tags)
    {
        this.tags = tags;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getOperUserid()
    {
        return operUserid;
    }

    public void setOperUserid(String operUserid)
    {
        this.operUserid = operUserid;
    }

    public Date getFollowCreateTime()
    {
        return followCreateTime;
    }

    public void setFollowCreateTime(Date followCreateTime)
    {
        this.followCreateTime = followCreateTime;
    }

    public String getRemarkMobiles()
    {
        return remarkMobiles;
    }

    public void setRemarkMobiles(String remarkMobiles)
    {
        this.remarkMobiles = remarkMobiles;
    }
}
