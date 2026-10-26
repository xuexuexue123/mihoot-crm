package com.ruoyi.web.controller.crm.wechatmp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.crm.wechatmp.service.IWechatMpMaterialService;

/**
 * 微信公众号永久素材管理（本地缓存 + 微信同步）
 */
@RestController
@RequestMapping({ "/crm/wechat_mp", "/wechat_mp" })
public class WechatMpMaterialController extends BaseController
{
    @Autowired
    private IWechatMpMaterialService materialService;

    @PreAuthorize("@ss.hasPermi('crm:wechat_mp:menu')")
    @GetMapping("/materials")
    public TableDataInfo listMaterials(
            @RequestParam String appId,
            @RequestParam String type,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "20") int count)
    {
        return materialService.listMaterials(appId, type, offset, count);
    }

    @PreAuthorize("@ss.hasPermi('crm:wechat_mp:menu')")
    @GetMapping("/materials/default")
    public AjaxResult getDefaultMaterial(@RequestParam String appId)
    {
        return materialService.getDefaultMaterial(appId);
    }

    @PreAuthorize("@ss.hasPermi('crm:wechat_mp:menu')")
    @Log(title = "公众号上传并设为默认素材", businessType = BusinessType.INSERT)
    @PostMapping("/materials/default")
    public AjaxResult uploadAndSetDefaultMaterial(
            @RequestParam String appId,
            @RequestParam("media") MultipartFile media)
    {
        return materialService.uploadAndSetDefaultMaterial(appId, media);
    }

    @PreAuthorize("@ss.hasPermi('crm:wechat_mp:menu')")
    @Log(title = "公众号设置默认素材", businessType = BusinessType.UPDATE)
    @PutMapping("/materials/default")
    public AjaxResult setDefaultMaterial(
            @RequestParam String appId,
            @RequestParam String mediaId)
    {
        return materialService.setDefaultMaterial(appId, mediaId);
    }

    @PreAuthorize("@ss.hasPermi('crm:wechat_mp:menu')")
    @Log(title = "公众号永久素材上传", businessType = BusinessType.INSERT)
    @PostMapping("/materials")
    public AjaxResult uploadMaterial(
            @RequestParam String appId,
            @RequestParam String type,
            @RequestParam("media") MultipartFile media,
            @RequestParam(required = false) String description)
    {
        return materialService.uploadMaterial(appId, type, media, description);
    }

    @PreAuthorize("@ss.hasPermi('crm:wechat_mp:menu')")
    @Log(title = "公众号永久素材删除", businessType = BusinessType.DELETE)
    @DeleteMapping("/materials")
    public AjaxResult deleteMaterial(
            @RequestParam String appId,
            @RequestParam String mediaId)
    {
        return materialService.deleteMaterial(appId, mediaId);
    }

    @PreAuthorize("@ss.hasPermi('crm:wechat_mp:menu')")
    @GetMapping("/materials/detail")
    public AjaxResult getMaterialDetail(
            @RequestParam String appId,
            @RequestParam String mediaId,
            @RequestParam String type)
    {
        return materialService.getMaterialDetail(appId, mediaId, type);
    }
}
