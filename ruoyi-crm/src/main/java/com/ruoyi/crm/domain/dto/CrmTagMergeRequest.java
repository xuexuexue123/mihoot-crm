package com.ruoyi.crm.domain.dto;

/**
 * orphan 合并到标准标签
 */
public class CrmTagMergeRequest
{
    private String fromText;
    private String fromId;
    private String toDictValue;

    public String getFromText()
    {
        return fromText;
    }

    public void setFromText(String fromText)
    {
        this.fromText = fromText;
    }

    public String getFromId()
    {
        return fromId;
    }

    public void setFromId(String fromId)
    {
        this.fromId = fromId;
    }

    public String getToDictValue()
    {
        return toDictValue;
    }

    public void setToDictValue(String toDictValue)
    {
        this.toDictValue = toDictValue;
    }
}
