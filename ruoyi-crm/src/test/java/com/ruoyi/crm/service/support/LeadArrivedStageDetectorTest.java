package com.ruoyi.crm.service.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.ruoyi.system.service.ISysDictTypeService;

@ExtendWith(MockitoExtension.class)
class LeadArrivedStageDetectorTest
{
    @Mock
    private ISysDictTypeService dictTypeService;

    @InjectMocks
    private LeadArrivedStageDetector detector;

    @BeforeEach
    void setUp()
    {
        ReflectionTestUtils.setField(detector, "extraArrivedStageValues", "custom_arrived");
        detector.refreshDictCache();
    }

    @Test
    void normalize_replacesSpacesAndHyphens()
    {
        assertEquals("arrived_at_clinic", LeadArrivedStageDetector.normalize("Arrived At Clinic"));
    }

    @Test
    void isArrivedStage_matchesChineseLabel()
    {
        assertTrue(detector.isArrivedStage("已到店"));
    }

    @Test
    void isArrivedStage_matchesDictValue()
    {
        assertTrue(detector.isArrivedStage("arrived_at_clinic"));
    }

    @Test
    void isArrivedStage_matchesExtraConfig()
    {
        assertTrue(detector.isArrivedStage("custom_arrived"));
    }

    @Test
    void stageChangedToArrived_onlyWhenTransitionToArrived()
    {
        assertTrue(detector.stageChangedToArrived("跟进中", "已到店"));
        assertFalse(detector.stageChangedToArrived("已到店", "已到店"));
        assertFalse(detector.stageChangedToArrived("已到店", "转化中"));
        assertFalse(detector.stageChangedToArrived("跟进中", "跟进中"));
    }
}
