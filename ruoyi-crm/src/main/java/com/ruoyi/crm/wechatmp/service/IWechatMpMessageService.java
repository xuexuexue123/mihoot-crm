package com.ruoyi.crm.wechatmp.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.web.multipart.MultipartFile;

import com.ruoyi.crm.wechatmp.domain.CrmWechatMpMessage;
import com.ruoyi.crm.wechatmp.dto.WechatMpMessageReplyRequest;
import com.ruoyi.crm.wechatmp.dto.WechatMpMessageTemplateRequest;

import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;

public interface IWechatMpMessageService
{
    List<Map<String, Object>> listConversations(String appId);

    List<CrmWechatMpMessage> listMessages(String appId, String openId);

    void reply(WechatMpMessageReplyRequest req);

    /** Inbox AI 客服回复：跳过坐席 owner 校验，消息 source=ai_inbox_agent。 */
    void replyAsAi(WechatMpMessageReplyRequest req);

    Long sendTemplate(WechatMpMessageTemplateRequest req);

    void saveInbound(String appId, WxMpXmlMessage wxMessage);

    /** 同 {@link #saveInbound}，返回落库后的消息（含 id）；未写入时返回 null。 */
    CrmWechatMpMessage saveInboundReturning(String appId, WxMpXmlMessage wxMessage);

    void streamTemporaryMedia(String appId, String mediaId, HttpServletResponse response) throws IOException;

    /**
     * Upload temporary media for KF (custom) messages.
     *
     * @return map with mediaId, msgType, url
     */
    Map<String, Object> uploadTemporaryMedia(String appId, String type, MultipartFile file) throws IOException;
}
