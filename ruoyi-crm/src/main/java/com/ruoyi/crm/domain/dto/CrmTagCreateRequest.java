package com.ruoyi.crm.domain.dto;

/**
 * 新建 CRM 标签
 */
public class CrmTagCreateRequest
{
    private String dictValue;
    private String dictLabel;
    private String dictLabelEn;
    private String dictLabelTh;
    /** 业务分类（remark） */
    private String remark;
    /** 0 启用 / 1 停用，默认 0 */
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
