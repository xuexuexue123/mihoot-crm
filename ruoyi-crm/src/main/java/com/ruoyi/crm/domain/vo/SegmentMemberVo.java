package com.ruoyi.crm.domain.vo;

/**
 * 分群成员 VO
 */
public class SegmentMemberVo
{
    private String contactId;
    private String name;
    private String mobilePhone;

    public String getContactId()
    {
        return contactId;
    }

    public void setContactId(String contactId)
    {
        this.contactId = contactId;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getMobilePhone()
    {
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone)
    {
        this.mobilePhone = mobilePhone;
    }
}
