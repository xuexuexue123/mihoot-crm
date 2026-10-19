package com.ruoyi.crm.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.crm.domain.CrmDataTableColumn;

public interface CrmDataTableColumnMapper
{
    List<CrmDataTableColumn> selectByDataTableId(@Param("dataTableId") String dataTableId);

    CrmDataTableColumn selectById(@Param("id") String id);

    CrmDataTableColumn selectByDataTableIdAndName(@Param("dataTableId") String dataTableId,
            @Param("name") String name);

    int insert(CrmDataTableColumn column);

    int batchInsert(List<CrmDataTableColumn> columns);

    int update(CrmDataTableColumn column);

    int deleteById(String id);

    int deleteNonSystemByDataTableId(@Param("dataTableId") String dataTableId);

    int deleteByDataTableId(@Param("dataTableId") String dataTableId);

    int countNonSystemByDataTableId(@Param("dataTableId") String dataTableId);
}
