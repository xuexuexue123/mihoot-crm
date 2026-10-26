package com.ruoyi.crm.wechatmp;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;

/**
 * 微信公众号服务端 API 封装（token 由调用方传入）。
 */
@Component
public class WechatMpTencentApiClient
{
    private static final int ERRCODE_TOKEN_INVALID = 40001;
    private static final int ERRCODE_API_FREQ = 45009;

    private final RestTemplate restTemplate = createUtf8RestTemplate();

    private static RestTemplate createUtf8RestTemplate()
    {
        RestTemplate template = new RestTemplate();
        template.getMessageConverters().removeIf(StringHttpMessageConverter.class::isInstance);
        template.getMessageConverters().add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));
        return template;
    }

    public JSONObject getUserInfo(String accessToken, String openId)
    {
        try
        {
            String url = "https://api.weixin.qq.com/cgi-bin/user/info?access_token=" + accessToken
                    + "&openid=" + URLEncoder.encode(openId, StandardCharsets.UTF_8.name())
                    + "&lang=zh_CN";
            return getJsonWithRateLimitRetry(url);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    public JSONObject createMenu(String accessToken, JSONObject menuBody)
    {
        String url = "https://api.weixin.qq.com/cgi-bin/menu/create?access_token=" + accessToken;
        return postJsonWithRateLimitRetry(url, menuBody);
    }

    public JSONObject getMenu(String accessToken)
    {
        String url = "https://api.weixin.qq.com/cgi-bin/menu/get?access_token=" + accessToken;
        return getJsonWithRateLimitRetry(url);
    }

    public JSONObject deleteMenu(String accessToken)
    {
        String url = "https://api.weixin.qq.com/cgi-bin/menu/delete?access_token=" + accessToken;
        return getJsonWithRateLimitRetry(url);
    }

    public JSONObject createQrCode(String accessToken, JSONObject body)
    {
        String url = "https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token=" + accessToken;
        return postJsonWithRateLimitRetry(url, body);
    }

    public JSONObject getFollowers(String accessToken, String nextOpenId)
    {
        try
        {
            String url = "https://api.weixin.qq.com/cgi-bin/user/get?access_token=" + accessToken;
            if (nextOpenId != null && !nextOpenId.isEmpty())
            {
                url += "&next_openid=" + URLEncoder.encode(nextOpenId, StandardCharsets.UTF_8.name());
            }
            return getJsonWithRateLimitRetry(url);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    public JSONObject batchGetUserInfo(String accessToken, JSONObject body)
    {
        String url = "https://api.weixin.qq.com/cgi-bin/user/info/batchget?access_token=" + accessToken;
        return postJsonWithRateLimitRetry(url, body);
    }

    public JSONObject getAccountBasicInfo(String accessToken)
    {
        String url = "https://api.weixin.qq.com/cgi-bin/account/getaccountbasicinfo?access_token=" + accessToken;
        return getJsonWithRateLimitRetry(url);
    }

    public JSONObject sendCustomMessage(String accessToken, JSONObject body)
    {
        String url = "https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token=" + accessToken;
        return postJsonWithRateLimitRetry(url, body);
    }

    public JSONObject sendTemplateMessage(String accessToken, JSONObject body)
    {
        String url = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=" + accessToken;
        return postJsonWithRateLimitRetry(url, body);
    }

    // ======================== 官方群发 ========================

    /**
     * 按标签 / 全体群发。
     * POST https://api.weixin.qq.com/cgi-bin/message/mass/sendall
     */
    public JSONObject massSendAll(String accessToken, JSONObject body)
    {
        String url = "https://api.weixin.qq.com/cgi-bin/message/mass/sendall?access_token=" + accessToken;
        return postJsonWithRateLimitRetry(url, body);
    }

    /**
     * 群发预览。
     * POST https://api.weixin.qq.com/cgi-bin/message/mass/preview
     */
    public JSONObject massPreview(String accessToken, JSONObject body)
    {
        String url = "https://api.weixin.qq.com/cgi-bin/message/mass/preview?access_token=" + accessToken;
        return postJsonWithRateLimitRetry(url, body);
    }

    /**
     * 查询群发消息状态。
     * POST https://api.weixin.qq.com/cgi-bin/message/mass/get
     */
    public JSONObject massGet(String accessToken, String msgId)
    {
        String url = "https://api.weixin.qq.com/cgi-bin/message/mass/get?access_token=" + accessToken;
        JSONObject body = new JSONObject();
        body.put("msg_id", msgId);
        return postJsonWithRateLimitRetry(url, body);
    }

    /**
     * 删除群发。
     * POST https://api.weixin.qq.com/cgi-bin/message/mass/delete
     *
     * @param articleIdx 图文文章位置，0 表示全部；可为 null
     */
    public JSONObject massDelete(String accessToken, String msgId, Integer articleIdx)
    {
        String url = "https://api.weixin.qq.com/cgi-bin/message/mass/delete?access_token=" + accessToken;
        JSONObject body = new JSONObject();
        body.put("msg_id", msgId);
        if (articleIdx != null)
        {
            body.put("article_idx", articleIdx);
        }
        return postJsonWithRateLimitRetry(url, body);
    }

    public JSONObject exchangeOAuthCode(String appId, String secret, String code)
    {
        try
        {
            String url = "https://api.weixin.qq.com/sns/oauth2/access_token"
                    + "?appid=" + URLEncoder.encode(appId, StandardCharsets.UTF_8.name())
                    + "&secret=" + URLEncoder.encode(secret, StandardCharsets.UTF_8.name())
                    + "&code=" + URLEncoder.encode(code, StandardCharsets.UTF_8.name())
                    + "&grant_type=authorization_code";
            return getJsonWithRateLimitRetry(url);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    public String buildOAuthAuthorizeUrl(String appId, String redirectUri, String state)
    {
        try
        {
            return "https://open.weixin.qq.com/connect/oauth2/authorize"
                    + "?appid=" + URLEncoder.encode(appId, StandardCharsets.UTF_8.name())
                    + "&redirect_uri=" + URLEncoder.encode(redirectUri, StandardCharsets.UTF_8.name())
                    + "&response_type=code&scope=snsapi_base&state="
                    + URLEncoder.encode(state, StandardCharsets.UTF_8.name())
                    + "#wechat_redirect";
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    public JSONObject getTemplateList(String accessToken)
    {
        String url = "https://api.weixin.qq.com/cgi-bin/template/get_all_private_template?access_token=" + accessToken;
        return getJsonWithRateLimitRetry(url);
    }

    public JSONObject batchGetPublishedArticles(String accessToken, int offset, int count)
    {
        String url = "https://api.weixin.qq.com/cgi-bin/freepublish/batchget?access_token=" + accessToken;
        JSONObject body = new JSONObject();
        body.put("offset", offset);
        body.put("count", count);
        body.put("no_content", 0);
        return postJsonWithRateLimitRetry(url, body);
    }

    /**
     * 获取已发布图文的详细数据（HTML正文、原文链接、评论设置等）。
     * 微信文档: POST https://api.weixin.qq.com/cgi-bin/freepublish/getarticle
     */
    public JSONObject getPublishedArticle(String accessToken, String articleId)
    {
        String url = "https://api.weixin.qq.com/cgi-bin/freepublish/getarticle?access_token=" + accessToken;
        JSONObject body = new JSONObject();
        body.put("article_id", articleId);
        return postJsonWithRateLimitRetry(url, body);
    }

    /**
     * 发布草稿（将图文草稿提交发布）。
     * 微信文档: POST https://api.weixin.qq.com/cgi-bin/freepublish/submit
     *
     * @param accessToken access_token
     * @param mediaId     要发布的草稿 media_id
     * @return 微信响应，成功时包含 publish_id、msg_data_id 等字段
     */
    public JSONObject freepublishSubmit(String accessToken, String mediaId)
    {
        String url = "https://api.weixin.qq.com/cgi-bin/freepublish/submit?access_token=" + accessToken;
        JSONObject body = new JSONObject();
        body.put("media_id", mediaId);
        return postJsonWithRateLimitRetry(url, body);
    }

    /**
     * 查询发布任务状态。
     * 微信文档: POST https://api.weixin.qq.com/cgi-bin/freepublish/get
     *
     * @param accessToken access_token
     * @param publishId   发布任务 id
     * @return 微信响应，包含 publish_status、article_id、article_detail 等字段
     */
    public JSONObject freepublishGet(String accessToken, String publishId)
    {
        String url = "https://api.weixin.qq.com/cgi-bin/freepublish/get?access_token=" + accessToken;
        JSONObject body = new JSONObject();
        body.put("publish_id", publishId);
        return postJsonWithRateLimitRetry(url, body);
    }

    // ======================== 草稿管理 ========================

    /**
     * 获取草稿列表。
     * 微信文档: POST https://api.weixin.qq.com/cgi-bin/draft/batchget
     */
    public JSONObject batchGetDrafts(String accessToken, int offset, int count, int noContent)
    {
        String url = "https://api.weixin.qq.com/cgi-bin/draft/batchget?access_token=" + accessToken;
        JSONObject body = new JSONObject();
        body.put("offset", offset);
        body.put("count", count);
        body.put("no_content", noContent);
        return postJsonWithRateLimitRetry(url, body);
    }

    /**
     * 获取草稿详情。
     * 微信文档: POST https://api.weixin.qq.com/cgi-bin/draft/get
     */
    public JSONObject getDraft(String accessToken, String mediaId)
    {
        String url = "https://api.weixin.qq.com/cgi-bin/draft/get?access_token=" + accessToken;
        JSONObject body = new JSONObject();
        body.put("media_id", mediaId);
        return postJsonWithRateLimitRetry(url, body);
    }

    /**
     * 新增草稿。
     * 微信文档: POST https://api.weixin.qq.com/cgi-bin/draft/add
     */
    public JSONObject addDraft(String accessToken, JSONObject body)
    {
        String url = "https://api.weixin.qq.com/cgi-bin/draft/add?access_token=" + accessToken;
        return postJsonWithRateLimitRetry(url, body);
    }

    /**
     * 修改草稿。
     * 微信文档: POST https://api.weixin.qq.com/cgi-bin/draft/update
     */
    public JSONObject updateDraft(String accessToken, JSONObject body)
    {
        String url = "https://api.weixin.qq.com/cgi-bin/draft/update?access_token=" + accessToken;
        return postJsonWithRateLimitRetry(url, body);
    }

    /**
     * 删除草稿。
     * 微信文档: POST https://api.weixin.qq.com/cgi-bin/draft/delete
     */
    public JSONObject deleteDraft(String accessToken, String mediaId)
    {
        String url = "https://api.weixin.qq.com/cgi-bin/draft/delete?access_token=" + accessToken;
        JSONObject body = new JSONObject();
        body.put("media_id", mediaId);
        return postJsonWithRateLimitRetry(url, body);
    }

    // ======================== 图文留言 comment/* ========================

    /**
     * 查看指定文章的评论数据。
     * POST https://api.weixin.qq.com/cgi-bin/comment/list
     *
     * @param type 0 普通评论+精选评论，1 普通评论，2 精选评论
     */
    public JSONObject commentList(String accessToken, String msgDataId, int index, int begin, int count, int type)
    {
        String url = "https://api.weixin.qq.com/cgi-bin/comment/list?access_token=" + accessToken;
        JSONObject body = new JSONObject();
        body.put("msg_data_id", msgDataId);
        body.put("index", index);
        body.put("begin", begin);
        body.put("count", count);
        body.put("type", type);
        return postJsonWithRateLimitRetry(url, body);
    }

    /**
     * 回复评论。
     * POST https://api.weixin.qq.com/cgi-bin/comment/reply/add
     */
    public JSONObject commentReplyAdd(String accessToken, String msgDataId, int index, long userCommentId,
            String content)
    {
        String url = "https://api.weixin.qq.com/cgi-bin/comment/reply/add?access_token=" + accessToken;
        JSONObject body = new JSONObject();
        body.put("msg_data_id", msgDataId);
        body.put("index", index);
        body.put("user_comment_id", userCommentId);
        body.put("content", content);
        return postJsonWithRateLimitRetry(url, body);
    }

    /**
     * 打开已群发文章评论。
     * POST https://api.weixin.qq.com/cgi-bin/comment/open
     */
    public JSONObject commentOpen(String accessToken, String msgDataId, int index)
    {
        String url = "https://api.weixin.qq.com/cgi-bin/comment/open?access_token=" + accessToken;
        JSONObject body = new JSONObject();
        body.put("msg_data_id", msgDataId);
        body.put("index", index);
        return postJsonWithRateLimitRetry(url, body);
    }

    /**
     * 关闭已群发文章评论。
     * POST https://api.weixin.qq.com/cgi-bin/comment/close
     */
    public JSONObject commentClose(String accessToken, String msgDataId, int index)
    {
        String url = "https://api.weixin.qq.com/cgi-bin/comment/close?access_token=" + accessToken;
        JSONObject body = new JSONObject();
        body.put("msg_data_id", msgDataId);
        body.put("index", index);
        return postJsonWithRateLimitRetry(url, body);
    }

    /**
     * 将评论标记精选。
     * POST https://api.weixin.qq.com/cgi-bin/comment/markelect
     */
    public JSONObject commentMarkelect(String accessToken, String msgDataId, int index, long userCommentId)
    {
        String url = "https://api.weixin.qq.com/cgi-bin/comment/markelect?access_token=" + accessToken;
        JSONObject body = new JSONObject();
        body.put("msg_data_id", msgDataId);
        body.put("index", index);
        body.put("user_comment_id", userCommentId);
        return postJsonWithRateLimitRetry(url, body);
    }

    /**
     * 取消精选。
     * POST https://api.weixin.qq.com/cgi-bin/comment/unmarkelect
     */
    public JSONObject commentUnmarkelect(String accessToken, String msgDataId, int index, long userCommentId)
    {
        String url = "https://api.weixin.qq.com/cgi-bin/comment/unmarkelect?access_token=" + accessToken;
        JSONObject body = new JSONObject();
        body.put("msg_data_id", msgDataId);
        body.put("index", index);
        body.put("user_comment_id", userCommentId);
        return postJsonWithRateLimitRetry(url, body);
    }

    /**
     * 删除评论。
     * POST https://api.weixin.qq.com/cgi-bin/comment/delete
     */
    public JSONObject commentDelete(String accessToken, String msgDataId, int index, long userCommentId)
    {
        String url = "https://api.weixin.qq.com/cgi-bin/comment/delete?access_token=" + accessToken;
        JSONObject body = new JSONObject();
        body.put("msg_data_id", msgDataId);
        body.put("index", index);
        body.put("user_comment_id", userCommentId);
        return postJsonWithRateLimitRetry(url, body);
    }

    /**
     * 删除回复。
     * POST https://api.weixin.qq.com/cgi-bin/comment/reply/delete
     */
    public JSONObject commentReplyDelete(String accessToken, String msgDataId, int index, long userCommentId)
    {
        String url = "https://api.weixin.qq.com/cgi-bin/comment/reply/delete?access_token=" + accessToken;
        JSONObject body = new JSONObject();
        body.put("msg_data_id", msgDataId);
        body.put("index", index);
        body.put("user_comment_id", userCommentId);
        return postJsonWithRateLimitRetry(url, body);
    }

    /**
     * 调用微信公众号数据统计 (datacube) 接口。
     *
     * @param accessToken access_token
     * @param endpoint    datacube 路径名，如 getupstreammsg
     * @param beginDate   起始日期 yyyy-MM-dd
     * @param endDate     结束日期 yyyy-MM-dd
     * @return 微信原始 JSON 响应
     */
    public JSONObject callDatacube(String accessToken, String endpoint, String beginDate, String endDate)
    {
        String url = "https://api.weixin.qq.com/datacube/" + endpoint + "?access_token=" + accessToken;
        JSONObject body = new JSONObject();
        body.put("begin_date", beginDate);
        body.put("end_date", endDate);
        return postJsonWithRateLimitRetry(url, body);
    }

    /**
     * 获取发表内容发表详细数据。
     * 微信文档: POST https://api.weixin.qq.com/datacube/getarticletotaldetail
     *
     * @param accessToken access_token
     * @param beginDate   起始日期 yyyy-MM-dd
     * @param endDate     结束日期 yyyy-MM-dd（最大为昨日）
     * @return 微信原始 JSON 响应，list 中包含 msgid、title、detail_list 等字段
     */
    public JSONObject getArticleTotalDetail(String accessToken, String beginDate, String endDate)
    {
        return callDatacube(accessToken, "getarticletotaldetail", beginDate, endDate);
    }

    /**
     * 获取临时素材（成功返回二进制，失败返回 JSON errcode）。
     */
    public WechatMpMediaFetchResult getTemporaryMedia(String accessToken, String mediaId)
    {
        try
        {
            String url = "https://api.weixin.qq.com/cgi-bin/media/get?access_token=" + accessToken
                    + "&media_id=" + URLEncoder.encode(mediaId, StandardCharsets.UTF_8.name());
            return getMediaWithRateLimitRetry(url);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    // ======================== 永久素材 ========================

    /**
     * 获取永久素材列表。
     * POST https://api.weixin.qq.com/cgi-bin/material/batchget_material
     */
    public JSONObject batchGetMaterial(String accessToken, String type, int offset, int count)
    {
        String url = "https://api.weixin.qq.com/cgi-bin/material/batchget_material?access_token=" + accessToken;
        JSONObject body = new JSONObject();
        body.put("type", type);
        body.put("offset", offset);
        body.put("count", count);
        return postJsonWithRateLimitRetry(url, body);
    }

    /**
     * 新增永久素材。
     * POST https://api.weixin.qq.com/cgi-bin/material/add_material?access_token=ACCESS_TOKEN&type=TYPE
     *
     * @param descriptionJson 视频时可选，JSON 字符串 {"title":"...","introduction":"..."}
     */
    public JSONObject addMaterial(String accessToken, String type, java.io.File file, String descriptionJson)
    {
        long backoffMs = 1000L;
        for (int attempt = 0; attempt < 4; attempt++)
        {
            JSONObject res = addMaterialOnce(accessToken, type, file, descriptionJson);
            if (!isRateLimited(res) || attempt == 3)
            {
                return res;
            }
            sleepQuietly(backoffMs);
            backoffMs = Math.min(backoffMs * 2, 8000L);
        }
        throw new AssertionError("addMaterial retry loop");
    }

    /**
     * 删除永久素材。
     * POST https://api.weixin.qq.com/cgi-bin/material/del_material
     */
    public JSONObject delMaterial(String accessToken, String mediaId)
    {
        String url = "https://api.weixin.qq.com/cgi-bin/material/del_material?access_token=" + accessToken;
        JSONObject body = new JSONObject();
        body.put("media_id", mediaId);
        return postJsonWithRateLimitRetry(url, body);
    }

    /**
     * 获取永久素材详情。
     * POST https://api.weixin.qq.com/cgi-bin/material/get_material
     * news/video 返回 JSON；image/voice 返回二进制。
     */
    public WechatMpMaterialFetchResult getMaterial(String accessToken, String mediaId)
    {
        String url = "https://api.weixin.qq.com/cgi-bin/material/get_material?access_token=" + accessToken;
        JSONObject body = new JSONObject();
        body.put("media_id", mediaId);
        return postMaterialWithRateLimitRetry(url, body);
    }

    /**
     * 上传图文消息内的图片（不占用素材库容量）。
     * POST https://api.weixin.qq.com/cgi-bin/media/uploadimg?access_token=ACCESS_TOKEN
     * Content-Type: multipart/form-data
     *
     * @param accessToken access_token
     * @param file        图片文件（jpg/png，1MB 以内）
     * @return 微信响应，成功时包含 url 字段
     */
    public JSONObject uploadImage(String accessToken, java.io.File file)
    {
        long backoffMs = 1000L;
        for (int attempt = 0; attempt < 4; attempt++)
        {
            JSONObject res = uploadImageOnce(accessToken, file);
            if (!isRateLimited(res) || attempt == 3)
            {
                return res;
            }
            sleepQuietly(backoffMs);
            backoffMs = Math.min(backoffMs * 2, 8000L);
        }
        throw new AssertionError("uploadImage retry loop");
    }

    private JSONObject uploadImageOnce(String accessToken, java.io.File file)
    {
        String url = "https://api.weixin.qq.com/cgi-bin/media/uploadimg?access_token=" + accessToken;

        FileSystemResource fileResource = new FileSystemResource(file);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("media", fileResource);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(body, headers);
        ResponseEntity<String> resp = restTemplate.postForEntity(url, entity, String.class);
        return JSON.parseObject(resp.getBody());
    }

    /**
     * 上传临时素材（客服消息用 media_id，有效期约 3 天）。
     * POST https://api.weixin.qq.com/cgi-bin/media/upload?access_token=ACCESS_TOKEN&amp;type=TYPE
     *
     * @param type image | voice | video | thumb
     */
    public JSONObject uploadTemporaryMedia(String accessToken, String type, java.io.File file)
    {
        long backoffMs = 1000L;
        for (int attempt = 0; attempt < 4; attempt++)
        {
            JSONObject res = uploadTemporaryMediaOnce(accessToken, type, file);
            if (!isRateLimited(res) || attempt == 3)
            {
                return res;
            }
            sleepQuietly(backoffMs);
            backoffMs = Math.min(backoffMs * 2, 8000L);
        }
        throw new AssertionError("uploadTemporaryMedia retry loop");
    }

    private JSONObject uploadTemporaryMediaOnce(String accessToken, String type, java.io.File file)
    {
        String url = "https://api.weixin.qq.com/cgi-bin/media/upload?access_token=" + accessToken
                + "&type=" + type;

        FileSystemResource fileResource = new FileSystemResource(file);
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("media", fileResource);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(body, headers);
        ResponseEntity<String> resp = restTemplate.postForEntity(url, entity, String.class);
        return JSON.parseObject(resp.getBody());
    }

    private JSONObject addMaterialOnce(String accessToken, String type, java.io.File file, String descriptionJson)
    {
        String url = "https://api.weixin.qq.com/cgi-bin/material/add_material?access_token=" + accessToken
                + "&type=" + type;

        FileSystemResource fileResource = new FileSystemResource(file);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("media", fileResource);
        if (descriptionJson != null && !descriptionJson.isEmpty())
        {
            body.add("description", descriptionJson);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(body, headers);
        ResponseEntity<String> resp = restTemplate.postForEntity(url, entity, String.class);
        return JSON.parseObject(resp.getBody());
    }

    private WechatMpMaterialFetchResult postMaterialWithRateLimitRetry(String url, JSONObject body)
    {
        long backoffMs = 1000L;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(body.toJSONString(), headers);
        for (int attempt = 0; attempt < 4; attempt++)
        {
            ResponseEntity<byte[]> resp = restTemplate.exchange(url, HttpMethod.POST, entity, byte[].class);
            WechatMpMaterialFetchResult result = WechatMpMaterialFetchResult.fromHttpResponse(resp);
            if (result.isError() && result.getErrcode() == ERRCODE_API_FREQ && attempt < 3)
            {
                sleepQuietly(backoffMs);
                backoffMs = Math.min(backoffMs * 2, 8000L);
                continue;
            }
            return result;
        }
        throw new AssertionError("postMaterialWithRateLimitRetry loop");
    }

    public boolean isTokenInvalid(JSONObject res)
    {
        return res != null && res.getIntValue("errcode") == ERRCODE_TOKEN_INVALID;
    }

    public boolean isRateLimited(JSONObject res)
    {
        return res != null && res.getIntValue("errcode") == ERRCODE_API_FREQ;
    }

    private JSONObject getJsonWithRateLimitRetry(String url)
    {
        long backoffMs = 1000L;
        for (int attempt = 0; attempt < 4; attempt++)
        {
            ResponseEntity<String> resp = restTemplate.getForEntity(url, String.class);
            JSONObject res = JSON.parseObject(resp.getBody());
            if (isRateLimited(res) && attempt < 3)
            {
                sleepQuietly(backoffMs);
                backoffMs = Math.min(backoffMs * 2, 8000L);
                continue;
            }
            return res;
        }
        throw new AssertionError("getJsonWithRateLimitRetry loop");
    }

    private JSONObject postJsonWithRateLimitRetry(String url, JSONObject body)
    {
        long backoffMs = 1000L;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(body.toJSONString(), headers);
        for (int attempt = 0; attempt < 4; attempt++)
        {
            ResponseEntity<String> resp = restTemplate.postForEntity(url, entity, String.class);
            JSONObject res = JSON.parseObject(resp.getBody());
            if (isRateLimited(res) && attempt < 3)
            {
                sleepQuietly(backoffMs);
                backoffMs = Math.min(backoffMs * 2, 8000L);
                continue;
            }
            return res;
        }
        throw new AssertionError("postJsonWithRateLimitRetry loop");
    }

    private WechatMpMediaFetchResult getMediaWithRateLimitRetry(String url)
    {
        long backoffMs = 1000L;
        for (int attempt = 0; attempt < 4; attempt++)
        {
            ResponseEntity<byte[]> resp = restTemplate.exchange(url, HttpMethod.GET, null, byte[].class);
            WechatMpMediaFetchResult result = WechatMpMediaFetchResult.fromHttpResponse(resp);
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

    private void sleepQuietly(long ms)
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
