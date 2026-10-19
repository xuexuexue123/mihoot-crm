package com.ruoyi.crm.mapper;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

/**
 * 活动受众 MP 数据源 SQL 回归：openid 多字段读取 + 推广码 JOIN 兜底。
 */
public class EventAudienceMapperSqlTest
{
    @Test
    public void selectMpSourcesReadsOpenIdFromPayloadAndContext() throws Exception
    {
        String sql = loadMapperXml();
        assertTrue(sql.contains("a.payload->>'open_id'"), "应读取 payload.open_id");
        assertTrue(sql.contains("a.context->>'open_id'"), "应读取 context.open_id");
        assertTrue(sql.contains("COALESCE("), "openid 应使用 COALESCE");
    }

    @Test
    public void selectMpSourcesJoinsQrcodeWhenCrmEventIdMissing() throws Exception
    {
        String sql = loadMapperXml();
        assertTrue(sql.contains("crm_wechat_mp_qrcode q"), "应通过推广码表兜底归因");
        assertTrue(sql.contains("a.context->>'scene_str' = q.scene_str"), "应匹配 scene_str");
        assertTrue(sql.contains("'qrscene_' || q.scene_str"), "应匹配 payload event_key");
    }

    @Test
    public void selectWebsiteSourcesFiltersWebsiteRegistrationSource() throws Exception
    {
        String sql = loadMapperXml();
        assertTrue(sql.contains("selectWebsiteSources"), "应定义 selectWebsiteSources");
        assertTrue(sql.contains("'website' AS channel"), "website 渠道标记");
        assertTrue(sql.contains("r.registration_source = 'website'"), "应按 website 过滤");
    }

    private String loadMapperXml() throws Exception
    {
        try (InputStream in = getClass().getClassLoader()
                .getResourceAsStream("mapper/crm/EventAudienceMapper.xml"))
        {
            if (in == null)
            {
                throw new IllegalStateException("EventAudienceMapper.xml not on classpath");
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
