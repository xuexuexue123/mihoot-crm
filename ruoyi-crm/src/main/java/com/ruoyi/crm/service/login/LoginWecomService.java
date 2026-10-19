package com.ruoyi.crm.service.login;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.enums.UserStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.MessageUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.domain.CrmWecomConfig;
import com.ruoyi.crm.domain.WecomUserInfo;
import com.ruoyi.crm.mapper.CrmWecomConfigMapper;
import com.ruoyi.crm.service.support.TenantDataSourceSupport;
import com.ruoyi.crm.wecom.WecomAccessTokenService;
import com.ruoyi.crm.wecom.WecomTencentApiClient;
import com.ruoyi.framework.datasource.TenantDataSourceRegistry;
import com.ruoyi.framework.manager.AsyncManager;
import com.ruoyi.framework.manager.factory.AsyncFactory;
import com.ruoyi.framework.web.service.SysPermissionService;
import com.ruoyi.framework.web.service.TokenService;
import com.ruoyi.system.service.ISysConfigService;
import com.ruoyi.system.service.ISysUserService;

/**
 * 企业微信登录服务
 *
 * @author ruoyi
 */
@Service
public class LoginWecomService
{
    private static final Logger log = LoggerFactory.getLogger(LoginWecomService.class);

    @Autowired
    private WecomAccessTokenService wecomAccessTokenService;

    @Autowired
    private WecomTencentApiClient wecomTencentApiClient;

    @Autowired
    private ISysUserService userService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private SysPermissionService permissionService;

    @Autowired
    private ISysConfigService configService;

    @Autowired
    private CrmWecomConfigMapper wecomConfigMapper;

    @Autowired
    private TenantDataSourceRegistry tenantDataSourceRegistry;

    /**
     * 企业微信扫码登录
     *
     * @param code   企业微信 OAuth 授权码
     * @param state  登录 state（防 CSRF）
     * @param tenant 租户标识
     * @return JWT token 字符串
     */
    public String login(String code, String state, String tenant)
    {
        if (StringUtils.isBlank(tenant))
        {
            throw new ServiceException(MessageUtils.message("tenant.null"));
        }

        try
        {
            tenantDataSourceRegistry.bindTenant(tenant);
        }
        catch (Exception e)
        {
            log.error("租户数据源绑定失败: tenant={}", tenant, e);
            throw new ServiceException("租户信息无效");
        }

        try
        {
            // 1. 获取企业微信配置
            CrmWecomConfig cfg = wecomConfigMapper.selectFirst();
            if (cfg == null)
            {
                log.error("未找到企业微信配置: tenant={}", tenant);
                throw new ServiceException("企业微信未配置，请联系管理员");
            }

            // 2. 获取 access_token
            String accessToken;
            try
            {
                accessToken = wecomAccessTokenService.getAgentAccessToken(cfg);
            }
            catch (Exception e)
            {
                log.error("获取 access_token 失败: corpId={}", cfg.getCorpId(), e);
                throw new ServiceException("企业微信服务异常，请稍后重试");
            }

            // 3. 用 code 获取 userId
            JSONObject userInfoResp = wecomTencentApiClient.userGetUserInfo(accessToken, code);
            int errcode = userInfoResp.getIntValue("errcode");
            if (errcode != 0)
            {
                String errmsg = userInfoResp.getString("errmsg");
                log.error("getuserinfo 失败: errcode={} errmsg={}", errcode, errmsg);
                if (errcode == 40029)
                {
                    throw new ServiceException("企业微信授权码无效或已过期");
                }
                throw new ServiceException("企业微信服务异常，请稍后重试");
            }

            String userId = userInfoResp.getString("UserId");
            if (StringUtils.isEmpty(userId))
            {
                String openId = userInfoResp.getString("OpenId");
                if (StringUtils.isEmpty(openId))
                {
                    throw new ServiceException("当前用户不在应用可见范围内，请联系管理员");
                }
                throw new ServiceException("无法获取企业微信用户信息");
            }

            // 4. 查找 CRM 用户
            SysUser user = userService.selectUserBySnUserid(userId);
            if (user == null)
            {
                // 检查是否启用自动创建用户
                String autoCreate = configService.selectConfigByKey("wecom.login.autoCreateUser");
                if ("true".equals(autoCreate))
                {
                    user = autoCreateUser(accessToken, userId, tenant);
                }
                else
                {
                    AsyncManager.me().execute(AsyncFactory.recordLogininfor(tenant, userId,
                            Constants.LOGIN_FAIL, "企业微信用户未关联CRM账号"));
                    throw new ServiceException("企业微信用户未关联CRM账号，请联系管理员");
                }
            }

            // 5. 校验用户状态
            if (UserStatus.DELETED.getCode().equals(user.getDelFlag()))
            {
                AsyncManager.me().execute(AsyncFactory.recordLogininfor(tenant, user.getUserName(),
                        Constants.LOGIN_FAIL, "用户已被删除"));
                throw new ServiceException("您的账号已被删除，请联系管理员");
            }
            if (UserStatus.DISABLE.getCode().equals(user.getStatus()))
            {
                AsyncManager.me().execute(AsyncFactory.recordLogininfor(tenant, user.getUserName(),
                        Constants.LOGIN_FAIL, "用户已被停用"));
                throw new ServiceException("您的账号已被停用，请联系管理员");
            }

            // 6. 构建 LoginUser
            Set<String> permissions = permissionService.getMenuPermission(user);
            LoginUser loginUser = new LoginUser(tenant, user.getUserId(), user.getDeptId(), user, permissions);

            // 7. 记录登录信息
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(tenant, user.getUserName(),
                    Constants.LOGIN_SUCCESS, "企业微信登录成功"));

            // 8. 签发 Token
            return tokenService.createToken(loginUser);
        }
        finally
        {
            TenantDataSourceSupport.restoreTenantKey(tenant);
        }
    }

    /**
     * 自动创建用户
     */
    private SysUser autoCreateUser(String accessToken, String userId, String tenant)
    {
        JSONObject userInfoResp = wecomTencentApiClient.userGet(accessToken, userId);
        int errcode = userInfoResp.getIntValue("errcode");
        if (errcode != 0)
        {
            log.error("user/get 失败: errcode={} errmsg={}", errcode, userInfoResp.getString("errmsg"));
            throw new ServiceException("获取企业微信用户信息失败");
        }

        WecomUserInfo wecomUser = new WecomUserInfo();
        wecomUser.setUserid(userInfoResp.getString("userid"));
        wecomUser.setName(userInfoResp.getString("name"));
        wecomUser.setMobile(userInfoResp.getString("mobile"));
        wecomUser.setEmail(userInfoResp.getString("email"));

        SysUser sysUser = new SysUser();
        sysUser.setSnUserid(wecomUser.getUserid());
        sysUser.setUserName(wecomUser.getUserid());

        String nickName = StringUtils.isNotBlank(wecomUser.getName()) ? wecomUser.getName() : wecomUser.getUserid();
        sysUser.setNickName(nickName);

        sysUser.setPhonenumber(wecomUser.getMobile());
        sysUser.setEmail(wecomUser.getEmail());
        sysUser.setStatus(UserStatus.OK.getCode());
        sysUser.setDelFlag("0");

        int rows = userService.insertUser(sysUser);
        if (rows == 0)
        {
            throw new ServiceException("自动创建用户失败");
        }

        log.info("企业微信用户自动创建成功: wecomUserId={} userId={} tenant={}", wecomUser.getUserid(),
                sysUser.getUserId(), tenant);

        AsyncManager.me().execute(AsyncFactory.recordLogininfor(tenant, sysUser.getUserName(),
                Constants.REGISTER, "企业微信用户自动创建"));

        return sysUser;
    }
}
