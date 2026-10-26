package com.ruoyi.crm.mapper;

import java.util.List;
import com.ruoyi.crm.domain.CrmEventSpeaker;

/**
 * 活动演讲者Mapper接口
 * 
 * @author luohd
 * @date 2025-07-20
 */
public interface CrmEventSpeakerMapper
{
    /**
     * 查询活动下的演讲者列表
     * 
     * @param eventId 活动ID
     * @return 演讲者集合
     */
    public List<CrmEventSpeaker> selectCrmEventSpeakerByEventId(String eventId);

    /**
     * 按主键查询演讲者
     * 
     * @param id 演讲者主键
     * @return 演讲者
     */
    public CrmEventSpeaker selectCrmEventSpeakerById(Long id);

    /**
     * 新增演讲者
     * 
     * @param speaker 演讲者
     * @return 结果
     */
    public int insertCrmEventSpeaker(CrmEventSpeaker speaker);

    /**
     * 修改演讲者
     * 
     * @param speaker 演讲者
     * @return 结果
     */
    public int updateCrmEventSpeaker(CrmEventSpeaker speaker);

    /**
     * 批量新增演讲者
     * 
     * @param speakers 演讲者列表
     * @return 结果
     */
    public int batchInsertCrmEventSpeaker(List<CrmEventSpeaker> speakers);

    /**
     * 按主键删除演讲者
     * 
     * @param id 演讲者主键
     * @return 结果
     */
    public int deleteCrmEventSpeakerById(Long id);

    /**
     * 删除活动下所有演讲者
     * 
     * @param eventId 活动ID
     * @return 结果
     */
    public int deleteCrmEventSpeakerByEventId(String eventId);

    /**
     * 按活动ID列表批量删除演讲者
     * 
     * @param eventIds 活动ID数组
     * @return 结果
     */
    public int deleteCrmEventSpeakerByEventIds(String[] eventIds);
}
