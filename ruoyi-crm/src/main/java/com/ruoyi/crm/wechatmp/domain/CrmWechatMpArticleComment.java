package com.ruoyi.crm.wechatmp.domain;

import java.util.Date;

/**
 * 微信公众号图文留言（对应 crm_wechat_mp_article_comment）
 */
public class CrmWechatMpArticleComment
{
    private Long id;
    private String appId;
    private String msgDataId;
    private Integer indexInMsg;
    private String articleId;
    private String articleTitle;
    private String articleUrl;
    private Long userCommentId;
    private String openid;
    private String content;
    private Integer commentType;
    private Date commentTime;
    private String replyContent;
    private Date replyTime;
    private String status;
    private Boolean isRead;
    private String contactId;
    /** 侧栏关联线索 ID */
    private String leadId;
    private Date syncedAt;
    private Date createdAt;
    private Date updatedAt;
    private Boolean reviewRequired;
    private Long autoReplyRuleId;

    /** 列表展示：来自 config，非表字段 */
    private String accountName;
    /** 列表展示：联系人姓名，非表字段 */
    private String contactName;
    /** 列表展示：粉丝昵称，非表字段（join crm_wechat_mp_fan） */
    private String nickname;
    /** 列表展示：粉丝头像，非表字段（join crm_wechat_mp_fan） */
    private String avatarUrl;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getAppId() { return appId; }
    public void setAppId(String appId) { this.appId = appId; }
    public String getMsgDataId() { return msgDataId; }
    public void setMsgDataId(String msgDataId) { this.msgDataId = msgDataId; }
    public Integer getIndexInMsg() { return indexInMsg; }
    public void setIndexInMsg(Integer indexInMsg) { this.indexInMsg = indexInMsg; }
    public String getArticleId() { return articleId; }
    public void setArticleId(String articleId) { this.articleId = articleId; }
    public String getArticleTitle() { return articleTitle; }
    public void setArticleTitle(String articleTitle) { this.articleTitle = articleTitle; }
    public String getArticleUrl() { return articleUrl; }
    public void setArticleUrl(String articleUrl) { this.articleUrl = articleUrl; }
    public Long getUserCommentId() { return userCommentId; }
    public void setUserCommentId(Long userCommentId) { this.userCommentId = userCommentId; }
    public String getOpenid() { return openid; }
    public void setOpenid(String openid) { this.openid = openid; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public Integer getCommentType() { return commentType; }
    public void setCommentType(Integer commentType) { this.commentType = commentType; }
    public Date getCommentTime() { return commentTime; }
    public void setCommentTime(Date commentTime) { this.commentTime = commentTime; }
    public String getReplyContent() { return replyContent; }
    public void setReplyContent(String replyContent) { this.replyContent = replyContent; }
    public Date getReplyTime() { return replyTime; }
    public void setReplyTime(Date replyTime) { this.replyTime = replyTime; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Boolean getIsRead() { return isRead; }
    public void setIsRead(Boolean isRead) { this.isRead = isRead; }
    public String getContactId() { return contactId; }
    public void setContactId(String contactId) { this.contactId = contactId; }
    public String getLeadId() { return leadId; }
    public void setLeadId(String leadId) { this.leadId = leadId; }
    public Date getSyncedAt() { return syncedAt; }
    public void setSyncedAt(Date syncedAt) { this.syncedAt = syncedAt; }
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }
    public Boolean getReviewRequired() { return reviewRequired; }
    public void setReviewRequired(Boolean reviewRequired) { this.reviewRequired = reviewRequired; }
    public Long getAutoReplyRuleId() { return autoReplyRuleId; }
    public void setAutoReplyRuleId(Long autoReplyRuleId) { this.autoReplyRuleId = autoReplyRuleId; }
    public String getAccountName() { return accountName; }
    public void setAccountName(String accountName) { this.accountName = accountName; }
    public String getContactName() { return contactName; }
    public void setContactName(String contactName) { this.contactName = contactName; }
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
}
