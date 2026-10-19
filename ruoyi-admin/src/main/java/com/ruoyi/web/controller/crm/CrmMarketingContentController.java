package com.ruoyi.web.controller.crm;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.github.pagehelper.PageHelper;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.crm.domain.CrmMarketingContent;
import com.ruoyi.crm.domain.dto.MarketingContentPublishRefreshRequest;
import com.ruoyi.crm.domain.dto.MarketingContentPublishRequest;
import com.ruoyi.crm.domain.dto.MarketingContentQuery;
import com.ruoyi.crm.domain.vo.MarketingContentListVo;
import com.ruoyi.crm.service.ICrmMarketingContentService;

/**
 * 营销内容管理
 */
@RestController
@RequestMapping("/crm/marketing_content")
public class CrmMarketingContentController extends BaseController
{
    private static final int MAX_PAGE_SIZE = 100;

    @Autowired
    private ICrmMarketingContentService marketingContentService;

    @PreAuthorize("@ss.hasPermi('crm:marketing_content:list')")
    @GetMapping("/list")
    public TableDataInfo list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Boolean published,
            @RequestParam(required = false) Boolean websiteVisible,
            @RequestParam(required = false, defaultValue = "1") Integer pageNum,
            @RequestParam(required = false, defaultValue = "20") Integer pageSize)
    {
        int num = pageNum != null && pageNum > 0 ? pageNum : 1;
        int size = pageSize != null && pageSize > 0 ? pageSize : 20;
        if (size > MAX_PAGE_SIZE)
        {
            size = MAX_PAGE_SIZE;
        }
        PageHelper.startPage(num, size);
        MarketingContentQuery query = new MarketingContentQuery();
        query.setKeyword(keyword);
        query.setStatus(status);
        query.setPublished(published);
        query.setWebsiteVisible(websiteVisible);
        List<MarketingContentListVo> list = marketingContentService.selectList(query);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('crm:marketing_content:query')")
    @GetMapping("/{id}")
    public AjaxResult get(@PathVariable String id)
    {
        return AjaxResult.success(marketingContentService.getDetail(id));
    }

    @PreAuthorize("@ss.hasPermi('crm:marketing_content:add')")
    @Log(title = "营销内容", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult create(@RequestBody CrmMarketingContent request)
    {
        return AjaxResult.success(marketingContentService.create(request));
    }

    @PreAuthorize("@ss.hasPermi('crm:marketing_content:edit')")
    @Log(title = "营销内容", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult update(@RequestBody CrmMarketingContent request)
    {
        return AjaxResult.success(marketingContentService.update(request));
    }

    @PreAuthorize("@ss.hasPermi('crm:marketing_content:remove')")
    @Log(title = "营销内容", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public AjaxResult delete(@PathVariable String id)
    {
        marketingContentService.delete(id);
        return AjaxResult.success();
    }

    @PreAuthorize("@ss.hasPermi('crm:marketing_content:publish')")
    @Log(title = "营销内容发布", businessType = BusinessType.UPDATE)
    @PostMapping("/{id}/publish")
    public AjaxResult publish(@PathVariable String id, @RequestBody MarketingContentPublishRequest request)
    {
        return AjaxResult.success(marketingContentService.publish(id, request));
    }

    @PreAuthorize("@ss.hasPermi('crm:marketing_content:publish')")
    @Log(title = "营销内容发布状态刷新", businessType = BusinessType.UPDATE)
    @PostMapping("/{id}/publish/refresh")
    public AjaxResult refreshPublish(@PathVariable String id,
            @RequestBody MarketingContentPublishRefreshRequest request)
    {
        return AjaxResult.success(marketingContentService.refreshPublish(id, request));
    }

    @PreAuthorize("@ss.hasPermi('crm:marketing_content:query')")
    @GetMapping("/{id}/publishes")
    public AjaxResult listPublishes(@PathVariable String id)
    {
        return AjaxResult.success(marketingContentService.listPublishes(id));
    }

    @PreAuthorize("@ss.hasPermi('crm:marketing_content:edit')")
    @Log(title = "营销内容官网发布", businessType = BusinessType.UPDATE)
    @PostMapping("/{id}/website/publish")
    public AjaxResult publishToWebsite(@PathVariable String id)
    {
        return AjaxResult.success(marketingContentService.publishToWebsite(id));
    }

    @PreAuthorize("@ss.hasPermi('crm:marketing_content:edit')")
    @Log(title = "营销内容官网取消发布", businessType = BusinessType.UPDATE)
    @PostMapping("/{id}/website/unpublish")
    public AjaxResult unpublishFromWebsite(@PathVariable String id)
    {
        return AjaxResult.success(marketingContentService.unpublishFromWebsite(id));
    }
}
