package com.ruoyi.crm.mapper;

import java.util.List;
import com.ruoyi.crm.domain.CrmPaymentterm;

/**
 * 收款Mapper接口
 * 
 * @author luohd
 * @date 2024-02-15
 */
public interface CrmPaymenttermMapper 
{
    /**
     * 查询收款
     * 
     * @param termId 收款主键
     * @return 收款
     */
    public CrmPaymentterm selectCrmPaymenttermByTermId(String termId);

    /**
     * 查询收款列表
     * 
     * @param crmPaymentterm 收款
     * @return 收款集合
     */
    public List<CrmPaymentterm> selectCrmPaymenttermList(CrmPaymentterm crmPaymentterm);

    /**
     * 查询本周收款列表
     *
     * @param crmPaymentterm 收款
     * @return 收款集合
     */
    public List<CrmPaymentterm> selectCrmPaymenttermListWeekly(CrmPaymentterm crmPaymentterm);

    /**
     * 新增收款
     * 
     * @param crmPaymentterm 收款
     * @return 结果
     */
    public int insertCrmPaymentterm(CrmPaymentterm crmPaymentterm);

    /**
     * 修改收款
     * 
     * @param crmPaymentterm 收款
     * @return 结果
     */
    public int updateCrmPaymentterm(CrmPaymentterm crmPaymentterm);

    /**
     * 删除收款
     * 
     * @param termId 收款主键
     * @return 结果
     */
    public int deleteCrmPaymenttermByTermId(String termId);

    /**
     * 批量删除收款
     * 
     * @param termIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteCrmPaymenttermByTermIds(String[] termIds);
}
