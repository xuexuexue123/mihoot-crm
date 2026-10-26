package com.ruoyi.crm.wechatmp.dto;

import java.time.OffsetDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 微信公众号草稿文章 DTO
 */
public class WechatMpArticleDraft
{
    private String mediaId;
    private int indexInMsg;
    private String articleType;
    private String title;
    private String author;
    private String digest;
    private String content;
    private String contentMd;
    private String contentSourceUrl;
    private String thumbMediaId;
    private Integer needOpenComment;
    private Integer onlyFansCanComment;
    private String url;
    private Boolean synced;
    private Boolean published;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    private OffsetDateTime updateTime;

    public String getMediaId() { return mediaId; }
    public void setMediaId(String mediaId) { this.mediaId = mediaId; }
    public int getIndexInMsg() { return indexInMsg; }
    public void setIndexInMsg(int indexInMsg) { this.indexInMsg = indexInMsg; }
    public String getArticleType() { return articleType; }
    public void setArticleType(String articleType) { this.articleType = articleType; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public String getDigest() { return digest; }
    public void setDigest(String digest) { this.digest = digest; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getContentMd() { return contentMd; }
    public void setContentMd(String contentMd) { this.contentMd = contentMd; }
    public String getContentSourceUrl() { return contentSourceUrl; }
    public void setContentSourceUrl(String contentSourceUrl) { this.contentSourceUrl = contentSourceUrl; }
    public String getThumbMediaId() { return thumbMediaId; }
    public void setThumbMediaId(String thumbMediaId) { this.thumbMediaId = thumbMediaId; }
    public Integer getNeedOpenComment() { return needOpenComment; }
    public void setNeedOpenComment(Integer needOpenComment) { this.needOpenComment = needOpenComment; }
    public Integer getOnlyFansCanComment() { return onlyFansCanComment; }
    public void setOnlyFansCanComment(Integer onlyFansCanComment) { this.onlyFansCanComment = onlyFansCanComment; }
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    public Boolean getSynced() { return synced; }
    public void setSynced(Boolean synced) { this.synced = synced; }
    public Boolean getPublished() { return published; }
    public void setPublished(Boolean published) { this.published = published; }
    public OffsetDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(OffsetDateTime updateTime) { this.updateTime = updateTime; }
}
