package com.ruoyi.crm.service;

import java.util.List;
import com.ruoyi.crm.domain.CrmAttendee;

/**
 * 参与者Service接口
 * 
 * @author luohd
 * @date 2024-02-16
 */
public interface ICrmAttendeeService 
{
    /**
     * 查询参与者
     * 
     * @param attendeeId 参与者主键
     * @return 参与者
     */
    public CrmAttendee selectCrmAttendeeByAttendeeId(String attendeeId);

    /**
     * 查询参与者列表
     * 
     * @param crmAttendee 参与者
     * @return 参与者集合
     */
    public List<CrmAttendee> selectCrmAttendeeList(CrmAttendee crmAttendee);

    /**
     * 新增参与者
     * 
     * @param crmAttendee 参与者
     * @return 结果
     */
    public int insertCrmAttendee(CrmAttendee crmAttendee);

    /**
     * 修改参与者
     * 
     * @param crmAttendee 参与者
     * @return 结果
     */
    public int updateCrmAttendee(CrmAttendee crmAttendee);

    /**
     * 批量删除参与者
     * 
     * @param attendeeIds 需要删除的参与者主键集合
     * @return 结果
     */
    public int deleteCrmAttendeeByAttendeeIds(String[] attendeeIds);

    /**
     * 删除参与者信息
     * 
     * @param attendeeId 参与者主键
     * @return 结果
     */
    public int deleteCrmAttendeeByAttendeeId(String attendeeId);
}
