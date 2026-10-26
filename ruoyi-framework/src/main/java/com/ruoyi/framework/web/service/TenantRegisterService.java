package com.ruoyi.framework.web.service;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.uuid.ShortUUID;
import com.ruoyi.tenant.dto.TenantDatabaseDTO;
import com.ruoyi.tenant.form.TenantRegisterBody;
import com.ruoyi.tenant.website.WebsiteBuilderSkillSeedSupport;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Locale;

/**
 * 租户库初始化（MySQL / PostgreSQL）
 */
@Component
@Slf4j
public class TenantRegisterService {

    @Value("${tenant.database.host}")
    private String host;

    @Value("${tenant.database.port}")
    private String port;

    @Value("${tenant.database.username}")
    private String username;

    @Value("${tenant.database.password}")
    private String password;

    @Value("${tenant.database.db-type:postgresql}")
    private String tenantDatabaseDbType;

    @Value("${tenant.prefix}")
    private String prefix;

    private boolean isPostgreSql() {
        return tenantDatabaseDbType != null
                && tenantDatabaseDbType.toLowerCase(Locale.ROOT).contains("postgres");
    }

    private String getMysqlAdminUrl() {
        return "jdbc:mysql://" + host + ":" + port + "/";
    }

    /** 连接 PostgreSQL 维护库（用于 CREATE DATABASE） */
    private String getPostgresMaintenanceUrl() {
        return "jdbc:postgresql://" + host + ":" + port + "/postgres";
    }

    private Connection getMysqlAdminConnection() throws SQLException {
        return DriverManager.getConnection(getMysqlAdminUrl(), username, password);
    }

    private Connection getPostgresMaintenanceConnection() throws SQLException {
        return DriverManager.getConnection(getPostgresMaintenanceUrl(), username, password);
    }

    /**
     * 校验并规范租户库名（PostgreSQL 未加引号时会折叠为小写，这里统一小写）
     */
    private String normalizeTenantDatabaseName(String raw) {
        String n = raw.toLowerCase(Locale.ROOT);
        if (!n.matches("[a-z][a-z0-9_]*")) {
            throw new ServiceException("非法租户数据库名: " + raw);
        }
        return n;
    }

    private static String sqlLiteral(String s) {
        if (s == null) {
            return "''";
        }
        return "'" + s.replace("'", "''") + "'";
    }

    public TenantDatabaseDTO initDatabase(TenantRegisterBody form) throws Exception {
        if (isPostgreSql()) {
            return initDatabasePostgreSql(form);
        }
        return initDatabaseMysql(form);
    }

    private TenantDatabaseDTO initDatabaseMysql(TenantRegisterBody form) throws Exception {
        Connection conn = getMysqlAdminConnection();
        Statement stmt = null;
        TenantDatabaseDTO tenantDatabaseDTO = null;

        String tenantDatabase = prefix + form.tenantAlias;
        try {
            conn.setAutoCommit(false);
            stmt = conn.createStatement();

            String createDatabaseSQL = "CREATE DATABASE IF NOT EXISTS `" + tenantDatabase + "` DEFAULT CHARSET utf8mb4 COLLATE utf8mb4_general_ci;";
            stmt.execute(createDatabaseSQL);

            String tenantDatabaseUsername = tenantDatabase;
            String tenantDatabasePassword = ShortUUID.nextID();

            String createUser = "create user '" + tenantDatabaseUsername + "'@'localhost' identified by '" + tenantDatabasePassword + "';";
            stmt.execute(createUser);

            String grantSQL = "GRANT select, insert, update, delete ON " + tenantDatabase + ".* TO '" + tenantDatabaseUsername + "'@'localhost';";
            stmt.execute(grantSQL);

            conn.setCatalog(tenantDatabase);

            tenantDatabaseDTO = new TenantDatabaseDTO();
            tenantDatabaseDTO.setCompanyName(form.getCompanyName());
            tenantDatabaseDTO.setTenantDatabase(tenantDatabase);
            tenantDatabaseDTO.setTenantName(form.tenantName);
            tenantDatabaseDTO.setDbUser(tenantDatabaseUsername);
            tenantDatabaseDTO.setDbPass(tenantDatabasePassword);
            tenantDatabaseDTO.setAdminName(form.adminName);
            tenantDatabaseDTO.setAdminPass(form.adminPass);
            String tenantUrl = getMysqlAdminUrl() + tenantDatabaseDTO.getTenantDatabase()
                    + "?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8";
            tenantDatabaseDTO.setUrl(tenantUrl);

            executeInitScriptMysql(conn, tenantDatabaseDTO);

        } catch (Exception ex) {
            ex.printStackTrace();
            if (stmt != null) {
                try {
                    stmt.execute("DROP DATABASE IF EXISTS " + tenantDatabase);
                } catch (SQLException ignored) {
                    log.error("回滚删除数据库失败: {}", tenantDatabase, ignored);
                }
            }
            log.error("删除数据库:{}", tenantDatabase);
            throw new ServiceException("执行数据库操作时发生错误");
        } finally {
            if (stmt != null) {
                stmt.close();
            }
            conn.close();
        }
        return tenantDatabaseDTO;
    }

    private TenantDatabaseDTO initDatabasePostgreSql(TenantRegisterBody form) throws Exception {
        String tenantDatabase = normalizeTenantDatabaseName(prefix + form.tenantAlias);
        String tenantDatabasePassword = ShortUUID.nextID();
        String tenantDatabaseUsername = tenantDatabase;

        TenantDatabaseDTO tenantDatabaseDTO = new TenantDatabaseDTO();
        tenantDatabaseDTO.setCompanyName(form.getCompanyName());
        tenantDatabaseDTO.setTenantDatabase(tenantDatabase);
        tenantDatabaseDTO.setTenantName(form.tenantName);
        tenantDatabaseDTO.setDbUser(tenantDatabaseUsername);
        tenantDatabaseDTO.setDbPass(tenantDatabasePassword);
        tenantDatabaseDTO.setAdminName(form.adminName);
        tenantDatabaseDTO.setAdminPass(form.adminPass);
        tenantDatabaseDTO.setUrl("jdbc:postgresql://" + host + ":" + port + "/" + tenantDatabase);

        try (Connection adminConn = getPostgresMaintenanceConnection()) {
            adminConn.setAutoCommit(true);
            try (Statement st = adminConn.createStatement()) {
                st.execute("CREATE ROLE " + tenantDatabaseUsername + " WITH LOGIN PASSWORD " + sqlLiteral(tenantDatabasePassword)
                        + " NOSUPERUSER NOCREATEDB NOCREATEROLE");
                st.execute("CREATE DATABASE " + tenantDatabase + " OWNER " + tenantDatabaseUsername + " ENCODING 'UTF8'");
            }
        } catch (SQLException ex) {
            log.error("PostgreSQL 创建租户库失败: {}", tenantDatabase, ex);
            tryRollbackPostgresDb(tenantDatabase, tenantDatabaseUsername);
            throw new ServiceException("执行数据库操作时发生错误");
        }

        try (Connection tenantConn = DriverManager.getConnection(
                "jdbc:postgresql://" + host + ":" + port + "/" + tenantDatabase,
                tenantDatabaseUsername,
                tenantDatabasePassword)) {
            tenantConn.setAutoCommit(false);
            executeInitScriptPostgreSql(tenantConn, tenantDatabaseDTO);
            tenantConn.commit();
        } catch (Exception ex) {
            ex.printStackTrace();
            tryRollbackPostgresDb(tenantDatabase, tenantDatabaseUsername);
            throw new ServiceException("初始化租户数据时发生错误");
        }

        return tenantDatabaseDTO;
    }

    private void tryRollbackPostgresDb(String tenantDatabase, String roleName) {
        try (Connection adminConn = getPostgresMaintenanceConnection()) {
            adminConn.setAutoCommit(true);
            try (Statement st = adminConn.createStatement()) {
                try {
                    st.execute("SELECT pg_terminate_backend(pid) FROM pg_stat_activity WHERE datname = "
                            + sqlLiteral(tenantDatabase) + " AND pid <> pg_backend_pid()");
                } catch (SQLException ignored) {
                    // best-effort
                }
                try {
                    st.execute("DROP DATABASE IF EXISTS " + tenantDatabase + " WITH (FORCE)");
                } catch (SQLException e) {
                    log.debug("DROP DATABASE WITH (FORCE) skipped or unsupported: {}", e.getMessage());
                    st.execute("DROP DATABASE IF EXISTS " + tenantDatabase);
                }
                st.execute("DROP ROLE IF EXISTS " + roleName);
            }
        } catch (SQLException e) {
            log.error("回滚 PostgreSQL 租户库失败: {}", tenantDatabase, e);
        }
    }

    private void executeInitScriptMysql(Connection conn, TenantDatabaseDTO tenantDatabaseDTO) throws SQLException {
        try {
            ScriptRunner runner = new ScriptRunner(conn);
            runner.setErrorLogWriter(null);
            runner.setLogWriter(null);

            Resources.setCharset(StandardCharsets.UTF_8);
            Reader reader = Resources.getResourceAsReader("init-sql-script/rycrm-tenant-sample.sql");
            runner.runScript(reader);

            runTenantSeedSql(conn, tenantDatabaseDTO, true);
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            conn.rollback();
            throw new ServiceException("初始化用户数据脚本时出错");
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new ServiceException("执行初始用户数据时出错");
        }
    }

    private void executeInitScriptPostgreSql(Connection conn, TenantDatabaseDTO tenantDatabaseDTO) throws SQLException {
        try {
            // DDL phase: auto-commit each statement so one failure doesn't abort the whole transaction
            conn.setAutoCommit(true);
            ScriptRunner runner = new ScriptRunner(conn);
            runner.setAutoCommit(true);
            runner.setErrorLogWriter(null);
            runner.setLogWriter(null);
            Resources.setCharset(StandardCharsets.UTF_8);
            Reader reader = Resources.getResourceAsReader("init-sql-script/tenant_bootstrap_postgresql_full.sql");
            runner.runScript(reader);

            // Seed data: roles, menus, permissions
            ScriptRunner seedRunner = new ScriptRunner(conn);
            seedRunner.setAutoCommit(true);
            seedRunner.setErrorLogWriter(null);
            seedRunner.setLogWriter(null);
            Reader seedReader = Resources.getResourceAsReader("init-sql-script/tenant_seed_postgresql.sql");
            seedRunner.runScript(seedReader);

            // Seed data phase: manual transaction
            conn.setAutoCommit(false);
            runTenantSeedSql(conn, tenantDatabaseDTO, false);

            // 重置所有序列到当前最大值，防止显式 ID 插入后序列不同步
            resetAllSequences(conn);
        } catch (SQLException e) {
            e.printStackTrace();
            try { conn.rollback(); } catch (SQLException ignored) { }
            throw new ServiceException("初始化用户数据脚本时出错");
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new ServiceException("执行初始用户数据时出错");
        }
    }

    private void runTenantSeedSql(Connection conn, TenantDatabaseDTO tenantDatabaseDTO, boolean mysqlBackticks) throws SQLException {
        String now = DateUtils.getTime();

        // sys_organization
        String orgSql = "INSERT INTO sys_organization VALUES ('2','',?,'','13641173537','','','','2023-03-29 11:01:12','','2024-03-17 15:32:44','91430102MA7ADAWL2U','','','','','')";
        try (PreparedStatement ps = conn.prepareStatement(orgSql)) {
            ps.setString(1, tenantDatabaseDTO.getCompanyName());
            ps.executeUpdate();
        }

        // sys_dept
        String deptSql = "INSERT INTO sys_dept VALUES (100, 0, '0', ?, 0, ?, '00000000000', ?, '0', '0', 'admin', ?::timestamp, 'admin', ?::timestamp)";
        try (PreparedStatement ps = conn.prepareStatement(deptSql)) {
            ps.setString(1, tenantDatabaseDTO.getCompanyName());
            ps.setString(2, tenantDatabaseDTO.getTenantName());
            ps.setString(3, tenantDatabaseDTO.getAdminName());
            ps.setString(4, now);
            ps.setString(5, now);
            ps.executeUpdate();
        }

        // sys_user — tenant administrator
        String operatorName = tenantDatabaseDTO.getAdminName();
        if (operatorName != null && !operatorName.isEmpty() && operatorName.contains("@")) {
            operatorName = "admin" + operatorName.substring(operatorName.indexOf("@"));
        }
        String encryptPassword = SecurityUtils.encryptPassword(tenantDatabaseDTO.getAdminPass());
        String userSql = "INSERT INTO sys_user (user_id, organization_id, dept_id, user_name, nick_name, user_type, email, phonenumber, sex, avatar, password, status, del_flag, login_ip, login_date, create_by, create_time, update_by, update_time, remark) VALUES (1, 2, 100, ?, '管理员', '00', ?, '00000000000', '1', '', ?, '0', '0', '127.0.0.1', ?::timestamp, ?, ?::timestamp, ?, ?::timestamp, '管理员')";
        try (PreparedStatement ps = conn.prepareStatement(userSql)) {
            ps.setString(1, operatorName);
            ps.setString(2, operatorName);
            ps.setString(3, encryptPassword);
            ps.setString(4, now);
            ps.setString(5, operatorName);
            ps.setString(6, now);
            ps.setString(7, "");
            ps.setString(8, now);
            ps.executeUpdate();
        }

        WebsiteBuilderSkillSeedSupport.seedIfAbsent(conn, tenantDatabaseDTO.getTenantName(), "system");
    }

    /**
     * Bootstrap SQL 脚本中显式指定了主键 ID 插入数据，但 PostgreSQL SERIAL 序列不会自动更新。
     * 通过 pg_depend 目录查找每个序列拥有的列和表，重置到当前最大值。
     */
    private void resetAllSequences(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                 "SELECT d.refobjid::regclass::text AS tbl_name, " +
                 "       a.attname AS col_name, " +
                 "       c.relname AS seq_name " +
                 "FROM pg_depend d " +
                 "JOIN pg_class c ON c.oid = d.objid AND c.relkind = 'S' " +
                 "JOIN pg_namespace n ON n.oid = c.relnamespace AND n.nspname = 'public' " +
                 "JOIN pg_attribute a ON a.attrelid = d.refobjid AND a.attnum = d.refobjsubid " +
                 "WHERE d.deptype = 'a'")) {

            while (rs.next()) {
                String seqName = rs.getString("seq_name");
                String tblName = rs.getString("tbl_name");
                String colName = rs.getString("col_name");

                String sql = "SELECT setval('" + seqName + "', " +
                        "COALESCE((SELECT MAX(" + colName + ") FROM " + tblName + "), 1))";
                try (Statement resetStmt = conn.createStatement()) {
                    resetStmt.execute(sql);
                } catch (SQLException e) {
                    log.debug("Failed to reset sequence {}: {}", seqName, e.getMessage());
                }
            }
            log.info("PostgreSQL sequences reset successfully for tenant DB");
        }
    }

}
