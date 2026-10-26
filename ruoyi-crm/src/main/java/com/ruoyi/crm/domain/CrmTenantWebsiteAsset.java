package com.ruoyi.crm.domain;

import java.util.Date;

/**
 * 租户网站公开素材（图片/视频）
 */
public class CrmTenantWebsiteAsset
{
    public static final String STATUS_ACTIVE = "active";
    public static final String STATUS_DELETED = "deleted";

    public static final String MEDIA_IMAGE = "image";
    public static final String MEDIA_VIDEO = "video";

    private String id;
    private String fileTitle;
    private String fileName;
    private String originalName;
    private String fileType;
    private Long fileSize;
    private String mediaType;
    private String mimeType;
    private Integer width;
    private Integer height;
    private Integer durationMs;
    private String status;
    private String publicPath;
    private String createBy;
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

    public String getFileTitle()
    {
        return fileTitle;
    }

    public void setFileTitle(String fileTitle)
    {
        this.fileTitle = fileTitle;
    }

    public String getFileName()
    {
        return fileName;
    }

    public void setFileName(String fileName)
    {
        this.fileName = fileName;
    }

    public String getOriginalName()
    {
        return originalName;
    }

    public void setOriginalName(String originalName)
    {
        this.originalName = originalName;
    }

    public String getFileType()
    {
        return fileType;
    }

    public void setFileType(String fileType)
    {
        this.fileType = fileType;
    }

    public Long getFileSize()
    {
        return fileSize;
    }

    public void setFileSize(Long fileSize)
    {
        this.fileSize = fileSize;
    }

    public String getMediaType()
    {
        return mediaType;
    }

    public void setMediaType(String mediaType)
    {
        this.mediaType = mediaType;
    }

    public String getMimeType()
    {
        return mimeType;
    }

    public void setMimeType(String mimeType)
    {
        this.mimeType = mimeType;
    }

    public Integer getWidth()
    {
        return width;
    }

    public void setWidth(Integer width)
    {
        this.width = width;
    }

    public Integer getHeight()
    {
        return height;
    }

    public void setHeight(Integer height)
    {
        this.height = height;
    }

    public Integer getDurationMs()
    {
        return durationMs;
    }

    public void setDurationMs(Integer durationMs)
    {
        this.durationMs = durationMs;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getPublicPath()
    {
        return publicPath;
    }

    public void setPublicPath(String publicPath)
    {
        this.publicPath = publicPath;
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

    public Date getUpdateTime()
    {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime)
    {
        this.updateTime = updateTime;
    }
}
