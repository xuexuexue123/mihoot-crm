package com.ruoyi.crm.service.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.ruoyi.common.exception.ServiceException;

public class CrmTagValidationSupportTest
{
    @Test
    public void acceptsDottedDictValue()
    {
        CrmTagValidationSupport.validateDictValue("risk.price_sensitive");
        CrmTagValidationSupport.validateDictValue("opportunity.high_intent");
    }

    @Test
    public void rejectsFlatDictValue()
    {
        assertThrows(ServiceException.class, () -> CrmTagValidationSupport.validateDictValue("high_intent"));
        assertThrows(ServiceException.class, () -> CrmTagValidationSupport.validateDictValue("Risk.Price"));
        assertThrows(ServiceException.class, () -> CrmTagValidationSupport.validateDictValue(""));
    }

    @Test
    public void requiresKnownCategory()
    {
        assertEquals("成交风险", CrmTagValidationSupport.requireCategory("成交风险"));
        assertThrows(ServiceException.class, () -> CrmTagValidationSupport.requireCategory("其他"));
    }

    @Test
    public void allocatesSortWithinCategoryBase()
    {
        assertEquals(11, CrmTagValidationSupport.nextDictSort("成交机会", 0));
        assertEquals(51, CrmTagValidationSupport.nextDictSort("成交风险", 0));
        assertEquals(53, CrmTagValidationSupport.nextDictSort("成交风险", 2));
    }
}
