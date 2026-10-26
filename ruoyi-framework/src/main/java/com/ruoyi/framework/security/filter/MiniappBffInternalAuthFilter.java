package com.ruoyi.framework.security.filter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.utils.StringUtils;

/**
 * 微信小程序 Next BFF → 9090 内部服务鉴权。
 *
 * 对白名单 /crm/* 路径：在无 Bearer 时校验 X-Internal-Secret，并注入最小权限 LoginUser，
 * 满足 authenticated() + @PreAuthorize。带 Authorization 时跳过（双轨：顾问 JWT / 旧服务 JWT）。
 *
 * 不依赖库内 miniapp_bff 登录席位；权限集合与 crm_miniapp_service 角色对齐（硬编码）。
 */
@Component
public class MiniappBffInternalAuthFilter extends OncePerRequestFilter
{
    public static final String HEADER_INTERNAL_SECRET = "X-Internal-Secret";
    public static final String SERVICE_USER_NAME = "miniapp_bff_service";
    /** 哨兵 userId，用于审计 create_by；不要求库内存在对应 sys_user */
    public static final long SERVICE_USER_ID = 9001L;

    private static final Set<String> SERVICE_PERMISSIONS = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
            "crm:marketevent:list", "crm:marketevent:query",
            "crm:industrialpark:list", "crm:industrialpark:query",
            "crm:tenantenterprise:list", "crm:tenantenterprise:query",
            "crm:chineseenterprise:list", "crm:chineseenterprise:query",
            "crm:professionalservice:list", "crm:professionalservice:query",
            "crm:sanctionedentity:list", "crm:sanctionedentity:query",
            "crm:governmentagency:list", "crm:governmentagency:query",
            "crm:wechat_miniapp:runtime",
            "crm:knowledge:retrieve",
            "crm:lead:list", "crm:lead:query", "crm:lead:add", "crm:lead:edit", "crm:lead:miniapp",
            "crm:contact:list", "crm:contact:query"
    )));

    private static final String[] SEVEN_LIB_PREFIXES = {
            "/crm/market-event",
            "/crm/industrial-park",
            "/crm/tenant-enterprise",
            "/crm/chinese-enterprise",
            "/crm/professional-service",
            "/crm/sanctioned-entity",
            "/crm/government-agency"
    };

    @Value("${internal.service.secret}")
    private String internalSecret;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException
    {
        if (shouldSkip(request))
        {
            chain.doFilter(request, response);
            return;
        }

        String path = servletPath(request);
        if (!isAllowlisted(request.getMethod(), path))
        {
            chain.doFilter(request, response);
            return;
        }

        String secret = request.getHeader(HEADER_INTERNAL_SECRET);
        if (secret == null || !internalSecret.equals(secret))
        {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            PrintWriter writer = response.getWriter();
            writer.write("{\"code\":401,\"msg\":\"Unauthorized\"}");
            writer.flush();
            return;
        }

        SysUser serviceUser = new SysUser();
        serviceUser.setUserId(SERVICE_USER_ID);
        serviceUser.setUserName(SERVICE_USER_NAME);
        serviceUser.setNickName("微信小程序BFF");

        LoginUser loginUser = new LoginUser(
                request.getHeader("tenant"), SERVICE_USER_ID, null, serviceUser, SERVICE_PERMISSIONS);
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                loginUser, null, loginUser.getAuthorities());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        logger.debug("MiniappBffInternalAuthFilter: auth=internal-secret path=" + path
                + " tenant=" + request.getHeader("tenant"));

        chain.doFilter(request, response);
    }

    private boolean shouldSkip(HttpServletRequest request)
    {
        if (SecurityContextHolder.getContext().getAuthentication() != null)
        {
            return true;
        }
        return StringUtils.isNotBlank(request.getHeader("Authorization"));
    }

    static String servletPath(HttpServletRequest request)
    {
        String uri = request.getRequestURI();
        String context = request.getContextPath();
        if (context != null && !context.isEmpty() && uri.startsWith(context))
        {
            return uri.substring(context.length());
        }
        return uri;
    }

    static boolean isAllowlisted(String method, String path)
    {
        if (path == null || path.isEmpty())
        {
            return false;
        }
        for (String prefix : SEVEN_LIB_PREFIXES)
        {
            if (path.equals(prefix) || path.startsWith(prefix + "/"))
            {
                return true;
            }
        }
        if ("GET".equalsIgnoreCase(method) && "/crm/wechat_miniapp/config/runtime".equals(path))
        {
            return true;
        }
        if ("GET".equalsIgnoreCase(method) && path.startsWith("/crm/lead/by-mobile"))
        {
            return true;
        }
        if ("POST".equalsIgnoreCase(method) && "/crm/lead/miniapp/upsert".equals(path))
        {
            return true;
        }
        if ("POST".equalsIgnoreCase(method) && "/crm/lead/miniapp/market-event-register".equals(path))
        {
            return true;
        }
        if ("POST".equalsIgnoreCase(method) && "/crm/knowledge/retrieve".equals(path))
        {
            return true;
        }
        if ("GET".equalsIgnoreCase(method))
        {
            if ("/crm/contact/list".equals(path))
            {
                return true;
            }
            // GET /crm/contact/{contactId} — 单段 id，排除子资源
            if (path.startsWith("/crm/contact/") && path.indexOf('/', "/crm/contact/".length()) < 0)
            {
                String id = path.substring("/crm/contact/".length());
                if (!id.isEmpty() && !"list".equals(id) && !"listjson".equals(id)
                        && !"listweekly".equals(id) && !"listbirthday".equals(id)
                        && !"listbyaccountid".equals(id) && !"listtag".equals(id))
                {
                    return true;
                }
            }
        }
        return false;
    }

    /** 供单测断言权限集合 */
    public static Set<String> servicePermissions()
    {
        return SERVICE_PERMISSIONS;
    }
}
