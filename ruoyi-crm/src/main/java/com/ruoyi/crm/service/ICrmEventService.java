package com.ruoyi.crm.service;

import java.util.List;
import com.ruoyi.crm.domain.CrmEvent;

/**
 * 业务事件Service接口
 * 
 * @author luohd
 * @date 2025-03-15
 */
public interface ICrmEventService 
{
    /**
     * 查询业务事件
     * 
     * @param eventId 业务事件主键
     * @return 业务事件
     */
    public CrmEvent selectCrmEventByEventId(String eventId);

    /**
     * 查询业务事件列表
     * 
     * @param crmEvent 业务事件
     * @return 业务事件集合
     */
    public List<CrmEvent> selectCrmEventList(CrmEvent crmEvent);

    /**
     * 新增业务事件
     * 
     * @param crmEvent 业务事件
     * @return 结果
     */
    public int insertCrmEvent(CrmEvent crmEvent);

    /**
     * 修改业务事件
     * 
     * @param crmEvent 业务事件
     * @return 结果
     */
    public int updateCrmEvent(CrmEvent crmEvent);

    /**
     * 批量删除业务事件
     * 
     * @param eventIds 需要删除的业务事件主键集合
     * @return 结果
     */
    public int deleteCrmEventByEventIds(String[] eventIds);

    /**
     * 删除业务事件信息
     * 
     * @param eventId 业务事件主键
     * @return 结果
     */
    public int deleteCrmEventByEventId(String eventId);
}
