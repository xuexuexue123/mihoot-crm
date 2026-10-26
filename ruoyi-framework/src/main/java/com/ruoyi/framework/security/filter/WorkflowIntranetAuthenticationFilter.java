package com.ruoyi.framework.security.filter;

import java.io.IOException;
import java.util.Collections;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.framework.security.intranet.IntranetRequestAccess;

/**
 * 为可信内网中的无 Bearer 工作流执行请求注入服务身份。
 *
 * 普通定时任务 CRUD（/crm/wscheduledtask/**）明确排除，仅 claim-due 例外。
 * 带 Authorization 的请求始终交给正常 JWT 链路处理。
 */
@Component
public class WorkflowIntranetAuthenticationFilter extends OncePerRequestFilter
{
    private static final String ALL_PERMISSION = "*:*:*";
    private static final String SCHEDULED_TASK_PREFIX = "/crm/wscheduledtask";
    private static final String CLAIM_DUE_PATH = "/crm/wscheduledtask/claim-due";

    @Autowired
    private IntranetRequestAccess intranetRequestAccess;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException
    {
        if (shouldAuthenticate(request))
        {
            SysUser serviceUser = new SysUser();
            serviceUser.setUserId(0L);
            serviceUser.setUserName("workflow-intranet");

            LoginUser loginUser = new LoginUser(request.getHeader("tenant"), 0L, null,
                    serviceUser, Collections.singleton(ALL_PERMISSION));
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    loginUser, null, loginUser.getAuthorities());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        chain.doFilter(request, response);
    }

    private boolean shouldAuthenticate(HttpServletRequest request)
    {
        if (SecurityContextHolder.getContext().getAuthentication() != null
                || StringUtils.isNotBlank(request.getHeader("Authorization"))
                || !intranetRequestAccess.isTrusted(request))
        {
            return false;
        }
        String path = request.getRequestURI().substring(request.getContextPath().length());
        if (CLAIM_DUE_PATH.equals(path))
        {
            return true;
        }
        return path.startsWith("/crm/") && !path.startsWith(SCHEDULED_TASK_PREFIX);
    }
}
