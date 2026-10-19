package com.ruoyi.crm.wecom;

import java.io.File;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;

/**
 * 调用企业微信服务端 API（不含加解密；token 由调用方传入）。
 */
@Component
public class WecomTencentApiClient
{
    /** 频率限制：稍后重试（见企微文档） */
    private static final int ERRCODE_API_FREQ = 45009;

    /** 其它应用抢发欢迎语 */
    private static final int ERRCODE_WELCOME_CONFLICT = 41096;

    /** 欢迎语已成功下发 */
    private static final int ERRCODE_WELCOME_ALREADY_SENT = 41051;

    /** access_token 无效或已过期 */
    public static final int ERRCODE_INVALID_ACCESS_TOKEN = 40014;

    /** access_token 过期 */
    public static final int ERRCODE_ACCESS_TOKEN_EXPIRED = 42001;

    private final RestTemplate restTemplate = new RestTemplate();

    public static boolean isAccessTokenInvalid(int errcode)
    {
        return errcode == ERRCODE_INVALID_ACCESS_TOKEN || errcode == ERRCODE_ACCESS_TOKEN_EXPIRED;
    }

    public JSONObject getAccessToken(String corpId, String agentSecret)
    {
        long backoffMs = 1000L;
        for (int attempt = 0; attempt < 4; attempt++)
        {
            try
            {
                String url = "https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid="
                        + URLEncoder.encode(corpId, StandardCharsets.UTF_8.name())
                        + "&corpsecret=" + URLEncoder.encode(agentSecret, StandardCharsets.UTF_8.name());
                ResponseEntity<String> resp = restTemplate.getForEntity(url, String.class);
                JSONObject res = JSON.parseObject(resp.getBody());
                if (res.getIntValue("errcode") == ERRCODE_API_FREQ && attempt < 3)
                {
                    sleepQuietly(backoffMs);
                    backoffMs = Math.min(backoffMs * 2, 8000L);
                    continue;
                }
                return res;
            }
            catch (RuntimeException e)
            {
                throw e;
            }
            catch (Exception e)
            {
                throw new RuntimeException(e);
            }
        }
        throw new AssertionError("getAccessToken retry loop");
    }

    public JSONObject externalContactAddContactWay(String accessToken, JSONObject body)
    {
        String url = "https://qyapi.weixin.qq.com/cgi-bin/externalcontact/add_contact_way?access_token=" + accessToken;
        return postJsonWithRateLimitRetry(url, body);
    }

    public JSONObject externalContactUpdateContactWay(String accessToken, JSONObject body)
    {
        String url = "https://qyapi.weixin.qq.com/cgi-bin/externalcontact/update_contact_way?access_token=" + accessToken;
        return postJsonWithRateLimitRetry(url, body);
    }

    public JSONObject externalContactDelContactWay(String accessToken, String configId)
    {
        JSONObject body = new JSONObject();
        body.put("config_id", configId);
        String url = "https://qyapi.weixin.qq.com/cgi-bin/externalcontact/del_contact_way?access_token=" + accessToken;
        return postJsonWithRateLimitRetry(url, body);
    }

    public JSONObject externalContactAddMsgTemplate(String accessToken, JSONObject body)
    {
        String url = "https://qyapi.weixin.qq.com/cgi-bin/externalcontact/add_msg_template?access_token=" + accessToken;
        return postJsonWithRateLimitRetry(url, body);
    }

    public JSONObject externalContactGetGroupMsgSendResult(String accessToken, JSONObject body)
    {
        String url = "https://qyapi.weixin.qq.com/cgi-bin/externalcontact/get_groupmsg_send_result?access_token=" + accessToken;
        return postJsonWithRateLimitRetry(url, body);
    }

    /** 获取配置了客户联系功能的成员 userid 列表 */
    public JSONObject externalContactGetFollowUserList(String accessToken)
    {
        String url = "https://qyapi.weixin.qq.com/cgi-bin/externalcontact/get_follow_user_list?access_token=" + accessToken;
        return getJsonWithRateLimitRetry(url);
    }

    /** 获取客户详情（含 external_contact.name） */
    public JSONObject externalContactGet(String accessToken, String externalUserid)
    {
        try
        {
            String url = "https://qyapi.weixin.qq.com/cgi-bin/externalcontact/get?access_token=" + accessToken
                    + "&external_userid=" + URLEncoder.encode(externalUserid, StandardCharsets.UTF_8.name());
            return getJsonWithRateLimitRetry(url);
        }
        catch (java.io.UnsupportedEncodingException e)
        {
            throw new RuntimeException(e);
        }
    }

    /** 获取指定成员添加的客户 external_userid 列表 */
    public JSONObject externalContactList(String accessToken, String userid)
    {
        try
        {
            String url = "https://qyapi.weixin.qq.com/cgi-bin/externalcontact/list?access_token=" + accessToken
                    + "&userid=" + URLEncoder.encode(userid, StandardCharsets.UTF_8.name());
            return getJsonWithRateLimitRetry(url);
        }
        catch (java.io.UnsupportedEncodingException e)
        {
            throw new RuntimeException(e);
        }
    }

    /** 批量获取客户详情（POST batch/get_by_user） */
    public JSONObject externalContactBatchGetByUser(String accessToken, JSONObject body)
    {
        String url = "https://qyapi.weixin.qq.com/cgi-bin/externalcontact/batch/get_by_user?access_token=" + accessToken;
        return postJsonWithRateLimitRetry(url, body);
    }

    /** 编辑客户企业标签（POST externalcontact/mark_tag） */
    public JSONObject markExternalContactTag(String accessToken, JSONObject body)
    {
        String url = "https://qyapi.weixin.qq.com/cgi-bin/externalcontact/mark_tag?access_token=" + accessToken;
        return postJsonWithRateLimitRetry(url, body);
    }

    /** 拉取微信客服消息（sync_msg），返回包含 msg_list / next_cursor / has_more 的 JSON */
    public JSONObject kfSyncMsg(String accessToken, String cursor, String token,
            String openKfid, int limit)
    {
        String url = "https://qyapi.weixin.qq.com/cgi-bin/kf/sync_msg?access_token=" + accessToken;
        JSONObject body = new JSONObject();
        body.put("cursor", cursor != null ? cursor : "");
        body.put("token", token);
        body.put("open_kfid", openKfid);
        body.put("limit", limit);
        return postJsonWithRateLimitRetry(url, body);
    }

    /** 发送微信客服消息（kf/send_msg） */
    public JSONObject kfSendMsg(String accessToken, JSONObject body)
    {
        String url = "https://qyapi.weixin.qq.com/cgi-bin/kf/send_msg?access_token=" + accessToken;
        return postJsonWithRateLimitRetry(url, body);
    }

    /**
     * 发送新客户欢迎语。41096 短间隔重试；41051 视为成功。
     */
    public JSONObject sendWelcomeMsg(String accessToken, String welcomeCode, String textContent)
    {
        JSONObject body = new JSONObject();
        body.put("welcome_code", welcomeCode);
        JSONObject text = new JSONObject();
        text.put("content", textContent);
        body.put("text", text);
        String url = "https://qyapi.weixin.qq.com/cgi-bin/externalcontact/send_welcome_msg?access_token=" + accessToken;
        long backoffMs = 200L;
        for (int attempt = 0; attempt < 4; attempt++)
        {
            JSONObject res = postJsonOnce(url, body);
            int err = res.getIntValue("errcode");
            if (err == 0 || err == ERRCODE_WELCOME_ALREADY_SENT)
            {
                if (err == ERRCODE_WELCOME_ALREADY_SENT)
                {
                    res.put("errcode", 0);
                    res.put("errmsg", "already_sent");
                }
                return res;
            }
            if (err == ERRCODE_WELCOME_CONFLICT && attempt < 3)
            {
                sleepQuietly(backoffMs + (long) (Math.random() * 300));
                backoffMs = Math.min(backoffMs + 100L, 500L);
                continue;
            }
            return res;
        }
        throw new AssertionError("sendWelcomeMsg retry loop");
    }

    /**
     * 上传临时素材到企业微信。
     * POST /cgi-bin/media/upload?access_token=TOKEN&type=TYPE
     * Content-Type: multipart/form-data
     *
     * @param accessToken 企业微信 agent access_token
     * @param mediaType   素材类型：image / voice / video / file
     * @param file        本地素材文件
     * @return 企微上传响应
     */
    public WecomMediaUploadResult uploadMedia(String accessToken, String mediaType, File file)
    {
        long backoffMs = 1000L;
        for (int attempt = 0; attempt < 4; attempt++)
        {
            WecomMediaUploadResult result = uploadMediaOnce(accessToken, mediaType, file);
            if (result.getErrcode() != ERRCODE_API_FREQ || attempt == 3)
            {
                return result;
            }
            sleepQuietly(backoffMs);
            backoffMs = Math.min(backoffMs * 2, 8000L);
        }
        throw new AssertionError("uploadMedia retry loop");
    }

    private WecomMediaUploadResult uploadMediaOnce(String accessToken, String mediaType, File file)
    {
        String url = "https://qyapi.weixin.qq.com/cgi-bin/media/upload"
                + "?access_token=" + accessToken
                + "&type=" + mediaType;

        FileSystemResource fileResource = new FileSystemResource(file);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("media", fileResource);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(body, headers);
        ResponseEntity<String> resp = restTemplate.postForEntity(url, entity, String.class);
        JSONObject res = JSON.parseObject(resp.getBody());

        WecomMediaUploadResult result = new WecomMediaUploadResult();
        result.setErrcode(res.getIntValue("errcode"));
        result.setErrmsg(res.getString("errmsg"));
        result.setType(res.getString("type"));
        result.setMediaId(res.getString("media_id"));
        result.setCreatedAt(res.getLong("created_at"));
        return result;
    }

    /**
     * 上传附件资源到企业微信（用于商品图册等场景）。
     * POST /cgi-bin/media/upload_attachment?access_token=TOKEN&media_type=TYPE&attachment_type=2
     * Content-Type: multipart/form-data
     *
     * @param accessToken 企业微信 agent access_token
     * @param mediaType   媒体文件类型：image / video / file
     * @param file        本地素材文件
     * @return 企微上传响应
     */
    public WecomMediaUploadResult uploadAttachment(String accessToken, String mediaType, File file, int attachmentType)
    {
        long backoffMs = 1000L;
        for (int attempt = 0; attempt < 4; attempt++)
        {
            WecomMediaUploadResult result = uploadAttachmentOnce(accessToken, mediaType, file, attachmentType);
            if (result.getErrcode() != ERRCODE_API_FREQ || attempt == 3)
            {
                return result;
            }
            sleepQuietly(backoffMs);
            backoffMs = Math.min(backoffMs * 2, 8000L);
        }
        throw new AssertionError("uploadAttachment retry loop");
    }

    private WecomMediaUploadResult uploadAttachmentOnce(String accessToken, String mediaType, File file, int attachmentType)
    {
        String url = "https://qyapi.weixin.qq.com/cgi-bin/media/upload_attachment"
                + "?access_token=" + accessToken
                + "&media_type=" + mediaType
                + "&attachment_type=" + attachmentType;

        FileSystemResource fileResource = new FileSystemResource(file);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("media", fileResource);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(body, headers);
        ResponseEntity<String> resp = restTemplate.postForEntity(url, entity, String.class);
        JSONObject res = JSON.parseObject(resp.getBody());

        WecomMediaUploadResult result = new WecomMediaUploadResult();
        result.setErrcode(res.getIntValue("errcode"));
        result.setErrmsg(res.getString("errmsg"));
        result.setType(res.getString("type"));
        result.setMediaId(res.getString("media_id"));
        result.setCreatedAt(res.getLong("created_at"));
        return result;
    }

    private JSONObject getJsonWithRateLimitRetry(String url)
    {
        long backoffMs = 1000L;
        for (int attempt = 0; attempt < 4; attempt++)
        {
            ResponseEntity<String> resp = restTemplate.getForEntity(url, String.class);
            JSONObject res = JSON.parseObject(resp.getBody());
            if (res.getIntValue("errcode") != ERRCODE_API_FREQ || attempt == 3)
            {
                return res;
            }
            sleepQuietly(backoffMs);
            backoffMs = Math.min(backoffMs * 2, 8000L);
        }
        throw new AssertionError("getJsonWithRateLimitRetry loop");
    }

    private JSONObject postJsonWithRateLimitRetry(String url, JSONObject body)
    {
        long backoffMs = 1000L;
        for (int attempt = 0; attempt < 4; attempt++)
        {
            JSONObject res = postJsonOnce(url, body);
            if (res.getIntValue("errcode") != ERRCODE_API_FREQ || attempt == 3)
            {
                return res;
            }
            sleepQuietly(backoffMs);
            backoffMs = Math.min(backoffMs * 2, 8000L);
        }
        throw new AssertionError("postJsonWithRateLimitRetry loop");
    }

    private JSONObject postJsonOnce(String url, JSONObject body)
    {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(body.toJSONString(), headers);
        ResponseEntity<String> resp = restTemplate.postForEntity(url, entity, String.class);
        return JSON.parseObject(resp.getBody());
    }

    /**
     * 通用 POST（Map 载荷）。
     */
    public JSONObject postJson(String url, Map<String, Object> body)
    {
        return postJsonWithRateLimitRetry(url, new JSONObject(body));
    }

    /**
     * 通过 OAuth code 获取企业微信成员 UserId。
     * GET /cgi-bin/user/getuserinfo?access_token=TOKEN&code=CODE
     */
    public JSONObject userGetUserInfo(String accessToken, String code)
    {
        try
        {
            String url = "https://qyapi.weixin.qq.com/cgi-bin/user/getuserinfo?access_token=" + accessToken
                    + "&code=" + URLEncoder.encode(code, StandardCharsets.UTF_8.name());
            return getJsonWithRateLimitRetry(url);
        }
        catch (java.io.UnsupportedEncodingException e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     * 通过 userid 获取企业微信成员完整信息。
     * GET /cgi-bin/user/get?access_token=TOKEN&userid=USERID
     */
    public JSONObject userGet(String accessToken, String userId)
    {
        try
        {
            String url = "https://qyapi.weixin.qq.com/cgi-bin/user/get?access_token=" + accessToken
                    + "&userid=" + URLEncoder.encode(userId, StandardCharsets.UTF_8.name());
            return getJsonWithRateLimitRetry(url);
        }
        catch (java.io.UnsupportedEncodingException e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     * 创建企业微信商品图册。
     * POST /cgi-bin/externalcontact/add_product_album?access_token=ACCESS_TOKEN
     */
    public JSONObject addProductAlbum(String accessToken, JSONObject body)
    {
        String url = "https://qyapi.weixin.qq.com/cgi-bin/externalcontact/add_product_album?access_token=" + accessToken;
        return postJsonWithRateLimitRetry(url, body);
    }

    /**
     * 更新企业微信商品图册。
     * POST /cgi-bin/externalcontact/update_product_album?access_token=ACCESS_TOKEN
     */
    public JSONObject updateProductAlbum(String accessToken, JSONObject body)
    {
        String url = "https://qyapi.weixin.qq.com/cgi-bin/externalcontact/update_product_album?access_token=" + accessToken;
        return postJsonWithRateLimitRetry(url, body);
    }

    /**
     * 删除企业微信商品图册。
     * POST /cgi-bin/externalcontact/delete_product_album?access_token=ACCESS_TOKEN
     */
    public JSONObject deleteProductAlbum(String accessToken, JSONObject body)
    {
        String url = "https://qyapi.weixin.qq.com/cgi-bin/externalcontact/delete_product_album?access_token=" + accessToken;
        return postJsonWithRateLimitRetry(url, body);
    }

    /**
     * 获取企业 jsapi_ticket（corp 级签名）。
     * GET /cgi-bin/get_jsapi_ticket?access_token=ACCESS_TOKEN
     */
    public JSONObject getJsapiTicket(String accessToken)
    {
        String url = "https://qyapi.weixin.qq.com/cgi-bin/get_jsapi_ticket?access_token=" + accessToken;
        return getJsonWithRateLimitRetry(url);
    }

    /**
     * 获取应用 agent_config ticket（应用级 JS-SDK 签名）。
     * GET /cgi-bin/ticket/get?access_token=ACCESS_TOKEN&type=agent_config
     */
    public JSONObject getAgentConfigTicket(String accessToken)
    {
        String url = "https://qyapi.weixin.qq.com/cgi-bin/ticket/get?access_token=" + accessToken
                + "&type=agent_config";
        return getJsonWithRateLimitRetry(url);
    }

    /**
     * 发送企业微信应用消息。
     * POST /cgi-bin/message/send?access_token=ACCESS_TOKEN
     */
    public JSONObject sendAppMessage(String accessToken, JSONObject body)
    {
        String url = "https://qyapi.weixin.qq.com/cgi-bin/message/send?access_token=" + accessToken;
        return postJsonWithRateLimitRetry(url, body);
    }

    /**
     * 下载临时素材。
     * GET /cgi-bin/media/get?access_token=TOKEN&media_id=MEDIA_ID
     */
    public WecomMediaFetchResult getTemporaryMedia(String accessToken, String mediaId)
    {
        try
        {
            String url = "https://qyapi.weixin.qq.com/cgi-bin/media/get?access_token=" + accessToken
                    + "&media_id=" + URLEncoder.encode(mediaId, StandardCharsets.UTF_8.name());
            return getMediaWithRateLimitRetry(url);
        }
        catch (RuntimeException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    private WecomMediaFetchResult getMediaWithRateLimitRetry(String url)
    {
        long backoffMs = 1000L;
        for (int attempt = 0; attempt < 4; attempt++)
        {
            ResponseEntity<byte[]> resp = restTemplate.exchange(url, HttpMethod.GET, null, byte[].class);
            WecomMediaFetchResult result = WecomMediaFetchResult.fromHttpResponse(resp);
            if (!result.isSuccess() && result.getErrcode() == ERRCODE_API_FREQ && attempt < 3)
            {
                sleepQuietly(backoffMs);
                backoffMs = Math.min(backoffMs * 2, 8000L);
                continue;
            }
            return result;
        }
        throw new AssertionError("getMediaWithRateLimitRetry loop");
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
