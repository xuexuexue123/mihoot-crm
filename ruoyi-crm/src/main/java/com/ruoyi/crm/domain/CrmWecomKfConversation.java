package com.ruoyi.crm.domain;

import java.util.Date;

/**
 * 微信客服坐席会话；业务主键 (open_kfid, external_userid)
 */
public class CrmWecomKfConversation
{
    private Long id;
    private String corpId;
    private String openKfid;
    private String externalUserid;
    private String nickname;
    private String avatarUrl;
    private String accountName;
    private String lastMessage;
    private Date lastMessageAt;
    private Integer messageCount;
    private Integer unreadCount;
    /** 1=可回复，0=不可 */
    private Integer canReply;
    /** 负责人系统用户 id；NULL = 公共池 */
    private Long userId;
    private String owner;
    private Date assignedAt;
    /**
     * Inbox AI 托管：null/true=可跟渠道托管；false=人工已接管。
     */
    private Boolean hostedByAi;
    private String contactId;
    private String contactName;
    private Date createTime;
    private Date updateTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCorpId() { return corpId; }
    public void setCorpId(String corpId) { this.corpId = corpId; }

    public String getOpenKfid() { return openKfid; }
    public void setOpenKfid(String openKfid) { this.openKfid = openKfid; }

    public String getExternalUserid() { return externalUserid; }
    public void setExternalUserid(String externalUserid) { this.externalUserid = externalUserid; }

    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }

    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }

    public String getAccountName() { return accountName; }
    public void setAccountName(String accountName) { this.accountName = accountName; }

    public String getLastMessage() { return lastMessage; }
    public void setLastMessage(String lastMessage) { this.lastMessage = lastMessage; }

    public Date getLastMessageAt() { return lastMessageAt; }
    public void setLastMessageAt(Date lastMessageAt) { this.lastMessageAt = lastMessageAt; }

    public Integer getMessageCount() { return messageCount; }
    public void setMessageCount(Integer messageCount) { this.messageCount = messageCount; }

    public Integer getUnreadCount() { return unreadCount; }
    public void setUnreadCount(Integer unreadCount) { this.unreadCount = unreadCount; }

    public Integer getCanReply() { return canReply; }
    public void setCanReply(Integer canReply) { this.canReply = canReply; }

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

    public String getContactName() { return contactName; }
    public void setContactName(String contactName) { this.contactName = contactName; }

    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }

    public Date getUpdateTime() { return updateTime; }
    public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }
}
