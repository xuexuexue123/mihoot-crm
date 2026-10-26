package com.ruoyi.crm.service;

import java.util.List;
import com.ruoyi.crm.domain.dto.SegmentPreviewRequest;
import com.ruoyi.crm.domain.dto.SegmentSaveRequest;
import com.ruoyi.crm.domain.vo.SegmentFieldMetaVo;
import com.ruoyi.crm.domain.vo.SegmentMemberVo;
import com.ruoyi.crm.domain.vo.SegmentPreviewVo;
import com.ruoyi.crm.domain.vo.SegmentVo;

public interface ICrmSegmentService
{
    List<SegmentVo> selectList(String tenantId);

    SegmentVo getById(String tenantId, Long id);

    void add(String tenantId, SegmentSaveRequest request);

    void update(String tenantId, SegmentSaveRequest request);

    void remove(String tenantId, Long id);

    SegmentPreviewVo preview(String tenantId, SegmentPreviewRequest request);

    long countMembers(String tenantId, Long id);

    List<SegmentMemberVo> selectMembers(String tenantId, Long id, int pageNum, int pageSize);

    List<SegmentMemberVo> previewMembers(String tenantId, Object ruleJson, int pageNum, int pageSize);

    long countByRuleJson(String tenantId, Object ruleJson);

    List<SegmentFieldMetaVo> listFields();
}
