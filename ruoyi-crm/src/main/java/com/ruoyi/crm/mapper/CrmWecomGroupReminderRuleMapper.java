package com.ruoyi.crm.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.crm.domain.CrmWecomGroupReminderRule;

public interface CrmWecomGroupReminderRuleMapper
{
    CrmWecomGroupReminderRule selectById(@Param("tenantId") String tenantId, @Param("ruleId") String ruleId);

    List<CrmWecomGroupReminderRule> selectList(CrmWecomGroupReminderRule query);

    List<CrmWecomGroupReminderRule> selectEnabledByAnchor(@Param("tenantId") String tenantId,
            @Param("anchorType") String anchorType);

    int insert(CrmWecomGroupReminderRule row);

    int update(CrmWecomGroupReminderRule row);

    int softDelete(@Param("tenantId") String tenantId, @Param("ruleId") String ruleId);
}
