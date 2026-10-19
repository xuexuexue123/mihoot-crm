package com.ruoyi.tenant.service.impl;

import java.util.List;

import com.ruoyi.common.annotation.DataSource;
import com.ruoyi.common.constant.TenantConstants;
import com.ruoyi.common.enums.DataSourceType;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.tenant.domain.vo.PublicTenantSiteVo;
import com.ruoyi.tenant.dto.TenantDatabaseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.tenant.mapper.MasterTenantMapper;
import com.ruoyi.tenant.domain.MasterTenant;
import com.ruoyi.tenant.service.IMasterTenantService;

/**
 * 租户管理Service业务层处理
 *
 * @author luohd
 * @date 2024-01-05
 */
@Service
public class MasterTenantServiceImpl implements IMasterTenantService
{
    @Autowired
    private MasterTenantMapper masterTenantMapper;

    @Override
    @DataSource(DataSourceType.MASTER)
    public MasterTenant selectMasterTenant(String tenant) {
        MasterTenant masterTenant = new MasterTenant();
        masterTenant.setTenant(tenant);
        return masterTenantMapper.selectMasterTenant(masterTenant);
    }

    @Override
    @DataSource(DataSourceType.MASTER)
    public MasterTenant selectTenantByTenantId(String tenantId) {

        return masterTenantMapper.selectTenantByTenantId(tenantId);
    }

    @Override
    public int getTenantCount() {
        return masterTenantMapper.getTenantCount();
    }

    @Override
    @DataSource(DataSourceType.MASTER)
    public String checkTenantNameUnique(String tenantName)
    {
        int count = masterTenantMapper.checkTenantNameUnique(tenantName);
        if (count > 0)
        {
            return TenantConstants.NOT_UNIQUE;
        }
        return TenantConstants.UNIQUE;
    }

    @Override
    @DataSource(DataSourceType.MASTER)
    public String checkWebsiteSlugUnique(String websiteSlug)
    {
        int count = masterTenantMapper.countByWebsiteSlug(websiteSlug);
        if (count > 0)
        {
            return TenantConstants.NOT_UNIQUE;
        }
        return TenantConstants.UNIQUE;
    }

    @Override
    @DataSource(DataSourceType.MASTER)
    public PublicTenantSiteVo selectPublicByWebsiteSlug(String websiteSlug)
    {
        MasterTenant row = masterTenantMapper.selectPublicByWebsiteSlug(websiteSlug);
        if (row == null)
        {
            return null;
        }
        PublicTenantSiteVo vo = new PublicTenantSiteVo();
        vo.setTenantName(row.getTenant());
        vo.setWebsiteSlug(row.getWebsiteSlug());
        vo.setWebsiteStatus(row.getWebsiteStatus());
        String companyName = StringUtils.isNotEmpty(row.getCompanyName()) ? row.getCompanyName() : row.getTenant();
        vo.setCompanyName(companyName);
        return vo;
    }

    /**
     * 查询租户管理
     *
     * @param id 租户管理主键
     * @return 租户管理
     */
    @Override
    @DataSource(DataSourceType.MASTER)
    public MasterTenant selectMasterTenantById(Long id)
    {
        return masterTenantMapper.selectMasterTenantById(id);
    }

    /**
     * 查询租户管理列表
     *
     * @param masterTenant 租户管理
     * @return 租户管理
     */
    @Override
    @DataSource(DataSourceType.MASTER)
    public List<MasterTenant> selectMasterTenantList(MasterTenant masterTenant)
    {
        return masterTenantMapper.selectMasterTenantList(masterTenant);
    }

    /**
     * 新增租户管理
     *
     * @param masterTenant 租户管理
     * @return 结果
     */
    @Override
    public int insertMasterTenant(MasterTenant masterTenant)
    {
        masterTenant.setCreateTime(DateUtils.getNowDate());
        return masterTenantMapper.insertMasterTenant(masterTenant);
    }

    @Override
    @DataSource(DataSourceType.MASTER)
    public int insertMasterTenant(TenantDatabaseDTO tenantDatabaseDTO){
        MasterTenant masterTenant = new MasterTenant();
        masterTenant.setTenant(tenantDatabaseDTO.getTenantName());
        masterTenant.setUrl(tenantDatabaseDTO.getUrl());
        masterTenant.setDatabaseName(tenantDatabaseDTO.getTenantDatabase());
        masterTenant.setUsername(tenantDatabaseDTO.getDbUser());
        masterTenant.setPassword(tenantDatabaseDTO.getDbPass());
        masterTenant.setCreateTime(DateUtils.getNowDate());
        masterTenant.setExpirationDate(DateUtils.addDays(DateUtils.getNowDate(), 7));//有效期7天
        masterTenant.setWebsiteSlug(tenantDatabaseDTO.getWebsiteSlug());
        masterTenant.setWebsiteStatus("active");
        masterTenant.setWebsiteCreatedAt(DateUtils.getNowDate());
        masterTenant.setCompanyName(tenantDatabaseDTO.getCompanyName());
        return masterTenantMapper.insertMasterTenant(masterTenant);
    }
    /**
     * 修改租户管理
     *
     * @param masterTenant 租户管理
     * @return 结果
     */
    @Override
    public int updateMasterTenant(MasterTenant masterTenant)
    {
        return masterTenantMapper.updateMasterTenant(masterTenant);
    }

    /**
     * 批量删除租户管理
     *
     * @param ids 需要删除的租户管理主键
     * @return 结果
     */
    @Override
    public int deleteMasterTenantByIds(Long[] ids)
    {
        return masterTenantMapper.deleteMasterTenantByIds(ids);
    }

    /**
     * 删除租户管理信息
     *
     * @param id 租户管理主键
     * @return 结果
     */
    @Override
    public int deleteMasterTenantById(Long id)
    {
        return masterTenantMapper.deleteMasterTenantById(id);
    }
}
