package com.ruoyi.crm.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.crm.mapper.CrmCashflowMapper;
import com.ruoyi.crm.domain.CrmCashflow;
import com.ruoyi.crm.service.ICrmCashflowService;

/**
 * 资金流水Service业务层处理
 * 
 * @author luohd
 * @date 2024-06-19
 */
@Service
public class CrmCashflowServiceImpl implements ICrmCashflowService 
{
    @Autowired
    private CrmCashflowMapper crmCashflowMapper;

    /**
     * 查询资金流水
     * 
     * @param cashflowId 资金流水主键
     * @return 资金流水
     */
    @Override
    public CrmCashflow selectCrmCashflowByCashflowId(String cashflowId)
    {
        return crmCashflowMapper.selectCrmCashflowByCashflowId(cashflowId);
    }

    /**
     * 查询资金流水列表
     * 
     * @param crmCashflow 资金流水
     * @return 资金流水
     */
    @Override
    public List<CrmCashflow> selectCrmCashflowList(CrmCashflow crmCashflow)
    {
        return crmCashflowMapper.selectCrmCashflowList(crmCashflow);
    }

    /**
     * 新增资金流水
     * 
     * @param crmCashflow 资金流水
     * @return 结果
     */
    @Override
    public int insertCrmCashflow(CrmCashflow crmCashflow)
    {
        crmCashflow.setCreateTime(DateUtils.getNowDate());
        return crmCashflowMapper.insertCrmCashflow(crmCashflow);
    }

    /**
     * 修改资金流水
     * 
     * @param crmCashflow 资金流水
     * @return 结果
     */
    @Override
    public int updateCrmCashflow(CrmCashflow crmCashflow)
    {
        crmCashflow.setUpdateTime(DateUtils.getNowDate());
        return crmCashflowMapper.updateCrmCashflow(crmCashflow);
    }

    /**
     * 批量删除资金流水
     * 
     * @param cashflowIds 需要删除的资金流水主键
     * @return 结果
     */
    @Override
    public int deleteCrmCashflowByCashflowIds(String[] cashflowIds)
    {
        return crmCashflowMapper.deleteCrmCashflowByCashflowIds(cashflowIds);
    }

    /**
     * 删除资金流水信息
     * 
     * @param cashflowId 资金流水主键
     * @return 结果
     */
    @Override
    public int deleteCrmCashflowByCashflowId(String cashflowId)
    {
        return crmCashflowMapper.deleteCrmCashflowByCashflowId(cashflowId);
    }
}
