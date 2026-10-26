package com.ruoyi.ems.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 报表定义对象 sys_re_reportdefinition
 * 
 * @author luohd
 * @date 2023-03-06
 */
public class SysReReportdefinition extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** id */
    private String id;

    /** dataobj id */
    private String dataobjId;

    /** 标签 */
    @Excel(name = "标签")
    private String pyLabel;

    /** 名称 */
    @Excel(name = "名称")
    private String pyName;

    /** 报表字段 */
    private String columns;

    /** 过滤条件 */
    private String filters;

    /** 多表 */
    private String classjoins;

    /** 参数 */
    private String parameters;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDataobjId() {
        return dataobjId;
    }

    public void setDataobjId(String dataobjId) {
        this.dataobjId = dataobjId;
    }

    public void setPyLabel(String pyLabel)
    {
        this.pyLabel = pyLabel;
    }

    public String getPyLabel() 
    {
        return pyLabel;
    }
    public void setPyName(String pyName) 
    {
        this.pyName = pyName;
    }

    public String getPyName() 
    {
        return pyName;
    }
    public void setColumns(String columns) 
    {
        this.columns = columns;
    }

    public String getColumns() 
    {
        return columns;
    }
    public void setFilters(String filters) 
    {
        this.filters = filters;
    }

    public String getFilters() 
    {
        return filters;
    }
    public void setClassjoins(String classjoins) 
    {
        this.classjoins = classjoins;
    }

    public String getClassjoins() 
    {
        return classjoins;
    }
    public void setParameters(String parameters) 
    {
        this.parameters = parameters;
    }

    public String getParameters() 
    {
        return parameters;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("dataobjId", getDataobjId())
            .append("pyLabel", getPyLabel())
            .append("pyName", getPyName())
            .append("columns", getColumns())
            .append("filters", getFilters())
            .append("classjoins", getClassjoins())
            .append("parameters", getParameters())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
