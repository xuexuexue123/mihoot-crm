package com.ruoyi.crm.service.support;

import java.time.Instant;
import java.util.Date;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.domain.CrmTenantWebsitePage;
import com.ruoyi.crm.domain.vo.TenantWebsitePageDetailVo;

public final class TenantWebsitePageVoSupport
{
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private TenantWebsitePageVoSupport()
    {
    }

    public static String schemaToJson(JsonNode schema)
    {
        if (schema == null || schema.isNull())
        {
            return null;
        }
        try
        {
            return OBJECT_MAPPER.writeValueAsString(schema);
        }
        catch (JsonProcessingException ex)
        {
            throw new ServiceException("INVALID_SCHEMA", HttpStatus.BAD_REQUEST);
        }
    }

    public static JsonNode parseSchema(String json)
    {
        if (StringUtils.isBlank(json))
        {
            return null;
        }
        try
        {
            return OBJECT_MAPPER.readTree(json);
        }
        catch (JsonProcessingException ex)
        {
            throw new ServiceException("INVALID_SCHEMA", HttpStatus.BAD_REQUEST);
        }
    }

    public static String toIso8601(Date date)
    {
        if (date == null)
        {
            return null;
        }
        return Instant.ofEpochMilli(date.getTime()).toString();
    }

    public static TenantWebsitePageDetailVo toDetailVo(CrmTenantWebsitePage page)
    {
        TenantWebsitePageDetailVo vo = new TenantWebsitePageDetailVo();
        vo.setId(page.getId());
        vo.setPath(page.getPath());
        vo.setTitle(page.getTitle());
        vo.setStatus(page.getStatus());
        vo.setSchemaVersion(page.getSchemaVersion());
        vo.setDraftSchema(parseSchema(page.getDraftSchema()));
        vo.setPublishedSchema(parseSchema(page.getPublishedSchema()));
        vo.setPublishedAt(toIso8601(page.getPublishedAt()));
        vo.setCreatedAt(toIso8601(page.getCreatedAt()));
        vo.setUpdatedAt(toIso8601(page.getUpdatedAt()));
        return vo;
    }
}
