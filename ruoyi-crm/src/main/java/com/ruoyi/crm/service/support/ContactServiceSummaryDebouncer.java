package com.ruoyi.crm.service.support;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.framework.datasource.DynamicDataSourceContextHolder;

/**
 * 同 contactId 60s 防抖增量入队（P1 消息管道 / P0 预留）。
 */
@Component
public class ContactServiceSummaryDebouncer
{
    private static final Logger log = LoggerFactory.getLogger(ContactServiceSummaryDebouncer.class);

    public static final long DEBOUNCE_SECONDS = 60L;

    private final ConcurrentHashMap<String, Long> pending = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, String> tenantByKey = new ConcurrentHashMap<>();

    @Autowired
    private ContactServiceSummaryWorker worker;

    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread t = new Thread(r, "contact-service-summary-debounce");
        t.setDaemon(true);
        return t;
    });

    @PreDestroy
    public void shutdown()
    {
        executor.shutdownNow();
    }

    public void enqueue(String tenantId, String contactId)
    {
        if (StringUtils.isEmpty(tenantId) || StringUtils.isEmpty(contactId))
        {
            return;
        }
        String key = tenantId + ":" + contactId;
        pending.put(key, System.currentTimeMillis());
        tenantByKey.put(key, tenantId);
        executor.schedule(this::flushPending, DEBOUNCE_SECONDS, TimeUnit.SECONDS);
    }

    private void flushPending()
    {
        long now = System.currentTimeMillis();
        for (String key : pending.keySet())
        {
            Long ts = pending.get(key);
            if (ts == null)
            {
                continue;
            }
            if (now - ts < DEBOUNCE_SECONDS * 1000L - 500L)
            {
                continue;
            }
            if (pending.remove(key, ts))
            {
                String tenantId = tenantByKey.remove(key);
                String contactId = key.substring(key.indexOf(':') + 1);
                String previous = null;
                try
                {
                    previous = DynamicDataSourceContextHolder.getDataSourceKey();
                    if (StringUtils.isNotEmpty(tenantId))
                    {
                        DynamicDataSourceContextHolder.setDataSourceKey(tenantId);
                    }
                    worker.run(tenantId, contactId, "incremental", false);
                }
                catch (Exception e)
                {
                    log.warn("Debounced summary failed key={}: {}", key, e.getMessage());
                }
                finally
                {
                    if (StringUtils.isNotEmpty(previous))
                    {
                        DynamicDataSourceContextHolder.setDataSourceKey(previous);
                    }
                    else
                    {
                        DynamicDataSourceContextHolder.clearDataSourceKey();
                    }
                }
            }
        }
    }
}
