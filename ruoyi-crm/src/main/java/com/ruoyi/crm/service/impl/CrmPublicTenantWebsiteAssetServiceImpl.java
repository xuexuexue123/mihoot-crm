package com.ruoyi.crm.service.impl;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.file.FileUtils;
import com.ruoyi.crm.domain.CrmTenantWebsiteAsset;
import com.ruoyi.crm.mapper.CrmTenantWebsiteAssetMapper;
import com.ruoyi.crm.service.ICrmPublicTenantWebsiteAssetService;
import com.ruoyi.crm.service.support.PublicTenantSiteResolver;
import com.ruoyi.crm.service.support.WaitResumeTenantBootstrap;
import com.ruoyi.framework.datasource.DynamicDataSourceContextHolder;
import com.ruoyi.tenant.domain.vo.PublicTenantSiteVo;

@Service
public class CrmPublicTenantWebsiteAssetServiceImpl implements ICrmPublicTenantWebsiteAssetService
{
    @Autowired
    private PublicTenantSiteResolver publicTenantSiteResolver;

    @Autowired
    private WaitResumeTenantBootstrap waitResumeTenantBootstrap;

    @Autowired
    private CrmTenantWebsiteAssetMapper assetMapper;

    @Override
    public void streamActiveAsset(String slug, UUID assetId, HttpServletResponse response) throws IOException
    {
        PublicTenantSiteVo site = publicTenantSiteResolver.resolveActiveSite(slug);
        try
        {
            waitResumeTenantBootstrap.bindTenantDataSource(site.getTenantName());
            CrmTenantWebsiteAsset asset = assetMapper.selectActiveById(assetId.toString());
            if (asset == null)
            {
                throw new ServiceException("Not found", HttpStatus.NOT_FOUND);
            }
            streamFile(asset, response);
        }
        finally
        {
            DynamicDataSourceContextHolder.clearDataSourceKey();
        }
    }

    private void streamFile(CrmTenantWebsiteAsset asset, HttpServletResponse response) throws IOException
    {
        String resource = asset.getFileName();
        if (StringUtils.isBlank(resource) || !resource.startsWith(Constants.RESOURCE_PREFIX))
        {
            throw new ServiceException("Not found", HttpStatus.NOT_FOUND);
        }

        String localPath = RuoYiConfig.getProfile() + StringUtils.substringAfter(resource, Constants.RESOURCE_PREFIX);
        File file = new File(localPath);
        if (!file.exists() || !file.isFile())
        {
            throw new ServiceException("Not found", HttpStatus.NOT_FOUND);
        }

        response.setContentType(asset.getMimeType());
        response.setHeader("Cache-Control", "public, max-age=31536000, immutable");
        if (asset.getUpdateTime() != null)
        {
            response.setHeader("ETag", "\"" + asset.getId() + "-" + asset.getUpdateTime().getTime() + "\"");
        }
        FileUtils.writeBytes(localPath, response.getOutputStream());
    }
}
