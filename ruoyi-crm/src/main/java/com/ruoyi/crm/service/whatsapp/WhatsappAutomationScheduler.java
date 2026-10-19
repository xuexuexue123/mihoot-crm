package com.ruoyi.crm.service.whatsapp;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.domain.CrmAppointment;
import com.ruoyi.crm.domain.CrmCustomerIdentity;
import com.ruoyi.crm.domain.CrmLead;
import com.ruoyi.crm.domain.CrmWhatsappAutomationRule;
import com.ruoyi.crm.domain.CrmWhatsappConfig;
import com.ruoyi.crm.mapper.CrmAppointmentMapper;
import com.ruoyi.crm.mapper.CrmCustomerIdentityMapper;
import com.ruoyi.crm.mapper.CrmLeadMapper;
import com.ruoyi.crm.mapper.CrmWhatsappAutomationRuleMapper;
import com.ruoyi.crm.mapper.CrmWhatsappConfigMapper;
import com.ruoyi.crm.mapper.CrmWhatsappMessageMapper;
import com.ruoyi.crm.service.ICrmWhatsappTemplateSendTaskService;
import com.ruoyi.crm.service.support.IdentityNormalizer;
import com.ruoyi.framework.datasource.DynamicDataSourceContextHolder;
import com.ruoyi.framework.datasource.DynamicRoutingDataSource;
import com.ruoyi.tenant.domain.MasterTenant;
import com.ruoyi.tenant.domain.enums.TenantStatus;
import com.ruoyi.tenant.service.IMasterTenantService;

/**
 * WhatsApp 营销自动化 Scheduler：首版仅 appointment_reminder / lead_no_reply。
 */
@Component
public class WhatsappAutomationScheduler
{
    private static final Logger log = LoggerFactory.getLogger(WhatsappAutomationScheduler.class);

    /** 扫描窗口：与 cron 1 分钟对齐，略放大避免漏扫 */
    private static final long SCAN_WINDOW_MS = 70_000L;

    private static final int LEAD_BATCH_LIMIT = 100;

    public static final String TRIGGER_APPOINTMENT_REMINDER = "appointment_reminder";
    public static final String TRIGGER_LEAD_NO_REPLY = "lead_no_reply";

    @Value("${spring.datasource.driverClassName}")
    private String driverClassName;

    @Autowired
    private IMasterTenantService masterTenantService;

    @Autowired
    private DynamicRoutingDataSource dynamicRoutingDataSource;

    @Autowired
    private CrmWhatsappAutomationRuleMapper ruleMapper;

    @Autowired
    private CrmAppointmentMapper appointmentMapper;

    @Autowired
    private CrmLeadMapper leadMapper;

    @Autowired
    private CrmCustomerIdentityMapper identityMapper;

    @Autowired
    private CrmWhatsappMessageMapper messageMapper;

    @Autowired
    private CrmWhatsappConfigMapper configMapper;

    @Autowired
    private ICrmWhatsappTemplateSendTaskService sendTaskService;

    @Scheduled(cron = "0 * * * * ?")
    public void pollAutomationRules()
    {
        MasterTenant query = new MasterTenant();
        List<MasterTenant> tenants = masterTenantService.selectMasterTenantList(query);
        for (MasterTenant tenant : tenants)
        {
            if (!isActiveTenant(tenant))
            {
                continue;
            }
            try
            {
                ensureTenantDataSourceReady(tenant);
                DynamicDataSourceContextHolder.setDataSourceKey(tenant.getTenant());
                processTenant(tenant.getTenant());
            }
            catch (Exception ex)
            {
                log.warn("WhatsApp automation scheduler tenant {}: {}", tenant.getTenant(), ex.getMessage());
            }
            finally
            {
                DynamicDataSourceContextHolder.clearDataSourceKey();
            }
        }
    }

    private void processTenant(String tenantId)
    {
        CrmWhatsappConfig config = configMapper.selectByTenantId(tenantId);
        if (config == null || config.getAccessTokenEnc() == null)
        {
            return;
        }

        List<CrmWhatsappAutomationRule> appointmentRules =
                ruleMapper.selectEnabledByTenantAndType(tenantId, TRIGGER_APPOINTMENT_REMINDER);
        for (CrmWhatsappAutomationRule rule : appointmentRules)
        {
            try
            {
                processAppointmentReminder(tenantId, rule, config.getPhoneNumberId());
            }
            catch (Exception e)
            {
                log.warn("appointment_reminder failed tenant={} ruleId={}: {}",
                        tenantId, rule.getId(), e.getMessage());
            }
        }

        List<CrmWhatsappAutomationRule> leadRules =
                ruleMapper.selectEnabledByTenantAndType(tenantId, TRIGGER_LEAD_NO_REPLY);
        for (CrmWhatsappAutomationRule rule : leadRules)
        {
            try
            {
                processLeadNoReply(tenantId, rule, config.getPhoneNumberId());
            }
            catch (Exception e)
            {
                log.warn("lead_no_reply failed tenant={} ruleId={}: {}",
                        tenantId, rule.getId(), e.getMessage());
            }
        }
    }

    /**
     * 优先 triggerConfig.daysBefore（默认 1 天），缺省回退 delaySeconds。
     * 命中条件：start_time ∈ [now + offset, now + offset + window)
     */
    private void processAppointmentReminder(String tenantId, CrmWhatsappAutomationRule rule,
                                            String phoneNumberId)
    {
        int offsetSec = resolveAppointmentOffsetSeconds(rule);
        long now = System.currentTimeMillis();
        Date windowStart = new Date(now + offsetSec * 1000L);
        Date windowEnd = new Date(windowStart.getTime() + SCAN_WINDOW_MS);

        List<CrmAppointment> appointments = appointmentMapper.selectForReminderWindow(windowStart, windowEnd);
        if (appointments == null || appointments.isEmpty())
        {
            return;
        }

        List<Map<String, Object>> components = parseComponents(rule.getTemplateParams());
        for (CrmAppointment appt : appointments)
        {
            String contactId = appt.getRelateId();
            String businessKey = "appointment:" + appt.getAppointmentId();
            WaTarget target = resolveWaTarget(contactId, null);
            if (target == null || StringUtils.isEmpty(target.waId))
            {
                log.debug("Skip appointment reminder: no WhatsApp identity appointmentId={}",
                        appt.getAppointmentId());
                continue;
            }
            try
            {
                sendTaskService.enqueueSendForAutomation(
                        tenantId, rule.getTemplateName(), rule.getTemplateLanguage(),
                        target.waId, target.contactId, phoneNumberId, components,
                        rule.getId(), businessKey);
            }
            catch (Exception e)
            {
                log.warn("Failed enqueue appointment reminder appointmentId={}: {}",
                        appt.getAppointmentId(), e.getMessage());
            }
        }
    }

    /**
     * 优先 triggerConfig.hoursWithoutReply（默认 6 小时），缺省回退 delaySeconds。
     * 线索创建时间落在 [now - offset - window, now - offset)。
     */
    private void processLeadNoReply(String tenantId, CrmWhatsappAutomationRule rule, String phoneNumberId)
    {
        int offsetSec = resolveLeadNoReplyOffsetSeconds(rule);
        long now = System.currentTimeMillis();
        Date createdTo = new Date(now - offsetSec * 1000L);
        Date createdFrom = new Date(createdTo.getTime() - SCAN_WINDOW_MS);

        List<CrmLead> leads = leadMapper.selectCreatedBetween(createdFrom, createdTo, LEAD_BATCH_LIMIT);
        if (leads == null || leads.isEmpty())
        {
            return;
        }

        List<Map<String, Object>> components = parseComponents(rule.getTemplateParams());
        for (CrmLead lead : leads)
        {
            String businessKey = "lead:" + lead.getLeadId() + ":noreply";
            WaTarget target = resolveWaTarget(lead.getContactId(), lead.getMobilePhone());
            if (target == null || StringUtils.isEmpty(target.waId))
            {
                continue;
            }
            if (messageMapper.countInboundByWaId(tenantId, target.waId) > 0)
            {
                continue;
            }
            try
            {
                sendTaskService.enqueueSendForAutomation(
                        tenantId, rule.getTemplateName(), rule.getTemplateLanguage(),
                        target.waId, target.contactId, phoneNumberId, components,
                        rule.getId(), businessKey);
            }
            catch (Exception e)
            {
                log.warn("Failed enqueue lead_no_reply leadId={}: {}", lead.getLeadId(), e.getMessage());
            }
        }
    }

    /**
     * appointment_reminder：triggerConfig.daysBefore（默认 1）→ 秒；缺省回退 delaySeconds。
     */
    static int resolveAppointmentOffsetSeconds(CrmWhatsappAutomationRule rule)
    {
        Integer daysBefore = readConfigNumber(rule != null ? rule.getTriggerConfig() : null, "daysBefore");
        if (daysBefore != null && daysBefore >= 0)
        {
            return daysBefore * 86400;
        }
        if (rule != null && rule.getDelaySeconds() != null && rule.getDelaySeconds() > 0)
        {
            return rule.getDelaySeconds();
        }
        return 86400; // 默认 1 天
    }

    /**
     * lead_no_reply：triggerConfig.hoursWithoutReply（默认 6）→ 秒；缺省回退 delaySeconds。
     */
    static int resolveLeadNoReplyOffsetSeconds(CrmWhatsappAutomationRule rule)
    {
        Integer hours = readConfigNumber(rule != null ? rule.getTriggerConfig() : null, "hoursWithoutReply");
        if (hours != null && hours >= 0)
        {
            return hours * 3600;
        }
        if (rule != null && rule.getDelaySeconds() != null && rule.getDelaySeconds() > 0)
        {
            return rule.getDelaySeconds();
        }
        return 6 * 3600; // 默认 6 小时
    }

    private static Integer readConfigNumber(String triggerConfigJson, String key)
    {
        if (StringUtils.isEmpty(triggerConfigJson) || StringUtils.isEmpty(key))
        {
            return null;
        }
        try
        {
            Object parsed = JSON.parse(triggerConfigJson);
            if (!(parsed instanceof Map))
            {
                return null;
            }
            Object v = ((Map<?, ?>) parsed).get(key);
            if (v instanceof Number)
            {
                return ((Number) v).intValue();
            }
            if (v != null)
            {
                return Integer.parseInt(String.valueOf(v).trim());
            }
        }
        catch (Exception ignored)
        {
            // ignore malformed config
        }
        return null;
    }

    private WaTarget resolveWaTarget(String contactId, String mobilePhone)
    {
        if (StringUtils.isNotEmpty(contactId))
        {
            List<CrmCustomerIdentity> identities = identityMapper.selectByContactId(contactId);
            if (identities != null)
            {
                String waId = null;
                String phoneAsWa = null;
                for (CrmCustomerIdentity id : identities)
                {
                    if (IdentityNormalizer.TYPE_WHATSAPP_WA_ID.equals(id.getIdentityType()))
                    {
                        waId = id.getIdentityValue();
                        break;
                    }
                    if (IdentityNormalizer.TYPE_WHATSAPP_PHONE.equals(id.getIdentityType())
                            || "mobile_phone".equals(id.getIdentityType()))
                    {
                        phoneAsWa = id.getIdentityValue();
                    }
                }
                if (StringUtils.isNotEmpty(waId))
                {
                    return new WaTarget(waId, contactId);
                }
                if (StringUtils.isNotEmpty(phoneAsWa))
                {
                    String candidate = toWhatsAppWaId(phoneAsWa);
                    if (CrmWhatsappTemplateSendTaskServiceImpl.isValidWaId(candidate))
                    {
                        return new WaTarget(candidate, contactId);
                    }
                }
            }
        }

        if (StringUtils.isNotEmpty(mobilePhone))
        {
            String normalized = IdentityNormalizer.normalize("mobile_phone", mobilePhone);
            String candidate = toWhatsAppWaId(normalized);
            if (CrmWhatsappTemplateSendTaskServiceImpl.isValidWaId(candidate))
            {
                return new WaTarget(candidate, contactId);
            }
        }
        return null;
    }

    /** 国内 11 位手机号补全国家码 86，便于 Meta wa_id 发送 */
    private static String toWhatsAppWaId(String digits)
    {
        if (digits == null)
        {
            return null;
        }
        String d = digits.trim();
        if (d.length() == 11 && d.startsWith("1"))
        {
            return "86" + d;
        }
        return d;
    }

    @SuppressWarnings("unchecked")
    private static List<Map<String, Object>> parseComponents(String templateParamsJson)
    {
        if (StringUtils.isEmpty(templateParamsJson))
        {
            return null;
        }
        try
        {
            Object parsed = JSON.parse(templateParamsJson);
            if (parsed instanceof JSONArray)
            {
                return (List<Map<String, Object>>) (List<?>) ((JSONArray) parsed).toJavaList(Map.class);
            }
            if (parsed instanceof Map)
            {
                Object comps = ((Map<?, ?>) parsed).get("components");
                if (comps instanceof List)
                {
                    return (List<Map<String, Object>>) comps;
                }
            }
        }
        catch (Exception ignored)
        {
            // fall through
        }
        return null;
    }

    private boolean isActiveTenant(MasterTenant tenant)
    {
        if (tenant == null || StringUtils.isBlank(tenant.getTenant()))
        {
            return false;
        }
        if (TenantStatus.DISABLE.getCode().equals(tenant.getStatus()))
        {
            return false;
        }
        return tenant.getExpirationDate() == null || !tenant.getExpirationDate().before(DateUtils.getNowDate());
    }

    private void ensureTenantDataSourceReady(MasterTenant tenant)
    {
        String tenantId = tenant.getTenant();
        if (!dynamicRoutingDataSource.existDataSource(tenantId))
        {
            Map<String, Object> map = new HashMap<>();
            map.put("driverClassName", driverClassName);
            map.put("url", tenant.getUrl());
            map.put("username", tenant.getUsername());
            map.put("password", tenant.getPassword());
            dynamicRoutingDataSource.addDataSource(tenantId, map);
        }
    }

    private static final class WaTarget
    {
        final String waId;
        final String contactId;

        WaTarget(String waId, String contactId)
        {
            this.waId = waId;
            this.contactId = contactId;
        }
    }
}
