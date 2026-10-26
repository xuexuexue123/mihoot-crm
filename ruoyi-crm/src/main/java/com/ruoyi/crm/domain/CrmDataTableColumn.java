package com.ruoyi.crm.domain;

/**
 * Data Table 列定义 crm_data_table_column
 */
public class CrmDataTableColumn
{
    private String id;
    private String dataTableId;
    private String name;
    private String type;
    private Boolean required;
    private String defaultValue;
    private Integer sortOrder;
    private Boolean isSystem;
    /** JSON array text, e.g. ["A","B"] */
    private String options;
    private String unit;
    private String format;
    private String section;

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getDataTableId()
    {
        return dataTableId;
    }

    public void setDataTableId(String dataTableId)
    {
        this.dataTableId = dataTableId;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public Boolean getRequired()
    {
        return required;
    }

    public void setRequired(Boolean required)
    {
        this.required = required;
    }

    public String getDefaultValue()
    {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue)
    {
        this.defaultValue = defaultValue;
    }

    public Integer getSortOrder()
    {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder)
    {
        this.sortOrder = sortOrder;
    }

    public Boolean getIsSystem()
    {
        return isSystem;
    }

    public void setIsSystem(Boolean isSystem)
    {
        this.isSystem = isSystem;
    }

    public String getOptions()
    {
        return options;
    }

    public void setOptions(String options)
    {
        this.options = options;
    }

    public String getUnit()
    {
        return unit;
    }

    public void setUnit(String unit)
    {
        this.unit = unit;
    }

    public String getFormat()
    {
        return format;
    }

    public void setFormat(String format)
    {
        this.format = format;
    }

    public String getSection()
    {
        return section;
    }

    public void setSection(String section)
    {
        this.section = section;
    }
}
