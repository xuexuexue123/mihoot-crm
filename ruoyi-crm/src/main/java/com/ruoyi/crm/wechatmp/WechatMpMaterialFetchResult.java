package com.ruoyi.crm.wechatmp;

import java.nio.charset.StandardCharsets;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;

/**
 * 微信永久素材 get_material 响应：news/video 为 JSON，image/voice 为二进制，失败为 JSON errcode。
 */
public class WechatMpMaterialFetchResult
{
    public enum Kind
    {
        JSON,
        BINARY,
        ERROR
    }

    private final Kind kind;
    private final JSONObject json;
    private final String contentType;
    private final byte[] body;
    private final int errcode;
    private final String errmsg;

    private WechatMpMaterialFetchResult(Kind kind, JSONObject json, String contentType, byte[] body,
            int errcode, String errmsg)
    {
        this.kind = kind;
        this.json = json;
        this.contentType = contentType;
        this.body = body;
        this.errcode = errcode;
        this.errmsg = errmsg;
    }

    public static WechatMpMaterialFetchResult json(JSONObject json)
    {
        return new WechatMpMaterialFetchResult(Kind.JSON, json, null, null, 0, null);
    }

    public static WechatMpMaterialFetchResult binary(String contentType, byte[] body)
    {
        return new WechatMpMaterialFetchResult(Kind.BINARY, null, contentType, body, 0, null);
    }

    public static WechatMpMaterialFetchResult error(int errcode, String errmsg)
    {
        return new WechatMpMaterialFetchResult(Kind.ERROR, null, null, null, errcode, errmsg);
    }

    public static WechatMpMaterialFetchResult fromHttpResponse(ResponseEntity<byte[]> resp)
    {
        byte[] body = resp.getBody();
        if (body == null || body.length == 0)
        {
            return error(-1, "empty response body");
        }

        MediaType contentType = resp.getHeaders().getContentType();
        boolean looksLikeJson = (contentType != null && MediaType.APPLICATION_JSON.includes(contentType))
                || body[0] == '{';
        if (looksLikeJson)
        {
            return parseJsonBody(body);
        }

        String mime = contentType != null ? contentType.toString() : MediaType.APPLICATION_OCTET_STREAM_VALUE;
        return binary(mime, body);
    }

    private static WechatMpMaterialFetchResult parseJsonBody(byte[] body)
    {
        JSONObject json = JSON.parseObject(new String(body, StandardCharsets.UTF_8));
        if (json == null)
        {
            return error(-1, "invalid json response");
        }
        if (json.containsKey("errcode") && json.getIntValue("errcode") != 0)
        {
            return error(json.getIntValue("errcode"), json.getString("errmsg"));
        }
        return json(json);
    }

    public Kind getKind()
    {
        return kind;
    }

    public boolean isJson()
    {
        return kind == Kind.JSON;
    }

    public boolean isBinary()
    {
        return kind == Kind.BINARY;
    }

    public boolean isError()
    {
        return kind == Kind.ERROR;
    }

    public JSONObject getJson()
    {
        return json;
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
        return errcode == 40001;
    }

    public boolean isInvalidMediaId()
    {
        return errcode == 40007;
    }

    public boolean isPermissionDenied()
    {
        return errcode == 48001;
    }
}
