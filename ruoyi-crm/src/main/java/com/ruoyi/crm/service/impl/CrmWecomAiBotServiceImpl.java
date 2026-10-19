package com.ruoyi.crm.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.sign.AESUtil;
import com.ruoyi.crm.domain.CrmWecomAiBotConfig;
import com.ruoyi.crm.domain.CrmWecomAiBotConnection;
import com.ruoyi.crm.domain.CrmWecomConfig;
import com.ruoyi.crm.domain.dto.wecom.WecomAiBotConfigSaveRequest;
import com.ruoyi.crm.domain.dto.wecom.WecomAiBotStatusReportRequest;
import com.ruoyi.crm.domain.dto.wecom.ResolveUserResult;
import com.ruoyi.crm.domain.dto.wecom.TenantBotCommand;
import com.ruoyi.crm.mapper.CrmWecomAiBotConfigMapper;
import com.ruoyi.crm.mapper.CrmWecomAiBotConnectionMapper;
import com.ruoyi.crm.mapper.CrmWecomConfigMapper;
import com.ruoyi.crm.service.ICrmWecomAiBotService;
import com.ruoyi.framework.datasource.DynamicDataSourceContextHolder;
import com.ruoyi.framework.datasource.DynamicRoutingDataSource;
import com.ruoyi.framework.web.service.SysPermissionService;
import com.ruoyi.framework.web.service.TokenService;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.tenant.domain.MasterTenant;
import com.ruoyi.tenant.domain.enums.TenantStatus;
import com.ruoyi.tenant.service.IMasterTenantService;
import com.ruoyi.system.mapper.SysUserMapper;

@Slf4j
@Service
public class CrmWecomAiBotServiceImpl implements ICrmWecomAiBotService
{
    private static final String STATUS_DISCONNECTED = "disconnected";
    private static final String STATUS_CONNECTING = "connecting";
    private static final String STATUS_CONNECTED = "connected";
    private static final String STATUS_DISCONNECTING = "disconnecting";
    private static final String STATUS_RECONNECTING = "reconnecting";

    @Autowired
    private CrmWecomAiBotConfigMapper crmWecomAiBotConfigMapper;

    @Autowired
    private CrmWecomAiBotConnectionMapper crmWecomAiBotConnectionMapper;

    @Autowired
    private CrmWecomConfigMapper crmWecomConfigMapper;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private SysPermissionService permissionService;

    @Autowired
    private IMasterTenantService masterTenantService;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private DynamicRoutingDataSource dynamicRoutingDataSource;

    @Value("${spring.datasource.driverClassName}")
    private String driverClassName;

    @Override
    public Map<String, Object> getConfigMasked(String tenant)
    {
        try
        {
            DynamicDataSourceContextHolder.setDataSourceKey(tenant);
            CrmWecomAiBotConfig cfg = crmWecomAiBotConfigMapper.selectFirst();
            return buildConfigMap(cfg);
        }
        finally
        {
            DynamicDataSourceContextHolder.clearDataSourceKey();
        }
    }

    @Override
    public Map<String, Object> saveOrUpdateConfig(WecomAiBotConfigSaveRequest body, String tenant)
    {
        try
        {
            DynamicDataSourceContextHolder.setDataSourceKey(tenant);

            if (body.getBotId() == null || body.getBotId().trim().isEmpty())
            {
                throw new ServiceException("botId 不能为空");
            }

            CrmWecomAiBotConfig existing = crmWecomAiBotConfigMapper.selectFirst();

            boolean isNew = (existing == null);
            if (isNew && (body.getBotSecret() == null || body.getBotSecret().trim().isEmpty()))
            {
                throw new ServiceException("首次配置需要填写机器人 Secret");
            }

            CrmWecomAiBotConfig row = new CrmWecomAiBotConfig();
            row.setTenant(tenant);
            row.setBotId(body.getBotId().trim());
            row.setEnabled(true);
            row.setUpdatedAt(java.time.LocalDateTime.now());

            if (body.getBotSecret() != null && !body.getBotSecret().trim().isEmpty())
            {
                try
                {
                    row.setBotSecretEnc(AESUtil.encrypt(body.getBotSecret().trim()));
                }
                catch (Exception e)
                {
                    throw new ServiceException("Secret 加密失败");
                }
            }
            else if (existing != null)
            {
                row.setBotSecretEnc(existing.getBotSecretEnc());
            }

            if (isNew)
            {
                row.setCreatedAt(java.time.LocalDateTime.now());
            }

            crmWecomAiBotConfigMapper.upsert(row);

            // 自动连接：仅在 botSecret 已提供时触发（新建配置或 rotated secret）
            if (body.getBotSecret() != null && !body.getBotSecret().trim().isEmpty())
            {
                CrmWecomConfig wecomCfg = crmWecomConfigMapper.selectFirst();
                boolean wecomReady = wecomCfg != null
                        && wecomCfg.getCorpId() != null && !wecomCfg.getCorpId().trim().isEmpty();
                if (wecomReady)
                {
                    CrmWecomAiBotConnection conn = crmWecomAiBotConnectionMapper.selectFirst();
                    String currentStatus = (conn != null) ? conn.getStatus() : null;
                    if (currentStatus == null || STATUS_DISCONNECTED.equals(currentStatus))
                    {
                        trySetConnecting(tenant, body.getBotId().trim());
                    }
                }
            }

            return getConfigMasked(tenant);
        }
        finally
        {
            DynamicDataSourceContextHolder.clearDataSourceKey();
        }
    }

    @Override
    public Map<String, Object> getStatus(String tenant)
    {
        try
        {
            DynamicDataSourceContextHolder.setDataSourceKey(tenant);
            CrmWecomAiBotConnection conn = crmWecomAiBotConnectionMapper.selectFirst();
            return buildConnectionMap(conn);
        }
        finally
        {
            DynamicDataSourceContextHolder.clearDataSourceKey();
        }
    }

    @Override
    public Map<String, Object> requestConnect(String tenant)
    {
        try
        {
            DynamicDataSourceContextHolder.setDataSourceKey(tenant);

            CrmWecomAiBotConfig cfg = crmWecomAiBotConfigMapper.selectFirst();
            if (cfg == null || cfg.getBotId() == null || cfg.getBotId().trim().isEmpty())
            {
                throw new ServiceException("请先配置智能机器人");
            }

            CrmWecomConfig wecomCfg = crmWecomConfigMapper.selectFirst();
            if (wecomCfg == null || wecomCfg.getCorpId() == null || wecomCfg.getCorpId().trim().isEmpty())
            {
                throw new ServiceException("请先配置企业微信应用连接");
            }

            CrmWecomAiBotConnection conn = crmWecomAiBotConnectionMapper.selectFirst();
            if (conn != null)
            {
                String currentStatus = conn.getStatus();
                if (STATUS_CONNECTING.equals(currentStatus)
                        || STATUS_CONNECTED.equals(currentStatus)
                        || STATUS_RECONNECTING.equals(currentStatus))
                {
                    throw new ServiceException("连接已在进行中");
                }
            }

            boolean set = trySetConnecting(tenant, cfg.getBotId());
            if (!set)
            {
                throw new ServiceException("无法建立连接，请稍后重试");
            }

            Map<String, Object> result = new HashMap<>();
            result.put("status", STATUS_CONNECTING);
            return result;
        }
        finally
        {
            DynamicDataSourceContextHolder.clearDataSourceKey();
        }
    }

    @Override
    public Map<String, Object> requestDisconnect(String tenant)
    {
        try
        {
            DynamicDataSourceContextHolder.setDataSourceKey(tenant);

            CrmWecomAiBotConnection conn = crmWecomAiBotConnectionMapper.selectFirst();
            if (conn != null && STATUS_DISCONNECTED.equals(conn.getStatus()))
            {
                Map<String, Object> result = new HashMap<>();
                result.put("status", STATUS_DISCONNECTED);
                return result;
            }

            CrmWecomAiBotConnection newConn = new CrmWecomAiBotConnection();
            newConn.setTenant(tenant);
            newConn.setStatus(STATUS_DISCONNECTING);
            newConn.setUpdatedAt(java.time.LocalDateTime.now());

            crmWecomAiBotConnectionMapper.upsert(newConn);

            Map<String, Object> result = new HashMap<>();
            result.put("status", STATUS_DISCONNECTING);
            return result;
        }
        finally
        {
            DynamicDataSourceContextHolder.clearDataSourceKey();
        }
    }

    @Override
    public boolean trySetConnecting(String tenant, String botId)
    {
        try
        {
            DynamicDataSourceContextHolder.setDataSourceKey(tenant);
            CrmWecomAiBotConnection row = new CrmWecomAiBotConnection();
            row.setTenant(tenant);
            row.setBotId(botId);
            row.setStatus(STATUS_CONNECTING);
            row.setUpdatedAt(java.time.LocalDateTime.now());
            int affected = crmWecomAiBotConnectionMapper.upsertConnecting(row);
            return affected > 0;
        }
        finally
        {
            DynamicDataSourceContextHolder.clearDataSourceKey();
        }
    }

    @Override
    public String getConnectionStatus(String tenant)
    {
        try
        {
            DynamicDataSourceContextHolder.setDataSourceKey(tenant);
            CrmWecomAiBotConnection conn = crmWecomAiBotConnectionMapper.selectFirst();
            return (conn != null) ? conn.getStatus() : null;
        }
        finally
        {
            DynamicDataSourceContextHolder.clearDataSourceKey();
        }
    }

    @Override
    public List<TenantBotCommand> getPendingCommands()
    {
        List<TenantBotCommand> commands = new ArrayList<>();

        MasterTenant filter = new MasterTenant();
        filter.setStatus(TenantStatus.NORMAL.getCode());
        List<MasterTenant> tenants = masterTenantService.selectMasterTenantList(filter);

        for (MasterTenant tenant : tenants)
        {
            String tenantId = tenant.getTenant();
            if (tenantId == null || tenantId.isEmpty())
            {
                continue;
            }

            try
            {
                DynamicDataSourceContextHolder.setDataSourceKey(tenantId);

                CrmWecomAiBotConfig cfg = crmWecomAiBotConfigMapper.selectFirst();
                if (cfg == null || !Boolean.TRUE.equals(cfg.getEnabled())
                        || cfg.getBotId() == null || cfg.getBotSecretEnc() == null)
                {
                    continue;
                }

                CrmWecomAiBotConnection conn = crmWecomAiBotConnectionMapper.selectFirst();
                if (conn == null)
                {
                    continue;
                }

                String status = conn.getStatus();
                if (!STATUS_CONNECTING.equals(status)
                        && !STATUS_DISCONNECTING.equals(status)
                        && !STATUS_RECONNECTING.equals(status))
                {
                    continue;
                }

                String decryptedSecret;
                try
                {
                    decryptedSecret = AESUtil.decrypt(cfg.getBotSecretEnc());
                }
                catch (Exception e)
                {
                    continue;
                }

                String crmToken = generateInternalCrmToken(tenantId);

                TenantBotCommand cmd = new TenantBotCommand();
                cmd.setTenant(tenantId);
                cmd.setBotId(cfg.getBotId());
                cmd.setBotSecret(decryptedSecret);
                cmd.setCrmToken(crmToken);
                cmd.setStatus(status);
                commands.add(cmd);
            }
            catch (Exception e)
            {
                // skip this tenant on error
            }
            finally
            {
                DynamicDataSourceContextHolder.clearDataSourceKey();
            }
        }

        return commands;
    }

    @Override
    public ResolveUserResult resolveUser(String userid, String tenant)
    {
        if (userid == null || userid.trim().isEmpty())
        {
            return null;
        }

        String trimmed = userid.trim();

        // 指定了租户：仅在该租户数据源下查找，不跨租户扫描，杜绝身份串号
        if (tenant != null && !tenant.isEmpty())
        {
            log.info("[resolveUser] resolving wecom userid={} within tenant={}", trimmed, tenant);
            try
            {
                // /resolve-user 被 TenantInterceptor 排除，需手动注册租户数据源
                boolean dsExists = dynamicRoutingDataSource.existDataSource(tenant);
                log.info("[resolveUser] tenant={} datasource exists={}, currentThreadDsKey={}",
                        tenant, dsExists, DynamicDataSourceContextHolder.getDataSourceKey());
                if (!dsExists)
                {
                    MasterTenant mt = masterTenantService.selectMasterTenant(tenant);
                    if (mt == null)
                    {
                        log.warn("[resolveUser] tenant={} not found in master_tenant table", tenant);
                        return null;
                    }
                    log.info("[resolveUser] registering datasource for tenant={} url={}", tenant, mt.getUrl());
                    Map<String, Object> dsProps = new HashMap<>();
                    dsProps.put("driverClassName", driverClassName);
                    dsProps.put("url", mt.getUrl());
                    dsProps.put("username", mt.getUsername());
                    dsProps.put("password", mt.getPassword());
                    dynamicRoutingDataSource.addDataSource(tenant, dsProps);
                }
                DynamicDataSourceContextHolder.setDataSourceKey(tenant);
                log.info("[resolveUser] switched datasource to tenant={} threadDsKey={}",
                        tenant, DynamicDataSourceContextHolder.getDataSourceKey());
                SysUser sysUser = sysUserMapper.selectResolveUserResultBySnUserid(trimmed);
                if (sysUser != null)
                {
                    log.info("[resolveUser] resolved wecomUser={} → userId={} userName={} nickName={}",
                            trimmed, sysUser.getUserId(), sysUser.getUserName(), sysUser.getNickName());
                    return new ResolveUserResult(
                            sysUser.getUserId(),
                            sysUser.getUserName(),
                            sysUser.getNickName(),
                            sysUser.getDeptId(),
                            sysUser.getAvatar());
                }
                log.warn("[resolveUser] no match for wecomUser={} in tenant={}", trimmed, tenant);
            }
            catch (Exception e)
            {
                log.error("[resolveUser] error resolving wecomUser={} in tenant={}: {}",
                        trimmed, tenant, e.getMessage(), e);
            }
            finally
            {
                DynamicDataSourceContextHolder.clearDataSourceKey();
            }
            return null;
        }

        // 未指定租户：兼容旧调用方，扫描所有正常租户
        log.warn("[resolveUser] no tenant specified, scanning all tenants for wecomUser={}", trimmed);
        MasterTenant filter = new MasterTenant();
        filter.setStatus(TenantStatus.NORMAL.getCode());
        List<MasterTenant> tenants = masterTenantService.selectMasterTenantList(filter);

        for (MasterTenant t : tenants)
        {
            String tenantId = t.getTenant();
            if (tenantId == null || tenantId.isEmpty())
            {
                continue;
            }

            try
            {
                DynamicDataSourceContextHolder.setDataSourceKey(tenantId);

                SysUser sysUser = sysUserMapper.selectResolveUserResultBySnUserid(trimmed);
                if (sysUser != null)
                {
                    log.warn("[resolveUser] LEGACY-FALLBACK: resolved wecomUser={} in tenant={} → userId={} userName={}",
                            trimmed, tenantId, sysUser.getUserId(), sysUser.getUserName());
                    return new ResolveUserResult(
                            sysUser.getUserId(),
                            sysUser.getUserName(),
                            sysUser.getNickName(),
                            sysUser.getDeptId(),
                            sysUser.getAvatar());
                }
            }
            catch (Exception e)
            {
                log.error("[resolveUser] LEGACY-FALLBACK: error in tenant={}: {}", tenantId, e.getMessage(), e);
            }
            finally
            {
                DynamicDataSourceContextHolder.clearDataSourceKey();
            }
        }

        log.warn("[resolveUser] LEGACY-FALLBACK: no match for wecomUser={} across all tenants", trimmed);
        return null;
    }

    @Override
    public void reportStatus(WecomAiBotStatusReportRequest body)
    {
        CrmWecomAiBotConnection update = new CrmWecomAiBotConnection();
        update.setTenant(body.getTenant());
        update.setStatus(body.getStatus());
        update.setLastMessageAt(body.getLastMessageAt());
        update.setErrorMessage(body.getErrorMessage());
        update.setErrorCount(body.getErrorCount());

        try
        {
            DynamicDataSourceContextHolder.setDataSourceKey(body.getTenant());
            crmWecomAiBotConnectionMapper.updateStatus(update);
        }
        finally
        {
            DynamicDataSourceContextHolder.clearDataSourceKey();
        }
    }

    private String generateInternalCrmToken(String tenant)
    {
        // 查找有权限访问 CRM 数据的真实用户作为"门禁通行证"
        // 该用户的 JWT 仅用于通过 JWT 认证过滤器，实际数据权限由 WecomAiBotActingFilter
        // 根据 X-Acting-User-Id 头切换到真实用户
        SysUser agentUser = findAgentUserForTenant();

        if (agentUser != null)
        {
            LoginUser loginUser = new LoginUser(tenant, agentUser.getUserId(), agentUser.getDeptId(),
                    agentUser, new HashSet<>());
            loginUser.setPermissions(permissionService.getMenuPermission(agentUser));
            return tokenService.createToken(loginUser);
        }

        // 兜底：该 tenant 无任何有效用户，使用虚拟用户
        SysUser botUser = new SysUser();
        botUser.setUserName("crm_aibot_fallback");
        botUser.setUserId(0L);
        botUser.setDeptId(0L);

        LoginUser loginUser = new LoginUser(tenant, 0L, 0L, botUser, new HashSet<>());
        return tokenService.createToken(loginUser);
    }

    /**
     * 在当前数据源上下文中查找代理用户（门禁通行证）
     * 策略：admin 用户 → 第一个非删除用户 → 无
     */
    private SysUser findAgentUserForTenant()
    {
        // 1. 查找该 tenant 下 user_name = 'admin' 的超管用户
        SysUser adminUser = sysUserMapper.selectUserByUserName("admin");
        if (adminUser != null)
        {
            return adminUser;
        }

        // 2. 兜底：取该 tenant 下第一个非删除状态的用户
        SysUser filter = new SysUser();
        List<SysUser> users = sysUserMapper.selectUserList(filter);
        if (users != null && !users.isEmpty())
        {
            return users.get(0);
        }

        return null;
    }

    static Map<String, Object> buildConfigMap(CrmWecomAiBotConfig cfg)
    {
        Map<String, Object> m = new HashMap<>();
        if (cfg == null)
        {
            m.put("botId", null);
            m.put("secretConfigured", false);
            m.put("enabled", false);
            m.put("updatedAt", null);
            return m;
        }
        boolean secretConfigured = cfg.getBotSecretEnc() != null && !cfg.getBotSecretEnc().isEmpty();
        m.put("botId", cfg.getBotId());
        m.put("secretConfigured", secretConfigured);
        m.put("enabled", Boolean.TRUE.equals(cfg.getEnabled()));
        if (cfg.getUpdatedAt() != null)
        {
            m.put("updatedAt", DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, java.sql.Timestamp.valueOf(cfg.getUpdatedAt())));
        }
        else
        {
            m.put("updatedAt", null);
        }
        return m;
    }

    static Map<String, Object> buildConnectionMap(CrmWecomAiBotConnection conn)
    {
        Map<String, Object> m = new HashMap<>();
        if (conn == null)
        {
            m.put("status", STATUS_DISCONNECTED);
            m.put("lastMessageAt", null);
            m.put("errorMessage", null);
            m.put("errorCount", 0);
            m.put("updatedAt", null);
            return m;
        }
        m.put("status", conn.getStatus());
        m.put("lastMessageAt", conn.getLastMessageAt());
        m.put("errorMessage", conn.getErrorMessage());
        m.put("errorCount", conn.getErrorCount() != null ? conn.getErrorCount() : 0);
        m.put("updatedAt", conn.getUpdatedAt());
        return m;
    }
}
