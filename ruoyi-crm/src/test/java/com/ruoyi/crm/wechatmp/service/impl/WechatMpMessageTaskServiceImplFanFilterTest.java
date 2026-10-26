package com.ruoyi.crm.wechatmp.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.crm.wechatmp.audience.FanAudienceCompileOptions;
import com.ruoyi.crm.wechatmp.audience.FanAudienceCompileResult;
import com.ruoyi.crm.wechatmp.audience.FanAudienceCompiler;
import com.ruoyi.crm.wechatmp.audience.FanAudienceFieldRegistry;
import com.ruoyi.crm.wechatmp.dto.WechatMpMessageBatchRequest;
import com.ruoyi.crm.service.support.SegmentOperator;

@ExtendWith(MockitoExtension.class)
class WechatMpMessageTaskServiceImplFanFilterTest
{
    private static final String APP_ID = "wxTestApp";

    @Mock
    private FanAudienceCompiler fanAudienceCompiler;

    @InjectMocks
    private WechatMpMessageTaskServiceImpl messageTaskService;

    private JSONObject filterJson;

    @BeforeEach
    void setUp()
    {
        filterJson = new JSONObject();
        filterJson.put("field", FanAudienceFieldRegistry.FIELD_SUBSCRIBED);
        filterJson.put("operator", SegmentOperator.EXISTS);
    }

    @Test
    void resolveAudience_fanFilterUsesCompilerOpenIds() throws Exception
    {
        Set<String> openIds = new LinkedHashSet<>();
        openIds.add("o1");
        openIds.add("o2");
        when(fanAudienceCompiler.compile(eq(APP_ID), any(), any(FanAudienceCompileOptions.class)))
                .thenReturn(new FanAudienceCompileResult(openIds, 1));

        WechatMpMessageBatchRequest req = new WechatMpMessageBatchRequest();
        req.setAppId(APP_ID);
        req.setAudienceType("fan_filter");
        req.setFilterJson(filterJson);

        Object result = invokeResolveAudience("tenant1", req);
        List<String> resolvedOpenIds = readField(result, "openIds");
        int skipCount = readField(result, "skipCount");

        assertEquals(2, resolvedOpenIds.size());
        assertEquals(1, skipCount);
    }

    private Object invokeResolveAudience(String tenantId, WechatMpMessageBatchRequest req) throws Exception
    {
        Method method = WechatMpMessageTaskServiceImpl.class.getDeclaredMethod(
                "resolveAudience", String.class, WechatMpMessageBatchRequest.class);
        method.setAccessible(true);
        return method.invoke(messageTaskService, tenantId, req);
    }

    @SuppressWarnings("unchecked")
    private static <T> T readField(Object target, String fieldName) throws Exception
    {
        java.lang.reflect.Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return (T) field.get(target);
    }
}
