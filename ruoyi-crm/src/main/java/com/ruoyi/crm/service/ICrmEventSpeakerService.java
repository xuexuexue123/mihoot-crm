package com.ruoyi.crm.service;

import java.util.List;
import com.ruoyi.crm.domain.CrmEventSpeaker;

/**
 * 活动演讲者Service接口
 * 
 * @author luohd
 * @date 2025-07-20
 */
public interface ICrmEventSpeakerService
{
    /**
     * 查询活动下的演讲者列表
     * 
     * @param eventId 活动ID
     * @return 演讲者集合
     */
    public List<CrmEventSpeaker> selectSpeakerByEventId(String eventId);

    /**
     * 按主键查询演讲者
     * 
     * @param id 演讲者主键
     * @return 演讲者
     */
    public CrmEventSpeaker selectSpeakerById(Long id);

    /**
     * 新增单个演讲者
     * 
     * @param speaker 演讲者
     * @return 结果
     */
    public int insertSpeaker(CrmEventSpeaker speaker);

    /**
     * 修改演讲者
     * 
     * @param speaker 演讲者
     * @return 结果
     */
    public int updateSpeaker(CrmEventSpeaker speaker);

    /**
     * 按主键删除演讲者
     * 
     * @param id 演讲者主键
     * @return 结果
     */
    public int deleteSpeakerById(Long id);

    /**
     * 替换活动的演讲者列表（先删后插）
     * 
     * @param eventId  活动ID
     * @param speakers 演讲者列表
     */
    public void replaceSpeakers(String eventId, List<CrmEventSpeaker> speakers);

    /**
     * 按活动ID列表批量删除演讲者
     * 
     * @param eventIds 活动ID数组
     * @return 结果
     */
    public int deleteSpeakerByEventIds(String[] eventIds);
}
