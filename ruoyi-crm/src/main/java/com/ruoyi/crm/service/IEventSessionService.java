package com.ruoyi.crm.service;

import java.util.List;
import com.ruoyi.crm.domain.dto.EventRegistrationSettingsSaveRequest;
import com.ruoyi.crm.domain.dto.EventSessionSaveRequest;
import com.ruoyi.crm.domain.vo.EventRegistrationSettingsVo;
import com.ruoyi.crm.domain.vo.EventSessionVo;

public interface IEventSessionService
{
    void ensureDefaultSession(String tenantId, String eventId, String operator);

    EventRegistrationSettingsVo getRegistrationSettings(String tenantId, String eventId);

    EventRegistrationSettingsVo updateRegistrationSettings(String tenantId, String eventId,
            EventRegistrationSettingsSaveRequest request, String operator);

    List<EventSessionVo> listSessions(String tenantId, String eventId, String status);

    EventSessionVo createSession(String tenantId, String eventId, EventSessionSaveRequest request, String operator);

    EventSessionVo updateSession(String tenantId, String eventId, String sessionId,
            EventSessionSaveRequest request, String operator);

    void deleteSession(String tenantId, String eventId, String sessionId, String operator);
}
