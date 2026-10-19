package com.ruoyi.crm.wechatmp.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class WechatMpMenuUrlNormalizerTest
{
    @Test
    void isUrlMenuKey_detectsHttpUrls()
    {
        assertTrue(WechatMpMenuUrlNormalizer.isUrlMenuKey("https://mp.weixin.qq.com/s/abc"));
        assertTrue(WechatMpMenuUrlNormalizer.isUrlMenuKey("http://mp.weixin.qq.com/s/abc"));
        assertFalse(WechatMpMenuUrlNormalizer.isUrlMenuKey("MENU_KEY_1"));
    }

    @Test
    void normalize_collapsesPathSlashes()
    {
        String normalized = WechatMpMenuUrlNormalizer.normalize(
                "http://mp.weixin.qq.com//s?__biz=abc&mid=1");
        assertEquals("https://mp.weixin.qq.com/s?__biz=abc&mid=1", normalized);
    }

    @Test
    void normalize_trimsWhitespace()
    {
        assertEquals("https://mp.weixin.qq.com/s/x",
                WechatMpMenuUrlNormalizer.normalize("  https://mp.weixin.qq.com/s/x  "));
    }

    @Test
    void normalize_nonUrlReturnsTrimmedKey()
    {
        assertEquals("my_menu_key", WechatMpMenuUrlNormalizer.normalize("  my_menu_key  "));
    }

    @Test
    void normalizeForSqlMatch_stripsFragmentAndLowercases()
    {
        String sqlMatch = WechatMpMenuUrlNormalizer.normalizeForSqlMatch(
                "HTTPS://MP.WEIXIN.QQ.COM/s/x#rd");
        assertEquals("https://mp.weixin.qq.com/s/x", sqlMatch);
    }
}
