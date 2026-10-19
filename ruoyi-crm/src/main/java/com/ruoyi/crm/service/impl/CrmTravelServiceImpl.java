package com.ruoyi.crm.service.impl;

import java.util.List;

import com.alibaba.fastjson2.util.UUIDUtils;
import com.ruoyi.common.annotation.DataScope;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.UUID;

import com.ruoyi.common.utils.StringUtils;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.crm.domain.CrmItinerary;
import com.ruoyi.crm.mapper.CrmTravelMapper;
import com.ruoyi.crm.domain.CrmTravel;
import com.ruoyi.crm.service.ICrmTravelService;

/**
 * 差旅Service业务层处理
 * 
 * @author luohd
 * @date 2024-03-13
 */
@Service
public class CrmTravelServiceImpl implements ICrmTravelService 
{
    @Autowired
    private CrmTravelMapper crmTravelMapper;

    /**
     * 查询差旅
     * 
     * @param travelId 差旅主键
     * @return 差旅
     */
    @Override
    public CrmTravel selectCrmTravelByTravelId(String travelId)
    {
        return crmTravelMapper.selectCrmTravelByTravelId(travelId);
    }

    /**
     * 查询差旅列表
     * 
     * @param crmTravel 差旅
     * @return 差旅
     */
    @Override
    @DataScope(deptAlias = "d", userAlias = "u")
    public List<CrmTravel> selectCrmTravelList(CrmTravel crmTravel)
    {
        return crmTravelMapper.selectCrmTravelList(crmTravel);
    }

    /**
     * 导出
     * @param crmTravel
     * @return
     */
    @Override
    @DataScope(deptAlias = "d", userAlias = "u")
    public List<CrmTravel> selectCrmTravelListForExport(CrmTravel crmTravel) {

        return crmTravelMapper.selectCrmTravelListForExport(crmTravel);
    }
    /**
     * 新增差旅
     * 
     * @param crmTravel 差旅
     * @return 结果
     */
    @Transactional
    @Override
    public int insertCrmTravel(CrmTravel crmTravel)
    {
        crmTravel.setCreateTime(DateUtils.getNowDate());
        int rows = crmTravelMapper.insertCrmTravel(crmTravel);
        insertCrmItinerary(crmTravel);
        return rows;
    }

    /**
     * 修改差旅
     * 
     * @param crmTravel 差旅
     * @return 结果
     */
    @Transactional
    @Override
    public int updateCrmTravel(CrmTravel crmTravel)
    {
        crmTravel.setUpdateTime(DateUtils.getNowDate());
        crmTravelMapper.deleteCrmItineraryByTravelId(crmTravel.getTravelId());
        insertCrmItinerary(crmTravel);
        return crmTravelMapper.updateCrmTravel(crmTravel);
    }

    /**
     * 批量删除差旅
     * 
     * @param travelIds 需要删除的差旅主键
     * @return 结果
     */
    @Transactional
    @Override
    public int deleteCrmTravelByTravelIds(String[] travelIds)
    {
        crmTravelMapper.deleteCrmItineraryByTravelIds(travelIds);
        return crmTravelMapper.deleteCrmTravelByTravelIds(travelIds);
    }

    /**
     * 删除差旅信息
     * 
     * @param travelId 差旅主键
     * @return 结果
     */
    @Transactional
    @Override
    public int deleteCrmTravelByTravelId(String travelId)
    {
        crmTravelMapper.deleteCrmItineraryByTravelId(travelId);
        return crmTravelMapper.deleteCrmTravelByTravelId(travelId);
    }

    /**
     * 新增行程信息
     * 
     * @param crmTravel 差旅对象
     */
    public void insertCrmItinerary(CrmTravel crmTravel)
    {
        List<CrmItinerary> crmItineraryList = crmTravel.getCrmItineraryList();
        String travelId = crmTravel.getTravelId();
        if (StringUtils.isNotNull(crmItineraryList))
        {
            List<CrmItinerary> list = new ArrayList<CrmItinerary>();
            for (CrmItinerary crmItinerary : crmItineraryList)
            {
                crmItinerary.setItinId(UUID.randomUUID().toString());
                crmItinerary.setTravelId(travelId);
                list.add(crmItinerary);
            }
            if (list.size() > 0)
            {
                crmTravelMapper.batchCrmItinerary(list);
            }
        }
    }
}
