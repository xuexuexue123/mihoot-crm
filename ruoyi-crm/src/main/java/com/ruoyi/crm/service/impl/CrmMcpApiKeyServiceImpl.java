package com.ruoyi.crm.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.domain.CrmMcpApiKey;
import com.ruoyi.crm.domain.CrmMcpAuditLog;
import com.ruoyi.crm.domain.dto.McpApiKeyAuditDto;
import com.ruoyi.crm.domain.dto.McpApiKeyCreateDto;
import com.ruoyi.crm.domain.dto.McpApiKeyValidateDto;
import com.ruoyi.crm.domain.vo.McpApiKeyCreateResultVo;
import com.ruoyi.crm.domain.vo.McpApiKeyValidateVo;
import com.ruoyi.crm.domain.vo.McpApiKeyVo;
import com.ruoyi.crm.mapper.CrmMcpApiKeyMapper;
import com.ruoyi.crm.mapper.CrmMcpAuditLogMapper;
import com.ruoyi.crm.service.ICrmMcpApiKeyService;
import com.ruoyi.crm.service.support.DataTableTenantSupport;
import com.ruoyi.crm.service.support.McpAgentToolCatalog;
import com.ruoyi.crm.service.support.McpApiKeyConverter;
import com.ruoyi.crm.service.support.McpApiKeyHashUtil;
import com.ruoyi.crm.service.support.McpApiKeyMaterialGenerator;
import com.ruoyi.crm.service.support.McpApiKeyMaterialGenerator.GeneratedMaterial;
import com.ruoyi.crm.service.support.McpApiKeyParser;
import com.ruoyi.crm.service.support.McpApiKeyParser.ParsedMcpKey;
import com.ruoyi.crm.service.support.McpApiKeyTenantResolver;
import com.ruoyi.crm.service.support.McpCrmAuthService;
import com.ruoyi.crm.service.support.McpTenantCode;

@Service
public class CrmMcpApiKeyServiceImpl implements ICrmMcpApiKeyService
{
    private static final String INVALID_KEY_MSG = "Invalid API key";

    @Autowired
    private CrmMcpApiKeyMapper crmMcpApiKeyMapper;

    @Autowired
    private CrmMcpAuditLogMapper crmMcpAuditLogMapper;

    @Autowired
    private McpCrmAuthService mcpCrmAuthService;

    @Autowired
    private McpApiKeyTenantResolver mcpApiKeyTenantResolver;

    @Autowired
    private McpTenantCode mcpTenantCode;

    @Override
    public List<McpApiKeyVo> list(String tenantId)
    {
        tenantId = DataTableTenantSupport.requireTenantId(tenantId);
        return McpApiKeyConverter.toVoList(crmMcpApiKeyMapper.selectListByTenant(tenantId), mcpTenantCode);
    }

    @Override
    public McpApiKeyCreateResultVo create(String tenantId, McpApiKeyCreateDto dto)
    {
        tenantId = DataTableTenantSupport.requireTenantId(tenantId);
        validateCreate(dto);

        String name = dto.getName().trim();
        if (crmMcpApiKeyMapper.selectByNameAndTenant(name, tenantId) != null)
        {
            throw new ServiceException("API key name already exists", HttpStatus.CONFLICT);
        }

        String boundUserId = resolveBoundUserId(dto.getUserId());
        McpAgentToolCatalog.validateAllowedTools(dto.getAllowedTools());

        String tenantCode = mcpTenantCode.compute(tenantId);
        GeneratedMaterial material = McpApiKeyMaterialGenerator.generate(tenantCode);
        String username = SecurityUtils.getUsername();
        Date now = DateUtils.getNowDate();

        CrmMcpApiKey row = new CrmMcpApiKey();
        row.setTenantId(tenantId);
        row.setName(name);
        row.setKeyPrefix(material.getKeyPrefix());
        row.setKeyHash(material.getKeyHash());
        row.setBoundUserId(boundUserId);
        row.setAllowedTools(McpApiKeyConverter.toJsonString(dto.getAllowedTools()));
        row.setEnabled(Boolean.FALSE.equals(dto.getEnabled()) ? 0 : 1);
        row.setExpiresAt(dto.getExpiresAt());
        row.setCreateBy(username);
        row.setCreateTime(now);
        row.setUpdateBy(username);
        row.setUpdateTime(now);

        try
        {
            mcpCrmAuthService.issueAndEncryptToken(row);
        }
        catch (Exception e)
        {
            throw new ServiceException("Failed to issue service token", HttpStatus.ERROR);
        }

        crmMcpApiKeyMapper.insert(row);
        return McpApiKeyConverter.toCreateResultVo(
                crmMcpApiKeyMapper.selectByIdAndTenant(row.getId(), tenantId),
                material.getApiKey(),
                mcpTenantCode);
    }

    @Override
    public void delete(String tenantId, Long id)
    {
        tenantId = DataTableTenantSupport.requireTenantId(tenantId);
        if (id == null)
        {
            throw new ServiceException("id 不能为空", HttpStatus.BAD_REQUEST);
        }
        CrmMcpApiKey existing = crmMcpApiKeyMapper.selectByIdAndTenant(id, tenantId);
        if (existing == null)
        {
            throw new ServiceException("API key not found", HttpStatus.NOT_FOUND);
        }
        crmMcpApiKeyMapper.disableByIdAndTenant(id, tenantId, SecurityUtils.getUsername(), DateUtils.getNowDate());
    }

    @Override
    public McpApiKeyCreateResultVo rotate(String tenantId, Long id)
    {
        tenantId = DataTableTenantSupport.requireTenantId(tenantId);
        if (id == null)
        {
            throw new ServiceException("id 不能为空", HttpStatus.BAD_REQUEST);
        }
        CrmMcpApiKey existing = crmMcpApiKeyMapper.selectByIdAndTenant(id, tenantId);
        if (existing == null)
        {
            throw new ServiceException("API key not found", HttpStatus.NOT_FOUND);
        }

        String tenantCode = mcpTenantCode.compute(tenantId);
        GeneratedMaterial material = McpApiKeyMaterialGenerator.generate(tenantCode);
        existing.setKeyPrefix(material.getKeyPrefix());
        existing.setKeyHash(material.getKeyHash());
        existing.setUpdateBy(SecurityUtils.getUsername());
        existing.setUpdateTime(DateUtils.getNowDate());

        try
        {
            mcpCrmAuthService.issueAndEncryptToken(existing);
        }
        catch (Exception e)
        {
            throw new ServiceException("Failed to issue service token", HttpStatus.ERROR);
        }

        crmMcpApiKeyMapper.update(existing);
        return McpApiKeyConverter.toCreateResultVo(
                crmMcpApiKeyMapper.selectByIdAndTenant(id, tenantId),
                material.getApiKey(),
                mcpTenantCode);
    }

    @Override
    public McpApiKeyValidateVo validate(McpApiKeyValidateDto dto)
    {
        if (dto == null || StringUtils.isBlank(dto.getApiKey()))
        {
            throw new ServiceException(INVALID_KEY_MSG, HttpStatus.UNAUTHORIZED);
        }

        ParsedMcpKey parsed = McpApiKeyParser.parse(dto.getApiKey()).orElse(null);
        if (parsed == null)
        {
            throw new ServiceException(INVALID_KEY_MSG, HttpStatus.UNAUTHORIZED);
        }

        String tenantId = mcpApiKeyTenantResolver.resolveByTenantCode(parsed.getTenantCode());
        if (tenantId == null)
        {
            throw new ServiceException(INVALID_KEY_MSG, HttpStatus.UNAUTHORIZED);
        }

        String serviceToken;
        CrmMcpApiKey row;
        try
        {
            mcpApiKeyTenantResolver.bindTenant(tenantId);
            row = crmMcpApiKeyMapper.selectByKeyPrefix(parsed.getKeyPrefix());
            if (row == null || row.getEnabled() == null || row.getEnabled() != 1)
            {
                throw new ServiceException(INVALID_KEY_MSG, HttpStatus.UNAUTHORIZED);
            }
            if (row.getExpiresAt() != null && row.getExpiresAt().before(new Date()))
            {
                throw new ServiceException(INVALID_KEY_MSG, HttpStatus.UNAUTHORIZED);
            }
            if (!McpApiKeyHashUtil.verifySecret(parsed.getSecret(), row.getKeyHash()))
            {
                throw new ServiceException(INVALID_KEY_MSG, HttpStatus.UNAUTHORIZED);
            }
            if (!tenantId.equals(row.getTenantId()))
            {
                throw new ServiceException(INVALID_KEY_MSG, HttpStatus.UNAUTHORIZED);
            }

            serviceToken = mcpCrmAuthService.resolveServiceToken(row, key -> {
                key.setUpdateTime(DateUtils.getNowDate());
                crmMcpApiKeyMapper.updateServiceTokenEnc(key);
            });
        }
        catch (ServiceException e)
        {
            throw e;
        }
        catch (IllegalArgumentException e)
        {
            throw new ServiceException(INVALID_KEY_MSG, HttpStatus.UNAUTHORIZED);
        }
        catch (Exception e)
        {
            throw new ServiceException("Service unavailable", HttpStatus.ERROR);
        }
        finally
        {
            mcpApiKeyTenantResolver.clearTenant();
        }

        McpApiKeyValidateVo vo = new McpApiKeyValidateVo();
        vo.setApiKeyId(row.getId());
        vo.setTenant(row.getTenantId());
        vo.setUserId(row.getBoundUserId());
        vo.setServiceToken(serviceToken);
        vo.setAllowedTools(McpApiKeyConverter.parseStringList(row.getAllowedTools()));
        return vo;
    }

    @Override
    public void recordAudit(McpApiKeyAuditDto dto)
    {
        if (dto == null || StringUtils.isBlank(dto.getToolName()))
        {
            throw new ServiceException("toolName 不能为空", HttpStatus.BAD_REQUEST);
        }
        String tenantId = DataTableTenantSupport.requireTenantId(dto.getTenant());

        CrmMcpAuditLog log = new CrmMcpAuditLog();
        log.setTenantId(tenantId);
        log.setApiKeyId(dto.getApiKeyId());
        log.setBoundUserId(dto.getUserId());
        log.setToolName(dto.getToolName().trim());
        log.setOk(Boolean.FALSE.equals(dto.getOk()) ? 0 : 1);
        log.setErrorCode(dto.getErrorCode());
        log.setLatencyMs(dto.getLatencyMs());
        log.setCreateTime(DateUtils.getNowDate());
        try
        {
            mcpApiKeyTenantResolver.bindTenant(tenantId);
            crmMcpAuditLogMapper.insert(log);
        }
        finally
        {
            mcpApiKeyTenantResolver.clearTenant();
        }
    }

    private void validateCreate(McpApiKeyCreateDto dto)
    {
        if (dto == null)
        {
            throw new ServiceException("请求体不能为空", HttpStatus.BAD_REQUEST);
        }
        if (StringUtils.isBlank(dto.getName()))
        {
            throw new ServiceException("name 不能为空", HttpStatus.BAD_REQUEST);
        }
    }

    private String resolveBoundUserId(String userId)
    {
        if (StringUtils.isNotBlank(userId))
        {
            return userId.trim();
        }
        return SecurityUtils.getUsername();
    }
}
