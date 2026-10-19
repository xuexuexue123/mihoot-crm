package com.ruoyi.crm.service;

import java.util.List;
import com.ruoyi.crm.domain.CrmItinerary;

/**
 * 行程Service接口
 * 
 * @author luohd
 * @date 2024-03-13
 */
public interface ICrmItineraryService 
{
    /**
     * 查询行程
     * 
     * @param itinId 行程主键
     * @return 行程
     */
    public CrmItinerary selectCrmItineraryByItinId(String itinId);

    /**
     * 查询行程列表
     * 
     * @param crmItinerary 行程
     * @return 行程集合
     */
    public List<CrmItinerary> selectCrmItineraryList(CrmItinerary crmItinerary);

    /**
     * 新增行程
     * 
     * @param crmItinerary 行程
     * @return 结果
     */
    public int insertCrmItinerary(CrmItinerary crmItinerary);

    /**
     * 修改行程
     * 
     * @param crmItinerary 行程
     * @return 结果
     */
    public int updateCrmItinerary(CrmItinerary crmItinerary);

    /**
     * 批量删除行程
     * 
     * @param itinIds 需要删除的行程主键集合
     * @return 结果
     */
    public int deleteCrmItineraryByItinIds(String[] itinIds);

    /**
     * 删除行程信息
     * 
     * @param itinId 行程主键
     * @return 结果
     */
    public int deleteCrmItineraryByItinId(String itinId);
}
