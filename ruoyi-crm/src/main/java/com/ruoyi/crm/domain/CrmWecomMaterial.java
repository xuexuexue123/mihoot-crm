package com.ruoyi.crm.domain;

import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 企业微信临时素材管理实体
 */
public class CrmWecomMaterial extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long id;
    private String fileTitle;
    private String fileName;
    private String originalName;
    private String fileType;
    private Long fileSize;
    private String mediaType;
    private String mediaId;
    private java.util.Date wecomUploadedAt;
    private Long mediaCreatedAt;
    private java.util.Date expiresAt;
    private String status;
    private String errmsg;
    /** 0:普通临时素材 1:朋友圈附件 2:商品图册 */
    private Integer attachmentType;

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

    public String getMediaId()
    {
        return mediaId;
    }

    public void setMediaId(String mediaId)
    {
        this.mediaId = mediaId;
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
}
