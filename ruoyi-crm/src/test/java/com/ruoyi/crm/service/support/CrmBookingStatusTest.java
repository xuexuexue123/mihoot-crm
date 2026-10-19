package com.ruoyi.crm.service.support;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import com.ruoyi.common.exception.ServiceException;

class CrmBookingStatusTest
{
    @Test
    void allowsScheduledToConfirmed()
    {
        assertDoesNotThrow(() -> CrmBookingStatus.assertTransition("scheduled", "confirmed"));
    }

    @Test
    void allowsScheduledToNoshowAndCancelled()
    {
        assertDoesNotThrow(() -> CrmBookingStatus.assertTransition("scheduled", "noshow"));
        assertDoesNotThrow(() -> CrmBookingStatus.assertTransition("scheduled", "cancelled"));
    }

    @Test
    void allowsToCompleted()
    {
        assertDoesNotThrow(() -> CrmBookingStatus.assertTransition("scheduled", "completed"));
        assertDoesNotThrow(() -> CrmBookingStatus.assertTransition("confirmed", "completed"));
        assertDoesNotThrow(() -> CrmBookingStatus.assertTransition("arrived", "completed"));
        assertDoesNotThrow(() -> CrmBookingStatus.assertTransition("completed", "completed"));
    }

    @Test
    void rejectsScheduledToArrived()
    {
        assertThrows(ServiceException.class,
                () -> CrmBookingStatus.assertTransition("scheduled", "arrived"));
    }

    @Test
    void allowsIdempotentSameStatus()
    {
        assertDoesNotThrow(() -> CrmBookingStatus.assertTransition("confirmed", "confirmed"));
    }

    @Test
    void rejectsTransitionFromTerminal()
    {
        assertThrows(ServiceException.class,
                () -> CrmBookingStatus.assertTransition("arrived", "noshow"));
        assertThrows(ServiceException.class,
                () -> CrmBookingStatus.assertTransition("cancelled", "scheduled"));
        assertThrows(ServiceException.class,
                () -> CrmBookingStatus.assertTransition("completed", "noshow"));
    }
}
