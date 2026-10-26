package com.ruoyi.crm.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ruoyi.crm.domain.CrmSanctionSourceTag;

/**
 * 制裁源标签 Mapper
 */
public interface CrmSanctionSourceTagMapper
{
    CrmSanctionSourceTag selectCrmSanctionSourceTagById(String id);

    CrmSanctionSourceTag selectCrmSanctionSourceTagByTagCode(@Param("tagCode") String tagCode);

    List<CrmSanctionSourceTag> selectCrmSanctionSourceTagList(CrmSanctionSourceTag query);

    int insertCrmSanctionSourceTag(CrmSanctionSourceTag row);

    int updateCrmSanctionSourceTag(CrmSanctionSourceTag row);

    int logicalDeleteById(@Param("id") String id, @Param("updateBy") String updateBy);
}
