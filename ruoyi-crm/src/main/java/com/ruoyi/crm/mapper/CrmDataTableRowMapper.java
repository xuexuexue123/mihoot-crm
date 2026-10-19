package com.ruoyi.crm.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.crm.domain.CrmDataTableRow;
import com.ruoyi.crm.domain.dto.datatable.DataTableRowQuery;

public interface CrmDataTableRowMapper
{
    List<CrmDataTableRow> selectByDataTableId(@Param("dataTableId") String dataTableId);

    List<CrmDataTableRow> selectPageByDataTableId(@Param("dataTableId") String dataTableId);

    CrmDataTableRow selectById(@Param("id") String id);

    CrmDataTableRow selectByIdAndDataTableId(@Param("id") String id, @Param("dataTableId") String dataTableId);

    long countByDataTableId(@Param("dataTableId") String dataTableId);

    int insert(CrmDataTableRow row);

    int update(CrmDataTableRow row);

    int deleteById(@Param("id") String id);

    int deleteByDataTableId(@Param("dataTableId") String dataTableId);

    List<CrmDataTableRow> selectByConditions(DataTableRowQuery query);
}
