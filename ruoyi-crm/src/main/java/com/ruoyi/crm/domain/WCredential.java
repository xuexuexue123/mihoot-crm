package com.ruoyi.crm.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * WCredential对象 w_credential
 * 
 * @author luohd
 * @date 2026-05-19
 */
public class WCredential extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private String id;

    @Excel(name = "名称")
    private String name;

    @Excel(name = "凭证值")
    private String value;

    @Excel(name = "类型")
    private String type;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date updatedAt;

    @Excel(name = "用户ID")
    private String userId;

    @Excel(name = "组织ID")
    private String organizationId;

    @Excel(name = "主凭证")
    private Long isPrimary;

    public void setId(String id)
    {
        this.id = id;
    }

    public String getId()
    {
        return id;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    public void setValue(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public String getType()
    {
        return type;
    }

    public void setCreatedAt(Date createdAt)
    {
        this.createdAt = createdAt;
    }

    public Date getCreatedAt()
    {
        return createdAt;
    }

    public void setUpdatedAt(Date updatedAt)
    {
        this.updatedAt = updatedAt;
    }

    public Date getUpdatedAt()
    {
        return updatedAt;
    }

    public void setUserId(String userId)
    {
        this.userId = userId;
    }

    public String getUserId()
    {
        return userId;
    }

    public void setOrganizationId(String organizationId)
    {
        this.organizationId = organizationId;
    }

    public String getOrganizationId()
    {
        return organizationId;
    }

    public void setIsPrimary(Long isPrimary)
    {
        this.isPrimary = isPrimary;
    }

    public Long getIsPrimary()
    {
        return isPrimary;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("name", getName())
            .append("value", getValue())
            .append("type", getType())
            .append("createdAt", getCreatedAt())
            .append("updatedAt", getUpdatedAt())
            .append("userId", getUserId())
            .append("organizationId", getOrganizationId())
            .append("isPrimary", getIsPrimary())
            .toString();
    }
}
