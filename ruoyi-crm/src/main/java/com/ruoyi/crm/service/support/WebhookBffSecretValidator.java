package com.ruoyi.crm.service.support;

import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;

/**
 * 校验 Next.js BFF 调用 resolve 时的共享密钥
 */
@Component
public class WebhookBffSecretValidator
{
    public static final String HEADER_NAME = "X-Webhook-BFF-Secret";

    @Value("${webhook.bff-secret:}")
    private String configuredSecret;

    public void validate(HttpServletRequest request)
    {
        if (StringUtils.isBlank(configuredSecret))
        {
            throw new ServiceException("Webhook BFF secret is not configured", HttpStatus.ERROR);
        }
        String provided = request != null ? request.getHeader(HEADER_NAME) : null;
        if (!configuredSecret.equals(provided))
        {
            throw new ServiceException("Unauthorized", HttpStatus.UNAUTHORIZED);
        }
    }
}
