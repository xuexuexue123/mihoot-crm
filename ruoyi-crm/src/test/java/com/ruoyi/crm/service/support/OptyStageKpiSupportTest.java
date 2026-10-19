package com.ruoyi.crm.service.support;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.ruoyi.common.core.domain.entity.SysDictData;
import com.ruoyi.system.service.ISysDictTypeService;

@ExtendWith(MockitoExtension.class)
public class OptyStageKpiSupportTest
{
    @Mock
    private ISysDictTypeService dictTypeService;

    @InjectMocks
    private OptyStageKpiSupport support;

    @Test
    public void closedWonFromDict()
    {
        SysDictData d = new SysDictData();
        d.setStatus("0");
        d.setDictValue("closed_won");
        when(dictTypeService.selectDictDataByType(OptyStageKpiSupport.DICT_CLOSED_WON_STAGES))
                .thenReturn(Collections.singletonList(d));

        assertTrue(support.isClosedWonStage("closed_won"));
        assertFalse(support.isClosedWonStage("prospecting"));
        assertTrue(support.isTerminalOpty("closed_won", null));
        assertTrue(support.isTerminalOpty("closed_lost", null));
    }

    @Test
    public void closedWonAliasWhenDictEmpty()
    {
        when(dictTypeService.selectDictDataByType(OptyStageKpiSupport.DICT_CLOSED_WON_STAGES))
                .thenReturn(Collections.emptyList());
        ReflectionTestUtils.setField(support, "closedWonStagesFallback", "");

        assertTrue(support.isClosedWonStage("赢单"));
        assertTrue(support.isClosedWonStage("won"));
    }
}
