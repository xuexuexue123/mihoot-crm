package com.ruoyi.crm.service;

import java.util.List;
import com.ruoyi.crm.domain.CrmFundinvestor;

/**
 * 投资人Service接口
 * 
 * @author luohd
 * @date 2024-06-17
 */
public interface ICrmFundinvestorService 
{
    /**
     * 查询投资人
     * 
     * @param investorId 投资人主键
     * @return 投资人
     */
    public CrmFundinvestor selectCrmFundinvestorByInvestorId(String investorId);

    /**
     * 查询投资人列表
     * 
     * @param crmFundinvestor 投资人
     * @return 投资人集合
     */
    public List<CrmFundinvestor> selectCrmFundinvestorList(CrmFundinvestor crmFundinvestor);

    /**
     * 新增投资人
     * 
     * @param crmFundinvestor 投资人
     * @return 结果
     */
    public int insertCrmFundinvestor(CrmFundinvestor crmFundinvestor);

    /**
     * 修改投资人
     * 
     * @param crmFundinvestor 投资人
     * @return 结果
     */
    public int updateCrmFundinvestor(CrmFundinvestor crmFundinvestor);

    /**
     * 批量删除投资人
     * 
     * @param investorIds 需要删除的投资人主键集合
     * @return 结果
     */
    public int deleteCrmFundinvestorByInvestorIds(String[] investorIds);

    /**
     * 删除投资人信息
     * 
     * @param investorId 投资人主键
     * @return 结果
     */
    public int deleteCrmFundinvestorByInvestorId(String investorId);
}
