package com.ruoyi.crm.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.crm.mapper.CrmForecastMapper;
import com.ruoyi.crm.domain.CrmForecast;
import com.ruoyi.crm.service.ICrmForecastService;

/**
 * 销售预测Service业务层处理
 * 
 * @author luohd
 * @date 2024-01-18
 */
@Service
public class CrmForecastServiceImpl implements ICrmForecastService 
{
    @Autowired
    private CrmForecastMapper crmForecastMapper;

    /**
     * 查询销售预测
     * 
     * @param forecastId 销售预测主键
     * @return 销售预测
     */
    @Override
    public CrmForecast selectCrmForecastByForecastId(String forecastId)
    {
        return crmForecastMapper.selectCrmForecastByForecastId(forecastId);
    }

    /**
     * 查询销售预测列表
     * 
     * @param crmForecast 销售预测
     * @return 销售预测
     */
    @Override
    public List<CrmForecast> selectCrmForecastList(CrmForecast crmForecast)
    {
        return crmForecastMapper.selectCrmForecastList(crmForecast);
    }

    /**
     * 新增销售预测
     * 
     * @param crmForecast 销售预测
     * @return 结果
     */
    @Override
    public int insertCrmForecast(CrmForecast crmForecast)
    {
        crmForecast.setCreateTime(DateUtils.getNowDate());
        return crmForecastMapper.insertCrmForecast(crmForecast);
    }

    /**
     * 修改销售预测
     * 
     * @param crmForecast 销售预测
     * @return 结果
     */
    @Override
    public int updateCrmForecast(CrmForecast crmForecast)
    {
        crmForecast.setUpdateTime(DateUtils.getNowDate());
        return crmForecastMapper.updateCrmForecast(crmForecast);
    }

    /**
     * 批量删除销售预测
     * 
     * @param forecastIds 需要删除的销售预测主键
     * @return 结果
     */
    @Override
    public int deleteCrmForecastByForecastIds(String[] forecastIds)
    {
        return crmForecastMapper.deleteCrmForecastByForecastIds(forecastIds);
    }

    /**
     * 删除销售预测信息
     * 
     * @param forecastId 销售预测主键
     * @return 结果
     */
    @Override
    public int deleteCrmForecastByForecastId(String forecastId)
    {
        return crmForecastMapper.deleteCrmForecastByForecastId(forecastId);
    }
}
