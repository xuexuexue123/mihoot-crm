package com.ruoyi.crm.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ruoyi.crm.domain.CrmProfessionalService;

/**
 * 专业服务机构 Mapper
 */
public interface CrmProfessionalServiceMapper
{
    CrmProfessionalService selectCrmProfessionalServiceById(String id);

    List<CrmProfessionalService> selectCrmProfessionalServiceList(CrmProfessionalService query);

    int insertCrmProfessionalService(CrmProfessionalService row);

    int updateCrmProfessionalService(CrmProfessionalService row);

    int logicalDeleteById(@Param("id") String id, @Param("updateBy") String updateBy);
}
