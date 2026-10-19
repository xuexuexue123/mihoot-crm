package com.ruoyi.crm.wechatmp.domain;

import java.util.Date;

/**
 * 公众号坐席会话；业务主键 (app_id, open_id)
 */
public class CrmWechatMpConversation
{
    private Long id;
    private String appId;
    private String openId;
    private String nickname;
    private String avatarUrl;
    private String lastMessage;
    private Date lastMessageAt;
    private Date lastCustomerMessageAt;
    private Integer messageCount;
    private Integer unreadCount;
    /** 负责人系统用户 id；NULL = 公共池 */
    private Long userId;
    private String owner;
    private Date assignedAt;
    /** true=AI托管; false=人工接管; null=跟随渠道 hosted */
    private Boolean hostedByAi;
    private String contactId;
    private String leadId;
    private Date createTime;
    private Date updateTime;

    /** JOIN 展示字段（非表列） */
    private String accountName;
    private String accountType;
    private String contactName;
    private String leadName;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getAppId() { return appId; }
    public void setAppId(String appId) { this.appId = appId; }

    public String getOpenId() { return openId; }
    public void setOpenId(String openId) { this.openId = openId; }

    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }

    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }

    public String getLastMessage() { return lastMessage; }
    public void setLastMessage(String lastMessage) { this.lastMessage = lastMessage; }

    public Date getLastMessageAt() { return lastMessageAt; }
    public void setLastMessageAt(Date lastMessageAt) { this.lastMessageAt = lastMessageAt; }

    public Date getLastCustomerMessageAt() { return lastCustomerMessageAt; }
    public void setLastCustomerMessageAt(Date lastCustomerMessageAt) { this.lastCustomerMessageAt = lastCustomerMessageAt; }

    public Integer getMessageCount() { return messageCount; }
    public void setMessageCount(Integer messageCount) { this.messageCount = messageCount; }

    public Integer getUnreadCount() { return unreadCount; }
    public void setUnreadCount(Integer unreadCount) { this.unreadCount = unreadCount; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getOwner() { return owner; }
    public void setOwner(String owner) { this.owner = owner; }

    public Date getAssignedAt() { return assignedAt; }
    public void setAssignedAt(Date assignedAt) { this.assignedAt = assignedAt; }

    public Boolean getHostedByAi() { return hostedByAi; }
    public void setHostedByAi(Boolean hostedByAi) { this.hostedByAi = hostedByAi; }

    public String getContactId() { return contactId; }
    public void setContactId(String contactId) { this.contactId = contactId; }

    public String getLeadId() { return leadId; }
    public void setLeadId(String leadId) { this.leadId = leadId; }

    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }

    public Date getUpdateTime() { return updateTime; }
    public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }

    public String getAccountName() { return accountName; }
    public void setAccountName(String accountName) { this.accountName = accountName; }

    public String getAccountType() { return accountType; }
    public void setAccountType(String accountType) { this.accountType = accountType; }

    public String getContactName() { return contactName; }
    public void setContactName(String contactName) { this.contactName = contactName; }

    public String getLeadName() { return leadName; }
    public void setLeadName(String leadName) { this.leadName = leadName; }
}
