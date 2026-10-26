package com.ruoyi.crm.feishu;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;

/**
 * 调用飞书开放平台 API（token 由调用方传入；不含配置持久化）。
 * Token 缓存在 {@link FeishuTokenService} 进程内存，不落库。
 */
@Component
public class FeishuOpenApiClient
{
    public static final String BASE_URL = "https://open.feishu.cn";

    /** tenant_access_token 无效 */
    public static final int CODE_TOKEN_INVALID = 99991663;

    /** tenant_access_token 过期 */
    public static final int CODE_TOKEN_EXPIRED = 99991661;

    private final RestTemplate restTemplate = new RestTemplate();

    public static boolean isAccessTokenInvalid(int code)
    {
        return code == CODE_TOKEN_INVALID || code == CODE_TOKEN_EXPIRED;
    }

    /**
     * POST /open-apis/auth/v3/tenant_access_token/internal
     */
    public JSONObject getTenantAccessToken(String appId, String appSecret)
    {
        JSONObject body = new JSONObject();
        body.put("app_id", appId);
        body.put("app_secret", appSecret);
        return postJson(BASE_URL + "/open-apis/auth/v3/tenant_access_token/internal", null, body);
    }

    /**
     * GET /open-apis/bitable/v1/apps/{app_token}/tables
     */
    public JSONObject listTables(String accessToken, String appToken, String pageToken, Integer pageSize)
    {
        UriComponentsBuilder b = UriComponentsBuilder
                .fromHttpUrl(BASE_URL + "/open-apis/bitable/v1/apps/{app_token}/tables");
        if (pageToken != null && !pageToken.isEmpty())
        {
            b.queryParam("page_token", pageToken);
        }
        if (pageSize != null && pageSize > 0)
        {
            b.queryParam("page_size", pageSize);
        }
        URI uri = b.buildAndExpand(appToken).encode(StandardCharsets.UTF_8).toUri();
        return exchange(HttpMethod.GET, uri, accessToken, null);
    }

    /**
     * GET /open-apis/bitable/v1/apps/{app_token}/tables/{table_id}/fields
     */
    public JSONObject listFields(String accessToken, String appToken, String tableId,
            String pageToken, Integer pageSize)
    {
        UriComponentsBuilder b = UriComponentsBuilder
                .fromHttpUrl(BASE_URL + "/open-apis/bitable/v1/apps/{app_token}/tables/{table_id}/fields");
        if (pageToken != null && !pageToken.isEmpty())
        {
            b.queryParam("page_token", pageToken);
        }
        if (pageSize != null && pageSize > 0)
        {
            b.queryParam("page_size", pageSize);
        }
        URI uri = b.buildAndExpand(appToken, tableId).encode(StandardCharsets.UTF_8).toUri();
        return exchange(HttpMethod.GET, uri, accessToken, null);
    }

    /**
     * POST /open-apis/bitable/v1/apps — 创建多维表格（Base）
     */
    public JSONObject createApp(String accessToken, JSONObject body)
    {
        return postJson(BASE_URL + "/open-apis/bitable/v1/apps", accessToken, body);
    }

    /**
     * POST /open-apis/bitable/v1/apps/{app_token}/tables — 新增数据表
     */
    public JSONObject createTable(String accessToken, String appToken, JSONObject body)
    {
        URI uri = UriComponentsBuilder
                .fromHttpUrl(BASE_URL + "/open-apis/bitable/v1/apps/{app_token}/tables")
                .buildAndExpand(appToken)
                .encode(StandardCharsets.UTF_8)
                .toUri();
        return exchange(HttpMethod.POST, uri, accessToken, body);
    }

    /**
     * GET /open-apis/bitable/v1/apps/{app_token}/tables/{table_id}/records — 列出记录
     */
    public JSONObject listRecords(String accessToken, String appToken, String tableId,
            String pageToken, Integer pageSize, String viewId)
    {
        UriComponentsBuilder b = UriComponentsBuilder
                .fromHttpUrl(BASE_URL + "/open-apis/bitable/v1/apps/{app_token}/tables/{table_id}/records");
        if (pageToken != null && !pageToken.isEmpty())
        {
            b.queryParam("page_token", pageToken);
        }
        if (pageSize != null && pageSize > 0)
        {
            b.queryParam("page_size", pageSize);
        }
        if (viewId != null && !viewId.isEmpty())
        {
            b.queryParam("view_id", viewId);
        }
        URI uri = b.buildAndExpand(appToken, tableId).encode(StandardCharsets.UTF_8).toUri();
        return exchange(HttpMethod.GET, uri, accessToken, null);
    }

    /**
     * POST /open-apis/bitable/v1/apps/{app_token}/tables/{table_id}/records
     */
    public JSONObject createRecord(String accessToken, String appToken, String tableId, JSONObject body)
    {
        URI uri = UriComponentsBuilder
                .fromHttpUrl(BASE_URL + "/open-apis/bitable/v1/apps/{app_token}/tables/{table_id}/records")
                .buildAndExpand(appToken, tableId)
                .encode(StandardCharsets.UTF_8)
                .toUri();
        return exchange(HttpMethod.POST, uri, accessToken, body);
    }

    /**
     * POST .../records/batch_create — 新增多条记录
     */
    public JSONObject batchCreateRecords(String accessToken, String appToken, String tableId, JSONObject body)
    {
        URI uri = UriComponentsBuilder
                .fromHttpUrl(BASE_URL
                        + "/open-apis/bitable/v1/apps/{app_token}/tables/{table_id}/records/batch_create")
                .buildAndExpand(appToken, tableId)
                .encode(StandardCharsets.UTF_8)
                .toUri();
        return exchange(HttpMethod.POST, uri, accessToken, body);
    }

    /**
     * POST .../records/batch_delete — 删除多条记录
     */
    public JSONObject batchDeleteRecords(String accessToken, String appToken, String tableId, JSONObject body)
    {
        URI uri = UriComponentsBuilder
                .fromHttpUrl(BASE_URL
                        + "/open-apis/bitable/v1/apps/{app_token}/tables/{table_id}/records/batch_delete")
                .buildAndExpand(appToken, tableId)
                .encode(StandardCharsets.UTF_8)
                .toUri();
        return exchange(HttpMethod.POST, uri, accessToken, body);
    }

    /**
     * PUT /open-apis/bitable/v1/apps/{app_token}/tables/{table_id}/records/{record_id}
     */
    public JSONObject updateRecord(String accessToken, String appToken, String tableId, String recordId,
            JSONObject body)
    {
        URI uri = UriComponentsBuilder
                .fromHttpUrl(BASE_URL
                        + "/open-apis/bitable/v1/apps/{app_token}/tables/{table_id}/records/{record_id}")
                .buildAndExpand(appToken, tableId, recordId)
                .encode(StandardCharsets.UTF_8)
                .toUri();
        return exchange(HttpMethod.PUT, uri, accessToken, body);
    }

    /**
     * POST /open-apis/bitable/v1/apps/{app_token}/tables/{table_id}/records/search
     */
    public JSONObject searchRecords(String accessToken, String appToken, String tableId, JSONObject body)
    {
        URI uri = UriComponentsBuilder
                .fromHttpUrl(BASE_URL + "/open-apis/bitable/v1/apps/{app_token}/tables/{table_id}/records/search")
                .buildAndExpand(appToken, tableId)
                .encode(StandardCharsets.UTF_8)
                .toUri();
        return exchange(HttpMethod.POST, uri, accessToken, body);
    }

    /**
     * DELETE /open-apis/bitable/v1/apps/{app_token}/tables/{table_id}/records/{record_id}
     */
    public JSONObject deleteRecord(String accessToken, String appToken, String tableId, String recordId)
    {
        URI uri = UriComponentsBuilder
                .fromHttpUrl(BASE_URL
                        + "/open-apis/bitable/v1/apps/{app_token}/tables/{table_id}/records/{record_id}")
                .buildAndExpand(appToken, tableId, recordId)
                .encode(StandardCharsets.UTF_8)
                .toUri();
        return exchange(HttpMethod.DELETE, uri, accessToken, null);
    }

    private JSONObject postJson(String url, String accessToken, JSONObject body)
    {
        return exchange(HttpMethod.POST, URI.create(url), accessToken, body);
    }

    private JSONObject exchange(HttpMethod method, URI uri, String accessToken, JSONObject body)
    {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        if (accessToken != null && !accessToken.isEmpty())
        {
            headers.setBearerAuth(accessToken);
        }
        HttpEntity<String> entity;
        if (body != null)
        {
            headers.setContentType(MediaType.APPLICATION_JSON);
            entity = new HttpEntity<>(body.toJSONString(), headers);
        }
        else
        {
            entity = new HttpEntity<>(headers);
        }
        try
        {
            ResponseEntity<String> resp = restTemplate.exchange(uri, method, entity, String.class);
            return parseBody(resp.getBody(), resp.getStatusCodeValue());
        }
        catch (HttpStatusCodeException e)
        {
            JSONObject err = parseBody(e.getResponseBodyAsString(), e.getRawStatusCode());
            if (err.getIntValue("code") == 0 && e.getRawStatusCode() >= 400)
            {
                err.put("code", e.getRawStatusCode());
                if (err.getString("msg") == null || err.getString("msg").isEmpty())
                {
                    err.put("msg", "HTTP " + e.getRawStatusCode());
                }
            }
            if (e.getRawStatusCode() == 403)
            {
                String msg = err.getString("msg");
                if (msg == null || msg.isEmpty() || "success".equalsIgnoreCase(msg) || "ok".equalsIgnoreCase(msg))
                {
                    err.put("msg", "请将应用加为多维表格协作者");
                }
                else if (!msg.contains("协作者"))
                {
                    err.put("msg", msg + "；请将应用加为多维表格协作者");
                }
            }
            return err;
        }
    }

    private static JSONObject parseBody(String body, int httpStatus)
    {
        if (body == null || body.isEmpty())
        {
            JSONObject empty = new JSONObject();
            empty.put("code", httpStatus >= 400 ? httpStatus : -1);
            empty.put("msg", "empty response from feishu");
            return empty;
        }
        try
        {
            JSONObject res = JSON.parseObject(body);
            if (res == null)
            {
                JSONObject empty = new JSONObject();
                empty.put("code", -1);
                empty.put("msg", "invalid json from feishu");
                return empty;
            }
            return res;
        }
        catch (Exception e)
        {
            JSONObject empty = new JSONObject();
            empty.put("code", -1);
            empty.put("msg", "invalid json from feishu");
            return empty;
        }
    }
}
