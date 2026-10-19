package com.ruoyi.crm.service.followup;

import java.time.Instant;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.domain.CrmAppointment;
import com.ruoyi.crm.domain.CrmBooking;
import com.ruoyi.crm.domain.CrmContact;
import com.ruoyi.crm.domain.CrmLead;
import com.ruoyi.crm.domain.CrmOpty;
import com.ruoyi.crm.domain.CrmPatientValue;
import com.ruoyi.crm.domain.CrmRecallSchedule;
import com.ruoyi.crm.domain.dto.FollowupRuleDispatchEvent;
import com.ruoyi.crm.mapper.CrmFollowupRuleEnrollmentMapper;
import com.ruoyi.crm.service.ICrmContactService;
import com.ruoyi.crm.service.support.TenantDataSourceSupport;

/**
 * 业务写操作提交后入队回访规则。
 */
@Component
public class FollowupRuleHookSupport
{
    private static final Logger log = LoggerFactory.getLogger(FollowupRuleHookSupport.class);

    @Autowired
    private FollowupRuleEngine engine;

    @Autowired
    private CrmFollowupRuleEnrollmentMapper enrollmentMapper;

    @Autowired
    private ICrmContactService contactService;

    public void onOpportunityWon(CrmOpty updated, CrmOpty previous)
    {
        if (updated == null)
        {
            return;
        }
        JSONObject payload = new JSONObject();
        payload.put("opportunity", com.ruoyi.crm.service.support.CrmDomainEventPayloads.optyJson(updated));
        if (StringUtils.isNotEmpty(updated.getContactId()))
        {
            payload.put("contact", contactJson(updated.getContactId(), null));
        }
        JSONObject previousWrap = new JSONObject();
        JSONObject prevOpty = new JSONObject();
        prevOpty.put("stage", previous == null ? null : previous.getStage());
        previousWrap.put("opportunity", prevOpty);
        payload.put("previous", previousWrap);
        payload.put("changedFields", java.util.Collections.singletonList("stage"));
        registerAfterCommit(buildEvent(FollowupRuleConstants.TRIGGER_OPPORTUNITY_WON,
                FollowupRuleConstants.SOURCE_RECORD_TRIGGER, updated.getOptyId(), payload));
    }

    public void onOpportunityLost(CrmOpty updated)
    {
        if (updated == null || StringUtils.isBlank(updated.getOptyId()))
        {
            return;
        }
        String tenant = TenantDataSourceSupport.currentTenantKey();
        if (StringUtils.isBlank(tenant))
        {
            return;
        }
        enrollmentMapper.cancelPendingBySubject(tenant, FollowupRuleConstants.SUBJECT_OPPORTUNITY,
                updated.getOptyId(), FollowupRuleConstants.TRIGGER_OPPORTUNITY_WON,
                FollowupRuleConstants.CANCEL_BY_EVENT);
    }

    public void onLeadAssigned(CrmLead lead, CrmLead previous)
    {
        if (lead == null || StringUtils.isBlank(lead.getLeadId()))
        {
            return;
        }
        boolean hadOwner = previous != null && (previous.getUserId() != null || StringUtils.isNotEmpty(previous.getOwner()));
        boolean newOwner = lead.getUserId() != null || StringUtils.isNotEmpty(lead.getOwner());
        if (!newOwner)
        {
            return;
        }
        String tenant = TenantDataSourceSupport.currentTenantKey();
        if (hadOwner && StringUtils.isNotBlank(tenant))
        {
            enrollmentMapper.cancelPendingBySubject(tenant, FollowupRuleConstants.SUBJECT_LEAD,
                    lead.getLeadId(), FollowupRuleConstants.TRIGGER_LEAD_ASSIGNED,
                    FollowupRuleConstants.CANCEL_BY_EVENT);
        }
        JSONObject payload = new JSONObject();
        payload.put("lead", com.ruoyi.crm.service.support.CrmDomainEventPayloads.leadJson(lead));
        JSONObject previousWrap = new JSONObject();
        JSONObject prevLead = new JSONObject();
        if (previous != null)
        {
            prevLead.put("owner", previous.getOwner());
            prevLead.put("userId", previous.getUserId());
        }
        previousWrap.put("lead", prevLead);
        payload.put("previous", previousWrap);
        payload.put("changedFields", java.util.Arrays.asList("owner", "userId"));
        registerAfterCommit(buildEvent(FollowupRuleConstants.TRIGGER_LEAD_ASSIGNED,
                FollowupRuleConstants.SOURCE_RECORD_TRIGGER, lead.getLeadId(), payload));
    }

    public void onLeadConvertedOrMerged(String leadId)
    {
        if (StringUtils.isBlank(leadId))
        {
            return;
        }
        String tenant = TenantDataSourceSupport.currentTenantKey();
        if (StringUtils.isBlank(tenant))
        {
            return;
        }
        enrollmentMapper.cancelPendingBySubject(tenant, FollowupRuleConstants.SUBJECT_LEAD, leadId,
                FollowupRuleConstants.TRIGGER_LEAD_ASSIGNED, FollowupRuleConstants.CANCEL_BY_EVENT);
    }

    public void onPatientRecallDue(CrmRecallSchedule schedule, CrmContact contact)
    {
        if (schedule == null || StringUtils.isBlank(schedule.getContactId()) || schedule.getNextDueAt() == null)
        {
            return;
        }
        JSONObject payload = new JSONObject();
        JSONObject contactJson = new JSONObject();
        contactJson.put("contactId", schedule.getContactId());
        contactJson.put("name", contact == null ? null : contact.getName());
        contactJson.put("owner", contact == null ? null : contact.getOwner());
        payload.put("contact", contactJson);
        JSONObject opty = new JSONObject();
        opty.put("optyId", schedule.getAnchorId());
        opty.put("treatmentItemCode", schedule.getTreatmentItemCode());
        payload.put("opportunity", opty);
        JSONObject recall = new JSONObject();
        recall.put("scheduleId", schedule.getScheduleId());
        recall.put("policyId", schedule.getPolicyId());
        recall.put("nextDueAt", toIso(schedule.getNextDueAt()));
        recall.put("anchorType", schedule.getAnchorType());
        recall.put("anchorId", schedule.getAnchorId());
        payload.put("recallSchedule", recall);
        registerAfterCommit(buildEvent(FollowupRuleConstants.TRIGGER_PATIENT_RECALL_DUE,
                FollowupRuleConstants.SOURCE_CRON_SCAN,
                "recall-sched-" + schedule.getScheduleId(),
                payload));
    }

    public void onPatientRecallDue(CrmPatientValue patient, CrmContact contact)
    {
        if (patient == null || StringUtils.isBlank(patient.getContactId()) || patient.getNextRecallDueAt() == null)
        {
            return;
        }
        JSONObject payload = new JSONObject();
        JSONObject contactJson = new JSONObject();
        contactJson.put("contactId", patient.getContactId());
        contactJson.put("name", contact == null ? null : contact.getName());
        contactJson.put("owner", contact == null ? null : contact.getOwner());
        payload.put("contact", contactJson);
        JSONObject pv = new JSONObject();
        pv.put("contactId", patient.getContactId());
        pv.put("nextRecallDueAt", toIso(patient.getNextRecallDueAt()));
        pv.put("tier", patient.getTier());
        pv.put("netClv", patient.getNetClv());
        payload.put("patientValue", pv);
        registerAfterCommit(buildEvent(FollowupRuleConstants.TRIGGER_PATIENT_RECALL_DUE,
                FollowupRuleConstants.SOURCE_CRON_SCAN,
                "recall-" + patient.getContactId() + "-" + toIso(patient.getNextRecallDueAt()),
                payload));
    }

    public void onRecallDateChanged(String contactId, Date newDueAt)
    {
        if (StringUtils.isBlank(contactId) || newDueAt == null)
        {
            return;
        }
        String tenant = TenantDataSourceSupport.currentTenantKey();
        if (StringUtils.isBlank(tenant))
        {
            return;
        }
        Instant next = FollowupRuleDelayCalculator.untilFieldNineAm(newDueAt.toInstant());
        enrollmentMapper.selectPendingBySubject(tenant, FollowupRuleConstants.SUBJECT_CONTACT, contactId,
                FollowupRuleConstants.TRIGGER_PATIENT_RECALL_DUE)
                .forEach(row -> enrollmentMapper.updateNextRunAt(row.getEnrollmentId(), Date.from(next)));
    }

    public void onAppointmentNoshow(CrmAppointment appointment, String sourceRef)
    {
        if (appointment == null)
        {
            return;
        }
        JSONObject payload = new JSONObject();
        JSONObject appt = new JSONObject();
        appt.put("appointmentId", appointment.getAppointmentId());
        appt.put("scheduledAt", toIso(appointment.getStartTime()));
        appt.put("status", appointment.getAppointmentStatus());
        payload.put("appointment", appt);
        String relatedType = FollowupRuleRelatedTypeMapper.toUiRelatedType(appointment.getRelateType());
        if ("业务线索".equals(relatedType))
        {
            JSONObject lead = new JSONObject();
            lead.put("leadId", appointment.getRelateId());
            lead.put("leadName", appointment.getRelateName());
            lead.put("owner", appointment.getOwner());
            payload.put("lead", lead);
            payload.put("contact", null);
        }
        else
        {
            JSONObject contact = new JSONObject();
            contact.put("contactId", appointment.getRelateId());
            contact.put("name", appointment.getRelateName());
            contact.put("owner", appointment.getOwner());
            payload.put("contact", contact);
            payload.put("lead", null);
        }
        registerAfterCommit(buildEvent(FollowupRuleConstants.TRIGGER_APPOINTMENT_NOSHOW,
                FollowupRuleConstants.SOURCE_EVENT_TRIGGER, sourceRef, payload));
    }

    public void onBookingNoshow(CrmBooking booking, String sourceRef)
    {
        if (booking == null || StringUtils.isBlank(booking.getBookingId()))
        {
            return;
        }
        JSONObject payload = new JSONObject();
        payload.put("appointment", com.ruoyi.crm.service.support.CrmDomainEventPayloads.bookingAsAppointmentJson(booking));
        boolean hasContact = StringUtils.isNotBlank(booking.getContactId());
        if (hasContact)
        {
            JSONObject contact = new JSONObject();
            contact.put("contactId", booking.getContactId());
            contact.put("name", booking.getBookingName());
            contact.put("owner", booking.getOwner());
            payload.put("contact", contact);
            if (StringUtils.isNotBlank(booking.getLeadId()))
            {
                JSONObject lead = new JSONObject();
                lead.put("leadId", booking.getLeadId());
                lead.put("name", booking.getBookingName());
                lead.put("owner", booking.getOwner());
                payload.put("lead", lead);
            }
            else
            {
                payload.put("lead", null);
            }
        }
        else
        {
            JSONObject lead = new JSONObject();
            lead.put("leadId", booking.getLeadId());
            lead.put("name", booking.getBookingName());
            lead.put("owner", booking.getOwner());
            payload.put("lead", lead);
            payload.put("contact", null);
        }
        String ref = StringUtils.isNotBlank(sourceRef) ? sourceRef : ("booking_noshow_" + booking.getBookingId());
        registerAfterCommit(buildEvent(FollowupRuleConstants.TRIGGER_APPOINTMENT_NOSHOW,
                FollowupRuleConstants.SOURCE_EVENT_TRIGGER, ref, payload));
    }

    private FollowupRuleDispatchEvent buildEvent(String triggerType, String source, String sourceRef, JSONObject payload)
    {
        FollowupRuleDispatchEvent event = new FollowupRuleDispatchEvent();
        event.setTenant(TenantDataSourceSupport.currentTenantKey());
        event.setTriggerType(triggerType);
        event.setOccurredAt(Instant.now());
        event.setSource(source);
        event.setSourceRef(sourceRef);
        event.setPayload(payload);
        return event;
    }

    public void registerAfterCommit(FollowupRuleDispatchEvent event)
    {
        if (event == null || StringUtils.isBlank(event.getTenant()))
        {
            return;
        }
        if (TransactionSynchronizationManager.isSynchronizationActive())
        {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization()
            {
                @Override
                public void afterCommit()
                {
                    dispatchSafe(event);
                }
            });
        }
        else
        {
            dispatchSafe(event);
        }
    }

    private void dispatchSafe(FollowupRuleDispatchEvent event)
    {
        try
        {
            TenantDataSourceSupport.bindTenantKey(event.getTenant());
            engine.dispatch(event);
        }
        catch (Exception ex)
        {
            log.warn("followup-rule dispatch failed trigger={}: {}", event.getTriggerType(), ex.getMessage());
        }
    }

    private JSONObject contactJson(String contactId, String name)
    {
        JSONObject json = new JSONObject();
        json.put("contactId", contactId);
        json.put("name", name);
        try
        {
            CrmContact contact = contactService.selectCrmContactByContactId(contactId);
            if (contact != null)
            {
                json.put("name", contact.getName());
                json.put("owner", contact.getOwner());
            }
        }
        catch (Exception ignored)
        {
        }
        return json;
    }

    private static String toIso(Date date)
    {
        return date == null ? null : date.toInstant().toString();
    }

    public static boolean isNoshowStatus(String status)
    {
        if (StringUtils.isBlank(status))
        {
            return false;
        }
        String normalized = status.trim().toLowerCase();
        return "noshow".equals(normalized) || "no_show".equals(normalized) || "no-show".equals(normalized)
                || "未到诊".equals(status.trim()) || "爽约".equals(status.trim()) || "未到".equals(status.trim());
    }

    public static boolean ownerChanged(CrmLead existing, CrmLead updated)
    {
        if (updated == null)
        {
            return false;
        }
        Long oldUser = existing == null ? null : existing.getUserId();
        Long newUser = updated.getUserId();
        String oldOwner = existing == null ? null : existing.getOwner();
        String newOwner = updated.getOwner();
        boolean userChanged = (oldUser == null) != (newUser == null)
                || (oldUser != null && !oldUser.equals(newUser));
        boolean ownerNameChanged = !java.util.Objects.equals(
                StringUtils.isEmpty(oldOwner) ? null : oldOwner,
                StringUtils.isEmpty(newOwner) ? null : newOwner);
        return userChanged || ownerNameChanged;
    }

    public static boolean isClosedLost(String stage, String closeReason)
    {
        if (StringUtils.isNotEmpty(closeReason))
        {
            String n = closeReason.trim().toLowerCase();
            if ("closed_lost".equals(n) || "lost".equals(n) || "lose".equals(n))
            {
                return true;
            }
        }
        if (StringUtils.isEmpty(stage))
        {
            return false;
        }
        String s = stage.trim().toLowerCase();
        return "closed_lost".equals(s) || s.contains("lost");
    }
}
