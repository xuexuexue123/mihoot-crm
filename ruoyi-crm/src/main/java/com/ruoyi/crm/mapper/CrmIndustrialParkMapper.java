package com.ruoyi.crm.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ruoyi.crm.domain.CrmIndustrialPark;

/**
 * 工业园区 Mapper
 */
public interface CrmIndustrialParkMapper
{
    CrmIndustrialPark selectCrmIndustrialParkById(String id);

    List<CrmIndustrialPark> selectCrmIndustrialParkList(CrmIndustrialPark query);

    int insertCrmIndustrialPark(CrmIndustrialPark row);

    int updateCrmIndustrialPark(CrmIndustrialPark row);

    int logicalDeleteById(@Param("id") String id, @Param("updateBy") String updateBy);
}
