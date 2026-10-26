package com.ruoyi.crm.wecom;

import java.nio.charset.StandardCharsets;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;

/**
 * 企微临时素材 media/get 响应：成功为二进制，失败为 JSON errcode。
 */
public class WecomMediaFetchResult
{
    private final boolean success;
    private final String contentType;
    private final byte[] body;
    private final int errcode;
    private final String errmsg;

    private WecomMediaFetchResult(boolean success, String contentType, byte[] body, int errcode, String errmsg)
    {
        this.success = success;
        this.contentType = contentType;
        this.body = body;
        this.errcode = errcode;
        this.errmsg = errmsg;
    }

    public static WecomMediaFetchResult success(String contentType, byte[] body)
    {
        return new WecomMediaFetchResult(true, contentType, body, 0, null);
    }

    public static WecomMediaFetchResult error(int errcode, String errmsg)
    {
        return new WecomMediaFetchResult(false, null, null, errcode, errmsg);
    }

    public static WecomMediaFetchResult fromHttpResponse(ResponseEntity<byte[]> resp)
    {
        byte[] body = resp.getBody();
        if (body == null || body.length == 0)
        {
            return error(-1, "empty response body");
        }

        MediaType contentType = resp.getHeaders().getContentType();
        if (contentType != null && MediaType.APPLICATION_JSON.includes(contentType))
        {
            return parseErrorJson(body);
        }
        if (body[0] == '{')
        {
            WecomMediaFetchResult err = parseErrorJson(body);
            if (!err.isSuccess())
            {
                return err;
            }
        }

        String mime = contentType != null ? contentType.toString() : MediaType.APPLICATION_OCTET_STREAM_VALUE;
        return success(mime, body);
    }

    private static WecomMediaFetchResult parseErrorJson(byte[] body)
    {
        JSONObject json = JSON.parseObject(new String(body, StandardCharsets.UTF_8));
        if (json != null && json.containsKey("errcode"))
        {
            int errcode = json.getIntValue("errcode");
            if (errcode != 0)
            {
                return error(errcode, json.getString("errmsg"));
            }
        }
        return error(-1, "unexpected json response");
    }

    public boolean isSuccess()
    {
        return success;
    }

    public String getContentType()
    {
        return contentType;
    }

    public byte[] getBody()
    {
        return body;
    }

    public int getErrcode()
    {
        return errcode;
    }

    public String getErrmsg()
    {
        return errmsg;
    }

    public boolean isTokenInvalid()
    {
        return WecomTencentApiClient.isAccessTokenInvalid(errcode) || errcode == 40001;
    }

    public boolean isInvalidMediaId()
    {
        return errcode == 40007;
    }
}
