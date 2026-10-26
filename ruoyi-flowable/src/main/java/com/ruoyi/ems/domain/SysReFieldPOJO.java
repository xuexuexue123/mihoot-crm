package com.ruoyi.ems.domain;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.List;

public class SysReFieldPOJO extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 表字段 */
    private String fields;

    /** 表的值 */
    private String values;

    /** 表名 */
    private String tableName;


    public void setFields(String fields)
    {
        this.fields = fields;
    }

    public String getFields()
    {
        return fields;
    }
    public void setValues(String values)
    {
        this.values = values;
    }

    public String getValues()
    {
        return values;
    }

    public void setTableName(String tableName)
    {
        this.tableName = tableName;
    }

    public String getTableName()
    {
        return tableName;
    }


    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("fields", getFields())
                .append("values", getValues())
                .append("tableName", getTableName())
                .append("createBy", getCreateBy())
                .append("createTime", getCreateTime())
                .append("updateBy", getUpdateBy())
                .append("updateTime", getUpdateTime())
                .toString();
    }
}