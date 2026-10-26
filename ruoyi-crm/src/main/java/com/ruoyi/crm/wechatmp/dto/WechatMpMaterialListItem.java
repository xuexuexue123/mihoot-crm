package com.ruoyi.crm.wechatmp.dto;

import java.time.OffsetDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 永久素材列表行（image/video/voice/news）
 */
public class WechatMpMaterialListItem
{
    private String mediaId;

    private String name;

    private String url;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    private OffsetDateTime updateTime;

    private String type;

    /** 图文便捷字段：取 newsItems[0] */
    private String title;

    private String digest;

    private String thumbUrl;

    private List<WechatMpMaterialNewsItem> newsItems;

    /** 是否为该公众号默认素材 */
    private Boolean isDefault;

    public String getMediaId()
    {
        return mediaId;
    }

    public void setMediaId(String mediaId)
    {
        this.mediaId = mediaId;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    public OffsetDateTime getUpdateTime()
    {
        return updateTime;
    }

    public void setUpdateTime(OffsetDateTime updateTime)
    {
        this.updateTime = updateTime;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getDigest()
    {
        return digest;
    }

    public void setDigest(String digest)
    {
        this.digest = digest;
    }

    public String getThumbUrl()
    {
        return thumbUrl;
    }

    public void setThumbUrl(String thumbUrl)
    {
        this.thumbUrl = thumbUrl;
    }

    public List<WechatMpMaterialNewsItem> getNewsItems()
    {
        return newsItems;
    }

    public void setNewsItems(List<WechatMpMaterialNewsItem> newsItems)
    {
        this.newsItems = newsItems;
    }

    public Boolean getIsDefault()
    {
        return isDefault;
    }

    public void setIsDefault(Boolean isDefault)
    {
        this.isDefault = isDefault;
    }
}
