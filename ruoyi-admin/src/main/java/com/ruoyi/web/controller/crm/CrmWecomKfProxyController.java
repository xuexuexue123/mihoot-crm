package com.ruoyi.web.controller.crm;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.domain.CrmWecomConfig;
import com.ruoyi.crm.mapper.CrmWecomConfigMapper;
import com.ruoyi.crm.wecom.WecomAccessTokenService;

/**
 * 微信客服统一 API 代理 Controller。
 * 所有 KF 操作（发送消息、账号管理、接待人员管理、客户信息查询）通过此类转发到企业微信 API，
 * access_token 由后端自动管理，前端无需手动传入。
 */
@RestController
@RequestMapping("/crm/wecom/kf")
public class CrmWecomKfProxyController extends BaseController
{
    @Autowired
    private WecomAccessTokenService wecomAccessTokenService;

    @Autowired
    private CrmWecomConfigMapper wecomConfigMapper;

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * 路径映射表：前端 operationPath → 企业微信 KF API 路径（不含 base URL）。
     * 部分前端 operationPath 与 WeChat API 实际路径不一致，需要映射。
     */
    private static final Map<String, String> PATH_MAP = new LinkedHashMap<>();

    static
    {
        PATH_MAP.put("send_msg", "kf/send_msg");
        PATH_MAP.put("account/add", "kf/account/add");
        PATH_MAP.put("account/del", "kf/account/del");
        PATH_MAP.put("account/update", "kf/account/update");
        PATH_MAP.put("account/list", "kf/account/list");
        PATH_MAP.put("get_account_link", "kf/customer/get_account_link");
        PATH_MAP.put("servicer/add", "kf/servicer/add");
        PATH_MAP.put("servicer/del", "kf/servicer/del");
        PATH_MAP.put("servicer/list", "kf/servicer/list");
        PATH_MAP.put("get_customer_info", "kf/customer/batchget");
    }

    /**
     * 两级路径：account/add、account/del、account/update、account/list、servicer/add、servicer/del、servicer/list
     */
    @PreAuthorize("@ss.hasPermi('crm:wecom:config')")
    @PostMapping("/{operation}/{subOperation}")
    public AjaxResult proxyKfSubApi(
            @PathVariable String operation,
            @PathVariable String subOperation,
            @RequestBody(required = false) String requestBody)
    {
        String operationPath = operation + "/" + subOperation;
        return doProxy(operationPath, requestBody);
    }

    /**
     * 单级路径：send_msg、get_account_link、get_customer_info
     */
    @PreAuthorize("@ss.hasPermi('crm:wecom:config')")
    @PostMapping("/{operation}")
    public AjaxResult proxyKfApi(
            @PathVariable String operation,
            @RequestBody(required = false) String requestBody)
    {
        return doProxy(operation, requestBody);
    }

    private AjaxResult doProxy(String operationPath, String requestBody)
    {
        // 1. 将前端 operationPath 映射到企业微信 API 实际路径
        String resolvedPath = PATH_MAP.getOrDefault(operationPath, "kf/" + operationPath);

        // 2. 从当前租户库获取企业微信配置
        CrmWecomConfig cfg = resolveConfig();
        if (cfg == null)
        {
            return AjaxResult.error(HttpStatus.BAD_REQUEST, "未找到企业微信配置，请先完成企微自建应用配置");
        }

        // 3. 获取 agent access_token（内部带缓存和并发控制）
        String accessToken;
        try
        {
            accessToken = wecomAccessTokenService.getAgentAccessToken(cfg);
        }
        catch (Exception e)
        {
            logger.error("获取 access_token 失败: corpId={}", cfg.getCorpId(), e);
            return AjaxResult.error(HttpStatus.BAD_REQUEST, "获取 access_token 失败: " + e.getMessage());
        }

        // 4. 构造企业微信 API URL 并转发请求
        String wecomUrl = "https://qyapi.weixin.qq.com/cgi-bin/"
                + resolvedPath + "?access_token=" + accessToken;

        try
        {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(wecomUrl, entity, String.class);
            String responseBody = response.getBody();
            JSONObject result = JSON.parseObject(responseBody);

            int errcode = result.getIntValue("errcode");
            if (errcode != 0)
            {
                String errmsg = result.getString("errmsg");
                logger.warn("企业微信 KF API 返回错误: operation={}, errcode={}, errmsg={}",
                        operationPath, errcode, errmsg);
            }

            return AjaxResult.success(result);
        }
        catch (Exception e)
        {
            logger.error("调用企业微信 KF API 失败: operation={}, url={}", operationPath, wecomUrl, e);
            return AjaxResult.error(HttpStatus.BAD_REQUEST, "调用微信客服API失败: " + e.getMessage());
        }
    }

    private CrmWecomConfig resolveConfig()
    {
        CrmWecomConfig cfg = wecomConfigMapper.selectFirst();
        if (cfg == null || StringUtils.isEmpty(cfg.getCorpId()))
        {
            return null;
        }
        return cfg;
    }
}
