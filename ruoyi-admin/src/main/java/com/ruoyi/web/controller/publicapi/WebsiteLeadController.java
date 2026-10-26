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
import com.ruoyi.crm.domain.dto.WebsiteLeadSubmitRequest;
import com.ruoyi.crm.domain.vo.WebsiteLeadSubmitResultVo;
import com.ruoyi.crm.service.IWebsiteOnlineRegisterService;
import com.ruoyi.framework.datasource.DynamicDataSourceContextHolder;

/**
 * 官网在线预约公开提交
 */
@RestController
public class WebsiteLeadController extends BaseController
{
    private static final Logger log = LoggerFactory.getLogger(WebsiteLeadController.class);

    @Autowired
    private IWebsiteOnlineRegisterService websiteOnlineRegisterService;

    @RateLimiter(time = 60, count = 10, limitType = LimitType.IP)
    @PostMapping("/websitelead")
    public AjaxResult submit(@RequestBody WebsiteLeadSubmitRequest request)
    {
        try
        {
            WebsiteLeadSubmitResultVo result = websiteOnlineRegisterService.submit(request);
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
            log.error("websitelead submission failed", ex);
            return AjaxResult.error("预约提交失败");
        }
        finally
        {
            DynamicDataSourceContextHolder.clearDataSourceKey();
        }
    }
}
