package com.ruoyi.tenant.website;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;

import com.ruoyi.common.utils.StringUtils;

/**
 * 建站 AI Skill（website-builder）种子数据 — 单一来源常量与 JDBC 写入。
 */
public final class WebsiteBuilderSkillSeedSupport
{
    public static final String SKILL_NAME = "website-builder";

    public static final String DOMAIN = "website";

    public static final String DESCRIPTION = "AI 官网建站：规划页面结构并生成 Puck 可编辑的 Website Schema";

    public static final String TRIGGER_KEYWORDS = "[\"建站\",\"网站\",\"官网\",\"landing\"]";

    public static final String DEFAULT_INSTRUCTION =
            "你是租户官网建站助手。输出必须符合 WebsitePageSchema（version: 1），禁止输出 HTML。\n"
                    + "\n"
                    + "页面结构建议（口腔/医疗诊所）：\n"
                    + "- 首页 /：navbar → hero → serviceList → featureGrid → testimonials → contact → footer\n"
                    + "- 关于 /about：navbar → hero → teamGrid → richText → footer\n"
                    + "- 价格 /pricing：navbar → hero → pricing → cta → footer\n"
                    + "\n"
                    + "规则：\n"
                    + "- blocks[].type 仅使用系统支持的 block 类型（由运行时注入白名单）\n"
                    + "- 每个 block 须有唯一 id（如 hero-1、nav-1）\n"
                    + "- 导航 links 的 href 须与站点已有 path 一致\n"
                    + "- 文案使用用户指定的语言（默认 zh）\n"
                    + "- CTA 优先指向 #contact 或站内 path";

    private WebsiteBuilderSkillSeedSupport()
    {
    }

    /**
     * 若租户尚无 website-builder Skill，则插入默认记录（幂等）。
     */
    public static void seedIfAbsent(Connection conn, String tenantId, String createBy) throws SQLException
    {
        if (StringUtils.isBlank(tenantId))
        {
            return;
        }
        String operator = StringUtils.isNotBlank(createBy) ? createBy : "system";
        if (exists(conn, tenantId))
        {
            return;
        }
        String insertSql = "INSERT INTO crm_ai_skill ("
                + "tenant_id, name, description, instruction, trigger_keywords, scope, priority, enabled, "
                + "skill_type, domain, create_by, create_time, update_by, update_time"
                + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        Timestamp now = Timestamp.from(Instant.now());
        try (PreparedStatement ps = conn.prepareStatement(insertSql))
        {
            ps.setString(1, tenantId);
            ps.setString(2, SKILL_NAME);
            ps.setString(3, DESCRIPTION);
            ps.setString(4, DEFAULT_INSTRUCTION);
            ps.setString(5, TRIGGER_KEYWORDS);
            ps.setString(6, "all");
            ps.setInt(7, 100);
            ps.setInt(8, 1);
            ps.setString(9, "prompt");
            ps.setString(10, DOMAIN);
            ps.setString(11, operator);
            ps.setTimestamp(12, now);
            ps.setString(13, operator);
            ps.setTimestamp(14, now);
            ps.executeUpdate();
        }
    }

    private static boolean exists(Connection conn, String tenantId) throws SQLException
    {
        String sql = "SELECT 1 FROM crm_ai_skill WHERE tenant_id = ? AND name = ? LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(sql))
        {
            ps.setString(1, tenantId);
            ps.setString(2, SKILL_NAME);
            try (ResultSet rs = ps.executeQuery())
            {
                return rs.next();
            }
        }
    }
}
