package com.ruoyi.crm.domain.vo;

import java.math.BigDecimal;

/**
 * 昨日签约明细。
 */
public class StandupWinItem
{
    private String contractId;
    private String contractName;
    private BigDecimal signAmount;
    private String signTime;
    private String ownerId;
    private String ownerName;
    private String contactId;
    private String contactName;
    private String accountName;
    private String deepLink;

    public String getContractId()
    {
        return contractId;
    }

    public void setContractId(String contractId)
    {
        this.contractId = contractId;
    }

    public String getContractName()
    {
        return contractName;
    }

    public void setContractName(String contractName)
    {
        this.contractName = contractName;
    }

    public BigDecimal getSignAmount()
    {
        return signAmount;
    }

    public void setSignAmount(BigDecimal signAmount)
    {
        this.signAmount = signAmount;
    }

    public String getSignTime()
    {
        return signTime;
    }

    public void setSignTime(String signTime)
    {
        this.signTime = signTime;
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

    public String getContactId()
    {
        return contactId;
    }

    public void setContactId(String contactId)
    {
        this.contactId = contactId;
    }

    public String getContactName()
    {
        return contactName;
    }

    public void setContactName(String contactName)
    {
        this.contactName = contactName;
    }

    public String getAccountName()
    {
        return accountName;
    }

    public void setAccountName(String accountName)
    {
        this.accountName = accountName;
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
