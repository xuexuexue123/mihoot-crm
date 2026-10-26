package com.ruoyi.crm.service.support;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.enums.UserStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.domain.vo.WebhookCrmAuthVO;
import com.ruoyi.framework.web.service.SysPermissionService;
import com.ruoyi.framework.web.service.TokenService;
import com.ruoyi.system.service.ISysUserService;

/**
 * 为 Webhook resolve 签发工作流 owner 的 CRM Bearer token
 */
@Component
public class WebhookCrmAuthService
{
    @Autowired
    private ISysUserService sysUserService;

    @Autowired
    private SysPermissionService permissionService;

    @Autowired
    private TokenService tokenService;

    public WebhookCrmAuthVO issueTokenForWorkflowOwner(String userId, String tenantId)
    {
        if (StringUtils.isBlank(userId))
        {
            throw new ServiceException("Webhook workflow owner not available", HttpStatus.ERROR);
        }
        Long uid;
        try
        {
            uid = Long.parseLong(userId.trim());
        }
        catch (NumberFormatException e)
        {
            throw new ServiceException("Webhook workflow owner not available", HttpStatus.ERROR);
        }

        SysUser user = sysUserService.selectUserById(uid);
        if (user == null)
        {
            throw new ServiceException("Webhook workflow owner not available", HttpStatus.ERROR);
        }
        if (UserStatus.DELETED.getCode().equals(user.getDelFlag())
                || UserStatus.DISABLE.getCode().equals(user.getStatus()))
        {
            throw new ServiceException("Webhook workflow owner not available", HttpStatus.ERROR);
        }

        LoginUser loginUser = new LoginUser(user.getUserId(), user.getDeptId(), user,
                permissionService.getMenuPermission(user));
        loginUser.setTenant(tenantId);
        String token = tokenService.createToken(loginUser);
        return new WebhookCrmAuthVO(token, tenantId);
    }
}
