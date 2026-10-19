package com.ruoyi.crm.service;

import java.util.List;
import com.ruoyi.crm.domain.CrmSalesScript;

/**
 * 话术库 Service
 */
public interface ICrmSalesScriptService
{
    CrmSalesScript selectCrmSalesScriptByScriptId(String scriptId);

    List<CrmSalesScript> selectCrmSalesScriptList(CrmSalesScript query);

    List<String> selectDistinctCategories();

    int insertCrmSalesScript(CrmSalesScript row);

    int updateCrmSalesScript(CrmSalesScript row);

    int deleteCrmSalesScriptByScriptId(String scriptId);
}
