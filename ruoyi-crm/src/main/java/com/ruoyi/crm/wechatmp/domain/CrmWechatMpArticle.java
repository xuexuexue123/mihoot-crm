package com.ruoyi.crm.wechatmp.domain;

import java.time.OffsetDateTime;
import java.util.Date;

/**
 * 微信公众号已发布图文本地缓存（对应 crm_wechat_mp_article 表）
 */
public class CrmWechatMpArticle
{
    private Long id;
    private String appId;
    private String articleId;
    private String title;
    private String url;
    private String thumbUrl;
    private String digest;
    private String author;
    private int indexInMsg;
    private OffsetDateTime updateTime;
    private String content;
    private String contentSourceUrl;
    private String thumbMediaId;
    private Integer needOpenComment;
    private Integer onlyFansCanComment;
    private Boolean isDeleted;
    private Boolean detailFetched;
    private Date createdAt;
    private Date updatedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getAppId() { return appId; }
    public void setAppId(String appId) { this.appId = appId; }
    public String getArticleId() { return articleId; }
    public void setArticleId(String articleId) { this.articleId = articleId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    public String getThumbUrl() { return thumbUrl; }
    public void setThumbUrl(String thumbUrl) { this.thumbUrl = thumbUrl; }
    public String getDigest() { return digest; }
    public void setDigest(String digest) { this.digest = digest; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public int getIndexInMsg() { return indexInMsg; }
    public void setIndexInMsg(int indexInMsg) { this.indexInMsg = indexInMsg; }
    public OffsetDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(OffsetDateTime updateTime) { this.updateTime = updateTime; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getContentSourceUrl() { return contentSourceUrl; }
    public void setContentSourceUrl(String contentSourceUrl) { this.contentSourceUrl = contentSourceUrl; }
    public String getThumbMediaId() { return thumbMediaId; }
    public void setThumbMediaId(String thumbMediaId) { this.thumbMediaId = thumbMediaId; }
    public Integer getNeedOpenComment() { return needOpenComment; }
    public void setNeedOpenComment(Integer needOpenComment) { this.needOpenComment = needOpenComment; }
    public Integer getOnlyFansCanComment() { return onlyFansCanComment; }
    public void setOnlyFansCanComment(Integer onlyFansCanComment) { this.onlyFansCanComment = onlyFansCanComment; }
    public Boolean getIsDeleted() { return isDeleted; }
    public void setIsDeleted(Boolean isDeleted) { this.isDeleted = isDeleted; }
    public Boolean getDetailFetched() { return detailFetched; }
    public void setDetailFetched(Boolean detailFetched) { this.detailFetched = detailFetched; }
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }
}
