package com.ruoyi.crm.mapper;

import java.util.List;
import com.ruoyi.crm.domain.CrmTravel;
import com.ruoyi.crm.domain.CrmItinerary;

/**
 * 差旅Mapper接口
 * 
 * @author luohd
 * @date 2024-03-13
 */
public interface CrmTravelMapper 
{
    /**
     * 查询差旅
     * 
     * @param travelId 差旅主键
     * @return 差旅
     */
    public CrmTravel selectCrmTravelByTravelId(String travelId);

    /**
     * 查询差旅列表
     * 
     * @param crmTravel 差旅
     * @return 差旅集合
     */
    public List<CrmTravel> selectCrmTravelList(CrmTravel crmTravel);

    /**
     * 导出
     * @param crmTravel
     * @return
     */
    public List<CrmTravel> selectCrmTravelListForExport(CrmTravel crmTravel);

    /**
     * 新增差旅
     * 
     * @param crmTravel 差旅
     * @return 结果
     */
    public int insertCrmTravel(CrmTravel crmTravel);

    /**
     * 修改差旅
     * 
     * @param crmTravel 差旅
     * @return 结果
     */
    public int updateCrmTravel(CrmTravel crmTravel);

    /**
     * 删除差旅
     * 
     * @param travelId 差旅主键
     * @return 结果
     */
    public int deleteCrmTravelByTravelId(String travelId);

    /**
     * 批量删除差旅
     * 
     * @param travelIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteCrmTravelByTravelIds(String[] travelIds);

    /**
     * 批量删除行程
     * 
     * @param travelIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteCrmItineraryByTravelIds(String[] travelIds);
    
    /**
     * 批量新增行程
     * 
     * @param crmItineraryList 行程列表
     * @return 结果
     */
    public int batchCrmItinerary(List<CrmItinerary> crmItineraryList);
    

    /**
     * 通过差旅主键删除行程信息
     * 
     * @param travelId 差旅ID
     * @return 结果
     */
    public int deleteCrmItineraryByTravelId(String travelId);
}
