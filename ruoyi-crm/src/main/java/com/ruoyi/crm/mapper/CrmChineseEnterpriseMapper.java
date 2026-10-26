package com.ruoyi.crm.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ruoyi.crm.domain.CrmChineseEnterprise;

/**
 * 中资企业 Mapper
 */
public interface CrmChineseEnterpriseMapper
{
    CrmChineseEnterprise selectCrmChineseEnterpriseById(String id);

    List<CrmChineseEnterprise> selectCrmChineseEnterpriseList(CrmChineseEnterprise query);

    int insertCrmChineseEnterprise(CrmChineseEnterprise row);

    int updateCrmChineseEnterprise(CrmChineseEnterprise row);

    int logicalDeleteById(@Param("id") String id, @Param("updateBy") String updateBy);
}
