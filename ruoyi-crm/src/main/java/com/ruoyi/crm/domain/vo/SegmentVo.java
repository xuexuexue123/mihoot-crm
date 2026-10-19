package com.ruoyi.crm.domain.vo;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 分群列表/详情 VO
 */
public class SegmentVo
{
    private Long id;
    private String name;
    private String description;
    private Object ruleJson;
    private String type;
    private Integer estSize;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date computedAt;

    private String status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
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

    public Object getRuleJson()
    {
        return ruleJson;
    }

    public void setRuleJson(Object ruleJson)
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
}
