package com.ruoyi.crm.wechatmp.domain;

import java.time.OffsetDateTime;
import java.util.Date;

/**
 * 微信公众号永久素材本地缓存（对应 crm_wechat_mp_material 表）
 */
public class CrmWechatMpMaterial
{
    private Long id;
    private String appId;
    private String mediaId;
    private String type;
    private String name;
    private String url;
    private String title;
    private String digest;
    private String thumbUrl;
    private String description;
    private String downUrl;
    private String contentType;
    private String newsItemsJson;
    private Boolean isDefault;
    private OffsetDateTime updateTime;
    private Date createdAt;
    private Date updatedAt;

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
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

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
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

    public String getContentType()
    {
        return contentType;
    }

    public void setContentType(String contentType)
    {
        this.contentType = contentType;
    }

    public String getNewsItemsJson()
    {
        return newsItemsJson;
    }

    public void setNewsItemsJson(String newsItemsJson)
    {
        this.newsItemsJson = newsItemsJson;
    }

    public Boolean getIsDefault()
    {
        return isDefault;
    }

    public void setIsDefault(Boolean isDefault)
    {
        this.isDefault = isDefault;
    }

    public OffsetDateTime getUpdateTime()
    {
        return updateTime;
    }

    public void setUpdateTime(OffsetDateTime updateTime)
    {
        this.updateTime = updateTime;
    }

    public Date getCreatedAt()
    {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt)
    {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt()
    {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt)
    {
        this.updatedAt = updatedAt;
    }
}
