package com.ruoyi.crm.support;

import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.domain.CrmEmailMessages;
import com.ruoyi.crm.domain.CrmEmailMessageRequest;
import com.ruoyi.crm.domain.CrmOauthConnections;

/**
 * Enforces mail/oauth data access to the currently logged-in CRM user.
 */
public final class CrmMailUserScopeSupport
{
    private CrmMailUserScopeSupport()
    {
    }

    public static String currentUserId()
    {
        return SecurityUtils.getUsername();
    }

    /**
     * Returns the logged-in user id. Rejects requests that target another user.
     */
    public static String applyCurrentUserId(String requestedUserId)
    {
        String currentUserId = currentUserId();
        if (StringUtils.isNotEmpty(requestedUserId) && !currentUserId.equals(requestedUserId))
        {
            throw new ServiceException("无权访问其他用户的邮件数据", HttpStatus.FORBIDDEN);
        }
        return currentUserId;
    }

    public static void applyCurrentUserId(CrmEmailMessageRequest request)
    {
        if (request == null)
        {
            return;
        }
        request.setUserId(applyCurrentUserId(request.getUserId()));
    }

    public static void applyCurrentUserId(CrmOauthConnections connection)
    {
        if (connection == null)
        {
            return;
        }
        connection.setUserId(applyCurrentUserId(connection.getUserId()));
    }

    public static void assertOwnedByCurrentUser(CrmOauthConnections connection)
    {
        if (connection == null)
        {
            return;
        }
        if (!currentUserId().equals(connection.getUserId()))
        {
            throw new ServiceException("无权访问该邮件账号", HttpStatus.FORBIDDEN);
        }
    }

    public static void assertOwnedByCurrentUser(CrmEmailMessages message)
    {
        if (message == null)
        {
            return;
        }
        if (!currentUserId().equals(message.getUserId()))
        {
            throw new ServiceException("无权访问该邮件", HttpStatus.FORBIDDEN);
        }
    }
}
