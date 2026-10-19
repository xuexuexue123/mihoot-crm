package com.ruoyi.crm.service;

import java.util.List;
import com.ruoyi.crm.domain.CrmTravel;

/**
 * 差旅Service接口
 * 
 * @author luohd
 * @date 2024-03-13
 */
public interface ICrmTravelService 
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
     * 批量删除差旅
     * 
     * @param travelIds 需要删除的差旅主键集合
     * @return 结果
     */
    public int deleteCrmTravelByTravelIds(String[] travelIds);

    /**
     * 删除差旅信息
     * 
     * @param travelId 差旅主键
     * @return 结果
     */
    public int deleteCrmTravelByTravelId(String travelId);
}
