package com.ruoyi.crm.mapper;

import java.util.List;

import com.ruoyi.crm.domain.CrmTenantWebsitePage;
import com.ruoyi.crm.domain.vo.TenantWebsitePageSummaryVo;

public interface CrmTenantWebsitePageMapper
{
    List<TenantWebsitePageSummaryVo> selectSummaryList();

    CrmTenantWebsitePage selectById(String id);

    CrmTenantWebsitePage selectByPath(String path);

    CrmTenantWebsitePage selectPublishedByPath(String path);

    List<TenantWebsitePageSummaryVo> selectPublishedSummaryList();

    int countByPath(String path);

    int insert(CrmTenantWebsitePage page);

    int update(CrmTenantWebsitePage page);

    int deleteById(String id);
}
