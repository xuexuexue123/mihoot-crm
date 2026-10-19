package com.ruoyi.crm.mapper;

import java.util.List;
import com.ruoyi.crm.domain.CrmContract;
import com.ruoyi.crm.domain.CrmPaymentterm;

/**
 * 合同Mapper接口
 * 
 * @author luohd
 * @date 2024-02-15
 */
public interface CrmContractMapper 
{
    /**
     * 查询合同
     * 
     * @param contractId 合同主键
     * @return 合同
     */
    public CrmContract selectCrmContractByContractId(String contractId);

    /**
     * 查询合同列表
     * 
     * @param crmContract 合同
     * @return 合同集合
     */
    public List<CrmContract> selectCrmContractList(CrmContract crmContract);

    /**
     * 查询本周新签订的合同列表
     *
     * @param crmContract 合同
     * @return 合同集合
     */
    public List<CrmContract> selectCrmContractListWeekly(CrmContract crmContract);

    /**
     * 查询快到期的合同
     * @param crmContract
     * @return
     */
    public List<CrmContract> selectCrmContractDueList(CrmContract crmContract);
    /**
    /**
     * 新增合同
     * 
     * @param crmContract 合同
     * @return 结果
     */
    public int insertCrmContract(CrmContract crmContract);

    /**
     * 修改合同
     * 
     * @param crmContract 合同
     * @return 结果
     */
    public int updateCrmContract(CrmContract crmContract);

    /**
     * 删除合同
     * 
     * @param contractId 合同主键
     * @return 结果
     */
    public int deleteCrmContractByContractId(String contractId);

    /**
     * 批量删除合同
     * 
     * @param contractIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteCrmContractByContractIds(String[] contractIds);

    /**
     * 批量删除收款
     * 
     * @param contractIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteCrmPaymenttermByContractIds(String[] contractIds);
    
    /**
     * 批量新增收款
     * 
     * @param crmPaymenttermList 收款列表
     * @return 结果
     */
    public int batchCrmPaymentterm(List<CrmPaymentterm> crmPaymenttermList);
    

    /**
     * 通过合同主键删除收款信息
     * 
     * @param contractId 合同ID
     * @return 结果
     */
    public int deleteCrmPaymenttermByContractId(String contractId);
}
