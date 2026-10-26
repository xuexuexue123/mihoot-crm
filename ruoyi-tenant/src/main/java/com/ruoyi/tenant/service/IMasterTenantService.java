package com.ruoyi.tenant.service;

import java.util.List;
import com.ruoyi.tenant.domain.MasterTenant;
import com.ruoyi.tenant.domain.vo.PublicTenantSiteVo;
import com.ruoyi.tenant.dto.TenantDatabaseDTO;

/**
 * 租户管理Service接口
 *
 * @author luohd
 * @date 2024-01-05
 */
public interface IMasterTenantService
{

    MasterTenant selectMasterTenant(String tenant);

    String checkTenantNameUnique(String tenantName);

    String checkWebsiteSlugUnique(String websiteSlug);

    PublicTenantSiteVo selectPublicByWebsiteSlug(String websiteSlug);

    int getTenantCount();

    int insertMasterTenant(TenantDatabaseDTO tenantDatabaseDTO);
    /**
     * 查询租户管理
     *
     * @param id 租户管理主键
     * @return 租户管理
     */
    public MasterTenant selectMasterTenantById(Long id);

    public MasterTenant selectTenantByTenantId(String tenantId);

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
     * 批量删除租户管理
     *
     * @param ids 需要删除的租户管理主键集合
     * @return 结果
     */
    public int deleteMasterTenantByIds(Long[] ids);

    /**
     * 删除租户管理信息
     *
     * @param id 租户管理主键
     * @return 结果
     */
    public int deleteMasterTenantById(Long id);
}
