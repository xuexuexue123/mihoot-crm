package com.ruoyi.crm.service.bulkemail;

import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mashape.unirest.http.exceptions.UnirestException;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.mailgun.MailgunClient;
import com.ruoyi.common.utils.sign.AESUtil;
import com.ruoyi.crm.domain.CrmBulkemailProviderConfig;
import com.ruoyi.crm.domain.dto.BulkemailProviderConfigSaveRequest;
import com.ruoyi.crm.mapper.CrmBulkemailProviderConfigMapper;

@Service
public class BulkemailProviderConfigServiceImpl implements IBulkemailProviderConfigService
{
    private static final Logger log = LoggerFactory.getLogger(BulkemailProviderConfigServiceImpl.class);

    public static final String PROVIDER_MAILGUN = "mailgun";
    public static final String STATUS_ACTIVE = "active";
    public static final String STATUS_DISABLED = "disabled";
    public static final String PROVIDER_NOT_CONFIGURED = "PROVIDER_NOT_CONFIGURED";

    private static final String TEST_SUBJECT = "[Test] Bulk email provider";
    private static final String TEST_BODY = "This is a connectivity test from bulk email provider configuration.";

    @Autowired
    private CrmBulkemailProviderConfigMapper providerConfigMapper;

    @Override
    public Map<String, Object> getMasked()
    {
        CrmBulkemailProviderConfig cfg = providerConfigMapper.selectFirst();
        return buildConfigMap(cfg);
    }

    static Map<String, Object> buildConfigMap(CrmBulkemailProviderConfig cfg)
    {
        Map<String, Object> m = new HashMap<>();
        if (cfg == null)
        {
            m.put("provider", null);
            m.put("domain", null);
            m.put("fromEmail", null);
            m.put("fromName", null);
            m.put("region", null);
            m.put("connected", false);
            m.put("apiKeyConfigured", false);
            m.put("status", null);
            m.put("lastTestAt", null);
            m.put("lastError", null);
            m.put("updatedAt", null);
            return m;
        }

        boolean apiKeyConfigured = StringUtils.isNotBlank(cfg.getApiKeyEnc());
        boolean connected = STATUS_ACTIVE.equalsIgnoreCase(cfg.getStatus())
                && apiKeyConfigured
                && StringUtils.isNotBlank(cfg.getDomain())
                && StringUtils.isNotBlank(cfg.getFromEmail());

        m.put("provider", cfg.getProvider());
        m.put("domain", cfg.getDomain());
        m.put("fromEmail", cfg.getFromEmail());
        m.put("fromName", cfg.getFromName());
        m.put("region", cfg.getRegion());
        m.put("connected", connected);
        m.put("apiKeyConfigured", apiKeyConfigured);
        m.put("status", cfg.getStatus());
        m.put("lastTestAt", toIso8601(cfg.getLastTestAt()));
        m.put("lastError", cfg.getLastError());
        m.put("updatedAt", toIso8601(cfg.getUpdateTime()));
        return m;
    }

    @Override
    public void saveOrUpdate(BulkemailProviderConfigSaveRequest body, String operator)
    {
        if (body == null)
        {
            throw new ServiceException("请求体不能为空", HttpStatus.BAD_REQUEST);
        }

        String provider = StringUtils.isNotBlank(body.getProvider()) ? body.getProvider().trim() : PROVIDER_MAILGUN;
        if (!PROVIDER_MAILGUN.equalsIgnoreCase(provider))
        {
            throw new ServiceException("provider 仅支持 mailgun", HttpStatus.BAD_REQUEST);
        }

        if (StringUtils.isBlank(body.getDomain()))
        {
            throw new ServiceException("domain 不能为空", HttpStatus.BAD_REQUEST);
        }
        if (StringUtils.isBlank(body.getFromEmail()))
        {
            throw new ServiceException("fromEmail 不能为空", HttpStatus.BAD_REQUEST);
        }
        if (StringUtils.isBlank(body.getRegion()))
        {
            throw new ServiceException("region 不能为空", HttpStatus.BAD_REQUEST);
        }

        String region = body.getRegion().trim().toLowerCase();
        if (!"us".equals(region) && !"eu".equals(region))
        {
            throw new ServiceException("region 仅支持 us 或 eu", HttpStatus.BAD_REQUEST);
        }

        CrmBulkemailProviderConfig existing = providerConfigMapper.selectFirst();
        if (existing == null && StringUtils.isBlank(body.getApiKey()))
        {
            throw new ServiceException("首次保存必须提供 apiKey", HttpStatus.BAD_REQUEST);
        }

        String domain = body.getDomain().trim();
        String fromEmail = body.getFromEmail().trim();
        warnIfFromEmailDomainMismatch(fromEmail, domain);

        CrmBulkemailProviderConfig row = existing != null ? existing : new CrmBulkemailProviderConfig();
        row.setProvider(PROVIDER_MAILGUN);
        row.setDomain(domain);
        row.setFromEmail(fromEmail);
        row.setFromName(StringUtils.isNotBlank(body.getFromName()) ? body.getFromName().trim() : null);
        row.setRegion(region);

        String status = StringUtils.isNotBlank(body.getStatus()) ? body.getStatus().trim().toLowerCase() : STATUS_ACTIVE;
        if (!STATUS_ACTIVE.equals(status) && !STATUS_DISABLED.equals(status))
        {
            throw new ServiceException("status 仅支持 active 或 disabled", HttpStatus.BAD_REQUEST);
        }
        row.setStatus(status);

        try
        {
            if (StringUtils.isNotBlank(body.getApiKey()))
            {
                row.setApiKeyEnc(AESUtil.encrypt(body.getApiKey().trim()));
            }
            else if (existing != null)
            {
                row.setApiKeyEnc(existing.getApiKeyEnc());
            }
        }
        catch (Exception e)
        {
            throw new ServiceException("apiKey 加密失败");
        }

        Date now = DateUtils.getNowDate();
        row.setUpdateBy(operator);
        row.setUpdateTime(now);

        if (existing == null)
        {
            row.setCreateBy(operator);
            row.setCreateTime(now);
            row.setLastError(null);
            providerConfigMapper.insert(row);
        }
        else
        {
            row.setId(existing.getId());
            providerConfigMapper.update(row);
        }
    }

    @Override
    public void requireActive()
    {
        resolveForSend();
    }

    @Override
    public BulkemailProviderRuntimeConfig resolveForSend()
    {
        CrmBulkemailProviderConfig cfg = providerConfigMapper.selectFirst();
        if (cfg == null
                || !STATUS_ACTIVE.equalsIgnoreCase(cfg.getStatus())
                || StringUtils.isBlank(cfg.getApiKeyEnc())
                || StringUtils.isBlank(cfg.getDomain())
                || StringUtils.isBlank(cfg.getFromEmail()))
        {
            throw new ServiceException(PROVIDER_NOT_CONFIGURED, HttpStatus.CONFLICT);
        }

        BulkemailProviderRuntimeConfig runtime = new BulkemailProviderRuntimeConfig();
        runtime.setProvider(cfg.getProvider());
        runtime.setDomain(cfg.getDomain());
        runtime.setFromEmail(cfg.getFromEmail());
        runtime.setFromName(cfg.getFromName());
        runtime.setRegion(cfg.getRegion());
        try
        {
            runtime.setApiKey(AESUtil.decrypt(cfg.getApiKeyEnc()));
        }
        catch (Exception e)
        {
            throw new ServiceException(PROVIDER_NOT_CONFIGURED, HttpStatus.CONFLICT);
        }
        if (StringUtils.isBlank(runtime.getApiKey()))
        {
            throw new ServiceException(PROVIDER_NOT_CONFIGURED, HttpStatus.CONFLICT);
        }
        return runtime;
    }

    @Override
    public void testSend(String toEmail, String operator)
    {
        if (StringUtils.isBlank(toEmail))
        {
            throw new ServiceException("toEmail 不能为空", HttpStatus.BAD_REQUEST);
        }

        BulkemailProviderRuntimeConfig cfg = resolveForSend();
        String from = MailgunClient.formatFrom(cfg.getFromName(), cfg.getFromEmail());
        CrmBulkemailProviderConfig row = providerConfigMapper.selectFirst();
        if (row == null)
        {
            throw new ServiceException(PROVIDER_NOT_CONFIGURED, HttpStatus.CONFLICT);
        }

        try
        {
            MailgunClient.sendText(cfg.getRegion(), cfg.getDomain(), cfg.getApiKey(),
                    from, toEmail.trim(), TEST_SUBJECT, TEST_BODY);

            Date now = DateUtils.getNowDate();
            providerConfigMapper.updateTestOutcome(row.getId(), now, null, operator, now);
        }
        catch (UnirestException ex)
        {
            String summary = MailgunClient.summarizeError(ex);
            Date now = DateUtils.getNowDate();
            providerConfigMapper.updateTestOutcome(row.getId(), row.getLastTestAt(), summary, operator, now);
            throw new ServiceException("连通性测试失败: " + summary, HttpStatus.BAD_REQUEST);
        }
    }

    static void warnIfFromEmailDomainMismatch(String fromEmail, String domain)
    {
        if (StringUtils.isBlank(fromEmail) || StringUtils.isBlank(domain))
        {
            return;
        }
        int at = fromEmail.lastIndexOf('@');
        if (at < 0 || at == fromEmail.length() - 1)
        {
            return;
        }
        String emailDomain = fromEmail.substring(at + 1).trim().toLowerCase();
        String mgDomain = domain.trim().toLowerCase();
        if (!emailDomain.equals(mgDomain) && !emailDomain.endsWith("." + mgDomain))
        {
            log.warn("fromEmail domain {} may not match Mailgun domain {}", emailDomain, mgDomain);
        }
    }

    static String toIso8601(Date date)
    {
        if (date == null)
        {
            return null;
        }
        return Instant.ofEpochMilli(date.getTime()).toString();
    }
}
