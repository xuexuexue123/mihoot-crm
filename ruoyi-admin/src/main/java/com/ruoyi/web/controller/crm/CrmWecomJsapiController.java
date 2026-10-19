package com.ruoyi.web.controller.crm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.domain.dto.wecom.WecomJsapiAgentSignatureDto;
import com.ruoyi.crm.wecom.WecomJsapiTicketService;

/**
 * 企微 JS-SDK 签名（侧边栏等 H5 场景）。
 */
@RestController
@RequestMapping({ "/crm/wecom", "/wecom" })
public class CrmWecomJsapiController extends BaseController
{
    @Autowired
    private WecomJsapiTicketService jsapiTicketService;

    @GetMapping("/jsapi/agent-signature")
    public AjaxResult agentSignature(@RequestParam(value = "url", required = false) String url)
    {
        return buildSignatureResponse(url, false);
    }

    @GetMapping("/jsapi/corp-signature")
    public AjaxResult corpSignature(@RequestParam(value = "url", required = false) String url)
    {
        return buildSignatureResponse(url, true);
    }

    private AjaxResult buildSignatureResponse(String url, boolean corp)
    {
        if (StringUtils.isEmpty(url))
        {
            return AjaxResult.error(HttpStatus.BAD_REQUEST, "url 不能为空");
        }
        try
        {
            WecomJsapiAgentSignatureDto data = corp
                    ? jsapiTicketService.buildCorpSignature(url)
                    : jsapiTicketService.buildAgentSignature(url);
            return AjaxResult.success(data);
        }
        catch (ServiceException e)
        {
            Integer code = e.getCode();
            if (code != null)
            {
                return AjaxResult.error(code, e.getMessage());
            }
            logger.error("JSAPI 签名失败: url={}", url, e);
            return AjaxResult.error(HttpStatus.ERROR, e.getMessage());
        }
        catch (Exception e)
        {
            logger.error("JSAPI 签名失败: url={}", url, e);
            return AjaxResult.error(HttpStatus.ERROR, "JSAPI 签名失败");
        }
    }
}
