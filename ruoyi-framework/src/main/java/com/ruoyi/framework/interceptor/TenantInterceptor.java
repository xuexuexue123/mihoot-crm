package com.ruoyi.framework.interceptor;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import com.alibaba.fastjson2.JSON;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.framework.datasource.DynamicDataSourceContextHolder;
import com.ruoyi.framework.datasource.TenantDataSourceRegistry;
import lombok.extern.slf4j.Slf4j;

/**
 * 数据源拦截器
 *
 * @author devjd
 */

@Component
@Slf4j
public class TenantInterceptor implements HandlerInterceptor {

    @Autowired
    private TenantDataSourceRegistry tenantDataSourceRegistry;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod()))
        {
            return true;
        }
        String tenant = request.getHeader("tenant");
        if (StringUtils.isBlank(tenant))
        {
            tenant = tenantFromLoginUser();
        }
        if (StringUtils.isBlank(tenant))
        {
            log.error("缺少请求头 tenant method={} uri={} query={} remote={}",
                    request.getMethod(), request.getRequestURI(), request.getQueryString(), request.getRemoteAddr());
            writeMissingTenant(response);
            return false;
        }
        tenantDataSourceRegistry.bindTenant(tenant);
        return true;
    }

    private static void writeMissingTenant(HttpServletResponse response) throws IOException
    {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        response.getWriter().print(JSON.toJSONString(AjaxResult.error(HttpStatus.BAD_REQUEST, "缺少请求头 tenant")));
    }

    private static String tenantFromLoginUser()
    {
        try
        {
            LoginUser loginUser = SecurityUtils.getLoginUser();
            if (loginUser == null)
            {
                return null;
            }
            Long userId = loginUser.getUserId();
            // 99999=匿名；0=内网工作流服务身份（tenant 可能为空）
            if (userId != null && (userId.longValue() == 99999L || userId.longValue() == 0L))
            {
                return null;
            }
            return loginUser.getTenant();
        }
        catch (Exception ex)
        {
            return null;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
                                Exception ex) {
        DynamicDataSourceContextHolder.clearDataSourceKey();
    }
}