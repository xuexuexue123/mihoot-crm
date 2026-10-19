package com.ruoyi.crm.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.crm.mapper.CrmAttendeeMapper;
import com.ruoyi.crm.domain.CrmAttendee;
import com.ruoyi.crm.service.ICrmAttendeeService;

/**
 * 参与者Service业务层处理
 * 
 * @author luohd
 * @date 2024-02-16
 */
@Service
public class CrmAttendeeServiceImpl implements ICrmAttendeeService 
{
    @Autowired
    private CrmAttendeeMapper crmAttendeeMapper;

    /**
     * 查询参与者
     * 
     * @param attendeeId 参与者主键
     * @return 参与者
     */
    @Override
    public CrmAttendee selectCrmAttendeeByAttendeeId(String attendeeId)
    {
        return crmAttendeeMapper.selectCrmAttendeeByAttendeeId(attendeeId);
    }

    /**
     * 查询参与者列表
     * 
     * @param crmAttendee 参与者
     * @return 参与者
     */
    @Override
    public List<CrmAttendee> selectCrmAttendeeList(CrmAttendee crmAttendee)
    {
        return crmAttendeeMapper.selectCrmAttendeeList(crmAttendee);
    }

    /**
     * 新增参与者
     * 
     * @param crmAttendee 参与者
     * @return 结果
     */
    @Override
    public int insertCrmAttendee(CrmAttendee crmAttendee)
    {
        crmAttendee.setCreateTime(DateUtils.getNowDate());
        return crmAttendeeMapper.insertCrmAttendee(crmAttendee);
    }

    /**
     * 修改参与者
     * 
     * @param crmAttendee 参与者
     * @return 结果
     */
    @Override
    public int updateCrmAttendee(CrmAttendee crmAttendee)
    {
        crmAttendee.setUpdateTime(DateUtils.getNowDate());
        return crmAttendeeMapper.updateCrmAttendee(crmAttendee);
    }

    /**
     * 批量删除参与者
     * 
     * @param attendeeIds 需要删除的参与者主键
     * @return 结果
     */
    @Override
    public int deleteCrmAttendeeByAttendeeIds(String[] attendeeIds)
    {
        return crmAttendeeMapper.deleteCrmAttendeeByAttendeeIds(attendeeIds);
    }

    /**
     * 删除参与者信息
     * 
     * @param attendeeId 参与者主键
     * @return 结果
     */
    @Override
    public int deleteCrmAttendeeByAttendeeId(String attendeeId)
    {
        return crmAttendeeMapper.deleteCrmAttendeeByAttendeeId(attendeeId);
    }
}
