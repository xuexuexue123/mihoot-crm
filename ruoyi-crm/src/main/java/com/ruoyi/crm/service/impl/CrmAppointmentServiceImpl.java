package com.ruoyi.crm.service.impl;

import java.util.List;

import com.ruoyi.common.annotation.DataScope;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.crm.mapper.CrmAttendeeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import com.ruoyi.common.utils.StringUtils;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.crm.domain.CrmAttendee;
import com.ruoyi.crm.mapper.CrmAppointmentMapper;
import com.ruoyi.crm.domain.CrmAppointment;
import com.ruoyi.crm.service.ICrmAppointmentService;
import com.ruoyi.crm.rule.SmartPushEventExecutor;
import com.ruoyi.crm.event.ActivityEventProducer;
import com.ruoyi.crm.service.followup.FollowupRuleHookSupport;
import com.ruoyi.crm.service.webhook.OutboundWebhookHookSupport;
import com.ruoyi.framework.datasource.DynamicDataSourceContextHolder;

/**
 * 预约Service业务层处理
 * 
 * @author luohd
 * @date 2024-02-16
 */
@Service
public class CrmAppointmentServiceImpl implements ICrmAppointmentService 
{
    @Autowired
    private CrmAppointmentMapper crmAppointmentMapper;

    @Autowired
    private CrmAttendeeMapper crmAttendeeMapper;

    @Autowired
    private SmartPushEventExecutor smartPushEventExecutor;

    @Autowired
    private ActivityEventProducer activityEventProducer;

    @Autowired
    private FollowupRuleHookSupport followupRuleHookSupport;

    @Autowired
    private OutboundWebhookHookSupport outboundWebhookHookSupport;
    /**
     * 查询预约
     * 
     * @param appointmentId 预约主键
     * @return 预约
     */
    @Override
    public CrmAppointment selectCrmAppointmentByAppointmentId(String appointmentId)
    {
        return crmAppointmentMapper.selectCrmAppointmentByAppointmentId(appointmentId);
    }

    /**
     * 查询预约列表
     * 
     * @param crmAppointment 预约
     * @return 预约
     */
    @Override
    @DataScope(deptAlias = "d", userAlias = "u")
    public List<CrmAppointment> selectCrmAppointmentList(CrmAppointment crmAppointment)
    {
        List<CrmAppointment> crmAppointmentList = crmAppointmentMapper.selectCrmAppointmentList(crmAppointment);

        for (int i = 0; i < crmAppointmentList.size(); i++) {
            CrmAppointment item = crmAppointmentList.get(i);
            CrmAttendee crmAttendee = new CrmAttendee();
            crmAttendee.setAppointmentId(item.getAppointmentId());
            List<CrmAttendee> crmAttendeeList = crmAttendeeMapper.selectCrmAttendeeList(crmAttendee);
            item.setCrmAttendeeList(crmAttendeeList);
        }

        return crmAppointmentList;
    }

    /**
     * 查询本月预约列表
     * @param crmAppointment
     * @return
     */
    @Override
    @DataScope(deptAlias = "d", userAlias = "u")
    public List<CrmAppointment> selectCrmAppointmentListMonthly(CrmAppointment crmAppointment) {
        return crmAppointmentMapper.selectCrmAppointmentListMonthly(crmAppointment);
    }

    /**
     * 新增预约
     * 
     * @param crmAppointment 预约
     * @return 结果
     */
    @Transactional
    @Override
    public int insertCrmAppointment(CrmAppointment crmAppointment)
    {
        crmAppointment.setCreateTime(DateUtils.getNowDate());
        LoginUser loginUser = SecurityUtils.getLoginUser();
        crmAppointment.setDeptId(loginUser.getDeptId());
        crmAppointment.setUserId(loginUser.getUserId());
        crmAppointment.setOwner(loginUser.getUser().getNickName());
        crmAppointment.setOwnerId(loginUser.getUsername());

        int rows = crmAppointmentMapper.insertCrmAppointment(crmAppointment);
        insertCrmAttendee(crmAppointment);
        smartPushEventExecutor.onEvent("appointment_booked", crmAppointment.getRelateId(), DynamicDataSourceContextHolder.getDataSourceKey());
        return rows;
    }

    /**
     * 修改预约
     * 
     * @param crmAppointment 预约
     * @return 结果
     */
    @Transactional
    @Override
    public int updateCrmAppointment(CrmAppointment crmAppointment)
    {
        CrmAppointment existing = crmAppointmentMapper.selectCrmAppointmentByAppointmentId(
                crmAppointment.getAppointmentId());
        crmAppointment.setUpdateTime(DateUtils.getNowDate());
        crmAppointmentMapper.deleteCrmAttendeeByAppointmentId(crmAppointment.getAppointmentId());
        insertCrmAttendee(crmAppointment);
        int rows = crmAppointmentMapper.updateCrmAppointment(crmAppointment);
        if (rows > 0 && FollowupRuleHookSupport.isNoshowStatus(crmAppointment.getAppointmentStatus())
                && (existing == null || !FollowupRuleHookSupport.isNoshowStatus(existing.getAppointmentStatus())))
        {
            CrmAppointment updated = crmAppointmentMapper.selectCrmAppointmentByAppointmentId(
                    crmAppointment.getAppointmentId());
            if (updated == null)
            {
                updated = crmAppointment;
            }
            String sourceRef = activityEventProducer.produceAppointmentNoshow(updated);
            followupRuleHookSupport.onAppointmentNoshow(updated, sourceRef);
            outboundWebhookHookSupport.onAppointmentNoshow(updated, sourceRef);
        }
        return rows;
    }

    /**
     * 批量删除预约
     * 
     * @param appointmentIds 需要删除的预约主键
     * @return 结果
     */
    @Transactional
    @Override
    public int deleteCrmAppointmentByAppointmentIds(String[] appointmentIds)
    {
        crmAppointmentMapper.deleteCrmAttendeeByAppointmentIds(appointmentIds);
        return crmAppointmentMapper.deleteCrmAppointmentByAppointmentIds(appointmentIds);
    }

    /**
     * 删除预约信息
     * 
     * @param appointmentId 预约主键
     * @return 结果
     */
    @Transactional
    @Override
    public int deleteCrmAppointmentByAppointmentId(String appointmentId)
    {
        crmAppointmentMapper.deleteCrmAttendeeByAppointmentId(appointmentId);
        return crmAppointmentMapper.deleteCrmAppointmentByAppointmentId(appointmentId);
    }

    /**
     * 新增参与者信息
     * 
     * @param crmAppointment 预约对象
     */
    public void insertCrmAttendee(CrmAppointment crmAppointment)
    {
        List<CrmAttendee> crmAttendeeList = crmAppointment.getCrmAttendeeList();
        String appointmentId = crmAppointment.getAppointmentId();
        if (StringUtils.isNotNull(crmAttendeeList))
        {
            List<CrmAttendee> list = new ArrayList<CrmAttendee>();
            for (CrmAttendee crmAttendee : crmAttendeeList)
            {
                crmAttendee.setAppointmentId(appointmentId);
                list.add(crmAttendee);
            }
            if (list.size() > 0)
            {
                crmAppointmentMapper.batchCrmAttendee(list);
            }
        }
    }
}
