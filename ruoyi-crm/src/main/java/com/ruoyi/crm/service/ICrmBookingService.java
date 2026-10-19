package com.ruoyi.crm.service;

import java.util.List;
import com.ruoyi.crm.domain.CrmBooking;

/**
 * 诊所上门预约 Service
 */
public interface ICrmBookingService
{
    CrmBooking selectCrmBookingByBookingId(String bookingId);

    /** 详情（带 DataScope；入参须含 bookingId） */
    CrmBooking selectCrmBookingByBookingIdScoped(CrmBooking query);

    List<CrmBooking> selectCrmBookingList(CrmBooking crmBooking);

    List<CrmBooking> selectCrmBookingListByDay(CrmBooking crmBooking, String date);

    int insertCrmBooking(CrmBooking crmBooking);

    int updateCrmBooking(CrmBooking crmBooking);

    /** 软取消：status = cancelled */
    int cancelCrmBookingByBookingId(String bookingId);
}
