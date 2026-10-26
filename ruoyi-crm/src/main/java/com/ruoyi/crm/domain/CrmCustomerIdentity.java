package com.ruoyi.crm.domain;

import com.ruoyi.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 客户身份对象 crm_customer_identity
 *
 * @author luohd
 * @date 2026-06-16
 */
public class CrmCustomerIdentity extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long identityId;

    private String contactId;

    private String identityType;

    private String identityValue;

    private String source;

    private Boolean isPrimary;

    private Boolean isVerified;

    public Long getIdentityId()
    {
        return identityId;
    }

    public void setIdentityId(Long identityId)
    {
        this.identityId = identityId;
    }

    public String getContactId()
    {
        return contactId;
    }

    public void setContactId(String contactId)
    {
        this.contactId = contactId;
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

    public String getSource()
    {
        return source;
    }

    public void setSource(String source)
    {
        this.source = source;
    }

    public Boolean getIsPrimary()
    {
        return isPrimary;
    }

    public void setIsPrimary(Boolean isPrimary)
    {
        this.isPrimary = isPrimary;
    }

    public Boolean getIsVerified()
    {
        return isVerified;
    }

    public void setIsVerified(Boolean isVerified)
    {
        this.isVerified = isVerified;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("identityId", getIdentityId())
            .append("contactId", getContactId())
            .append("identityType", getIdentityType())
            .append("identityValue", getIdentityValue())
            .append("source", getSource())
            .append("isPrimary", getIsPrimary())
            .append("isVerified", getIsVerified())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
