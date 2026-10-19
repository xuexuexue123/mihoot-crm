package com.ruoyi.web.controller.publicapi;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ruoyi.common.annotation.RateLimiter;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.LimitType;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.crm.domain.dto.CaptureSubmitRequest;
import com.ruoyi.crm.domain.vo.CaptureSubmitResultVo;
import com.ruoyi.crm.domain.vo.PublicCaptureDefinitionVo;
import com.ruoyi.crm.service.ICrmPublicCaptureService;
import com.ruoyi.framework.datasource.DynamicDataSourceContextHolder;

/**
 * Captures 公开定义与提交
 */
@RestController
@RequestMapping("/public/captures")
public class PublicCaptureController extends BaseController
{
    private static final Logger log = LoggerFactory.getLogger(PublicCaptureController.class);

    @Autowired
    private ICrmPublicCaptureService publicCaptureService;

    @RateLimiter(time = 60, count = 60, limitType = LimitType.IP)
    @GetMapping("/{captureId}")
    public AjaxResult getDefinition(@PathVariable String captureId,
            @RequestParam(value = "tenantSlug", required = false) String tenantSlug,
            HttpServletRequest httpRequest)
    {
        try
        {
            PublicCaptureDefinitionVo result = publicCaptureService.getPublished(
                    captureId, tenantSlug, httpRequest.getHeader("Origin"));
            return AjaxResult.success(result);
        }
        catch (ServiceException ex)
        {
            return toError(ex);
        }
        catch (Exception ex)
        {
            log.error("public capture get failed captureId={}", captureId, ex);
            return AjaxResult.error("获取表单失败");
        }
        finally
        {
            DynamicDataSourceContextHolder.clearDataSourceKey();
        }
    }

    @RateLimiter(time = 60, count = 10, limitType = LimitType.IP)
    @PostMapping("/{captureId}/submit")
    public AjaxResult submit(@PathVariable String captureId,
            @RequestBody CaptureSubmitRequest request,
            HttpServletRequest httpRequest)
    {
        try
        {
            CaptureSubmitResultVo result = publicCaptureService.submit(
                    captureId, request, httpRequest.getHeader("Origin"));
            return AjaxResult.success(result);
        }
        catch (ServiceException ex)
        {
            return toError(ex);
        }
        catch (Exception ex)
        {
            log.error("public capture submit failed captureId={}", captureId, ex);
            return AjaxResult.error("提交失败");
        }
        finally
        {
            DynamicDataSourceContextHolder.clearDataSourceKey();
        }
    }

    private static AjaxResult toError(ServiceException ex)
    {
        Integer code = ex.getCode();
        if (code != null)
        {
            return AjaxResult.error(code, ex.getMessage());
        }
        return AjaxResult.error(ex.getMessage());
    }
}
