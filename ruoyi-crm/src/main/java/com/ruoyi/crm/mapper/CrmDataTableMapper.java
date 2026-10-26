package com.ruoyi.crm.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.crm.domain.CrmDataTable;
import com.ruoyi.crm.domain.vo.DataTableVO;

public interface CrmDataTableMapper
{
    CrmDataTable selectByIdAndTenant(@Param("id") String id, @Param("tenantId") String tenantId);

    List<DataTableVO> selectListByTenant(@Param("tenantId") String tenantId);

    int insert(CrmDataTable row);

    int update(CrmDataTable row);

    int deleteByIdAndTenant(@Param("id") String id, @Param("tenantId") String tenantId);

    int countByTenantAndName(@Param("tenantId") String tenantId, @Param("name") String name);

    int countByTenantAndNameExcludeId(@Param("tenantId") String tenantId, @Param("name") String name,
            @Param("excludeId") String excludeId);
}
