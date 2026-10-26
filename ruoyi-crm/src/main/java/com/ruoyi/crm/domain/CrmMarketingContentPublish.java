package com.ruoyi.crm.domain;

import java.util.Date;

/**
 * 营销内容渠道发布记录（对应 crm_marketing_content_publish）
 */
public class CrmMarketingContentPublish
{
    public static final String CHANNEL_WECHAT_MP = "wechat_mp";

    public static final String STATUS_SYNCED = "synced";
    public static final String STATUS_PUBLISHING = "publishing";
    public static final String STATUS_PUBLISHED = "published";
    public static final String STATUS_FAILED = "failed";

    private String id;
    private String contentId;
    private String channel;
    private String appId;
    private String mediaId;
    private String publishId;
    private String articleId;
    private String articleUrl;
    private String thumbMediaId;
    private String status;
    private String errorMsg;
    private Date publishedAt;
    private Date createTime;
    private Date updateTime;

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getContentId()
    {
        return contentId;
    }

    public void setContentId(String contentId)
    {
        this.contentId = contentId;
    }

    public String getChannel()
    {
        return channel;
    }

    public void setChannel(String channel)
    {
        this.channel = channel;
    }

    public String getAppId()
    {
        return appId;
    }

    public void setAppId(String appId)
    {
        this.appId = appId;
    }

    public String getMediaId()
    {
        return mediaId;
    }

    public void setMediaId(String mediaId)
    {
        this.mediaId = mediaId;
    }

    public String getPublishId()
    {
        return publishId;
    }

    public void setPublishId(String publishId)
    {
        this.publishId = publishId;
    }

    public String getArticleId()
    {
        return articleId;
    }

    public void setArticleId(String articleId)
    {
        this.articleId = articleId;
    }

    public String getArticleUrl()
    {
        return articleUrl;
    }

    public void setArticleUrl(String articleUrl)
    {
        this.articleUrl = articleUrl;
    }

    public String getThumbMediaId()
    {
        return thumbMediaId;
    }

    public void setThumbMediaId(String thumbMediaId)
    {
        this.thumbMediaId = thumbMediaId;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getErrorMsg()
    {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg)
    {
        this.errorMsg = errorMsg;
    }

    public Date getPublishedAt()
    {
        return publishedAt;
    }

    public void setPublishedAt(Date publishedAt)
    {
        this.publishedAt = publishedAt;
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
