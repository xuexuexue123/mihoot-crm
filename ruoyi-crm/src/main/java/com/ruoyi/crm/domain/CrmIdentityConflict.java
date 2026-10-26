package com.ruoyi.crm.domain;

import java.util.Date;

import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 身份冲突待人工合并对象 crm_identity_conflict
 *
 * <p>同一身份值被解析到不同 Contact 时记录，供运营在前端确认后合并。
 */
public class CrmIdentityConflict extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long id;

    /** 身份类型：mobile_phone / email / wechat_unionid / wecom_external_userid ... */
    private String identityType;

    /** 身份值（归一化后） */
    private String identityValue;

    /** 该身份当前绑定的 Contact */
    private String existingContactId;

    /** 本次尝试绑定的 Contact（可能为空串） */
    private String incomingContactId;

    /** 来源：wecom_callback / mp_callback / event_registration ... */
    private String source;

    /** 状态：pending | resolved | ignored */
    private String status;

    private String resolvedBy;

    private Date resolvedAt;

    private String rawContext;

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getIdentityType()
    {
        return identityType;
    }

    public void setIdentityType(String identityType)
    {
        this.identityType = identityType;
    }

    public String getIdentityValue()
    {
        return identityValue;
    }

    public void setIdentityValue(String identityValue)
    {
        this.identityValue = identityValue;
    }

    public String getExistingContactId()
    {
        return existingContactId;
    }

    public void setExistingContactId(String existingContactId)
    {
        this.existingContactId = existingContactId;
    }

    public String getIncomingContactId()
    {
        return incomingContactId;
    }

    public void setIncomingContactId(String incomingContactId)
    {
        this.incomingContactId = incomingContactId;
    }

    public String getSource()
    {
        return source;
    }

    public void setSource(String source)
    {
        this.source = source;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getResolvedBy()
    {
        return resolvedBy;
    }

    public void setResolvedBy(String resolvedBy)
    {
        this.resolvedBy = resolvedBy;
    }

    public Date getResolvedAt()
    {
        return resolvedAt;
    }

    public void setResolvedAt(Date resolvedAt)
    {
        this.resolvedAt = resolvedAt;
    }

    public String getRawContext()
    {
        return rawContext;
    }

    public void setRawContext(String rawContext)
    {
        this.rawContext = rawContext;
    }
}
