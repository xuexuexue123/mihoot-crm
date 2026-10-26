package com.ruoyi.crm.wechatmp.dto;

/**
 * 微信公众号修改草稿请求，对应 draft/update 接口的 Request Payload
 */
public class WechatMpDraftUpdateRequest
{
    private String mediaId;
    private int index;
    private Article articles;

    public String getMediaId() { return mediaId; }
    public void setMediaId(String mediaId) { this.mediaId = mediaId; }
    public int getIndex() { return index; }
    public void setIndex(int index) { this.index = index; }
    public Article getArticles() { return articles; }
    public void setArticles(Article articles) { this.articles = articles; }

    public static class Article
    {
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
    }
}
