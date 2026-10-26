package com.ruoyi.crm.domain.dto;

import java.util.Date;

/**
 * 赢单率聚合用商机关闭行。
 */
public class SalesKpiOptyCloseRow
{
    private String ownerId;
    private String stage;
    private String closeReason;
    private Date closeAt;

    public String getOwnerId()
    {
        return ownerId;
    }

    public void setOwnerId(String ownerId)
    {
        this.ownerId = ownerId;
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

    public Date getCloseAt()
    {
        return closeAt;
    }

    public void setCloseAt(Date closeAt)
    {
        this.closeAt = closeAt;
    }
}
