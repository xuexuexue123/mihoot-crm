package com.ruoyi.web.controller.crm.wechatmp;

import java.io.BufferedReader;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.alibaba.fastjson2.JSONArray;
import com.ruoyi.crm.domain.dto.InboxAgentInboundPayload;
import com.ruoyi.crm.service.IInboxAgentInboundDispatchService;
import com.ruoyi.crm.service.support.TenantDataSourceSupport;
import com.ruoyi.crm.wechatmp.WechatMpMessageNewsBuilder;
import com.ruoyi.crm.wechatmp.domain.CrmWechatMpAutoReply;
import com.ruoyi.crm.wechatmp.domain.CrmWechatMpConfig;
import com.ruoyi.crm.wechatmp.domain.CrmWechatMpConversation;
import com.ruoyi.crm.wechatmp.domain.CrmWechatMpMessage;
import com.ruoyi.crm.wechatmp.domain.MpCallbackLog;
import com.ruoyi.crm.wechatmp.mapper.CrmWechatMpConfigMapper;
import com.ruoyi.crm.wechatmp.mapper.MpCallbackLogMapper;
import com.ruoyi.crm.wechatmp.service.IWechatMpConversationService;
import com.ruoyi.crm.wechatmp.service.WechatMpAutoReplyWorker;
import com.ruoyi.crm.wechatmp.service.WechatMpEventProducer;
import com.ruoyi.framework.datasource.DynamicDataSourceContextHolder;
import com.ruoyi.framework.datasource.DynamicRoutingDataSource;
import com.ruoyi.tenant.domain.MasterTenant;
import com.ruoyi.tenant.domain.SysWechatMpTenant;
import com.ruoyi.tenant.service.IMasterTenantService;
import com.ruoyi.tenant.service.ISysWechatMpTenantService;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import me.chanjar.weixin.mp.config.impl.WxMpDefaultConfigImpl;
import me.chanjar.weixin.mp.util.crypto.WxMpCryptUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 微信公众号回调端点
 * GET  /crm/wechat_mp/callback/{appId} — URL 验证
 * POST /crm/wechat_mp/callback/{appId} — 接收事件推送
 *
 * @author luohd
 * @date 2026-06-18
 */
@Slf4j
@RestController
@RequestMapping({ "/crm/wechat_mp", "/wechat_mp" })
public class WechatMpCallbackController
{
    @Value("${spring.datasource.driverClassName}")
    private String driverClassName;

    @Autowired
    private WechatMpEventProducer eventProducer;

    @Autowired
    private WechatMpAutoReplyWorker autoReplyWorker;

    @Autowired
    private IWechatMpConversationService conversationService;

    @Autowired
    private IInboxAgentInboundDispatchService inboxAgentInboundDispatchService;

    @Autowired
    private MpCallbackLogMapper callbackLogMapper;

    @Autowired
    private CrmWechatMpConfigMapper wechatMpConfigMapper;

    @Autowired
    private ISysWechatMpTenantService sysWechatMpTenantService;

    @Autowired
    private IMasterTenantService masterTenantService;

    @Autowired
    private DynamicRoutingDataSource dynamicRoutingDataSource;

    /**
     * URL 验证（微信服务器配置回调地址时回调）
     */
    @GetMapping("/callback/{appId}")
    public String verify(
            @PathVariable String appId,
            @RequestParam String signature,
            @RequestParam String timestamp,
            @RequestParam String nonce,
            @RequestParam String echostr,
            @RequestParam(value = "msg_signature", required = false) String msgSignature)
    {
        try
        {
            bindTenantForAppId(appId);
            CrmWechatMpConfig cfg = wechatMpConfigMapper.selectByAppId(appId);
            if (cfg == null)
            {
                log.warn("WeChat MP GET verify: no config found for appId={}", appId);
                return "signature check fail";
            }
            if (Boolean.TRUE.equals(cfg.getVerifySignature()))
            {
                String sig = isEncryptedMode(cfg) && msgSignature != null ? msgSignature : signature;
                if (!checkSignature(sig, timestamp, nonce, cfg.getToken()))
                {
                    log.warn("WeChat MP GET verify: signature check failed for appId={}", appId);
                    return "signature check fail";
                }
            }
            if (isEncryptedMode(cfg) && msgSignature != null)
            {
                return buildCrypt(cfg, appId).decrypt(echostr);
            }
            return echostr;
        }
        catch (Exception e)
        {
            log.error("WeChat MP GET verify error for appId={}", appId, e);
            return "signature check fail";
        }
        finally
        {
            DynamicDataSourceContextHolder.clearDataSourceKey();
        }
    }

    /**
     * 接收事件推送
     */
    @PostMapping("/callback/{appId}")
    public String receive(@PathVariable String appId, HttpServletRequest request)
    {
        String xmlBody = null;
        try
        {
            bindTenantForAppId(appId);
            CrmWechatMpConfig cfg = wechatMpConfigMapper.selectByAppId(appId);
            if (cfg == null)
            {
                log.warn("WeChat MP POST: no config found for appId={}", appId);
                return "success";
            }

            // 读取 XML 请求体
            StringBuilder sb = new StringBuilder();
            BufferedReader reader = request.getReader();
            String line;
            while ((line = reader.readLine()) != null)
            {
                sb.append(line);
            }
            xmlBody = sb.toString();

            if (xmlBody.isEmpty())
            {
                log.warn("WeChat MP POST: empty body for appId={}", appId);
                return "success";
            }

            // 验签 + 解密
            String msgSignature = request.getParameter("msg_signature");
            String timestamp = request.getParameter("timestamp");
            String nonce = request.getParameter("nonce");
            if (Boolean.TRUE.equals(cfg.getVerifySignature()))
            {
                String sig = isEncryptedMode(cfg) && msgSignature != null ? msgSignature : request.getParameter("signature");
                if (!checkSignature(sig, timestamp, nonce, cfg.getToken()))
                {
                    log.warn("WeChat MP POST: signature check failed for appId={}", appId);
                    return "signature check fail";
                }
            }

            String plainXml = xmlBody;
            if (isEncryptedMode(cfg) && msgSignature != null)
            {
                plainXml = buildCrypt(cfg, appId).decryptXml(msgSignature, timestamp, nonce, xmlBody);
            }

            // 解析 XML
            WxMpXmlMessage wxMessage = WxMpXmlMessage.fromXml(plainXml);

            // 存储回调日志
            saveCallbackLog(wxMessage, plainXml);

            // 委托 EventProducer 处理（活动入库、身份归因等副作用）
            CrmWechatMpMessage inbound = eventProducer.produceFromWxMessage(wxMessage, plainXml, appId);

            // 同步匹配自动回复规则并返回被动回复 XML（适用于 subscribe / 关键词 / 菜单点击）
            String autoReplyXml = buildAutoReplyXml(cfg, appId, wxMessage, msgSignature);
            boolean keywordAutoReplied = autoReplyXml != null;

            // Inbox AI：文本入站、异步通知 Next worker（不阻塞 5s callback）
            scheduleInboxAgentIfNeeded(appId, wxMessage, inbound, keywordAutoReplied);

            if (autoReplyXml != null)
            {
                return autoReplyXml;
            }

            return "success";
        }
        catch (Exception e)
        {
            log.error("WeChat MP callback error for appId={}", appId, e);

            // 记录异常日志
            try
            {
                MpCallbackLog errorLog = new MpCallbackLog();
                errorLog.setRawXml(xmlBody);
                errorLog.setParsed(false);
                errorLog.setErrorMsg(e.getMessage());
                callbackLogMapper.insert(errorLog);
            }
            catch (Exception ignore)
            {
                // ignore
            }

            return "success";
        }
        finally
        {
            DynamicDataSourceContextHolder.clearDataSourceKey();
        }
    }

    // ========================
    // Inbox AI Agent
    // ========================

    private void scheduleInboxAgentIfNeeded(String appId, WxMpXmlMessage wxMessage,
            CrmWechatMpMessage inbound, boolean keywordAutoReplied)
    {
        if (wxMessage == null || inbound == null || !"text".equals(wxMessage.getMsgType()))
        {
            return;
        }
        try
        {
            String tenant = TenantDataSourceSupport.currentTenantKey();
            if (tenant == null || tenant.isEmpty())
            {
                return;
            }
            CrmWechatMpConversation conv = conversationService.findByAppOpenId(appId, wxMessage.getFromUser());
            if (conv == null || conv.getId() == null)
            {
                return;
            }
            InboxAgentInboundPayload payload = new InboxAgentInboundPayload();
            payload.setChannel(InboxAgentInboundPayload.CHANNEL_WECHAT_MP);
            payload.setConversationId(conv.getId());
            payload.setInboundMessageId(inbound.getId());
            payload.setAccountId(appId);
            payload.setPeerId(wxMessage.getFromUser());
            payload.setMsgType("text");
            payload.setText(wxMessage.getContent());
            payload.setKeywordAutoReplied(keywordAutoReplied);
            inboxAgentInboundDispatchService.scheduleDispatch(tenant, payload);
        }
        catch (Exception e)
        {
            log.warn("schedule Inbox AI dispatch failed appId={} openId={}: {}",
                    appId, wxMessage.getFromUser(), e.getMessage());
        }
    }

    // ========================
    // 被动回复
    // ========================

    /**
     * 同步匹配自动回复规则并构造被动回复 XML；加密模式下返回加密报文。
     *
     * @return 命中规则时返回被动回复 XML；未命中或异常返回 null（由调用方返回 "success"）
     */
    private String buildAutoReplyXml(CrmWechatMpConfig cfg, String appId, WxMpXmlMessage wxMessage,
            String msgSignature)
    {
        try
        {
            CrmWechatMpAutoReply rule = autoReplyWorker.matchRule(appId, wxMessage);
            if (rule == null || rule.getReplyContent() == null || rule.getReplyContent().isEmpty())
            {
                return null;
            }
            log.info("WeChat MP auto-reply matched: appId={}, openId={}, msgType={}, event={}, ruleId={}, replyType={}",
                    appId, wxMessage.getFromUser(), wxMessage.getMsgType(), wxMessage.getEvent(),
                    rule.getId(), rule.getReplyType());
            String plainReply;
            if ("news".equals(rule.getReplyType()))
            {
                JSONArray articles = WechatMpMessageNewsBuilder.parseArticles(rule.getReplyContent());
                WechatMpMessageNewsBuilder.validateArticlesFromJsonArray(articles);
                plainReply = WechatMpMessageNewsBuilder.buildPassiveReplyXml(wxMessage, articles);
            }
            else
            {
                plainReply = WxMpXmlOutMessage.TEXT()
                        .content(rule.getReplyContent())
                        .fromUser(wxMessage.getToUser())
                        .toUser(wxMessage.getFromUser())
                        .build()
                        .toXml();
            }
            if (isEncryptedMode(cfg) && msgSignature != null)
            {
                return buildCrypt(cfg, appId).encrypt(plainReply);
            }
            return plainReply;
        }
        catch (Exception e)
        {
            log.warn("WeChat MP auto-reply build failed for appId={}: {}", appId, e.getMessage());
            return null;
        }
    }

    // ========================
    // 租户绑定
    // ========================

    private void bindTenantForAppId(String appId)
    {
        SysWechatMpTenant mapping = sysWechatMpTenantService.selectByAppId(appId);
        if (mapping == null)
        {
            throw new IllegalStateException("appId not mapped to tenant: " + appId);
        }
        String tenantId = mapping.getTenant();
        if (!dynamicRoutingDataSource.existDataSource(tenantId))
        {
            MasterTenant masterTenant = masterTenantService.selectMasterTenant(tenantId);
            if (masterTenant == null)
            {
                throw new IllegalStateException("tenant not found: " + tenantId);
            }
            Map<String, Object> map = new HashMap<>();
            map.put("driverClassName", driverClassName);
            map.put("url", masterTenant.getUrl());
            map.put("username", masterTenant.getUsername());
            map.put("password", masterTenant.getPassword());
            dynamicRoutingDataSource.addDataSource(tenantId, map);
        }
        DynamicDataSourceContextHolder.setDataSourceKey(tenantId);
    }

    // ========================
    // 签名校验
    // ========================

    private boolean isEncryptedMode(CrmWechatMpConfig cfg)
    {
        return cfg.getEncodingAesKey() != null && !cfg.getEncodingAesKey().isEmpty();
    }

    private WxMpCryptUtil buildCrypt(CrmWechatMpConfig cfg, String appId)
    {
        WxMpDefaultConfigImpl config = new WxMpDefaultConfigImpl();
        config.setAppId(appId);
        config.setToken(cfg.getToken());
        config.setAesKey(cfg.getEncodingAesKey());
        return new WxMpCryptUtil(config);
    }

    /**
     * 微信验签算法: sha1(lexicographic_sort([token, timestamp, nonce]))
     */
    private boolean checkSignature(String signature, String timestamp, String nonce, String token)
    {
        if (signature == null || timestamp == null || nonce == null || token == null)
        {
            return false;
        }
        try
        {
            String[] arr = {token, timestamp, nonce};
            Arrays.sort(arr);
            String raw = arr[0] + arr[1] + arr[2];
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] digest = md.digest(raw.getBytes(StandardCharsets.UTF_8));
            String computed = bytesToHex(digest);
            return computed.equals(signature);
        }
        catch (Exception e)
        {
            log.error("SHA1 signature check error", e);
            return false;
        }
    }

    private static String bytesToHex(byte[] bytes)
    {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes)
        {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    // ========================
    // 回调日志
    // ========================

    private void saveCallbackLog(WxMpXmlMessage wxMessage, String rawXml)
    {
        try
        {
            MpCallbackLog logEntry = new MpCallbackLog();
            logEntry.setMsgType(wxMessage.getMsgType());
            logEntry.setEvent(wxMessage.getEvent());
            logEntry.setFromUser(wxMessage.getFromUser());
            logEntry.setToUser(wxMessage.getToUser());
            logEntry.setRawXml(rawXml);
            logEntry.setParsed(true);
            callbackLogMapper.insert(logEntry);
        }
        catch (Exception e)
        {
            log.warn("Failed to save callback log: {}", e.getMessage());
        }
    }
}
