package com.ruoyi.crm.domain.vo;

/**
 * 渠道映射（按 dictValue 聚合）
 */
public class CrmTagMappingVo
{
    private String dictValue;
    private String wecomTagId;
    private String wecomTagName;
    private String wecomStatus;
    private String mpTagId;
    private String mpTagName;
    private String mpStatus;

    public String getDictValue()
    {
        return dictValue;
    }

    public void setDictValue(String dictValue)
    {
        this.dictValue = dictValue;
    }

    public String getWecomTagId()
    {
        return wecomTagId;
    }

    public void setWecomTagId(String wecomTagId)
    {
        this.wecomTagId = wecomTagId;
    }

    public String getWecomTagName()
    {
        return wecomTagName;
    }

    public void setWecomTagName(String wecomTagName)
    {
        this.wecomTagName = wecomTagName;
    }

    public String getWecomStatus()
    {
        return wecomStatus;
    }

    public void setWecomStatus(String wecomStatus)
    {
        this.wecomStatus = wecomStatus;
    }

    public String getMpTagId()
    {
        return mpTagId;
    }

    public void setMpTagId(String mpTagId)
    {
        this.mpTagId = mpTagId;
    }

    public String getMpTagName()
    {
        return mpTagName;
    }

    public void setMpTagName(String mpTagName)
    {
        this.mpTagName = mpTagName;
    }

    public String getMpStatus()
    {
        return mpStatus;
    }

    public void setMpStatus(String mpStatus)
    {
        this.mpStatus = mpStatus;
    }
}
