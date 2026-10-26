package com.ruoyi.crm.sfexpress;

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

/**
 * 顺丰 OAuth2 换票客户端。
 */
@Component
public class SfExpressOAuthClient
{
    private static final String SANDBOX_TOKEN_URL = "https://sfapi-sbox.sf-express.com/oauth2/accessToken";

    private static final String PROD_TOKEN_URL = "https://bspgw.sf-express.com/oauth2/accessToken";

    private static final String API_SUCCESS = "A1000";

    private static final int DEFAULT_EXPIRES_IN_SEC = 7200;

    private static final int CONNECT_TIMEOUT_MS = 10_000;

    private static final int READ_TIMEOUT_MS = 30_000;

    private final RestTemplate restTemplate = createRestTemplate();

    /**
     * 向顺丰 OAuth2 换票，返回 accessToken 与 expiresIn（秒）。
     */
    public OAuthTokenResult fetchAccessToken(String customerCode, String checkwordPlain, boolean sandbox)
    {
        if (StringUtils.isBlank(customerCode) || StringUtils.isBlank(checkwordPlain))
        {
            throw new ServiceException("请先配置顺丰集成", 500);
        }

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("partnerID", customerCode.trim());
        form.add("secret", checkwordPlain.trim());
        form.add("grantType", "password");

        String url = sandbox ? SANDBOX_TOKEN_URL : PROD_TOKEN_URL;
        try
        {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(form, headers);
            ResponseEntity<String> resp = restTemplate.postForEntity(url, entity, String.class);
            JSONObject top = JSON.parseObject(resp.getBody());
            if (top == null)
            {
                throw new ServiceException("顺丰 OAuth 无响应体", 500);
            }

            String apiResultCode = top.getString("apiResultCode");
            if (!API_SUCCESS.equals(apiResultCode))
            {
                String msg = top.getString("apiErrorMsg");
                throw new ServiceException(StringUtils.isNotBlank(msg) ? msg
                        : ("顺丰 OAuth 失败: " + apiResultCode), 500);
            }

            return parseTokenPayload(top);
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
            throw new ServiceException("顺丰 OAuth 调用失败: " + e.getMessage(), 500);
        }
    }

    private OAuthTokenResult parseTokenPayload(JSONObject top)
    {
        JSONObject data = null;
        String apiResultDataStr = top.getString("apiResultData");
        if (StringUtils.isNotBlank(apiResultDataStr))
        {
            data = JSON.parseObject(apiResultDataStr);
        }
        if (data == null)
        {
            data = top;
        }

        String accessToken = firstNonBlank(data.getString("accessToken"), data.getString("access_token"),
                top.getString("accessToken"), top.getString("access_token"));
        if (StringUtils.isBlank(accessToken))
        {
            throw new ServiceException("顺丰 OAuth 响应缺少 accessToken", 500);
        }

        Integer expiresIn = data.getInteger("expiresIn");
        if (expiresIn == null)
        {
            expiresIn = data.getInteger("expires_in");
        }
        if (expiresIn == null)
        {
            expiresIn = top.getInteger("expiresIn");
        }
        if (expiresIn == null || expiresIn <= 0)
        {
            expiresIn = DEFAULT_EXPIRES_IN_SEC;
        }

        return new OAuthTokenResult(accessToken, expiresIn);
    }

    private static String firstNonBlank(String... values)
    {
        for (String v : values)
        {
            if (StringUtils.isNotBlank(v))
            {
                return v.trim();
            }
        }
        return null;
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
            return "无法连接顺丰 OAuth（连接超时），请检查网络或沙箱/生产环境配置";
        }
        return "顺丰 OAuth 网络异常: " + detail;
    }

    public static final class OAuthTokenResult
    {
        private final String accessToken;
        private final int expiresInSec;

        public OAuthTokenResult(String accessToken, int expiresInSec)
        {
            this.accessToken = accessToken;
            this.expiresInSec = expiresInSec;
        }

        public String getAccessToken()
        {
            return accessToken;
        }

        public int getExpiresInSec()
        {
            return expiresInSec;
        }
    }
}
