package com.ruoyi.framework.config;

import java.util.concurrent.TimeUnit;

import com.ruoyi.framework.interceptor.TenantInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.constant.Constants;
import com.ruoyi.framework.interceptor.RepeatSubmitInterceptor;

/**
 * 通用配置
 * 
 * @author ruoyi
 */
@Configuration
public class ResourcesConfig implements WebMvcConfigurer
{
    @Autowired
    private RepeatSubmitInterceptor repeatSubmitInterceptor;

    @Autowired
    private TenantInterceptor tenantInterceptor;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry)
    {
        /** 本地文件上传路径 */
        registry.addResourceHandler(Constants.RESOURCE_PREFIX + "/**")
                .addResourceLocations("file:" + RuoYiConfig.getProfile() + "/");

        /** swagger配置 */
        registry.addResourceHandler("/swagger-ui/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/springfox-swagger-ui/")
                .setCacheControl(CacheControl.maxAge(5, TimeUnit.HOURS).cachePublic());;
    }

    /**
     * 自定义拦截规则
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry)
    {
        registry.addInterceptor(repeatSubmitInterceptor).addPathPatterns("/**");
        registry.addInterceptor(tenantInterceptor).addPathPatterns("/**")
                .excludePathPatterns("/error")
                .excludePathPatterns("/login")
                .excludePathPatterns("/login/wecom")
                .excludePathPatterns("/captchaImage")
                .excludePathPatterns("/captchaDigit")
                .excludePathPatterns("/register")
                .excludePathPatterns("/check-tenant-alias")
                .excludePathPatterns("/api/public/tenant-sites/**")
                .excludePathPatterns("/websitelead")
                .excludePathPatterns("/website/pageview")
                .excludePathPatterns("/public/captures/**")
                .excludePathPatterns("/outreachlead")
                .excludePathPatterns("/outreachdata")
                .excludePathPatterns("/outreachrespondent")
                .excludePathPatterns("/outreachsurvey**")
                .excludePathPatterns("/outreacheventregistration**")
                .excludePathPatterns("/outreacheventticket**")
                .excludePathPatterns("/outreacheventcheckin**")
                .excludePathPatterns("/outreach/qualify-survey", "/outreach/qualify-survey**")
                .excludePathPatterns("/outreach/wechat/oauth/**")
                .excludePathPatterns("/track**")
                .excludePathPatterns("/webhook/**")
                .excludePathPatterns("/webhook/start/**")
                .excludePathPatterns("/waitresume/**")
                .excludePathPatterns("/correspreview")
                .excludePathPatterns("/changeLanguage")
                .excludePathPatterns("/profile/**")
                .excludePathPatterns("/common/download**")
                .excludePathPatterns("/common/download/resource**")
                .excludePathPatterns("/common/download/resource/upload/**")
                .excludePathPatterns("/crm/wecom/callback/**", "/wecom/callback/**")
                .excludePathPatterns("/crm/wechat_mp/callback/**", "/wechat_mp/callback/**",
                        "/crm/wechat_mp/token/**", "/wechat_mp/token/**")
                .excludePathPatterns("/crm/whatsapp/webhook", "/whatsapp/webhook")
                .excludePathPatterns("/crm/messenger/webhook", "/messenger/webhook")
                .excludePathPatterns("/crm/webchat/public/**")
                .excludePathPatterns("/crm/public/reminder/links/**")
                .excludePathPatterns("/crm/sf-express/callback/**")
                // WEBHOOK_TRIGGER BFF resolve: tenant 来自加密 path 解密，非 header
                .excludePathPatterns("/crm/webhook-trigger/resolve")
                .excludePathPatterns("/crm/record-trigger/match")
                .excludePathPatterns("/crm/event-trigger/match")
                .excludePathPatterns("/crm/wscheduledtask/claim-due", "/crm/wscheduledtask/claim-due/**")
                // Next 旧回访 cron 仍可能 POST 此路径；调度已改为进程内 FollowupRuleScheduler
                .excludePathPatterns("/crm/followuprule/enrollment/claim-due")
                .excludePathPatterns("/crm/wwaitresume/key/**")
                .excludePathPatterns("/flowable/**")
                // 入站 MCP：tenant 由 key_prefix 解析或 body.tenant 绑定，非 header
                .excludePathPatterns("/crm/mcp/api-key/validate", "/crm/mcp/api-key/audit")
                .excludePathPatterns("/crm/wecom/aibot/pending", "/crm/wecom/aibot/status", "/crm/wecom/aibot/resolve-user")
                .excludePathPatterns("/crm/wecom/group-reminder/pending", "/crm/wecom/group-reminder/report")
                .excludePathPatterns("/crm/inbox-agent/**");
    }

    /**
     * 跨域配置
     */
    @Bean
    public CorsFilter corsFilter()
    {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        // 设置访问源地址
        config.addAllowedOriginPattern("*");
        // 设置访问源请求头
        config.addAllowedHeader("*");
        // 设置访问源请求方法
        config.addAllowedMethod("*");
        // 有效期 1800秒
        config.setMaxAge(1800L);
        // 添加映射路径，拦截一切请求
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        // 返回新的CorsFilter
        return new CorsFilter(source);
    }
}