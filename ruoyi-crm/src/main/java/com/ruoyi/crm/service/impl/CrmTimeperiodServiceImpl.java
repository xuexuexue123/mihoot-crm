package com.ruoyi.crm.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.crm.mapper.CrmTimeperiodMapper;
import com.ruoyi.crm.domain.CrmTimeperiod;
import com.ruoyi.crm.service.ICrmTimeperiodService;

/**
 * 销售周期Service业务层处理
 * 
 * @author luohd
 * @date 2024-02-27
 */
@Service
public class CrmTimeperiodServiceImpl implements ICrmTimeperiodService 
{
    @Autowired
    private CrmTimeperiodMapper crmTimeperiodMapper;

    /**
     * 查询销售周期
     * 
     * @param periodId 销售周期主键
     * @return 销售周期
     */
    @Override
    public CrmTimeperiod selectCrmTimeperiodByPeriodId(String periodId)
    {
        return crmTimeperiodMapper.selectCrmTimeperiodByPeriodId(periodId);
    }

    /**
     * 查询销售周期列表
     * 
     * @param crmTimeperiod 销售周期
     * @return 销售周期
     */
    @Override
    public List<CrmTimeperiod> selectCrmTimeperiodList(CrmTimeperiod crmTimeperiod)
    {
        return crmTimeperiodMapper.selectCrmTimeperiodList(crmTimeperiod);
    }

    /**
     * 新增销售周期
     * 
     * @param crmTimeperiod 销售周期
     * @return 结果
     */
    @Override
    public int insertCrmTimeperiod(CrmTimeperiod crmTimeperiod)
    {
        crmTimeperiod.setCreateTime(DateUtils.getNowDate());
        return crmTimeperiodMapper.insertCrmTimeperiod(crmTimeperiod);
    }

    /**
     * 修改销售周期
     * 
     * @param crmTimeperiod 销售周期
     * @return 结果
     */
    @Override
    public int updateCrmTimeperiod(CrmTimeperiod crmTimeperiod)
    {
        crmTimeperiod.setUpdateTime(DateUtils.getNowDate());
        return crmTimeperiodMapper.updateCrmTimeperiod(crmTimeperiod);
    }

    /**
     * 更新当前年度下的所有销售周期
     * @param crmTimeperiod
     * @return
     */
    @Override
    public int updateCrmTimeperiodToNotActive(CrmTimeperiod crmTimeperiod) {
        crmTimeperiod.setUpdateTime(DateUtils.getNowDate());
        return crmTimeperiodMapper.updateCrmTimeperiodToNotActive(crmTimeperiod);
    }

    /**
     * 批量删除销售周期
     * 
     * @param periodIds 需要删除的销售周期主键
     * @return 结果
     */
    @Override
    public int deleteCrmTimeperiodByPeriodIds(String[] periodIds)
    {
        return crmTimeperiodMapper.deleteCrmTimeperiodByPeriodIds(periodIds);
    }

    /**
     * 删除销售周期信息
     * 
     * @param periodId 销售周期主键
     * @return 结果
     */
    @Override
    public int deleteCrmTimeperiodByPeriodId(String periodId)
    {
        return crmTimeperiodMapper.deleteCrmTimeperiodByPeriodId(periodId);
    }
}
