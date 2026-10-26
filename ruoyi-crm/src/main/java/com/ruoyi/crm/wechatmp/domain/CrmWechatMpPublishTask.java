package com.ruoyi.crm.wechatmp.domain;

import java.util.Date;

/**
 * 微信公众号发布任务（对应 crm_wechat_mp_publish_task 表）
 */
public class CrmWechatMpPublishTask
{
    private Long id;
    private String appId;
    private String mediaId;
    private String publishId;
    private String msgDataId;
    private String articleId;
    private int indexInMsg;
    private Integer publishStatus;
    private String failIdxs;
    private String articleUrl;
    private Date createdAt;
    private Date updatedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getAppId() { return appId; }
    public void setAppId(String appId) { this.appId = appId; }
    public String getMediaId() { return mediaId; }
    public void setMediaId(String mediaId) { this.mediaId = mediaId; }
    public String getPublishId() { return publishId; }
    public void setPublishId(String publishId) { this.publishId = publishId; }
    public String getMsgDataId() { return msgDataId; }
    public void setMsgDataId(String msgDataId) { this.msgDataId = msgDataId; }
    public String getArticleId() { return articleId; }
    public void setArticleId(String articleId) { this.articleId = articleId; }
    public int getIndexInMsg() { return indexInMsg; }
    public void setIndexInMsg(int indexInMsg) { this.indexInMsg = indexInMsg; }
    public Integer getPublishStatus() { return publishStatus; }
    public void setPublishStatus(Integer publishStatus) { this.publishStatus = publishStatus; }
    public String getFailIdxs() { return failIdxs; }
    public void setFailIdxs(String failIdxs) { this.failIdxs = failIdxs; }
    public String getArticleUrl() { return articleUrl; }
    public void setArticleUrl(String articleUrl) { this.articleUrl = articleUrl; }
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }
}
