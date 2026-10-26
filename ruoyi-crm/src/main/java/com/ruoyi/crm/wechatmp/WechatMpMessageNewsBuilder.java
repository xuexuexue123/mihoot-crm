package com.ruoyi.crm.wechatmp;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;

import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutNewsMessage;

/**
 * 公众号图文消息构建 & 校验工具类。
 * <p>
 * 封装 articles 校验、被动回复 XML 构建、客服消息 JSON 构建、replyContent 解析，
 * 供自动回复、客服主动消息、Smart Push 批量发送三处共用。
 */
public final class WechatMpMessageNewsBuilder
{
    private static final int MAX_ARTICLES = 8;
    private static final String URL_PATTERN = "^https?://.*$";

    private WechatMpMessageNewsBuilder() {}

    /**
     * 校验 articles 数组合法性。
     * <ul>
     *   <li>非空且大小 1-8</li>
     *   <li>每项 title 必填</li>
     *   <li>若 url 非空，需以 http:// 或 https:// 开头</li>
     * </ul>
     *
     * @param articles 图文列表
     * @throws ServiceException 校验不通过
     */
    public static void validateArticles(List<Map<String, String>> articles)
    {
        if (articles == null || articles.isEmpty())
        {
            throw new ServiceException("图文消息 articles 不能为空", HttpStatus.BAD_REQUEST);
        }
        if (articles.size() > MAX_ARTICLES)
        {
            throw new ServiceException("图文消息最多支持 " + MAX_ARTICLES + " 条", HttpStatus.BAD_REQUEST);
        }
        for (int i = 0; i < articles.size(); i++)
        {
            Map<String, String> item = articles.get(i);
            String title = item != null ? item.get("title") : null;
            if (StringUtils.isEmpty(title))
            {
                throw new ServiceException("第 " + (i + 1) + " 条图文消息标题不能为空", HttpStatus.BAD_REQUEST);
            }
            String url = item != null ? item.get("url") : null;
            if (url != null && !url.isEmpty() && !url.matches(URL_PATTERN))
            {
                throw new ServiceException("第 " + (i + 1) + " 条图文消息 URL 格式不正确", HttpStatus.BAD_REQUEST);
            }
        }
    }

    /**
     * 校验 JSONArray 格式的 articles（供 BatchWorker 使用，无需二次解析为 Map）。
     */
    public static void validateArticlesFromJsonArray(JSONArray articles)
    {
        if (articles == null || articles.isEmpty())
        {
            throw new ServiceException("图文消息 articles 不能为空", HttpStatus.BAD_REQUEST);
        }
        if (articles.size() > MAX_ARTICLES)
        {
            throw new ServiceException("图文消息最多支持 " + MAX_ARTICLES + " 条", HttpStatus.BAD_REQUEST);
        }
        for (int i = 0; i < articles.size(); i++)
        {
            JSONObject item = articles.getJSONObject(i);
            if (item == null || StringUtils.isEmpty(item.getString("title")))
            {
                throw new ServiceException("第 " + (i + 1) + " 条图文消息标题不能为空", HttpStatus.BAD_REQUEST);
            }
            String url = item.getString("url");
            if (url != null && !url.isEmpty() && !url.matches(URL_PATTERN))
            {
                throw new ServiceException("第 " + (i + 1) + " 条图文消息 URL 格式不正确", HttpStatus.BAD_REQUEST);
            }
        }
    }

    /**
     * 构建微信客服消息 /news 类型的请求体。
     *
     * <pre>{@code
     * {
     *   "touser": "OPENID",
     *   "msgtype": "news",
     *   "news": {
     *     "articles": [ { "title": "...", "description": "...", "url": "...", "picurl": "..." } ]
     *   }
     * }
     * }</pre>
     */
    public static JSONObject buildCustomMessageBody(String openId, JSONArray articles)
    {
        JSONObject body = new JSONObject();
        body.put("touser", openId);
        body.put("msgtype", "news");
        JSONObject news = new JSONObject();
        news.put("articles", articles);
        body.put("news", news);
        return body;
    }

    /**
     * 构建被动回复 /news 类型的 XML。
     * <p>
     * 使用 {@link WxMpXmlOutMessage#NEWS()} builder 构造。
     */
    public static String buildPassiveReplyXml(WxMpXmlMessage wxMessage, JSONArray articles)
    {
        List<WxMpXmlOutNewsMessage.Item> items = new ArrayList<>(articles.size());
        for (int i = 0; i < articles.size(); i++)
        {
            JSONObject obj = articles.getJSONObject(i);
            WxMpXmlOutNewsMessage.Item item = new WxMpXmlOutNewsMessage.Item();
            item.setTitle(obj.getString("title"));
            item.setDescription(obj.getString("description"));
            item.setPicUrl(obj.getString("picurl"));
            item.setUrl(obj.getString("url"));
            items.add(item);
        }
        return WxMpXmlOutMessage.NEWS()
                .articles(items)
                .fromUser(wxMessage.getToUser())
                .toUser(wxMessage.getFromUser())
                .build()
                .toXml();
    }

    /**
     * 从 replyContent JSON 字符串中解析 articles 数组。
     * <pre>{@code
     * { "articles": [ { "title": "...", "description": "...", "picurl": "...", "url": "..." } ] }
     * }</pre>
     *
     * @param replyContentJson JSON 字符串
     * @return fastjson2 JSONArray 对象
     * @throws ServiceException 解析失败或 articles 缺失
     */
    public static JSONArray parseArticles(String replyContentJson)
    {
        if (StringUtils.isEmpty(replyContentJson))
        {
            throw new ServiceException("图文消息内容不能为空", HttpStatus.BAD_REQUEST);
        }
        JSONObject obj;
        try
        {
            obj = JSONObject.parseObject(replyContentJson);
        }
        catch (Exception e)
        {
            throw new ServiceException("图文消息内容 JSON 解析失败: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        JSONArray articles = obj.getJSONArray("articles");
        if (articles == null || articles.isEmpty())
        {
            throw new ServiceException("图文消息 articles 不能为空", HttpStatus.BAD_REQUEST);
        }
        return articles;
    }
}
