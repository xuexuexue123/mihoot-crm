package com.ruoyi.crm.wechatmp.domain;

import java.util.Date;

/**
 * 图文留言半自动回复规则（crm_wechat_mp_comment_auto_reply_rule）
 */
public class CrmWechatMpCommentAutoReplyRule
{
    private Long id;
    private String appId;
    private String keyword;
    private String matchMode;
    private String replyContent;
    private Boolean enabled;
    private Integer priority;
    private Boolean requireReview;
    private Date createdAt;
    private Date updatedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getAppId() { return appId; }
    public void setAppId(String appId) { this.appId = appId; }
    public String getKeyword() { return keyword; }
    public void setKeyword(String keyword) { this.keyword = keyword; }
    public String getMatchMode() { return matchMode; }
    public void setMatchMode(String matchMode) { this.matchMode = matchMode; }
    public String getReplyContent() { return replyContent; }
    public void setReplyContent(String replyContent) { this.replyContent = replyContent; }
    public Boolean getEnabled() { return enabled; }
    public void setEnabled(Boolean enabled) { this.enabled = enabled; }
    public Integer getPriority() { return priority; }
    public void setPriority(Integer priority) { this.priority = priority; }
    public Boolean getRequireReview() { return requireReview; }
    public void setRequireReview(Boolean requireReview) { this.requireReview = requireReview; }
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }
}
