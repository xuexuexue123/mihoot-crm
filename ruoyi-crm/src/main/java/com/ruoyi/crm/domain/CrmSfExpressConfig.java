package com.ruoyi.crm.domain;

import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 顺丰丰桥租户配置（每租户库单行）
 */
public class CrmSfExpressConfig extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long id;
    private String customerCode;
    /** AES 密文，不落日志 */
    private String checkwordEnc;
    private String monthlyCard;
    private Boolean sandbox;
    private Boolean connected;
    private String lastError;

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getCustomerCode()
    {
        return customerCode;
    }

    public void setCustomerCode(String customerCode)
    {
        this.customerCode = customerCode;
    }

    public String getCheckwordEnc()
    {
        return checkwordEnc;
    }

    public void setCheckwordEnc(String checkwordEnc)
    {
        this.checkwordEnc = checkwordEnc;
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

    public Boolean getConnected()
    {
        return connected;
    }

    public void setConnected(Boolean connected)
    {
        this.connected = connected;
    }

    public String getLastError()
    {
        return lastError;
    }

    public void setLastError(String lastError)
    {
        this.lastError = lastError;
    }
}
