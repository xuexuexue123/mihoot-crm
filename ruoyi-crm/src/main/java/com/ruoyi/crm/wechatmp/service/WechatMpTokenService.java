package com.ruoyi.crm.wechatmp.service;

import java.util.concurrent.ConcurrentHashMap;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.sign.AESUtil;
import com.ruoyi.crm.wechatmp.domain.CrmWechatMpConfig;
import com.ruoyi.crm.wechatmp.mapper.CrmWechatMpConfigMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * 微信公众号 access_token 缓存管理
 * L1: ConcurrentHashMap 本地缓存，TTL 6900s (7200s - 300s 安全边界)
 * L2: 主动刷新：剩余 TTL < 5min 时获取新 token
 * 并发控制: synchronized + 单飞锁
 *
 * @author luohd
 * @date 2026-06-18
 */
@Slf4j
@Service
public class WechatMpTokenService
{
    private static final String STABLE_TOKEN_URL =
            "https://api.weixin.qq.com/cgi-bin/stable_token";

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

    private final RestTemplate restTemplate = new RestTemplate();
    private final ConcurrentHashMap<String, CachedEntry> cache = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Object> refreshLocks = new ConcurrentHashMap<>();

    @Autowired
    private CrmWechatMpConfigMapper wechatMpConfigMapper;

    /**
     * 从租户数据库读取 secret，获取当前有效的 access_token
     *
     * @param appId 公众号 AppID
     * @return access_token
     */
    public String getAccessToken(String appId)
    {
        CrmWechatMpConfig cfg = wechatMpConfigMapper.selectByAppId(appId);
        if (cfg == null || cfg.getSecretEnc() == null)
        {
            throw new ServiceException("未找到公众号配置或 secret 未设置: appId=" + appId);
        }
        String secret;
        try
        {
            secret = AESUtil.decrypt(cfg.getSecretEnc());
        }
        catch (Exception e)
        {
            throw new ServiceException("Secret 解密失败: " + e.getMessage());
        }
        return getAccessToken(appId, secret);
    }

    /**
     * 强制向微信刷新 access_token（errcode 40001 后重试时调用）。
     */
    public String refreshAccessToken(String appId)
    {
        CrmWechatMpConfig cfg = wechatMpConfigMapper.selectByAppId(appId);
        if (cfg == null || cfg.getSecretEnc() == null)
        {
            throw new ServiceException("未找到公众号配置或 secret 未设置: appId=" + appId);
        }
        String secret;
        try
        {
            secret = AESUtil.decrypt(cfg.getSecretEnc());
        }
        catch (Exception e)
        {
            throw new ServiceException("Secret 解密失败: " + e.getMessage());
        }
        invalidateCache(appId);
        return getAccessToken(appId, secret, true);
    }

    /**
     * 获取当前有效的 access_token，自动刷新过期 token
     *
     * @param appId  公众号 AppID
     * @param secret 公众号 AppSecret（明文）
     * @return access_token
     */
    public String getAccessToken(String appId, String secret)
    {
        return getAccessToken(appId, secret, false);
    }

    /**
     * 获取 access_token；forceRefresh 为 true 时向微信请求强制刷新（errcode 40001 后重试）。
     */
    public String getAccessToken(String appId, String secret, boolean forceRefresh)
    {
        long now = System.currentTimeMillis();
        if (!forceRefresh)
        {
            CachedEntry ce = cache.get(appId);
            if (ce != null && ce.expireAtEpochMs - now > 120_000)
            {
                return ce.token;
            }
        }

        Object lock = refreshLocks.computeIfAbsent(appId, k -> new Object());
        synchronized (lock)
        {
            now = System.currentTimeMillis();
            if (!forceRefresh)
            {
                CachedEntry ce = cache.get(appId);
                if (ce != null && ce.expireAtEpochMs - now > 120_000)
                {
                    return ce.token;
                }
            }

            try
            {
                JSONObject body = new JSONObject();
                body.put("grant_type", "client_credential");
                body.put("appid", appId);
                body.put("secret", secret);
                body.put("force_refresh", forceRefresh);

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                HttpEntity<String> entity = new HttpEntity<>(body.toJSONString(), headers);
                ResponseEntity<String> resp = restTemplate.postForEntity(STABLE_TOKEN_URL, entity, String.class);
                JSONObject res = JSON.parseObject(resp.getBody());

                int errcode = res.getIntValue("errcode");
                if (errcode != 0)
                {
                    log.error("WeChat MP stable_token failed: errcode={}, errmsg={}",
                            errcode, res.getString("errmsg"));
                    throw new ServiceException("微信公众号 stable_token 失败: errcode=" + errcode
                            + " errmsg=" + res.getString("errmsg"));
                }

                String token = res.getString("access_token");
                int expiresIn = res.getIntValue("expires_in");
                if (expiresIn <= 0)
                {
                    expiresIn = 7200;
                }
                cache.put(appId, new CachedEntry(token, now + expiresIn * 1000L));
                log.debug("WeChat MP access_token refreshed for appId={}, expires_in={}s, forceRefresh={}",
                        appId, expiresIn, forceRefresh);
                return token;
            }
            catch (ServiceException e)
            {
                throw e;
            }
            catch (Exception e)
            {
                throw new ServiceException("获取微信公众号 access_token 失败: " + e.getMessage());
            }
        }
    }

    /**
     * 主动失效缓存（errcode 40001 时调用）
     */
    public void invalidateCache(String appId)
    {
        if (appId != null)
        {
            cache.remove(appId);
            log.info("WeChat MP token cache invalidated for appId={}", appId);
        }
    }
}
