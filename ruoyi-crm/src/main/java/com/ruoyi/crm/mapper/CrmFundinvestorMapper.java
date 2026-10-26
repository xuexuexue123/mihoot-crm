package com.ruoyi.crm.mapper;

import java.util.List;
import com.ruoyi.crm.domain.CrmFundinvestor;

/**
 * 投资人Mapper接口
 * 
 * @author luohd
 * @date 2024-06-17
 */
public interface CrmFundinvestorMapper 
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
     * 删除投资人
     * 
     * @param investorId 投资人主键
     * @return 结果
     */
    public int deleteCrmFundinvestorByInvestorId(String investorId);

    /**
     * 批量删除投资人
     * 
     * @param investorIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteCrmFundinvestorByInvestorIds(String[] investorIds);
}
