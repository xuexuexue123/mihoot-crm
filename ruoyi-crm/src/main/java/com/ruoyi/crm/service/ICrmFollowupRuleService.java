package com.ruoyi.crm.service;

import java.util.List;
import com.ruoyi.crm.domain.dto.FollowupRuleSaveRequest;
import com.ruoyi.crm.domain.vo.FollowupRuleEnrollmentVo;
import com.ruoyi.crm.domain.vo.FollowupRuleVo;

public interface ICrmFollowupRuleService
{
    List<FollowupRuleVo> selectList(String tenantId, String triggerType, String enabled);

    FollowupRuleVo getById(String tenantId, String ruleId);

    void add(String tenantId, FollowupRuleSaveRequest request);

    void update(String tenantId, FollowupRuleSaveRequest request);

    void remove(String tenantId, String ruleId);

    List<FollowupRuleEnrollmentVo> selectEnrollmentList(String tenantId, String ruleId, String status, String subjectId);

    List<String> selectEnabledCatalogKeys(String tenantId);
}
