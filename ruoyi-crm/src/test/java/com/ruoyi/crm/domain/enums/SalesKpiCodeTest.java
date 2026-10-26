package com.ruoyi.crm.domain.enums;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.ruoyi.common.exception.ServiceException;

public class SalesKpiCodeTest
{
    @Test
    public void fromCode_acceptsAllNine()
    {
        assertEquals(SalesKpiCode.CONTRACT_AMOUNT, SalesKpiCode.fromCode("contract_amount"));
        assertEquals(SalesKpiCode.WIN_RATE, SalesKpiCode.fromCode("win_rate"));
        assertEquals(9, SalesKpiCode.values().length);
    }

    @Test
    public void fromCode_rejectsIllegal()
    {
        ServiceException ex = assertThrows(ServiceException.class, () -> SalesKpiCode.fromCode("selling"));
        assertTrue(ex.getMessage().contains("非法 kpiCode"));
    }

    @Test
    public void fromCode_rejectsBlank()
    {
        assertThrows(ServiceException.class, () -> SalesKpiCode.fromCode(null));
        assertThrows(ServiceException.class, () -> SalesKpiCode.fromCode(""));
    }
}
