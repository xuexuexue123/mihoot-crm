package com.ruoyi.ems.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * datatype对象 sys_re_datatype
 * 
 * @author luohd
 * @date 2022-12-13
 */
public class SysDatatype extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 字段编码 */
    private Long dataId;

    /** 排序 */
    @Excel(name = "排序")
    private Long dataSort;

    /** 字段中文名 */
    @Excel(name = "字段中文名")
    private String dataLabel;

    /** 字段名称 */
    @Excel(name = "字段名称")
    private String dataName;

    /** 字段类型 */
    @Excel(name = "字段类型")
    private String dataType;

    /** case_type_id */
    @Excel(name = "case_type_id")
    private String caseTypeId;

    public void setDataId(Long dataId) 
    {
        this.dataId = dataId;
    }

    public Long getDataId() 
    {
        return dataId;
    }
    public void setDataSort(Long dataSort) 
    {
        this.dataSort = dataSort;
    }

    public Long getDataSort() 
    {
        return dataSort;
    }
    public void setDataLabel(String dataLabel) 
    {
        this.dataLabel = dataLabel;
    }

    public String getDataLabel() 
    {
        return dataLabel;
    }
    public void setDataName(String dataName) 
    {
        this.dataName = dataName;
    }

    public String getDataName() 
    {
        return dataName;
    }
    public void setDataType(String dataType) 
    {
        this.dataType = dataType;
    }

    public String getDataType() 
    {
        return dataType;
    }
    public void setCaseTypeId(String caseTypeId) 
    {
        this.caseTypeId = caseTypeId;
    }

    public String getCaseTypeId() 
    {
        return caseTypeId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("dataId", getDataId())
            .append("dataSort", getDataSort())
            .append("dataLabel", getDataLabel())
            .append("dataName", getDataName())
            .append("dataType", getDataType())
            .append("caseTypeId", getCaseTypeId())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
