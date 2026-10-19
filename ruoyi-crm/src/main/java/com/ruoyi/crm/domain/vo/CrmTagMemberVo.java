package com.ruoyi.crm.domain.vo;

/**
 * 打了某标签的联系人 / 线索成员（钻取）
 */
public class CrmTagMemberVo
{
    /** contact | lead */
    private String entity;
    /** contactId 或 leadId */
    private String id;
    private String name;
    private String mobilePhone;

    public String getEntity()
    {
        return entity;
    }

    public void setEntity(String entity)
    {
        this.entity = entity;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
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
