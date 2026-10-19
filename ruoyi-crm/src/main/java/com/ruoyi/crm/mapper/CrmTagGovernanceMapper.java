package com.ruoyi.crm.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ruoyi.crm.domain.CrmTagChannelMapping;
import com.ruoyi.crm.domain.CrmTagDispatchTask;
import com.ruoyi.crm.domain.vo.CrmTagMemberVo;
import com.ruoyi.crm.domain.vo.CrmTagOrphanVo;
import com.ruoyi.crm.domain.vo.CrmTagUsageVo;

/**
 * CRM 标签治理（usage / orphans / merge / mapping / dispatch）
 */
public interface CrmTagGovernanceMapper
{
    List<CrmTagUsageVo> selectUsage(@Param("dictValue") String dictValue);

    CrmTagUsageVo selectUsageOne(@Param("dictValue") String dictValue);

    List<CrmTagOrphanVo> selectOrphans();

    long countContactMembers(@Param("dictValue") String dictValue);

    List<CrmTagMemberVo> selectContactMembers(@Param("dictValue") String dictValue,
            @Param("limit") int limit, @Param("offset") int offset);

    long countLeadMembers(@Param("dictValue") String dictValue);

    List<CrmTagMemberVo> selectLeadMembers(@Param("dictValue") String dictValue,
            @Param("limit") int limit, @Param("offset") int offset);

    List<String> selectContactIdsForMerge(@Param("fromId") String fromId,
            @Param("fromText") String fromText, @Param("limit") int limit);

    List<String> selectLeadIdsForMerge(@Param("fromId") String fromId,
            @Param("fromText") String fromText, @Param("limit") int limit);

    int mergeContactTagsBatch(@Param("contactIds") List<String> contactIds,
            @Param("fromId") String fromId, @Param("fromText") String fromText,
            @Param("toDictValue") String toDictValue, @Param("toLabel") String toLabel);

    int mergeLeadTagsBatch(@Param("leadIds") List<String> leadIds,
            @Param("fromId") String fromId, @Param("fromText") String fromText,
            @Param("toDictValue") String toDictValue, @Param("toLabel") String toLabel);

    List<CrmTagChannelMapping> selectAllMappings();

    CrmTagChannelMapping selectMapping(@Param("dictValue") String dictValue,
            @Param("channel") String channel);

    int upsertMapping(CrmTagChannelMapping row);

    int deleteMapping(@Param("dictValue") String dictValue, @Param("channel") String channel);

    int insertDispatchTask(CrmTagDispatchTask task);

    CrmTagDispatchTask selectDispatchTaskById(@Param("id") Long id);

    int updateDispatchTask(CrmTagDispatchTask task);
}
