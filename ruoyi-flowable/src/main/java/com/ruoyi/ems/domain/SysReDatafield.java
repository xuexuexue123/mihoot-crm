package com.ruoyi.ems.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 字段对象 sys_re_datafield
 * 
 * @author luohd
 * @date 2024-02-21
 */
public class SysReDatafield extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 字段编码 */
    private String fieldId;

    /** 排序 */
    @Excel(name = "排序")
    private Long fieldSort;

    /** 字段中文名 */
    @Excel(name = "字段中文名")
    private String fieldLabel;

    /** 字段名称 */
    @Excel(name = "字段名称")
    private String fieldName;

    /** 字段类型 */
    @Excel(name = "字段类型")
    private String fieldType;

    /** 数据对象id */
    @Excel(name = "数据对象id")
    private String dataobjId;

    /** 是否显示 */
    @Excel(name = "是否显示")
    private String isShow;

    /** 字段类别 */
    @Excel(name = "字段类别")
    private String fieldCategory;

    /** 字段配置 */
    @Excel(name = "字段配置")
    private String fieldDetail;

    public void setFieldId(String fieldId) 
    {
        this.fieldId = fieldId;
    }

    public String getFieldId() 
    {
        return fieldId;
    }
    public void setFieldSort(Long fieldSort) 
    {
        this.fieldSort = fieldSort;
    }

    public Long getFieldSort() 
    {
        return fieldSort;
    }
    public void setFieldLabel(String fieldLabel) 
    {
        this.fieldLabel = fieldLabel;
    }

    public String getFieldLabel() 
    {
        return fieldLabel;
    }
    public void setFieldName(String fieldName) 
    {
        this.fieldName = fieldName;
    }

    public String getFieldName() 
    {
        return fieldName;
    }
    public void setFieldType(String fieldType) 
    {
        this.fieldType = fieldType;
    }

    public String getFieldType() 
    {
        return fieldType;
    }
    public void setDataobjId(String dataobjId) 
    {
        this.dataobjId = dataobjId;
    }

    public String getDataobjId() 
    {
        return dataobjId;
    }
    public void setIsShow(String isShow) 
    {
        this.isShow = isShow;
    }

    public String getIsShow() 
    {
        return isShow;
    }
    public void setFieldCategory(String fieldCategory) 
    {
        this.fieldCategory = fieldCategory;
    }

    public String getFieldCategory() 
    {
        return fieldCategory;
    }
    public void setFieldDetail(String fieldDetail)
    {
        this.fieldDetail = fieldDetail;
    }

    public String getFieldDetail()
    {
        return fieldDetail;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("fieldId", getFieldId())
            .append("fieldSort", getFieldSort())
            .append("fieldLabel", getFieldLabel())
            .append("fieldName", getFieldName())
            .append("fieldType", getFieldType())
            .append("dataobjId", getDataobjId())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("isShow", getIsShow())
            .append("fieldCategory", getFieldCategory())
            .toString();
    }
}
