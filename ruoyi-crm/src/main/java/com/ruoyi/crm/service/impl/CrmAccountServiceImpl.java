package com.ruoyi.crm.service.impl;

import java.util.List;

import com.ruoyi.common.annotation.DataScope;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.crm.mapper.CrmAccountMapper;
import com.ruoyi.crm.domain.CrmAccount;
import com.ruoyi.crm.domain.CrmFamilyLtvVo;
import com.ruoyi.crm.service.ICrmAccountService;

/**
 * 客户Service业务层处理
 * 
 * @author luohd
 * @date 2024-01-30
 */
@Service
public class CrmAccountServiceImpl implements ICrmAccountService 
{
    @Autowired
    private CrmAccountMapper crmAccountMapper;

    /**
     * 查询客户
     * 
     * @param accountId 客户主键
     * @return 客户
     */
    @Override
    public CrmAccount selectCrmAccountByAccountId(String accountId)
    {
        return crmAccountMapper.selectCrmAccountByAccountId(accountId);
    }

    /**
     * 查询客户列表
     *
     * @param accountId 客户
     * @return 客户
     */
    @Override
    @DataScope(deptAlias = "d", userAlias = "u")
    public CrmAccount selectCrmAccountByLp(String accountId)
    {
        return crmAccountMapper.selectCrmAccountByLp(accountId);
    }


    /**
     * 根据团队成员名称查询
     *
     * @param partName 团队成员名称
     * @return 客户集合
     */
    @Override
    @DataScope(deptAlias = "d", userAlias = "u")
    public List<CrmAccount> selectCrmAccountByParticipant(String partName){

        return crmAccountMapper.selectCrmAccountByParticipant(partName);
    }

    /**
     * 查询客户列表
     * 
     * @param crmAccount 客户
     * @return 客户
     */
    @Override
    @DataScope(deptAlias = "d", userAlias = "u")
    public List<CrmAccount> selectCrmAccountList(CrmAccount crmAccount)
    {
        return crmAccountMapper.selectCrmAccountList(crmAccount);
    }

    /**
     * 新增客户
     * 
     * @param crmAccount 客户
     * @return 结果
     */
    @Override
    public int insertCrmAccount(CrmAccount crmAccount)
    {
        crmAccount.setCreateTime(DateUtils.getNowDate());
        return crmAccountMapper.insertCrmAccount(crmAccount);
    }

    /**
     * 修改客户
     * 
     * @param crmAccount 客户
     * @return 结果
     */
    @Override
    public int updateCrmAccount(CrmAccount crmAccount)
    {
        crmAccount.setUpdateTime(DateUtils.getNowDate());
        return crmAccountMapper.updateCrmAccount(crmAccount);
    }

    /**
     * 批量删除客户
     * 
     * @param accountIds 需要删除的客户主键
     * @return 结果
     */
    @Override
    public int deleteCrmAccountByAccountIds(String[] accountIds)
    {
        return crmAccountMapper.deleteCrmAccountByAccountIds(accountIds);
    }

    /**
     * 删除客户信息
     * 
     * @param accountId 客户主键
     * @return 结果
     */
    @Override
    public int deleteCrmAccountByAccountId(String accountId)
    {
        return crmAccountMapper.deleteCrmAccountByAccountId(accountId);
    }

    @Override
    public CrmFamilyLtvVo selectFamilyLtvByAccountId(String accountId)
    {
        return crmAccountMapper.selectFamilyLtvByAccountId(accountId);
    }
}
