package com.ruoyi.crm.domain.dto.sfexpress;

/**
 * 取消顺丰运单/订单（waybillNo 与 orderId 二选一）
 */
public class SfExpressCancelRequest
{
    private String waybillNo;
    private String orderId;

    public String getWaybillNo()
    {
        return waybillNo;
    }

    public void setWaybillNo(String waybillNo)
    {
        this.waybillNo = waybillNo;
    }

    public String getOrderId()
    {
        return orderId;
    }

    public void setOrderId(String orderId)
    {
        this.orderId = orderId;
    }
}
