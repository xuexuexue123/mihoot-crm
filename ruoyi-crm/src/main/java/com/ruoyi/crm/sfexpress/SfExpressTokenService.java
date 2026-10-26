package com.ruoyi.crm.sfexpress;

import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.sign.AESUtil;
import com.ruoyi.crm.domain.CrmSfExpressConfig;
import com.ruoyi.crm.domain.CrmSfExpressToken;
import com.ruoyi.crm.mapper.CrmSfExpressConfigMapper;
import com.ruoyi.crm.mapper.CrmSfExpressTokenMapper;
import com.ruoyi.crm.sfexpress.SfExpressOAuthClient.OAuthTokenResult;

/**
 * 顺丰 OAuth2 accessToken 获取、缓存与刷新。
 */
@Service
public class SfExpressTokenService
{
    private static final Logger log = LoggerFactory.getLogger(SfExpressTokenService.class);

    private static final String REDIS_KEY_PREFIX = "sf:token:";

    private final ConcurrentHashMap<String, Object> refreshLocks = new ConcurrentHashMap<>();

    @Value("${sf.express.token.refresh-buffer-sec:300}")
    private int refreshBufferSec;

    @Autowired
    private CrmSfExpressConfigMapper crmSfExpressConfigMapper;

    @Autowired
    private CrmSfExpressTokenMapper crmSfExpressTokenMapper;

    @Autowired
    private SfExpressOAuthClient sfExpressOAuthClient;

    @Autowired
    private RedisCache redisCache;

    /**
     * 取有效 accessToken；临近过期时自动刷新。
     */
    public String getValidToken(String tenantId)
    {
        RefreshOutcome outcome = refreshIfNeeded(tenantId, false);
        if (!outcome.isTokenValid())
        {
            String msg = outcome.getLastError() != null ? outcome.getLastError() : "顺丰 accessToken 无效";
            throw new ServiceException(msg, 500);
        }
        CachedToken cached = getCachedToken(tenantId);
        if (cached != null && StringUtils.isNotBlank(cached.accessToken))
        {
            return cached.accessToken;
        }
        return loadPlainTokenFromDb();
    }

    /**
     * 契约 {@code POST /crm/sf-express/token/refresh} 核心逻辑。
     */
    public Map<String, Object> refreshToken(String tenantId, boolean force)
    {
        RefreshOutcome outcome = refreshIfNeeded(tenantId, force);
        return outcome.toResponseMap();
    }

    public void invalidate(String tenantId)
    {
        if (StringUtils.isNotBlank(tenantId))
        {
            redisCache.deleteObject(redisCacheKey(tenantId));
        }
    }

    public TokenStatus getTokenStatus(String tenantId)
    {
        CachedToken cached = getCachedToken(tenantId);
        if (cached != null)
        {
            return TokenStatus.fromCached(cached);
        }
        CrmSfExpressToken row = crmSfExpressTokenMapper.selectFirst();
        if (row == null || StringUtils.isBlank(row.getAccessTokenEnc()))
        {
            return TokenStatus.empty();
        }
        Date now = DateUtils.getNowDate();
        boolean valid = row.getExpiresAt() != null && row.getExpiresAt().after(now);
        return new TokenStatus(valid, row.getExpiresAt(), row.getLastRefreshAt(), row.getLastError());
    }

    private RefreshOutcome refreshIfNeeded(String tenantId, boolean force)
    {
        if (StringUtils.isBlank(tenantId))
        {
            throw new ServiceException("tenant 不能为空");
        }

        Object lock = refreshLocks.computeIfAbsent(tenantId, k -> new Object());
        synchronized (lock)
        {
            CachedToken cached = getCachedToken(tenantId);
            if (!force && cached != null && !needsRefresh(cached.expiresAt))
            {
                return RefreshOutcome.skipped(cached);
            }

            CrmSfExpressToken dbRow = crmSfExpressTokenMapper.selectFirst();
            if (!force && dbRow != null && StringUtils.isNotBlank(dbRow.getAccessTokenEnc())
                    && dbRow.getExpiresAt() != null && !needsRefresh(dbRow.getExpiresAt()))
            {
                try
                {
                    String plain = AESUtil.decrypt(dbRow.getAccessTokenEnc());
                    putCache(tenantId, plain, dbRow.getExpiresAt());
                    return RefreshOutcome.skipped(new CachedToken(plain, dbRow.getExpiresAt(),
                            dbRow.getLastRefreshAt(), dbRow.getLastError()));
                }
                catch (Exception e)
                {
                    log.warn("SF token decrypt failed for tenant {}: {}", tenantId, e.getMessage());
                }
            }

            return doRefresh(tenantId);
        }
    }

    private RefreshOutcome doRefresh(String tenantId)
    {
        CrmSfExpressConfig config = crmSfExpressConfigMapper.selectFirst();
        if (config == null || StringUtils.isBlank(config.getCustomerCode())
                || StringUtils.isBlank(config.getCheckwordEnc()))
        {
            return RefreshOutcome.failed("请先配置顺丰集成");
        }

        String checkwordPlain;
        try
        {
            checkwordPlain = AESUtil.decrypt(config.getCheckwordEnc());
        }
        catch (Exception e)
        {
            return RefreshOutcome.failed("checkword 解密失败");
        }

        Date now = DateUtils.getNowDate();
        try
        {
            OAuthTokenResult oauth = sfExpressOAuthClient.fetchAccessToken(config.getCustomerCode(), checkwordPlain,
                    config.getSandbox() == null || Boolean.TRUE.equals(config.getSandbox()));

            Date expiresAt = addSeconds(now, oauth.getExpiresInSec());
            String enc = AESUtil.encrypt(oauth.getAccessToken());

            CrmSfExpressToken existing = crmSfExpressTokenMapper.selectFirst();
            CrmSfExpressToken row = existing != null ? existing : new CrmSfExpressToken();
            row.setAccessTokenEnc(enc);
            row.setExpiresAt(expiresAt);
            row.setLastRefreshAt(now);
            row.setLastError(null);
            if (existing == null)
            {
                crmSfExpressTokenMapper.insert(row);
            }
            else
            {
                row.setId(existing.getId());
                crmSfExpressTokenMapper.update(row);
            }

            putCache(tenantId, oauth.getAccessToken(), expiresAt);
            syncConfigConnected(true, null);

            return RefreshOutcome.refreshed(oauth.getAccessToken(), expiresAt, now, null);
        }
        catch (ServiceException e)
        {
            String err = truncate(e.getMessage(), 500);
            persistTokenError(existingTokenRow(), now, err);
            syncConfigConnected(false, err);
            return RefreshOutcome.failed(err);
        }
        catch (Exception e)
        {
            String err = truncate(e.getMessage() != null ? e.getMessage() : "OAuth 换票失败", 500);
            persistTokenError(existingTokenRow(), now, err);
            syncConfigConnected(false, err);
            return RefreshOutcome.failed(err);
        }
    }

    private CrmSfExpressToken existingTokenRow()
    {
        return crmSfExpressTokenMapper.selectFirst();
    }

    private void persistTokenError(CrmSfExpressToken existing, Date now, String err)
    {
        if (existing == null)
        {
            return;
        }
        existing.setLastError(err);
        crmSfExpressTokenMapper.update(existing);
    }

    private void syncConfigConnected(boolean connected, String lastError)
    {
        CrmSfExpressConfig config = crmSfExpressConfigMapper.selectFirst();
        if (config == null)
        {
            return;
        }
        config.setConnected(connected);
        config.setLastError(lastError);
        config.setUpdateTime(DateUtils.getNowDate());
        crmSfExpressConfigMapper.update(config);
    }

    private String loadPlainTokenFromDb()
    {
        CrmSfExpressToken row = crmSfExpressTokenMapper.selectFirst();
        if (row == null || StringUtils.isBlank(row.getAccessTokenEnc()))
        {
            throw new ServiceException("请先刷新顺丰 accessToken", 500);
        }
        try
        {
            return AESUtil.decrypt(row.getAccessTokenEnc());
        }
        catch (Exception e)
        {
            throw new ServiceException("accessToken 解密失败", 500);
        }
    }

    private boolean needsRefresh(Date expiresAt)
    {
        if (expiresAt == null)
        {
            return true;
        }
        long bufferMs = refreshBufferSec * 1000L;
        return expiresAt.getTime() - System.currentTimeMillis() < bufferMs;
    }

    private CachedToken getCachedToken(String tenantId)
    {
        Object raw = redisCache.getCacheObject(redisCacheKey(tenantId));
        if (raw == null)
        {
            return null;
        }
        if (raw instanceof Map)
        {
            Map<?, ?> map = (Map<?, ?>) raw;
            Object token = map.get("accessToken");
            Object expires = map.get("expiresAt");
            Object refreshed = map.get("lastRefreshAt");
            Object err = map.get("lastError");
            if (token == null || expires == null)
            {
                return null;
            }
            Date expiresAt = expires instanceof Date ? (Date) expires : new Date(((Number) expires).longValue());
            Date lastRefreshAt = refreshed instanceof Date ? (Date) refreshed
                    : (refreshed != null ? new Date(((Number) refreshed).longValue()) : null);
            return new CachedToken(String.valueOf(token), expiresAt, lastRefreshAt,
                    err != null ? String.valueOf(err) : null);
        }
        if (raw instanceof String)
        {
            try
            {
                com.alibaba.fastjson2.JSONObject json = com.alibaba.fastjson2.JSON.parseObject((String) raw);
                return new CachedToken(json.getString("accessToken"), json.getDate("expiresAt"),
                        json.getDate("lastRefreshAt"), json.getString("lastError"));
            }
            catch (Exception ignored)
            {
                return null;
            }
        }
        return null;
    }

    private void putCache(String tenantId, String accessToken, Date expiresAt)
    {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("accessToken", accessToken);
        map.put("expiresAt", expiresAt.getTime());
        map.put("lastRefreshAt", System.currentTimeMillis());
        long ttlSec = Math.max(60L, (expiresAt.getTime() - System.currentTimeMillis()) / 1000L);
        redisCache.setCacheObject(redisCacheKey(tenantId), map, (int) ttlSec, TimeUnit.SECONDS);
    }

    private static String redisCacheKey(String tenantId)
    {
        return REDIS_KEY_PREFIX + tenantId;
    }

    private static Date addSeconds(Date base, int seconds)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(base);
        cal.add(Calendar.SECOND, seconds);
        return cal.getTime();
    }

    private static String truncate(String s, int max)
    {
        if (s == null)
        {
            return null;
        }
        return s.length() <= max ? s : s.substring(0, max);
    }

    public static String toIso8601(Date date)
    {
        if (date == null)
        {
            return null;
        }
        return Instant.ofEpochMilli(date.getTime()).toString();
    }

    static final class CachedToken
    {
        final String accessToken;
        final Date expiresAt;
        final Date lastRefreshAt;
        final String lastError;

        CachedToken(String accessToken, Date expiresAt, Date lastRefreshAt, String lastError)
        {
            this.accessToken = accessToken;
            this.expiresAt = expiresAt;
            this.lastRefreshAt = lastRefreshAt;
            this.lastError = lastError;
        }

        boolean isValid()
        {
            return StringUtils.isNotBlank(accessToken) && expiresAt != null && expiresAt.after(DateUtils.getNowDate());
        }
    }

    public static final class TokenStatus
    {
        private final boolean tokenValid;
        private final Date tokenExpiresAt;
        private final Date lastTokenRefreshAt;
        private final String lastError;

        TokenStatus(boolean tokenValid, Date tokenExpiresAt, Date lastTokenRefreshAt, String lastError)
        {
            this.tokenValid = tokenValid;
            this.tokenExpiresAt = tokenExpiresAt;
            this.lastTokenRefreshAt = lastTokenRefreshAt;
            this.lastError = lastError;
        }

        public static TokenStatus empty()
        {
            return new TokenStatus(false, null, null, null);
        }

        static TokenStatus fromCached(CachedToken cached)
        {
            return new TokenStatus(cached.isValid(), cached.expiresAt, cached.lastRefreshAt, cached.lastError);
        }

        public boolean isTokenValid()
        {
            return tokenValid;
        }

        public Date getTokenExpiresAt()
        {
            return tokenExpiresAt;
        }

        public Date getLastTokenRefreshAt()
        {
            return lastTokenRefreshAt;
        }

        public String getLastError()
        {
            return lastError;
        }
    }

    static final class RefreshOutcome
    {
        private final boolean refreshed;
        private final boolean tokenValid;
        private final Date tokenExpiresAt;
        private final Date lastTokenRefreshAt;
        private final String lastError;

        private RefreshOutcome(boolean refreshed, boolean tokenValid, Date tokenExpiresAt, Date lastTokenRefreshAt,
                String lastError)
        {
            this.refreshed = refreshed;
            this.tokenValid = tokenValid;
            this.tokenExpiresAt = tokenExpiresAt;
            this.lastTokenRefreshAt = lastTokenRefreshAt;
            this.lastError = lastError;
        }

        static RefreshOutcome skipped(CachedToken cached)
        {
            return new RefreshOutcome(false, cached.isValid(), cached.expiresAt, cached.lastRefreshAt, cached.lastError);
        }

        static RefreshOutcome refreshed(String accessToken, Date expiresAt, Date lastRefreshAt, String lastError)
        {
            boolean valid = StringUtils.isNotBlank(accessToken) && expiresAt != null
                    && expiresAt.after(DateUtils.getNowDate());
            return new RefreshOutcome(true, valid, expiresAt, lastRefreshAt, lastError);
        }

        static RefreshOutcome failed(String lastError)
        {
            return new RefreshOutcome(true, false, null, null, lastError);
        }

        boolean isTokenValid()
        {
            return tokenValid;
        }

        String getLastError()
        {
            return lastError;
        }

        Map<String, Object> toResponseMap()
        {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("refreshed", refreshed);
            m.put("tokenValid", tokenValid);
            m.put("tokenExpiresAt", toIso8601(tokenExpiresAt));
            m.put("lastTokenRefreshAt", toIso8601(lastTokenRefreshAt));
            m.put("lastError", lastError);
            return m;
        }
    }
}
