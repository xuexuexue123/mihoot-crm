package com.ruoyi.crm.service;

import java.util.List;
import com.ruoyi.crm.domain.CrmContract;

/**
 * 合同Service接口
 * 
 * @author luohd
 * @date 2024-02-15
 */
public interface ICrmContractService 
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
     * 批量删除合同
     * 
     * @param contractIds 需要删除的合同主键集合
     * @return 结果
     */
    public int deleteCrmContractByContractIds(String[] contractIds);

    /**
     * 删除合同信息
     * 
     * @param contractId 合同主键
     * @return 结果
     */
    public int deleteCrmContractByContractId(String contractId);
}
