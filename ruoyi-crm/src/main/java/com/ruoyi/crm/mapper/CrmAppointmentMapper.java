package com.ruoyi.crm.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.crm.domain.CrmAppointment;
import com.ruoyi.crm.domain.CrmAttendee;
import com.ruoyi.crm.domain.CrmContact;

/**
 * 预约Mapper接口
 * 
 * @author luohd
 * @date 2024-02-16
 */
public interface CrmAppointmentMapper 
{
    /**
     * 查询预约
     * 
     * @param appointmentId 预约主键
     * @return 预约
     */
    public CrmAppointment selectCrmAppointmentByAppointmentId(String appointmentId);

    /**
     * 查询预约列表
     * 
     * @param crmAppointment 预约
     * @return 预约集合
     */
    public List<CrmAppointment> selectCrmAppointmentList(CrmAppointment crmAppointment);

    /**
     * 查询本月过期的预约列表
     * @param crmAppointment
     * @return
     */
    public List<CrmAppointment> selectCrmAppointmentListMonthly(CrmAppointment crmAppointment);

    /**
     * 新增预约
     * 
     * @param crmAppointment 预约
     * @return 结果
     */
    public int insertCrmAppointment(CrmAppointment crmAppointment);

    /**
     * 修改预约
     * 
     * @param crmAppointment 预约
     * @return 结果
     */
    public int updateCrmAppointment(CrmAppointment crmAppointment);

    /**
     * 删除预约
     * 
     * @param appointmentId 预约主键
     * @return 结果
     */
    public int deleteCrmAppointmentByAppointmentId(String appointmentId);

    /**
     * 批量删除预约
     * 
     * @param appointmentIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteCrmAppointmentByAppointmentIds(String[] appointmentIds);

    /**
     * 批量删除参与者
     * 
     * @param appointmentIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteCrmAttendeeByAppointmentIds(String[] appointmentIds);
    
    /**
     * 批量新增参与者
     * 
     * @param crmAttendeeList 参与者列表
     * @return 结果
     */
    public int batchCrmAttendee(List<CrmAttendee> crmAttendeeList);
    

    /**
     * 通过预约主键删除参与者信息
     * 
     * @param appointmentId 预约ID
     * @return 结果
     */
    public int deleteCrmAttendeeByAppointmentId(String appointmentId);

    /**
     * 查询最近指定天数内没有完成预约的联系人。
     * 用于"距上次就诊超过N天"等场景。
     */
    List<CrmContact> selectContactsWithNoRecentCompletedAppointment(@Param("daysThreshold") Integer daysThreshold);

    /**
     * WhatsApp 自动化：预约开始时间落在 [windowStart, windowEnd) 且关联了 relate_id 的预约。
     */
    List<CrmAppointment> selectForReminderWindow(@Param("windowStart") java.util.Date windowStart,
                                                 @Param("windowEnd") java.util.Date windowEnd);
}
