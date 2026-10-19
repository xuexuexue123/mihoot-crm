package com.ruoyi.crm.service.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.crm.wechatmp.domain.CrmWechatMpConfig;
import com.ruoyi.crm.wechatmp.mapper.CrmWechatMpConfigMapper;

@ExtendWith(MockitoExtension.class)
class WechatMpMenuClickSegmentResolverTest
{
    private static final String APP_ID = "wxTestApp";

    @Mock
    private CrmWechatMpConfigMapper wechatMpConfigMapper;

    private WechatMpMenuClickSegmentValidator menuClickSegmentValidator;

    private WechatMpMenuClickSegmentResolver resolver;

    @BeforeEach
    void setUp() throws Exception
    {
        menuClickSegmentValidator = new WechatMpMenuClickSegmentValidator();
        injectField(menuClickSegmentValidator, "wechatMpConfigMapper", wechatMpConfigMapper);
        resolver = new WechatMpMenuClickSegmentResolver();
        injectField(resolver, "menuClickSegmentValidator", menuClickSegmentValidator);

        CrmWechatMpConfig config = new CrmWechatMpConfig();
        config.setAppId(APP_ID);
        when(wechatMpConfigMapper.selectByAppId(APP_ID)).thenReturn(config);
    }

    @Test
    void compile_exists_clickKeyUsesExactMatch()
    {
        JSONObject value = menuValue("MENU_KEY_1");
        SegmentSqlParamBinder binder = new SegmentSqlParamBinder();
        String sql = resolver.compile(SegmentOperator.EXISTS, value, binder);

        assertTrue(sql.contains("crm_activity a"));
        assertTrue(sql.contains("channel.wechat_mp.menu.click"));
        assertTrue(sql.contains("crm_customer_identity i"));
        assertTrue(sql.contains("wechat_mp_openid"));
        assertTrue(sql.contains("= ?"));
        assertEquals(2, binder.getParams().size());
        assertEquals(APP_ID, binder.getParams().get(0));
        assertEquals("MENU_KEY_1", binder.getParams().get(1));
    }

    @Test
    void compile_notExists_wrapsWithNot()
    {
        SegmentSqlParamBinder binder = new SegmentSqlParamBinder();
        String sql = resolver.compile(SegmentOperator.NOT_EXISTS, menuValue("MENU_KEY_1"), binder);
        assertTrue(sql.startsWith("NOT EXISTS"));
    }

    @Test
    void compile_exists_urlKeyBindsMultipleParams()
    {
        String url = "http://mp.weixin.qq.com/s?mid=1";
        SegmentSqlParamBinder binder = new SegmentSqlParamBinder();
        resolver.compile(SegmentOperator.EXISTS, menuValue(url), binder);

        assertTrue(binder.getParams().size() >= 3);
        assertEquals("http://mp.weixin.qq.com/s?mid=1", binder.getParams().get(1));
        assertEquals("https://mp.weixin.qq.com/s?mid=1", binder.getParams().get(2));
    }

    private static JSONObject menuValue(String menuKey)
    {
        JSONObject value = new JSONObject();
        value.put("appId", APP_ID);
        value.put("menuKey", menuKey);
        value.put("menuName", "测试菜单");
        return value;
    }

    private static void injectField(Object target, String fieldName, Object value) throws Exception
    {
        java.lang.reflect.Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}
