package com.ruoyi.crm.domain.dto;

/**
 * 未保存分群规则的分页成员查询
 */
public class SegmentPreviewMembersRequest
{
    private Object ruleJson;
    private Integer pageNum;
    private Integer pageSize;

    public Object getRuleJson()
    {
        return ruleJson;
    }

    public void setRuleJson(Object ruleJson)
    {
        this.ruleJson = ruleJson;
    }

    public Integer getPageNum()
    {
        return pageNum;
    }

    public void setPageNum(Integer pageNum)
    {
        this.pageNum = pageNum;
    }

    public Integer getPageSize()
    {
        return pageSize;
    }

    public void setPageSize(Integer pageSize)
    {
        this.pageSize = pageSize;
    }
}
