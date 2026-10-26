package com.ruoyi.web.controller.crm;

import java.util.List;

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
import com.ruoyi.crm.domain.dto.McpServerUpsertDto;
import com.ruoyi.crm.domain.vo.McpServerCredentialVo;
import com.ruoyi.crm.domain.vo.McpServerVo;
import com.ruoyi.crm.service.ICrmMcpServerService;

/**
 * MCP Server 配置 API
 */
@RestController
@RequestMapping("/crm/mcp/server")
public class CrmMcpServerController extends BaseController
{
    @Autowired
    private ICrmMcpServerService crmMcpServerService;

    @PreAuthorize("@ss.hasPermi('crm:mcp:config:list')")
    @GetMapping("/list")
    public AjaxResult list(@RequestHeader(value = "tenant", required = false) String tenant)
    {
        List<McpServerVo> data = crmMcpServerService.list(tenant);
        return success(data);
    }

    @PreAuthorize("@ss.hasPermi('crm:mcp:config:edit')")
    @Log(title = "MCP Server", businessType = BusinessType.UPDATE)
    @PostMapping
    public AjaxResult upsert(@RequestBody McpServerUpsertDto body,
            @RequestHeader(value = "tenant", required = false) String tenant)
    {
        McpServerVo data = crmMcpServerService.upsert(tenant, body);
        return success(data);
    }

    @PreAuthorize("@ss.hasPermi('crm:mcp:config:edit')")
    @Log(title = "MCP Server", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public AjaxResult delete(@PathVariable("id") Long id,
            @RequestHeader(value = "tenant", required = false) String tenant)
    {
        crmMcpServerService.delete(tenant, id);
        return success();
    }

    @PreAuthorize("@ss.hasPermi('crm:mcp:runtime:read')")
    @GetMapping("/credentials")
    public AjaxResult credentials(@RequestHeader(value = "tenant", required = false) String tenant)
    {
        List<McpServerCredentialVo> data = crmMcpServerService.listCredentials(tenant);
        return success(data);
    }
}
