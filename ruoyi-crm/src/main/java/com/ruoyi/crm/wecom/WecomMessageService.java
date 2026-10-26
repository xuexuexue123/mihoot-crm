package com.ruoyi.crm.wecom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.crm.domain.CrmWecomConfig;

/**
 * 企业微信应用消息发送服务（自建应用 send message）
 */
@Service
public class WecomMessageService
{
    private static final Logger log = LoggerFactory.getLogger(WecomMessageService.class);

    @Autowired
    private WecomAccessTokenService wecomAccessTokenService;

    @Autowired
    private WecomTencentApiClient apiClient;

    /**
     * 发送文本消息给指定企业微信内部成员（通过自建应用消息接口）。
     *
     * @param config  企业微信配置（包含 corpId / agentId）
     * @param touser  接收消息的成员 userid（即 snUserid）
     * @param content 文本消息内容
     */
    public void sendTextToUser(CrmWecomConfig config, String touser, String content)
    {
        String accessToken = wecomAccessTokenService.getAgentAccessToken(config);

        JSONObject text = new JSONObject();
        text.put("content", content);

        JSONObject body = new JSONObject();
        body.put("touser", touser);
        body.put("msgtype", "text");
        body.put("agentid", Integer.parseInt(config.getAgentId()));
        body.put("text", text);

        JSONObject res = apiClient.sendAppMessage(accessToken, body);
        int errcode = res.getIntValue("errcode");
        if (errcode != 0)
        {
            String errmsg = res.getString("errmsg");
            log.warn("WeCom send text message failed: touser={}, errcode={}, errmsg={}", touser, errcode, errmsg);
            throw new ServiceException("企业微信应用消息发送失败: errcode=" + errcode + " errmsg=" + errmsg);
        }
    }

    /**
     * 发送文本卡片消息给企业内部成员（深链建群等）。按钮文案默认「详情」。
     */
    public void sendTextcardToUser(CrmWecomConfig config, String touser, String title, String description, String url)
    {
        sendTextcardToUser(config, touser, title, description, url, "详情");
    }

    /**
     * 发送文本卡片消息给企业内部成员。
     *
     * @param btnTxt 按钮文字，空则「详情」
     */
    public void sendTextcardToUser(CrmWecomConfig config, String touser, String title, String description, String url,
            String btnTxt)
    {
        String accessToken = wecomAccessTokenService.getAgentAccessToken(config);

        JSONObject textcard = new JSONObject();
        textcard.put("title", title);
        textcard.put("description", description);
        textcard.put("url", url);
        textcard.put("btntxt", (btnTxt == null || btnTxt.trim().isEmpty()) ? "详情" : btnTxt.trim());

        JSONObject body = new JSONObject();
        body.put("touser", touser);
        body.put("msgtype", "textcard");
        body.put("agentid", Integer.parseInt(config.getAgentId()));
        body.put("textcard", textcard);

        JSONObject res = apiClient.sendAppMessage(accessToken, body);
        int errcode = res.getIntValue("errcode");
        if (errcode != 0)
        {
            String errmsg = res.getString("errmsg");
            log.warn("WeCom send textcard failed: touser={}, errcode={}, errmsg={}", touser, errcode, errmsg);
            throw new ServiceException("企业微信文本卡片发送失败: errcode=" + errcode + " errmsg=" + errmsg);
        }
    }

    /**
     * 发送文本消息给企业微信外部联系人（通过客户群发消息模版接口）。
     *
     * @param config             企业微信配置
     * @param externalUserid     外部联系人的 external_userid
     * @param senderUserid       发送该消息的内部成员 userid（须是该外部联系人的跟进人）
     * @param content            文本消息内容
     */
    public void sendTextToExternalUser(CrmWecomConfig config, String externalUserid, String senderUserid, String content)
    {
        String accessToken = wecomAccessTokenService.getAgentAccessToken(config);

        JSONObject text = new JSONObject();
        text.put("content", content);

        JSONObject body = new JSONObject();
        body.put("chat_type", "single");
        body.put("external_userid", new String[] { externalUserid });
        body.put("sender", senderUserid);
        body.put("text", text);

        JSONObject res = apiClient.externalContactAddMsgTemplate(accessToken, body);
        int errcode = res.getIntValue("errcode");
        if (errcode != 0)
        {
            String errmsg = res.getString("errmsg");
            log.warn("WeCom send external text message failed: externalUserid={}, sender={}, errcode={}, errmsg={}",
                    externalUserid, senderUserid, errcode, errmsg);
            throw new ServiceException("企业微信客户消息发送失败: errcode=" + errcode + " errmsg=" + errmsg);
        }
    }
}
