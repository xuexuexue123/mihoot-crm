package com.ruoyi.crm.service.impl;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.domain.WWaitResume;
import com.ruoyi.crm.enums.WaitResumeMode;
import com.ruoyi.crm.mapper.WWaitResumeMapper;
import com.ruoyi.crm.service.ICrmSfExpressCallbackService;
import com.ruoyi.crm.service.IWWaitResumeContractService;
import com.ruoyi.crm.service.IWaitResumeNotifyService;
import com.ruoyi.crm.service.support.WaitResumeTenantBootstrap;
import com.ruoyi.framework.datasource.DynamicDataSourceContextHolder;

@Service
public class CrmSfExpressCallbackServiceImpl implements ICrmSfExpressCallbackService
{
    private static final Logger log = LoggerFactory.getLogger(CrmSfExpressCallbackServiceImpl.class);

    @Autowired
    private WaitResumeTenantBootstrap waitResumeTenantBootstrap;

    @Autowired
    private WWaitResumeMapper wWaitResumeMapper;

    @Autowired
    private IWWaitResumeContractService wWaitResumeContractService;

    @Autowired
    private IWaitResumeNotifyService waitResumeNotifyService;

    @Override
    public ResponseEntity<Object> handleRoutePush(HttpServletRequest request)
    {
        String tenantId = null;
        try
        {
            tenantId = resolveTenantId(request);
            waitResumeTenantBootstrap.bindTenantDataSource(tenantId);

            String content = extractContent(request);
            JSONObject payload = parseRoutePushContent(content);
            String resumeKey = extractOrderId(payload);
            if (StringUtils.isBlank(resumeKey))
            {
                log.warn("SF route push missing orderid/resumeKey, tenant={}", tenantId);
                return sfAck(true);
            }

            WWaitResume row = wWaitResumeMapper.selectWWaitResumeByResumeKey(resumeKey);
            if (row == null)
            {
                log.warn("SF route push: wait resume not found for orderid={}, tenant={}", resumeKey, tenantId);
                return sfAck(true);
            }

            if (!WaitResumeMode.ON_WEBHOOK_CALL.getValue().equalsIgnoreCase(row.getMode()))
            {
                log.warn("SF route push: resume key {} is not webhook mode, tenant={}", resumeKey, tenantId);
                return sfAck(true);
            }

            if (row.getResumedAt() != null)
            {
                log.debug("SF route push: resume key {} already resumed, tenant={}", resumeKey, tenantId);
                return sfAck(true);
            }

            if (row.getExpiresAt() != null && !row.getExpiresAt().after(DateUtils.getNowDate()))
            {
                log.warn("SF route push: resume key {} expired, tenant={}", resumeKey, tenantId);
                return sfAck(true);
            }

            wWaitResumeContractService.resumeWithPayloadJson(resumeKey, payload.toJSONString());
            waitResumeNotifyService.notifyAfterCrmUpdate(resumeKey, tenantId);
            return sfAck(true);
        }
        catch (ServiceException e)
        {
            log.warn("SF route push service error, tenant={}: {}", tenantId, e.getMessage());
            return sfAck(false);
        }
        catch (Exception e)
        {
            log.warn("SF route push failed, tenant={}: {}", tenantId, e.getMessage());
            return sfAck(false);
        }
        finally
        {
            DynamicDataSourceContextHolder.clearDataSourceKey();
        }
    }

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
        throw new ServiceException("无法解析租户：请在 URL 携带 ?tenant= 或 header tenant");
    }

    private String extractContent(HttpServletRequest request) throws Exception
    {
        String content = request.getParameter("content");
        if (StringUtils.isNotBlank(content))
        {
            return URLDecoder.decode(content, StandardCharsets.UTF_8.name());
        }

        byte[] bodyBytes = StreamUtils.copyToByteArray(request.getInputStream());
        if (bodyBytes.length == 0)
        {
            return "{}";
        }
        String body = new String(bodyBytes, request.getCharacterEncoding() != null
                ? request.getCharacterEncoding() : StandardCharsets.UTF_8.name());
        if (StringUtils.isBlank(body))
        {
            return "{}";
        }

        if (body.contains("content="))
        {
            for (String part : body.split("&"))
            {
                if (part.startsWith("content="))
                {
                    String encoded = part.substring("content=".length());
                    return URLDecoder.decode(encoded, StandardCharsets.UTF_8.name());
                }
            }
        }
        return body;
    }

    private JSONObject parseRoutePushContent(String content)
    {
        if (StringUtils.isBlank(content))
        {
            return new JSONObject();
        }
        String trimmed = content.trim();
        if (trimmed.startsWith("{"))
        {
            return JSON.parseObject(trimmed);
        }
        if (trimmed.startsWith("<"))
        {
            return xmlRoutePushToJson(trimmed);
        }
        try
        {
            return JSON.parseObject(trimmed);
        }
        catch (Exception e)
        {
            Map<String, Object> wrapper = Collections.singletonMap("raw", trimmed);
            return new JSONObject(wrapper);
        }
    }

    private JSONObject xmlRoutePushToJson(String xml)
    {
        JSONObject result = new JSONObject();
        JSONArray routes = new JSONArray();
        String orderId = extractXmlTag(xml, "orderid");
        if (StringUtils.isBlank(orderId))
        {
            orderId = extractXmlTag(xml, "orderId");
        }
        JSONObject route = new JSONObject();
        route.put("mailno", extractXmlTag(xml, "mailno"));
        route.put("orderid", orderId);
        route.put("acceptAddress", extractXmlTag(xml, "acceptAddress"));
        route.put("acceptTime", extractXmlTag(xml, "acceptTime"));
        route.put("remark", extractXmlTag(xml, "remark"));
        route.put("opCode", extractXmlTag(xml, "opCode"));
        route.put("reasonName", extractXmlTag(xml, "reasonName"));
        routes.add(route);
        JSONObject body = new JSONObject();
        body.put("WaybillRoute", routes);
        result.put("Body", body);
        return result;
    }

    private static String extractXmlTag(String xml, String tag)
    {
        String open = "<" + tag + ">";
        String close = "</" + tag + ">";
        int start = xml.indexOf(open);
        if (start < 0)
        {
            return null;
        }
        start += open.length();
        int end = xml.indexOf(close, start);
        if (end < 0)
        {
            return null;
        }
        return xml.substring(start, end).trim();
    }

    private String extractOrderId(JSONObject payload)
    {
        if (payload == null)
        {
            return null;
        }
        JSONObject body = payload.getJSONObject("Body");
        if (body != null)
        {
            String fromBody = firstOrderIdFromWaybillRoutes(body.getJSONArray("WaybillRoute"));
            if (StringUtils.isNotBlank(fromBody))
            {
                return fromBody;
            }
        }
        JSONArray direct = payload.getJSONArray("WaybillRoute");
        String fromDirect = firstOrderIdFromWaybillRoutes(direct);
        if (StringUtils.isNotBlank(fromDirect))
        {
            return fromDirect;
        }
        return payload.getString("orderid");
    }

    private static String firstOrderIdFromWaybillRoutes(JSONArray routes)
    {
        if (routes == null || routes.isEmpty())
        {
            return null;
        }
        for (int i = 0; i < routes.size(); i++)
        {
            JSONObject route = routes.getJSONObject(i);
            if (route == null)
            {
                continue;
            }
            String orderId = route.getString("orderid");
            if (StringUtils.isBlank(orderId))
            {
                orderId = route.getString("orderId");
            }
            if (StringUtils.isNotBlank(orderId))
            {
                return orderId.trim();
            }
        }
        return null;
    }

    private ResponseEntity<Object> sfAck(boolean success)
    {
        Map<String, String> body = new LinkedHashMap<>();
        if (success)
        {
            body.put("return_code", "0000");
            body.put("return_msg", "成功");
        }
        else
        {
            body.put("return_code", "1000");
            body.put("return_msg", "系统异常");
        }
        return ResponseEntity.ok(body);
    }
}
