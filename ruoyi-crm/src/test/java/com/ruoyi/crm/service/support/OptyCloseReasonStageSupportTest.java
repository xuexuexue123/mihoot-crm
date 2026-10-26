package com.ruoyi.crm.service.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import com.ruoyi.crm.domain.CrmOpty;

class OptyCloseReasonStageSupportTest
{
    @Test
    void normalizeCloseReason_mapsDisplayAliasesToCodes()
    {
        assertEquals("closed_won", OptyCloseReasonStageSupport.normalizeCloseReason("Won"));
        assertEquals("closed_won", OptyCloseReasonStageSupport.normalizeCloseReason("won"));
        assertEquals("closed_lost", OptyCloseReasonStageSupport.normalizeCloseReason("Lost"));
        assertEquals("closed_lost", OptyCloseReasonStageSupport.normalizeCloseReason("Lose"));
    }

    @Test
    void resolveStageFromCloseReason_mapsWonAndLostCodes()
    {
        assertEquals(OptyCloseReasonStageSupport.STAGE_CLOSED_WON,
                OptyCloseReasonStageSupport.resolveStageFromCloseReason("closed_won"));
        assertEquals(OptyCloseReasonStageSupport.STAGE_CLOSED_WON,
                OptyCloseReasonStageSupport.resolveStageFromCloseReason("Won"));
        assertEquals(OptyCloseReasonStageSupport.STAGE_CLOSED_LOST,
                OptyCloseReasonStageSupport.resolveStageFromCloseReason("closed_lost"));
        assertEquals(OptyCloseReasonStageSupport.STAGE_CLOSED_LOST,
                OptyCloseReasonStageSupport.resolveStageFromCloseReason("suspended"));
        assertEquals(OptyCloseReasonStageSupport.STAGE_CLOSED_LOST,
                OptyCloseReasonStageSupport.resolveStageFromCloseReason("duplicate"));
        assertNull(OptyCloseReasonStageSupport.resolveStageFromCloseReason("unknown"));
    }

    @Test
    void applyStageFromCloseReason_setsStageWhenMissing()
    {
        CrmOpty opty = new CrmOpty();
        opty.setCloseReason("closed_won");
        OptyCloseReasonStageSupport.applyStageFromCloseReason(opty);
        assertEquals("closed_won", opty.getStage());
        assertEquals("closed_won", opty.getForecastCategory());
    }

    @Test
    void applyStageFromCloseReason_overridesStaleOpenStageFromFullPayload()
    {
        CrmOpty opty = new CrmOpty();
        opty.setCloseReason("Won");
        opty.setStage("New");
        OptyCloseReasonStageSupport.applyStageFromCloseReason(opty);
        assertEquals("closed_won", opty.getCloseReason());
        assertEquals("closed_won", opty.getStage());
    }
}
