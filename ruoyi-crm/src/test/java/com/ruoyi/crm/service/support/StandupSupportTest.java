package com.ruoyi.crm.service.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.crm.domain.vo.StandupDayMetrics;
import com.ruoyi.crm.domain.vo.StandupMemberRow;

public class StandupSupportTest
{
    @Test
    public void parseReportDate_rejectsIllegal()
    {
        assertThrows(ServiceException.class, () -> StandupSupport.parseReportDate(null));
        assertThrows(ServiceException.class, () -> StandupSupport.parseReportDate("2026/09/14"));
        assertEquals("2026-09-14", StandupSupport.parseReportDate("2026-09-14").toString());
    }

    @Test
    public void windowOf_halfOpenShanghai()
    {
        assertEquals("2026-09-14T00:00:00+08:00",
                StandupSupport.windowOf(java.time.LocalDate.parse("2026-09-14")).getFrom());
        assertEquals("2026-09-15T00:00:00+08:00",
                StandupSupport.windowOf(java.time.LocalDate.parse("2026-09-14")).getTo());
    }

    @Test
    public void sortMembers_zeroActionFirstThenContractThenFollowup()
    {
        StandupMemberRow zeroHigh = member("z1", "张零", true, "100", "1");
        StandupMemberRow zeroLow = member("z2", "李零", true, "10", "9");
        StandupMemberRow activeHigh = member("a1", "王活", false, "500", "1");
        StandupMemberRow activeLow = member("a2", "赵活", false, "50", "20");

        List<StandupMemberRow> list = new ArrayList<>(Arrays.asList(activeLow, zeroLow, activeHigh, zeroHigh));
        StandupSupport.sortMembers(list);

        assertEquals("z1", list.get(0).getOwnerId());
        assertEquals("z2", list.get(1).getOwnerId());
        assertEquals("a1", list.get(2).getOwnerId());
        assertEquals("a2", list.get(3).getOwnerId());
        assertTrue(StandupSupport.isZeroAction(list.get(0)));
        assertFalse(StandupSupport.isZeroAction(list.get(2)));
    }

    @Test
    public void parseKpiCodes_defaults()
    {
        assertEquals(Arrays.asList("contract_amount", "met_customers", "followup_count"),
                StandupSupport.parseKpiCodes(null));
        assertEquals(Collections.singletonList("opty_count"), StandupSupport.parseKpiCodes("opty_count"));
    }

    private static StandupMemberRow member(String id, String name, boolean zero, String contract, String followup)
    {
        StandupMemberRow row = new StandupMemberRow();
        row.setOwnerId(id);
        row.setOwnerName(name);
        StandupDayMetrics y = new StandupDayMetrics();
        y.setContractAmount(new BigDecimal(contract));
        y.setFollowupCount(new BigDecimal(followup));
        y.setMetCustomers(zero ? BigDecimal.ZERO : BigDecimal.ONE);
        if (zero)
        {
            y.setFollowupCount(BigDecimal.ZERO);
        }
        y.refreshZeroAction();
        row.setYesterday(y);
        if (zero)
        {
            row.setFlags(new ArrayList<>(Collections.singletonList(StandupSupport.FLAG_ZERO_ACTION)));
        }
        return row;
    }
}
