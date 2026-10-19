package com.ruoyi.crm.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.crm.domain.CrmFollowupRule;
import com.ruoyi.crm.domain.vo.FollowupRuleVo;

public interface CrmFollowupRuleMapper
{
    List<FollowupRuleVo> selectList(@Param("tenantId") String tenantId,
            @Param("triggerType") String triggerType,
            @Param("enabled") String enabled);

    FollowupRuleVo selectById(@Param("tenantId") String tenantId, @Param("ruleId") String ruleId);

    CrmFollowupRule selectEntityById(@Param("tenantId") String tenantId, @Param("ruleId") String ruleId);

    List<CrmFollowupRule> selectEnabledByTrigger(@Param("tenantId") String tenantId,
            @Param("triggerType") String triggerType);

    List<String> selectEnabledCatalogKeys(@Param("tenantId") String tenantId);

    int insertCrmFollowupRule(CrmFollowupRule row);

    int updateCrmFollowupRule(CrmFollowupRule row);

    int logicalDeleteById(@Param("ruleId") String ruleId,
            @Param("tenantId") String tenantId,
            @Param("updateBy") String updateBy);
}
