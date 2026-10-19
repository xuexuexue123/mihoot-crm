package com.ruoyi.web.controller.crm;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.crm.domain.dto.McpApiKeyAuditDto;
import com.ruoyi.crm.domain.dto.McpApiKeyCreateDto;
import com.ruoyi.crm.domain.dto.McpApiKeyValidateDto;
import com.ruoyi.crm.service.ICrmMcpApiKeyService;
import com.ruoyi.crm.service.support.McpApiKeyBffSecretValidator;

/**
 * 入站 MCP API Key API
 */
@RestController
@RequestMapping("/crm/mcp/api-key")
public class CrmMcpApiKeyController extends BaseController
{
    @Autowired
    private ICrmMcpApiKeyService crmMcpApiKeyService;

    @Autowired
    private McpApiKeyBffSecretValidator mcpApiKeyBffSecretValidator;

    /**
     * BFF / MCP Server 专用：校验 API Key 并返回服务账号 JWT
     */
    @PostMapping("/validate")
    public AjaxResult validate(@RequestBody McpApiKeyValidateDto body, HttpServletRequest request)
    {
        mcpApiKeyBffSecretValidator.validate(request);
        return success(crmMcpApiKeyService.validate(body));
    }

    /**
     * Next BFF 异步上报 tools/call 审计
     */
    @PostMapping("/audit")
    public AjaxResult audit(@RequestBody McpApiKeyAuditDto body, HttpServletRequest request)
    {
        mcpApiKeyBffSecretValidator.validate(request);
        crmMcpApiKeyService.recordAudit(body);
        return success();
    }

    @PreAuthorize("@ss.hasPermi('crm:mcp:apikey:edit')")
    @GetMapping("/list")
    public AjaxResult list(@RequestHeader(value = "tenant", required = false) String tenant)
    {
        return success(crmMcpApiKeyService.list(tenant));
    }

    @PreAuthorize("@ss.hasPermi('crm:mcp:apikey:edit')")
    @Log(title = "MCP API Key", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult create(@RequestBody McpApiKeyCreateDto body,
            @RequestHeader(value = "tenant", required = false) String tenant)
    {
        return success(crmMcpApiKeyService.create(tenant, body));
    }

    @PreAuthorize("@ss.hasPermi('crm:mcp:apikey:edit')")
    @Log(title = "MCP API Key", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public AjaxResult delete(@PathVariable("id") Long id,
            @RequestHeader(value = "tenant", required = false) String tenant)
    {
        crmMcpApiKeyService.delete(tenant, id);
        return success();
    }

    @PreAuthorize("@ss.hasPermi('crm:mcp:apikey:edit')")
    @Log(title = "MCP API Key", businessType = BusinessType.UPDATE)
    @PostMapping("/rotate/{id}")
    public AjaxResult rotate(@PathVariable("id") Long id,
            @RequestHeader(value = "tenant", required = false) String tenant)
    {
        return success(crmMcpApiKeyService.rotate(tenant, id));
    }
}
