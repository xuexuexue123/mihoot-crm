package com.ruoyi.crm.domain.vo;

import java.util.Date;

/**
 * 营销内容列表行（不含正文大字段）
 */
public class MarketingContentListVo
{
    private String id;
    private String title;
    private String author;
    private String digest;
    private String coverUrl;
    private String status;
    private Boolean websiteVisible;
    private Date websitePublishedAt;
    private Date createTime;
    private Date updateTime;
    private MarketingContentLatestPublishVo latestPublish;

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getAuthor()
    {
        return author;
    }

    public void setAuthor(String author)
    {
        this.author = author;
    }

    public String getDigest()
    {
        return digest;
    }

    public void setDigest(String digest)
    {
        this.digest = digest;
    }

    public String getCoverUrl()
    {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl)
    {
        this.coverUrl = coverUrl;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public Boolean getWebsiteVisible()
    {
        return websiteVisible;
    }

    public void setWebsiteVisible(Boolean websiteVisible)
    {
        this.websiteVisible = websiteVisible;
    }

    public Date getWebsitePublishedAt()
    {
        return websitePublishedAt;
    }

    public void setWebsitePublishedAt(Date websitePublishedAt)
    {
        this.websitePublishedAt = websitePublishedAt;
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

    public MarketingContentLatestPublishVo getLatestPublish()
    {
        return latestPublish;
    }

    public void setLatestPublish(MarketingContentLatestPublishVo latestPublish)
    {
        this.latestPublish = latestPublish;
    }
}
