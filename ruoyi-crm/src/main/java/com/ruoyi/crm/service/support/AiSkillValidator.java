package com.ruoyi.crm.service.support;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.domain.dto.AiSkillUpsertDto;

/**
 * AI Skill 字段校验
 */
public final class AiSkillValidator
{
    private static final int MAX_NAME_LEN = 128;
    private static final int MAX_INSTRUCTION_BYTES = 8192;

    private static final Set<String> SCOPES = new HashSet<>(
            Arrays.asList("all", "assistant", "workflow"));

    private static final Set<String> SKILL_TYPES = new HashSet<>(
            Arrays.asList("prompt", "data_fetch", "analysis", "strategy", "synthesis"));

    private AiSkillValidator()
    {
    }

    public static void validateUpsert(AiSkillUpsertDto dto)
    {
        if (dto == null)
        {
            throw new ServiceException("请求体不能为空", HttpStatus.BAD_REQUEST);
        }
        if (StringUtils.isBlank(dto.getName()))
        {
            throw new ServiceException("name 不能为空", HttpStatus.BAD_REQUEST);
        }
        if (dto.getName().trim().length() > MAX_NAME_LEN)
        {
            throw new ServiceException("name 长度不能超过 " + MAX_NAME_LEN, HttpStatus.BAD_REQUEST);
        }
        if (StringUtils.isBlank(dto.getInstruction()))
        {
            throw new ServiceException("instruction 不能为空", HttpStatus.BAD_REQUEST);
        }
        if (dto.getInstruction().getBytes(StandardCharsets.UTF_8).length > MAX_INSTRUCTION_BYTES)
        {
            throw new ServiceException("instruction 不能超过 " + MAX_INSTRUCTION_BYTES + " 字节", HttpStatus.BAD_REQUEST);
        }

        String scope = StringUtils.isNotBlank(dto.getScope()) ? dto.getScope().trim() : "all";
        if (!SCOPES.contains(scope))
        {
            throw new ServiceException("scope 无效", HttpStatus.BAD_REQUEST);
        }

        if (StringUtils.isNotBlank(dto.getSkillType()) && !SKILL_TYPES.contains(dto.getSkillType().trim()))
        {
            throw new ServiceException("skillType 无效", HttpStatus.BAD_REQUEST);
        }
    }

    public static void validateMcpReferences(String tenantId, AiSkillUpsertDto dto,
            McpServerReferenceChecker checker)
    {
        if (checker == null)
        {
            return;
        }
        if (dto.getMcpServerIds() != null)
        {
            for (Long serverId : dto.getMcpServerIds())
            {
                if (serverId != null && !checker.isEnabledServer(tenantId, serverId))
                {
                    throw new ServiceException("MCP server id=" + serverId + " 不存在或未启用", HttpStatus.BAD_REQUEST);
                }
            }
        }
        List<Map<String, Object>> bindings = dto.getToolBindings();
        if (bindings == null)
        {
            return;
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
            long serverId = parseLong(serverIdObj);
            if (!checker.isEnabledServer(tenantId, serverId))
            {
                throw new ServiceException("toolBindings 中 MCP server id=" + serverId + " 不存在或未启用",
                        HttpStatus.BAD_REQUEST);
            }
        }
    }

    private static long parseLong(Object value)
    {
        if (value instanceof Number)
        {
            return ((Number) value).longValue();
        }
        return Long.parseLong(String.valueOf(value));
    }

    /**
     * MCP server 引用校验回调（Phase 2 注入）
     */
    public interface McpServerReferenceChecker
    {
        boolean isEnabledServer(String tenantId, Long serverId);
    }
}
