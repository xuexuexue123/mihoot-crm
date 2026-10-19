package com.ruoyi.crm.domain.dto.identity;

/**
 * 批量 identity resolve 单条输入
 */
public class IdentityResolveBatchItem
{
    private String identityType;
    private String identityValue;

    public String getIdentityType()
    {
        return identityType;
    }

    public void setIdentityType(String identityType)
    {
        this.identityType = identityType;
    }

    public String getIdentityValue()
    {
        return identityValue;
    }

    public void setIdentityValue(String identityValue)
    {
        this.identityValue = identityValue;
    }
}
