package com.ruoyi.crm.domain;

import java.io.IOException;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 智能推送策略配置对象 smart_push_strategy
 *
 * @author ruoyi
 * @date 2026-07-13
 */
public class SmartPushStrategy extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long id;
    private String strategyType;
    private String name;
    private Boolean enabled;
    private String cronExpression;
    private String channel;
    private String targetMode;
    private String messageTemplate;
    private String configJson;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updatedAt;

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getStrategyType()
    {
        return strategyType;
    }

    public void setStrategyType(String strategyType)
    {
        this.strategyType = strategyType;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public Boolean getEnabled()
    {
        return enabled;
    }

    public void setEnabled(Boolean enabled)
    {
        this.enabled = enabled;
    }

    public String getCronExpression()
    {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression)
    {
        this.cronExpression = cronExpression;
    }

    public String getChannel()
    {
        return channel;
    }

    public void setChannel(String channel)
    {
        this.channel = channel;
    }

    public String getTargetMode()
    {
        return targetMode;
    }

    public void setTargetMode(String targetMode)
    {
        this.targetMode = targetMode;
    }

    public String getMessageTemplate()
    {
        return messageTemplate;
    }

    public void setMessageTemplate(String messageTemplate)
    {
        this.messageTemplate = messageTemplate;
    }

    @JsonRawValue
    @JsonDeserialize(using = RawJsonDeserializer.class)
    public String getConfigJson()
    {
        return configJson;
    }

    public void setConfigJson(String configJson)
    {
        this.configJson = configJson;
    }

    /**
     * 自定义反序列化器：将前端传入的 JSON object/array 转为 JSON 字符串存储。
     */
    public static class RawJsonDeserializer extends JsonDeserializer<String>
    {
        @Override
        public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException
        {
            return p.readValueAsTree().toString();
        }
    }

    public Date getCreatedAt()
    {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt)
    {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt()
    {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt)
    {
        this.updatedAt = updatedAt;
    }
}
