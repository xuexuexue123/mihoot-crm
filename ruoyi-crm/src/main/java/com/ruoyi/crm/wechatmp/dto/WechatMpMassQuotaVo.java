package com.ruoyi.crm.wechatmp.dto;

/**
 * 官方群发配额提示。
 */
public class WechatMpMassQuotaVo
{
    private String appId;
    /** service | subscription | null */
    private String accountType;
    private String quotaHint;
    /** month | day */
    private String period;
    private Integer isToAllUsedEstimate;
    private Integer isToAllLimitEstimate;
    private Integer remainEstimate;
    private String note;

    public String getAppId() { return appId; }
    public void setAppId(String appId) { this.appId = appId; }
    public String getAccountType() { return accountType; }
    public void setAccountType(String accountType) { this.accountType = accountType; }
    public String getQuotaHint() { return quotaHint; }
    public void setQuotaHint(String quotaHint) { this.quotaHint = quotaHint; }
    public String getPeriod() { return period; }
    public void setPeriod(String period) { this.period = period; }
    public Integer getIsToAllUsedEstimate() { return isToAllUsedEstimate; }
    public void setIsToAllUsedEstimate(Integer isToAllUsedEstimate) { this.isToAllUsedEstimate = isToAllUsedEstimate; }
    public Integer getIsToAllLimitEstimate() { return isToAllLimitEstimate; }
    public void setIsToAllLimitEstimate(Integer isToAllLimitEstimate) { this.isToAllLimitEstimate = isToAllLimitEstimate; }
    public Integer getRemainEstimate() { return remainEstimate; }
    public void setRemainEstimate(Integer remainEstimate) { this.remainEstimate = remainEstimate; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}
