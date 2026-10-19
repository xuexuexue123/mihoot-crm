package com.ruoyi.crm.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.crm.mapper.CrmItineraryMapper;
import com.ruoyi.crm.domain.CrmItinerary;
import com.ruoyi.crm.service.ICrmItineraryService;

/**
 * 行程Service业务层处理
 * 
 * @author luohd
 * @date 2024-03-13
 */
@Service
public class CrmItineraryServiceImpl implements ICrmItineraryService 
{
    @Autowired
    private CrmItineraryMapper crmItineraryMapper;

    /**
     * 查询行程
     * 
     * @param itinId 行程主键
     * @return 行程
     */
    @Override
    public CrmItinerary selectCrmItineraryByItinId(String itinId)
    {
        return crmItineraryMapper.selectCrmItineraryByItinId(itinId);
    }

    /**
     * 查询行程列表
     * 
     * @param crmItinerary 行程
     * @return 行程
     */
    @Override
    public List<CrmItinerary> selectCrmItineraryList(CrmItinerary crmItinerary)
    {
        return crmItineraryMapper.selectCrmItineraryList(crmItinerary);
    }

    /**
     * 新增行程
     * 
     * @param crmItinerary 行程
     * @return 结果
     */
    @Override
    public int insertCrmItinerary(CrmItinerary crmItinerary)
    {
        crmItinerary.setCreateTime(DateUtils.getNowDate());
        return crmItineraryMapper.insertCrmItinerary(crmItinerary);
    }

    /**
     * 修改行程
     * 
     * @param crmItinerary 行程
     * @return 结果
     */
    @Override
    public int updateCrmItinerary(CrmItinerary crmItinerary)
    {
        crmItinerary.setUpdateTime(DateUtils.getNowDate());
        return crmItineraryMapper.updateCrmItinerary(crmItinerary);
    }

    /**
     * 批量删除行程
     * 
     * @param itinIds 需要删除的行程主键
     * @return 结果
     */
    @Override
    public int deleteCrmItineraryByItinIds(String[] itinIds)
    {
        return crmItineraryMapper.deleteCrmItineraryByItinIds(itinIds);
    }

    /**
     * 删除行程信息
     * 
     * @param itinId 行程主键
     * @return 结果
     */
    @Override
    public int deleteCrmItineraryByItinId(String itinId)
    {
        return crmItineraryMapper.deleteCrmItineraryByItinId(itinId);
    }
}
