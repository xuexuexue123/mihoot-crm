package com.ruoyi.crm.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.sign.AESUtil;
import com.ruoyi.crm.domain.CrmAiSkill;
import com.ruoyi.crm.domain.CrmMcpServer;
import com.ruoyi.crm.domain.dto.McpServerUpsertDto;
import com.ruoyi.crm.domain.vo.McpServerCredentialVo;
import com.ruoyi.crm.domain.vo.McpServerVo;
import com.ruoyi.crm.mapper.CrmAiSkillMapper;
import com.ruoyi.crm.mapper.CrmMcpServerMapper;
import com.ruoyi.crm.service.ICrmMcpServerService;
import com.ruoyi.crm.service.support.DataTableTenantSupport;
import com.ruoyi.crm.service.support.McpServerConverter;

@Service
public class CrmMcpServerServiceImpl implements ICrmMcpServerService
{
    private static final Set<String> TRANSPORTS = new HashSet<>(Arrays.asList("http", "sse"));
    private static final Set<String> AUTH_TYPES = new HashSet<>(Arrays.asList("none", "bearer", "header"));

    @Autowired
    private CrmMcpServerMapper crmMcpServerMapper;

    @Autowired
    private CrmAiSkillMapper crmAiSkillMapper;

    @Override
    public List<McpServerVo> list(String tenantId)
    {
        tenantId = DataTableTenantSupport.requireTenantId(tenantId);
        return McpServerConverter.toVoList(crmMcpServerMapper.selectListByTenant(tenantId));
    }

    @Override
    public McpServerVo upsert(String tenantId, McpServerUpsertDto dto)
    {
        tenantId = DataTableTenantSupport.requireTenantId(tenantId);
        validateUpsert(dto);

        String name = dto.getName().trim();
        CrmMcpServer duplicate = crmMcpServerMapper.selectByNameAndTenant(name, tenantId);
        if (duplicate != null && (dto.getId() == null || !duplicate.getId().equals(dto.getId())))
        {
            throw new ServiceException("MCP server name already exists", HttpStatus.CONFLICT);
        }

        String username = SecurityUtils.getUsername();
        CrmMcpServer existing = dto.getId() != null ? crmMcpServerMapper.selectByIdAndTenant(dto.getId(), tenantId) : null;
        if (dto.getId() != null && existing == null)
        {
            throw new ServiceException("MCP server not found", HttpStatus.NOT_FOUND);
        }

        CrmMcpServer row = buildRow(tenantId, dto, existing, username);
        if (existing == null)
        {
            row.setCreateBy(username);
            row.setCreateTime(DateUtils.getNowDate());
            crmMcpServerMapper.insert(row);
        }
        else
        {
            row.setId(existing.getId());
            row.setCreateBy(existing.getCreateBy());
            row.setCreateTime(existing.getCreateTime());
            crmMcpServerMapper.update(row);
        }
        return McpServerConverter.toVo(crmMcpServerMapper.selectByIdAndTenant(row.getId(), tenantId));
    }

    @Override
    public void delete(String tenantId, Long id)
    {
        tenantId = DataTableTenantSupport.requireTenantId(tenantId);
        if (id == null)
        {
            throw new ServiceException("id 不能为空", HttpStatus.BAD_REQUEST);
        }
        CrmMcpServer existing = crmMcpServerMapper.selectByIdAndTenant(id, tenantId);
        if (existing == null)
        {
            throw new ServiceException("MCP server not found", HttpStatus.NOT_FOUND);
        }
        List<CrmAiSkill> skills = crmAiSkillMapper.selectAllByTenant(tenantId);
        for (CrmAiSkill skill : skills)
        {
            if (McpServerConverter.skillReferencesServer(skill, id))
            {
                throw new ServiceException("MCP server is referenced by skill id=" + skill.getId(), HttpStatus.CONFLICT);
            }
        }
        crmMcpServerMapper.deleteByIdAndTenant(id, tenantId);
    }

    @Override
    public List<McpServerCredentialVo> listCredentials(String tenantId)
    {
        tenantId = DataTableTenantSupport.requireTenantId(tenantId);
        List<CrmMcpServer> rows = crmMcpServerMapper.selectListByTenant(tenantId);
        List<McpServerCredentialVo> result = new ArrayList<>();
        for (CrmMcpServer row : rows)
        {
            if (row.getEnabled() != null && row.getEnabled() == 1)
            {
                result.add(McpServerConverter.toCredentialVo(row));
            }
        }
        return result;
    }

    private void validateUpsert(McpServerUpsertDto dto)
    {
        if (dto == null)
        {
            throw new ServiceException("请求体不能为空", HttpStatus.BAD_REQUEST);
        }
        if (StringUtils.isBlank(dto.getName()))
        {
            throw new ServiceException("name 不能为空", HttpStatus.BAD_REQUEST);
        }
        if (StringUtils.isBlank(dto.getUrl()))
        {
            throw new ServiceException("url 不能为空", HttpStatus.BAD_REQUEST);
        }
        String transport = StringUtils.isNotBlank(dto.getTransport()) ? dto.getTransport().trim() : "http";
        if (!TRANSPORTS.contains(transport))
        {
            throw new ServiceException("transport 无效", HttpStatus.BAD_REQUEST);
        }
        String authType = StringUtils.isNotBlank(dto.getAuthType()) ? dto.getAuthType().trim() : "none";
        if (!AUTH_TYPES.contains(authType))
        {
            throw new ServiceException("authType 无效", HttpStatus.BAD_REQUEST);
        }
        if (!"none".equals(authType) && dto.getId() == null && StringUtils.isBlank(dto.getAuthSecret()))
        {
            throw new ServiceException("authSecret 不能为空", HttpStatus.BAD_REQUEST);
        }
    }

    private CrmMcpServer buildRow(String tenantId, McpServerUpsertDto dto, CrmMcpServer existing, String username)
    {
        CrmMcpServer row = new CrmMcpServer();
        row.setTenantId(tenantId);
        row.setName(dto.getName().trim());
        row.setUrl(dto.getUrl().trim());
        row.setTransport(StringUtils.isNotBlank(dto.getTransport()) ? dto.getTransport().trim() : "http");
        row.setAuthType(StringUtils.isNotBlank(dto.getAuthType()) ? dto.getAuthType().trim() : "none");
        row.setAllowedTools(McpServerConverter.toJsonString(dto.getAllowedTools()));
        row.setEnabled(Boolean.FALSE.equals(dto.getEnabled()) ? 0 : 1);
        row.setUpdateBy(username);
        row.setUpdateTime(DateUtils.getNowDate());

        try
        {
            if (StringUtils.isNotBlank(dto.getAuthSecret()))
            {
                row.setAuthSecretEnc(AESUtil.encrypt(dto.getAuthSecret().trim()));
            }
            else if (existing != null)
            {
                row.setAuthSecretEnc(existing.getAuthSecretEnc());
            }
            else if ("none".equals(row.getAuthType()))
            {
                row.setAuthSecretEnc(null);
            }
        }
        catch (Exception e)
        {
            throw new ServiceException("Secret 加密失败", HttpStatus.ERROR);
        }
        return row;
    }
}
