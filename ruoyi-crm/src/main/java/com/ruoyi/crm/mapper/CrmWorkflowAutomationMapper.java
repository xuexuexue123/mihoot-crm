package com.ruoyi.crm.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ruoyi.crm.domain.CrmWorkflowAutomation;
import com.ruoyi.crm.domain.vo.WorkflowAutomationVO;

/**
 * 工作流自动化配置 Mapper
 */
public interface CrmWorkflowAutomationMapper
{
    List<WorkflowAutomationVO> selectWorkflowAutomationList(@Param("tenantId") String tenantId);

    WorkflowAutomationVO selectByTenantAndKey(@Param("tenantId") String tenantId,
            @Param("automationKey") String automationKey);

    CrmWorkflowAutomation selectEntityByTenantAndKey(@Param("tenantId") String tenantId,
            @Param("automationKey") String automationKey);

    CrmWorkflowAutomation selectEntityByTenantAndKeyAny(@Param("tenantId") String tenantId,
            @Param("automationKey") String automationKey);

    int insertCrmWorkflowAutomation(CrmWorkflowAutomation row);

    int updateCrmWorkflowAutomation(CrmWorkflowAutomation row);

    int logicalDeleteByTenantAndKey(@Param("tenantId") String tenantId,
            @Param("automationKey") String automationKey,
            @Param("updateBy") String updateBy);
}
