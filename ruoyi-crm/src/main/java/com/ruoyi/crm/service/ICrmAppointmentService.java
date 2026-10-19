package com.ruoyi.crm.service;

import java.util.List;
import com.ruoyi.crm.domain.CrmAppointment;
import com.ruoyi.crm.domain.CrmTask;

/**
 * 预约Service接口
 * 
 * @author luohd
 * @date 2024-02-16
 */
public interface ICrmAppointmentService 
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
     * 批量删除预约
     * 
     * @param appointmentIds 需要删除的预约主键集合
     * @return 结果
     */
    public int deleteCrmAppointmentByAppointmentIds(String[] appointmentIds);

    /**
     * 删除预约信息
     * 
     * @param appointmentId 预约主键
     * @return 结果
     */
    public int deleteCrmAppointmentByAppointmentId(String appointmentId);
}
