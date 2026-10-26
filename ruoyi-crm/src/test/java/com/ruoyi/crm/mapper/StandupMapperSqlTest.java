package com.ruoyi.crm.mapper;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

/**
 * Standup SQL 口径回归。
 */
public class StandupMapperSqlTest
{
    @Test
    public void winsUseSignAmountOnlyAndExcludeDraftTerminated() throws Exception
    {
        String sql = loadMapperXml();
        assertTrue(sql.contains("COALESCE(c.sign_amount, 0)"), "wins 应仅用 sign_amount");
        assertFalse(sql.contains("COALESCE(c.sign_amount, c.total_amount"), "禁止回退 total_amount");
        assertTrue(sql.contains("'Draft'") && sql.contains("'Terminated'"), "应排除 Draft/Terminated");
        assertTrue(sql.contains("COALESCE(c.sign_time, c.create_time)"), "日期应回退 create_time");
    }

    @Test
    public void bookingCountsExcludeCancelledByDefaultAndGroupByUserId() throws Exception
    {
        String sql = loadMapperXml();
        assertTrue(sql.contains("b.user_id::text AS ownerId") || sql.contains("user_id::text AS ownerId"),
                "预约归属应按 user_id");
        assertTrue(sql.contains("status &lt;&gt; 'cancelled'") || sql.contains("status <> 'cancelled'")
                || sql.contains("b.status &lt;&gt; 'cancelled'"), "默认排除 cancelled");
    }

    @Test
    public void idleLeadsUseAssignedAnchor() throws Exception
    {
        String sql = loadMapperXml();
        assertTrue(sql.contains("last_followup_at") && sql.contains("assigned_at"),
                "闲置锚点应含 last_followup_at / assigned_at");
        assertTrue(sql.contains("'assigned'"), "仅 Assigned 线索");
    }

    private String loadMapperXml() throws Exception
    {
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("mapper/crm/StandupMapper.xml"))
        {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buf = new byte[4096];
            int n;
            while ((n = in.read(buf)) >= 0)
            {
                out.write(buf, 0, n);
            }
            return new String(out.toByteArray(), StandardCharsets.UTF_8);
        }
    }
}
