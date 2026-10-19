package com.ruoyi.crm.wechatmp.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;

/**
 * 微信公众号文章统计数据（对应 crm_wechat_mp_article_stat 表）
 */
public class CrmWechatMpArticleStat
{
    private Long id;
    private String appId;
    private String articleId;
    private int indexInMsg;
    private LocalDate refDate;
    private LocalDate statDate;
    private Integer readUser;
    private Integer shareUser;
    private Integer zaikanUser;
    private Integer likeUser;
    private Integer commentCount;
    private Integer collectionUser;
    private Integer praiseMoney;
    private Integer readSubscribeUser;
    private BigDecimal readDeliveryRate;
    private BigDecimal readFinishRate;
    private BigDecimal readAvgActivetime;
    private Date createdAt;
    private Date updatedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getAppId() { return appId; }
    public void setAppId(String appId) { this.appId = appId; }
    public String getArticleId() { return articleId; }
    public void setArticleId(String articleId) { this.articleId = articleId; }
    public int getIndexInMsg() { return indexInMsg; }
    public void setIndexInMsg(int indexInMsg) { this.indexInMsg = indexInMsg; }
    public LocalDate getRefDate() { return refDate; }
    public void setRefDate(LocalDate refDate) { this.refDate = refDate; }
    public LocalDate getStatDate() { return statDate; }
    public void setStatDate(LocalDate statDate) { this.statDate = statDate; }
    public Integer getReadUser() { return readUser; }
    public void setReadUser(Integer readUser) { this.readUser = readUser; }
    public Integer getShareUser() { return shareUser; }
    public void setShareUser(Integer shareUser) { this.shareUser = shareUser; }
    public Integer getZaikanUser() { return zaikanUser; }
    public void setZaikanUser(Integer zaikanUser) { this.zaikanUser = zaikanUser; }
    public Integer getLikeUser() { return likeUser; }
    public void setLikeUser(Integer likeUser) { this.likeUser = likeUser; }
    public Integer getCommentCount() { return commentCount; }
    public void setCommentCount(Integer commentCount) { this.commentCount = commentCount; }
    public Integer getCollectionUser() { return collectionUser; }
    public void setCollectionUser(Integer collectionUser) { this.collectionUser = collectionUser; }
    public Integer getPraiseMoney() { return praiseMoney; }
    public void setPraiseMoney(Integer praiseMoney) { this.praiseMoney = praiseMoney; }
    public Integer getReadSubscribeUser() { return readSubscribeUser; }
    public void setReadSubscribeUser(Integer readSubscribeUser) { this.readSubscribeUser = readSubscribeUser; }
    public BigDecimal getReadDeliveryRate() { return readDeliveryRate; }
    public void setReadDeliveryRate(BigDecimal readDeliveryRate) { this.readDeliveryRate = readDeliveryRate; }
    public BigDecimal getReadFinishRate() { return readFinishRate; }
    public void setReadFinishRate(BigDecimal readFinishRate) { this.readFinishRate = readFinishRate; }
    public BigDecimal getReadAvgActivetime() { return readAvgActivetime; }
    public void setReadAvgActivetime(BigDecimal readAvgActivetime) { this.readAvgActivetime = readAvgActivetime; }
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }
}
