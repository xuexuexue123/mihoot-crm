package com.ruoyi.crm.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ruoyi.crm.domain.CrmWhatsappAutomationRule;

public interface CrmWhatsappAutomationRuleMapper
{
    List<CrmWhatsappAutomationRule> selectByTenant(@Param("tenantId") String tenantId);

    List<CrmWhatsappAutomationRule> selectEnabledByTenantAndType(
            @Param("tenantId") String tenantId,
            @Param("triggerType") String triggerType);

    CrmWhatsappAutomationRule selectById(@Param("id") Long id, @Param("tenantId") String tenantId);

    int insert(CrmWhatsappAutomationRule row);

    int update(CrmWhatsappAutomationRule row);

    int deleteById(@Param("id") Long id, @Param("tenantId") String tenantId);
}
