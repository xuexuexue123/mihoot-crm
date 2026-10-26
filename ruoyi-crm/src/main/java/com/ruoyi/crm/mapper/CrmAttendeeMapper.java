package com.ruoyi.crm.mapper;

import java.util.List;
import com.ruoyi.crm.domain.CrmAttendee;

/**
 * 参与者Mapper接口
 * 
 * @author luohd
 * @date 2024-02-16
 */
public interface CrmAttendeeMapper 
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
     * 删除参与者
     * 
     * @param attendeeId 参与者主键
     * @return 结果
     */
    public int deleteCrmAttendeeByAttendeeId(String attendeeId);

    /**
     * 批量删除参与者
     * 
     * @param attendeeIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteCrmAttendeeByAttendeeIds(String[] attendeeIds);
}
