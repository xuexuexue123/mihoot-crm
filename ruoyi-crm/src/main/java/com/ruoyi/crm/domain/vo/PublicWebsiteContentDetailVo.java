package com.ruoyi.crm.domain.vo;

import java.util.Date;

/**
 * 官网公开内容详情（含正文）
 */
public class PublicWebsiteContentDetailVo
{
    private String id;
    private String title;
    private String author;
    private String digest;
    private String coverUrl;
    private String contentMd;
    private String contentHtml;
    private Date websitePublishedAt;
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

    public Date getWebsitePublishedAt()
    {
        return websitePublishedAt;
    }

    public void setWebsitePublishedAt(Date websitePublishedAt)
    {
        this.websitePublishedAt = websitePublishedAt;
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
