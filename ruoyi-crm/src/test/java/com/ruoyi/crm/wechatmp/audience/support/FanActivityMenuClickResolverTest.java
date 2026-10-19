package com.ruoyi.crm.wechatmp.audience.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.crm.service.support.SegmentOperator;
import com.ruoyi.crm.wechatmp.audience.FanAudienceRuleValidator;

@ExtendWith(MockitoExtension.class)
class FanActivityMenuClickResolverTest
{
    private static final String APP_ID = "wxTestApp";

    @Mock
    private JdbcTemplate jdbcTemplate;

    @Mock
    private FanAudienceScopeHelper scopeHelper;

    private FanActivityMenuClickResolver resolver;

    @BeforeEach
    void setUp()
    {
        FanAudienceRuleValidator ruleValidator = new FanAudienceRuleValidator();
        resolver = new FanActivityMenuClickResolver();
        injectField(resolver, "jdbcTemplate", jdbcTemplate);
        injectField(resolver, "ruleValidator", ruleValidator);
        injectField(resolver, "scopeHelper", scopeHelper);
    }

    @Test
    void resolve_exists_queriesMenuClickActivity()
    {
        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        when(jdbcTemplate.query(sqlCaptor.capture(), any(Object[].class), any(RowMapper.class)))
                .thenReturn(Collections.singletonList("o1"));

        JSONObject value = new JSONObject();
        value.put("menuKey", "MENU_KEY_1");
        Set<String> result = resolver.resolve(APP_ID, SegmentOperator.EXISTS, value);

        assertEquals(1, result.size());
        assertTrue(result.contains("o1"));
        assertTrue(sqlCaptor.getValue().contains("channel.wechat_mp.menu.click"));
        assertTrue(sqlCaptor.getValue().contains("DISTINCT"));
    }

    private static void injectField(Object target, String fieldName, Object value)
    {
        try
        {
            java.lang.reflect.Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        }
        catch (ReflectiveOperationException e)
        {
            throw new RuntimeException(e);
        }
    }
}
