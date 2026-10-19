package com.ruoyi.crm.service;

import java.util.List;

import com.ruoyi.crm.domain.dto.WorkflowAutomationSaveRequest;
import com.ruoyi.crm.domain.vo.WorkflowAutomationVO;

/**
 * 工作流自动化配置 Service
 */
public interface ICrmWorkflowAutomationService
{
    List<WorkflowAutomationVO> selectWorkflowAutomationList(String tenantId);

    WorkflowAutomationVO getByKey(String tenantId, String automationKey);

    void add(String tenantId, WorkflowAutomationSaveRequest request);

    void upsert(String tenantId, WorkflowAutomationSaveRequest request);

    void removeByKey(String tenantId, String automationKey);
}
