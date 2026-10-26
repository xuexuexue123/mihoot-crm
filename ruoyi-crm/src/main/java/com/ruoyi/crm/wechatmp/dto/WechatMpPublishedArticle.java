package com.ruoyi.crm.wechatmp.dto;

import java.time.OffsetDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 公众号已发布图文（菜单文章选择器 + 图文详细数据）
 */
public class WechatMpPublishedArticle
{
    private String articleId;

    private String publishId;

    private String title;

    private String url;

    private String thumbUrl;

    private String digest;

    private String author;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    private OffsetDateTime updateTime;

    private int indexInMessage;

    /** 图文消息的具体内容（HTML标签），来自 freepublish/getarticle */
    private String content;

    /** 原文地址（点击"阅读原文"后的URL），来自 freepublish/getarticle */
    private String contentSourceUrl;

    /** 封面图片素材id（永久MediaID），来自 freepublish/getarticle */
    private String thumbMediaId;

    /** 是否打开评论 0不打开 1打开，来自 freepublish/getarticle */
    private Integer needOpenComment;

    /** 是否粉丝才可评论 0所有人 1粉丝，来自 freepublish/getarticle */
    private Integer onlyFansCanComment;

    /** 是否被删除，来自 freepublish/getarticle */
    private Boolean isDeleted;

    // ======================== 统计聚合字段（来自 crm_wechat_mp_article_stat LEFT JOIN） ========================

    /** 是否有任意统计记录 */
    private Boolean hasStats;

    /** 累计阅读人数 SUM(read_user) */
    private Integer totalRead;

    /** 累计分享人数 SUM(share_user) */
    private Integer totalShare;

    /** 累计点赞（爱心赞 + 拇指赞）SUM(zaikan_user + like_user) */
    private Integer totalLike;

    /** 累计留言数 SUM(comment_count) */
    private Integer totalComment;

    /** 累计收藏人数 SUM(collection_user) */
    private Integer totalCollect;

    public String getArticleId()
    {
        return articleId;
    }

    public void setArticleId(String articleId)
    {
        this.articleId = articleId;
    }

    public String getPublishId()
    {
        return publishId;
    }

    public void setPublishId(String publishId)
    {
        this.publishId = publishId;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    public String getThumbUrl()
    {
        return thumbUrl;
    }

    public void setThumbUrl(String thumbUrl)
    {
        this.thumbUrl = thumbUrl;
    }

    public String getDigest()
    {
        return digest;
    }

    public void setDigest(String digest)
    {
        this.digest = digest;
    }

    public String getAuthor()
    {
        return author;
    }

    public void setAuthor(String author)
    {
        this.author = author;
    }

    public OffsetDateTime getUpdateTime()
    {
        return updateTime;
    }

    public void setUpdateTime(OffsetDateTime updateTime)
    {
        this.updateTime = updateTime;
    }

    public int getIndexInMessage()
    {
        return indexInMessage;
    }

    public void setIndexInMessage(int indexInMessage)
    {
        this.indexInMessage = indexInMessage;
    }

    public String getContent()
    {
        return content;
    }

    public void setContent(String content)
    {
        this.content = content;
    }

    public String getContentSourceUrl()
    {
        return contentSourceUrl;
    }

    public void setContentSourceUrl(String contentSourceUrl)
    {
        this.contentSourceUrl = contentSourceUrl;
    }

    public String getThumbMediaId()
    {
        return thumbMediaId;
    }

    public void setThumbMediaId(String thumbMediaId)
    {
        this.thumbMediaId = thumbMediaId;
    }

    public Integer getNeedOpenComment()
    {
        return needOpenComment;
    }

    public void setNeedOpenComment(Integer needOpenComment)
    {
        this.needOpenComment = needOpenComment;
    }

    public Integer getOnlyFansCanComment()
    {
        return onlyFansCanComment;
    }

    public void setOnlyFansCanComment(Integer onlyFansCanComment)
    {
        this.onlyFansCanComment = onlyFansCanComment;
    }

    public Boolean getIsDeleted()
    {
        return isDeleted;
    }

    public void setIsDeleted(Boolean isDeleted)
    {
        this.isDeleted = isDeleted;
    }

    public Boolean getHasStats()
    {
        return hasStats;
    }

    public void setHasStats(Boolean hasStats)
    {
        this.hasStats = hasStats;
    }

    public Integer getTotalRead()
    {
        return totalRead;
    }

    public void setTotalRead(Integer totalRead)
    {
        this.totalRead = totalRead;
    }

    public Integer getTotalShare()
    {
        return totalShare;
    }

    public void setTotalShare(Integer totalShare)
    {
        this.totalShare = totalShare;
    }

    public Integer getTotalLike()
    {
        return totalLike;
    }

    public void setTotalLike(Integer totalLike)
    {
        this.totalLike = totalLike;
    }

    public Integer getTotalComment()
    {
        return totalComment;
    }

    public void setTotalComment(Integer totalComment)
    {
        this.totalComment = totalComment;
    }

    public Integer getTotalCollect()
    {
        return totalCollect;
    }

    public void setTotalCollect(Integer totalCollect)
    {
        this.totalCollect = totalCollect;
    }
}
