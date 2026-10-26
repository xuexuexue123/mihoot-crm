package com.ruoyi.crm.mapper;

import java.util.List;
import com.ruoyi.crm.domain.CrmItinerary;

/**
 * 行程Mapper接口
 * 
 * @author luohd
 * @date 2024-03-13
 */
public interface CrmItineraryMapper 
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
     * 删除行程
     * 
     * @param itinId 行程主键
     * @return 结果
     */
    public int deleteCrmItineraryByItinId(String itinId);

    /**
     * 批量删除行程
     * 
     * @param itinIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteCrmItineraryByItinIds(String[] itinIds);
}
