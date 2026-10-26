package com.ruoyi.web.controller.crm;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ruoyi.crm.domain.CrmWecomConfig;
import com.ruoyi.crm.event.ActivityEventProducer;
import com.ruoyi.crm.mapper.CrmWecomConfigMapper;
import com.ruoyi.crm.service.ICrmWecomExternalContactService;
import com.ruoyi.crm.service.IWecomCallbackEventService;
import com.ruoyi.crm.wecom.WecomKfMessageService;
import com.ruoyi.crm.wecom.WecomWelcomeService;
import com.ruoyi.framework.datasource.DynamicDataSourceContextHolder;
import com.ruoyi.framework.datasource.DynamicRoutingDataSource;
import com.ruoyi.tenant.domain.MasterTenant;
import com.ruoyi.tenant.domain.SysWecomCorpTenant;
import com.ruoyi.tenant.service.IMasterTenantService;
import com.ruoyi.tenant.service.ISysWecomCorpTenantService;

import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxRuntimeException;
import me.chanjar.weixin.cp.config.impl.WxCpDefaultConfigImpl;
import me.chanjar.weixin.cp.util.crypto.WxCpCryptUtil;

/**
 * 企业微信回调（无登录、无 tenant 头）。主路径 {@code /crm/wecom/callback/{corpId}}；运维别名 {@code /wecom/callback/{corpId}}（须与企微后台 URL 完全一致）。
 */
@Slf4j
@RestController
@RequestMapping({ "/crm/wecom", "/wecom" })
public class CrmWecomCallbackController
{
    /** 企业微信要求响应体为明文：{@code Content-Type: text/plain;charset=UTF-8}，禁止 HTML/JSON。 */
    private static final MediaType WE_COM_TEXT_PLAIN_UTF8 =
            new MediaType(MediaType.TEXT_PLAIN, StandardCharsets.UTF_8);

    @Autowired
    private ISysWecomCorpTenantService sysWecomCorpTenantService;

    @Autowired
    private IMasterTenantService masterTenantService;

    @Autowired
    private DynamicRoutingDataSource dynamicRoutingDataSource;

    @Autowired
    private CrmWecomConfigMapper crmWecomConfigMapper;

    @Autowired
    private ICrmWecomExternalContactService crmWecomExternalContactService;

    @Autowired
    private WecomWelcomeService wecomWelcomeService;

    @Autowired
    private WecomKfMessageService wecomKfMessageService;

    @Autowired
    private IWecomCallbackEventService wecomCallbackEventService;

    @Autowired
    private ActivityEventProducer activityEventProducer;

    @Autowired
    private com.ruoyi.crm.service.wecom.contactgroup.WecomContactGroupCallbackHandler wecomContactGroupCallbackHandler;

    @Value("${spring.datasource.driverClassName}")
    private String driverClassName;

    @GetMapping(value = "/callback/{corpId}", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> verifyUrl(
            @PathVariable("corpId") String corpId,
            @RequestParam(value = "msg_signature", required = false) String msgSignature,
            @RequestParam(value = "timestamp", required = false) String timestamp,
            @RequestParam(value = "nonce", required = false) String nonce,
            @RequestParam(value = "echostr", required = false) String echostr)
    {
        if (isBlank(msgSignature) || isBlank(timestamp) || isBlank(nonce) || isBlank(echostr))
        {
            return wecomPlain(HttpStatus.BAD_REQUEST,
                    "Bad Request: URL verification requires query parameters msg_signature, timestamp, nonce, and echostr (see WeCom documentation).");
        }
        // 官方要求对接收到的参数做 UrlDecode；URLDecoder 会把「+」当成空格，Base64 须先把 + 写成 %2B 再解码
        msgSignature = normalizeWecomCallbackQueryParam(msgSignature);
        timestamp = normalizeWecomCallbackQueryParam(timestamp);
        nonce = normalizeWecomCallbackQueryParam(nonce);
        echostr = normalizeWecomCallbackQueryParam(echostr);
        // Servlet 仍可能把 query 中的「+」解码为空格，导致 Base64 验签/解密失败
        msgSignature = msgSignature.replace(' ', '+');
        echostr = echostr.replace(' ', '+');
        try
        {
            bindTenantForCorp(corpId);
            CrmWecomConfig cfg = crmWecomConfigMapper.selectByCorpId(corpId);
            if (cfg == null || cfg.getCallbackToken() == null || cfg.getEncodingAesKey() == null)
            {
                log.warn("WeCom callback verify: no crm_wecom_config for corpId={} or missing token/AES key", corpId);
                return wecomPlain(HttpStatus.BAD_REQUEST,
                        "Bad Request: tenant has no WeCom row for this corpId, or missing callback Token / EncodingAESKey. Ensure crm_wecom_config.corp_id matches the URL path and matches WeCom admin.");
            }
            WxCpCryptUtil crypt = buildCrypt(cfg, corpId);
            String plain = crypt.decryptContent(msgSignature, timestamp, nonce, echostr);
            String body = sanitizeWecomUrlVerifyPlain(plain);
            if (body.isEmpty())
            {
                log.warn("WeCom callback verify: decrypted echostr msg empty after sanitize for corpId={}", corpId);
                return wecomPlain(HttpStatus.BAD_REQUEST,
                        "Bad Request: decrypted URL verify payload was empty after normalization.");
            }
            return wecomPlain(HttpStatus.OK, body);
        }
        catch (IllegalStateException e)
        {
            log.warn("WeCom verify tenant routing: {}", e.getMessage());
            return wecomPlain(HttpStatus.BAD_REQUEST,
                    "Bad Request: corpId is not mapped to a tenant in master.sys_wecom_corp_tenant, or tenant datasource is missing.");
        }
        catch (WxRuntimeException e)
        {
            log.warn("WeCom verify signature/decrypt failed: {}", e.getMessage());
            return wecomPlain(HttpStatus.BAD_REQUEST,
                    "Bad Request: msg_signature verification or echostr decryption failed. Check callback Token and EncodingAESKey in CRM and in WeCom admin.");
        }
        catch (Exception e)
        {
            log.error("WeCom verify error", e);
            return wecomPlain(HttpStatus.BAD_REQUEST,
                    "Bad Request: " + (e.getMessage() != null ? e.getMessage() : "unexpected error during URL verification."));
        }
        finally
        {
            DynamicDataSourceContextHolder.clearDataSourceKey();
        }
    }

    @PostMapping(value = "/callback/{corpId}", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> receiveEvent(
            @PathVariable("corpId") String corpId,
            @RequestParam(value = "msg_signature", required = false) String msgSignature,
            @RequestParam(value = "timestamp", required = false) String timestamp,
            @RequestParam(value = "nonce", required = false) String nonce,
            @RequestBody(required = false) String body)
    {
        if (isBlank(msgSignature) || isBlank(timestamp) || isBlank(nonce) || body == null || body.isEmpty())
        {
            return wecomPlain(HttpStatus.BAD_REQUEST,
                    "Bad Request: event callback requires msg_signature, timestamp, nonce query parameters and a request body.");
        }
        msgSignature = normalizeWecomCallbackQueryParam(msgSignature);
        timestamp = normalizeWecomCallbackQueryParam(timestamp);
        nonce = normalizeWecomCallbackQueryParam(nonce);
        msgSignature = msgSignature.replace(' ', '+');
        try
        {
            bindTenantForCorp(corpId);
            CrmWecomConfig cfg = crmWecomConfigMapper.selectByCorpId(corpId);
            if (cfg == null || cfg.getCallbackToken() == null || cfg.getEncodingAesKey() == null)
            {
                log.warn("WeCom POST: no config row for corpId={} or missing token/AES key", corpId);
                return wecomPlain(HttpStatus.OK, "success");
            }
            WxCpCryptUtil crypt = buildCrypt(cfg, corpId);
            String plainXml = crypt.decryptXml(msgSignature, timestamp, nonce, body);
            try
            {
                wecomCallbackEventService.logEvent(plainXml);
            }
            catch (Exception e)
            {
                log.warn("WeCom callback event logging failed: {}", e.getMessage());
            }

            // Activity Hub: 生产 Activity 事件（不阻塞回调流程）
            try
            {
                activityEventProducer.produceFromWecomCallbackXml(plainXml);
            }
            catch (Exception e)
            {
                log.warn("Activity Event production from WeCom callback failed: {}", e.getMessage());
            }

            wecomWelcomeService.handleFromCallbackXml(plainXml);
            try
            {
                crmWecomExternalContactService.handlePlainXml(plainXml);
            }
            catch (Exception e)
            {
                log.warn("WeCom external contact upsert from callback failed: {}", e.getMessage());
            }
            try
            {
                wecomContactGroupCallbackHandler.handleFromCallbackXml(plainXml);
            }
            catch (Exception e)
            {
                log.warn("WeCom contact group callback handler failed: {}", e.getMessage());
            }
            wecomKfMessageService.handleFromCallbackXml(plainXml);
            return wecomPlain(HttpStatus.OK, "success");
        }
        catch (IllegalStateException e)
        {
            log.warn("WeCom POST tenant routing: {}", e.getMessage());
            return wecomPlain(HttpStatus.OK, "success");
        }
        catch (WxRuntimeException e)
        {
            log.warn("WeCom decrypt/signature: {}", e.getMessage());
            return wecomPlain(HttpStatus.OK, "success");
        }
        catch (Exception e)
        {
            log.error("WeCom callback error", e);
            return wecomPlain(HttpStatus.OK, "success");
        }
        finally
        {
            DynamicDataSourceContextHolder.clearDataSourceKey();
        }
    }

    private static boolean isBlank(String s)
    {
        return s == null || s.isEmpty();
    }

    private static ResponseEntity<String> wecomPlain(HttpStatus status, String body)
    {
        return ResponseEntity.status(status)
                .header(HttpHeaders.CACHE_CONTROL, "no-store")
                .contentType(WE_COM_TEXT_PLAIN_UTF8)
                .body(body != null ? body : "");
    }

    /**
     * 企业微信《接收消息与事件》URL 校验：须对接收到的 query 做 UTF-8 UrlDecode。
     * {@link URLDecoder} 按 x-www-form-urlencoded 规则把「+」解析为空格，会破坏 echostr/msg_signature 中的 Base64，
     * 故解码前将「+」写成「%2B」再解码。
     */
    private static String normalizeWecomCallbackQueryParam(String raw)
    {
        if (raw == null)
        {
            return null;
        }
        String s = raw.trim();
        try
        {
            return URLDecoder.decode(s.replace("+", "%2B"), StandardCharsets.UTF_8.name());
        }
        catch (UnsupportedEncodingException e)
        {
            throw new AssertionError("UTF-8", e);
        }
        catch (IllegalArgumentException e)
        {
            return s;
        }
    }

    /**
     * 官方《回调配置》GET 校验：响应体须为解密后的明文 msg，且不得带 BOM、换行（见
     * https://developer.work.weixin.qq.com/document/path/91116 §3.1）。
     */
    private static String sanitizeWecomUrlVerifyPlain(String plain)
    {
        if (plain == null)
        {
            return "";
        }
        String s = plain;
        if (!s.isEmpty() && s.charAt(0) == '\uFEFF')
        {
            s = s.substring(1);
        }
        s = s.trim();
        s = s.replace("\r", "").replace("\n", "");
        return s;
    }

    private void bindTenantForCorp(String corpId)
    {
        SysWecomCorpTenant mapping = sysWecomCorpTenantService.selectByCorpId(corpId);
        if (mapping == null)
        {
            throw new IllegalStateException("corp not mapped to tenant");
        }
        String tenantId = mapping.getTenant();
        if (!dynamicRoutingDataSource.existDataSource(tenantId))
        {
            MasterTenant masterTenant = masterTenantService.selectTenantByTenantId(tenantId);
            if (masterTenant == null)
            {
                throw new IllegalStateException("tenant not found");
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

    private static WxCpCryptUtil buildCrypt(CrmWecomConfig cfg, String corpId)
    {
        WxCpDefaultConfigImpl wx = new WxCpDefaultConfigImpl();
        wx.setCorpId(corpId);
        wx.setToken(cfg.getCallbackToken());
        wx.setAesKey(cfg.getEncodingAesKey());
        return new WxCpCryptUtil(wx);
    }
}
