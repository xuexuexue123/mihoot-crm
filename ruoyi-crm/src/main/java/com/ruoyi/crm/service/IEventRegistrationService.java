package com.ruoyi.crm.service;

import com.ruoyi.crm.domain.CrmEvent;
import com.ruoyi.crm.domain.CrmEventRegistration;
import com.ruoyi.crm.domain.dto.EventRegistrationCreateRequest;
import com.ruoyi.crm.domain.dto.EventRegistrationQuery;
import com.ruoyi.crm.domain.vo.EventRegistrationDetailVo;
import com.ruoyi.crm.domain.vo.EventRegistrationRowVo;
import com.ruoyi.crm.domain.vo.OutreachRegistrationSubmitResultVo;
import com.ruoyi.crm.domain.CrmOutreachlead;
import java.util.List;

public interface IEventRegistrationService
{
    String DEFAULT_WEBSITE_LEAD_SOURCE = "Website Event Registration";

    OutreachRegistrationSubmitResultVo registerFromH5(String tenantId, CrmOutreachlead submission,
            CrmEvent event, String sessionId, String tokenOpenId, String tokenAppId);

    /**
     * 官网活动报名：无 registrationToken / openId；registrationSource = website。
     */
    OutreachRegistrationSubmitResultVo registerFromWebsite(String tenantId, CrmOutreachlead submission,
            CrmEvent event, String sessionId);

    CrmEventRegistration registerWalkInForSelfCheckIn(String tenantId, CrmEvent event, String sessionId,
            CrmOutreachlead submission, String tokenOpenId, String tokenAppId);

    EventRegistrationRowVo createManualRegistration(String tenantId, String eventId,
            EventRegistrationCreateRequest request, String operator);

    void cancelRegistration(String tenantId, String eventId, String registrationId, String operator);

    List<EventRegistrationRowVo> listRegistrations(String tenantId, String eventId, EventRegistrationQuery query);

    EventRegistrationDetailVo getRegistrationDetail(String tenantId, String eventId, String registrationId);
}
