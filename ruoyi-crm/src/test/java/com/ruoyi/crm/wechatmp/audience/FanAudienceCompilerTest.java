package com.ruoyi.crm.wechatmp.audience;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.crm.service.support.SegmentOperator;
import com.ruoyi.crm.wechatmp.audience.support.FanActivityArticleReadResolver;
import com.ruoyi.crm.wechatmp.audience.support.FanActivityMenuClickResolver;
import com.ruoyi.crm.wechatmp.audience.support.FanActivityScanResolver;
import com.ruoyi.crm.wechatmp.audience.support.FanAudienceScopeHelper;
import com.ruoyi.crm.wechatmp.audience.support.FanTableConditionResolver;
import com.ruoyi.crm.wechatmp.domain.CrmWechatMpConfig;
import com.ruoyi.crm.wechatmp.mapper.CrmWechatMpConfigMapper;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class FanAudienceCompilerTest
{
    private static final String APP_ID = "wxTestApp";

    @Mock
    private CrmWechatMpConfigMapper wechatMpConfigMapper;

    @Mock
    private FanTableConditionResolver fanTableConditionResolver;

    @Mock
    private FanActivityMenuClickResolver menuClickResolver;

    @Mock
    private FanActivityArticleReadResolver articleReadResolver;

    @Mock
    private FanActivityScanResolver scanResolver;

    @Mock
    private FanAudienceScopeHelper scopeHelper;

    private FanAudienceRuleValidator ruleValidator;
    private FanAudienceCompiler compiler;

    @BeforeEach
    void setUp()
    {
        ruleValidator = new FanAudienceRuleValidator();
        injectField(ruleValidator, "wechatMpConfigMapper", wechatMpConfigMapper);
        compiler = new FanAudienceCompiler();
        injectField(compiler, "ruleValidator", ruleValidator);
        injectField(compiler, "fanTableConditionResolver", fanTableConditionResolver);
        injectField(compiler, "menuClickResolver", menuClickResolver);
        injectField(compiler, "articleReadResolver", articleReadResolver);
        injectField(compiler, "scanResolver", scanResolver);
        injectField(compiler, "scopeHelper", scopeHelper);

        CrmWechatMpConfig config = new CrmWechatMpConfig();
        config.setAppId(APP_ID);
        when(wechatMpConfigMapper.selectByAppId(APP_ID)).thenReturn(config);
    }

    @Test
    void compile_andIntersectsChildSets()
    {
        when(fanTableConditionResolver.resolve(APP_ID, FanAudienceFieldRegistry.FIELD_SUBSCRIBED,
                SegmentOperator.EXISTS, null))
                .thenReturn(setOf("o1", "o2", "o3"));
        when(menuClickResolver.resolve(anyString(), anyString(), any()))
                .thenReturn(setOf("o2", "o4"));

        JSONObject filter = and(
                leaf(FanAudienceFieldRegistry.FIELD_SUBSCRIBED, SegmentOperator.EXISTS, null),
                leaf(FanAudienceFieldRegistry.FIELD_MENU_CLICKED, SegmentOperator.EXISTS, menuValue("KEY1")));

        FanAudienceCompileResult result = compiler.compile(APP_ID, filter, FanAudienceCompileOptions.preview());
        assertEquals(1, result.getOpenIds().size());
        assertTrue(result.getOpenIds().contains("o2"));
    }

    @Test
    void compile_orUnionsChildSets()
    {
        when(fanTableConditionResolver.resolve(APP_ID, FanAudienceFieldRegistry.FIELD_SUBSCRIBED,
                SegmentOperator.EXISTS, null))
                .thenReturn(setOf("o1"));
        when(menuClickResolver.resolve(anyString(), anyString(), any()))
                .thenReturn(setOf("o2"));

        JSONObject filter = or(
                leaf(FanAudienceFieldRegistry.FIELD_SUBSCRIBED, SegmentOperator.EXISTS, null),
                leaf(FanAudienceFieldRegistry.FIELD_MENU_CLICKED, SegmentOperator.EXISTS, menuValue("KEY1")));

        FanAudienceCompileResult result = compiler.compile(APP_ID, filter, FanAudienceCompileOptions.preview());
        assertEquals(2, result.getOpenIds().size());
        assertEquals(setOf("o1", "o2"), new LinkedHashSet<>(result.getOpenIds()));
    }

    @Test
    void compile_notUsesSubscribedUniverse()
    {
        when(menuClickResolver.resolve(anyString(), anyString(), any()))
                .thenReturn(setOf("o2"));
        when(scopeHelper.loadSubscribedOpenIds(APP_ID)).thenReturn(setOf("o1", "o2", "o3"));

        JSONObject filter = not(leaf(FanAudienceFieldRegistry.FIELD_MENU_CLICKED, SegmentOperator.EXISTS, menuValue("KEY1")));
        FanAudienceCompileResult result = compiler.compile(APP_ID, filter, FanAudienceCompileOptions.preview());
        assertEquals(setOf("o1", "o3"), new LinkedHashSet<>(result.getOpenIds()));
    }

    @Test
    void compile_forSendAppliesSubscribedDefaultWhenMissing()
    {
        when(fanTableConditionResolver.resolve(APP_ID, FanAudienceFieldRegistry.FIELD_HAS_CONTACT,
                SegmentOperator.EXISTS, null))
                .thenReturn(setOf("o1", "o2"));
        when(scopeHelper.loadSubscribedOpenIds(APP_ID)).thenReturn(setOf("o1"));

        JSONObject filter = leaf(FanAudienceFieldRegistry.FIELD_HAS_CONTACT, SegmentOperator.EXISTS, null);
        FanAudienceCompileResult result = compiler.compile(APP_ID, filter, FanAudienceCompileOptions.forSend());
        assertEquals(setOf("o1"), new LinkedHashSet<>(result.getOpenIds()));
        assertEquals(1, result.getSkipCount());
    }

    @Test
    void listFields_returnsSixEntries()
    {
        assertEquals(6, FanAudienceFieldRegistry.allFieldMeta().size());
    }

    private static JSONObject leaf(String field, String operator, Object value)
    {
        JSONObject node = new JSONObject();
        node.put("field", field);
        node.put("operator", operator);
        if (value != null)
        {
            node.put("value", value);
        }
        return node;
    }

    private static JSONObject and(JSONObject... children)
    {
        JSONObject node = new JSONObject();
        node.put("op", "AND");
        node.put("children", new JSONArray(Arrays.asList(children)));
        return node;
    }

    private static JSONObject or(JSONObject... children)
    {
        JSONObject node = new JSONObject();
        node.put("op", "OR");
        node.put("children", new JSONArray(Arrays.asList(children)));
        return node;
    }

    private static JSONObject not(JSONObject child)
    {
        JSONObject node = new JSONObject();
        node.put("op", "NOT");
        node.put("children", new JSONArray(Arrays.asList(child)));
        return node;
    }

    private static JSONObject menuValue(String menuKey)
    {
        JSONObject value = new JSONObject();
        value.put("menuKey", menuKey);
        return value;
    }

    private static Set<String> setOf(String... ids)
    {
        return new HashSet<>(Arrays.asList(ids));
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
