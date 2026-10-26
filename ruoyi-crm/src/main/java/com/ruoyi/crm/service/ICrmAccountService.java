package com.ruoyi.crm.service;

import java.util.List;
import com.ruoyi.crm.domain.CrmAccount;
import com.ruoyi.crm.domain.CrmFamilyLtvVo;

/**
 * 客户Service接口
 * 
 * @author luohd
 * @date 2024-01-30
 */
public interface ICrmAccountService 
{
    /**
     * 查询客户
     * 
     * @param accountId 客户主键
     * @return 客户
     */
    public CrmAccount selectCrmAccountByAccountId(String accountId);

    /**
     * 根据LP中的关联基金ID查询
     *
     * @param accountId 客户
     * @return 客户集合
     */
    public CrmAccount selectCrmAccountByLp(String accountId);

    /**
     * 根据团队成员名称查询
     *
     * @param partName 团队成员名称
     * @return 客户集合
     */
    public List<CrmAccount> selectCrmAccountByParticipant(String partName);

    /**
     * 查询客户列表
     * 
     * @param crmAccount 客户
     * @return 客户集合
     */
    public List<CrmAccount> selectCrmAccountList(CrmAccount crmAccount);

    /**
     * 新增客户
     * 
     * @param crmAccount 客户
     * @return 结果
     */
    public int insertCrmAccount(CrmAccount crmAccount);

    /**
     * 修改客户
     * 
     * @param crmAccount 客户
     * @return 结果
     */
    public int updateCrmAccount(CrmAccount crmAccount);

    /**
     * 批量删除客户
     * 
     * @param accountIds 需要删除的客户主键集合
     * @return 结果
     */
    public int deleteCrmAccountByAccountIds(String[] accountIds);

    /**
     * 删除客户信息
     * 
     * @param accountId 客户主键
     * @return 结果
     */
    public int deleteCrmAccountByAccountId(String accountId);

    /**
     * 家庭账户 LTV 聚合
     */
    CrmFamilyLtvVo selectFamilyLtvByAccountId(String accountId);
}
