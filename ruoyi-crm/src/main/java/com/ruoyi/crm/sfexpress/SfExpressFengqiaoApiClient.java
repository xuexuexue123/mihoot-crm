package com.ruoyi.crm.sfexpress;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.domain.CrmSfExpressConfig;

/**
 * 调用顺丰丰桥统一接入平台（表单 POST + msgDigest 签名）。
 */
@Component
public class SfExpressFengqiaoApiClient
{
    public static final String SERVICE_CREATE_ORDER = "EXP_RECE_CREATE_ORDER";

    public static final String SERVICE_SEARCH_ROUTES = "EXP_RECE_SEARCH_ROUTES";

    public static final String SERVICE_UPDATE_ORDER = "EXP_RECE_UPDATE_ORDER";

    /** 丰桥沙箱须走 HTTPS；HTTP 会连接超时。 */
    private static final String SANDBOX_URL = "https://sfapi-sbox.sf-express.com/std/service";

    private static final String PROD_URL = "https://sfapi.sf-express.com/std/service";

    private static final String API_SUCCESS = "A1000";

    private static final int CONNECT_TIMEOUT_MS = 10_000;

    private static final int READ_TIMEOUT_MS = 30_000;

    private final RestTemplate restTemplate = createRestTemplate();

    /**
     * 调用丰桥接口，返回顶层响应 JSON（含 apiResultCode、apiResultData 字符串）。
     */
    public JSONObject call(CrmSfExpressConfig config, String checkwordPlain, String serviceCode, String msgDataJson)
    {
        if (config == null || StringUtils.isBlank(config.getCustomerCode()) || StringUtils.isBlank(checkwordPlain))
        {
            throw new ServiceException("请先配置顺丰集成", 500);
        }

        String timestamp = String.valueOf(System.currentTimeMillis());
        String msgDigest = SfExpressSignUtil.sign(msgDataJson, timestamp, checkwordPlain);

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("partnerID", config.getCustomerCode().trim());
        form.add("requestID", UUID.randomUUID().toString().replace("-", ""));
        form.add("serviceCode", serviceCode);
        form.add("timestamp", timestamp);
        form.add("msgData", msgDataJson);
        form.add("msgDigest", msgDigest);

        String url = Boolean.TRUE.equals(config.getSandbox()) ? SANDBOX_URL : PROD_URL;
        long backoffMs = 1000L;
        for (int attempt = 0; attempt < 4; attempt++)
        {
            try
            {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(form, headers);
                ResponseEntity<String> resp = restTemplate.postForEntity(url, entity, String.class);
                JSONObject top = JSON.parseObject(resp.getBody());
                if (top == null)
                {
                    throw new ServiceException("顺丰 API 无响应体", 500);
                }
                String apiResultCode = top.getString("apiResultCode");
                if (isRetryable(apiResultCode) && attempt < 3)
                {
                    sleepQuietly(backoffMs);
                    backoffMs = Math.min(backoffMs * 2, 8000L);
                    continue;
                }
                return top;
            }
            catch (ServiceException e)
            {
                throw e;
            }
            catch (ResourceAccessException e)
            {
                throw new ServiceException(formatNetworkError(e), 500);
            }
            catch (RuntimeException e)
            {
                if (attempt < 3)
                {
                    sleepQuietly(backoffMs);
                    backoffMs = Math.min(backoffMs * 2, 8000L);
                    continue;
                }
                throw new ServiceException("顺丰 API 调用失败: " + e.getMessage(), 500);
            }
        }
        throw new AssertionError("SF Express API retry loop");
    }

    /**
     * 解析 apiResultData 内层 JSON；平台层非 A1000 时抛异常。
     */
    public JSONObject parseBusinessResult(JSONObject top, boolean requireInnerSuccess)
    {
        if (top == null)
        {
            throw new ServiceException("顺丰 API 响应为空", 500);
        }
        String apiResultCode = top.getString("apiResultCode");
        if (!API_SUCCESS.equals(apiResultCode))
        {
            String msg = top.getString("apiErrorMsg");
            throw new ServiceException(StringUtils.isNotBlank(msg) ? msg : "顺丰 API 平台错误: " + apiResultCode, 500);
        }

        String apiResultDataStr = top.getString("apiResultData");
        if (StringUtils.isBlank(apiResultDataStr))
        {
            Map<String, Object> wrapper = new LinkedHashMap<>();
            wrapper.put("raw", top);
            return new JSONObject(wrapper);
        }

        JSONObject inner = JSON.parseObject(apiResultDataStr);
        if (requireInnerSuccess && inner != null && inner.containsKey("success")
                && !Boolean.TRUE.equals(inner.getBoolean("success")))
        {
            String errMsg = inner.getString("errorMsg");
            String errCode = inner.getString("errorCode");
            String msg = StringUtils.isNotBlank(errMsg) ? errMsg
                    : ("顺丰业务错误" + (StringUtils.isNotBlank(errCode) ? ": " + errCode : ""));
            throw new ServiceException(msg, 500);
        }
        return inner != null ? inner : new JSONObject();
    }

    /**
     * 连通性探测：平台层 A1000 即视为凭证可达（内层业务错误如「无路由」仍算连通）。
     */
    public boolean probeConnectivity(CrmSfExpressConfig config, String checkwordPlain)
    {
        JSONObject msgData = new JSONObject();
        msgData.put("language", "0");
        msgData.put("trackingType", 1);
        msgData.put("trackingNumber", new String[] { "SF0000000000000" });
        msgData.put("methodType", 1);

        JSONObject top = call(config, checkwordPlain, SERVICE_SEARCH_ROUTES, msgData.toJSONString());
        return top != null && API_SUCCESS.equals(top.getString("apiResultCode"));
    }

    private static boolean isRetryable(String apiResultCode)
    {
        if (StringUtils.isBlank(apiResultCode))
        {
            return true;
        }
        return "A1003".equals(apiResultCode) || "A1004".equals(apiResultCode);
    }

    private static RestTemplate createRestTemplate()
    {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(CONNECT_TIMEOUT_MS);
        factory.setReadTimeout(READ_TIMEOUT_MS);
        return new RestTemplate(factory);
    }

    private static String formatNetworkError(ResourceAccessException e)
    {
        String detail = e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName();
        if (detail.contains("timed out") || detail.contains("Timeout"))
        {
            return "无法连接顺丰 API（连接超时），请检查网络或确认沙箱/生产环境配置正确";
        }
        if (detail.contains("Connection refused") || detail.contains("connect"))
        {
            return "无法连接顺丰 API，请检查服务器出网权限与丰桥网关地址";
        }
        return "顺丰 API 网络异常: " + detail;
    }

    private static void sleepQuietly(long ms)
    {
        try
        {
            Thread.sleep(ms);
        }
        catch (InterruptedException e)
        {
            Thread.currentThread().interrupt();
        }
    }
}
