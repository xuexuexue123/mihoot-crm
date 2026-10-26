package com.ruoyi.crm.mapper;

import java.util.List;
import com.ruoyi.crm.domain.CrmBooking;

/**
 * 诊所上门预约 Mapper
 */
public interface CrmBookingMapper
{
    CrmBooking selectCrmBookingByBookingId(String bookingId);

    List<CrmBooking> selectCrmBookingList(CrmBooking crmBooking);

    List<CrmBooking> selectCrmBookingListByDay(CrmBooking crmBooking);

    int insertCrmBooking(CrmBooking crmBooking);

    int updateCrmBooking(CrmBooking crmBooking);

    int cancelCrmBookingByBookingId(CrmBooking crmBooking);
}
