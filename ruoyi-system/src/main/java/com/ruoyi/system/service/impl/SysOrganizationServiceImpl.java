package com.ruoyi.system.service.impl;

import java.util.List;

import com.ruoyi.common.core.domain.entity.SysOrganization;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.SysOrganizationMapper;
import com.ruoyi.system.service.ISysOrganizationService;

/**
 * 组织管理Service业务层处理
 * 
 * @author luohd
 * @date 2024-02-28
 */
@Service
public class SysOrganizationServiceImpl implements ISysOrganizationService 
{
    @Autowired
    private SysOrganizationMapper sysOrganizationMapper;

    /**
     * 查询组织管理
     * 
     * @param organizationId 组织管理主键
     * @return 组织管理
     */
    @Override
    public SysOrganization selectSysOrganizationByOrganizationId(String organizationId)
    {
        return sysOrganizationMapper.selectSysOrganizationByOrganizationId(organizationId);
    }

    /**
     * 查询组织管理列表
     * 
     * @param sysOrganization 组织管理
     * @return 组织管理
     */
    @Override
    public List<SysOrganization> selectSysOrganizationList(SysOrganization sysOrganization)
    {
        return sysOrganizationMapper.selectSysOrganizationList(sysOrganization);
    }

    /**
     * 新增组织管理
     * 
     * @param sysOrganization 组织管理
     * @return 结果
     */
    @Override
    public int insertSysOrganization(SysOrganization sysOrganization)
    {
        sysOrganization.setCreateTime(DateUtils.getNowDate());
        return sysOrganizationMapper.insertSysOrganization(sysOrganization);
    }

    /**
     * 修改组织管理
     * 
     * @param sysOrganization 组织管理
     * @return 结果
     */
    @Override
    public int updateSysOrganization(SysOrganization sysOrganization)
    {
        sysOrganization.setUpdateTime(DateUtils.getNowDate());
        return sysOrganizationMapper.updateSysOrganization(sysOrganization);
    }

    /**
     * 批量删除组织管理
     * 
     * @param organizationIds 需要删除的组织管理主键
     * @return 结果
     */
    @Override
    public int deleteSysOrganizationByOrganizationIds(String[] organizationIds)
    {
        return sysOrganizationMapper.deleteSysOrganizationByOrganizationIds(organizationIds);
    }

    /**
     * 删除组织管理信息
     * 
     * @param organizationId 组织管理主键
     * @return 结果
     */
    @Override
    public int deleteSysOrganizationByOrganizationId(String organizationId)
    {
        return sysOrganizationMapper.deleteSysOrganizationByOrganizationId(organizationId);
    }
}
