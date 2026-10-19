package com.ruoyi.crm.service;

import java.util.List;
import com.ruoyi.crm.domain.CrmTimeperiod;

/**
 * 销售周期Service接口
 * 
 * @author luohd
 * @date 2024-02-27
 */
public interface ICrmTimeperiodService 
{
    /**
     * 查询销售周期
     * 
     * @param periodId 销售周期主键
     * @return 销售周期
     */
    public CrmTimeperiod selectCrmTimeperiodByPeriodId(String periodId);

    /**
     * 查询销售周期列表
     * 
     * @param crmTimeperiod 销售周期
     * @return 销售周期集合
     */
    public List<CrmTimeperiod> selectCrmTimeperiodList(CrmTimeperiod crmTimeperiod);

    /**
     * 新增销售周期
     * 
     * @param crmTimeperiod 销售周期
     * @return 结果
     */
    public int insertCrmTimeperiod(CrmTimeperiod crmTimeperiod);

    /**
     * 修改销售周期
     * 
     * @param crmTimeperiod 销售周期
     * @return 结果
     */
    public int updateCrmTimeperiod(CrmTimeperiod crmTimeperiod);

    /**
     * 更新当前年度下的所有销售周期
     * @param crmTimeperiod
     * @return
     */
    public int updateCrmTimeperiodToNotActive(CrmTimeperiod crmTimeperiod);

    /**
     * 批量删除销售周期
     * 
     * @param periodIds 需要删除的销售周期主键集合
     * @return 结果
     */
    public int deleteCrmTimeperiodByPeriodIds(String[] periodIds);

    /**
     * 删除销售周期信息
     * 
     * @param periodId 销售周期主键
     * @return 结果
     */
    public int deleteCrmTimeperiodByPeriodId(String periodId);
}
