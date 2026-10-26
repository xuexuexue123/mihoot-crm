package com.ruoyi.crm.domain.dto;

/**
 * 受众 preview 请求
 */
public class BulkemailAudiencePreviewRequest
{
    private BulkemailAudienceFilter filter = new BulkemailAudienceFilter();
    private Integer pageNum = 1;
    private Integer pageSize = 50;

    public BulkemailAudienceFilter getFilter()
    {
        return filter;
    }

    public void setFilter(BulkemailAudienceFilter filter)
    {
        this.filter = filter;
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
