package com.ruoyi.crm.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 邮件附件对象 crm_email_attachments
 * 
 * @author luohd
 * @date 2025-05-16
 */
public class CrmEmailAttachments extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 本地附件ID */
    private String id;

    /** 关联邮件ID（email_messages.id） */
    @Excel(name = "关联邮件ID", readConverterExp = "e=mail_messages.id")
    private String messageId;

    /** 外部服务中的附件ID（Gmail attachmentId / Outlook attachment id） */
    @Excel(name = "外部服务中的附件ID", readConverterExp = "G=mail,a=ttachmentId,/=,O=utlook,a=ttachment,i=d")
    private String attachmentId;

    /** 附件文件名 */
    @Excel(name = "附件文件名")
    private String filename;

    /** MIME类型，如 application/pdf, image/png 等 */
    @Excel(name = "MIME类型，如 application/pdf, image/png 等")
    private String mimeType;

    /** 附件大小（单位：字节） */
    @Excel(name = "附件大小", readConverterExp = "单=位：字节")
    private Integer size;

    /** HTML嵌入 */
    @Excel(name = "HTML嵌入")
    private String contentId;

    /** 是否为嵌入式资源（如邮件中的图片） */
    @Excel(name = "是否为嵌入式资源", readConverterExp = "如=邮件中的图片")
    private Integer isInline;

    /** 是否已下载并保存到本地 */
    @Excel(name = "是否已下载并保存到本地")
    private Integer downloaded;

    /** 本地或远程存储地址（如S3 URL或本地路径） */
    @Excel(name = "本地或远程存储地址", readConverterExp = "如=S3,U=RL或本地路径")
    private String storageUrl;

    /**  */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "", width = 30, dateFormat = "yyyy-MM-dd")
    private Date createdAt;

    /**  */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "", width = 30, dateFormat = "yyyy-MM-dd")
    private Date updatedAt;

    public void setId(String id) 
    {
        this.id = id;
    }

    public String getId() 
    {
        return id;
    }
    public void setMessageId(String messageId) 
    {
        this.messageId = messageId;
    }

    public String getMessageId() 
    {
        return messageId;
    }
    public void setAttachmentId(String attachmentId) 
    {
        this.attachmentId = attachmentId;
    }

    public String getAttachmentId() 
    {
        return attachmentId;
    }
    public void setFilename(String filename) 
    {
        this.filename = filename;
    }

    public String getFilename() 
    {
        return filename;
    }
    public void setMimeType(String mimeType) 
    {
        this.mimeType = mimeType;
    }

    public String getMimeType() 
    {
        return mimeType;
    }
    public void setSize(Integer size)
    {
        this.size = size;
    }

    public Integer getSize()
    {
        return size;
    }
    public void setContentId(String contentId) 
    {
        this.contentId = contentId;
    }

    public String getContentId() 
    {
        return contentId;
    }
    public void setIsInline(Integer isInline) 
    {
        this.isInline = isInline;
    }

    public Integer getIsInline() 
    {
        return isInline;
    }
    public void setDownloaded(Integer downloaded) 
    {
        this.downloaded = downloaded;
    }

    public Integer getDownloaded() 
    {
        return downloaded;
    }
    public void setStorageUrl(String storageUrl) 
    {
        this.storageUrl = storageUrl;
    }

    public String getStorageUrl() 
    {
        return storageUrl;
    }
    public void setCreatedAt(Date createdAt) 
    {
        this.createdAt = createdAt;
    }

    public Date getCreatedAt() 
    {
        return createdAt;
    }
    public void setUpdatedAt(Date updatedAt) 
    {
        this.updatedAt = updatedAt;
    }

    public Date getUpdatedAt() 
    {
        return updatedAt;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("messageId", getMessageId())
            .append("attachmentId", getAttachmentId())
            .append("filename", getFilename())
            .append("mimeType", getMimeType())
            .append("size", getSize())
            .append("contentId", getContentId())
            .append("isInline", getIsInline())
            .append("downloaded", getDownloaded())
            .append("storageUrl", getStorageUrl())
            .append("createdAt", getCreatedAt())
            .append("updatedAt", getUpdatedAt())
            .toString();
    }
}
