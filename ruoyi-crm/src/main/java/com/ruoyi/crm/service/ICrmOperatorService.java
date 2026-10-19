package com.ruoyi.crm.service;

import java.util.List;
import com.ruoyi.crm.domain.CrmOperator;

/**
 * 销售人员Service接口
 * 
 * @author luohd
 * @date 2024-01-29
 */
public interface ICrmOperatorService 
{
    /**
     * 查询销售人员
     * 
     * @param operatorId 销售人员主键
     * @return 销售人员
     */
    public CrmOperator selectCrmOperatorByOperatorId(String operatorId);

    /**
     * 查询销售人员列表
     * 
     * @param crmOperator 销售人员
     * @return 销售人员集合
     */
    public List<CrmOperator> selectCrmOperatorList(CrmOperator crmOperator);

    /**
     * 新增销售人员
     * 
     * @param crmOperator 销售人员
     * @return 结果
     */
    public int insertCrmOperator(CrmOperator crmOperator);

    /**
     * 修改销售人员
     * 
     * @param crmOperator 销售人员
     * @return 结果
     */
    public int updateCrmOperator(CrmOperator crmOperator);

    /**
     * 批量删除销售人员
     * 
     * @param operatorIds 需要删除的销售人员主键集合
     * @return 结果
     */
    public int deleteCrmOperatorByOperatorIds(String[] operatorIds);

    /**
     * 删除销售人员信息
     * 
     * @param operatorId 销售人员主键
     * @return 结果
     */
    public int deleteCrmOperatorByOperatorId(String operatorId);
}
