package com.ruoyi.crm.mapper;

import java.util.List;
import com.ruoyi.crm.domain.CrmInterest;
import com.ruoyi.crm.domain.CrmFundpaid;

/**
 * 权益变更Mapper接口
 * 
 * @author luohd
 * @date 2024-07-09
 */
public interface CrmInterestMapper 
{
    /**
     * 查询权益变更
     * 
     * @param interestId 权益变更主键
     * @return 权益变更
     */
    public CrmInterest selectCrmInterestByInterestId(String interestId);

    /**
     * 查询权益变更列表
     * 
     * @param crmInterest 权益变更
     * @return 权益变更集合
     */
    public List<CrmInterest> selectCrmInterestList(CrmInterest crmInterest);

    /**
     * 新增权益变更
     * 
     * @param crmInterest 权益变更
     * @return 结果
     */
    public int insertCrmInterest(CrmInterest crmInterest);

    /**
     * 修改权益变更
     * 
     * @param crmInterest 权益变更
     * @return 结果
     */
    public int updateCrmInterest(CrmInterest crmInterest);

    /**
     * 删除权益变更
     * 
     * @param interestId 权益变更主键
     * @return 结果
     */
    public int deleteCrmInterestByInterestId(String interestId);

    /**
     * 批量删除权益变更
     * 
     * @param interestIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteCrmInterestByInterestIds(String[] interestIds);

    /**
     * 批量删除基金投资
     * 
     * @param interestIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteCrmFundpaidByInterestIds(String[] interestIds);
    
    /**
     * 批量新增基金投资
     * 
     * @param crmFundpaidList 基金投资列表
     * @return 结果
     */
    public int batchCrmFundpaid(List<CrmFundpaid> crmFundpaidList);
    

    /**
     * 通过权益变更主键删除基金投资信息
     * 
     * @param interestId 权益变更ID
     * @return 结果
     */
    public int deleteCrmFundpaidByInterestId(String interestId);
}
