package com.ruoyi.crm.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 客户聚合指标快照对象 customer_metrics
 *
 * @author luohd
 * @date 2026-06-16
 */
public class CustomerMetrics extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 客户ID (主键) */
    private String contactId;

    // 企业微信指标
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date wecomAddedAt;
    private Integer wecomMessageCount;
    private Integer wecomOutboundCount;
    private Integer wecomInboundCount;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date wecomLastMessageAt;
    private Integer wecomGroupCount;
    private Integer wecomActiveDays30d;

    // 公众号指标
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date wechatMpFollowedAt;
    private Integer wechatMpArticleRead30d;

    // 小程序指标
    private Integer wechatMiniappLogin30d;
    private Integer wechatMiniappFormSubmit;

    // CRM 指标
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date leadCreatedAt;
    private Integer leadCount;
    private Integer appointmentCount;
    private Integer appointmentNoshowCount;
    private Integer visitCount;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date firstVisitAt;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastVisitAt;
    private Integer opportunityCount;
    private Integer opportunityWonCount;

    // 价值指标
    private BigDecimal totalRevenue;
    private BigDecimal avgDealAmount;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastPaymentAt;

    // AI 指标
    private Integer aiScoreLatest;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date aiScoreUpdatedAt;
    private Integer aiTagCount;
    private String aiIntentLatest;
    private String aiRiskLevel;

    // 生命周期
    private String lifecycleStage;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lifecycleStageAt;
    private Integer churnRiskScore;

    /** 是否经历过 lead.converted 事件（仅全量重算时使用，不入库） */
    @JsonIgnore
    private transient Boolean leadConverted;

    // 整体统计
    private Integer totalEventCount;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastEventAt;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date computedAt;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updatedAt;

    // ========================
    // Getter / Setter
    // ========================

    public String getContactId() { return contactId; }
    public void setContactId(String contactId) { this.contactId = contactId; }

    public Date getWecomAddedAt() { return wecomAddedAt; }
    public void setWecomAddedAt(Date wecomAddedAt) { this.wecomAddedAt = wecomAddedAt; }

    public Integer getWecomMessageCount() { return wecomMessageCount; }
    public void setWecomMessageCount(Integer wecomMessageCount) { this.wecomMessageCount = wecomMessageCount; }

    public Integer getWecomOutboundCount() { return wecomOutboundCount; }
    public void setWecomOutboundCount(Integer wecomOutboundCount) { this.wecomOutboundCount = wecomOutboundCount; }

    public Integer getWecomInboundCount() { return wecomInboundCount; }
    public void setWecomInboundCount(Integer wecomInboundCount) { this.wecomInboundCount = wecomInboundCount; }

    public Date getWecomLastMessageAt() { return wecomLastMessageAt; }
    public void setWecomLastMessageAt(Date wecomLastMessageAt) { this.wecomLastMessageAt = wecomLastMessageAt; }

    public Integer getWecomGroupCount() { return wecomGroupCount; }
    public void setWecomGroupCount(Integer wecomGroupCount) { this.wecomGroupCount = wecomGroupCount; }

    public Integer getWecomActiveDays30d() { return wecomActiveDays30d; }
    public void setWecomActiveDays30d(Integer wecomActiveDays30d) { this.wecomActiveDays30d = wecomActiveDays30d; }

    public Date getWechatMpFollowedAt() { return wechatMpFollowedAt; }
    public void setWechatMpFollowedAt(Date wechatMpFollowedAt) { this.wechatMpFollowedAt = wechatMpFollowedAt; }

    public Integer getWechatMpArticleRead30d() { return wechatMpArticleRead30d; }
    public void setWechatMpArticleRead30d(Integer wechatMpArticleRead30d) { this.wechatMpArticleRead30d = wechatMpArticleRead30d; }

    public Integer getWechatMiniappLogin30d() { return wechatMiniappLogin30d; }
    public void setWechatMiniappLogin30d(Integer wechatMiniappLogin30d) { this.wechatMiniappLogin30d = wechatMiniappLogin30d; }

    public Integer getWechatMiniappFormSubmit() { return wechatMiniappFormSubmit; }
    public void setWechatMiniappFormSubmit(Integer wechatMiniappFormSubmit) { this.wechatMiniappFormSubmit = wechatMiniappFormSubmit; }

    public Date getLeadCreatedAt() { return leadCreatedAt; }
    public void setLeadCreatedAt(Date leadCreatedAt) { this.leadCreatedAt = leadCreatedAt; }

    public Integer getLeadCount() { return leadCount; }
    public void setLeadCount(Integer leadCount) { this.leadCount = leadCount; }

    public Integer getAppointmentCount() { return appointmentCount; }
    public void setAppointmentCount(Integer appointmentCount) { this.appointmentCount = appointmentCount; }

    public Integer getAppointmentNoshowCount() { return appointmentNoshowCount; }
    public void setAppointmentNoshowCount(Integer appointmentNoshowCount) { this.appointmentNoshowCount = appointmentNoshowCount; }

    public Integer getVisitCount() { return visitCount; }
    public void setVisitCount(Integer visitCount) { this.visitCount = visitCount; }

    public Date getFirstVisitAt() { return firstVisitAt; }
    public void setFirstVisitAt(Date firstVisitAt) { this.firstVisitAt = firstVisitAt; }

    public Date getLastVisitAt() { return lastVisitAt; }
    public void setLastVisitAt(Date lastVisitAt) { this.lastVisitAt = lastVisitAt; }

    public Integer getOpportunityCount() { return opportunityCount; }
    public void setOpportunityCount(Integer opportunityCount) { this.opportunityCount = opportunityCount; }

    public Integer getOpportunityWonCount() { return opportunityWonCount; }
    public void setOpportunityWonCount(Integer opportunityWonCount) { this.opportunityWonCount = opportunityWonCount; }

    public BigDecimal getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(BigDecimal totalRevenue) { this.totalRevenue = totalRevenue; }

    public BigDecimal getAvgDealAmount() { return avgDealAmount; }
    public void setAvgDealAmount(BigDecimal avgDealAmount) { this.avgDealAmount = avgDealAmount; }

    public Date getLastPaymentAt() { return lastPaymentAt; }
    public void setLastPaymentAt(Date lastPaymentAt) { this.lastPaymentAt = lastPaymentAt; }

    public Integer getAiScoreLatest() { return aiScoreLatest; }
    public void setAiScoreLatest(Integer aiScoreLatest) { this.aiScoreLatest = aiScoreLatest; }

    public Date getAiScoreUpdatedAt() { return aiScoreUpdatedAt; }
    public void setAiScoreUpdatedAt(Date aiScoreUpdatedAt) { this.aiScoreUpdatedAt = aiScoreUpdatedAt; }

    public Integer getAiTagCount() { return aiTagCount; }
    public void setAiTagCount(Integer aiTagCount) { this.aiTagCount = aiTagCount; }

    public String getAiIntentLatest() { return aiIntentLatest; }
    public void setAiIntentLatest(String aiIntentLatest) { this.aiIntentLatest = aiIntentLatest; }

    public String getAiRiskLevel() { return aiRiskLevel; }
    public void setAiRiskLevel(String aiRiskLevel) { this.aiRiskLevel = aiRiskLevel; }

    public String getLifecycleStage() { return lifecycleStage; }
    public void setLifecycleStage(String lifecycleStage) { this.lifecycleStage = lifecycleStage; }

    public Date getLifecycleStageAt() { return lifecycleStageAt; }
    public void setLifecycleStageAt(Date lifecycleStageAt) { this.lifecycleStageAt = lifecycleStageAt; }

    public Integer getChurnRiskScore() { return churnRiskScore; }
    public void setChurnRiskScore(Integer churnRiskScore) { this.churnRiskScore = churnRiskScore; }

    public Boolean getLeadConverted() { return leadConverted; }
    public void setLeadConverted(Boolean leadConverted) { this.leadConverted = leadConverted; }

    public Integer getTotalEventCount() { return totalEventCount; }
    public void setTotalEventCount(Integer totalEventCount) { this.totalEventCount = totalEventCount; }

    public Date getLastEventAt() { return lastEventAt; }
    public void setLastEventAt(Date lastEventAt) { this.lastEventAt = lastEventAt; }

    public Date getComputedAt() { return computedAt; }
    public void setComputedAt(Date computedAt) { this.computedAt = computedAt; }

    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }
}
