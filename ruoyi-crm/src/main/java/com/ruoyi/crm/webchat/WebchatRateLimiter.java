package com.ruoyi.crm.webchat;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;

/**
 * In-memory sliding-window rate limiter for public webchat endpoints.
 */
@Component
public class WebchatRateLimiter
{
    private static final long WINDOW_MS = 60_000L;

    @Value("${webchat.rate-limit.config-per-ip-per-min:60}")
    private int configPerIpPerMin;

    @Value("${webchat.rate-limit.session-per-ip-per-min:20}")
    private int sessionPerIpPerMin;

    @Value("${webchat.rate-limit.message-per-visitor-per-min:30}")
    private int messagePerVisitorPerMin;

    @Value("${webchat.rate-limit.identify-per-ip-per-min:10}")
    private int identifyPerIpPerMin;

    private final ConcurrentHashMap<String, Deque<Long>> windows = new ConcurrentHashMap<>();

    public void checkConfig(String ip)
    {
        check("cfg:" + safe(ip), configPerIpPerMin);
    }

    public void checkSession(String ip)
    {
        check("sess:" + safe(ip), sessionPerIpPerMin);
    }

    public void checkMessage(String visitorId)
    {
        check("msg:" + safe(visitorId), messagePerVisitorPerMin);
    }

    public void checkIdentify(String ip)
    {
        check("id:" + safe(ip), identifyPerIpPerMin);
    }

    private void check(String key, int limit)
    {
        if (limit <= 0)
        {
            return;
        }
        long now = System.currentTimeMillis();
        Deque<Long> q = windows.computeIfAbsent(key, k -> new ArrayDeque<>());
        synchronized (q)
        {
            while (!q.isEmpty() && now - q.peekFirst() > WINDOW_MS)
            {
                q.removeFirst();
            }
            if (q.size() >= limit)
            {
                throw new ServiceException("Too many requests", HttpStatus.TOO_MANY_REQUESTS);
            }
            q.addLast(now);
        }
        // occasional prune of empty keys to avoid unbounded growth
        if (windows.size() > 50_000)
        {
            pruneStale(now);
        }
    }

    private void pruneStale(long now)
    {
        Iterator<ConcurrentHashMap.Entry<String, Deque<Long>>> it = windows.entrySet().iterator();
        while (it.hasNext())
        {
            ConcurrentHashMap.Entry<String, Deque<Long>> e = it.next();
            Deque<Long> q = e.getValue();
            synchronized (q)
            {
                while (!q.isEmpty() && now - q.peekFirst() > WINDOW_MS)
                {
                    q.removeFirst();
                }
                if (q.isEmpty())
                {
                    it.remove();
                }
            }
        }
    }

    private static String safe(String v)
    {
        return StringUtils.hasText(v) ? v.trim() : "unknown";
    }
}
