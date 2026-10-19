package com.ruoyi.web.controller.publicapi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ruoyi.common.annotation.RateLimiter;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.LimitType;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.crm.service.ICrmPublicTenantWebsiteContentService;
import com.ruoyi.framework.datasource.DynamicDataSourceContextHolder;

/**
 * 租户官网公开营销内容列表 / 详情（website_visible = true）
 */
@RestController
@RequestMapping("/api/public/tenant-sites/{slug}/contents")
public class PublicTenantSiteContentController extends BaseController
{
    private static final Logger log = LoggerFactory.getLogger(PublicTenantSiteContentController.class);

    @Autowired
    private ICrmPublicTenantWebsiteContentService publicTenantWebsiteContentService;

    @RateLimiter(time = 60, count = 60, limitType = LimitType.IP)
    @GetMapping
    public AjaxResult list(@PathVariable String slug,
            @RequestParam(required = false) Integer limit)
    {
        try
        {
            return AjaxResult.success(publicTenantWebsiteContentService.listContents(slug, limit));
        }
        catch (ServiceException ex)
        {
            return toError(ex);
        }
        catch (Exception ex)
        {
            log.error("public contents list failed, slug={}", slug, ex);
            return AjaxResult.error("Failed to load contents");
        }
        finally
        {
            DynamicDataSourceContextHolder.clearDataSourceKey();
        }
    }

    @RateLimiter(time = 60, count = 60, limitType = LimitType.IP)
    @GetMapping("/{contentId}")
    public AjaxResult detail(@PathVariable String slug, @PathVariable String contentId)
    {
        try
        {
            return AjaxResult.success(publicTenantWebsiteContentService.getContent(slug, contentId));
        }
        catch (ServiceException ex)
        {
            return toError(ex);
        }
        catch (Exception ex)
        {
            log.error("public content detail failed, slug={}, contentId={}", slug, contentId, ex);
            return AjaxResult.error("Failed to load content");
        }
        finally
        {
            DynamicDataSourceContextHolder.clearDataSourceKey();
        }
    }

    private AjaxResult toError(ServiceException ex)
    {
        Integer code = ex.getCode();
        if (code != null)
        {
            return AjaxResult.error(code, ex.getMessage());
        }
        return AjaxResult.error(ex.getMessage());
    }
}
