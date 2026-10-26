package com.ruoyi.crm.mapper;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

/**
 * 活动效果归因 SQL 回归
 */
public class EventPerformanceMapperSqlTest
{
    @Test
    public void attributedContactsUnionContactConversionAndWecom() throws Exception
    {
        String sql = loadMapperXml();
        assertTrue(sql.contains("conversion_type = 'Contact'"), "应包含 Contact 转化归因");
        assertTrue(sql.contains("crm_wecom_external_contact"), "应包含企微直接 contact 归因");
        assertTrue(sql.contains("crm_wechat_mp_fan"), "应包含公众号粉丝 contact 归因");
        assertTrue(sql.contains("UNION"), "应使用 UNION 去重归因 contact");
    }

    private String loadMapperXml() throws Exception
    {
        try (InputStream in = getClass().getClassLoader()
                .getResourceAsStream("mapper/crm/EventPerformanceMapper.xml"))
        {
            if (in == null)
            {
                throw new IllegalStateException("EventPerformanceMapper.xml not on classpath");
            }
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            int read;
            while ((read = in.read(buffer)) != -1)
            {
                out.write(buffer, 0, read);
            }
            return new String(out.toByteArray(), StandardCharsets.UTF_8);
        }
    }
}
