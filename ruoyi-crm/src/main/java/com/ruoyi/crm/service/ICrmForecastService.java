package com.ruoyi.crm.service;

import java.util.List;
import com.ruoyi.crm.domain.CrmForecast;

/**
 * 销售预测Service接口
 * 
 * @author luohd
 * @date 2024-01-18
 */
public interface ICrmForecastService 
{
    /**
     * 查询销售预测
     * 
     * @param forecastId 销售预测主键
     * @return 销售预测
     */
    public CrmForecast selectCrmForecastByForecastId(String forecastId);

    /**
     * 查询销售预测列表
     * 
     * @param crmForecast 销售预测
     * @return 销售预测集合
     */
    public List<CrmForecast> selectCrmForecastList(CrmForecast crmForecast);

    /**
     * 新增销售预测
     * 
     * @param crmForecast 销售预测
     * @return 结果
     */
    public int insertCrmForecast(CrmForecast crmForecast);

    /**
     * 修改销售预测
     * 
     * @param crmForecast 销售预测
     * @return 结果
     */
    public int updateCrmForecast(CrmForecast crmForecast);

    /**
     * 批量删除销售预测
     * 
     * @param forecastIds 需要删除的销售预测主键集合
     * @return 结果
     */
    public int deleteCrmForecastByForecastIds(String[] forecastIds);

    /**
     * 删除销售预测信息
     * 
     * @param forecastId 销售预测主键
     * @return 结果
     */
    public int deleteCrmForecastByForecastId(String forecastId);
}
