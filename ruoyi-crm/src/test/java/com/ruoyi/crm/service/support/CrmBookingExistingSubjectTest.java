package com.ruoyi.crm.service.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.crm.domain.CrmBooking;
import com.ruoyi.crm.mapper.CrmBookingMapper;
import com.ruoyi.crm.service.impl.CrmBookingServiceImpl;

/**
 * 已有主体约诊验收要点（docs/crm-booking-existing-subject.md S1–S3）
 */
@ExtendWith(MockitoExtension.class)
class CrmBookingExistingSubjectTest
{
    @Mock
    private CrmBookingMapper crmBookingMapper;

    @InjectMocks
    private CrmBookingServiceImpl crmBookingService;

    @Test
    void insert_withOnlyLeadId_succeeds()
    {
        when(crmBookingMapper.insertCrmBooking(any(CrmBooking.class))).thenReturn(1);
        CrmBooking booking = baseBooking();
        booking.setLeadId("lead-1");
        booking.setContactId(null);

        assertEquals(1, crmBookingService.insertCrmBooking(booking));
        ArgumentCaptor<CrmBooking> captor = ArgumentCaptor.forClass(CrmBooking.class);
        verify(crmBookingMapper).insertCrmBooking(captor.capture());
        assertEquals("lead-1", captor.getValue().getLeadId());
        assertNull(captor.getValue().getContactId());
    }

    @Test
    void insert_withOnlyContactId_succeedsAndLeadIdNull()
    {
        when(crmBookingMapper.insertCrmBooking(any(CrmBooking.class))).thenReturn(1);
        CrmBooking booking = baseBooking();
        booking.setLeadId("");
        booking.setContactId("contact-1");
        booking.setSource("manual");

        assertEquals(1, crmBookingService.insertCrmBooking(booking));
        ArgumentCaptor<CrmBooking> captor = ArgumentCaptor.forClass(CrmBooking.class);
        verify(crmBookingMapper).insertCrmBooking(captor.capture());
        assertNull(captor.getValue().getLeadId());
        assertEquals("contact-1", captor.getValue().getContactId());
        assertEquals("manual", captor.getValue().getSource());
        verify(crmBookingMapper, never()).updateCrmBooking(any());
    }

    @Test
    void insert_withNeitherSubject_fails()
    {
        CrmBooking booking = baseBooking();
        booking.setLeadId(null);
        booking.setContactId("  ");
        assertThrows(ServiceException.class, () -> crmBookingService.insertCrmBooking(booking));
        verify(crmBookingMapper, never()).insertCrmBooking(any());
    }

    private static CrmBooking baseBooking()
    {
        CrmBooking booking = new CrmBooking();
        booking.setBookingName("张三");
        booking.setStartTime(new Date(System.currentTimeMillis()));
        booking.setEndTime(new Date(System.currentTimeMillis() + 30 * 60 * 1000L));
        return booking;
    }
}
