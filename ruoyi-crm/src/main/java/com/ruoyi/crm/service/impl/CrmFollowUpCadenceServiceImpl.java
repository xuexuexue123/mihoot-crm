package com.ruoyi.crm.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.uuid.IdUtils;
import com.ruoyi.crm.domain.CrmFollowUpCadence;
import com.ruoyi.crm.domain.dto.FollowUpCadenceSaveRequest;
import com.ruoyi.crm.domain.vo.FollowUpCadenceVo;
import com.ruoyi.crm.mapper.CrmFollowUpCadenceMapper;
import com.ruoyi.crm.service.ICrmFollowUpCadenceService;
import com.ruoyi.crm.service.support.FollowUpCadenceValidator;
import com.ruoyi.crm.service.support.FollowUpCadenceValidator.ValidatedFollowUpCadence;

@Service
public class CrmFollowUpCadenceServiceImpl implements ICrmFollowUpCadenceService
{
    @Autowired
    private CrmFollowUpCadenceMapper crmFollowUpCadenceMapper;

    @Override
    public List<FollowUpCadenceVo> selectList(String tenantId)
    {
        return crmFollowUpCadenceMapper.selectList(requireTenantId(tenantId));
    }

    @Override
    public FollowUpCadenceVo getById(String tenantId, String cadenceId)
    {
        tenantId = requireTenantId(tenantId);
        if (StringUtils.isBlank(cadenceId))
        {
            return null;
        }
        return crmFollowUpCadenceMapper.selectById(tenantId, cadenceId.trim());
    }

    @Override
    public void add(String tenantId, FollowUpCadenceSaveRequest request)
    {
        tenantId = requireTenantId(tenantId);
        ValidatedFollowUpCadence validated = FollowUpCadenceValidator.validateSaveRequest(request, false);
        String username = SecurityUtils.getUsername();
        CrmFollowUpCadence row = new CrmFollowUpCadence();
        row.setCadenceId(IdUtils.fastUUID());
        row.setTenantId(tenantId);
        applyValidated(row, validated);
        row.setDelFlag("0");
        row.setCreateBy(username);
        row.setCreateTime(DateUtils.getNowDate());
        row.setUpdateBy(username);
        row.setUpdateTime(DateUtils.getNowDate());
        crmFollowUpCadenceMapper.insertCrmFollowUpCadence(row);
    }

    @Override
    public void update(String tenantId, FollowUpCadenceSaveRequest request)
    {
        tenantId = requireTenantId(tenantId);
        ValidatedFollowUpCadence validated = FollowUpCadenceValidator.validateSaveRequest(request, true);
        CrmFollowUpCadence existing = crmFollowUpCadenceMapper.selectEntityById(tenantId, validated.cadenceId);
        if (existing == null)
        {
            throw new ServiceException("跟进序列不存在或不属于当前租户", HttpStatus.BAD_REQUEST);
        }
        String username = SecurityUtils.getUsername();
        applyValidated(existing, validated);
        existing.setUpdateBy(username);
        existing.setUpdateTime(DateUtils.getNowDate());
        crmFollowUpCadenceMapper.updateCrmFollowUpCadence(existing);
    }

    @Override
    public void remove(String tenantId, String cadenceId)
    {
        tenantId = requireTenantId(tenantId);
        if (StringUtils.isBlank(cadenceId))
        {
            return;
        }
        CrmFollowUpCadence existing = crmFollowUpCadenceMapper.selectEntityById(tenantId, cadenceId.trim());
        if (existing == null)
        {
            return;
        }
        crmFollowUpCadenceMapper.logicalDeleteById(existing.getCadenceId(), tenantId, SecurityUtils.getUsername());
    }

    private void applyValidated(CrmFollowUpCadence row, ValidatedFollowUpCadence validated)
    {
        row.setName(validated.name);
        row.setDescription(validated.description);
        row.setEnabled(validated.enabled);
        row.setStartTrigger(validated.startTrigger);
        row.setStepsJson(validated.stepsJson);
        row.setRemark(validated.remark);
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
