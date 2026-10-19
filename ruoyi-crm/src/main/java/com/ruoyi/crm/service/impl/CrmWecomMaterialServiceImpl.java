package com.ruoyi.crm.service.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.uuid.IdUtils;
import com.ruoyi.crm.domain.CrmWecomConfig;
import com.ruoyi.crm.domain.CrmWecomMaterial;
import com.ruoyi.crm.domain.dto.wecom.WecomMaterialQueryRequest;
import com.ruoyi.crm.mapper.CrmWecomConfigMapper;
import com.ruoyi.crm.mapper.CrmWecomMaterialMapper;
import com.ruoyi.crm.service.ICrmWecomMaterialService;
import com.ruoyi.crm.wecom.WecomAccessTokenService;
import com.ruoyi.crm.wecom.WecomMediaUploadResult;
import com.ruoyi.crm.wecom.WecomTencentApiClient;

@Service
public class CrmWecomMaterialServiceImpl implements ICrmWecomMaterialService
{
    private static final Logger log = LoggerFactory.getLogger(CrmWecomMaterialServiceImpl.class);

    /** media_id 有效期 3 天（秒） */
    private static final long MEDIA_ID_TTL_SECONDS = 3 * 24 * 60 * 60;

    /** 上传子目录 */
    private static final String UPLOAD_SUB_DIR = "upload";

    @Autowired
    private CrmWecomMaterialMapper crmWecomMaterialMapper;

    @Autowired
    private CrmWecomConfigMapper crmWecomConfigMapper;

    @Autowired
    private WecomAccessTokenService wecomAccessTokenService;

    @Autowired
    private WecomTencentApiClient wecomTencentApiClient;

    @Override
    public CrmWecomMaterial upload(MultipartFile file, String fileTitle, String mediaType, Integer attachmentType)
    {
        if (file == null || file.isEmpty())
        {
            throw new ServiceException("上传文件不能为空");
        }
        if (StringUtils.isEmpty(mediaType))
        {
            throw new ServiceException("素材类型 mediaType 不能为空");
        }

        // 确保 attachmentType 有默认值
        int attType = (attachmentType != null) ? attachmentType : 0;

        String originalName = file.getOriginalFilename();

        // ... keep existing file-save logic below ...
        String extension = FilenameUtils.getExtension(originalName);
        if (StringUtils.isEmpty(extension))
        {
            extension = "bin";
        }

        String storeFileName = IdUtils.fastUUID() + "." + extension.toLowerCase();
        String datePath = DateUtils.datePath();
        String relativePath = UPLOAD_SUB_DIR + "/" + datePath;
        String uploadDir = RuoYiConfig.getProfile() + File.separator + relativePath;
        File uploadDirFile = new File(uploadDir);
        if (!uploadDirFile.exists())
        {
            uploadDirFile.mkdirs();
        }

        try
        {
            File destFile = new File(uploadDirFile, storeFileName);
            file.transferTo(destFile);
        }
        catch (IOException e)
        {
            log.error("素材文件保存失败: {}", e.getMessage(), e);
            throw new ServiceException("文件保存失败: " + e.getMessage());
        }

        CrmWecomMaterial material = new CrmWecomMaterial();
        material.setFileTitle(StringUtils.isNotEmpty(fileTitle) ? fileTitle : originalName);
        material.setFileName(relativePath + "/" + storeFileName);
        material.setOriginalName(originalName);
        material.setFileType(extension.toLowerCase());
        material.setFileSize(file.getSize());
        material.setMediaType(mediaType);
        material.setAttachmentType(attType);
        material.setStatus("pending");
        material.setCreateBy(SecurityUtils.getUsername());
        material.setCreateTime(new Date());

        crmWecomMaterialMapper.insert(material);
        return material;
    }

    @Override
    public CrmWecomMaterial getById(Long id)
    {
        CrmWecomMaterial material = crmWecomMaterialMapper.selectById(id);
        if (material == null)
        {
            throw new ServiceException("素材记录不存在: id=" + id);
        }
        return material;
    }

    @Override
    public List<CrmWecomMaterial> list(WecomMaterialQueryRequest query)
    {
        updateExpiredStatus();
        return crmWecomMaterialMapper.selectPage(query);
    }

    @Override
    public CrmWecomMaterial uploadToWecom(Long id)
    {
        CrmWecomMaterial material = crmWecomMaterialMapper.selectById(id);
        if (material == null)
        {
            throw new ServiceException("素材记录不存在: id=" + id);
        }

        String currentStatus = material.getStatus();
        boolean canUpload = "pending".equals(currentStatus)
                || "upload_failed".equals(currentStatus)
                || "expired".equals(currentStatus)
                || ("uploaded".equals(currentStatus) && isExpired(material.getExpiresAt()));
        if (!canUpload)
        {
            throw new ServiceException("当前素材状态不允许上传到企业微信: " + currentStatus);
        }

        // 获取 access_token
        CrmWecomConfig config = crmWecomConfigMapper.selectFirst();
        if (config == null)
        {
            throw new ServiceException("企业微信未配置，请先完成企业微信集成配置");
        }
        String accessToken = wecomAccessTokenService.getAgentAccessToken(config);

        // 构建本地文件路径
        String uploadProfile = RuoYiConfig.getProfile();
        File localFile = new File(uploadProfile, material.getFileName());
        if (!localFile.exists())
        {
            throw new ServiceException("素材文件不存在: " + material.getFileName());
        }

        // 调用企微 API 上传
        WecomMediaUploadResult result;
        if (material.getAttachmentType() != null && material.getAttachmentType() != 0)
        {
            result = wecomTencentApiClient.uploadAttachment(accessToken, material.getMediaType(), localFile, material.getAttachmentType());
        }
        else
        {
            result = wecomTencentApiClient.uploadMedia(accessToken, material.getMediaType(), localFile);
        }

        if (result.isSuccess())
        {
            // 计算过期时间
            long createdAt = result.getCreatedAt();
            Date expiresAt = new Date((createdAt + MEDIA_ID_TTL_SECONDS) * 1000L);

            crmWecomMaterialMapper.updateWecomUploadResult(
                    id,
                    "uploaded",
                    result.getMediaId(),
                    createdAt,
                    expiresAt,
                    new Date(),
                    null
            );

            material.setStatus("uploaded");
            material.setMediaId(result.getMediaId());
            material.setMediaCreatedAt(createdAt);
            material.setExpiresAt(expiresAt);
            material.setWecomUploadedAt(new Date());
            material.setErrmsg(null);
        }
        else
        {
            String errmsg = result.getErrmsg();
            if (result.getErrcode() == 41081 && material.getAttachmentType() != null && material.getAttachmentType() == 1)
            {
                errmsg = "朋友圈类型图片，长边不超过10800像素，短边不超过1080像素。";
            }
            crmWecomMaterialMapper.updateWecomUploadResult(
                    id,
                    "upload_failed",
                    null,
                    null,
                    null,
                    null,
                    errmsg
            );

            material.setStatus("upload_failed");
            material.setErrmsg(errmsg);
            throw new ServiceException("上传到企业微信失败: " + errmsg);
        }

        return material;
    }

    @Override
    public String getDownloadUrl(Long id)
    {
        CrmWecomMaterial material = crmWecomMaterialMapper.selectById(id);
        if (material == null)
        {
            throw new ServiceException("素材记录不存在: id=" + id);
        }
        return Constants.RESOURCE_PREFIX + "/" + material.getFileName();
    }

    @Override
    public void delete(Long id)
    {
        CrmWecomMaterial material = crmWecomMaterialMapper.selectById(id);
        if (material == null)
        {
            throw new ServiceException("素材记录不存在: id=" + id);
        }

        // 删除服务器文件（best-effort）
        try
        {
            String uploadProfile = RuoYiConfig.getProfile();
            File localFile = new File(uploadProfile, material.getFileName());
            if (localFile.exists())
            {
                localFile.delete();
            }
        }
        catch (Exception e)
        {
            log.warn("删除素材文件失败（非致命）: {}", e.getMessage());
        }

        crmWecomMaterialMapper.deleteById(id);
    }

    @Override
    public void updateExpiredStatus()
    {
        List<CrmWecomMaterial> expiredList = crmWecomMaterialMapper.selectExpiredUploaded();
        if (expiredList == null || expiredList.isEmpty())
        {
            return;
        }

        List<Long> ids = new ArrayList<>();
        for (CrmWecomMaterial m : expiredList)
        {
            ids.add(m.getId());
        }

        int count = crmWecomMaterialMapper.batchUpdateExpired(ids);
        log.info("定时任务：更新过期素材状态，共 {} 条", count);
    }

    private boolean isExpired(Date expiresAt)
    {
        return expiresAt != null && expiresAt.before(new Date());
    }
}
