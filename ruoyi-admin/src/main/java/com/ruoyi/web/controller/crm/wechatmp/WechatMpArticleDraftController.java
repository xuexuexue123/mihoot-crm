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
import org.springframework.web.multipart.MultipartFile;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.crm.wechatmp.dto.WechatMpDraftAddRequest;
import com.ruoyi.crm.wechatmp.dto.WechatMpDraftSyncRequest;
import com.ruoyi.crm.wechatmp.dto.WechatMpDraftUpdateRequest;
import com.ruoyi.crm.wechatmp.service.IWechatMpArticleDraftService;
import com.ruoyi.crm.wechatmp.service.IWechatMpArticleStatService;

/**
 * 微信公众号草稿文章管理
 */
@RestController
@RequestMapping({ "/crm/wechat_mp", "/wechat_mp" })
public class WechatMpArticleDraftController extends BaseController
{
    @Autowired
    private IWechatMpArticleDraftService draftService;

    @Autowired
    private IWechatMpArticleStatService articleStatService;

    @PreAuthorize("@ss.hasPermi('crm:wechat_mp:menu')")
    @GetMapping("/drafts")
    public TableDataInfo listDrafts(
            @RequestParam String appId,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "20") int count)
    {
        return draftService.listDrafts(appId, offset, count);
    }

    @PreAuthorize("@ss.hasPermi('crm:wechat_mp:menu')")
    @GetMapping("/drafts/detail")
    public AjaxResult getDraftDetail(
            @RequestParam String appId,
            @RequestParam String mediaId,
            @RequestParam(defaultValue = "false") boolean localOnly)
    {
        return draftService.getDraftDetail(appId, mediaId, localOnly);
    }

    @PreAuthorize("@ss.hasPermi('crm:wechat_mp:menu')")
    @Log(title = "公众号草稿新增", businessType = BusinessType.INSERT)
    @PostMapping("/drafts")
    public AjaxResult createDraft(
            @RequestParam String appId,
            @RequestParam(defaultValue = "false") boolean localOnly,
            @RequestBody WechatMpDraftAddRequest request)
    {
        return draftService.createDraft(appId, request, localOnly);
    }

    @PreAuthorize("@ss.hasPermi('crm:wechat_mp:menu')")
    @Log(title = "公众号草稿修改", businessType = BusinessType.UPDATE)
    @PutMapping("/drafts")
    public AjaxResult updateDraft(
            @RequestParam String appId,
            @RequestParam String mediaId,
            @RequestParam(defaultValue = "false") boolean localOnly,
            @RequestBody WechatMpDraftUpdateRequest request)
    {
        return draftService.updateDraft(appId, mediaId, request, localOnly);
    }

    @PreAuthorize("@ss.hasPermi('crm:wechat_mp:menu')")
    @Log(title = "公众号草稿删除", businessType = BusinessType.DELETE)
    @DeleteMapping("/drafts")
    public AjaxResult deleteDraft(
            @RequestParam String appId,
            @RequestParam String mediaId,
            @RequestParam(defaultValue = "false") boolean localOnly)
    {
        return draftService.deleteDraft(appId, mediaId, localOnly);
    }

    @PreAuthorize("@ss.hasPermi('crm:wechat_mp:menu')")
    @Log(title = "公众号草稿同步", businessType = BusinessType.UPDATE)
    @PostMapping("/drafts/sync")
    public AjaxResult syncDraft(
            @RequestParam String appId,
            @RequestParam String mediaId,
            @RequestBody(required = false) WechatMpDraftSyncRequest body)
    {
        return draftService.syncDraft(appId, mediaId, body);
    }

    @PreAuthorize("@ss.hasPermi('crm:wechat_mp:menu')")
    @PostMapping("/drafts/fetch")
    public AjaxResult fetchDraftsFromWechat(@RequestParam String appId)
    {
        return draftService.fetchDraftsFromWechat(appId);
    }

    @PreAuthorize("@ss.hasPermi('crm:wechat_mp:menu')")
    @Log(title = "公众号草稿发布", businessType = BusinessType.UPDATE)
    @PostMapping("/drafts/publish")
    public AjaxResult publishDraft(
            @RequestParam String appId,
            @RequestParam String mediaId)
    {
        return draftService.publishDraft(appId, mediaId);
    }

    @PreAuthorize("@ss.hasPermi('crm:wechat_mp:menu')")
    @Log(title = "公众号草稿发布状态更新", businessType = BusinessType.UPDATE)
    @PutMapping("/drafts/published")
    public AjaxResult updatePublished(
            @RequestParam String appId,
            @RequestParam String mediaId,
            @RequestParam boolean published)
    {
        return draftService.updatePublished(appId, mediaId, published);
    }

    @PreAuthorize("@ss.hasPermi('crm:wechat_mp:menu')")
    @Log(title = "公众号上传图文图片", businessType = BusinessType.OTHER)
    @PostMapping("/media/uploadimg")
    public AjaxResult uploadImage(
            @RequestParam String appId,
            @RequestParam("media") MultipartFile file)
    {
        return draftService.uploadImage(appId, file);
    }

    @PreAuthorize("@ss.hasPermi('crm:wechat_mp:menu')")
    @Log(title = "公众号文章统计拉取", businessType = BusinessType.OTHER)
    @PostMapping("/articles/stat/fetch")
    public AjaxResult fetchArticleStat(
            @RequestParam String appId,
            @RequestParam String beginDate,
            @RequestParam String endDate)
    {
        return articleStatService.fetchArticleStat(appId, beginDate, endDate);
    }

    @PreAuthorize("@ss.hasPermi('crm:wechat_mp:menu')")
    @Log(title = "公众号发布状态查询", businessType = BusinessType.OTHER)
    @PostMapping("/articles/publish/status")
    public AjaxResult queryPublishStatus(
            @RequestParam String appId,
            @RequestParam String publishId)
    {
        return draftService.queryPublishStatus(appId, publishId);
    }

    @PreAuthorize("@ss.hasPermi('crm:wechat_mp:menu')")
    @Log(title = "公众号批量刷新发布状态", businessType = BusinessType.OTHER)
    @PostMapping("/articles/publish/allpendingstatus")
    public AjaxResult refreshAllPendingStatus(@RequestParam String appId)
    {
        return draftService.refreshAllPendingStatus(appId);
    }
}
