package com.ruoyi.crm.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.ruoyi.crm.domain.dto.WebsiteAssetQuery;
import com.ruoyi.crm.domain.vo.WebsiteAssetVo;

public interface ICrmTenantWebsiteAssetService
{
    WebsiteAssetVo upload(MultipartFile file, String fileTitle, String tenantName);

    List<WebsiteAssetVo> list(WebsiteAssetQuery query, String tenantName);

    WebsiteAssetVo getById(String id, String tenantName);

    void softDelete(String id);
}
