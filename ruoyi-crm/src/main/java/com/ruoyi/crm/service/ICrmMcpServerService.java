package com.ruoyi.crm.service;

import java.util.List;

import com.ruoyi.crm.domain.dto.McpServerUpsertDto;
import com.ruoyi.crm.domain.vo.McpServerCredentialVo;
import com.ruoyi.crm.domain.vo.McpServerVo;

/**
 * MCP Server 服务
 */
public interface ICrmMcpServerService
{
    List<McpServerVo> list(String tenantId);

    McpServerVo upsert(String tenantId, McpServerUpsertDto dto);

    void delete(String tenantId, Long id);

    List<McpServerCredentialVo> listCredentials(String tenantId);
}
