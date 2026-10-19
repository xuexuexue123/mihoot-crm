package com.ruoyi.web.controller.crm.wechatmp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.crm.wechatmp.dto.WechatMpMenuDraftRequest;
import com.ruoyi.crm.wechatmp.dto.WechatMpMenuPublishRequest;
import com.ruoyi.crm.wechatmp.service.IWechatMpArticleService;
import com.ruoyi.crm.wechatmp.service.IWechatMpMenuService;

@RestController
@RequestMapping({ "/crm/wechat_mp", "/wechat_mp" })
public class WechatMpMenuController extends BaseController
{
    @Autowired
    private IWechatMpMenuService menuService;

    @Autowired
    private IWechatMpArticleService articleService;

    @PreAuthorize("@ss.hasPermi('crm:wechat_mp:menu')")
    @GetMapping("/articles")
    public TableDataInfo listArticles(
            @RequestParam String appId,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "20") int count)
    {
        return articleService.listPublishedArticles(appId, offset, count);
    }

    @PreAuthorize("@ss.hasPermi('crm:wechat_mp:menu')")
    @GetMapping("/menu/templates")
    public AjaxResult templates()
    {
        return AjaxResult.success(menuService.listTemplates());
    }

    @PreAuthorize("@ss.hasPermi('crm:wechat_mp:menu')")
    @GetMapping("/menu")
    public AjaxResult getMenu(@RequestParam String appId)
    {
        return AjaxResult.success(menuService.getMenuState(appId));
    }

    @PreAuthorize("@ss.hasPermi('crm:wechat_mp:menu')")
    @Log(title = "公众号菜单草稿", businessType = BusinessType.UPDATE)
    @PostMapping("/menu/draft")
    public AjaxResult saveDraft(@RequestBody WechatMpMenuDraftRequest req)
    {
        menuService.saveDraft(req);
        return AjaxResult.success();
    }

    @PreAuthorize("@ss.hasPermi('crm:wechat_mp:menu')")
    @Log(title = "公众号菜单发布", businessType = BusinessType.UPDATE)
    @PostMapping("/menu/publish")
    public AjaxResult publish(@RequestBody WechatMpMenuPublishRequest req)
    {
        menuService.publish(req);
        return AjaxResult.success();
    }
}
