package com.ruoyi.crm.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ruoyi.crm.domain.CrmSanctionedEntity;

/**
 * 合规制裁主体 Mapper
 */
public interface CrmSanctionedEntityMapper
{
    CrmSanctionedEntity selectCrmSanctionedEntityById(String id);

    List<CrmSanctionedEntity> selectCrmSanctionedEntityList(CrmSanctionedEntity query);

    int insertCrmSanctionedEntity(CrmSanctionedEntity row);

    int updateCrmSanctionedEntity(CrmSanctionedEntity row);

    int logicalDeleteById(@Param("id") String id, @Param("updateBy") String updateBy);
}
