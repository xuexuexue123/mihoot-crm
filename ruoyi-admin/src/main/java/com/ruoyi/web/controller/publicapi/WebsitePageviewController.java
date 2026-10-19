package com.ruoyi.web.controller.publicapi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ruoyi.common.annotation.RateLimiter;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.LimitType;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.crm.domain.dto.WebsitePageviewRequest;
import com.ruoyi.crm.domain.vo.WebsitePageviewResultVo;
import com.ruoyi.crm.service.ICrmPublicWebsitePageviewService;
import com.ruoyi.framework.datasource.DynamicDataSourceContextHolder;

/**
 * 官网实时 Pageview 公开上报
 */
@RestController
public class WebsitePageviewController extends BaseController
{
    private static final Logger log = LoggerFactory.getLogger(WebsitePageviewController.class);

    @Autowired
    private ICrmPublicWebsitePageviewService publicWebsitePageviewService;

    @RateLimiter(time = 60, count = 60, limitType = LimitType.IP)
    @PostMapping("/website/pageview")
    public AjaxResult submit(@RequestBody WebsitePageviewRequest request)
    {
        try
        {
            WebsitePageviewResultVo result = publicWebsitePageviewService.acceptPageview(request);
            return AjaxResult.success(result);
        }
        catch (ServiceException ex)
        {
            Integer code = ex.getCode();
            if (code != null)
            {
                return AjaxResult.error(code, ex.getMessage());
            }
            return AjaxResult.error(ex.getMessage());
        }
        catch (Exception ex)
        {
            log.error("website pageview failed", ex);
            return AjaxResult.error("pageview 上报失败");
        }
        finally
        {
            DynamicDataSourceContextHolder.clearDataSourceKey();
        }
    }
}
