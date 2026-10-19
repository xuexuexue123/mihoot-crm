package com.ruoyi.crm.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.crm.domain.CrmEventRegistration;
import com.ruoyi.crm.domain.dto.EventRegistrationQuery;
import com.ruoyi.crm.domain.vo.EventRegistrationDetailVo;
import com.ruoyi.crm.domain.vo.EventRegistrationRowVo;
import com.ruoyi.crm.domain.vo.SubjectRegistrationRowVo;
import com.ruoyi.crm.service.support.EventRegistrationBestRow;

public interface CrmEventRegistrationMapper
{
    CrmEventRegistration selectByRegistrationId(@Param("tenantId") String tenantId,
            @Param("registrationId") String registrationId);

    CrmEventRegistration selectActiveBySlot(@Param("tenantId") String tenantId, @Param("eventId") String eventId,
            @Param("sessionId") String sessionId, @Param("audienceKey") String audienceKey);

    List<SubjectRegistrationRowVo> selectByLeadId(@Param("tenantId") String tenantId,
            @Param("leadId") String leadId, @Param("registrationSource") String registrationSource);

    List<SubjectRegistrationRowVo> selectByContactId(@Param("tenantId") String tenantId,
            @Param("contactId") String contactId, @Param("registrationSource") String registrationSource);
    int countConfirmedBySession(@Param("tenantId") String tenantId, @Param("eventId") String eventId,
            @Param("sessionId") String sessionId);

    int countWaitlistBySession(@Param("tenantId") String tenantId, @Param("eventId") String eventId,
            @Param("sessionId") String sessionId);

    int countDistinctConfirmedByEvent(@Param("tenantId") String tenantId, @Param("eventId") String eventId,
            @Param("sessionId") String sessionId);

    int countWaitlistByEvent(@Param("tenantId") String tenantId, @Param("eventId") String eventId,
            @Param("sessionId") String sessionId);

    int countConfirmedRegistrationsBySession(@Param("tenantId") String tenantId, @Param("sessionId") String sessionId);

    int insert(CrmEventRegistration registration);

    int update(CrmEventRegistration registration);

    List<EventRegistrationRowVo> selectRegistrationRows(@Param("tenantId") String tenantId,
            @Param("eventId") String eventId, @Param("query") EventRegistrationQuery query);

    EventRegistrationDetailVo selectRegistrationDetail(@Param("tenantId") String tenantId,
            @Param("eventId") String eventId, @Param("registrationId") String registrationId);

    int countConfirmedOrCheckedInBySession(@Param("tenantId") String tenantId, @Param("sessionId") String sessionId);

    List<EventRegistrationBestRow> selectBestRegistrationByEvent(
            @Param("tenantId") String tenantId, @Param("eventId") String eventId);

    int deleteByEventIds(@Param("eventIds") String[] eventIds);
}
