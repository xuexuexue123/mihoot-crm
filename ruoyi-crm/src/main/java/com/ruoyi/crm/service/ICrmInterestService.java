package com.ruoyi.crm.service;

import java.util.List;
import com.ruoyi.crm.domain.CrmInterest;

/**
 * 权益变更Service接口
 * 
 * @author luohd
 * @date 2024-07-09
 */
public interface ICrmInterestService 
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
     * 批量删除权益变更
     * 
     * @param interestIds 需要删除的权益变更主键集合
     * @return 结果
     */
    public int deleteCrmInterestByInterestIds(String[] interestIds);

    /**
     * 删除权益变更信息
     * 
     * @param interestId 权益变更主键
     * @return 结果
     */
    public int deleteCrmInterestByInterestId(String interestId);
}
