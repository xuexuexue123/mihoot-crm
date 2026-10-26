package com.ruoyi.crm.service.impl;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.domain.WWaitResume;
import com.ruoyi.crm.enums.WaitResumeMode;
import com.ruoyi.crm.mapper.WWaitResumeMapper;
import com.ruoyi.crm.service.IWWaitResumeContractService;
import com.ruoyi.crm.service.IWWaitResumePublicService;
import com.ruoyi.crm.service.IWaitResumeNotifyService;
import com.ruoyi.crm.service.support.TenantDataSourceSupport;
import com.ruoyi.crm.service.support.WaitResumeTenantBootstrap;
import com.ruoyi.framework.datasource.DynamicDataSourceContextHolder;

@Service
public class WWaitResumePublicServiceImpl implements IWWaitResumePublicService
{
    @Autowired
    private WWaitResumeMapper wWaitResumeMapper;

    @Autowired
    private IWWaitResumeContractService wWaitResumeContractService;

    @Autowired
    private IWaitResumeNotifyService waitResumeNotifyService;

    @Autowired
    private WaitResumeTenantBootstrap waitResumeTenantBootstrap;

    @Override
    public ResponseEntity<Object> handleWebhookCallback(String resumeKey, HttpServletRequest request)
    {
        try
        {
            String tenantId = resolveTenantId(request);
            waitResumeTenantBootstrap.bindTenantDataSource(tenantId);

            WWaitResume row = wWaitResumeMapper.selectWWaitResumeByResumeKey(resumeKey);
            if (row == null)
            {
                return errorResponse(com.ruoyi.common.constant.HttpStatus.NOT_FOUND, "wait resume 不存在");
            }

            if (!WaitResumeMode.ON_WEBHOOK_CALL.getValue().equalsIgnoreCase(row.getMode()))
            {
                return errorResponse(com.ruoyi.common.constant.HttpStatus.BAD_METHOD, "该 resume key 不是 webhook 模式");
            }

            if (row.getResumedAt() != null)
            {
                return errorResponse(com.ruoyi.common.constant.HttpStatus.GONE, "wait resume 已恢复");
            }

            if (row.getExpiresAt() != null && !row.getExpiresAt().after(DateUtils.getNowDate()))
            {
                return errorResponse(com.ruoyi.common.constant.HttpStatus.GONE, "wait resume 已过期");
            }

            if (!matchesHttpMethod(request.getMethod(), row.getHttpMethod()))
            {
                return errorResponse(com.ruoyi.common.constant.HttpStatus.BAD_METHOD, "HTTP 方法不匹配");
            }

            if (!validateAuth(row.getAuthConfig(), request))
            {
                return errorResponse(com.ruoyi.common.constant.HttpStatus.UNAUTHORIZED, "鉴权失败");
            }

            String payloadJson = buildPayloadJson(request);
            wWaitResumeContractService.resumeWithPayloadJson(resumeKey, payloadJson);
            waitResumeNotifyService.notifyAfterCrmUpdate(resumeKey, tenantId);

            return buildSuccessResponse(row.getResponseConfig());
        }
        catch (ServiceException e)
        {
            int code = e.getCode() != null ? e.getCode() : com.ruoyi.common.constant.HttpStatus.ERROR;
            return errorResponse(code, e.getMessage());
        }
        catch (Exception e)
        {
            return errorResponse(com.ruoyi.common.constant.HttpStatus.ERROR, e.getMessage());
        }
        finally
        {
            DynamicDataSourceContextHolder.clearDataSourceKey();
        }
    }

    /**
     * 公开回调必须显式携带 tenant（header 或 query），仅在 tenant 库访问 {@code w_wait_resume}。
     */
    private String resolveTenantId(HttpServletRequest request)
    {
        String queryTenant = request.getParameter("tenant");
        if (StringUtils.isNotBlank(queryTenant))
        {
            return queryTenant.trim();
        }
        String headerTenant = request.getHeader("tenant");
        if (StringUtils.isNotBlank(headerTenant))
        {
            return headerTenant.trim();
        }
        throw new ServiceException("无法解析租户：请在 URL 携带 ?tenant= 或 header tenant",
                com.ruoyi.common.constant.HttpStatus.BAD_REQUEST);
    }

    private boolean matchesHttpMethod(String actual, String expected)
    {
        if (StringUtils.isBlank(expected))
        {
            return true;
        }
        return expected.equalsIgnoreCase(actual);
    }

    private boolean validateAuth(String authConfigJson, HttpServletRequest request)
    {
        if (StringUtils.isBlank(authConfigJson))
        {
            return true;
        }
        JSONObject authConfig = JSON.parseObject(authConfigJson);
        String kind = authConfig.getString("kind");
        if (StringUtils.isBlank(kind) || "none".equalsIgnoreCase(kind))
        {
            return true;
        }
        String tokenName = authConfig.getString("tokenName");
        String tokenValue = authConfig.getString("tokenValue");
        if (StringUtils.isBlank(tokenName) || tokenValue == null)
        {
            return false;
        }
        if ("header".equalsIgnoreCase(kind))
        {
            return tokenValue.equals(request.getHeader(tokenName));
        }
        if ("query".equalsIgnoreCase(kind))
        {
            return tokenValue.equals(request.getParameter(tokenName));
        }
        return false;
    }

    private String buildPayloadJson(HttpServletRequest request) throws Exception
    {
        String method = request.getMethod();
        if ("GET".equalsIgnoreCase(method) || "DELETE".equalsIgnoreCase(method))
        {
            Map<String, Object> query = new LinkedHashMap<>();
            Enumeration<String> names = request.getParameterNames();
            while (names.hasMoreElements())
            {
                String name = names.nextElement();
                query.put(name, request.getParameter(name));
            }
            Map<String, Object> wrapper = new LinkedHashMap<>();
            wrapper.put("query", query);
            return JSON.toJSONString(wrapper);
        }

        String contentType = request.getContentType();
        if (contentType != null && contentType.toLowerCase().contains(MediaType.APPLICATION_FORM_URLENCODED_VALUE))
        {
            Map<String, Object> form = new LinkedHashMap<>();
            Enumeration<String> names = request.getParameterNames();
            while (names.hasMoreElements())
            {
                String name = names.nextElement();
                form.put(name, request.getParameter(name));
            }
            return JSON.toJSONString(form);
        }

        byte[] bodyBytes = StreamUtils.copyToByteArray(request.getInputStream());
        if (bodyBytes.length == 0)
        {
            return "{}";
        }
        String body = new String(bodyBytes, request.getCharacterEncoding() != null ? request.getCharacterEncoding()
                : StandardCharsets.UTF_8.name());
        if (StringUtils.isBlank(body))
        {
            return "{}";
        }
        if (contentType != null && contentType.toLowerCase().contains(MediaType.APPLICATION_JSON_VALUE))
        {
            return body;
        }
        try
        {
            JSON.parse(body);
            return body;
        }
        catch (Exception ignored)
        {
            Map<String, Object> wrapper = Collections.singletonMap("raw", body);
            return JSON.toJSONString(wrapper);
        }
    }

    private ResponseEntity<Object> buildSuccessResponse(String responseConfigJson)
    {
        int statusCode = 200;
        Object body = Collections.singletonMap("received", true);
        if (StringUtils.isNotBlank(responseConfigJson))
        {
            JSONObject responseConfig = JSON.parseObject(responseConfigJson);
            if (responseConfig.containsKey("statusCode"))
            {
                statusCode = responseConfig.getIntValue("statusCode");
            }
            if (responseConfig.containsKey("body"))
            {
                body = responseConfig.get("body");
            }
        }
        return ResponseEntity.status(statusCode).body(body);
    }

    private ResponseEntity<Object> errorResponse(int code, String message)
    {
        HttpStatus status = HttpStatus.resolve(code);
        if (status == null)
        {
            status = HttpStatus.BAD_REQUEST;
        }
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("code", code);
        body.put("msg", message);
        return ResponseEntity.status(status).body(body);
    }
}
