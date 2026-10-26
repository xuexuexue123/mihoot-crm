package com.ruoyi.crm.mapper;

import java.util.List;
import com.ruoyi.crm.domain.CrmCashflow;

/**
 * 资金流水Mapper接口
 * 
 * @author luohd
 * @date 2024-06-19
 */
public interface CrmCashflowMapper 
{
    /**
     * 查询资金流水
     * 
     * @param cashflowId 资金流水主键
     * @return 资金流水
     */
    public CrmCashflow selectCrmCashflowByCashflowId(String cashflowId);

    /**
     * 查询资金流水列表
     * 
     * @param crmCashflow 资金流水
     * @return 资金流水集合
     */
    public List<CrmCashflow> selectCrmCashflowList(CrmCashflow crmCashflow);

    /**
     * 新增资金流水
     * 
     * @param crmCashflow 资金流水
     * @return 结果
     */
    public int insertCrmCashflow(CrmCashflow crmCashflow);

    /**
     * 修改资金流水
     * 
     * @param crmCashflow 资金流水
     * @return 结果
     */
    public int updateCrmCashflow(CrmCashflow crmCashflow);

    /**
     * 删除资金流水
     * 
     * @param cashflowId 资金流水主键
     * @return 结果
     */
    public int deleteCrmCashflowByCashflowId(String cashflowId);

    /**
     * 批量删除资金流水
     * 
     * @param cashflowIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteCrmCashflowByCashflowIds(String[] cashflowIds);
}
