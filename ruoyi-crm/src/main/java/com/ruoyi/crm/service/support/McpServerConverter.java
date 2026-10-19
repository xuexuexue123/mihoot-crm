package com.ruoyi.crm.service.support;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.sign.AESUtil;
import com.ruoyi.crm.domain.CrmAiSkill;
import com.ruoyi.crm.domain.CrmMcpServer;
import com.ruoyi.crm.domain.vo.McpServerCredentialVo;
import com.ruoyi.crm.domain.vo.McpServerVo;

/**
 * MCP Server 转换与引用检查
 */
public final class McpServerConverter
{
    private McpServerConverter()
    {
    }

    public static McpServerVo toVo(CrmMcpServer row)
    {
        if (row == null)
        {
            return null;
        }
        McpServerVo vo = new McpServerVo();
        vo.setId(row.getId());
        vo.setName(row.getName());
        vo.setUrl(row.getUrl());
        vo.setTransport(row.getTransport());
        vo.setAuthType(row.getAuthType());
        vo.setEnabled(row.getEnabled() != null && row.getEnabled() == 1);
        vo.setAllowedTools(parseStringList(row.getAllowedTools()));
        vo.setUpdatedAt(row.getUpdateTime());
        return vo;
    }

    public static McpServerCredentialVo toCredentialVo(CrmMcpServer row)
    {
        McpServerVo base = toVo(row);
        if (base == null)
        {
            return null;
        }
        McpServerCredentialVo vo = new McpServerCredentialVo();
        vo.setId(base.getId());
        vo.setName(base.getName());
        vo.setUrl(base.getUrl());
        vo.setTransport(base.getTransport());
        vo.setAuthType(base.getAuthType());
        vo.setEnabled(base.getEnabled());
        vo.setAllowedTools(base.getAllowedTools());
        vo.setUpdatedAt(base.getUpdatedAt());
        if (row != null && StringUtils.isNotBlank(row.getAuthSecretEnc()))
        {
            try
            {
                vo.setAuthSecret(AESUtil.decrypt(row.getAuthSecretEnc()));
            }
            catch (Exception e)
            {
                vo.setAuthSecret(null);
            }
        }
        return vo;
    }

    public static List<McpServerVo> toVoList(List<CrmMcpServer> rows)
    {
        if (rows == null || rows.isEmpty())
        {
            return Collections.emptyList();
        }
        return rows.stream().map(McpServerConverter::toVo).collect(java.util.stream.Collectors.toList());
    }

    public static String toJsonString(Object value)
    {
        if (value == null)
        {
            return null;
        }
        return JSON.toJSONString(value);
    }

    private static List<String> parseStringList(String json)
    {
        if (StringUtils.isBlank(json))
        {
            return null;
        }
        return JSON.parseObject(json, new TypeReference<List<String>>()
        {
        });
    }

    /**
     * 检查 skill 是否引用 MCP server id
     */
    public static boolean skillReferencesServer(CrmAiSkill skill, Long serverId)
    {
        if (skill == null || serverId == null)
        {
            return false;
        }
        List<Long> ids = parseLongList(skill.getMcpServerIds());
        if (ids != null && ids.contains(serverId))
        {
            return true;
        }
        List<Map<String, Object>> bindings = parseObjectList(skill.getToolBindings());
        if (bindings == null)
        {
            return false;
        }
        for (Map<String, Object> binding : bindings)
        {
            if (binding == null || !"mcp".equals(String.valueOf(binding.get("source"))))
            {
                continue;
            }
            Object serverIdObj = binding.get("serverId");
            if (serverIdObj == null)
            {
                continue;
            }
            long boundId = serverIdObj instanceof Number ? ((Number) serverIdObj).longValue()
                    : Long.parseLong(String.valueOf(serverIdObj));
            if (boundId == serverId)
            {
                return true;
            }
        }
        return false;
    }

    private static List<Long> parseLongList(String json)
    {
        if (StringUtils.isBlank(json))
        {
            return null;
        }
        return JSON.parseObject(json, new TypeReference<List<Long>>()
        {
        });
    }

    private static List<Map<String, Object>> parseObjectList(String json)
    {
        if (StringUtils.isBlank(json))
        {
            return null;
        }
        return JSON.parseObject(json, new TypeReference<List<Map<String, Object>>>()
        {
        });
    }
}
