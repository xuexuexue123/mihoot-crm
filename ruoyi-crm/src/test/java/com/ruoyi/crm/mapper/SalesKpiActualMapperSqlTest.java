package com.ruoyi.crm.mapper;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

/**
 * KPI 聚合 SQL 口径回归（禁止 sign_amount 回退 amount；分配不含企微转接）。
 */
public class SalesKpiActualMapperSqlTest
{
    @Test
    public void contractAmountUsesSignAmountOnly() throws Exception
    {
        String sql = loadMapperXml();
        assertTrue(sql.contains("COALESCE(c.sign_amount, 0)"), "合同金额应仅用 sign_amount");
        assertFalse(sql.contains("COALESCE(c.sign_amount, c.total_amount"), "禁止回退 total_amount");
        assertFalse(sql.contains("COALESCE(sign_amount, amount)"), "禁止回退 amount");
        assertTrue(sql.contains("'Draft'") && sql.contains("'Terminated'"), "应排除 Draft/Terminated");
    }

    @Test
    public void assignedCustomersUseLeadAssignedAndExcludeTransfer() throws Exception
    {
        String sql = loadMapperXml();
        assertTrue(sql.contains("lead.assigned"), "分配客户应基于 lead.assigned");
        assertTrue(sql.contains("assigned_toid"), "归属应读 payload assigned_toid");
        assertTrue(sql.contains("wecom_transfer") || sql.contains("transfer_customer"),
                "应排除企微转接口径");
    }

    @Test
    public void salesgoalMapperHasUniqueAndAssess() throws Exception
    {
        String sql = loadSalesgoalMapperXml();
        assertTrue(sql.contains("kpi_code"), "应映射 kpi_code");
        assertTrue(sql.contains("updateAssessBatch") || sql.contains("updateAssessBatch".toLowerCase())
                || sql.contains("id=\"updateAssessBatch\""), "应有批量考核");
        assertTrue(sql.contains("Monthly"), "默认 Monthly 过滤");
    }

    private String loadMapperXml() throws Exception
    {
        return loadClasspath("mapper/crm/SalesKpiActualMapper.xml");
    }

    private String loadSalesgoalMapperXml() throws Exception
    {
        return loadClasspath("mapper/crm/CrmSalesgoalMapper.xml");
    }

    private String loadClasspath(String path) throws Exception
    {
        try (InputStream in = getClass().getClassLoader().getResourceAsStream(path))
        {
            if (in == null)
            {
                throw new IllegalStateException(path + " not on classpath");
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
