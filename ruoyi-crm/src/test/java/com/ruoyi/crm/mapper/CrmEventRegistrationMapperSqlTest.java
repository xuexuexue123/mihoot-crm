package com.ruoyi.crm.mapper;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

/**
 * 营销活动报名 SQL 必须排除 registration_source=market_event
 */
public class CrmEventRegistrationMapperSqlTest
{
    @Test
    public void marketingQueriesExcludeMarketEventSource() throws Exception
    {
        String sql = loadMapperXml();
        assertTrue(sql.contains("registration_source <> 'market_event'")
                || sql.contains("registration_source &lt;&gt; 'market_event'"),
                "营销 count/list SQL 应排除 market_event");
        assertTrue(sql.contains("selectRegistrationRows"));
        assertTrue(sql.contains("countConfirmedBySession"));
        assertTrue(sql.contains("selectBestRegistrationByEvent"));
        assertTrue(sql.contains("selectByLeadId"), "应支持 Lead 反查");
        assertTrue(sql.contains("selectByContactId"), "应支持 Contact 反查");
        assertTrue(sql.contains("crm_market_event"), "标题解析应 JOIN 七库");
    }

    @Test
    public void selectActiveBySlotDoesNotExcludeMarketEvent() throws Exception
    {
        String sql = loadMapperXml();
        int activeIdx = sql.indexOf("<select id=\"selectActiveBySlot\"");
        assertTrue(activeIdx > 0);
        int endIdx = sql.indexOf("</select>", activeIdx);
        assertTrue(endIdx > activeIdx);
        String fragment = sql.substring(activeIdx, endIdx);
        assertTrue(!fragment.contains("registration_source &lt;&gt; 'market_event'")
                && !fragment.contains("registration_source <> 'market_event'"),
                "selectActiveBySlot 用于七库幂等，不得全局排除 market_event");
    }

    private String loadMapperXml() throws Exception
    {
        try (InputStream in = getClass().getClassLoader()
                .getResourceAsStream("mapper/crm/CrmEventRegistrationMapper.xml"))
        {
            if (in == null)
            {
                throw new IllegalStateException("CrmEventRegistrationMapper.xml not on classpath");
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
