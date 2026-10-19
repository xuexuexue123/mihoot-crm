package com.ruoyi.crm.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ruoyi.crm.domain.CrmWhatsappTemplate;

public interface CrmWhatsappTemplateMapper
{
    List<CrmWhatsappTemplate> selectPage(
            @Param("tenantId") String tenantId,
            @Param("status") String status,
            @Param("category") String category,
            @Param("language") String language,
            @Param("keyword") String keyword
    );

    CrmWhatsappTemplate selectByTemplate(@Param("tenantId") String tenantId,
                                         @Param("templateName") String templateName,
                                         @Param("language") String language);

    int insertOrUpdate(CrmWhatsappTemplate row);

    int updateLastSyncTime(@Param("id") Long id, @Param("lastSyncTime") java.util.Date lastSyncTime);

    int updateStatusByNameLanguage(@Param("tenantId") String tenantId,
                                   @Param("templateName") String templateName,
                                   @Param("language") String language,
                                   @Param("status") String status,
                                   @Param("rejectionReason") String rejectionReason);
}
