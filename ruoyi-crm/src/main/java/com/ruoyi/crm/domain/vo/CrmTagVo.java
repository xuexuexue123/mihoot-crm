package com.ruoyi.crm.domain.vo;

/**
 * CRM 标签词表项
 */
public class CrmTagVo
{
    private Long dictCode;
    private String dictValue;
    private String dictLabel;
    private String dictLabelEn;
    private String dictLabelTh;
    private String remark;
    private Long dictSort;
    private String status;

    public Long getDictCode()
    {
        return dictCode;
    }

    public void setDictCode(Long dictCode)
    {
        this.dictCode = dictCode;
    }

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

    public Long getDictSort()
    {
        return dictSort;
    }

    public void setDictSort(Long dictSort)
    {
        this.dictSort = dictSort;
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
