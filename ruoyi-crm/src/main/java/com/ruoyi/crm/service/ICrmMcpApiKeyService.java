package com.ruoyi.crm.service;

import java.util.List;

import com.ruoyi.crm.domain.dto.McpApiKeyAuditDto;
import com.ruoyi.crm.domain.dto.McpApiKeyCreateDto;
import com.ruoyi.crm.domain.dto.McpApiKeyValidateDto;
import com.ruoyi.crm.domain.vo.McpApiKeyCreateResultVo;
import com.ruoyi.crm.domain.vo.McpApiKeyValidateVo;
import com.ruoyi.crm.domain.vo.McpApiKeyVo;

/**
 * 入站 MCP API Key 服务
 */
public interface ICrmMcpApiKeyService
{
    List<McpApiKeyVo> list(String tenantId);

    McpApiKeyCreateResultVo create(String tenantId, McpApiKeyCreateDto dto);

    void delete(String tenantId, Long id);

    McpApiKeyCreateResultVo rotate(String tenantId, Long id);

    McpApiKeyValidateVo validate(McpApiKeyValidateDto dto);

    void recordAudit(McpApiKeyAuditDto dto);
}
