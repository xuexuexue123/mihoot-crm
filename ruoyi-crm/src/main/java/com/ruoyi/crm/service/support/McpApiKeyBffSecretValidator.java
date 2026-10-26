package com.ruoyi.crm.service.support;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;

/**
 * 校验 Next.js BFF / MCP Server 调用 validate、audit 时的共享密钥
 */
@Component
public class McpApiKeyBffSecretValidator
{
    public static final String HEADER_NAME = "X-Mcp-Internal-Secret";

    @Value("${mcp.validate-internal-secret:}")
    private String configuredSecret;

    public void validate(HttpServletRequest request)
    {
        if (StringUtils.isBlank(configuredSecret))
        {
            throw new ServiceException("MCP validate internal secret is not configured", HttpStatus.ERROR);
        }
        String provided = request != null ? request.getHeader(HEADER_NAME) : null;
        if (!configuredSecret.equals(provided))
        {
            throw new ServiceException("Unauthorized", HttpStatus.UNAUTHORIZED);
        }
    }
}
