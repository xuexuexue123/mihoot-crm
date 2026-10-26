package com.ruoyi.crm.service;

import java.util.List;

import com.ruoyi.crm.domain.dto.FollowUpCadenceSaveRequest;
import com.ruoyi.crm.domain.vo.FollowUpCadenceVo;

/**
 * 跟进序列配置 Service
 */
public interface ICrmFollowUpCadenceService
{
    List<FollowUpCadenceVo> selectList(String tenantId);

    FollowUpCadenceVo getById(String tenantId, String cadenceId);

    void add(String tenantId, FollowUpCadenceSaveRequest request);

    void update(String tenantId, FollowUpCadenceSaveRequest request);

    void remove(String tenantId, String cadenceId);
}
