package com.ruoyi.crm.wechatmp.dto;

/**
 * 微信公众号草稿同步请求 Body。所有字段均为可选，非空时覆盖 DB 中的对应值。
 */
public class WechatMpDraftSyncRequest
{
    private String content;
    private String contentMd;
    private String title;
    private String author;
    private String digest;
    private String contentSourceUrl;
    private String thumbMediaId;
    private Integer needOpenComment;
    private Integer onlyFansCanComment;
    private Integer index;

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getContentMd() { return contentMd; }
    public void setContentMd(String contentMd) { this.contentMd = contentMd; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public String getDigest() { return digest; }
    public void setDigest(String digest) { this.digest = digest; }
    public String getContentSourceUrl() { return contentSourceUrl; }
    public void setContentSourceUrl(String contentSourceUrl) { this.contentSourceUrl = contentSourceUrl; }
    public String getThumbMediaId() { return thumbMediaId; }
    public void setThumbMediaId(String thumbMediaId) { this.thumbMediaId = thumbMediaId; }
    public Integer getNeedOpenComment() { return needOpenComment; }
    public void setNeedOpenComment(Integer needOpenComment) { this.needOpenComment = needOpenComment; }
    public Integer getOnlyFansCanComment() { return onlyFansCanComment; }
    public void setOnlyFansCanComment(Integer onlyFansCanComment) { this.onlyFansCanComment = onlyFansCanComment; }
    public Integer getIndex() { return index; }
    public void setIndex(Integer index) { this.index = index; }
}
