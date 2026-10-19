package com.ruoyi.crm.service.impl;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.file.FileUploadUtils;
import com.ruoyi.crm.domain.CrmTenantWebsiteAsset;
import com.ruoyi.crm.domain.dto.WebsiteAssetQuery;
import com.ruoyi.crm.domain.vo.WebsiteAssetVo;
import com.ruoyi.crm.mapper.CrmTenantWebsiteAssetMapper;
import com.ruoyi.crm.service.ICrmTenantWebsiteAssetService;
import com.ruoyi.crm.service.support.MasterDataSourceSupport;
import com.ruoyi.crm.service.support.TenantWebsiteAssetUploadValidator;
import com.ruoyi.crm.service.support.TenantWebsiteAssetUploadValidator.DetectedMedia;
import com.ruoyi.crm.service.support.TenantWebsiteAssetVoSupport;
import com.ruoyi.crm.service.support.TenantWebsitePublicUrlBuilder;
import com.ruoyi.tenant.domain.MasterTenant;
import com.ruoyi.tenant.service.IMasterTenantService;

@Service
public class CrmTenantWebsiteAssetServiceImpl implements ICrmTenantWebsiteAssetService
{
    @Autowired
    private CrmTenantWebsiteAssetMapper assetMapper;

    @Autowired
    private TenantWebsiteAssetUploadValidator uploadValidator;

    @Autowired
    private TenantWebsitePublicUrlBuilder publicUrlBuilder;

    @Autowired
    private IMasterTenantService masterTenantService;

    @Override
    public WebsiteAssetVo upload(MultipartFile file, String fileTitle, String tenantName)
    {
        if (StringUtils.isBlank(tenantName))
        {
            throw new ServiceException("tenant header is required", HttpStatus.BAD_REQUEST);
        }

        DetectedMedia media = uploadValidator.detectAndValidate(file);
        String id = UUID.randomUUID().toString();
        String extension = FilenameUtils.getExtension(file.getOriginalFilename()).toLowerCase();
        String storedPath;
        try
        {
            String uploadDir = RuoYiConfig.getUploadPath() + "/" + tenantName;
            storedPath = FileUploadUtils.upload(uploadDir, file, uploadValidator.allowedExtensionsFor(media));
        }
        catch (Exception ex)
        {
            throw new ServiceException("文件保存失败: " + ex.getMessage(), HttpStatus.ERROR);
        }

        String originalName = file.getOriginalFilename();
        String title = StringUtils.isNotBlank(fileTitle)
                ? fileTitle.trim()
                : FilenameUtils.getBaseName(originalName);
        Date now = new Date();

        CrmTenantWebsiteAsset row = new CrmTenantWebsiteAsset();
        row.setId(id);
        row.setFileTitle(title);
        row.setFileName(storedPath);
        row.setOriginalName(originalName);
        row.setFileType(extension);
        row.setFileSize(file.getSize());
        row.setMediaType(media.getMediaType());
        row.setMimeType(file.getContentType().split(";")[0].trim());
        row.setStatus(CrmTenantWebsiteAsset.STATUS_ACTIVE);
        row.setPublicPath(publicUrlBuilder.buildPublicPath(id));
        row.setCreateBy(SecurityUtils.getUsername());
        row.setCreateTime(now);
        row.setUpdateTime(now);

        assetMapper.insert(row);
        return TenantWebsiteAssetVoSupport.toVo(row, buildPublicUrl(resolveWebsiteSlug(tenantName), id));
    }

    @Override
    public List<WebsiteAssetVo> list(WebsiteAssetQuery query, String tenantName)
    {
        List<CrmTenantWebsiteAsset> rows = assetMapper.selectList(query);
        String websiteSlug = resolveWebsiteSlug(tenantName);
        return rows.stream()
                .map(row -> TenantWebsiteAssetVoSupport.toVo(row, buildPublicUrl(websiteSlug, row.getId())))
                .collect(Collectors.toList());
    }

    @Override
    public WebsiteAssetVo getById(String id, String tenantName)
    {
        CrmTenantWebsiteAsset row = assetMapper.selectActiveById(id);
        if (row == null)
        {
            throw new ServiceException("Not found", HttpStatus.NOT_FOUND);
        }
        return TenantWebsiteAssetVoSupport.toVo(row, buildPublicUrl(resolveWebsiteSlug(tenantName), id));
    }

    @Override
    public void softDelete(String id)
    {
        CrmTenantWebsiteAsset row = assetMapper.selectActiveById(id);
        if (row == null)
        {
            throw new ServiceException("Not found", HttpStatus.NOT_FOUND);
        }
        if (assetMapper.countSchemaReferences(id) > 0)
        {
            throw new ServiceException("ASSET_IN_USE", HttpStatus.CONFLICT);
        }
        int updated = assetMapper.softDeleteById(id);
        if (updated == 0)
        {
            throw new ServiceException("Not found", HttpStatus.NOT_FOUND);
        }
    }

    private String buildPublicUrl(String tenantNameOrSlug, String assetId)
    {
        if (StringUtils.isBlank(tenantNameOrSlug))
        {
            return publicUrlBuilder.buildPublicPath(assetId);
        }
        return publicUrlBuilder.build(tenantNameOrSlug, assetId);
    }

    private String resolveWebsiteSlug(String tenantName)
    {
        if (StringUtils.isBlank(tenantName))
        {
            return null;
        }
        String previousKey = MasterDataSourceSupport.currentKey();
        try
        {
            MasterDataSourceSupport.bindMasterKey();
            MasterTenant tenant = masterTenantService.selectMasterTenant(tenantName);
            return tenant != null ? tenant.getWebsiteSlug() : null;
        }
        finally
        {
            MasterDataSourceSupport.restoreKey(previousKey);
        }
    }
}
