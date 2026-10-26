package com.ruoyi.crm.domain.dto;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 闲置 / 公海临近线索行。
 */
public class StandupIdleLeadRow
{
    private String leadId;
    private String leadName;
    private String ownerId;
    private String ownerName;
    private Date lastFollowupAt;
    private Double idleHours;
    private Double poolEtaHours;

    public String getLeadId()
    {
        return leadId;
    }

    public void setLeadId(String leadId)
    {
        this.leadId = leadId;
    }

    public String getLeadName()
    {
        return leadName;
    }

    public void setLeadName(String leadName)
    {
        this.leadName = leadName;
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

    public Date getLastFollowupAt()
    {
        return lastFollowupAt;
    }

    public void setLastFollowupAt(Date lastFollowupAt)
    {
        this.lastFollowupAt = lastFollowupAt;
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
}
