package com.ruoyi.crm.domain.dto;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 陈旧商机行。
 */
public class StandupStaleOptyRow
{
    private String optyId;
    private String optyName;
    private String ownerId;
    private String ownerName;
    private String stage;
    private String closeReason;
    private BigDecimal amount;
    private Date lastFollowupAt;

    public String getOptyId()
    {
        return optyId;
    }

    public void setOptyId(String optyId)
    {
        this.optyId = optyId;
    }

    public String getOptyName()
    {
        return optyName;
    }

    public void setOptyName(String optyName)
    {
        this.optyName = optyName;
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

    public String getStage()
    {
        return stage;
    }

    public void setStage(String stage)
    {
        this.stage = stage;
    }

    public String getCloseReason()
    {
        return closeReason;
    }

    public void setCloseReason(String closeReason)
    {
        this.closeReason = closeReason;
    }

    public BigDecimal getAmount()
    {
        return amount;
    }

    public void setAmount(BigDecimal amount)
    {
        this.amount = amount;
    }

    public Date getLastFollowupAt()
    {
        return lastFollowupAt;
    }

    public void setLastFollowupAt(Date lastFollowupAt)
    {
        this.lastFollowupAt = lastFollowupAt;
    }
}
