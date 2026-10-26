package com.ruoyi.crm.mapper;

import org.apache.ibatis.annotations.Param;

import com.ruoyi.crm.domain.CrmWebchatConfig;

public interface CrmWebchatConfigMapper
{
    CrmWebchatConfig selectByTenantId(@Param("tenantId") String tenantId);

    CrmWebchatConfig selectByWebsiteToken(@Param("websiteToken") String websiteToken);

    int insertOrUpdate(CrmWebchatConfig row);

    int updateWebsiteToken(@Param("tenantId") String tenantId,
                           @Param("websiteToken") String websiteToken,
                           @Param("updateBy") String updateBy);
}
