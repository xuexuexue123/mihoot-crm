package com.ruoyi.crm.service.impl;

import java.util.List;

import com.ruoyi.common.annotation.DataScope;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.uuid.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import com.ruoyi.common.utils.StringUtils;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.crm.domain.CrmPaymentterm;
import com.ruoyi.crm.mapper.CrmContractMapper;
import com.ruoyi.crm.domain.CrmContract;
import com.ruoyi.crm.service.ICrmContractService;
import com.ruoyi.crm.service.IPatientClvService;

/**
 * 合同Service业务层处理
 * 
 * @author luohd
 * @date 2024-02-15
 */
@Service
public class CrmContractServiceImpl implements ICrmContractService 
{
    @Autowired
    private CrmContractMapper crmContractMapper;

    @Autowired
    private IPatientClvService patientClvService;

    /**
     * 查询合同
     * 
     * @param contractId 合同主键
     * @return 合同
     */
    @Override
    public CrmContract selectCrmContractByContractId(String contractId)
    {
        return crmContractMapper.selectCrmContractByContractId(contractId);
    }

    /**
     * 查询合同列表
     * 
     * @param crmContract 合同
     * @return 合同
     */
    @Override
    @DataScope(deptAlias = "d", userAlias = "u")
    public List<CrmContract> selectCrmContractList(CrmContract crmContract)
    {
        return crmContractMapper.selectCrmContractList(crmContract);
    }

    /**
     * 查询本周新签订的合同列表
     *
     * @param crmContract 合同
     * @return 合同
     */
    @Override
    @DataScope(deptAlias = "d", userAlias = "u")
    public List<CrmContract> selectCrmContractListWeekly(CrmContract crmContract)
    {
        return crmContractMapper.selectCrmContractListWeekly(crmContract);
    }

    /**
     * 查询到期的合同列表
     *
     * @param crmContract 合同
     * @return 合同
     */
    @Override
    @DataScope(deptAlias = "d", userAlias = "u")
    public List<CrmContract> selectCrmContractDueList(CrmContract crmContract) {
        return crmContractMapper.selectCrmContractDueList(crmContract);
    }

    /**
     * 新增合同
     * 
     * @param crmContract 合同
     * @return 结果
     */
    @Transactional
    @Override
    public int insertCrmContract(CrmContract crmContract)
    {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        crmContract.setCreateTime(DateUtils.getNowDate());
        crmContract.setUserId(loginUser.getUserId());
        crmContract.setDeptId(loginUser.getDeptId());
        int rows = crmContractMapper.insertCrmContract(crmContract);
        insertCrmPaymentterm(crmContract);
        if (rows > 0)
        {
            patientClvService.enqueueRecomputeForContact(crmContract.getContactId(), crmContract.getAccountId());
        }
        return rows;
    }

    /**
     * 修改合同
     * 
     * @param crmContract 合同
     * @return 结果
     */
    @Transactional
    @Override
    public int updateCrmContract(CrmContract crmContract)
    {
        crmContract.setUpdateTime(DateUtils.getNowDate());
        crmContractMapper.deleteCrmPaymenttermByContractId(crmContract.getContractId());
        insertCrmPaymentterm(crmContract);
        int rows = crmContractMapper.updateCrmContract(crmContract);
        if (rows > 0)
        {
            patientClvService.enqueueRecomputeForContact(crmContract.getContactId(), crmContract.getAccountId());
        }
        return rows;
    }

    /**
     * 批量删除合同
     * 
     * @param contractIds 需要删除的合同主键
     * @return 结果
     */
    @Transactional
    @Override
    public int deleteCrmContractByContractIds(String[] contractIds)
    {
        crmContractMapper.deleteCrmPaymenttermByContractIds(contractIds);
        return crmContractMapper.deleteCrmContractByContractIds(contractIds);
    }

    /**
     * 删除合同信息
     * 
     * @param contractId 合同主键
     * @return 结果
     */
    @Transactional
    @Override
    public int deleteCrmContractByContractId(String contractId)
    {
        crmContractMapper.deleteCrmPaymenttermByContractId(contractId);
        return crmContractMapper.deleteCrmContractByContractId(contractId);
    }

    /**
     * 新增收款信息
     * 
     * @param crmContract 合同对象
     */
    public void insertCrmPaymentterm(CrmContract crmContract)
    {
        List<CrmPaymentterm> crmPaymenttermList = crmContract.getCrmPaymenttermList();
        String contractId = crmContract.getContractId();

        LoginUser loginUser = SecurityUtils.getLoginUser();

        if (StringUtils.isNotNull(crmPaymenttermList))
        {
            List<CrmPaymentterm> list = new ArrayList<CrmPaymentterm>();
            for (CrmPaymentterm crmPaymentterm : crmPaymenttermList)
            {
                crmPaymentterm.setTermId(UUID.randomUUID().toString());
                crmPaymentterm.setContractId(contractId);
                crmPaymentterm.setUserId(loginUser.getUserId());
                crmPaymentterm.setDeptId(loginUser.getDeptId());
                list.add(crmPaymentterm);
            }
            if (list.size() > 0)
            {
                crmContractMapper.batchCrmPaymentterm(list);
            }
        }
    }
}
