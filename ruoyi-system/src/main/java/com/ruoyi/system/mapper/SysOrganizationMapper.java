package com.ruoyi.system.mapper;

import java.util.List;

import com.ruoyi.common.core.domain.entity.SysOrganization;

/**
 * 组织管理Mapper接口
 * 
 * @author luohd
 * @date 2024-02-28
 */
public interface SysOrganizationMapper 
{
    /**
     * 查询组织管理
     * 
     * @param organizationId 组织管理主键
     * @return 组织管理
     */
    public SysOrganization selectSysOrganizationByOrganizationId(String organizationId);

    /**
     * 查询组织管理列表
     * 
     * @param sysOrganization 组织管理
     * @return 组织管理集合
     */
    public List<SysOrganization> selectSysOrganizationList(SysOrganization sysOrganization);

    /**
     * 新增组织管理
     * 
     * @param sysOrganization 组织管理
     * @return 结果
     */
    public int insertSysOrganization(SysOrganization sysOrganization);

    /**
     * 修改组织管理
     * 
     * @param sysOrganization 组织管理
     * @return 结果
     */
    public int updateSysOrganization(SysOrganization sysOrganization);

    /**
     * 删除组织管理
     * 
     * @param organizationId 组织管理主键
     * @return 结果
     */
    public int deleteSysOrganizationByOrganizationId(String organizationId);

    /**
     * 批量删除组织管理
     * 
     * @param organizationIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteSysOrganizationByOrganizationIds(String[] organizationIds);
}
