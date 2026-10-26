package com.ruoyi.framework.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.filter.CorsFilter;
import com.ruoyi.framework.config.properties.PermitAllUrlProperties;
import com.ruoyi.framework.security.filter.InternalSecretFilter;
import com.ruoyi.framework.security.filter.JwtAuthenticationTokenFilter;
import com.ruoyi.framework.security.filter.MiniappBffInternalAuthFilter;
import com.ruoyi.framework.security.filter.WecomAiBotActingFilter;
import com.ruoyi.framework.security.filter.WorkflowIntranetAuthenticationFilter;
import com.ruoyi.framework.security.handle.AuthenticationEntryPointImpl;
import com.ruoyi.framework.security.handle.LogoutSuccessHandlerImpl;

/**
 * spring security配置
 * 
 * @author ruoyi
 */
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter
{
    /**
     * 自定义用户认证逻辑
     */
    @Autowired
    private UserDetailsService userDetailsService;
    
    /**
     * 认证失败处理类
     */
    @Autowired
    private AuthenticationEntryPointImpl unauthorizedHandler;

    /**
     * 退出处理类
     */
    @Autowired
    private LogoutSuccessHandlerImpl logoutSuccessHandler;

    /**
     * token认证过滤器
     */
    @Autowired
    private JwtAuthenticationTokenFilter authenticationTokenFilter;

    /**
     * 可信内网工作流免 Bearer 身份过滤器
     */
    @Autowired
    private WorkflowIntranetAuthenticationFilter workflowIntranetAuthenticationFilter;
    
    /**
     * 内部服务认证过滤器
     */
    @Autowired
    private InternalSecretFilter internalSecretFilter;

    /**
     * AI Bot 身份模拟过滤器
     */
    @Autowired
    private WecomAiBotActingFilter wecomAiBotActingFilter;

    /**
     * 微信小程序 BFF：X-Internal-Secret → 最小权限 LoginUser
     */
    @Autowired
    private MiniappBffInternalAuthFilter miniappBffInternalAuthFilter;

    /**
     * 跨域过滤器
     */
    @Autowired
    private CorsFilter corsFilter;

    /**
     * 允许匿名访问的地址
     */
    @Autowired
    private PermitAllUrlProperties permitAllUrl;

    /**
     * 解决 无法直接注入 AuthenticationManager
     *
     * @return
     * @throws Exception
     */
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception
    {
        return super.authenticationManagerBean();
    }

    /**
     * anyRequest          |   匹配所有请求路径
     * access              |   SpringEl表达式结果为true时可以访问
     * anonymous           |   匿名可以访问
     * denyAll             |   用户不能访问
     * fullyAuthenticated  |   用户完全认证可以访问（非remember-me下自动登录）
     * hasAnyAuthority     |   如果有参数，参数表示权限，则其中任何一个权限可以访问
     * hasAnyRole          |   如果有参数，参数表示角色，则其中任何一个角色可以访问
     * hasAuthority        |   如果有参数，参数表示权限，则其权限可以访问
     * hasIpAddress        |   如果有参数，参数表示IP地址，如果用户IP和参数匹配，则可以访问
     * hasRole             |   如果有参数，参数表示角色，则其角色可以访问
     * permitAll           |   用户可以任意访问
     * rememberMe          |   允许通过remember-me登录的用户访问
     * authenticated       |   用户登录后可访问
     */
    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception
    {
        // 注解标记允许匿名访问的url
        ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry registry = httpSecurity.authorizeRequests();
        permitAllUrl.getUrls().forEach(url -> registry.antMatchers(url).permitAll());

        httpSecurity
                // CSRF禁用，因为不使用session
                .csrf().disable()
                // 禁用HTTP响应标头
                .headers().cacheControl().disable().and()
                // 认证失败处理类
                .exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()
                // 基于token，所以不需要session
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                // 过滤请求
                .authorizeRequests()
                // 对于登录login 注册register 验证码captchaImage 允许匿名访问
                .antMatchers("/login", "/login/wecom", "/register", "/check-tenant-alias", "/captchaImage", "/captchaDigit", "/outreachlead", "/outreachdata","/correspreview","/track", "/outreachrespondent","/outreachsurvey", "/outreacheventregistration", "/outreacheventticket", "/outreacheventcheckin", "/outreach/qualify-survey", "/outreach/wechat/oauth/**", "/webhook").permitAll()
                .antMatchers("/api/public/tenant-sites/**").permitAll()
                .antMatchers("/websitelead**").permitAll()
                .antMatchers("/website/pageview**").permitAll()
                .antMatchers("/public/captures/**").permitAll()
                // 静态资源，可匿名访问
                .antMatchers(HttpMethod.GET,
                        "/",
                        "/*.html",
                        "/**/*.html",
                        "/**/*.css",
                        "/**/*.js",
                        "/profile/**"
                ).permitAll()
                .antMatchers("/webhook/**").permitAll()  // ✅ Webhook 接口放行
                .antMatchers("/webhook/start/**").permitAll()  // ✅ Webhook 接口放行
                .antMatchers("/waitresume/**").permitAll()  // Wait 节点公开 webhook
                .antMatchers("/crm/wwaitresume/key/**").permitAll()  // Wait resumeKey 查询/恢复（须 tenant header）
                .antMatchers("/outreachlead**").permitAll()
                .antMatchers("/outreachdata**").permitAll()
                .antMatchers("/outreachsurvey**").permitAll()
                .antMatchers("/outreacheventregistration**").permitAll()
                .antMatchers("/outreacheventticket**").permitAll()
                .antMatchers("/outreacheventcheckin**").permitAll()
                .antMatchers("/outreach/qualify-survey", "/outreach/qualify-survey**").permitAll()
                .antMatchers("/outreach/wechat/oauth/**").permitAll()
                .antMatchers("/outreachrespondent**").permitAll()
                .antMatchers("/track**").permitAll()
                .antMatchers("/correspreview**").permitAll()
                .antMatchers("/common/download**").permitAll()
                .antMatchers("/common/download/resource**").permitAll()
                .antMatchers("/common/download/resource/upload/**").permitAll()
                .antMatchers("/flowable/**").permitAll()
                .antMatchers("/crm/wecom/callback/**", "/wecom/callback/**").permitAll()
                .antMatchers("/crm/wechat_mp/callback/**", "/wechat_mp/callback/**", "/crm/wechat_mp/token/**", "/wechat_mp/token/**").permitAll()
                .antMatchers("/crm/whatsapp/webhook", "/whatsapp/webhook").permitAll()
                .antMatchers("/crm/messenger/webhook", "/messenger/webhook").permitAll()
                .antMatchers("/crm/webchat/public/**").permitAll()
                .antMatchers("/crm/public/reminder/links/**").permitAll()
                .antMatchers("/crm/sf-express/callback/**").permitAll()
                .antMatchers("/crm/webhook-trigger/resolve").permitAll()
                .antMatchers("/crm/record-trigger/match").permitAll()
                .antMatchers("/crm/event-trigger/match").permitAll()
                .antMatchers("/crm/inbox-agent/**").permitAll()
                .antMatchers("/crm/wscheduledtask/claim-due").permitAll()
                .antMatchers("/crm/mcp/api-key/validate", "/crm/mcp/api-key/audit").permitAll()
                .antMatchers("/crm/wecom/aibot/pending", "/crm/wecom/aibot/status", "/crm/wecom/aibot/resolve-user").permitAll()
                .antMatchers("/crm/wecom/group-reminder/pending", "/crm/wecom/group-reminder/report").permitAll()
                .antMatchers("/changeLanguage").permitAll()
                .antMatchers("/swagger-ui.html", "/swagger-resources/**", "/webjars/**", "/*/api-docs", "/druid/**").permitAll()
                // 除上面外的所有请求全部需要鉴权认证
                .anyRequest().authenticated()
                .and()
                .headers().frameOptions().disable();
        // 添加Logout filter
        httpSecurity.logout().logoutUrl("/logout").logoutSuccessHandler(logoutSuccessHandler);
        // 添加JWT filter
        httpSecurity.addFilterBefore(authenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);
        // 可信内网工作流请求先注入服务身份；带 Bearer 的请求仍走正常 JWT
        httpSecurity.addFilterBefore(workflowIntranetAuthenticationFilter, JwtAuthenticationTokenFilter.class);
        // 小程序 BFF：白名单路径无 Bearer 时用 X-Internal-Secret 注入最小权限主体
        httpSecurity.addFilterBefore(miniappBffInternalAuthFilter, JwtAuthenticationTokenFilter.class);
        // 添加AI Bot身份模拟过滤器（在JWT filter之后执行，将bot用户替换为真实用户）
        httpSecurity.addFilterAfter(wecomAiBotActingFilter, JwtAuthenticationTokenFilter.class);
        // 添加内部服务认证过滤器（在JWT filter之前执行）
        httpSecurity.addFilterBefore(internalSecretFilter, JwtAuthenticationTokenFilter.class);
        // 添加CORS filter
        httpSecurity.addFilterBefore(corsFilter, JwtAuthenticationTokenFilter.class);
        httpSecurity.addFilterBefore(corsFilter, LogoutFilter.class);
    }

    /**
     * 强散列哈希加密实现
     */
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder()
    {
        return new BCryptPasswordEncoder();
    }

    /**
     * 身份认证接口
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception
    {
        auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder());
    }
}
