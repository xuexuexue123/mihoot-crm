package com.ruoyi.crm.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.ruoyi.crm.interceptor.InboxChannelAccessInterceptor;

/**
 * Registers Inbox channel gate after {@code TenantInterceptor} (see {@code ResourcesConfig}).
 */
@Configuration
@Order
public class InboxChannelWebConfig implements WebMvcConfigurer
{
    @Autowired
    private InboxChannelAccessInterceptor inboxChannelAccessInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry)
    {
        registry.addInterceptor(inboxChannelAccessInterceptor)
                .addPathPatterns("/crm/**", "/whatsapp/**", "/messenger/**", "/webchat/**", "/wechat_mp/**");
    }
}
