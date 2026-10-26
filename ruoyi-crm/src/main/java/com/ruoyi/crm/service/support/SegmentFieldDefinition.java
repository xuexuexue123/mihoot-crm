package com.ruoyi.crm.service.support;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import com.ruoyi.crm.domain.vo.SegmentEnumValueVo;

/**
 * 分群字段定义（field key 与 Java 实体属性 / identity_type 一致）
 */
public final class SegmentFieldDefinition
{
    private final String source;
    private final String field;
    private final String physicalColumn;
    private final String fieldType;
    private final String label;
    private final List<String> operators;
    private final List<SegmentEnumValueVo> enumValues;

    public SegmentFieldDefinition(String source, String field, String physicalColumn, String fieldType,
            String label, List<String> operators, List<SegmentEnumValueVo> enumValues)
    {
        this.source = source;
        this.field = field;
        this.physicalColumn = physicalColumn;
        this.fieldType = fieldType;
        this.label = label;
        this.operators = Collections.unmodifiableList(operators);
        this.enumValues = enumValues == null ? Collections.emptyList() : Collections.unmodifiableList(enumValues);
    }

    public SegmentFieldDefinition(String source, String field, String physicalColumn, String fieldType,
            String label, String... operators)
    {
        this(source, field, physicalColumn, fieldType, label, Arrays.asList(operators), null);
    }

    public String getSource()
    {
        return source;
    }

    public String getField()
    {
        return field;
    }

    public String getPhysicalColumn()
    {
        return physicalColumn;
    }

    public String getFieldType()
    {
        return fieldType;
    }

    public String getLabel()
    {
        return label;
    }

    public List<String> getOperators()
    {
        return operators;
    }

    public List<SegmentEnumValueVo> getEnumValues()
    {
        return enumValues;
    }

    public String registryKey()
    {
        return source + ":" + field;
    }
}
