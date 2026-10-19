package com.ruoyi.crm.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ruoyi.crm.domain.CrmGovernmentAgency;

/**
 * 政府招商机构 Mapper
 */
public interface CrmGovernmentAgencyMapper
{
    CrmGovernmentAgency selectCrmGovernmentAgencyById(String id);

    List<CrmGovernmentAgency> selectCrmGovernmentAgencyList(CrmGovernmentAgency query);

    int insertCrmGovernmentAgency(CrmGovernmentAgency row);

    int updateCrmGovernmentAgency(CrmGovernmentAgency row);

    int logicalDeleteById(@Param("id") String id, @Param("updateBy") String updateBy);
}
