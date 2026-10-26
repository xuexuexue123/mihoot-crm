package com.ruoyi.crm.service.impl;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.domain.CrmWecomConfig;
import com.ruoyi.crm.domain.CrmWecomStatsMeta;
import com.ruoyi.crm.domain.dto.wecom.CrmWecomStatsBackfillResult;
import com.ruoyi.crm.domain.dto.wecom.CrmWecomStatsStatusResponse;
import com.ruoyi.crm.mapper.CrmWecomConfigMapper;
import com.ruoyi.crm.mapper.CrmWecomStatsMetaMapper;
import com.ruoyi.crm.service.ICrmWecomStatsInitService;
import com.ruoyi.crm.service.ICrmWecomStatsSnapshotService;
import com.ruoyi.crm.service.support.TenantDataSourceSupport;

/**
 * 企微统计冷启动：CAS 抢锁 + 异步 180 天回补
 */
@Service
public class CrmWecomStatsInitServiceImpl implements ICrmWecomStatsInitService
{
    private static final Logger log = LoggerFactory.getLogger(CrmWecomStatsInitServiceImpl.class);

    private static final int COLD_START_DAYS = 180;
    private static final String COLD_START_DATASET = "both";
    private static final int LOCK_MINUTES = 15;

    @Autowired
    private CrmWecomStatsMetaMapper statsMetaMapper;

    @Autowired
    private CrmWecomConfigMapper wecomConfigMapper;

    @Autowired
    private ICrmWecomStatsSnapshotService statsSnapshotService;

    @Override
    public void ensureInitialized()
    {
        CrmWecomStatsMeta meta = loadOrCreateMeta();
        if (meta == null)
        {
            return;
        }
        if (CrmWecomStatsMeta.STATUS_DONE.equals(meta.getInitStatus()))
        {
            return;
        }
        if (isInitializing(meta))
        {
            return;
        }
        if (!isWecomConnected())
        {
            if (CrmWecomStatsMeta.STATUS_NONE.equals(meta.getInitStatus())
                    || CrmWecomStatsMeta.STATUS_FAILED.equals(meta.getInitStatus()))
            {
                statsMetaMapper.markFailed("企微未连接，请先完成企微配置");
            }
            return;
        }
        if (statsMetaMapper.tryAcquireColdStartLock(LOCK_MINUTES) == 0)
        {
            return;
        }

        String tenantKey = TenantDataSourceSupport.currentTenantKey();
        if (StringUtils.isBlank(tenantKey))
        {
            statsMetaMapper.markFailed("租户数据源未绑定");
            return;
        }

        log.info("WeCom stats cold start triggered tenant={}", tenantKey);
        statsSnapshotService.backfillTenantAsync(tenantKey, COLD_START_DAYS, COLD_START_DATASET,
                result -> completeColdStart(result, COLD_START_DAYS),
                e -> failColdStart(e.getMessage()));
    }

    @Override
    public CrmWecomStatsStatusResponse getStatus()
    {
        CrmWecomStatsMeta meta = loadOrCreateMeta();
        return toStatusResponse(meta);
    }

    @Override
    public void completeColdStart(CrmWecomStatsBackfillResult result, int backfillDays)
    {
        if (result != null && !result.getErrors().isEmpty())
        {
            for (String err : result.getErrors())
            {
                if (err.contains("未找到已连接") || err.contains("access_token"))
                {
                    failColdStart(err);
                    return;
                }
            }
        }
        statsMetaMapper.markDone(backfillDays);
        log.info("WeCom stats cold start completed backfillDays={} behaviorRows={} groupchatRows={}",
                backfillDays,
                result != null ? result.getBehaviorRows() : 0,
                result != null ? result.getGroupchatRows() : 0);
    }

    @Override
    public void failColdStart(String error)
    {
        String msg = StringUtils.isNotEmpty(error) ? error : "冷启动回补失败";
        statsMetaMapper.markFailed(msg);
        log.warn("WeCom stats cold start failed: {}", msg);
    }

    @Override
    public void forceAcquireForManualBackfill()
    {
        statsMetaMapper.insertDefaultIfAbsent();
        statsMetaMapper.forceAcquireBackfillLock(LOCK_MINUTES);
    }

    private CrmWecomStatsMeta loadOrCreateMeta()
    {
        statsMetaMapper.insertDefaultIfAbsent();
        return statsMetaMapper.selectMeta();
    }

    private boolean isWecomConnected()
    {
        CrmWecomConfig cfg = wecomConfigMapper.selectFirst();
        return cfg != null && StringUtils.isNotEmpty(cfg.getCorpId()) && Boolean.TRUE.equals(cfg.getConnected());
    }

    private static boolean isInitializing(CrmWecomStatsMeta meta)
    {
        if (meta == null || !CrmWecomStatsMeta.STATUS_RUNNING.equals(meta.getInitStatus()))
        {
            return false;
        }
        Date lockUntil = meta.getLockUntil();
        return lockUntil != null && lockUntil.after(new Date());
    }

    private static CrmWecomStatsStatusResponse toStatusResponse(CrmWecomStatsMeta meta)
    {
        CrmWecomStatsStatusResponse response = new CrmWecomStatsStatusResponse();
        if (meta == null)
        {
            response.setInitialized(false);
            response.setInitializing(false);
            response.setBackfilledAt(null);
            return response;
        }
        response.setInitialized(CrmWecomStatsMeta.STATUS_DONE.equals(meta.getInitStatus()));
        response.setInitializing(isInitializing(meta));
        if (meta.getBackfilledAt() != null)
        {
            response.setBackfilledAt(meta.getBackfilledAt().getTime() / 1000L);
        }
        else
        {
            response.setBackfilledAt(null);
        }
        return response;
    }
}
