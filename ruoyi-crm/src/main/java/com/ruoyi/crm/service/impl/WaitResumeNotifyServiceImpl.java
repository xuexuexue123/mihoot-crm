package com.ruoyi.crm.service.impl;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.service.IWaitResumeNotifyService;

@Service
public class WaitResumeNotifyServiceImpl implements IWaitResumeNotifyService
{
    private static final Logger log = LoggerFactory.getLogger(WaitResumeNotifyServiceImpl.class);

    private static final String NOTIFY_SECRET_HEADER = "X-Wait-Resume-Notify-Secret";

    private static final String TENANT_HEADER = "tenant";

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${wait.resume.notify-base-url:}")
    private String notifyBaseUrl;

    @Value("${wait.resume.notify-secret:}")
    private String notifySecret;

    @Value("${wait.resume.notify-enabled:true}")
    private boolean notifyEnabled;

    @Override
    public void notifyAfterCrmUpdate(String resumeKey, String tenantId)
    {
        if (!notifyEnabled)
        {
            return;
        }
        if (StringUtils.isBlank(notifyBaseUrl))
        {
            log.debug("wait resume notify skipped: wait.resume.notify-base-url is not configured");
            return;
        }
        if (StringUtils.isBlank(resumeKey) || StringUtils.isBlank(tenantId))
        {
            log.warn("wait resume notify skipped: resumeKey or tenantId is blank");
            return;
        }

        try
        {
            String base = notifyBaseUrl.trim().replaceAll("/+$", "");
            String encodedKey = URLEncoder.encode(resumeKey, StandardCharsets.UTF_8.name());
            String url = base + "/waitresume-notify/" + encodedKey;

            HttpHeaders headers = new HttpHeaders();
            headers.set(TENANT_HEADER, tenantId);
            if (StringUtils.isNotBlank(notifySecret))
            {
                headers.set(NOTIFY_SECRET_HEADER, notifySecret.trim());
            }

            ResponseEntity<String> response = restTemplate.postForEntity(url, new HttpEntity<>(headers), String.class);
            if (!response.getStatusCode().is2xxSuccessful())
            {
                log.warn("wait resume notify non-2xx for key={}, tenant={}, status={}, body={}",
                        resumeKey, tenantId, response.getStatusCode(), response.getBody());
            }
            else
            {
                log.debug("wait resume notify ok for key={}, tenant={}", resumeKey, tenantId);
            }
        }
        catch (Exception e)
        {
            log.warn("wait resume notify failed for key={}, tenant={}: {}", resumeKey, tenantId, e.getMessage());
        }
    }
}
