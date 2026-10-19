package com.ruoyi.crm.service;

import java.util.List;
import com.ruoyi.crm.domain.CrmFundpaid;

/**
 * 基金投资Service接口
 * 
 * @author luohd
 * @date 2024-07-09
 */
public interface ICrmFundpaidService 
{
    /**
     * 查询基金投资
     * 
     * @param paidId 基金投资主键
     * @return 基金投资
     */
    public CrmFundpaid selectCrmFundpaidByPaidId(String paidId);

    /**
     * 根据权益ID查询流水
     *
     * @param interestId
     * @return
     */
    public List<CrmFundpaid> selectCrmFundpaidByInterestId(String interestId);

    /**
     * 查询基金投资列表
     * 
     * @param crmFundpaid 基金投资
     * @return 基金投资集合
     */
    public List<CrmFundpaid> selectCrmFundpaidList(CrmFundpaid crmFundpaid);

    /**
     * 新增基金投资
     * 
     * @param crmFundpaid 基金投资
     * @return 结果
     */
    public int insertCrmFundpaid(CrmFundpaid crmFundpaid);

    /**
     * 修改基金投资
     * 
     * @param crmFundpaid 基金投资
     * @return 结果
     */
    public int updateCrmFundpaid(CrmFundpaid crmFundpaid);

    /**
     * 批量删除基金投资
     * 
     * @param paidIds 需要删除的基金投资主键集合
     * @return 结果
     */
    public int deleteCrmFundpaidByPaidIds(String[] paidIds);

    /**
     * 删除基金投资信息
     * 
     * @param paidId 基金投资主键
     * @return 结果
     */
    public int deleteCrmFundpaidByPaidId(String paidId);
}
