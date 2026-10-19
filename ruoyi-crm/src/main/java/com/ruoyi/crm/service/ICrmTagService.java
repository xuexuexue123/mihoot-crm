package com.ruoyi.crm.service;

import java.util.List;

import com.ruoyi.crm.domain.dto.CrmTagCreateRequest;
import com.ruoyi.crm.domain.dto.CrmTagDispatchRequest;
import com.ruoyi.crm.domain.dto.CrmTagMappingPutRequest;
import com.ruoyi.crm.domain.dto.CrmTagMergeRequest;
import com.ruoyi.crm.domain.dto.CrmTagUpdateRequest;
import com.ruoyi.crm.domain.vo.CrmTagDispatchTaskVo;
import com.ruoyi.crm.domain.vo.CrmTagMappingVo;
import com.ruoyi.crm.domain.vo.CrmTagMemberVo;
import com.ruoyi.crm.domain.vo.CrmTagMergeResultVo;
import com.ruoyi.crm.domain.vo.CrmTagOrphanVo;
import com.ruoyi.crm.domain.vo.CrmTagUsageVo;
import com.ruoyi.crm.domain.vo.CrmTagVo;
import com.ruoyi.crm.domain.vo.SegmentEnumValueVo;

/**
 * CRM 标签治理
 */
public interface ICrmTagService
{
    List<CrmTagVo> listAll();

    List<SegmentEnumValueVo> listEnabledEnumValues();

    CrmTagVo create(CrmTagCreateRequest request);

    CrmTagVo update(CrmTagUpdateRequest request);

    void remove(String dictValue);

    List<CrmTagUsageVo> usage(String dictValue);

    /**
     * 打了某标签的联系人 / 线索分页列表。
     * @param entity {@code contact} 或 {@code lead}
     */
    List<CrmTagMemberVo> listMembers(String dictValue, String entity, int pageNum, int pageSize);

    long countMembers(String dictValue, String entity);

    List<CrmTagOrphanVo> orphans();

    CrmTagMergeResultVo merge(CrmTagMergeRequest request);

    List<CrmTagMappingVo> listMappings();

    void putMapping(CrmTagMappingPutRequest request);

    CrmTagDispatchTaskVo dispatch(String tenantId, CrmTagDispatchRequest request);

    CrmTagDispatchTaskVo getDispatchTask(Long taskId);
}
