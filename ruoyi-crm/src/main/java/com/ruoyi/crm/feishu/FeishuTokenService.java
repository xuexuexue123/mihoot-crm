package com.ruoyi.crm.feishu;

import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.sign.AESUtil;
import com.ruoyi.crm.feishu.domain.CrmFeishuConfig;

/**
 * 按 appId 缓存 tenant_access_token（进程内存，不落库）。
 * TTL = 飞书 expire 减去约 5 分钟缓冲；同一 appId 刷新路径带进程内单飞。
 */
@Service
public class FeishuTokenService
{
    /** 提前刷新缓冲（毫秒），约 5 分钟 */
    private static final long REFRESH_BUFFER_MS = 300_000L;

    private final FeishuOpenApiClient apiClient;

    private static final class CachedEntry
    {
        final String token;
        final long expireAtEpochMs;

        CachedEntry(String token, long expireAtEpochMs)
        {
            this.token = token;
            this.expireAtEpochMs = expireAtEpochMs;
        }
    }

    private final ConcurrentHashMap<String, CachedEntry> cache = new ConcurrentHashMap<>();

    /** 按 appId 单飞刷新，避免并发同时打换票接口 */
    private final ConcurrentHashMap<String, Object> refreshLocks = new ConcurrentHashMap<>();

    public FeishuTokenService(FeishuOpenApiClient apiClient)
    {
        this.apiClient = apiClient;
    }

    public void invalidate(String appId)
    {
        if (appId != null)
        {
            cache.remove(appId);
        }
    }

    /**
     * 使用配置中的凭证获取有效 token（命中缓存则直接返回）。
     */
    public String getTenantAccessToken(CrmFeishuConfig cfg)
    {
        return getTenantAccessTokenResult(cfg, false).accessToken;
    }

    /**
     * 使用配置中的凭证获取有效 token 及过期时间（命中缓存则直接返回）。
     */
    public TokenResult getTenantAccessTokenWithExpiry(CrmFeishuConfig cfg)
    {
        return getTenantAccessTokenResult(cfg, false);
    }

    /**
     * 强制换票（忽略缓存），用于连通性探测。
     */
    public TokenResult forceRefresh(CrmFeishuConfig cfg)
    {
        return getTenantAccessTokenResult(cfg, true);
    }

    /**
     * 使用明文 appId/secret 换票并写入缓存（保存探针用）。
     */
    public TokenResult getTenantAccessToken(String appId, String appSecret, boolean forceRefresh)
    {
        if (appId == null || appId.isEmpty())
        {
            throw new ServiceException("未配置飞书集成：缺少 appId");
        }
        if (appSecret == null || appSecret.isEmpty())
        {
            throw new ServiceException("未配置飞书集成：缺少 secret");
        }
        long now = System.currentTimeMillis();
        if (!forceRefresh)
        {
            CachedEntry ce = cache.get(appId);
            if (ce != null && ce.expireAtEpochMs - now > 0)
            {
                return new TokenResult(ce.token, ce.expireAtEpochMs);
            }
        }
        Object lock = refreshLocks.computeIfAbsent(appId, k -> new Object());
        synchronized (lock)
        {
            if (!forceRefresh)
            {
                CachedEntry ce = cache.get(appId);
                now = System.currentTimeMillis();
                if (ce != null && ce.expireAtEpochMs - now > 0)
                {
                    return new TokenResult(ce.token, ce.expireAtEpochMs);
                }
            }
            JSONObject res = apiClient.getTenantAccessToken(appId, appSecret);
            int code = res.getIntValue("code");
            if (code != 0)
            {
                String msg = res.getString("msg");
                throw new ServiceException("飞书换票失败: code=" + code
                        + (msg != null ? " msg=" + msg : ""));
            }
            String token = res.getString("tenant_access_token");
            if (token == null || token.isEmpty())
            {
                throw new ServiceException("飞书换票失败: empty tenant_access_token");
            }
            int expireSec = res.getIntValue("expire");
            if (expireSec <= 0)
            {
                expireSec = 7200;
            }
            long expireAt = System.currentTimeMillis() + Math.max(expireSec * 1000L - REFRESH_BUFFER_MS, 60_000L);
            cache.put(appId, new CachedEntry(token, expireAt));
            return new TokenResult(token, expireAt);
        }
    }

    private TokenResult getTenantAccessTokenResult(CrmFeishuConfig cfg, boolean forceRefresh)
    {
        if (cfg == null)
        {
            throw new ServiceException("请先配置飞书集成");
        }
        String appId = cfg.getAppId();
        if (appId == null || appId.isEmpty())
        {
            throw new ServiceException("请先配置飞书集成");
        }
        if (cfg.getAppSecretEnc() == null || cfg.getAppSecretEnc().isEmpty())
        {
            throw new ServiceException("请先配置飞书集成：secret 缺失");
        }
        String plainSecret;
        try
        {
            plainSecret = AESUtil.decrypt(cfg.getAppSecretEnc());
        }
        catch (Exception e)
        {
            throw new ServiceException("Secret 解密失败");
        }
        return getTenantAccessToken(appId, plainSecret, forceRefresh);
    }

    public static final class TokenResult
    {
        public final String accessToken;
        public final long expireAtEpochMs;

        public TokenResult(String accessToken, long expireAtEpochMs)
        {
            this.accessToken = accessToken;
            this.expireAtEpochMs = expireAtEpochMs;
        }
    }
}
