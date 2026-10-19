package com.ruoyi.crm.domain.dto;

/**
 * 分群创建/更新请求
 */
public class SegmentSaveRequest
{
    private Long id;
    private String name;
    private String description;
    private Object ruleJson;
    private String status;

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

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }
}
