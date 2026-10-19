package com.ruoyi.crm.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 批量邮件对象 crm_bulkemail
 * 
 * @author luohd
 * @date 2024-09-24
 */
public class CrmBulkemail extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** ID */
    private String bulkId;

    /** 标题 */
    @Excel(name = "标题")
    private String subject;

    /** 模版ID */
    @Excel(name = "模版ID")
    private String templateId;

    /** 标识ID */
    @Excel(name = "标识ID")
    private String uniqueId;

    /** 发送人 */
    @Excel(name = "发送人")
    private String sender;

    /** 接收人 */
    @Excel(name = "接收人")
    private String recipients;

    /** 发送数 */
    @Excel(name = "发送数")
    private Long total;

    /** 失败数 */
    @Excel(name = "失败数")
    private Long fails;

    /** 打开数 */
    @Excel(name = "打开数")
    private Long opens;

    /** 回复数 */
    @Excel(name = "回复数")
    private Long replies;

    /** 邮件内容 */
    @Excel(name = "邮件内容")
    private String emailContent;

    /** -- 状态 (draft, scheduled, sent, completed) */
    @Excel(name = "-- 状态 (draft, scheduled, sent, completed)")
    private String status;

    /** 计划发送时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "计划发送时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date sendDate;

    /** 活动花费 */
    private BigDecimal spendAmount;

    /** 发送模式 immediate / scheduled / draft */
    private String sendMode;

    /** 计划发送时间 */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private Date scheduledAt;

    /** 全部 contact 处理完成时间 */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private Date sentAt;

    /** 取消时间 */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private Date cancelledAt;

    /** 扩展元数据 JSON */
    private String metadata;

    public void setBulkId(String bulkId) 
    {
        this.bulkId = bulkId;
    }

    public String getBulkId() 
    {
        return bulkId;
    }
    public void setSubject(String subject) 
    {
        this.subject = subject;
    }

    public String getSubject() 
    {
        return subject;
    }
    public void setTemplateId(String templateId) 
    {
        this.templateId = templateId;
    }

    public String getTemplateId() 
    {
        return templateId;
    }
    public void setUniqueId(String uniqueId) 
    {
        this.uniqueId = uniqueId;
    }

    public String getUniqueId() 
    {
        return uniqueId;
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
    public void setTotal(Long total) 
    {
        this.total = total;
    }

    public Long getTotal() 
    {
        return total;
    }
    public void setFails(Long fails) 
    {
        this.fails = fails;
    }

    public Long getFails() 
    {
        return fails;
    }
    public void setOpens(Long opens) 
    {
        this.opens = opens;
    }

    public Long getOpens() 
    {
        return opens;
    }
    public void setReplies(Long replies) 
    {
        this.replies = replies;
    }

    public Long getReplies() 
    {
        return replies;
    }
    public void setEmailContent(String emailContent) 
    {
        this.emailContent = emailContent;
    }

    public String getEmailContent() 
    {
        return emailContent;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getStatus()
    {
        return status;
    }
    public void setSendDate(Date sendDate)
    {
        this.sendDate = sendDate;
    }

    public Date getSendDate()
    {
        return sendDate;
    }

    public BigDecimal getSpendAmount()
    {
        return spendAmount;
    }

    public void setSpendAmount(BigDecimal spendAmount)
    {
        this.spendAmount = spendAmount;
    }

    public String getSendMode()
    {
        return sendMode;
    }

    public void setSendMode(String sendMode)
    {
        this.sendMode = sendMode;
    }

    public Date getScheduledAt()
    {
        return scheduledAt;
    }

    public void setScheduledAt(Date scheduledAt)
    {
        this.scheduledAt = scheduledAt;
    }

    public Date getSentAt()
    {
        return sentAt;
    }

    public void setSentAt(Date sentAt)
    {
        this.sentAt = sentAt;
    }

    public Date getCancelledAt()
    {
        return cancelledAt;
    }

    public void setCancelledAt(Date cancelledAt)
    {
        this.cancelledAt = cancelledAt;
    }

    public String getMetadata()
    {
        return metadata;
    }

    public void setMetadata(String metadata)
    {
        this.metadata = metadata;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("bulkId", getBulkId())
            .append("subject", getSubject())
            .append("templateId", getTemplateId())
            .append("uniqueId", getUniqueId())
            .append("sender", getSender())
            .append("recipients", getRecipients())
            .append("total", getTotal())
            .append("fails", getFails())
            .append("opens", getOpens())
            .append("replies", getReplies())
            .append("emailContent", getEmailContent())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("spendAmount", getSpendAmount())
            .toString();
    }
}
