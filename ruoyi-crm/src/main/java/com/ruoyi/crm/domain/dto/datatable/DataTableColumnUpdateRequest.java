package com.ruoyi.crm.domain.dto.datatable;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 列更新请求（改名 / options / 元数据）
 */
public class DataTableColumnUpdateRequest
{
    private String name;
    private String type;
    private boolean typePresent;
    private List<String> options;
    private boolean optionsPresent;
    private String unit;
    private boolean unitPresent;
    private String format;
    private boolean formatPresent;
    private String section;
    private boolean sectionPresent;

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

    @JsonProperty("type")
    public void setType(String type)
    {
        this.type = type;
        this.typePresent = true;
    }

    public boolean isTypePresent()
    {
        return typePresent;
    }

    public List<String> getOptions()
    {
        return options;
    }

    @JsonProperty("options")
    public void setOptions(List<String> options)
    {
        this.options = options;
        this.optionsPresent = true;
    }

    public boolean isOptionsPresent()
    {
        return optionsPresent;
    }

    public String getUnit()
    {
        return unit;
    }

    @JsonProperty("unit")
    public void setUnit(String unit)
    {
        this.unit = unit;
        this.unitPresent = true;
    }

    public boolean isUnitPresent()
    {
        return unitPresent;
    }

    public String getFormat()
    {
        return format;
    }

    @JsonProperty("format")
    public void setFormat(String format)
    {
        this.format = format;
        this.formatPresent = true;
    }

    public boolean isFormatPresent()
    {
        return formatPresent;
    }

    public String getSection()
    {
        return section;
    }

    @JsonProperty("section")
    public void setSection(String section)
    {
        this.section = section;
        this.sectionPresent = true;
    }

    public boolean isSectionPresent()
    {
        return sectionPresent;
    }
}
