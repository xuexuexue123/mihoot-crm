package com.ruoyi.crm.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ruoyi.crm.domain.dto.SalesKpiActualRow;
import com.ruoyi.crm.domain.dto.SalesKpiOptyCloseRow;
import com.ruoyi.crm.domain.enums.SalesKpiCode;
import com.ruoyi.crm.mapper.SalesKpiActualMapper;
import com.ruoyi.crm.service.support.OptyStageKpiSupport;

@ExtendWith(MockitoExtension.class)
public class SalesKpiActualServiceImplTest
{
    @Mock
    private SalesKpiActualMapper salesKpiActualMapper;

    @Mock
    private OptyStageKpiSupport optyStageKpiSupport;

    @InjectMocks
    private SalesKpiActualServiceImpl service;

    @Test
    public void contractAmountDelegates()
    {
        SalesKpiActualRow row = new SalesKpiActualRow();
        row.setOwnerId("1001");
        row.setActualValue(new BigDecimal("82000"));
        when(salesKpiActualMapper.sumContractAmount(anyList(), any(Date.class), any(Date.class)))
                .thenReturn(Collections.singletonList(row));

        Map<String, BigDecimal> map = service.aggregate(Collections.singletonList("1001"),
                new Date(), new Date(), SalesKpiCode.CONTRACT_AMOUNT);
        assertEquals(0, new BigDecimal("82000").compareTo(map.get("1001")));
    }

    @Test
    public void winRate_nullWhenDenominatorZero()
    {
        when(salesKpiActualMapper.selectClosedOpties(anyList(), any(Date.class), any(Date.class)))
                .thenReturn(Collections.emptyList());

        Map<String, BigDecimal> map = service.aggregate(Collections.singletonList("1001"),
                new Date(), new Date(), SalesKpiCode.WIN_RATE);
        assertTrue(map.isEmpty());
        assertNull(map.get("1001"));
    }

    @Test
    public void winRate_percent()
    {
        SalesKpiOptyCloseRow won = new SalesKpiOptyCloseRow();
        won.setOwnerId("1001");
        won.setStage("closed_won");
        SalesKpiOptyCloseRow lost = new SalesKpiOptyCloseRow();
        lost.setOwnerId("1001");
        lost.setStage("closed_lost");
        when(salesKpiActualMapper.selectClosedOpties(anyList(), any(Date.class), any(Date.class)))
                .thenReturn(Arrays.asList(won, lost));
        when(optyStageKpiSupport.isTerminalOpty(eq("closed_won"), any())).thenReturn(true);
        when(optyStageKpiSupport.isTerminalOpty(eq("closed_lost"), any())).thenReturn(true);
        when(optyStageKpiSupport.isClosedWonStage("closed_won")).thenReturn(true);
        when(optyStageKpiSupport.isClosedWonStage("closed_lost")).thenReturn(false);

        Map<String, BigDecimal> map = service.aggregate(Collections.singletonList("1001"),
                new Date(), new Date(), SalesKpiCode.WIN_RATE);
        // 1/2 * 100 = 50.00
        assertEquals(0, new BigDecimal("50.00").compareTo(map.get("1001")));
    }
}
