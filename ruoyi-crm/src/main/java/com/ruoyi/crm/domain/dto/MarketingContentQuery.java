package com.ruoyi.crm.domain.dto;

/**
 * 营销内容列表查询参数
 */
public class MarketingContentQuery
{
    private String keyword;
    private String status;
    /** true=至少一条 published；false=无已发表记录；null=不限 */
    private Boolean published;
    /** 官网可见筛选；null=不限 */
    private Boolean websiteVisible;

    public String getKeyword()
    {
        return keyword;
    }

    public void setKeyword(String keyword)
    {
        this.keyword = keyword;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public Boolean getPublished()
    {
        return published;
    }

    public void setPublished(Boolean published)
    {
        this.published = published;
    }

    public Boolean getWebsiteVisible()
    {
        return websiteVisible;
    }

    public void setWebsiteVisible(Boolean websiteVisible)
    {
        this.websiteVisible = websiteVisible;
    }
}
