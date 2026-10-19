package com.ruoyi.web.controller.crm;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.HmacUtil;
import com.ruoyi.common.utils.sign.AESUtil;
import com.ruoyi.crm.domain.CrmWeeklyreport;
import com.ruoyi.flowable.config.TenantContext;
import com.ruoyi.flowable.factory.FlowServiceFactory;
import com.ruoyi.flowable.service.IFlowDefinitionService;
import com.ruoyi.framework.datasource.DynamicDataSourceContextHolder;
import com.ruoyi.framework.datasource.DynamicRoutingDataSource;
import com.ruoyi.system.domain.FlowProcDefDto;
import com.ruoyi.tenant.domain.MasterTenant;
import com.ruoyi.tenant.domain.enums.TenantStatus;
import com.ruoyi.tenant.service.IMasterTenantService;
import org.flowable.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/webhook/start")
public class WebhookFlowController extends BaseController {

    @Autowired
    private IFlowDefinitionService flowDefinitionService;

    @Autowired
    private IMasterTenantService masterTenantService;

    @Autowired
    private DynamicRoutingDataSource dynamicRoutingDataSource;

    @Autowired
    private FlowServiceFactory flowServiceFactory;

    @Value("${spring.datasource.driverClassName}")
    private String driverClassName;
    /**
     *
     */
    @Log(title = "Webhook", businessType = BusinessType.INSERT)
    @PostMapping("/{tenantId}/{flowName}/{token}")
    public AjaxResult triggerProcess(
            @PathVariable String tenantId,
            @PathVariable String flowName,
            @PathVariable String token,
            @RequestBody Map<String, Object> variables) throws Exception {

        if (tenantId == null || tenantId == "") {
            return AjaxResult.error("Failed to get right information.");
        }

        String decodedTenantId = decodeBase64UrlSafe(tenantId);
        String decryptedTenantId = AESUtil.decrypt(decodedTenantId);

        // 1. 验证 token
        if (!isValidToken(decryptedTenantId, flowName, token)) {
            ResponseEntity<?> result = ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
            return AjaxResult.error(result.toString());
        }

        if (!dynamicRoutingDataSource.existDataSource(decryptedTenantId)) {
            //搜索默认数据库，去注册租户的数据源，下次进来直接session匹配数据源
            MasterTenant masterTenant = masterTenantService.selectMasterTenant(decryptedTenantId);

            if (masterTenant == null) {
                return AjaxResult.error("No tenant found.");
            }

            Map<String, Object> map = new HashMap<>();
            map.put("driverClassName", driverClassName);
            map.put("url", masterTenant.getUrl());
            map.put("username", masterTenant.getUsername());
            map.put("password", masterTenant.getPassword());
            dynamicRoutingDataSource.addDataSource(decryptedTenantId, map);

            System.out.println("&&&&&&&&&&& 已设置租户:{"+decryptedTenantId+"} 连接信息: {"+masterTenant+"}");
        }else{
            System.out.println("&&&&&&&&&&& 当前租户:{"+decryptedTenantId+"}");
        }
        TenantContext.setCurrentTenant(decryptedTenantId);
//        flowServiceFactory.setCurrentTenant(decryptedTenantId);
        // 2. 动态切换租户数据源
        DynamicDataSourceContextHolder.setDataSourceKey(decryptedTenantId);

        // 3. 启动流程实例
//        String procDefId = "ccb73ee5-3c65-11f0-aaab-0edc9fe473f4";
//        String procDefId = "emailProcess:1:ccb988d7-3c65-11f0-aaab-0edc9fe473f4";
        FlowProcDefDto procDef = flowDefinitionService.getDeploymentIdByKey(flowName); // 你自己实现的方法
        AjaxResult instance = flowDefinitionService.startProcessInstanceByProcDefId(procDef.getId(), decryptedTenantId, variables);
        System.out.println("####### procDef = " + instance);
        return AjaxResult.success();

    }

    private boolean isValidToken(String tenantId, String flowName, String token) {

        String expectedToken = HmacUtil.generateSurveyToken(flowName, tenantId);

        String prefixToken = expectedToken.substring(0, 8);
        String suffixToken = expectedToken.substring(expectedToken.length() -8);
        String webhookToken = prefixToken + suffixToken;

        if (!webhookToken.equals(token)) {
            return false;
        }

        return true;

    }


    /**
     * 将 URL-safe Base64 编码的字符串解码为原始字符串
     * 例如： "2x9_BRVvY9CoJ_JPoi3IcA" → "2x9/BRVvY9CoJ/JPoi3IcA=="
     */
    public static String decodeBase64UrlSafe(String urlSafeBase64) {
        // 替换回标准 base64 字符
        String base64 = urlSafeBase64.replace('-', '+').replace('_', '/');
        // 补齐 padding
        int paddingNeeded = (4 - base64.length() % 4) % 4;
        for (int i = 0; i < paddingNeeded; i++) {
            base64 += "=";
        }
        return base64;

        // 解码并转为字符串（假设是 UTF-8 编码）
//        byte[] decodedBytes = Base64.getDecoder().decode(base64);
//        return new String(decodedBytes, java.nio.charset.StandardCharsets.UTF_8);
    }


    private String getExpectedTokenForTenant(String tenantId) {
        // 示例：实际应用应从配置或租户表中读取
        return "TENANT_TOKEN";
    }
}
