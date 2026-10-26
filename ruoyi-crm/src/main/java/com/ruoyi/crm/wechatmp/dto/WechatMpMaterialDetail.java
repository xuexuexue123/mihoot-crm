package com.ruoyi.crm.wechatmp.dto;

import java.util.List;

/**
 * 永久素材详情（按 type 填充字段）
 */
public class WechatMpMaterialDetail
{
    private String type;

    private String mediaId;

    private String contentType;

    private String title;

    private String description;

    private String downUrl;

    private List<WechatMpMaterialNewsItem> newsItems;

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public String getMediaId()
    {
        return mediaId;
    }

    public void setMediaId(String mediaId)
    {
        this.mediaId = mediaId;
    }

    public String getContentType()
    {
        return contentType;
    }

    public void setContentType(String contentType)
    {
        this.contentType = contentType;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getDownUrl()
    {
        return downUrl;
    }

    public void setDownUrl(String downUrl)
    {
        this.downUrl = downUrl;
    }

    public List<WechatMpMaterialNewsItem> getNewsItems()
    {
        return newsItems;
    }

    public void setNewsItems(List<WechatMpMaterialNewsItem> newsItems)
    {
        this.newsItems = newsItems;
    }
}
