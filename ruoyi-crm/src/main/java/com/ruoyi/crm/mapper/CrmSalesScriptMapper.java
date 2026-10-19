package com.ruoyi.crm.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.crm.domain.CrmSalesScript;

/**
 * 话术库 Mapper
 */
public interface CrmSalesScriptMapper
{
    CrmSalesScript selectCrmSalesScriptByScriptId(String scriptId);

    List<CrmSalesScript> selectCrmSalesScriptList(CrmSalesScript query);

    List<String> selectDistinctCategories();

    int insertCrmSalesScript(CrmSalesScript row);

    int updateCrmSalesScript(CrmSalesScript row);

    int logicalDeleteByScriptId(@Param("scriptId") String scriptId, @Param("updateBy") String updateBy);
}
