package com.ruoyi.crm.domain.vo;

import java.math.BigDecimal;

/**
 * 风险名单行。
 */
public class StandupRiskItem
{
    private String riskId;
    private String riskType;
    private String severity;
    private String reason;
    private String subjectType;
    private String subjectId;
    private String subjectName;
    private String ownerId;
    private String ownerName;
    private Double idleHours;
    private Double poolEtaHours;
    private String lastFollowupAt;
    private BigDecimal optyAmount;
    private String deepLink;

    public String getRiskId()
    {
        return riskId;
    }

    public void setRiskId(String riskId)
    {
        this.riskId = riskId;
    }

    public String getRiskType()
    {
        return riskType;
    }

    public void setRiskType(String riskType)
    {
        this.riskType = riskType;
    }

    public String getSeverity()
    {
        return severity;
    }

    public void setSeverity(String severity)
    {
        this.severity = severity;
    }

    public String getReason()
    {
        return reason;
    }

    public void setReason(String reason)
    {
        this.reason = reason;
    }

    public String getSubjectType()
    {
        return subjectType;
    }

    public void setSubjectType(String subjectType)
    {
        this.subjectType = subjectType;
    }

    public String getSubjectId()
    {
        return subjectId;
    }

    public void setSubjectId(String subjectId)
    {
        this.subjectId = subjectId;
    }

    public String getSubjectName()
    {
        return subjectName;
    }

    public void setSubjectName(String subjectName)
    {
        this.subjectName = subjectName;
    }

    public String getOwnerId()
    {
        return ownerId;
    }

    public void setOwnerId(String ownerId)
    {
        this.ownerId = ownerId;
    }

    public String getOwnerName()
    {
        return ownerName;
    }

    public void setOwnerName(String ownerName)
    {
        this.ownerName = ownerName;
    }

    public Double getIdleHours()
    {
        return idleHours;
    }

    public void setIdleHours(Double idleHours)
    {
        this.idleHours = idleHours;
    }

    public Double getPoolEtaHours()
    {
        return poolEtaHours;
    }

    public void setPoolEtaHours(Double poolEtaHours)
    {
        this.poolEtaHours = poolEtaHours;
    }

    public String getLastFollowupAt()
    {
        return lastFollowupAt;
    }

    public void setLastFollowupAt(String lastFollowupAt)
    {
        this.lastFollowupAt = lastFollowupAt;
    }

    public BigDecimal getOptyAmount()
    {
        return optyAmount;
    }

    public void setOptyAmount(BigDecimal optyAmount)
    {
        this.optyAmount = optyAmount;
    }

    public String getDeepLink()
    {
        return deepLink;
    }

    public void setDeepLink(String deepLink)
    {
        this.deepLink = deepLink;
    }
}
