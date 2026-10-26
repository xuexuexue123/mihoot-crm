package com.ruoyi.crm.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.crm.domain.CrmRecallPolicy;

public interface CrmRecallPolicyMapper
{
    List<CrmRecallPolicy> selectList(@Param("tenantId") String tenantId,
            @Param("enabled") String enabled);

    CrmRecallPolicy selectEntityById(@Param("tenantId") String tenantId, @Param("policyId") String policyId);

    List<CrmRecallPolicy> selectEnabledOrdered(@Param("tenantId") String tenantId);

    int insertCrmRecallPolicy(CrmRecallPolicy row);

    int updateCrmRecallPolicy(CrmRecallPolicy row);

    int logicalDeleteById(@Param("policyId") String policyId,
            @Param("tenantId") String tenantId,
            @Param("updateBy") String updateBy);
}
