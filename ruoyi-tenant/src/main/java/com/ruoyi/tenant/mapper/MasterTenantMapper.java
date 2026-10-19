package com.ruoyi.tenant.mapper;

import java.util.List;
import com.ruoyi.tenant.domain.MasterTenant;

/**
 * 租户管理Mapper接口
 *
 * @author luohd
 * @date 2024-01-05
 */
public interface MasterTenantMapper
{
    int checkTenantNameUnique(String tenantName);

    int countByWebsiteSlug(String websiteSlug);

    MasterTenant selectPublicByWebsiteSlug(String websiteSlug);

    int getTenantCount();

    MasterTenant selectMasterTenant(MasterTenant masterTenant);

    MasterTenant selectTenantByTenantId(String tenantId);

    /**
     * 查询租户管理
     *
     * @param id 租户管理主键
     * @return 租户管理
     */
    public MasterTenant selectMasterTenantById(Long id);

    /**
     * 查询租户管理列表
     *
     * @param masterTenant 租户管理
     * @return 租户管理集合
     */
    public List<MasterTenant> selectMasterTenantList(MasterTenant masterTenant);

    /**
     * 新增租户管理
     *
     * @param masterTenant 租户管理
     * @return 结果
     */
    public int insertMasterTenant(MasterTenant masterTenant);

    /**
     * 修改租户管理
     *
     * @param masterTenant 租户管理
     * @return 结果
     */
    public int updateMasterTenant(MasterTenant masterTenant);

    /**
     * 删除租户管理
     *
     * @param id 租户管理主键
     * @return 结果
     */
    public int deleteMasterTenantById(Long id);

    /**
     * 批量删除租户管理
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteMasterTenantByIds(Long[] ids);
}
