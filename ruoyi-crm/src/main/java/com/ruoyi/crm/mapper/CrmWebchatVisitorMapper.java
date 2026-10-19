package com.ruoyi.crm.mapper;

import org.apache.ibatis.annotations.Param;

import com.ruoyi.crm.domain.CrmWebchatVisitor;

public interface CrmWebchatVisitorMapper
{
    CrmWebchatVisitor selectByVisitorId(@Param("tenantId") String tenantId,
                                        @Param("visitorId") String visitorId);

    int upsertVisitor(CrmWebchatVisitor row);

    int updateProfile(CrmWebchatVisitor row);

    int updateSubjects(@Param("tenantId") String tenantId,
                       @Param("visitorId") String visitorId,
                       @Param("contactId") String contactId,
                       @Param("leadId") String leadId);

    int migrateLeadToContact(@Param("oldLeadId") String oldLeadId,
                             @Param("newContactId") String newContactId);
}
