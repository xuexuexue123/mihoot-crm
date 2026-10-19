package com.ruoyi.crm.wechatmp.audience;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.crm.service.support.SegmentOperator;
import com.ruoyi.crm.wechatmp.domain.CrmWechatMpConfig;
import com.ruoyi.crm.wechatmp.mapper.CrmWechatMpConfigMapper;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class FanAudienceRuleValidatorTest
{
    private static final String APP_ID = "wxTestApp";

    @Mock
    private CrmWechatMpConfigMapper wechatMpConfigMapper;

    private FanAudienceRuleValidator validator;

    @BeforeEach
    void setUp()
    {
        validator = new FanAudienceRuleValidator();
        injectField(validator, "wechatMpConfigMapper", wechatMpConfigMapper);
        CrmWechatMpConfig config = new CrmWechatMpConfig();
        config.setAppId(APP_ID);
        when(wechatMpConfigMapper.selectByAppId(APP_ID)).thenReturn(config);
    }

    @Test
    void validateAppId_rejectsUnknownApp()
    {
        when(wechatMpConfigMapper.selectByAppId("wxUnknown")).thenReturn(null);
        ServiceException ex = assertThrows(ServiceException.class, () -> validator.validateAppId("wxUnknown"));
        assertTrue(ex.getMessage().contains("不属于当前租户"));
    }

    @Test
    void validateFilterJson_requiresLeaf()
    {
        JSONObject branchOnly = new JSONObject();
        branchOnly.put("op", "AND");
        branchOnly.put("children", new JSONArray());
        ServiceException ex = assertThrows(ServiceException.class, () -> validator.validateFilterJson(branchOnly));
        assertTrue(ex.getMessage().contains("children"));
    }

    @Test
    void validateFilterJson_acceptsSubscribedLeaf()
    {
        JSONObject leaf = new JSONObject();
        leaf.put("field", FanAudienceFieldRegistry.FIELD_SUBSCRIBED);
        leaf.put("operator", SegmentOperator.EXISTS);
        validator.validateFilterJson(leaf);
    }

    @Test
    void validateFilterJson_requiresMenuKey()
    {
        JSONObject leaf = new JSONObject();
        leaf.put("field", FanAudienceFieldRegistry.FIELD_MENU_CLICKED);
        leaf.put("operator", SegmentOperator.EXISTS);
        leaf.put("value", new JSONObject());
        ServiceException ex = assertThrows(ServiceException.class, () -> validator.validateFilterJson(leaf));
        assertTrue(ex.getMessage().contains("menuKey"));
    }

    @Test
    void parseArticleRef_requiresUrlOrArticleId()
    {
        JSONObject value = new JSONObject();
        value.put("title", "only title");
        ServiceException ex = assertThrows(ServiceException.class,
                () -> validator.parseArticleRef(SegmentOperator.EXISTS, value));
        assertTrue(ex.getMessage().contains("url 或 articleId"));
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
