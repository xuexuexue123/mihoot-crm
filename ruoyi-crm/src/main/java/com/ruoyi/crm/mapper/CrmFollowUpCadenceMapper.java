package com.ruoyi.crm.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ruoyi.crm.domain.CrmFollowUpCadence;
import com.ruoyi.crm.domain.vo.FollowUpCadenceVo;

/**
 * 跟进序列配置 Mapper
 */
public interface CrmFollowUpCadenceMapper
{
    List<FollowUpCadenceVo> selectList(@Param("tenantId") String tenantId);

    FollowUpCadenceVo selectById(@Param("tenantId") String tenantId, @Param("cadenceId") String cadenceId);

    CrmFollowUpCadence selectEntityById(@Param("tenantId") String tenantId, @Param("cadenceId") String cadenceId);

    int insertCrmFollowUpCadence(CrmFollowUpCadence row);

    int updateCrmFollowUpCadence(CrmFollowUpCadence row);

    int logicalDeleteById(@Param("cadenceId") String cadenceId,
            @Param("tenantId") String tenantId,
            @Param("updateBy") String updateBy);
}
