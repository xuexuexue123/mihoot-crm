package com.ruoyi.crm.service.followup;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.ruoyi.crm.domain.CrmFollowupRuleEnrollment;
import com.ruoyi.crm.mapper.CrmFollowupRuleEnrollmentMapper;
import com.ruoyi.framework.datasource.DynamicRoutingDataSource;
import com.ruoyi.tenant.domain.MasterTenant;
import com.ruoyi.tenant.domain.enums.TenantStatus;
import com.ruoyi.tenant.service.IMasterTenantService;

@ExtendWith(MockitoExtension.class)
public class FollowupRuleSchedulerTest
{
    @Mock
    private IMasterTenantService masterTenantService;

    @Mock
    private DynamicRoutingDataSource dynamicRoutingDataSource;

    @Mock
    private CrmFollowupRuleEnrollmentMapper enrollmentMapper;

    @Mock
    private FollowupRuleEngine engine;

    @InjectMocks
    private FollowupRuleScheduler scheduler;

    @BeforeEach
    public void setUp()
    {
        ReflectionTestUtils.setField(scheduler, "driverClassName", "org.postgresql.Driver");
        ReflectionTestUtils.setField(scheduler, "schedulerEnabled", true);
        ReflectionTestUtils.setField(scheduler, "maxRetry", 3);
        ReflectionTestUtils.setField(scheduler, "claimLimit", 200);
    }

    @Test
    public void processesDueEnrollmentIncludingCatchUp()
    {
        MasterTenant tenant = new MasterTenant();
        tenant.setTenant("demo");
        tenant.setStatus(TenantStatus.NORMAL.getCode());
        when(masterTenantService.selectMasterTenantList(any())).thenReturn(Collections.singletonList(tenant));
        when(dynamicRoutingDataSource.existDataSource("demo")).thenReturn(true);
        CrmFollowupRuleEnrollment overdue = new CrmFollowupRuleEnrollment();
        overdue.setEnrollmentId("en-overdue");
        overdue.setNextRunAt(new Date(System.currentTimeMillis() - 86_400_000L));
        when(enrollmentMapper.selectDuePending(eq("demo"), any(Date.class), eq(3), eq(200)))
                .thenReturn(Collections.singletonList(overdue));

        scheduler.processDueEnrollments();
        verify(engine, times(1)).processDueEnrollment(overdue);
    }

    @Test
    public void skipsDisabledTenant()
    {
        MasterTenant tenant = new MasterTenant();
        tenant.setTenant("demo");
        tenant.setStatus(TenantStatus.DISABLE.getCode());
        when(masterTenantService.selectMasterTenantList(any())).thenReturn(Collections.singletonList(tenant));

        scheduler.processDueEnrollments();
        verify(enrollmentMapper, never()).selectDuePending(any(), any(), anyInt(), anyInt());
        verify(engine, never()).processDueEnrollment(any());
    }
}
