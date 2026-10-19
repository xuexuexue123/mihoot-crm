package com.ruoyi.crm.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ruoyi.crm.domain.CrmWecomContactWay;

public interface CrmWecomContactWayMapper
{
    List<CrmWecomContactWay> selectList(CrmWecomContactWay query);

    int insert(CrmWecomContactWay row);

    CrmWecomContactWay selectByConfigId(String configId);

    CrmWecomContactWay selectByState(String state);

    CrmWecomContactWay selectById(Long id);

    int updatePartial(@Param("row") CrmWecomContactWay row, @Param("welcomeTextSet") boolean welcomeTextSet);

    int deleteById(Long id);
}
