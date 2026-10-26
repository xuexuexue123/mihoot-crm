package com.ruoyi.crm.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 客户分群 crm_segment
 */
public class CrmSegment extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long id;
    private String tenantId;
    private String name;
    private String description;
    private String ruleJson;
    private String type;
    private Integer estSize;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date computedAt;

    private String status;

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getTenantId()
    {
        return tenantId;
    }

    public void setTenantId(String tenantId)
    {
        this.tenantId = tenantId;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getRuleJson()
    {
        return ruleJson;
    }

    public void setRuleJson(String ruleJson)
    {
        this.ruleJson = ruleJson;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public Integer getEstSize()
    {
        return estSize;
    }

    public void setEstSize(Integer estSize)
    {
        this.estSize = estSize;
    }

    public Date getComputedAt()
    {
        return computedAt;
    }

    public void setComputedAt(Date computedAt)
    {
        this.computedAt = computedAt;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }
}
