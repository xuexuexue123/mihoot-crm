package com.ruoyi.crm.service.support;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.ruoyi.common.constant.CacheConstants;
import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.enums.UserStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.sign.AESUtil;
import com.ruoyi.crm.domain.CrmMcpApiKey;
import com.ruoyi.framework.web.service.SysPermissionService;
import com.ruoyi.framework.web.service.TokenService;
import com.ruoyi.system.service.ISysUserService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

/**
 * 为 MCP API Key 绑定的服务账号签发 / 刷新 CRM JWT
 */
@Component
public class McpCrmAuthService
{
    @Autowired
    private ISysUserService sysUserService;

    @Autowired
    private SysPermissionService permissionService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private RedisCache redisCache;

    @Value("${token.secret}")
    private String tokenSecret;

    private static final long MILLIS_MINUTE_TEN = 20 * 60 * 1000L;

    /**
     * 签发新 JWT 并加密写入行（create / rotate / 刷新缓存）
     */
    public String issueAndEncryptToken(CrmMcpApiKey row) throws Exception
    {
        String token = issueToken(row.getBoundUserId(), row.getTenantId());
        row.setServiceTokenEnc(AESUtil.encrypt(token));
        return token;
    }

    /**
     * validate：优先解密缓存；失败或不可用时重新签发并更新 DB
     */
    public String resolveServiceToken(CrmMcpApiKey row, CrmMcpApiKeyMapperUpdater updater) throws Exception
    {
        if (StringUtils.isNotBlank(row.getServiceTokenEnc()))
        {
            try
            {
                String cached = AESUtil.decrypt(row.getServiceTokenEnc());
                if (StringUtils.isNotBlank(cached) && isTokenUsable(cached))
                {
                    return cached;
                }
            }
            catch (Exception ignored)
            {
                // fall through to refresh
            }
        }
        String token = issueAndEncryptToken(row);
        if (updater != null)
        {
            updater.persistServiceTokenEnc(row);
        }
        return token;
    }

    private boolean isTokenUsable(String jwt)
    {
        try
        {
            Claims claims = Jwts.parser().setSigningKey(tokenSecret).parseClaimsJws(jwt).getBody();
            String uuid = (String) claims.get(Constants.LOGIN_USER_KEY);
            if (StringUtils.isBlank(uuid))
            {
                return false;
            }
            LoginUser loginUser = redisCache.getCacheObject(CacheConstants.LOGIN_TOKEN_KEY + uuid);
            if (loginUser == null)
            {
                return false;
            }
            return loginUser.getExpireTime() - System.currentTimeMillis() > MILLIS_MINUTE_TEN;
        }
        catch (Exception ex)
        {
            return false;
        }
    }

    public String issueToken(String boundUserId, String tenantId)
    {
        SysUser user = resolveBoundUser(boundUserId);
        if (user == null)
        {
            throw new ServiceException("Service account not available", HttpStatus.ERROR);
        }
        if (UserStatus.DELETED.getCode().equals(user.getDelFlag())
                || UserStatus.DISABLE.getCode().equals(user.getStatus()))
        {
            throw new ServiceException("Service account not available", HttpStatus.ERROR);
        }
        LoginUser loginUser = new LoginUser(user.getUserId(), user.getDeptId(), user,
                permissionService.getMenuPermission(user));
        loginUser.setTenant(tenantId);
        return tokenService.createToken(loginUser);
    }

    private SysUser resolveBoundUser(String boundUserId)
    {
        if (StringUtils.isBlank(boundUserId))
        {
            return null;
        }
        String trimmed = boundUserId.trim();
        try
        {
            Long uid = Long.parseLong(trimmed);
            return sysUserService.selectUserById(uid);
        }
        catch (NumberFormatException e)
        {
            return sysUserService.selectUserByUserName(trimmed);
        }
    }

    /**
     * 回调：将刷新后的 service_token_enc 写回数据库
     */
    @FunctionalInterface
    public interface CrmMcpApiKeyMapperUpdater
    {
        void persistServiceTokenEnc(CrmMcpApiKey row);
    }
}
