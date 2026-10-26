package com.ruoyi.crm.domain.dto.wecom;

/**
 * 企业微信 {@code externalcontact/get} 解析后的客户概要。
 */
public class WecomExternalContactProfile
{
    private String externalUserid;
    private String name;
    private String unionId;
    private String avatar;
    private Integer gender;
    private Integer type;
    private String position;
    private String corpName;
    private String corpFullName;
    /** externalcontact/get 完整响应 JSON */
    private String rawJson;

    public String getExternalUserid()
    {
        return externalUserid;
    }

    public void setExternalUserid(String externalUserid)
    {
        this.externalUserid = externalUserid;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getUnionId()
    {
        return unionId;
    }

    public void setUnionId(String unionId)
    {
        this.unionId = unionId;
    }

    public String getAvatar()
    {
        return avatar;
    }

    public void setAvatar(String avatar)
    {
        this.avatar = avatar;
    }

    public Integer getGender()
    {
        return gender;
    }

    public void setGender(Integer gender)
    {
        this.gender = gender;
    }

    public Integer getType()
    {
        return type;
    }

    public void setType(Integer type)
    {
        this.type = type;
    }

    public String getPosition()
    {
        return position;
    }

    public void setPosition(String position)
    {
        this.position = position;
    }

    public String getCorpName()
    {
        return corpName;
    }

    public void setCorpName(String corpName)
    {
        this.corpName = corpName;
    }

    public String getCorpFullName()
    {
        return corpFullName;
    }

    public void setCorpFullName(String corpFullName)
    {
        this.corpFullName = corpFullName;
    }

    public String getRawJson()
    {
        return rawJson;
    }

    public void setRawJson(String rawJson)
    {
        this.rawJson = rawJson;
    }
}
