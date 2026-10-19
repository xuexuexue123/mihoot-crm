package com.ruoyi.crm.service.webhook;

import java.time.Instant;
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
import com.ruoyi.crm.domain.CrmRecallSchedule;
import com.ruoyi.crm.domain.dto.OutboundWebhookDispatchEvent;
import com.ruoyi.crm.service.ICrmContactService;
import com.ruoyi.crm.service.followup.FollowupRuleRelatedTypeMapper;
import com.ruoyi.crm.service.support.CrmDomainEventPayloads;
import com.ruoyi.crm.service.support.TenantDataSourceSupport;

@Component
public class OutboundWebhookHookSupport
{
    private static final Logger log = LoggerFactory.getLogger(OutboundWebhookHookSupport.class);

    @Autowired
    private OutboundWebhookDispatcher dispatcher;

    @Autowired
    private ICrmContactService contactService;

    public void onOpportunityWon(CrmOpty updated, CrmOpty previous)
    {
        if (updated == null)
        {
            return;
        }
        JSONObject payload = new JSONObject();
        payload.put("opportunity", CrmDomainEventPayloads.optyJson(updated));
        if (StringUtils.isNotEmpty(updated.getContactId()))
        {
            payload.put("contact", loadContact(updated.getContactId()));
        }
        JSONObject previousWrap = new JSONObject();
        JSONObject prevOpty = new JSONObject();
        prevOpty.put("stage", previous == null ? null : previous.getStage());
        previousWrap.put("opportunity", prevOpty);
        payload.put("previous", previousWrap);
        payload.put("changedFields", java.util.Collections.singletonList("stage"));
        registerAfterCommit(build(OutboundWebhookConstants.EVENT_OPPORTUNITY_WON, updated.getOptyId(), payload));
    }

    public void onLeadAssigned(CrmLead lead, CrmLead previous)
    {
        if (lead == null || StringUtils.isBlank(lead.getLeadId()))
        {
            return;
        }
        boolean newOwner = lead.getUserId() != null || StringUtils.isNotEmpty(lead.getOwner());
        if (!newOwner)
        {
            return;
        }
        JSONObject payload = new JSONObject();
        payload.put("lead", CrmDomainEventPayloads.leadJson(lead));
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
        registerAfterCommit(build(OutboundWebhookConstants.EVENT_LEAD_ASSIGNED, lead.getLeadId(), payload));
    }

    public void onLeadCreated(CrmLead lead)
    {
        if (lead == null || StringUtils.isBlank(lead.getLeadId()))
        {
            return;
        }
        JSONObject payload = new JSONObject();
        payload.put("lead", CrmDomainEventPayloads.leadJson(lead));
        registerAfterCommit(build(OutboundWebhookConstants.EVENT_LEAD_CREATED, lead.getLeadId(), payload));
    }

    public void onContactCreated(CrmContact contact)
    {
        if (contact == null || StringUtils.isBlank(contact.getContactId()))
        {
            return;
        }
        JSONObject payload = new JSONObject();
        payload.put("contact", CrmDomainEventPayloads.contactJson(contact));
        registerAfterCommit(build(OutboundWebhookConstants.EVENT_CONTACT_CREATED, contact.getContactId(), payload));
    }

    public void onPatientRecallDue(CrmRecallSchedule schedule, CrmContact contact)
    {
        if (schedule == null || StringUtils.isBlank(schedule.getContactId()) || schedule.getNextDueAt() == null)
        {
            return;
        }
        JSONObject payload = new JSONObject();
        JSONObject contactJson = CrmDomainEventPayloads.contactJson(contact);
        contactJson.put("contactId", schedule.getContactId());
        payload.put("contact", contactJson);
        JSONObject opty = new JSONObject();
        opty.put("optyId", schedule.getAnchorId());
        opty.put("treatmentItemCode", schedule.getTreatmentItemCode());
        payload.put("opportunity", opty);
        payload.put("recallSchedule", CrmDomainEventPayloads.recallScheduleJson(schedule));
        registerAfterCommit(build(OutboundWebhookConstants.EVENT_PATIENT_RECALL_DUE,
                "recall-sched-" + schedule.getScheduleId(), payload));
    }

    public void onAppointmentNoshow(CrmAppointment appointment, String sourceRef)
    {
        if (appointment == null)
        {
            return;
        }
        JSONObject payload = new JSONObject();
        payload.put("appointment", CrmDomainEventPayloads.appointmentJson(appointment));
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
            payload.put("contact", CrmDomainEventPayloads.contactStub(appointment.getRelateId(),
                    appointment.getRelateName(), appointment.getOwner()));
            payload.put("lead", null);
        }
        String ref = StringUtils.isNotBlank(sourceRef) ? sourceRef : appointment.getAppointmentId();
        registerAfterCommit(build(OutboundWebhookConstants.EVENT_APPOINTMENT_NOSHOW, ref, payload));
    }

    public void onBookingNoshow(CrmBooking booking, String sourceRef)
    {
        if (booking == null || StringUtils.isBlank(booking.getBookingId()))
        {
            return;
        }
        JSONObject payload = new JSONObject();
        payload.put("appointment", CrmDomainEventPayloads.bookingAsAppointmentJson(booking));
        boolean hasContact = StringUtils.isNotBlank(booking.getContactId());
        if (hasContact)
        {
            payload.put("contact", CrmDomainEventPayloads.contactStub(booking.getContactId(),
                    booking.getBookingName(), booking.getOwner()));
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
        String ref = StringUtils.isNotBlank(sourceRef) ? sourceRef : booking.getBookingId();
        registerAfterCommit(build(OutboundWebhookConstants.EVENT_APPOINTMENT_NOSHOW, ref, payload));
    }

    public void registerAfterCommit(OutboundWebhookDispatchEvent event)
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

    private void dispatchSafe(OutboundWebhookDispatchEvent event)
    {
        try
        {
            TenantDataSourceSupport.bindTenantKey(event.getTenant());
            dispatcher.dispatch(event);
        }
        catch (Exception ex)
        {
            log.warn("outbound webhook dispatch failed eventType={}: {}", event.getEventType(), ex.getMessage());
        }
    }

    private OutboundWebhookDispatchEvent build(String eventType, String sourceRef, JSONObject payload)
    {
        OutboundWebhookDispatchEvent event = new OutboundWebhookDispatchEvent();
        event.setTenant(TenantDataSourceSupport.currentTenantKey());
        event.setEventType(eventType);
        event.setOccurredAt(Instant.now());
        event.setSourceRef(sourceRef);
        event.setPayload(payload);
        return event;
    }

    private JSONObject loadContact(String contactId)
    {
        try
        {
            CrmContact contact = contactService.selectCrmContactByContactId(contactId);
            if (contact != null)
            {
                return CrmDomainEventPayloads.contactJson(contact);
            }
        }
        catch (Exception ignored)
        {
        }
        return CrmDomainEventPayloads.contactStub(contactId, null, null);
    }
}
