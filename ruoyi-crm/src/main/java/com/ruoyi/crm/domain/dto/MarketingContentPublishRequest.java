package com.ruoyi.crm.domain.dto;

/**
 * 发布到公众号请求
 */
public class MarketingContentPublishRequest
{
    private String appId;
    /** 微信友好 HTML（Paper 产出）；与 contentMd 至少其一非空 */
    private String content;
    private String contentMd;
    private String title;
    private String author;
    private String digest;
    private String thumbMediaId;
    private String contentSourceUrl;
    private Integer needOpenComment;
    private Integer onlyFansCanComment;

    public String getAppId()
    {
        return appId;
    }

    public void setAppId(String appId)
    {
        this.appId = appId;
    }

    public String getContent()
    {
        return content;
    }

    public void setContent(String content)
    {
        this.content = content;
    }

    public String getContentMd()
    {
        return contentMd;
    }

    public void setContentMd(String contentMd)
    {
        this.contentMd = contentMd;
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

    public String getThumbMediaId()
    {
        return thumbMediaId;
    }

    public void setThumbMediaId(String thumbMediaId)
    {
        this.thumbMediaId = thumbMediaId;
    }

    public String getContentSourceUrl()
    {
        return contentSourceUrl;
    }

    public void setContentSourceUrl(String contentSourceUrl)
    {
        this.contentSourceUrl = contentSourceUrl;
    }

    public Integer getNeedOpenComment()
    {
        return needOpenComment;
    }

    public void setNeedOpenComment(Integer needOpenComment)
    {
        this.needOpenComment = needOpenComment;
    }

    public Integer getOnlyFansCanComment()
    {
        return onlyFansCanComment;
    }

    public void setOnlyFansCanComment(Integer onlyFansCanComment)
    {
        this.onlyFansCanComment = onlyFansCanComment;
    }
}
