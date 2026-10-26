package com.ruoyi.crm.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 活动联系人对象 crm_campaign_contacts
 * 
 * @author luohd
 * @date 2025-04-06
 */
public class CrmCampaignContacts extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** ID */
    private String id;

    /** 活动ID */
    @Excel(name = "活动ID")
    private String campaignId;

    /** 联系人ID */
    @Excel(name = "联系人ID")
    private String contactId;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private String contactName;

    /** email */
    @Excel(name = "email")
    private String email;

    /** 发送状态 */
    @Excel(name = "发送状态")
    private String status;

    /** 设备信息 */
    @Excel(name = "设备信息")
    private String metadata;

    /** 打开次数 */
    @Excel(name = "打开次数")
    private Long open;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd hh:mm:ss")
    private Date openedAt;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd hh:mm:ss")
    private Date clickedAt;
    /** 组织名 */
    @Excel(name = "组织名")
    private String fullName;

    /** 投递状态 pending/sent/failed/... */
    @Excel(name = "投递状态")
    private String deliveryStatus;

    /** 退信原因 */
    private String bounceReason;

    /** 单封发出时间 */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private Date sentAt;

    public void setId(String id) 
    {
        this.id = id;
    }

    public String getId() 
    {
        return id;
    }
    public void setCampaignId(String campaignId) 
    {
        this.campaignId = campaignId;
    }

    public String getCampaignId() 
    {
        return campaignId;
    }
    public void setContactId(String contactId) 
    {
        this.contactId = contactId;
    }

    public String getContactId() 
    {
        return contactId;
    }
    public void setContactName(String contactName) 
    {
        this.contactName = contactName;
    }

    public String getContactName() 
    {
        return contactName;
    }
    public void setEmail(String email) 
    {
        this.email = email;
    }

    public String getEmail() 
    {
        return email;
    }
    public void setStatus(String status) 
    {
        this.status = status;
    }

    public String getStatus() 
    {
        return status;
    }
    public void setMetadata(String metadata) 
    {
        this.metadata = metadata;
    }

    public String getMetadata() 
    {
        return metadata;
    }
    public void setOpen(Long open) 
    {
        this.open = open;
    }

    public Long getOpen() 
    {
        return open;
    }
    public void setOpenedAt(Date openedAt) 
    {
        this.openedAt = openedAt;
    }

    public Date getOpenedAt() 
    {
        return openedAt;
    }
    public void setClickedAt(Date clickedAt) 
    {
        this.clickedAt = clickedAt;
    }

    public Date getClickedAt() 
    {
        return clickedAt;
    }

    public void setFullName(String fullName)
    {
        this.fullName = fullName;
    }

    public String getFullName()
    {
        return fullName;
    }

    public String getDeliveryStatus()
    {
        return deliveryStatus;
    }

    public void setDeliveryStatus(String deliveryStatus)
    {
        this.deliveryStatus = deliveryStatus;
    }

    public String getBounceReason()
    {
        return bounceReason;
    }

    public void setBounceReason(String bounceReason)
    {
        this.bounceReason = bounceReason;
    }

    public Date getSentAt()
    {
        return sentAt;
    }

    public void setSentAt(Date sentAt)
    {
        this.sentAt = sentAt;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("campaignId", getCampaignId())
            .append("contactId", getContactId())
            .append("contactName", getContactName())
            .append("email", getEmail())
            .append("status", getStatus())
            .append("metadata", getMetadata())
            .append("open", getOpen())
            .append("openedAt", getOpenedAt())
            .append("clickedAt", getClickedAt())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
