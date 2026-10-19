package com.ruoyi.crm.service.support;

import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.crm.domain.CrmAppointment;
import com.ruoyi.crm.domain.CrmBooking;
import com.ruoyi.crm.domain.CrmContact;
import com.ruoyi.crm.domain.CrmLead;
import com.ruoyi.crm.domain.CrmOpty;
import com.ruoyi.crm.domain.CrmRecallSchedule;

/**
 * Shared domain-event JSON snapshots for follow-up rules and outbound webhooks.
 */
public final class CrmDomainEventPayloads
{
    private CrmDomainEventPayloads()
    {
    }

    public static JSONObject optyJson(CrmOpty opty)
    {
        JSONObject json = new JSONObject();
        if (opty == null)
        {
            return json;
        }
        json.put("optyId", opty.getOptyId());
        json.put("name", opty.getName());
        json.put("stage", opty.getStage());
        json.put("owner", opty.getOwner());
        json.put("userId", opty.getUserId());
        json.put("contactId", opty.getContactId());
        json.put("closedWonBasis", opty.getClosedWonBasis());
        json.put("treatmentItemCode", opty.getTreatmentItemCode());
        return json;
    }

    public static JSONObject leadJson(CrmLead lead)
    {
        JSONObject json = new JSONObject();
        if (lead == null)
        {
            return json;
        }
        json.put("leadId", lead.getLeadId());
        json.put("leadName", lead.getLeadName());
        json.put("owner", lead.getOwner());
        json.put("userId", lead.getUserId());
        json.put("mobilePhone", lead.getMobilePhone());
        json.put("stage", lead.getStage());
        return json;
    }

    public static JSONObject contactJson(CrmContact contact)
    {
        JSONObject json = new JSONObject();
        if (contact == null)
        {
            return json;
        }
        json.put("contactId", contact.getContactId());
        json.put("name", contact.getName());
        json.put("mobile", contact.getMobilePhone());
        json.put("owner", contact.getOwner());
        return json;
    }

    public static JSONObject contactStub(String contactId, String name, String owner)
    {
        JSONObject json = new JSONObject();
        json.put("contactId", contactId);
        json.put("name", name);
        json.put("owner", owner);
        return json;
    }

    public static JSONObject appointmentJson(CrmAppointment appointment)
    {
        JSONObject json = new JSONObject();
        if (appointment == null)
        {
            return json;
        }
        json.put("appointmentId", appointment.getAppointmentId());
        json.put("scheduledAt", appointment.getStartTime() == null ? null : appointment.getStartTime().toInstant().toString());
        json.put("status", appointment.getAppointmentStatus());
        return json;
    }

    /** booking noshow payload：appointmentId 语义为 booking_id */
    public static JSONObject bookingAsAppointmentJson(CrmBooking booking)
    {
        JSONObject json = new JSONObject();
        if (booking == null)
        {
            return json;
        }
        json.put("appointmentId", booking.getBookingId());
        json.put("scheduledAt", booking.getStartTime() == null ? null : booking.getStartTime().toInstant().toString());
        json.put("status", booking.getStatus());
        return json;
    }

    public static JSONObject recallScheduleJson(CrmRecallSchedule schedule)
    {
        JSONObject json = new JSONObject();
        if (schedule == null)
        {
            return json;
        }
        json.put("scheduleId", schedule.getScheduleId());
        json.put("policyId", schedule.getPolicyId());
        json.put("nextDueAt", schedule.getNextDueAt() == null ? null : schedule.getNextDueAt().toInstant().toString());
        json.put("anchorType", schedule.getAnchorType());
        json.put("anchorId", schedule.getAnchorId());
        return json;
    }
}
