package com.ruoyi.crm.domain.dto;

/**
 * 编辑 CRM 标签（dictValue 不可改，仅作定位键）
 */
public class CrmTagUpdateRequest
{
    private String dictValue;
    private String dictLabel;
    private String dictLabelEn;
    private String dictLabelTh;
    private String remark;
    private String status;

    public String getDictValue()
    {
        return dictValue;
    }

    public void setDictValue(String dictValue)
    {
        this.dictValue = dictValue;
    }

    public String getDictLabel()
    {
        return dictLabel;
    }

    public void setDictLabel(String dictLabel)
    {
        this.dictLabel = dictLabel;
    }

    public String getDictLabelEn()
    {
        return dictLabelEn;
    }

    public void setDictLabelEn(String dictLabelEn)
    {
        this.dictLabelEn = dictLabelEn;
    }

    public String getDictLabelTh()
    {
        return dictLabelTh;
    }

    public void setDictLabelTh(String dictLabelTh)
    {
        this.dictLabelTh = dictLabelTh;
    }

    public String getRemark()
    {
        return remark;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }
}
