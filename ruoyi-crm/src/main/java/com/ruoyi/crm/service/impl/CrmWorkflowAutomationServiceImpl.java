package com.ruoyi.crm.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.uuid.IdUtils;
import com.ruoyi.crm.domain.CrmWorkflowAutomation;
import com.ruoyi.crm.domain.WWorkflow;
import com.ruoyi.crm.domain.dto.WorkflowAutomationSaveRequest;
import com.ruoyi.crm.domain.vo.WorkflowAutomationVO;
import com.ruoyi.crm.enums.WorkflowAutomationKey;
import com.ruoyi.crm.mapper.CrmWorkflowAutomationMapper;
import com.ruoyi.crm.mapper.WWorkflowMapper;
import com.ruoyi.crm.service.ICrmWorkflowAutomationService;

@Service
public class CrmWorkflowAutomationServiceImpl implements ICrmWorkflowAutomationService
{
    private static final String ENABLED = "1";
    private static final String DISABLED = "0";

    @Autowired
    private CrmWorkflowAutomationMapper crmWorkflowAutomationMapper;

    @Autowired
    private WWorkflowMapper wWorkflowMapper;

    @Override
    public List<WorkflowAutomationVO> selectWorkflowAutomationList(String tenantId)
    {
        return crmWorkflowAutomationMapper.selectWorkflowAutomationList(tenantId);
    }

    @Override
    public WorkflowAutomationVO getByKey(String tenantId, String automationKey)
    {
        validateAutomationKey(automationKey);
        return crmWorkflowAutomationMapper.selectByTenantAndKey(tenantId, automationKey);
    }

    @Override
    public void add(String tenantId, WorkflowAutomationSaveRequest request)
    {
        ValidatedAutomation validated = validateRequest(request);
        CrmWorkflowAutomation existing = crmWorkflowAutomationMapper.selectEntityByTenantAndKeyAny(tenantId,
                request.getAutomationKey());
        if (existing != null)
        {
            throw new ServiceException("该 automationKey 配置已存在，请使用编辑接口");
        }
        insertRow(tenantId, request.getAutomationKey(), validated);
    }

    @Override
    public void upsert(String tenantId, WorkflowAutomationSaveRequest request)
    {
        ValidatedAutomation validated = validateRequest(request);
        CrmWorkflowAutomation existing = crmWorkflowAutomationMapper.selectEntityByTenantAndKeyAny(tenantId,
                request.getAutomationKey());
        String username = SecurityUtils.getUsername();
        if (existing == null)
        {
            insertRow(tenantId, request.getAutomationKey(), validated);
            return;
        }

        existing.setDelFlag("0");
        existing.setWorkflowId(validated.workflowId);
        existing.setEnabled(validated.enabled);
        existing.setTriggerEvent(validated.triggerEvent);
        existing.setConfigJson(request.getConfigJson());
        existing.setRemark(request.getRemark());
        existing.setUpdateBy(username);
        existing.setUpdateTime(DateUtils.getNowDate());
        crmWorkflowAutomationMapper.updateCrmWorkflowAutomation(existing);
    }

    @Override
    public void removeByKey(String tenantId, String automationKey)
    {
        validateAutomationKey(automationKey);
        CrmWorkflowAutomation existing = crmWorkflowAutomationMapper.selectEntityByTenantAndKey(tenantId, automationKey);
        if (existing == null)
        {
            return;
        }
        String username = SecurityUtils.getUsername();
        crmWorkflowAutomationMapper.logicalDeleteByTenantAndKey(tenantId, automationKey, username);
    }

    private void insertRow(String tenantId, String automationKey, ValidatedAutomation validated)
    {
        String username = SecurityUtils.getUsername();
        CrmWorkflowAutomation row = new CrmWorkflowAutomation();
        row.setConfigId(IdUtils.fastUUID());
        row.setTenantId(tenantId);
        row.setAutomationKey(automationKey);
        row.setWorkflowId(validated.workflowId);
        row.setEnabled(validated.enabled);
        row.setTriggerEvent(validated.triggerEvent);
        row.setConfigJson(validated.configJson);
        row.setDelFlag("0");
        row.setRemark(validated.remark);
        row.setCreateBy(username);
        row.setCreateTime(DateUtils.getNowDate());
        row.setUpdateBy(username);
        row.setUpdateTime(DateUtils.getNowDate());
        crmWorkflowAutomationMapper.insertCrmWorkflowAutomation(row);
    }

    private ValidatedAutomation validateRequest(WorkflowAutomationSaveRequest request)
    {
        if (request == null || StringUtils.isBlank(request.getAutomationKey()))
        {
            throw new ServiceException("automationKey 不能为空");
        }
        if (StringUtils.isBlank(request.getEnabled()))
        {
            throw new ServiceException("enabled 不能为空");
        }

        WorkflowAutomationKey keyEnum = validateAutomationKey(request.getAutomationKey());
        String enabled = normalizeEnabled(request.getEnabled());
        String workflowId = trimToNull(request.getWorkflowId());

        if (ENABLED.equals(enabled) && StringUtils.isBlank(workflowId))
        {
            throw new ServiceException("启用时必须选择工作流");
        }
        if (StringUtils.isNotBlank(workflowId))
        {
            WWorkflow workflow = wWorkflowMapper.selectWWorkflowById(workflowId);
            if (workflow == null)
            {
                throw new ServiceException("工作流不存在或不属于当前租户");
            }
            if (Boolean.TRUE.equals(workflow.getIsTemplate()))
            {
                throw new ServiceException("模板不可绑定为自动化工作流");
            }
        }

        String triggerEvent = StringUtils.isNotBlank(request.getTriggerEvent())
                ? request.getTriggerEvent().trim()
                : keyEnum.getDefaultTriggerEvent();

        if (!ENABLED.equals(enabled))
        {
            workflowId = null;
        }

        ValidatedAutomation validated = new ValidatedAutomation();
        validated.enabled = enabled;
        validated.workflowId = workflowId;
        validated.triggerEvent = triggerEvent;
        validated.configJson = request.getConfigJson();
        validated.remark = request.getRemark();
        return validated;
    }

    private WorkflowAutomationKey validateAutomationKey(String automationKey)
    {
        WorkflowAutomationKey keyEnum = WorkflowAutomationKey.fromKey(automationKey);
        if (keyEnum == null)
        {
            throw new ServiceException("不支持的 automationKey: " + automationKey);
        }
        return keyEnum;
    }

    private String normalizeEnabled(String enabled)
    {
        if (ENABLED.equals(enabled) || DISABLED.equals(enabled))
        {
            return enabled;
        }
        throw new ServiceException("enabled 必须为 0 或 1");
    }

    private String trimToNull(String value)
    {
        if (value == null)
        {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private static class ValidatedAutomation
    {
        private String enabled;
        private String workflowId;
        private String triggerEvent;
        private String configJson;
        private String remark;
    }
}
