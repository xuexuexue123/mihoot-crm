package com.ruoyi.crm.domain;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 邮件对象 crm_email_messages
 * 
 * @author luohd
 * @date 2025-05-14
 */
public class CrmEmailMessages extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 本地邮件唯一ID */
    private String id;

    /** 系统用户ID（关联crm用户） */
    @Excel(name = "系统用户ID", readConverterExp = "关=联crm用户")
    private String userId;

    /** 邮件服务提供商（如 gmail / outlook / imap） */
    @Excel(name = "邮件服务提供商", readConverterExp = "如=,g=mail,/=,o=utlook,/=,i=map")
    private String provider;

    /** 外部服务中的唯一邮件ID（如 Gmail messageId / Outlook id） */
    @Excel(name = "外部服务中的唯一邮件ID", readConverterExp = "如=,G=mail,m=essageId,/=,O=utlook,i=d")
    private String messageExternalId;

    /** 邮件线程ID（Gmail使用，Outlook 可为空） */
    @Excel(name = "邮件线程ID", readConverterExp = "G=mail使用，Outlook,可=为空")
    private String threadId;

    /** 邮件主题 */
    @Excel(name = "邮件主题")
    private String subject;

    /** 发件人邮箱地址 */
    @Excel(name = "发件人邮箱地址")
    private String sender;

    /** 收件人（包含 to, cc 等，以JSON或逗号分隔） */
    @Excel(name = "收件人", readConverterExp = "包=含,t=o,,c=c,等=，以JSON或逗号分隔")
    private String recipients;

    /** 预览摘要（body 的片段） */
    @Excel(name = "预览摘要", readConverterExp = "b=ody,的=片段")
    private String snippet;

    /** 是否有附件 */
    @Excel(name = "是否有附件")
    private Integer hasAttachment;

    /** 纯文本正文（延迟加载） */
    @Excel(name = "纯文本正文", readConverterExp = "延=迟加载")
    private String bodyPlain;

    /** HTML正文（延迟加载） */
    @Excel(name = "HTML正文", readConverterExp = "延=迟加载")
    private String bodyHtml;

    /** 标签/分类（Gmail labelIds 或 Outlook categories） */
    @Excel(name = "标签/分类", readConverterExp = "G=mail,l=abelIds,或=,O=utlook,c=ategories")
    private String labels;

    /** 是否已读 */
    @Excel(name = "是否已读")
    private Integer isRead;

    /** 是否已经下载完整邮件 */
    @Excel(name = "是否已经下载完整邮件")
    private Integer isDownloaded;

    /** 邮件接收时间（从header中解析） */
    @Excel(name = "邮件接收时间", readConverterExp = "从=header中解析")
    private Date receivedAt;

    /** 同步到本地时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "同步到本地时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date syncedAt;

    /** 原始邮件数据（调试/回溯使用，可选） */
    @Excel(name = "原始邮件数据", readConverterExp = "调=试/回溯使用，可选")
    private String rawJson;

    /** $column.columnComment */
    @Excel(name = "folder", readConverterExp = "$column.readConverterExp()")
    private String folder;

    /** 是否被删除 */
    @Excel(name = "是否被删除")
    private Integer isDeleted;

    /** 账号名称 */
    @Excel(name = "账号名称")
    private String externalName;

    /** Agent 搜索：主题模糊匹配（非持久化字段） */
    private String subjectLike;

    /** Agent 搜索：发件人模糊匹配（非持久化字段） */
    private String senderLike;

    /** Agent 搜索：正文关键词，匹配 snippet/body_plain（非持久化字段） */
    private String bodyLike;

    /** 邮件附件信息 */
    private List<CrmEmailAttachments> crmEmailAttachmentsList;

    public void setId(String id) 
    {
        this.id = id;
    }

    public String getId() 
    {
        return id;
    }
    public void setUserId(String userId) 
    {
        this.userId = userId;
    }

    public String getUserId() 
    {
        return userId;
    }
    public void setProvider(String provider) 
    {
        this.provider = provider;
    }

    public String getProvider() 
    {
        return provider;
    }
    public void setMessageExternalId(String messageExternalId) 
    {
        this.messageExternalId = messageExternalId;
    }

    public String getMessageExternalId() 
    {
        return messageExternalId;
    }
    public void setThreadId(String threadId) 
    {
        this.threadId = threadId;
    }

    public String getThreadId() 
    {
        return threadId;
    }
    public void setSubject(String subject) 
    {
        this.subject = subject;
    }

    public String getSubject() 
    {
        return subject;
    }
    public void setSender(String sender) 
    {
        this.sender = sender;
    }

    public String getSender() 
    {
        return sender;
    }
    public void setRecipients(String recipients) 
    {
        this.recipients = recipients;
    }

    public String getRecipients() 
    {
        return recipients;
    }
    public void setSnippet(String snippet) 
    {
        this.snippet = snippet;
    }

    public String getSnippet() 
    {
        return snippet;
    }
    public void setHasAttachment(Integer hasAttachment) 
    {
        this.hasAttachment = hasAttachment;
    }

    public Integer getHasAttachment() 
    {
        return hasAttachment;
    }
    public void setBodyPlain(String bodyPlain) 
    {
        this.bodyPlain = bodyPlain;
    }

    public String getBodyPlain() 
    {
        return bodyPlain;
    }
    public void setBodyHtml(String bodyHtml) 
    {
        this.bodyHtml = bodyHtml;
    }

    public String getBodyHtml() 
    {
        return bodyHtml;
    }
    public void setLabels(String labels) 
    {
        this.labels = labels;
    }

    public String getLabels() 
    {
        return labels;
    }
    public void setIsRead(Integer isRead) 
    {
        this.isRead = isRead;
    }

    public Integer getIsRead() 
    {
        return isRead;
    }
    public void setIsDownloaded(Integer isDownloaded) 
    {
        this.isDownloaded = isDownloaded;
    }

    public Integer getIsDownloaded() 
    {
        return isDownloaded;
    }
    public void setReceivedAt(Date receivedAt) 
    {
        this.receivedAt = receivedAt;
    }

    public Date getReceivedAt() 
    {
        return receivedAt;
    }
    public void setSyncedAt(Date syncedAt) 
    {
        this.syncedAt = syncedAt;
    }

    public Date getSyncedAt() 
    {
        return syncedAt;
    }
    public void setRawJson(String rawJson) 
    {
        this.rawJson = rawJson;
    }

    public String getRawJson() 
    {
        return rawJson;
    }

    public void setFolder(String folder)
    {
        this.folder = folder;
    }

    public String getFolder()
    {
        return folder;
    }
    public void setExternalName(String externalName)
    {
        this.externalName = externalName;
    }

    public String getExternalName()
    {
        return externalName;
    }
    public List<CrmEmailAttachments> getCrmEmailAttachmentsList()
    {
        return crmEmailAttachmentsList;
    }

    public void setCrmEmailAttachmentsList(List<CrmEmailAttachments> crmEmailAttachmentsList)
    {
        this.crmEmailAttachmentsList = crmEmailAttachmentsList;
    }

    public Integer getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Integer isDeleted) {
        this.isDeleted = isDeleted;
    }

    public String getSubjectLike()
    {
        return subjectLike;
    }

    public void setSubjectLike(String subjectLike)
    {
        this.subjectLike = subjectLike;
    }

    public String getSenderLike()
    {
        return senderLike;
    }

    public void setSenderLike(String senderLike)
    {
        this.senderLike = senderLike;
    }

    public String getBodyLike()
    {
        return bodyLike;
    }

    public void setBodyLike(String bodyLike)
    {
        this.bodyLike = bodyLike;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("userId", getUserId())
            .append("provider", getProvider())
            .append("messageExternalId", getMessageExternalId())
            .append("threadId", getThreadId())
            .append("subject", getSubject())
            .append("sender", getSender())
            .append("recipients", getRecipients())
            .append("snippet", getSnippet())
            .append("hasAttachment", getHasAttachment())
            .append("bodyPlain", getBodyPlain())
            .append("bodyHtml", getBodyHtml())
            .append("labels", getLabels())
            .append("isRead", getIsRead())
            .append("isDownloaded", getIsDownloaded())
            .append("receivedAt", getReceivedAt())
            .append("syncedAt", getSyncedAt())
            .append("rawJson", getRawJson())
            .toString();
    }
}
