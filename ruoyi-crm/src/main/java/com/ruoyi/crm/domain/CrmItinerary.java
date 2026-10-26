package com.ruoyi.crm.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 行程对象 crm_itinerary
 * 
 * @author luohd
 * @date 2024-03-13
 */
public class CrmItinerary extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 编号 */
    private String itinId;

    /** 编号 */
    @Excel(name = "编号")
    private String travelId;

    /** 出发地 */
    @Excel(name = "出发地")
    private String itinFrom;

    /** 目的地 */
    @Excel(name = "目的地")
    private String itinTo;

    /** 日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date itinDate;

    /** 交通工具 */
    @Excel(name = "交通工具")
    private String transportation;

    /** 酒店 */
    @Excel(name = "酒店")
    private String hotel;

    /** 费用估算 */
    @Excel(name = "费用估算")
    private BigDecimal assumedCost;

    /** 描述 */
    @Excel(name = "描述")
    private String description;

    public void setItinId(String itinId) 
    {
        this.itinId = itinId;
    }

    public String getItinId() 
    {
        return itinId;
    }
    public void setTravelId(String travelId) 
    {
        this.travelId = travelId;
    }

    public String getTravelId() 
    {
        return travelId;
    }
    public void setItinFrom(String itinFrom) 
    {
        this.itinFrom = itinFrom;
    }

    public String getItinFrom() 
    {
        return itinFrom;
    }
    public void setItinTo(String itinTo) 
    {
        this.itinTo = itinTo;
    }

    public String getItinTo() 
    {
        return itinTo;
    }
    public void setItinDate(Date itinDate) 
    {
        this.itinDate = itinDate;
    }

    public Date getItinDate() 
    {
        return itinDate;
    }
    public void setTransportation(String transportation) 
    {
        this.transportation = transportation;
    }

    public String getTransportation() 
    {
        return transportation;
    }
    public void setHotel(String hotel) 
    {
        this.hotel = hotel;
    }

    public String getHotel() 
    {
        return hotel;
    }
    public void setAssumedCost(BigDecimal assumedCost) 
    {
        this.assumedCost = assumedCost;
    }

    public BigDecimal getAssumedCost() 
    {
        return assumedCost;
    }
    public void setDescription(String description) 
    {
        this.description = description;
    }

    public String getDescription() 
    {
        return description;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("itinId", getItinId())
            .append("travelId", getTravelId())
            .append("itinFrom", getItinFrom())
            .append("itinTo", getItinTo())
            .append("itinDate", getItinDate())
            .append("transportation", getTransportation())
            .append("hotel", getHotel())
            .append("assumedCost", getAssumedCost())
            .append("description", getDescription())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
