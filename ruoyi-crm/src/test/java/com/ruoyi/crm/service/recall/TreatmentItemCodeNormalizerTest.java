package com.ruoyi.crm.service.recall;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class TreatmentItemCodeNormalizerTest
{
    @Test
    public void normalizesChineseAndEnglishAliases()
    {
        assertEquals("implant", TreatmentItemCodeNormalizer.normalize("种植牙"));
        assertEquals("implant", TreatmentItemCodeNormalizer.normalize("Implant"));
        assertEquals("preventive", TreatmentItemCodeNormalizer.normalize("洁牙"));
        assertEquals("restorative", TreatmentItemCodeNormalizer.normalize("补牙"));
        assertEquals("ortho", TreatmentItemCodeNormalizer.normalize("正畸"));
        assertEquals("pediatric", TreatmentItemCodeNormalizer.normalize("儿牙"));
    }

    @Test
    public void unknownPassthrough()
    {
        assertEquals("custom_x", TreatmentItemCodeNormalizer.normalize(" custom_x "));
        assertEquals("", TreatmentItemCodeNormalizer.normalize("  "));
        assertEquals("", TreatmentItemCodeNormalizer.normalize(null));
    }
}
