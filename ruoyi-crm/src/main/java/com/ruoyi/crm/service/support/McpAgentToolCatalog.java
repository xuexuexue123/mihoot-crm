package com.ruoyi.crm.service.support;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StreamUtils;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;

/**
 * Agent 工具白名单，须与 sevencrm {@code src/features/agent/tools/catalog.ts} 保持同步。
 * 数据源：classpath:crm/mcp-agent-tool-catalog.json
 */
public final class McpAgentToolCatalog
{
    private static final Set<String> ALLOWED = loadCatalog();

    private McpAgentToolCatalog()
    {
    }

    private static Set<String> loadCatalog()
    {
        try
        {
            ClassPathResource resource = new ClassPathResource("crm/mcp-agent-tool-catalog.json");
            try (InputStream in = resource.getInputStream())
            {
                byte[] bytes = StreamUtils.copyToByteArray(in);
                List<String> names = JSON.parseObject(new String(bytes, StandardCharsets.UTF_8),
                        new TypeReference<List<String>>()
                        {
                        });
                if (names == null || names.isEmpty())
                {
                    return Collections.emptySet();
                }
                return Collections.unmodifiableSet(new HashSet<>(names));
            }
        }
        catch (Exception ex)
        {
            throw new IllegalStateException("Failed to load mcp-agent-tool-catalog.json", ex);
        }
    }

    public static void validateAllowedTools(List<String> allowedTools)
    {
        if (allowedTools == null || allowedTools.isEmpty())
        {
            return;
        }
        for (String name : allowedTools)
        {
            if (StringUtils.isBlank(name))
            {
                throw new ServiceException("allowedTools contains empty tool name", HttpStatus.BAD_REQUEST);
            }
            String trimmed = name.trim();
            if (!ALLOWED.contains(trimmed))
            {
                throw new ServiceException("Invalid tool name: " + trimmed, HttpStatus.BAD_REQUEST);
            }
        }
    }

    public static Set<String> allToolNames()
    {
        return ALLOWED;
    }
}
