package com.ruoyi.crm.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson2.JSON;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.uuid.IdUtils;
import com.ruoyi.crm.domain.AnalyticsQueryScope;
import com.ruoyi.crm.domain.CrmAnalyticsQueryLog;
import com.ruoyi.crm.domain.dto.AnalyticsQueryRequest;
import com.ruoyi.crm.domain.vo.AnalyticsQueryResponse;
import com.ruoyi.crm.domain.vo.AnalyticsQueryResponse.BreakdownItem;
import com.ruoyi.crm.mapper.CrmAnalyticsQueryLogMapper;
import com.ruoyi.crm.service.ICrmAnalyticsAggregateService;
import com.ruoyi.crm.service.ICrmAnalyticsQueryService;
import com.ruoyi.crm.service.support.PatientClvCalculator;

@Service
public class CrmAnalyticsQueryServiceImpl implements ICrmAnalyticsQueryService
{
    private static final Set<String> SUPPORTED_METRICS = new HashSet<>(Arrays.asList(
            "opty.pipeline_total",
            "opty.count_by_stage",
            "task.overdue_count",
            "lead.conversion_rate",
            "clv.summary"));

    private static final Set<String> ALLOWED_GROUP_BY = new HashSet<>(Arrays.asList("stage", "owner"));

    private static final List<String> DEFAULT_LOST_STAGES = Arrays.asList("Closed Lost", "Lost", "closed_lost");

    @Autowired
    private ICrmAnalyticsAggregateService crmAnalyticsAggregateService;

    @Autowired
    private CrmAnalyticsQueryLogMapper crmAnalyticsQueryLogMapper;

    @Autowired
    private PatientClvCalculator patientClvCalculator;

    @Override
    public AnalyticsQueryResponse query(String tenantId, AnalyticsQueryRequest request)
    {
        tenantId = requireTenantId(tenantId);
        validateRequest(request);
        long started = System.currentTimeMillis();

        AnalyticsQueryScope scope = buildScope(request);
        String metric = request.getMetric().trim();
        AnalyticsQueryResponse response = dispatchMetric(metric, scope, request);
        response.setMetric(metric);
        response.setAsOf(Instant.now().toString());

        writeAuditLog(tenantId, metric, request, response, (int) (System.currentTimeMillis() - started));
        return response;
    }

    private AnalyticsQueryResponse dispatchMetric(String metric, AnalyticsQueryScope scope, AnalyticsQueryRequest request)
    {
        switch (metric)
        {
            case "opty.pipeline_total":
                return buildOptyPipelineTotal(scope, request.getCurrency());
            case "opty.count_by_stage":
                return buildOptyCountByStage(scope, request.getGroupBy());
            case "task.overdue_count":
                return buildTaskOverdueCount(scope);
            case "lead.conversion_rate":
                return buildLeadConversionRate(scope);
            case "clv.summary":
                return buildClvSummary(scope, request.getCurrency());
            default:
                throw new ServiceException("不支持的 metric: " + metric, HttpStatus.BAD_REQUEST);
        }
    }

    private AnalyticsQueryResponse buildOptyPipelineTotal(AnalyticsQueryScope scope, String currency)
    {
        scope.setExcludeStages(resolvePipelineExcludeStages());
        BigDecimal total = nz(crmAnalyticsAggregateService.queryOptyPipelineTotal(scope));
        AnalyticsQueryResponse response = new AnalyticsQueryResponse();
        response.setValue(total);
        response.setUnit("currency");
        return response;
    }

    private AnalyticsQueryResponse buildOptyCountByStage(AnalyticsQueryScope scope, List<String> groupBy)
    {
        List<Map<String, Object>> rows = crmAnalyticsAggregateService.queryOptyCountByStage(scope);
        List<BreakdownItem> breakdown = new ArrayList<>();
        BigDecimal totalCount = BigDecimal.ZERO;
        for (Map<String, Object> row : rows)
        {
            BreakdownItem item = new BreakdownItem();
            item.setDimension("stage");
            item.setKey(stringVal(row.get("stage_key")));
            Long cnt = longVal(row.get("cnt"));
            item.setCount(cnt);
            item.setValue(nzDecimal(row.get("stage_amount")));
            breakdown.add(item);
            totalCount = totalCount.add(BigDecimal.valueOf(cnt != null ? cnt : 0L));
        }
        AnalyticsQueryResponse response = new AnalyticsQueryResponse();
        response.setValue(totalCount);
        response.setUnit("count");
        response.setBreakdown(breakdown);
        return response;
    }

    private AnalyticsQueryResponse buildTaskOverdueCount(AnalyticsQueryScope scope)
    {
        Long count = crmAnalyticsAggregateService.queryTaskOverdueCount(scope);
        AnalyticsQueryResponse response = new AnalyticsQueryResponse();
        response.setValue(BigDecimal.valueOf(count != null ? count : 0L));
        response.setUnit("count");
        return response;
    }

    private AnalyticsQueryResponse buildLeadConversionRate(AnalyticsQueryScope scope)
    {
        Long created = crmAnalyticsAggregateService.queryLeadCreatedCount(scope);
        Long converted = crmAnalyticsAggregateService.queryLeadConvertedCount(scope);
        long createdCount = created != null ? created : 0L;
        long convertedCount = converted != null ? converted : 0L;
        BigDecimal rate = BigDecimal.ZERO;
        if (createdCount > 0)
        {
            rate = BigDecimal.valueOf(convertedCount)
                    .divide(BigDecimal.valueOf(createdCount), 6, RoundingMode.HALF_UP);
        }
        AnalyticsQueryResponse response = new AnalyticsQueryResponse();
        response.setValue(rate);
        response.setUnit("ratio");
        return response;
    }

    private AnalyticsQueryResponse buildClvSummary(AnalyticsQueryScope scope, String currency)
    {
        Map<String, Object> row = crmAnalyticsAggregateService.queryClvSummary(scope);
        AnalyticsQueryResponse response = new AnalyticsQueryResponse();
        response.setValue(nzDecimal(row != null ? row.get("net_clv") : null));
        response.setUnit("currency");
        List<BreakdownItem> breakdown = new ArrayList<>();
        if (row != null)
        {
            breakdown.add(breakdownItem("booked_total", row.get("booked_total")));
            breakdown.add(breakdownItem("inflight_total", row.get("inflight_total")));
            breakdown.add(breakdownItem("refund_total", row.get("refund_total")));
            breakdown.add(breakdownItem("paid_total", row.get("paid_total")));
            BreakdownItem accounts = new BreakdownItem();
            accounts.setDimension("summary");
            accounts.setKey("account_count");
            accounts.setCount(longVal(row.get("account_count")));
            accounts.setValue(BigDecimal.valueOf(accounts.getCount() != null ? accounts.getCount() : 0L));
            breakdown.add(accounts);
        }
        response.setBreakdown(breakdown);
        return response;
    }

    private BreakdownItem breakdownItem(String key, Object value)
    {
        BreakdownItem item = new BreakdownItem();
        item.setDimension("summary");
        item.setKey(key);
        item.setValue(nzDecimal(value));
        return item;
    }

    private List<String> resolvePipelineExcludeStages()
    {
        List<String> stages = new ArrayList<>(patientClvCalculator.resolveClosedWonStages());
        stages.addAll(DEFAULT_LOST_STAGES);
        return stages;
    }

    private AnalyticsQueryScope buildScope(AnalyticsQueryRequest request)
    {
        AnalyticsQueryScope scope = new AnalyticsQueryScope();
        if (request.getFilters() != null)
        {
            scope.setFilterOwnerId(trimToNull(request.getFilters().get("ownerId")));
            scope.setFilterOrgId(trimToNull(request.getFilters().get("orgId")));
            scope.setFilterStage(trimToNull(request.getFilters().get("stage")));
            scope.setFilterAccountId(trimToNull(request.getFilters().get("accountId")));
        }
        if (request.getDateRange() != null)
        {
            scope.setDateField(StringUtils.isNotEmpty(request.getDateRange().getField())
                    ? request.getDateRange().getField() : "closeDate");
            scope.setDateFrom(trimToNull(request.getDateRange().getFrom()));
            scope.setDateTo(trimToNull(request.getDateRange().getTo()));
        }
        return scope;
    }

    private void validateRequest(AnalyticsQueryRequest request)
    {
        if (request == null || StringUtils.isBlank(request.getMetric()))
        {
            throw new ServiceException("metric 不能为空", HttpStatus.BAD_REQUEST);
        }
        if (!SUPPORTED_METRICS.contains(request.getMetric().trim()))
        {
            throw new ServiceException("不支持的 metric: " + request.getMetric(), HttpStatus.BAD_REQUEST);
        }
        if (request.getGroupBy() != null)
        {
            for (String dim : request.getGroupBy())
            {
                if (StringUtils.isNotEmpty(dim) && !ALLOWED_GROUP_BY.contains(dim))
                {
                    throw new ServiceException("不支持的 groupBy: " + dim, HttpStatus.BAD_REQUEST);
                }
            }
        }
    }

    private void writeAuditLog(String tenantId, String metric, AnalyticsQueryRequest request,
            AnalyticsQueryResponse response, int durationMs)
    {
        CrmAnalyticsQueryLog log = new CrmAnalyticsQueryLog();
        log.setLogId(IdUtils.fastUUID());
        log.setTenantId(tenantId);
        log.setUserId(SecurityUtils.getUsername());
        log.setMetric(metric);
        log.setRequestJson(JSON.toJSONString(request));
        log.setResponseJson(JSON.toJSONString(response));
        log.setDurationMs(durationMs);
        crmAnalyticsQueryLogMapper.insertCrmAnalyticsQueryLog(log);
    }

    private String requireTenantId(String tenantId)
    {
        if (StringUtils.isBlank(tenantId))
        {
            throw new ServiceException("缺少租户头 tenant", HttpStatus.BAD_REQUEST);
        }
        return tenantId.trim();
    }

    private String trimToNull(String value)
    {
        if (StringUtils.isBlank(value))
        {
            return null;
        }
        return value.trim();
    }

    private BigDecimal nz(BigDecimal value)
    {
        return value != null ? value : BigDecimal.ZERO;
    }

    private BigDecimal nzDecimal(Object value)
    {
        if (value == null)
        {
            return BigDecimal.ZERO;
        }
        if (value instanceof BigDecimal)
        {
            return (BigDecimal) value;
        }
        return new BigDecimal(value.toString());
    }

    private Long longVal(Object value)
    {
        if (value == null)
        {
            return 0L;
        }
        if (value instanceof Number)
        {
            return ((Number) value).longValue();
        }
        return Long.parseLong(value.toString());
    }

    private String stringVal(Object value)
    {
        return value != null ? value.toString() : "";
    }
}
