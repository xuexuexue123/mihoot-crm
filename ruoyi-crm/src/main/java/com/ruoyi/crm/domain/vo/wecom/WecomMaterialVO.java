package com.ruoyi.crm.domain.vo.wecom;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 素材管理视图对象
 */
public class WecomMaterialVO
{
    private Long id;
    private String fileTitle;
    private String originalName;
    private String fileName;
    private String fileType;
    private Long fileSize;
    private String mediaType;
    private String mediaId;
    private String status;
    private String errmsg;
    private String createBy;

    /** 0:普通临时素材 1:朋友圈附件 2:商品图册 */
    private Integer attachmentType;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private java.util.Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private java.util.Date updateTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private java.util.Date wecomUploadedAt;

    private Long mediaCreatedAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private java.util.Date expiresAt;

    private String downloadUrl;

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
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

    public String getOriginalName()
    {
        return originalName;
    }

    public void setOriginalName(String originalName)
    {
        this.originalName = originalName;
    }

    public String getFileName()
    {
        return fileName;
    }

    public void setFileName(String fileName)
    {
        this.fileName = fileName;
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

    public String getMediaId()
    {
        return mediaId;
    }

    public void setMediaId(String mediaId)
    {
        this.mediaId = mediaId;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getErrmsg()
    {
        return errmsg;
    }

    public void setErrmsg(String errmsg)
    {
        this.errmsg = errmsg;
    }

    public Integer getAttachmentType()
    {
        return attachmentType;
    }

    public void setAttachmentType(Integer attachmentType)
    {
        this.attachmentType = attachmentType;
    }

    public String getCreateBy()
    {
        return createBy;
    }

    public void setCreateBy(String createBy)
    {
        this.createBy = createBy;
    }

    public java.util.Date getCreateTime()
    {
        return createTime;
    }

    public void setCreateTime(java.util.Date createTime)
    {
        this.createTime = createTime;
    }

    public java.util.Date getUpdateTime()
    {
        return updateTime;
    }

    public void setUpdateTime(java.util.Date updateTime)
    {
        this.updateTime = updateTime;
    }

    public java.util.Date getWecomUploadedAt()
    {
        return wecomUploadedAt;
    }

    public void setWecomUploadedAt(java.util.Date wecomUploadedAt)
    {
        this.wecomUploadedAt = wecomUploadedAt;
    }

    public Long getMediaCreatedAt()
    {
        return mediaCreatedAt;
    }

    public void setMediaCreatedAt(Long mediaCreatedAt)
    {
        this.mediaCreatedAt = mediaCreatedAt;
    }

    public java.util.Date getExpiresAt()
    {
        return expiresAt;
    }

    public void setExpiresAt(java.util.Date expiresAt)
    {
        this.expiresAt = expiresAt;
    }

    public String getDownloadUrl()
    {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl)
    {
        this.downloadUrl = downloadUrl;
    }
}
