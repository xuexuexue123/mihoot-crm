package com.ruoyi.crm.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.crm.domain.WScheduledTask;
import com.ruoyi.crm.domain.dto.ScheduledTaskClaimDueResult;
import com.ruoyi.crm.mapper.WScheduledTaskMapper;
import com.ruoyi.framework.datasource.DynamicRoutingDataSource;
import com.ruoyi.tenant.domain.MasterTenant;
import com.ruoyi.tenant.domain.enums.TenantStatus;
import com.ruoyi.tenant.service.IMasterTenantService;

@ExtendWith(MockitoExtension.class)
public class ScheduledTaskClaimDueServiceTest
{
    @Mock
    private IMasterTenantService masterTenantService;

    @Mock
    private DynamicRoutingDataSource dynamicRoutingDataSource;

    @Mock
    private WScheduledTaskMapper wScheduledTaskMapper;

    @InjectMocks
    private ScheduledTaskClaimDueService claimDueService;

    @BeforeEach
    public void setUp()
    {
        ReflectionTestUtils.setField(claimDueService, "driverClassName", "org.postgresql.Driver");
    }

    @Test
    public void parseSlotAcceptsHalfHourShanghai()
    {
        ScheduledTaskClaimDueService.ParsedSlot parsed =
                ScheduledTaskClaimDueService.parseAndValidateSlot("2026-07-26T09:30+08:00");
        assertEquals("09:30", parsed.runTime);
        // 2026-07-26 is Sunday → ISO 7
        assertEquals("7", parsed.dayOfWeek);
        assertEquals("2026-07-26T09:30+08:00", parsed.normalizedSlot);
    }

    @Test
    public void parseSlotRejectsMinute15()
    {
        ServiceException ex = assertThrows(ServiceException.class,
                () -> ScheduledTaskClaimDueService.parseAndValidateSlot("2026-07-26T09:15+08:00"));
        assertEquals(400, ex.getCode());
    }

    @Test
    public void parseSlotRejectsWrongOffset()
    {
        ServiceException ex = assertThrows(ServiceException.class,
                () -> ScheduledTaskClaimDueService.parseAndValidateSlot("2026-07-26T09:30Z"));
        assertEquals(400, ex.getCode());
    }

    @Test
    public void claimDueReturnsClaimedTaskAndIsIdempotentOnSecondClaim()
    {
        MasterTenant tenant = new MasterTenant();
        tenant.setTenant("clinic_a");
        tenant.setStatus(TenantStatus.NORMAL.getCode());
        tenant.setUrl("jdbc:postgresql://localhost/a");
        tenant.setUsername("u");
        tenant.setPassword("p");

        when(masterTenantService.selectMasterTenantList(any(MasterTenant.class)))
                .thenReturn(Collections.singletonList(tenant));
        when(dynamicRoutingDataSource.existDataSource("clinic_a")).thenReturn(true);

        WScheduledTask candidate = new WScheduledTask();
        candidate.setId("task-1");
        candidate.setName("每日患者回访");
        candidate.setWorkflowId("wf-1");

        when(wScheduledTaskMapper.selectDueCandidates(eq("08:00"), eq("7"), eq("2026-07-26T08:00+08:00")))
                .thenReturn(Collections.singletonList(candidate));
        when(wScheduledTaskMapper.claimSlot("task-1", "2026-07-26T08:00+08:00")).thenReturn(1);

        ScheduledTaskClaimDueResult first = claimDueService.claimDue("2026-07-26T08:00+08:00");
        assertEquals(1, first.getTasks().size());
        assertEquals("clinic_a", first.getTasks().get(0).getTenant());
        assertEquals("task-1", first.getTasks().get(0).getTaskId());
        assertEquals("wf-1", first.getTasks().get(0).getWorkflowId());

        // 第二次：候选为空（已抢占）→ 不再返回
        when(wScheduledTaskMapper.selectDueCandidates(eq("08:00"), eq("7"), eq("2026-07-26T08:00+08:00")))
                .thenReturn(Collections.emptyList());
        ScheduledTaskClaimDueResult second = claimDueService.claimDue("2026-07-26T08:00+08:00");
        assertTrue(second.getTasks().isEmpty());
        verify(wScheduledTaskMapper, times(1)).claimSlot("task-1", "2026-07-26T08:00+08:00");
    }

    @Test
    public void claimDueSkipsWhenClaimLosesRace()
    {
        MasterTenant tenant = new MasterTenant();
        tenant.setTenant("clinic_a");
        tenant.setStatus(TenantStatus.NORMAL.getCode());

        when(masterTenantService.selectMasterTenantList(any(MasterTenant.class)))
                .thenReturn(Collections.singletonList(tenant));
        when(dynamicRoutingDataSource.existDataSource("clinic_a")).thenReturn(true);

        WScheduledTask candidate = new WScheduledTask();
        candidate.setId("task-1");
        candidate.setName("回访");
        candidate.setWorkflowId("wf-1");

        when(wScheduledTaskMapper.selectDueCandidates(any(), any(), any()))
                .thenReturn(Collections.singletonList(candidate));
        when(wScheduledTaskMapper.claimSlot(eq("task-1"), any())).thenReturn(0);

        ScheduledTaskClaimDueResult result = claimDueService.claimDue("2026-07-26T08:00+08:00");
        assertTrue(result.getTasks().isEmpty());
    }

    @Test
    public void claimDueSkipsDisabledTenant()
    {
        MasterTenant tenant = new MasterTenant();
        tenant.setTenant("disabled_t");
        tenant.setStatus(TenantStatus.DISABLE.getCode());

        when(masterTenantService.selectMasterTenantList(any(MasterTenant.class)))
                .thenReturn(Collections.singletonList(tenant));

        ScheduledTaskClaimDueResult result = claimDueService.claimDue("2026-07-26T08:00+08:00");
        assertTrue(result.getTasks().isEmpty());
        verify(wScheduledTaskMapper, never()).selectDueCandidates(any(), any(), any());
    }

    @Test
    public void mondaySlotDayOfWeekIsOne()
    {
        // 2026-07-27 is Monday
        ScheduledTaskClaimDueService.ParsedSlot parsed =
                ScheduledTaskClaimDueService.parseAndValidateSlot("2026-07-27T08:00+08:00");
        assertEquals("1", parsed.dayOfWeek);
        assertEquals("08:00", parsed.runTime);
    }
}
