package com.ruoyi.framework.security.filter;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * 内部服务认证过滤器
 * 对 aibot pending/status/resolve-user 与 group-reminder pending/report 生效，
 * 校验 X-Internal-Secret 请求头，供 aibot 进程等内部服务调用。
 */
@Component
public class InternalSecretFilter extends OncePerRequestFilter
{
    @Value("${internal.service.secret}")
    private String internalSecret;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException
    {
        String path = request.getRequestURI();

        boolean aibotInternal = path.startsWith("/crm/wecom/aibot/")
                && (path.endsWith("/pending")
                    || path.endsWith("/resolve-user")
                    || (path.endsWith("/status") && "POST".equals(request.getMethod())));
        boolean reminderInternal = path.startsWith("/crm/wecom/group-reminder/")
                && (path.endsWith("/pending") || path.endsWith("/report"));

        if (aibotInternal || reminderInternal)
        {
            String secret = request.getHeader("X-Internal-Secret");
            if (secret == null || !internalSecret.equals(secret))
            {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json;charset=UTF-8");
                PrintWriter writer = response.getWriter();
                writer.write("{\"code\":401,\"msg\":\"Unauthorized\"}");
                writer.flush();
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
