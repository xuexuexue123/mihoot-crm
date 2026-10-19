package com.ruoyi.crm.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ruoyi.crm.domain.CrmTenantWebsiteAsset;
import com.ruoyi.crm.domain.dto.WebsiteAssetQuery;

public interface CrmTenantWebsiteAssetMapper
{
    int insert(CrmTenantWebsiteAsset row);

    CrmTenantWebsiteAsset selectById(@Param("id") String id);

    CrmTenantWebsiteAsset selectActiveById(@Param("id") String id);

    List<CrmTenantWebsiteAsset> selectList(WebsiteAssetQuery query);

    int softDeleteById(@Param("id") String id);

    int countSchemaReferences(@Param("assetId") String assetId);
}
