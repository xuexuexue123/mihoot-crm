package com.ruoyi.crm.domain.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

public class CampaignRoiVo implements Serializable
{
    private static final long serialVersionUID = 1L;

    private String campaignType;
    private String campaignId;
    private String campaignName;
    private BigDecimal spend;
    private Integer convertedContacts;
    private BigDecimal convertedClvTotal;
    private BigDecimal cacPerContact;
    private BigDecimal roi;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private Date firstActivityAt;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private Date lastActivityAt;

    public String getCampaignType() { return campaignType; }
    public void setCampaignType(String campaignType) { this.campaignType = campaignType; }
    public String getCampaignId() { return campaignId; }
    public void setCampaignId(String campaignId) { this.campaignId = campaignId; }
    public String getCampaignName() { return campaignName; }
    public void setCampaignName(String campaignName) { this.campaignName = campaignName; }
    public BigDecimal getSpend() { return spend; }
    public void setSpend(BigDecimal spend) { this.spend = spend; }
    public Integer getConvertedContacts() { return convertedContacts; }
    public void setConvertedContacts(Integer convertedContacts) { this.convertedContacts = convertedContacts; }
    public BigDecimal getConvertedClvTotal() { return convertedClvTotal; }
    public void setConvertedClvTotal(BigDecimal convertedClvTotal) { this.convertedClvTotal = convertedClvTotal; }
    public BigDecimal getCacPerContact() { return cacPerContact; }
    public void setCacPerContact(BigDecimal cacPerContact) { this.cacPerContact = cacPerContact; }
    public BigDecimal getRoi() { return roi; }
    public void setRoi(BigDecimal roi) { this.roi = roi; }
    public Date getFirstActivityAt() { return firstActivityAt; }
    public void setFirstActivityAt(Date firstActivityAt) { this.firstActivityAt = firstActivityAt; }
    public Date getLastActivityAt() { return lastActivityAt; }
    public void setLastActivityAt(Date lastActivityAt) { this.lastActivityAt = lastActivityAt; }
}
