package com.ruoyi.crm.domain;

import java.math.BigDecimal;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 销售预测对象 crm_forecast
 * 
 * @author luohd
 * @date 2024-01-18
 */
public class CrmForecast extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 编号 */
    private String forecastId;

    /** 时间周期 */
    @Excel(name = "时间周期")
    private String periodType;

    /** 预告名 */
    @Excel(name = "预告名")
    private String forecastName;

    /** 年 */
    @Excel(name = "年")
    private Long forecastYear;

    /** 月 */
    @Excel(name = "月")
    private Long forecastMonth;

    /** OwnerID */
    @Excel(name = "OwnerID")
    private String ownerId;

    /** 客户经理 */
    @Excel(name = "客户经理")
    private String owner;

    /** closeonly */
    @Excel(name = "closeonly")
    private BigDecimal closedOnly;

    /** commit */
    @Excel(name = "commit")
    private BigDecimal commit;

    /** closecommit */
    @Excel(name = "closecommit")
    private BigDecimal closeCommit;

    /** bestcase */
    @Excel(name = "bestcase")
    private BigDecimal bestCase;

    /** pipeline */
    @Excel(name = "pipeline")
    private BigDecimal openPipeline;

    /** subtotal */
    @Excel(name = "subtotal")
    private BigDecimal subtotal;

    /** quota */
    @Excel(name = "quota")
    private BigDecimal quota;

    public void setForecastId(String forecastId) 
    {
        this.forecastId = forecastId;
    }

    public String getForecastId() 
    {
        return forecastId;
    }
    public void setPeriodType(String periodType) 
    {
        this.periodType = periodType;
    }

    public String getPeriodType() 
    {
        return periodType;
    }
    public void setForecastName(String forecastName) 
    {
        this.forecastName = forecastName;
    }

    public String getForecastName() 
    {
        return forecastName;
    }
    public void setForecastYear(Long forecastYear) 
    {
        this.forecastYear = forecastYear;
    }

    public Long getForecastYear() 
    {
        return forecastYear;
    }
    public void setForecastMonth(Long forecastMonth) 
    {
        this.forecastMonth = forecastMonth;
    }

    public Long getForecastMonth() 
    {
        return forecastMonth;
    }
    public void setOwnerId(String ownerId) 
    {
        this.ownerId = ownerId;
    }

    public String getOwnerId() 
    {
        return ownerId;
    }
    public void setOwner(String owner) 
    {
        this.owner = owner;
    }

    public String getOwner() 
    {
        return owner;
    }
    public void setClosedOnly(BigDecimal closedOnly) 
    {
        this.closedOnly = closedOnly;
    }

    public BigDecimal getClosedOnly() 
    {
        return closedOnly;
    }
    public void setCommit(BigDecimal commit) 
    {
        this.commit = commit;
    }

    public BigDecimal getCommit() 
    {
        return commit;
    }
    public void setCloseCommit(BigDecimal closeCommit) 
    {
        this.closeCommit = closeCommit;
    }

    public BigDecimal getCloseCommit() 
    {
        return closeCommit;
    }
    public void setBestCase(BigDecimal bestCase) 
    {
        this.bestCase = bestCase;
    }

    public BigDecimal getBestCase() 
    {
        return bestCase;
    }
    public void setOpenPipeline(BigDecimal openPipeline) 
    {
        this.openPipeline = openPipeline;
    }

    public BigDecimal getOpenPipeline() 
    {
        return openPipeline;
    }
    public void setSubtotal(BigDecimal subtotal) 
    {
        this.subtotal = subtotal;
    }

    public BigDecimal getSubtotal() 
    {
        return subtotal;
    }
    public void setQuota(BigDecimal quota) 
    {
        this.quota = quota;
    }

    public BigDecimal getQuota() 
    {
        return quota;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("forecastId", getForecastId())
            .append("periodType", getPeriodType())
            .append("forecastName", getForecastName())
            .append("forecastYear", getForecastYear())
            .append("forecastMonth", getForecastMonth())
            .append("ownerId", getOwnerId())
            .append("owner", getOwner())
            .append("closedOnly", getClosedOnly())
            .append("commit", getCommit())
            .append("closeCommit", getCloseCommit())
            .append("bestCase", getBestCase())
            .append("openPipeline", getOpenPipeline())
            .append("subtotal", getSubtotal())
            .append("quota", getQuota())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
