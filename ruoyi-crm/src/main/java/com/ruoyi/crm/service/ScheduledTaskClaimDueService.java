package com.ruoyi.crm.service;

import java.time.DateTimeException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.domain.WScheduledTask;
import com.ruoyi.crm.domain.dto.ClaimedScheduledTask;
import com.ruoyi.crm.domain.dto.ScheduledTaskClaimDueResult;
import com.ruoyi.crm.mapper.WScheduledTaskMapper;
import com.ruoyi.framework.datasource.DynamicDataSourceContextHolder;
import com.ruoyi.framework.datasource.DynamicRoutingDataSource;
import com.ruoyi.tenant.domain.MasterTenant;
import com.ruoyi.tenant.domain.enums.TenantStatus;
import com.ruoyi.tenant.service.IMasterTenantService;

/**
 * 跨租户扫描并原子抢占到期工作流定时任务
 */
@Service
public class ScheduledTaskClaimDueService
{
    private static final Logger log = LoggerFactory.getLogger(ScheduledTaskClaimDueService.class);

    private static final ZoneOffset SHANGHAI_OFFSET = ZoneOffset.ofHours(8);

    @Value("${spring.datasource.driverClassName}")
    private String driverClassName;

    @Autowired
    private IMasterTenantService masterTenantService;

    @Autowired
    private DynamicRoutingDataSource dynamicRoutingDataSource;

    @Autowired
    private WScheduledTaskMapper wScheduledTaskMapper;

    public ScheduledTaskClaimDueResult claimDue(String slot)
    {
        ParsedSlot parsed = parseAndValidateSlot(slot);
        ScheduledTaskClaimDueResult result = new ScheduledTaskClaimDueResult();
        result.setSlot(parsed.normalizedSlot);
        result.setTasks(new ArrayList<>());

        List<MasterTenant> tenants = masterTenantService.selectMasterTenantList(new MasterTenant());
        for (MasterTenant tenant : tenants)
        {
            if (tenant == null || StringUtils.isBlank(tenant.getTenant()))
            {
                continue;
            }
            if (TenantStatus.DISABLE.getCode().equals(tenant.getStatus()))
            {
                continue;
            }
            if (tenant.getExpirationDate() != null && tenant.getExpirationDate().before(DateUtils.getNowDate()))
            {
                continue;
            }
            try
            {
                ensureTenantDataSourceReady(tenant);
                DynamicDataSourceContextHolder.setDataSourceKey(tenant.getTenant());
                List<WScheduledTask> candidates = wScheduledTaskMapper.selectDueCandidates(
                        parsed.runTime, parsed.dayOfWeek, parsed.normalizedSlot);
                if (candidates == null || candidates.isEmpty())
                {
                    continue;
                }
                for (WScheduledTask candidate : candidates)
                {
                    int claimed = wScheduledTaskMapper.claimSlot(candidate.getId(), parsed.normalizedSlot);
                    if (claimed == 1)
                    {
                        result.getTasks().add(new ClaimedScheduledTask(
                                tenant.getTenant(),
                                candidate.getId(),
                                candidate.getName(),
                                candidate.getWorkflowId()));
                    }
                }
            }
            catch (Exception e)
            {
                log.warn("scheduled task claim-due tenant {}: {}", tenant.getTenant(), e.getMessage());
            }
            finally
            {
                DynamicDataSourceContextHolder.clearDataSourceKey();
            }
        }
        return result;
    }

    /**
     * 解析并校验 slot；公开供单测
     */
    public static ParsedSlot parseAndValidateSlot(String slot)
    {
        if (StringUtils.isBlank(slot))
        {
            throw new ServiceException("slot 不能为空", HttpStatus.BAD_REQUEST);
        }
        String trimmed = slot.trim();
        OffsetDateTime odt;
        try
        {
            odt = OffsetDateTime.parse(trimmed);
        }
        catch (DateTimeException e)
        {
            throw new ServiceException("slot 格式非法，须为 ISO-8601", HttpStatus.BAD_REQUEST);
        }
        if (!SHANGHAI_OFFSET.equals(odt.getOffset()))
        {
            throw new ServiceException("slot 时区必须为 +08:00（Asia/Shanghai）", HttpStatus.BAD_REQUEST);
        }
        int minute = odt.getMinute();
        if (minute != 0 && minute != 30)
        {
            throw new ServiceException("slot 分钟必须为 00 或 30", HttpStatus.BAD_REQUEST);
        }
        if (odt.getSecond() != 0 || odt.getNano() != 0)
        {
            throw new ServiceException("slot 秒与纳秒必须为 0", HttpStatus.BAD_REQUEST);
        }
        String runTime = String.format("%02d:%02d", odt.getHour(), odt.getMinute());
        // ISO-8601 day-of-week: Monday=1 … Sunday=7
        String dayOfWeek = String.valueOf(odt.getDayOfWeek().getValue());
        // 保留请求原文作为幂等键，避免格式化后与 Next 传入不一致
        return new ParsedSlot(trimmed, runTime, dayOfWeek);
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

    public static final class ParsedSlot
    {
        public final String normalizedSlot;
        public final String runTime;
        public final String dayOfWeek;

        public ParsedSlot(String normalizedSlot, String runTime, String dayOfWeek)
        {
            this.normalizedSlot = normalizedSlot;
            this.runTime = runTime;
            this.dayOfWeek = dayOfWeek;
        }
    }
}
