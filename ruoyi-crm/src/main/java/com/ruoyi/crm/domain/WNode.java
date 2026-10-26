package com.ruoyi.crm.domain;

import java.util.Date;
import java.io.IOException;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * WNode对象 w_node
 * 
 * @author luohd
 * @date 2026-05-19
 */
public class WNode extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private String id;

    @Excel(name = "工作流ID")
    private String workflowId;

    @Excel(name = "名称")
    private String name;

    @Excel(name = "类型")
    private String type;

    private String position;

    private String data;

    @JsonSerialize(using = RawJsonStringSerializer.class)
    @JsonDeserialize(using = FlexibleJsonStringDeserializer.class)
    private String inputs;

    @JsonSerialize(using = RawJsonStringSerializer.class)
    @JsonDeserialize(using = FlexibleJsonStringDeserializer.class)
    private String outputs;

    @Excel(name = "凭证ID")
    private String credentialId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date updatedAt;

    public void setId(String id)
    {
        this.id = id;
    }

    public String getId()
    {
        return id;
    }

    public void setWorkflowId(String workflowId)
    {
        this.workflowId = workflowId;
    }

    public String getWorkflowId()
    {
        return workflowId;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public String getType()
    {
        return type;
    }

    public void setPosition(String position)
    {
        this.position = position;
    }

    public String getPosition()
    {
        return position;
    }

    public void setData(String data)
    {
        this.data = data;
    }

    public String getData()
    {
        return data;
    }

    public void setInputs(String inputs)
    {
        this.inputs = inputs;
    }

    public String getInputs()
    {
        return inputs;
    }

    public void setOutputs(String outputs)
    {
        this.outputs = outputs;
    }

    public String getOutputs()
    {
        return outputs;
    }

    public void setCredentialId(String credentialId)
    {
        this.credentialId = credentialId;
    }

    public String getCredentialId()
    {
        return credentialId;
    }

    public void setCreatedAt(Date createdAt)
    {
        this.createdAt = createdAt;
    }

    public Date getCreatedAt()
    {
        return createdAt;
    }

    public void setUpdatedAt(Date updatedAt)
    {
        this.updatedAt = updatedAt;
    }

    public Date getUpdatedAt()
    {
        return updatedAt;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("workflowId", getWorkflowId())
            .append("name", getName())
            .append("type", getType())
            .append("position", getPosition())
            .append("data", getData())
            .append("inputs", getInputs())
            .append("outputs", getOutputs())
            .append("credentialId", getCredentialId())
            .append("createdAt", getCreatedAt())
            .append("updatedAt", getUpdatedAt())
            .toString();
    }

    /**
     * 序列化：将 JSON 字符串当作原始 JSON 写入（同 @JsonRawValue 效果）
     */
    public static class RawJsonStringSerializer extends JsonSerializer<String>
    {
        @Override
        public void serialize(String value, JsonGenerator gen, SerializerProvider serializers) throws IOException
        {
            if (value == null || value.isEmpty())
            {
                gen.writeNull();
            }
            else
            {
                gen.writeRawValue(value);
            }
        }
    }

    /**
     * 反序列化：兼容 String（直接取）和 Array/Object（转为 JSON 字符串）
     */
    public static class FlexibleJsonStringDeserializer extends JsonDeserializer<String>
    {
        @Override
        public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException
        {
            if (p.currentToken().isScalarValue())
            {
                return p.getValueAsString();
            }
            return p.readValueAsTree().toString();
        }
    }
}
