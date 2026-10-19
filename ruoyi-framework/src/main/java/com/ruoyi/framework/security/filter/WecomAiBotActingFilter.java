package com.ruoyi.framework.security.filter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.framework.datasource.DynamicDataSourceContextHolder;
import com.ruoyi.framework.web.service.SysPermissionService;
import com.ruoyi.system.service.ISysUserService;

/**
 * WeCom AI Bot 身份模拟过滤器
 *
 * 处理 bot-server 发起的「以真实用户身份执行」的 CRM API 请求。
 * 读取 X-Acting-User-Id 请求头，校验 X-Internal-Secret 确认调用方身份后，
 * 将 SecurityContext 从 bot 系统用户（crm_aibot）切换为真实 CRM 用户。
 * 请求完成后清理 SecurityContext，防止线程池复用导致身份泄漏。
 *
 * 安全约束：
 * 1. 必须携带有效的 X-Internal-Secret（与 /pending、/status 共享同一密钥）
 * 2. X-Acting-User-Id 对应的用户必须存在且未被删除
 * 3. 禁止模拟管理员角色（roleKey=admin）的用户
 */
@Component
public class WecomAiBotActingFilter extends OncePerRequestFilter
{
    @Value("${internal.service.secret}")
    private String internalSecret;

    @Autowired
    private ISysUserService sysUserService;

    @Autowired
    private SysPermissionService permissionService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException
    {
        // ── 1. 检查是否需要处理 ──
        String actingUserIdStr = request.getHeader("X-Acting-User-Id");
        if (actingUserIdStr == null || actingUserIdStr.trim().isEmpty())
        {
            filterChain.doFilter(request, response);
            return;
        }

        // ── 2. 校验 X-Internal-Secret（确认调用方是 bot-server）──
        String secret = request.getHeader("X-Internal-Secret");
        if (secret == null || !internalSecret.equals(secret))
        {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json;charset=UTF-8");
            PrintWriter writer = response.getWriter();
            writer.write("{\"code\":403,\"msg\":\"Forbidden: invalid X-Internal-Secret for acting user\"}");
            writer.flush();
            return;
        }

        // ── 3. 解析 userId ──
        Long actingUserId;
        try
        {
            actingUserId = Long.parseLong(actingUserIdStr.trim());
        }
        catch (NumberFormatException e)
        {
            logger.warn("WecomAiBotActingFilter: invalid X-Acting-User-Id format: " + actingUserIdStr);
            filterChain.doFilter(request, response);
            return;
        }

        // ── 4. 从当前 SecurityContext 中已认证的 bot LoginUser 获取 tenant ──
        Authentication existingAuth = SecurityContextHolder.getContext().getAuthentication();
        if (existingAuth == null || !(existingAuth.getPrincipal() instanceof LoginUser))
        {
            logger.warn("WecomAiBotActingFilter: no authenticated bot user in SecurityContext,"
                    + " crmToken may be expired. Rejecting acting-user request.");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            PrintWriter writer = response.getWriter();
            writer.write("{\"code\":401,\"msg\":\"Bot authentication expired, please reconnect\"}");
            writer.flush();
            return;
        }
        LoginUser botLoginUser = (LoginUser) existingAuth.getPrincipal();
        String tenant = botLoginUser.getTenant();

        // ── 5. 切换租户数据源后加载真实用户（TenantInterceptor 在 Filter 之后执行，需手动设置）──
        SysUser realUser;
        Set<String> perms;
        if (tenant != null)
        {
            DynamicDataSourceContextHolder.setDataSourceKey(tenant);
        }
        try
        {
            realUser = sysUserService.selectUserById(actingUserId);
            if (realUser == null || "2".equals(realUser.getDelFlag()))
            {
                logger.warn("WecomAiBotActingFilter: acting user not found or deleted, userId=" + actingUserId
                        + " tenant=" + tenant);
                filterChain.doFilter(request, response);
                return;
            }

            // ── 6. 角色限制：禁止模拟管理员 ──
            if (realUser.isAdmin())
            {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.setContentType("application/json;charset=UTF-8");
                PrintWriter writer = response.getWriter();
                writer.write("{\"code\":403,\"msg\":\"Acting as admin role is not allowed\"}");
                writer.flush();
                return;
            }

            // 加载真实用户的菜单权限，确保 @PreAuthorize、@DataScope 生效（需要在同一数据源下）
            perms = permissionService.getMenuPermission(realUser);
        }
        finally
        {
            // 清除临时数据源，TenantInterceptor.preHandle 会重新设置正确的租户数据源
            if (tenant != null)
            {
                DynamicDataSourceContextHolder.clearDataSourceKey();
            }
        }

        // ── 7. 构建 LoginUser 并设置 SecurityContext ──
        LoginUser actingLoginUser = new LoginUser(
                tenant, realUser.getUserId(), realUser.getDeptId(),
                realUser, perms);

        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(
                        actingLoginUser, null, actingLoginUser.getAuthorities());

        // 保存原始认证，请求完成后恢复（防止方法级安全异常导致 /error 分发时上下文为空）
        Authentication originalAuth = SecurityContextHolder.getContext().getAuthentication();
        SecurityContextHolder.getContext().setAuthentication(authToken);

        // ── 8. 审计日志 ──
        String actingUserName = request.getHeader("X-Acting-User-Name");
        logger.info(String.format(
                "WecomAiBotActingFilter: acting as userId=%d name=%s tenant=%s",
                actingUserId, actingUserName != null ? actingUserName : "unknown", tenant));

        // ── 9. 放行请求；执行完毕后恢复原始 SecurityContext ──
        try
        {
            filterChain.doFilter(request, response);
        }
        finally
        {
            if (originalAuth != null)
            {
                SecurityContextHolder.getContext().setAuthentication(originalAuth);
            }
            else
            {
                SecurityContextHolder.clearContext();
            }
        }
    }
}
