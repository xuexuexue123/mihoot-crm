package com.ruoyi.crm.wechatmp.service.impl;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.crm.wechatmp.WechatMpMaterialFetchResult;
import com.ruoyi.crm.wechatmp.WechatMpTencentApiClient;
import com.ruoyi.crm.wechatmp.domain.CrmWechatMpMaterial;
import com.ruoyi.crm.wechatmp.dto.WechatMpMaterialDetail;
import com.ruoyi.crm.wechatmp.dto.WechatMpMaterialListItem;
import com.ruoyi.crm.wechatmp.dto.WechatMpMaterialUploadResult;
import com.ruoyi.crm.wechatmp.mapper.CrmWechatMpConfigMapper;
import com.ruoyi.crm.wechatmp.mapper.CrmWechatMpMaterialMapper;
import com.ruoyi.crm.wechatmp.service.IWechatMpMaterialService;
import com.ruoyi.crm.wechatmp.service.WechatMpTokenService;
import com.ruoyi.crm.wechatmp.support.WechatMpMaterialMappingSupport;

/**
 * 公众号永久素材服务 —— 微信同步 + 本地缓存。
 * <ul>
 *   <li>读接口（列表 / 详情 / 默认）：只查本地表返回前端</li>
 *   <li>列表读取前做轻量增量同步（video 同步时补拉详情落库）</li>
 *   <li>上传 / 删除：先调微信，再写本地表</li>
 * </ul>
 */
@Service
public class WechatMpMaterialServiceImpl implements IWechatMpMaterialService
{
    private static final Logger log = LoggerFactory.getLogger(WechatMpMaterialServiceImpl.class);

    private static final int MAX_COUNT = 20;
    private static final int SYNC_MAX_BATCHES = 25;
    private static final int ERRCODE_INVALID_MEDIA_ID = 40007;
    private static final int ERRCODE_PERMISSION_DENIED = 48001;

    private static final String MSG_PERMISSION_DENIED = "MP_MATERIAL_PERMISSION_DENIED";
    private static final String MSG_INVALID_MEDIA_ID = "MP_MATERIAL_INVALID_MEDIA_ID";
    private static final String MSG_API_FAILED = "MP_MATERIAL_API_FAILED";

    private static final Set<String> LIST_TYPES = new HashSet<>(Arrays.asList("image", "video", "voice", "news"));
    private static final Set<String> UPLOAD_TYPES = new HashSet<>(Arrays.asList("image", "voice", "video", "thumb"));
    private static final Set<String> DETAIL_TYPES = new HashSet<>(Arrays.asList("image", "video", "voice", "news"));

    private static final Set<String> IMAGE_EXTS = new HashSet<>(Arrays.asList("bmp", "png", "jpeg", "jpg", "gif"));
    private static final Set<String> VOICE_EXTS = new HashSet<>(Arrays.asList("mp3", "wma", "wav", "amr"));
    private static final Set<String> VIDEO_EXTS = new HashSet<>(Arrays.asList("mp4"));
    private static final Set<String> THUMB_EXTS = new HashSet<>(Arrays.asList("jpg"));

    private static final long IMAGE_MAX_BYTES = 10L * 1024 * 1024;
    private static final long VOICE_MAX_BYTES = 2L * 1024 * 1024;
    private static final long VIDEO_MAX_BYTES = 10L * 1024 * 1024;
    private static final long THUMB_MAX_BYTES = 64L * 1024;

    @Autowired
    private WechatMpTokenService tokenService;

    @Autowired
    private WechatMpTencentApiClient tencentApiClient;

    @Autowired
    private CrmWechatMpConfigMapper wechatMpConfigMapper;

    @Autowired
    private CrmWechatMpMaterialMapper materialMapper;

    @Override
    public TableDataInfo listMaterials(String appId, String type, int offset, int count)
    {
        validateAppId(appId);
        if (!StringUtils.hasText(type) || !LIST_TYPES.contains(type.trim()))
        {
            throw new ServiceException("type 必须为 image / video / voice / news", HttpStatus.BAD_REQUEST);
        }

        String safeType = type.trim();
        int safeOffset = Math.max(offset, 0);
        int safeCount = Math.min(Math.max(count, 1), MAX_COUNT);

        try
        {
            syncIncremental(appId, safeType);
        }
        catch (Exception e)
        {
            log.warn("syncIncremental materials failed for appId={} type={}, fallback to DB. {}",
                    appId, safeType, e.getMessage());
        }

        List<CrmWechatMpMaterial> rows = materialMapper.selectPageByAppIdAndType(appId, safeType, safeOffset, safeCount);
        long total = materialMapper.countByAppIdAndType(appId, safeType);

        TableDataInfo data = new TableDataInfo();
        data.setCode(200);
        data.setMsg("success");
        data.setTotal(total);
        data.setRows(WechatMpMaterialMappingSupport.toListItems(rows));
        return data;
    }

    @Override
    public AjaxResult uploadMaterial(String appId, String type, MultipartFile file, String description)
    {
        validateAppId(appId);
        if (!StringUtils.hasText(type))
        {
            return AjaxResult.error(HttpStatus.BAD_REQUEST, "type 不能为空");
        }
        String safeType = type.trim();
        if ("news".equals(safeType))
        {
            return AjaxResult.error(HttpStatus.BAD_REQUEST, "图文素材请使用草稿接口 POST /drafts");
        }
        if (!UPLOAD_TYPES.contains(safeType))
        {
            return AjaxResult.error(HttpStatus.BAD_REQUEST, "type 必须为 image / voice / video / thumb");
        }
        if (file == null || file.isEmpty())
        {
            return AjaxResult.error(HttpStatus.BAD_REQUEST, "上传文件不能为空");
        }

        String validationError = validateUploadFile(safeType, file);
        if (validationError != null)
        {
            return AjaxResult.error(HttpStatus.BAD_REQUEST, validationError);
        }

        java.io.File tempFile = null;
        try
        {
            String originalFilename = file.getOriginalFilename();
            String suffix = "";
            if (originalFilename != null && originalFilename.contains("."))
            {
                suffix = originalFilename.substring(originalFilename.lastIndexOf('.'));
            }
            tempFile = java.io.File.createTempFile("wechat_mp_material_", suffix);
            file.transferTo(tempFile);

            String desc = "video".equals(safeType) ? description : null;
            String token = tokenService.getAccessToken(appId);
            JSONObject res = tencentApiClient.addMaterial(token, safeType, tempFile, desc);
            if (tencentApiClient.isTokenInvalid(res))
            {
                token = tokenService.refreshAccessToken(appId);
                res = tencentApiClient.addMaterial(token, safeType, tempFile, desc);
            }

            if (!isWeChatSuccess(res) || !StringUtils.hasText(res.getString("media_id")))
            {
                return materialApiError(res);
            }

            String mediaId = res.getString("media_id");
            String url = res.getString("url");
            if (url == null)
            {
                url = "";
            }

            // thumb 在列表侧通常归入 image；本地按上传类型落库，thumb 单独存便于封面选用
            String storeType = safeType;
            CrmWechatMpMaterial entity = new CrmWechatMpMaterial();
            entity.setAppId(appId);
            entity.setMediaId(mediaId);
            entity.setType(storeType);
            entity.setName(originalFilename);
            entity.setUrl(url);
            entity.setUpdateTime(OffsetDateTime.now());
            if ("video".equals(safeType) && StringUtils.hasText(description))
            {
                try
                {
                    JSONObject descJson = JSON.parseObject(description);
                    if (descJson != null)
                    {
                        entity.setTitle(descJson.getString("title"));
                        entity.setDescription(descJson.getString("introduction"));
                    }
                }
                catch (Exception ignore)
                {
                    // description 非法 JSON 时忽略，不影响上传成功
                }
            }
            materialMapper.upsert(entity);

            return AjaxResult.success("success", new WechatMpMaterialUploadResult(mediaId, url));
        }
        catch (ServiceException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            log.error("uploadMaterial failed for appId={} type={}", appId, type, e);
            return AjaxResult.error(MSG_API_FAILED + ": " + e.getMessage());
        }
        finally
        {
            if (tempFile != null && tempFile.exists() && !tempFile.delete())
            {
                log.warn("Failed to delete temp file {}", tempFile.getAbsolutePath());
            }
        }
    }

    @Override
    public AjaxResult deleteMaterial(String appId, String mediaId)
    {
        validateAppId(appId);
        if (!StringUtils.hasText(mediaId))
        {
            return AjaxResult.error(HttpStatus.BAD_REQUEST, "mediaId 不能为空");
        }

        String safeMediaId = mediaId.trim();
        String token = tokenService.getAccessToken(appId);
        JSONObject res = tencentApiClient.delMaterial(token, safeMediaId);
        if (tencentApiClient.isTokenInvalid(res))
        {
            token = tokenService.refreshAccessToken(appId);
            res = tencentApiClient.delMaterial(token, safeMediaId);
        }

        if (!isWeChatSuccess(res))
        {
            return materialApiError(res);
        }

        materialMapper.deleteByMediaId(appId, safeMediaId);
        return AjaxResult.success("success");
    }

    @Override
    public AjaxResult getMaterialDetail(String appId, String mediaId, String type)
    {
        validateAppId(appId);
        if (!StringUtils.hasText(mediaId))
        {
            return AjaxResult.error(HttpStatus.BAD_REQUEST, "mediaId 不能为空");
        }
        if (!StringUtils.hasText(type) || !DETAIL_TYPES.contains(type.trim()))
        {
            return AjaxResult.error(HttpStatus.BAD_REQUEST, "type 必须为 image / video / voice / news");
        }

        String safeType = type.trim();
        String safeMediaId = mediaId.trim();

        CrmWechatMpMaterial cached = materialMapper.selectByMediaId(appId, safeMediaId);
        if (cached == null)
        {
            return AjaxResult.error("素材不存在，请先打开列表触发同步");
        }

        WechatMpMaterialDetail detail = WechatMpMaterialMappingSupport.toDetail(cached);
        detail.setType(safeType);
        // image / voice：本地无 contentType 时给前端约定默认值（预览仍用列表 url）
        if (("image".equals(safeType) || "voice".equals(safeType)) && !StringUtils.hasText(detail.getContentType()))
        {
            detail.setContentType("image".equals(safeType) ? "image/jpeg" : "audio/mpeg");
        }
        return AjaxResult.success("success", detail);
    }

    @Override
    public AjaxResult getDefaultMaterial(String appId)
    {
        validateAppId(appId);
        CrmWechatMpMaterial row = materialMapper.selectDefaultByAppId(appId.trim());
        if (row == null || !StringUtils.hasText(row.getMediaId()))
        {
            return AjaxResult.error("未配置默认素材");
        }
        java.util.Map<String, String> data = new java.util.HashMap<>(2);
        data.put("mediaId", row.getMediaId());
        return AjaxResult.success("success", data);
    }

    @Override
    public AjaxResult setDefaultMaterial(String appId, String mediaId)
    {
        validateAppId(appId);
        if (!StringUtils.hasText(mediaId))
        {
            return AjaxResult.error(HttpStatus.BAD_REQUEST, "mediaId 不能为空");
        }
        String safeAppId = appId.trim();
        String safeMediaId = mediaId.trim();
        CrmWechatMpMaterial existing = materialMapper.selectByMediaId(safeAppId, safeMediaId);
        if (existing == null)
        {
            return AjaxResult.error(HttpStatus.BAD_REQUEST, "素材不存在，请先同步或上传后再设为默认");
        }
        materialMapper.clearDefaultByAppId(safeAppId);
        int updated = materialMapper.setDefaultByMediaId(safeAppId, safeMediaId);
        if (updated <= 0)
        {
            return AjaxResult.error("设置默认素材失败");
        }
        java.util.Map<String, String> data = new java.util.HashMap<>(2);
        data.put("mediaId", safeMediaId);
        return AjaxResult.success("success", data);
    }

    @Override
    public AjaxResult uploadAndSetDefaultMaterial(String appId, MultipartFile file)
    {
        AjaxResult uploadResult = uploadMaterial(appId, "image", file, null);
        if (uploadResult.get("code") != null && (int) uploadResult.get("code") != 200)
        {
            return uploadResult;
        }
        WechatMpMaterialUploadResult uploadData = (WechatMpMaterialUploadResult) uploadResult.get("data");
        if (uploadData == null || !StringUtils.hasText(uploadData.getMediaId()))
        {
            return AjaxResult.error("上传成功但未获取到 mediaId");
        }
        // 先清除旧默认，再将新素材标为默认
        materialMapper.clearDefaultByAppId(appId.trim());
        materialMapper.setDefaultByMediaId(appId.trim(), uploadData.getMediaId());

        java.util.Map<String, Object> result = new java.util.HashMap<>(2);
        result.put("mediaId", uploadData.getMediaId());
        result.put("url", uploadData.getUrl());
        return AjaxResult.success("success", result);
    }

    // ======================== 增量同步 ========================

    private void syncIncremental(String appId, String type)
    {
        OffsetDateTime maxUpdateTime = materialMapper.selectMaxUpdateTime(appId, type);

        String token = tokenService.getAccessToken(appId);
        boolean tokenRefreshed = false;
        int offset = 0;
        int batchCount = 0;

        while (batchCount < SYNC_MAX_BATCHES)
        {
            JSONObject res = tencentApiClient.batchGetMaterial(token, type, offset, MAX_COUNT);
            if (tencentApiClient.isTokenInvalid(res) && !tokenRefreshed)
            {
                token = tokenService.refreshAccessToken(appId);
                tokenRefreshed = true;
                res = tencentApiClient.batchGetMaterial(token, type, offset, MAX_COUNT);
            }
            if (!isWeChatSuccess(res))
            {
                log.warn("batchget material returned error for appId={} type={}, errcode={}, errmsg={}",
                        appId, type, res.getIntValue("errcode"), res.getString("errmsg"));
                break;
            }

            JSONArray items = res.getJSONArray("item");
            if (items == null || items.isEmpty())
            {
                break;
            }

            List<WechatMpMaterialListItem> mapped = WechatMpMaterialMappingSupport.mapListItems(res, type);
            List<WechatMpMaterialListItem> toSync = new ArrayList<>();
            boolean reachedExisting = false;
            for (WechatMpMaterialListItem item : mapped)
            {
                if (maxUpdateTime != null
                        && item.getUpdateTime() != null
                        && !item.getUpdateTime().isAfter(maxUpdateTime))
                {
                    reachedExisting = true;
                    break;
                }
                toSync.add(item);
            }

            if (!toSync.isEmpty())
            {
                List<CrmWechatMpMaterial> entities = WechatMpMaterialMappingSupport.toEntityList(appId, toSync);
                // video：batchget 无 down_url，同步时补拉详情再落库，读接口只查 DB
                if ("video".equals(type))
                {
                    for (CrmWechatMpMaterial entity : entities)
                    {
                        enrichVideoDetail(appId, token, tokenRefreshed, entity);
                    }
                }
                materialMapper.batchUpsert(entities);
            }

            long totalCount = WechatMpMaterialMappingSupport.extractTotalCount(res);
            offset += items.size();
            batchCount++;

            if (reachedExisting || offset >= totalCount)
            {
                break;
            }
        }
    }

    /**
     * 同步阶段补全视频详情（title / description / downUrl），写入 entity 后由调用方 upsert。
     */
    private void enrichVideoDetail(String appId, String token, boolean tokenRefreshed, CrmWechatMpMaterial entity)
    {
        try
        {
            String tk = token;
            WechatMpMaterialFetchResult result = tencentApiClient.getMaterial(tk, entity.getMediaId());
            if (result.isError() && result.isTokenInvalid() && !tokenRefreshed)
            {
                tk = tokenService.refreshAccessToken(appId);
                result = tencentApiClient.getMaterial(tk, entity.getMediaId());
            }
            if (result.isError() || !result.isJson() || result.getJson() == null)
            {
                log.warn("enrich video detail failed mediaId={} errcode={}",
                        entity.getMediaId(), result.isError() ? result.getErrcode() : -1);
                return;
            }
            JSONObject json = result.getJson();
            entity.setTitle(json.getString("title"));
            entity.setDescription(json.getString("description"));
            entity.setDownUrl(json.getString("down_url"));
        }
        catch (Exception e)
        {
            log.warn("enrich video detail exception mediaId={}: {}", entity.getMediaId(), e.getMessage());
        }
    }

    private void validateAppId(String appId)
    {
        if (!StringUtils.hasText(appId))
        {
            throw new ServiceException("appId 不能为空", HttpStatus.BAD_REQUEST);
        }
        if (wechatMpConfigMapper.selectByAppId(appId.trim()) == null)
        {
            throw new ServiceException("appId 不属于当前租户", HttpStatus.FORBIDDEN);
        }
    }

    private String validateUploadFile(String type, MultipartFile file)
    {
        String ext = extractExtension(file.getOriginalFilename());
        long size = file.getSize();

        switch (type)
        {
            case "image":
                if (!IMAGE_EXTS.contains(ext))
                {
                    return "image 仅支持 bmp / png / jpeg / jpg / gif";
                }
                if (size > IMAGE_MAX_BYTES)
                {
                    return "image 大小不能超过 10MB";
                }
                break;
            case "voice":
                if (!VOICE_EXTS.contains(ext))
                {
                    return "voice 仅支持 mp3 / wma / wav / amr";
                }
                if (size > VOICE_MAX_BYTES)
                {
                    return "voice 大小不能超过 2MB";
                }
                break;
            case "video":
                if (!VIDEO_EXTS.contains(ext))
                {
                    return "video 仅支持 mp4";
                }
                if (size > VIDEO_MAX_BYTES)
                {
                    return "video 大小不能超过 10MB";
                }
                break;
            case "thumb":
                if (!THUMB_EXTS.contains(ext))
                {
                    return "thumb 仅支持 jpg";
                }
                if (size > THUMB_MAX_BYTES)
                {
                    return "thumb 大小不能超过 64KB";
                }
                break;
            default:
                return "不支持的 type";
        }
        return null;
    }

    private static String extractExtension(String filename)
    {
        if (filename == null || !filename.contains("."))
        {
            return "";
        }
        return filename.substring(filename.lastIndexOf('.') + 1).toLowerCase(Locale.ROOT);
    }

    private boolean isWeChatSuccess(JSONObject res)
    {
        return res != null && res.getIntValue("errcode") == 0;
    }

    private AjaxResult materialApiError(JSONObject res)
    {
        int errcode = res != null ? res.getIntValue("errcode") : -1;
        String errmsg = res != null ? res.getString("errmsg") : "unknown";
        if (errcode == ERRCODE_PERMISSION_DENIED)
        {
            return AjaxResult.error(MSG_PERMISSION_DENIED + " errcode=" + errcode + " errmsg=" + errmsg);
        }
        if (errcode == ERRCODE_INVALID_MEDIA_ID)
        {
            return AjaxResult.error(MSG_INVALID_MEDIA_ID + " errcode=" + errcode + " errmsg=" + errmsg);
        }
        return AjaxResult.error(MSG_API_FAILED + " errcode=" + errcode + " errmsg=" + errmsg);
    }
}
