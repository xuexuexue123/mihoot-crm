package com.ruoyi.web.controller.crm.feishu;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.crm.feishu.FeishuTokenService;
import com.ruoyi.crm.feishu.domain.CrmFeishuConfig;
import com.ruoyi.crm.feishu.service.ICrmFeishuConfigService;

/**
 * 飞书 tenant_access_token 查询端点，供 Next BFF / 工作流获取 token。
 * 不加 @PreAuthorize（对齐 WecomTokenController）；依赖 JWT / 内网工作流鉴权 + tenant header。
 */
@RestController
@RequestMapping("/crm/feishu")
public class CrmFeishuTokenController extends BaseController
{
    @Autowired
    private ICrmFeishuConfigService feishuConfigService;

    @Autowired
    private FeishuTokenService feishuTokenService;

    @GetMapping("/token")
    public AjaxResult getToken()
    {
        CrmFeishuConfig cfg = feishuConfigService.getRawConfig();
        if (cfg == null)
        {
            return AjaxResult.error(HttpStatus.BAD_REQUEST, "请先配置飞书集成");
        }
        if (cfg.getAppSecretEnc() == null || cfg.getAppSecretEnc().isEmpty())
        {
            return AjaxResult.error(HttpStatus.BAD_REQUEST, "请先配置飞书集成：secret 缺失");
        }

        try
        {
            FeishuTokenService.TokenResult result = feishuTokenService.getTenantAccessTokenWithExpiry(cfg);
            Map<String, Object> data = new LinkedHashMap<>();
            data.put("accessToken", result.accessToken);
            data.put("expireAt", Instant.ofEpochMilli(result.expireAtEpochMs).toString());
            return AjaxResult.success(data);
        }
        catch (ServiceException e)
        {
            return AjaxResult.error(HttpStatus.BAD_REQUEST, e.getMessage());
        }
        catch (Exception e)
        {
            logger.error("获取飞书 tenant_access_token 失败: appId={}", cfg.getAppId(), e);
            return AjaxResult.error(HttpStatus.BAD_REQUEST, "failed to get tenant_access_token");
        }
    }
}
