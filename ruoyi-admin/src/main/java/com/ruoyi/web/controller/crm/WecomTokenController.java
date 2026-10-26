package com.ruoyi.web.controller.crm;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.crm.domain.CrmWecomConfig;
import com.ruoyi.crm.mapper.CrmWecomConfigMapper;
import com.ruoyi.crm.wecom.WecomAccessTokenService;

/**
 * 企业微信 access_token 查询端点，供 Next.js 工作流自动获取 token。
 * tenant header 由 {@code TenantInterceptor} 校验并切换数据源，控制器无需手动处理租户切换。
 */
@RestController
@RequestMapping("/crm/wecom")
public class WecomTokenController extends BaseController
{
    @Autowired
    private WecomAccessTokenService wecomAccessTokenService;

    @Autowired
    private CrmWecomConfigMapper wecomConfigMapper;

    @GetMapping("/token")
    public AjaxResult getToken()
    {
        CrmWecomConfig cfg = wecomConfigMapper.selectFirst();
        if (cfg == null)
        {
            return AjaxResult.error(HttpStatus.BAD_REQUEST, "wecom config not found for tenant");
        }

        String accessToken;
        try
        {
            accessToken = wecomAccessTokenService.getAgentAccessToken(cfg);
        }
        catch (Exception e)
        {
            logger.error("获取 access_token 失败: corpId={}", cfg.getCorpId(), e);
            return AjaxResult.error(HttpStatus.BAD_REQUEST, "failed to get access_token");
        }

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("access_token", accessToken);
        data.put("expires_in", 7200);

        return AjaxResult.success(data);
    }
}
