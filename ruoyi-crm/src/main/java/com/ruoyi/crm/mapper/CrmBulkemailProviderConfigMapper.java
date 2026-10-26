package com.ruoyi.crm.mapper;

import org.apache.ibatis.annotations.Param;

import com.ruoyi.crm.domain.CrmBulkemailProviderConfig;

public interface CrmBulkemailProviderConfigMapper
{
    CrmBulkemailProviderConfig selectFirst();

    int insert(CrmBulkemailProviderConfig row);

    int update(CrmBulkemailProviderConfig row);

    int updateTestOutcome(@Param("id") Long id,
            @Param("lastTestAt") java.util.Date lastTestAt,
            @Param("lastError") String lastError,
            @Param("updateBy") String updateBy,
            @Param("updateTime") java.util.Date updateTime);
}
