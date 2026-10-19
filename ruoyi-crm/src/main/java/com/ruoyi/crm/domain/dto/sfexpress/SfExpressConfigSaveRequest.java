package com.ruoyi.crm.domain.dto.sfexpress;

/**
 * 保存顺丰丰桥租户配置（checkword 新建必填，更新可省略）
 */
public class SfExpressConfigSaveRequest
{
    private String customerCode;
    /** 新建必填；更新时省略表示保留原校验码 */
    private String checkword;
    private String monthlyCard;
    private Boolean sandbox;

    public String getCustomerCode()
    {
        return customerCode;
    }

    public void setCustomerCode(String customerCode)
    {
        this.customerCode = customerCode;
    }

    public String getCheckword()
    {
        return checkword;
    }

    public void setCheckword(String checkword)
    {
        this.checkword = checkword;
    }

    public String getMonthlyCard()
    {
        return monthlyCard;
    }

    public void setMonthlyCard(String monthlyCard)
    {
        this.monthlyCard = monthlyCard;
    }

    public Boolean getSandbox()
    {
        return sandbox;
    }

    public void setSandbox(Boolean sandbox)
    {
        this.sandbox = sandbox;
    }
}
