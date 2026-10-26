package com.ruoyi.crm.domain.dto;

import java.util.List;

/**
 * 批量考核 / 预建行请求体。
 */
public class SalesgoalAssessBatchRequest
{
    private String periodId;
    private String kpiCode;
    /** "0"=考核中，"1"=未考核；也可传数字 */
    private String isActive;
    /** ensure-rows 专用；为空时按销售角色拉用户 */
    private List<String> ownerIds;

    public String getPeriodId()
    {
        return periodId;
    }

    public void setPeriodId(String periodId)
    {
        this.periodId = periodId;
    }

    public String getKpiCode()
    {
        return kpiCode;
    }

    public void setKpiCode(String kpiCode)
    {
        this.kpiCode = kpiCode;
    }

    public String getIsActive()
    {
        return isActive;
    }

    public void setIsActive(String isActive)
    {
        this.isActive = isActive;
    }

    public List<String> getOwnerIds()
    {
        return ownerIds;
    }

    public void setOwnerIds(List<String> ownerIds)
    {
        this.ownerIds = ownerIds;
    }
}
