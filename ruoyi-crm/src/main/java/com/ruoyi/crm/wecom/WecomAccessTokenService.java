package com.ruoyi.crm.wecom;

import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import com.alibaba.fastjson2.JSONObject;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.sign.AESUtil;
import com.ruoyi.crm.domain.CrmWecomConfig;

/**
 * 按 corpId 缓存 agent access_token，提前约 2 分钟刷新；同一 corpId 刷新路径带进程内单飞，避免并发重复请求 gettoken。
 */
@Service
public class WecomAccessTokenService
{
    private final WecomTencentApiClient apiClient;

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

    /** 按 corpId 单飞刷新，避免并发同时打 gettoken */
    private final ConcurrentHashMap<String, Object> refreshLocks = new ConcurrentHashMap<>();

    public WecomAccessTokenService(WecomTencentApiClient apiClient)
    {
        this.apiClient = apiClient;
    }

    public void invalidate(String corpId)
    {
        if (corpId != null)
        {
            cache.remove(corpId);
        }
    }

    public String getAgentAccessToken(CrmWecomConfig cfg)
    {
        String corpId = cfg.getCorpId();
        long now = System.currentTimeMillis();
        CachedEntry ce = cache.get(corpId);
        if (ce != null && ce.expireAtEpochMs - now > 120_000)
        {
            return ce.token;
        }
        Object lock = refreshLocks.computeIfAbsent(corpId, k -> new Object());
        synchronized (lock)
        {
            ce = cache.get(corpId);
            now = System.currentTimeMillis();
            if (ce != null && ce.expireAtEpochMs - now > 120_000)
            {
                return ce.token;
            }
            try
            {
                String secret = AESUtil.decrypt(cfg.getAgentSecretEnc());
                JSONObject res = apiClient.getAccessToken(corpId, secret);
                int err = res.getIntValue("errcode");
                if (err != 0)
                {
                    throw new ServiceException("企业微信 gettoken 失败: errcode=" + err + " errmsg=" + res.getString("errmsg"));
                }
                String token = res.getString("access_token");
                int expiresIn = res.getIntValue("expires_in");
                if (expiresIn <= 0)
                {
                    expiresIn = 7200;
                }
                cache.put(corpId, new CachedEntry(token, now + expiresIn * 1000L));
                return token;
            }
            catch (ServiceException e)
            {
                throw e;
            }
            catch (Exception e)
            {
                throw new ServiceException("解密 Secret 或获取 token 失败: " + e.getMessage());
            }
        }
    }
}
