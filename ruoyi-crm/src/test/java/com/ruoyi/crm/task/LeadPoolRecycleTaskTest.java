package com.ruoyi.crm.task;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ruoyi.crm.domain.CrmLead;
import com.ruoyi.crm.domain.CrmLeadPoolConfig;
import com.ruoyi.crm.domain.dto.LeadReleaseRequest;
import com.ruoyi.crm.mapper.CrmLeadMapper;
import com.ruoyi.crm.service.ICrmLeadPoolService;
import com.ruoyi.crm.service.impl.CrmLeadPoolConfigServiceImpl;

@ExtendWith(MockitoExtension.class)
class LeadPoolRecycleTaskTest
{
    @Mock
    private CrmLeadMapper crmLeadMapper;
    @Mock
    private CrmLeadPoolConfigServiceImpl poolConfigService;
    @Mock
    private ICrmLeadPoolService leadPoolService;

    @InjectMocks
    private LeadPoolRecycleTask task;

    @Test
    void recycleCurrentTenant_skipsWhenDisabled()
    {
        CrmLeadPoolConfig config = new CrmLeadPoolConfig();
        config.setRecycleEnabled(false);
        when(poolConfigService.getConfig()).thenReturn(config);

        task.recycleCurrentTenant("t1");

        verify(crmLeadMapper, never()).selectLeadsForAutoRecycle(anyInt(), anyInt());
        verify(leadPoolService, never()).release(any(), any(), anyBoolean());
    }

    @Test
    void recycleCurrentTenant_releasesIdleLeads()
    {
        CrmLeadPoolConfig config = new CrmLeadPoolConfig();
        config.setRecycleEnabled(true);
        config.setIdleToPoolHours(72);
        config.setMaxHoldCount(100);
        when(poolConfigService.getConfig()).thenReturn(config);

        CrmLead stale = new CrmLead();
        stale.setLeadId("L-idle");
        when(crmLeadMapper.selectLeadsForAutoRecycle(72, 200)).thenReturn(Collections.singletonList(stale));
        when(crmLeadMapper.selectUserIdsOverHold(100)).thenReturn(Collections.emptyList());

        task.recycleCurrentTenant("t1");

        verify(leadPoolService).release(eq("L-idle"), any(LeadReleaseRequest.class), eq(true));
    }
}
