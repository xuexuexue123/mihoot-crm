package com.ruoyi.tenant.service.impl;

import java.util.List;

import com.ruoyi.common.constant.TenantConstants;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.tenant.mapper.MasterRegistrationMapper;
import com.ruoyi.tenant.domain.MasterRegistration;
import com.ruoyi.tenant.service.IMasterRegistrationService;

/**
 * 主注册Service业务层处理
 * 
 * @author luohd
 * @date 2024-03-17
 */
@Service
public class MasterRegistrationServiceImpl implements IMasterRegistrationService 
{
    @Autowired
    private MasterRegistrationMapper masterRegistrationMapper;

    @Override
    public String checkCompanyNameUnique(String companyName) {
        int count = masterRegistrationMapper.checkCompanyNameUnique(companyName);
        if (count > 0)
        {
            return TenantConstants.NOT_UNIQUE;
        }
        return TenantConstants.UNIQUE;
    }

    @Override
    public String checkMobileUnique(String mobile) {
        int count = masterRegistrationMapper.checkCompanyNameUnique(mobile);
        if (count > 0)
        {
            return TenantConstants.NOT_UNIQUE;
        }
        return TenantConstants.UNIQUE;
    }

    @Override
    public String checkEmailUnique(String email) {
        int count = masterRegistrationMapper.checkCompanyNameUnique(email);
        if (count > 0)
        {
            return TenantConstants.NOT_UNIQUE;
        }
        return TenantConstants.UNIQUE;
    }

    /**
     * 查询主注册
     * 
     * @param registId 主注册主键
     * @return 主注册
     */
    @Override
    public MasterRegistration selectMasterRegistrationByRegistId(Long registId)
    {
        return masterRegistrationMapper.selectMasterRegistrationByRegistId(registId);
    }

    /**
     * 查询主注册列表
     * 
     * @param masterRegistration 主注册
     * @return 主注册
     */
    @Override
    public List<MasterRegistration> selectMasterRegistrationList(MasterRegistration masterRegistration)
    {
        return masterRegistrationMapper.selectMasterRegistrationList(masterRegistration);
    }

    /**
     * 新增主注册
     * 
     * @param masterRegistration 主注册
     * @return 结果
     */
    @Override
    public int insertMasterRegistration(MasterRegistration masterRegistration)
    {
        masterRegistration.setCreateTime(DateUtils.getNowDate());
        return masterRegistrationMapper.insertMasterRegistration(masterRegistration);
    }

    /**
     * 修改主注册
     * 
     * @param masterRegistration 主注册
     * @return 结果
     */
    @Override
    public int updateMasterRegistration(MasterRegistration masterRegistration)
    {
        masterRegistration.setUpdateTime(DateUtils.getNowDate());
        return masterRegistrationMapper.updateMasterRegistration(masterRegistration);
    }

    /**
     * 批量删除主注册
     * 
     * @param registIds 需要删除的主注册主键
     * @return 结果
     */
    @Override
    public int deleteMasterRegistrationByRegistIds(Long[] registIds)
    {
        return masterRegistrationMapper.deleteMasterRegistrationByRegistIds(registIds);
    }

    /**
     * 删除主注册信息
     * 
     * @param registId 主注册主键
     * @return 结果
     */
    @Override
    public int deleteMasterRegistrationByRegistId(Long registId)
    {
        return masterRegistrationMapper.deleteMasterRegistrationByRegistId(registId);
    }
}
