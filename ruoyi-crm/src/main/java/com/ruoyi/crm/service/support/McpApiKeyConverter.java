package com.ruoyi.crm.service.support;

import java.util.Collections;
import java.util.List;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.domain.CrmMcpApiKey;
import com.ruoyi.crm.domain.vo.McpApiKeyCreateResultVo;
import com.ruoyi.crm.domain.vo.McpApiKeyVo;

/**
 * MCP API Key 实体与 VO 转换
 */
public final class McpApiKeyConverter
{
    private McpApiKeyConverter()
    {
    }

    public static McpApiKeyVo toVo(CrmMcpApiKey row, McpTenantCode mcpTenantCode)
    {
        if (row == null)
        {
            return null;
        }
        McpApiKeyVo vo = new McpApiKeyVo();
        vo.setId(row.getId());
        vo.setName(row.getName());
        vo.setKeyPrefix(row.getKeyPrefix());
        vo.setUserId(row.getBoundUserId());
        vo.setAllowedTools(parseStringList(row.getAllowedTools()));
        vo.setEnabled(row.getEnabled() != null && row.getEnabled() == 1);
        vo.setExpiresAt(row.getExpiresAt());
        vo.setCreatedAt(row.getCreateTime());
        vo.setUpdatedAt(row.getUpdateTime());
        if (mcpTenantCode != null && StringUtils.isNotBlank(row.getTenantId()))
        {
            String tenantCode = mcpTenantCode.compute(row.getTenantId());
            vo.setTenantCode(tenantCode);
            vo.setDisplayPrefix(mcpTenantCode.displayPrefix(tenantCode, row.getKeyPrefix()));
        }
        return vo;
    }

    public static McpApiKeyCreateResultVo toCreateResultVo(CrmMcpApiKey row, String apiKey, McpTenantCode mcpTenantCode)
    {
        McpApiKeyVo base = toVo(row, mcpTenantCode);
        if (base == null)
        {
            return null;
        }
        McpApiKeyCreateResultVo vo = new McpApiKeyCreateResultVo();
        vo.setId(base.getId());
        vo.setName(base.getName());
        vo.setKeyPrefix(base.getKeyPrefix());
        vo.setTenantCode(base.getTenantCode());
        vo.setDisplayPrefix(base.getDisplayPrefix());
        vo.setUserId(base.getUserId());
        vo.setAllowedTools(base.getAllowedTools());
        vo.setEnabled(base.getEnabled());
        vo.setExpiresAt(base.getExpiresAt());
        vo.setCreatedAt(base.getCreatedAt());
        vo.setUpdatedAt(base.getUpdatedAt());
        vo.setApiKey(apiKey);
        return vo;
    }

    public static List<McpApiKeyVo> toVoList(List<CrmMcpApiKey> rows, McpTenantCode mcpTenantCode)
    {
        if (rows == null || rows.isEmpty())
        {
            return Collections.emptyList();
        }
        return rows.stream().map(row -> toVo(row, mcpTenantCode)).collect(java.util.stream.Collectors.toList());
    }

    public static String toJsonString(Object value)
    {
        if (value == null)
        {
            return null;
        }
        if (value instanceof List && ((List<?>) value).isEmpty())
        {
            return null;
        }
        return JSON.toJSONString(value);
    }

    public static List<String> parseStringList(String json)
    {
        if (StringUtils.isBlank(json))
        {
            return null;
        }
        return JSON.parseObject(json, new TypeReference<List<String>>()
        {
        });
    }
}
