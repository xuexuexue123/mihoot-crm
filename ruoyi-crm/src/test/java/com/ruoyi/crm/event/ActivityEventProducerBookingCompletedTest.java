package com.ruoyi.crm.event;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.crm.domain.CrmActivity;
import com.ruoyi.crm.domain.CrmBooking;
import com.ruoyi.crm.service.ICrmActivityService;
import com.ruoyi.crm.service.ICustomerMetricsService;
import com.ruoyi.crm.service.support.LeadFollowupAnchorSupport;

@ExtendWith(MockitoExtension.class)
public class ActivityEventProducerBookingCompletedTest
{
    @Mock
    private ICrmActivityService activityService;

    @Mock
    private ICustomerMetricsService customerMetricsService;

    @Mock
    private LeadFollowupAnchorSupport leadFollowupAnchorSupport;

    @InjectMocks
    private ActivityEventProducer producer;

    @Test
    public void produceBookingCompleted_writesAppointmentCompletedWithContactSubject()
    {
        when(activityService.insertTimelineActivity(any(CrmActivity.class))).thenReturn(1);

        CrmBooking booking = new CrmBooking();
        booking.setBookingId("bk-1");
        booking.setContactId("c-1");
        booking.setLeadId("l-1");
        booking.setBookingName("张三");
        booking.setStartTime(new Date());
        booking.setUserId(1001L);
        booking.setDeptId(200L);

        String ref = producer.produceBookingCompleted(booking);
        assertEquals("booking_completed_bk-1", ref);

        ArgumentCaptor<CrmActivity> captor = ArgumentCaptor.forClass(CrmActivity.class);
        verify(activityService).insertTimelineActivity(captor.capture());
        CrmActivity activity = captor.getValue();
        assertEquals("appointment.completed", activity.getEventType());
        assertEquals("appointment", activity.getEventCategory());
        assertEquals("预约完成", activity.getEventName());
        assertEquals("Contact", activity.getSubjectType());
        assertEquals("c-1", activity.getSubjectId());
        assertEquals(Long.valueOf(1001L), activity.getUserId());
        assertEquals("bk-1", activity.getPulseId());
        assertEquals("booking_completed_bk-1", activity.getChannelEventId());

        JSONObject payload = JSON.parseObject(activity.getPayload());
        assertEquals("bk-1", payload.getJSONObject("appointment").getString("appointmentId"));
        assertEquals("completed", payload.getJSONObject("appointment").getString("status"));
        assertEquals("c-1", payload.getJSONObject("contact").getString("contactId"));
    }

    @Test
    public void produceBookingCompleted_fallsBackToLeadSubject()
    {
        when(activityService.insertTimelineActivity(any(CrmActivity.class))).thenReturn(1);

        CrmBooking booking = new CrmBooking();
        booking.setBookingId("bk-2");
        booking.setLeadId("l-2");
        booking.setBookingName("李四");

        producer.produceBookingCompleted(booking);

        ArgumentCaptor<CrmActivity> captor = ArgumentCaptor.forClass(CrmActivity.class);
        verify(activityService).insertTimelineActivity(captor.capture());
        assertEquals("Lead", captor.getValue().getSubjectType());
        assertEquals("l-2", captor.getValue().getSubjectId());
    }

    @Test
    public void produceBookingCompleted_skipsNullBooking()
    {
        assertNull(producer.produceBookingCompleted(null));
        verify(activityService, never()).insertTimelineActivity(any());
    }
}
