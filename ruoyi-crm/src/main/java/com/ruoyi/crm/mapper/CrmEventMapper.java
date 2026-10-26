package com.ruoyi.crm.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.crm.domain.CrmEvent;

/**
 * 业务事件Mapper接口
 * 
 * @author luohd
 * @date 2025-03-15
 */
public interface CrmEventMapper 
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
     * 删除业务事件
     * 
     * @param eventId 业务事件主键
     * @return 结果
     */
    public int deleteCrmEventByEventId(String eventId);

    /**
     * 批量删除业务事件
     * 
     * @param eventIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteCrmEventByEventIds(String[] eventIds);

    /**
     * 按去连字符后的 event_id 后缀匹配（末 N 位 hex 等），最多返回 2 条用于碰撞检测。
     */
    public List<String> selectEventIdsByCompactSuffix(String suffix);

    /**
     * 官网公开：Execution 阶段活动，按 start_time 升序（空值靠后）。
     */
    public List<CrmEvent> selectPublicExecutionEvents(@Param("limit") int limit);
}
