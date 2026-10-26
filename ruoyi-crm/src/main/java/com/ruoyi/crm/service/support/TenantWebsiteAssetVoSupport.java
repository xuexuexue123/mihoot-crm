package com.ruoyi.crm.service.support;

import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.domain.CrmTenantWebsiteAsset;
import com.ruoyi.crm.domain.vo.WebsiteAssetVo;

public final class TenantWebsiteAssetVoSupport
{
    private TenantWebsiteAssetVoSupport()
    {
    }

    public static WebsiteAssetVo toVo(CrmTenantWebsiteAsset asset, String publicUrl)
    {
        WebsiteAssetVo vo = new WebsiteAssetVo();
        vo.setId(asset.getId());
        vo.setFileTitle(asset.getFileTitle());
        vo.setOriginalName(asset.getOriginalName());
        vo.setFileType(asset.getFileType());
        vo.setFileSize(asset.getFileSize());
        vo.setMediaType(asset.getMediaType());
        vo.setMimeType(asset.getMimeType());
        vo.setWidth(asset.getWidth());
        vo.setHeight(asset.getHeight());
        vo.setDurationMs(asset.getDurationMs());
        vo.setPublicPath(asset.getPublicPath());
        vo.setPublicUrl(StringUtils.isNotBlank(publicUrl) ? publicUrl : asset.getPublicPath());
        vo.setCreateBy(asset.getCreateBy());
        vo.setCreateTime(TenantWebsitePageVoSupport.toIso8601(asset.getCreateTime()));
        return vo;
    }
}
