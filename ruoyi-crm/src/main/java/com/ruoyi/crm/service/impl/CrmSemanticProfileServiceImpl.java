package com.ruoyi.crm.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.uuid.IdUtils;
import com.ruoyi.crm.domain.CrmSemanticProfile;
import com.ruoyi.crm.domain.dto.SemanticProfileSaveRequest;
import com.ruoyi.crm.domain.vo.SemanticProfileVO;
import com.ruoyi.crm.mapper.CrmSemanticProfileMapper;
import com.ruoyi.crm.service.ICrmSemanticProfileService;
import com.ruoyi.crm.service.support.SemanticProfileValidator;

@Service
public class CrmSemanticProfileServiceImpl implements ICrmSemanticProfileService
{
    private static final int DEFAULT_PROFILE_VERSION = 1;

    @Autowired
    private CrmSemanticProfileMapper crmSemanticProfileMapper;

    @Override
    public SemanticProfileVO getCurrent(String tenantId)
    {
        return crmSemanticProfileMapper.selectByTenantId(requireTenantId(tenantId));
    }

    @Override
    public void upsert(String tenantId, SemanticProfileSaveRequest request)
    {
        tenantId = requireTenantId(tenantId);
        SemanticProfileValidator.validateSaveRequest(request);

        Integer profileVersion = request.getProfileVersion() != null ? request.getProfileVersion() : DEFAULT_PROFILE_VERSION;
        CrmSemanticProfile existing = crmSemanticProfileMapper.selectEntityByTenantIdAny(tenantId);
        String username = SecurityUtils.getUsername();
        if (existing == null)
        {
            insertRow(tenantId, request, profileVersion, username);
            return;
        }

        existing.setDelFlag("0");
        existing.setIndustryKey(request.getIndustryKey().trim());
        existing.setEnabled(request.getEnabled().trim());
        existing.setProfileVersion(profileVersion);
        existing.setProfileJson(request.getProfileJson());
        existing.setUpdateBy(username);
        existing.setUpdateTime(DateUtils.getNowDate());
        crmSemanticProfileMapper.updateCrmSemanticProfile(existing);
    }

    @Override
    public void deleteCurrent(String tenantId)
    {
        tenantId = requireTenantId(tenantId);
        SemanticProfileVO current = crmSemanticProfileMapper.selectByTenantId(tenantId);
        if (current == null)
        {
            return;
        }
        String username = SecurityUtils.getUsername();
        crmSemanticProfileMapper.logicalDeleteByTenantId(tenantId, username);
    }

    private void insertRow(String tenantId, SemanticProfileSaveRequest request, Integer profileVersion, String username)
    {
        CrmSemanticProfile row = new CrmSemanticProfile();
        row.setProfileId(IdUtils.fastUUID());
        row.setTenantId(tenantId);
        row.setIndustryKey(request.getIndustryKey().trim());
        row.setEnabled(request.getEnabled().trim());
        row.setProfileVersion(profileVersion);
        row.setProfileJson(request.getProfileJson());
        row.setDelFlag("0");
        row.setCreateBy(username);
        row.setCreateTime(DateUtils.getNowDate());
        row.setUpdateBy(username);
        row.setUpdateTime(DateUtils.getNowDate());
        crmSemanticProfileMapper.insertCrmSemanticProfile(row);
    }

    private String requireTenantId(String tenantId)
    {
        if (StringUtils.isBlank(tenantId))
        {
            throw new ServiceException("缺少租户头 tenant", HttpStatus.BAD_REQUEST);
        }
        return tenantId.trim();
    }
}
