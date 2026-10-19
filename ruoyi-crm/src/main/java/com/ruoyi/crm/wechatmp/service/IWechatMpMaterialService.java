package com.ruoyi.crm.wechatmp.service;

import org.springframework.web.multipart.MultipartFile;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 微信公众号永久素材（纯代理，不落本地表）
 */
public interface IWechatMpMaterialService
{
    /** 分页查询永久素材列表 */
    TableDataInfo listMaterials(String appId, String type, int offset, int count);

    /** 上传永久素材（image/voice/video/thumb） */
    AjaxResult uploadMaterial(String appId, String type, MultipartFile file, String description);

    /** 删除永久素材 */
    AjaxResult deleteMaterial(String appId, String mediaId);

    /** 获取永久素材详情 */
    AjaxResult getMaterialDetail(String appId, String mediaId, String type);

    /** 获取公众号默认素材 mediaId */
    AjaxResult getDefaultMaterial(String appId);

    /** 设置公众号默认素材 */
    AjaxResult setDefaultMaterial(String appId, String mediaId);

    /** 上传图片素材并一步设为默认（默认封面等场景） */
    AjaxResult uploadAndSetDefaultMaterial(String appId, MultipartFile file);
}
