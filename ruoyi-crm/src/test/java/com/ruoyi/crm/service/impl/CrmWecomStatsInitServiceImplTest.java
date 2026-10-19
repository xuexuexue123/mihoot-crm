package com.ruoyi.crm.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Calendar;
import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ruoyi.crm.domain.CrmWecomStatsMeta;
import com.ruoyi.crm.domain.dto.wecom.CrmWecomStatsStatusResponse;
import com.ruoyi.crm.mapper.CrmWecomConfigMapper;
import com.ruoyi.crm.mapper.CrmWecomStatsMetaMapper;
import com.ruoyi.crm.service.ICrmWecomStatsSnapshotService;

@ExtendWith(MockitoExtension.class)
class CrmWecomStatsInitServiceImplTest
{
    @Mock
    private CrmWecomStatsMetaMapper statsMetaMapper;

    @Mock
    private CrmWecomConfigMapper wecomConfigMapper;

    @Mock
    private ICrmWecomStatsSnapshotService statsSnapshotService;

    @InjectMocks
    private CrmWecomStatsInitServiceImpl initService;

    @BeforeEach
    void setUpMetaRow()
    {
        when(statsMetaMapper.selectMeta()).thenReturn(buildMeta(CrmWecomStatsMeta.STATUS_NONE, null));
    }

    @Test
    void getStatus_mapsDoneWithBackfilledAt()
    {
        Calendar cal = Calendar.getInstance();
        cal.set(2026, Calendar.JULY, 1, 12, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date backfilledAt = cal.getTime();

        CrmWecomStatsMeta meta = buildMeta(CrmWecomStatsMeta.STATUS_DONE, backfilledAt);
        when(statsMetaMapper.selectMeta()).thenReturn(meta);

        CrmWecomStatsStatusResponse status = initService.getStatus();

        assertTrue(status.getInitialized());
        assertFalse(status.getInitializing());
        assertEquals(backfilledAt.getTime() / 1000L, status.getBackfilledAt());
    }

    @Test
    void getStatus_mapsRunningWithValidLockAsInitializing()
    {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, 10);
        CrmWecomStatsMeta meta = buildMeta(CrmWecomStatsMeta.STATUS_RUNNING, null);
        meta.setLockUntil(cal.getTime());
        when(statsMetaMapper.selectMeta()).thenReturn(meta);

        CrmWecomStatsStatusResponse status = initService.getStatus();

        assertFalse(status.getInitialized());
        assertTrue(status.getInitializing());
    }

    @Test
    void ensureInitialized_skipsWhenAlreadyDone()
    {
        when(statsMetaMapper.selectMeta()).thenReturn(buildMeta(CrmWecomStatsMeta.STATUS_DONE, new Date()));

        initService.ensureInitialized();

        verify(statsMetaMapper, never()).tryAcquireColdStartLock(anyInt());
        verify(statsSnapshotService, never()).backfillTenantAsync(eq(null), anyInt(), eq(null), eq(null), eq(null));
    }

    @Test
    void ensureInitialized_skipsWhenAnotherInstanceHoldsLock()
    {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, 10);
        CrmWecomStatsMeta meta = buildMeta(CrmWecomStatsMeta.STATUS_RUNNING, null);
        meta.setLockUntil(cal.getTime());
        when(statsMetaMapper.selectMeta()).thenReturn(meta);

        initService.ensureInitialized();

        verify(statsMetaMapper, never()).tryAcquireColdStartLock(anyInt());
    }

    private static CrmWecomStatsMeta buildMeta(String status, Date backfilledAt)
    {
        CrmWecomStatsMeta meta = new CrmWecomStatsMeta();
        meta.setId(1);
        meta.setInitStatus(status);
        meta.setBackfilledAt(backfilledAt);
        return meta;
    }
}
