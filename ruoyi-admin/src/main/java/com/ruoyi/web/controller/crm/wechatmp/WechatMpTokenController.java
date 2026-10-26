package com.ruoyi.web.controller.crm.wechatmp;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.crm.wechatmp.service.ICrmWechatMpConfigService;
import com.ruoyi.crm.wechatmp.service.WechatMpTokenService;

/**
 * 微信公众号 access_token 查询端点，供前端调用。
 * 从当前租户数据库读取公众号配置，支持多公众号。
 *
 * @author luohd
 * @date 2026-06-18
 */
@RestController
@RequestMapping({ "/crm/wechat_mp", "/wechat_mp" })
public class WechatMpTokenController extends BaseController
{
    @Autowired
    private WechatMpTokenService wechatMpTokenService;

    @Autowired
    private ICrmWechatMpConfigService wechatMpConfigService;

    /**
     * 获取指定 appId 的 access_token（从当前租户数据库读取 secret）
     */
    @GetMapping("/token/{appId}")
    public AjaxResult getToken(@PathVariable String appId)
    {
        if (appId == null || appId.isEmpty())
        {
            return AjaxResult.error(HttpStatus.BAD_REQUEST, "appId 不能为空");
        }
        try
        {
            String accessToken = wechatMpTokenService.getAccessToken(appId);
            Map<String, String> data = new LinkedHashMap<>();
            data.put("app_id", appId);
            data.put("access_token", accessToken);
            return AjaxResult.success(data);
        }
        catch (Exception e)
        {
            logger.error("获取微信公众号 access_token 失败: appId={}", appId, e);
            return AjaxResult.error(HttpStatus.BAD_REQUEST, "failed to get access_token: " + e.getMessage());
        }
    }

    /**
     * 获取当前租户所有公众号配置摘要（含 appId，不含 secret）
     */
    @GetMapping("/apps")
    public AjaxResult listApps()
    {
        List<Map<String, Object>> configs = wechatMpConfigService.listConfigs();
        List<Map<String, Object>> apps = configs.stream().map(cfg -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("appId", cfg.get("appId"));
            m.put("connected", cfg.get("connected"));
            m.put("lastError", cfg.get("lastError"));
            return m;
        }).collect(Collectors.toList());
        return AjaxResult.success(apps);
    }
}
