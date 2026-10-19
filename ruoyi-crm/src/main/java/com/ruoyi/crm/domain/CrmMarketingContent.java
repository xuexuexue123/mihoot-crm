package com.ruoyi.crm.domain;

import java.util.Date;

/**
 * 营销内容（对应 crm_marketing_content）
 */
public class CrmMarketingContent
{
    public static final String STATUS_DRAFT = "draft";
    public static final String STATUS_READY = "ready";
    public static final String STATUS_ARCHIVED = "archived";

    private String id;
    private String title;
    private String author;
    private String digest;
    private String coverUrl;
    private String contentMd;
    private String contentHtml;
    private String status;
    private Boolean websiteVisible;
    private Date websitePublishedAt;
    private String createBy;
    private Date createTime;
    private String updateBy;
    private Date updateTime;

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

    public String getContentMd()
    {
        return contentMd;
    }

    public void setContentMd(String contentMd)
    {
        this.contentMd = contentMd;
    }

    public String getContentHtml()
    {
        return contentHtml;
    }

    public void setContentHtml(String contentHtml)
    {
        this.contentHtml = contentHtml;
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

    public String getCreateBy()
    {
        return createBy;
    }

    public void setCreateBy(String createBy)
    {
        this.createBy = createBy;
    }

    public Date getCreateTime()
    {
        return createTime;
    }

    public void setCreateTime(Date createTime)
    {
        this.createTime = createTime;
    }

    public String getUpdateBy()
    {
        return updateBy;
    }

    public void setUpdateBy(String updateBy)
    {
        this.updateBy = updateBy;
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
