package com.ruoyi.crm.wechatmp.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.LinkedHashSet;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.crm.service.support.SegmentOperator;
import com.ruoyi.crm.wechatmp.audience.FanAudienceCompileOptions;
import com.ruoyi.crm.wechatmp.audience.FanAudienceCompileResult;
import com.ruoyi.crm.wechatmp.audience.FanAudienceCompiler;
import com.ruoyi.crm.wechatmp.audience.FanAudienceFieldRegistry;
import com.ruoyi.crm.wechatmp.audience.dto.FanAudiencePreviewRequest;
import com.ruoyi.crm.wechatmp.audience.vo.FanAudiencePreviewVo;

@ExtendWith(MockitoExtension.class)
class FanAudienceServiceImplTest
{
    private static final String APP_ID = "wxTestApp";

    @Mock
    private FanAudienceCompiler fanAudienceCompiler;

    @InjectMocks
    private FanAudienceServiceImpl fanAudienceService;

    private JSONObject filterJson;

    @BeforeEach
    void setUp()
    {
        filterJson = new JSONObject();
        filterJson.put("field", FanAudienceFieldRegistry.FIELD_SUBSCRIBED);
        filterJson.put("operator", SegmentOperator.EXISTS);
    }

    @Test
    void preview_returnsCountAndSample()
    {
        Set<String> openIds = new LinkedHashSet<>();
        openIds.add("o1");
        openIds.add("o2");
        when(fanAudienceCompiler.compile(eq(APP_ID), any(), any(FanAudienceCompileOptions.class)))
                .thenReturn(new FanAudienceCompileResult(openIds, 0));

        FanAudiencePreviewRequest request = new FanAudiencePreviewRequest();
        request.setAppId(APP_ID);
        request.setFilterJson(filterJson);

        FanAudiencePreviewVo vo = fanAudienceService.preview(request);
        assertEquals(2, vo.getCount());
        assertEquals(2, vo.getSampleOpenIds().size());
    }
}
