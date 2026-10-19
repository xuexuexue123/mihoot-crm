package com.ruoyi.crm.wecom;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.crm.domain.CrmWecomConfig;
import com.ruoyi.crm.domain.dto.wecom.WecomJsapiAgentSignatureDto;
import com.ruoyi.crm.mapper.CrmWecomConfigMapper;

/**
 * 企微 JS-SDK ticket 获取、缓存与签名（按 corpId 进程内缓存）。
 */
@Service
public class WecomJsapiTicketService
{
    private static final long TICKET_TTL_MS = 7_000_000L;

    private static final int MAX_URL_LENGTH = 1024;

    private static final String NONCE_CHARS = "abcdefghijklmnopqrstuvwxyz0123456789";

    private final WecomTencentApiClient apiClient;

    private final WecomAccessTokenService accessTokenService;

    private final CrmWecomConfigMapper wecomConfigMapper;

    private final ConcurrentHashMap<String, CachedTicket> corpTicketCache = new ConcurrentHashMap<>();

    private final ConcurrentHashMap<String, CachedTicket> agentTicketCache = new ConcurrentHashMap<>();

    private final ConcurrentHashMap<String, Object> corpRefreshLocks = new ConcurrentHashMap<>();

    private final ConcurrentHashMap<String, Object> agentRefreshLocks = new ConcurrentHashMap<>();

    private static final class CachedTicket
    {
        final String ticket;
        final long expireAtEpochMs;

        CachedTicket(String ticket, long expireAtEpochMs)
        {
            this.ticket = ticket;
            this.expireAtEpochMs = expireAtEpochMs;
        }
    }

    public WecomJsapiTicketService(
            WecomTencentApiClient apiClient,
            WecomAccessTokenService accessTokenService,
            CrmWecomConfigMapper wecomConfigMapper)
    {
        this.apiClient = apiClient;
        this.accessTokenService = accessTokenService;
        this.wecomConfigMapper = wecomConfigMapper;
    }

    public void invalidateCorpTicket(String corpId)
    {
        if (corpId != null)
        {
            corpTicketCache.remove(corpId);
        }
    }

    public void invalidateAgentTicket(String corpId)
    {
        if (corpId != null)
        {
            agentTicketCache.remove(corpId);
        }
    }

    public WecomJsapiAgentSignatureDto buildAgentSignature(String url)
    {
        validateUrl(url);
        CrmWecomConfig cfg = requireWecomConfig();
        String ticket = getAgentJsapiTicket(cfg);
        return buildSignature(cfg, ticket, url);
    }

    public WecomJsapiAgentSignatureDto buildCorpSignature(String url)
    {
        validateUrl(url);
        CrmWecomConfig cfg = requireWecomConfig();
        String ticket = getCorpJsapiTicket(cfg);
        return buildSignature(cfg, ticket, url);
    }

    static void validateUrl(String url)
    {
        if (url == null || url.trim().isEmpty())
        {
            throw new ServiceException("url 不能为空", 400);
        }
        if (url.contains("#"))
        {
            throw new ServiceException("url 不能包含 #", 400);
        }
        if (url.length() > MAX_URL_LENGTH)
        {
            throw new ServiceException("url 长度超过限制", 400);
        }
    }

    static String sha1Hex(String raw)
    {
        try
        {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] digest = md.digest(raw.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(digest.length * 2);
            for (byte b : digest)
            {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        }
        catch (Exception e)
        {
            throw new ServiceException("签名计算失败: " + e.getMessage());
        }
    }

    private WecomJsapiAgentSignatureDto buildSignature(CrmWecomConfig cfg, String ticket, String url)
    {
        String nonceStr = randomNonce(16);
        long timestamp = System.currentTimeMillis() / 1000;
        String raw = "jsapi_ticket=" + ticket
                + "&noncestr=" + nonceStr
                + "&timestamp=" + timestamp
                + "&url=" + url;
        WecomJsapiAgentSignatureDto dto = new WecomJsapiAgentSignatureDto();
        dto.setCorpId(cfg.getCorpId());
        dto.setAgentId(cfg.getAgentId());
        dto.setTimestamp(timestamp);
        dto.setNonceStr(nonceStr);
        dto.setSignature(sha1Hex(raw));
        return dto;
    }

    private String getCorpJsapiTicket(CrmWecomConfig cfg)
    {
        return getTicket(cfg, corpTicketCache, corpRefreshLocks, true);
    }

    private String getAgentJsapiTicket(CrmWecomConfig cfg)
    {
        return getTicket(cfg, agentTicketCache, agentRefreshLocks, false);
    }

    private String getTicket(
            CrmWecomConfig cfg,
            ConcurrentHashMap<String, CachedTicket> cache,
            ConcurrentHashMap<String, Object> locks,
            boolean corp)
    {
        String corpId = cfg.getCorpId();
        long now = System.currentTimeMillis();
        CachedTicket cached = cache.get(corpId);
        if (cached != null && cached.expireAtEpochMs - now > 0)
        {
            return cached.ticket;
        }

        Object lock = locks.computeIfAbsent(corpId, k -> new Object());
        synchronized (lock)
        {
            cached = cache.get(corpId);
            now = System.currentTimeMillis();
            if (cached != null && cached.expireAtEpochMs - now > 0)
            {
                return cached.ticket;
            }

            String ticket = fetchTicketWithRetry(cfg, corp);
            cache.put(corpId, new CachedTicket(ticket, now + TICKET_TTL_MS));
            return ticket;
        }
    }

    private String fetchTicketWithRetry(CrmWecomConfig cfg, boolean corp)
    {
        String accessToken = accessTokenService.getAgentAccessToken(cfg);
        JSONObject res = corp ? apiClient.getJsapiTicket(accessToken) : apiClient.getAgentConfigTicket(accessToken);
        int err = res.getIntValue("errcode");
        if (err == 0)
        {
            String ticket = res.getString("ticket");
            if (ticket == null || ticket.isEmpty())
            {
                throw new ServiceException("企业微信 ticket 响应为空", 503);
            }
            return ticket;
        }

        if (WecomTencentApiClient.isAccessTokenInvalid(err))
        {
            accessTokenService.invalidate(cfg.getCorpId());
            invalidateCorpTicket(cfg.getCorpId());
            invalidateAgentTicket(cfg.getCorpId());

            accessToken = accessTokenService.getAgentAccessToken(cfg);
            res = corp ? apiClient.getJsapiTicket(accessToken) : apiClient.getAgentConfigTicket(accessToken);
            err = res.getIntValue("errcode");
            if (err == 0)
            {
                String ticket = res.getString("ticket");
                if (ticket == null || ticket.isEmpty())
                {
                    throw new ServiceException("企业微信 ticket 响应为空", 503);
                }
                return ticket;
            }
        }

        throw new ServiceException(
                "企业微信 ticket 获取失败: errcode=" + err + " errmsg=" + res.getString("errmsg"), 503);
    }

    private CrmWecomConfig requireWecomConfig()
    {
        CrmWecomConfig cfg = wecomConfigMapper.selectFirst();
        if (cfg == null)
        {
            throw new ServiceException("wecom config not found for tenant", 503);
        }
        return cfg;
    }

    private static String randomNonce(int length)
    {
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++)
        {
            sb.append(NONCE_CHARS.charAt(random.nextInt(NONCE_CHARS.length())));
        }
        return sb.toString();
    }
}
