package com.ruoyi.web.controller.crm.wechatmp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.crm.wechatmp.dto.WechatMpTagBatchRequest;
import com.ruoyi.crm.wechatmp.dto.WechatMpTagCreateRequest;
import com.ruoyi.crm.wechatmp.dto.WechatMpTagUpdateRequest;
import com.ruoyi.crm.wechatmp.service.IWechatMpTagService;

/**
 * 微信公众号标签管理
 *
 * @author luohd
 * @date 2026-07-17
 */
@RestController
@RequestMapping({ "/crm/wechat_mp", "/wechat_mp" })
public class WechatMpTagController extends BaseController
{
    @Autowired
    private IWechatMpTagService tagService;

    @PreAuthorize("@ss.hasPermi('crm:wechat_mp:tags')")
    @GetMapping("/tags")
    public AjaxResult listTags(@RequestParam String appId)
    {
        return tagService.listTags(appId);
    }

    @PreAuthorize("@ss.hasPermi('crm:wechat_mp:tags')")
    @Log(title = "公众号标签创建", businessType = BusinessType.INSERT)
    @PostMapping("/tags")
    public AjaxResult createTag(
            @RequestParam String appId,
            @RequestBody WechatMpTagCreateRequest request)
    {
        return tagService.createTag(appId, request);
    }

    @PreAuthorize("@ss.hasPermi('crm:wechat_mp:tags')")
    @Log(title = "公众号标签编辑", businessType = BusinessType.UPDATE)
    @PutMapping("/tags")
    public AjaxResult updateTag(
            @RequestParam String appId,
            @RequestBody WechatMpTagUpdateRequest request)
    {
        return tagService.updateTag(appId, request);
    }

    @PreAuthorize("@ss.hasPermi('crm:wechat_mp:tags')")
    @Log(title = "公众号标签删除", businessType = BusinessType.DELETE)
    @DeleteMapping("/tags")
    public AjaxResult deleteTag(
            @RequestParam String appId,
            @RequestParam Long tagId)
    {
        return tagService.deleteTag(appId, tagId);
    }

    @PreAuthorize("@ss.hasPermi('crm:wechat_mp:tags')")
    @Log(title = "公众号批量打标签", businessType = BusinessType.UPDATE)
    @PostMapping("/tags/batch-tagging")
    public AjaxResult batchTagging(
            @RequestParam String appId,
            @RequestBody WechatMpTagBatchRequest request)
    {
        return tagService.batchTagging(appId, request);
    }

    @PreAuthorize("@ss.hasPermi('crm:wechat_mp:tags')")
    @Log(title = "公众号批量取消标签", businessType = BusinessType.UPDATE)
    @PostMapping("/tags/batch-untagging")
    public AjaxResult batchUntagging(
            @RequestParam String appId,
            @RequestBody WechatMpTagBatchRequest request)
    {
        return tagService.batchUntagging(appId, request);
    }

    @PreAuthorize("@ss.hasPermi('crm:wechat_mp:tags')")
    @GetMapping("/tags/user")
    public AjaxResult getUserTags(
            @RequestParam String appId,
            @RequestParam String openId)
    {
        return tagService.getUserTags(appId, openId);
    }

    @PreAuthorize("@ss.hasPermi('crm:wechat_mp:tags')")
    @GetMapping("/tags/fans")
    public AjaxResult getTagFans(
            @RequestParam String appId,
            @RequestParam Long tagId,
            @RequestParam(required = false) String nextOpenId)
    {
        return tagService.getTagFans(appId, tagId, nextOpenId);
    }
}
