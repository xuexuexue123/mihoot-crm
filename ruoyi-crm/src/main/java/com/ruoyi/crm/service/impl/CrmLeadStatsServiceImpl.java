package com.ruoyi.crm.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruoyi.common.annotation.DataScope;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.crm.domain.dto.CrmLeadStatsQuery;
import com.ruoyi.crm.domain.vo.CrmLeadStatsSummaryVo;
import com.ruoyi.crm.mapper.CrmLeadStatsMapper;
import com.ruoyi.crm.service.ICrmLeadStatsService;

@Service
public class CrmLeadStatsServiceImpl implements ICrmLeadStatsService
{
    @Autowired
    private CrmLeadStatsMapper statsMapper;

    @Override
    @DataScope(deptAlias = "d", userAlias = "u")
    public CrmLeadStatsSummaryVo summary(CrmLeadStatsQuery query)
    {
        validateRange(query);
        CrmLeadStatsSummaryVo vo = new CrmLeadStatsSummaryVo();
        vo.setFrom(query.getFrom());
        vo.setTo(query.getTo());
        long newLeads = safeLong(statsMapper.countNewLeads(query));
        long converted = safeLong(statsMapper.countConvertedInRange(query));
        vo.setNewLeads(newLeads);
        vo.setActiveLeads(safeLong(statsMapper.countActiveLeads(query)));
        vo.setConvertedCount(converted);
        vo.setOpenPoolCount(safeLong(statsMapper.countOpenPool(query)));
        vo.setAssignedCount(safeLong(statsMapper.countAssignedPool(query)));
        if (newLeads > 0)
        {
            vo.setConversionRate(BigDecimal.valueOf(converted)
                    .divide(BigDecimal.valueOf(newLeads), 4, RoundingMode.HALF_UP));
        }
        return vo;
    }

    @Override
    @DataScope(deptAlias = "d", userAlias = "u")
    public Map<String, Object> funnel(CrmLeadStatsQuery query)
    {
        List<Map<String, Object>> rows = statsMapper.selectFunnelByStage(query);
        long total = 0;
        List<Map<String, Object>> items = new ArrayList<>();
        for (Map<String, Object> row : rows)
        {
            Map<String, Object> item = new HashMap<>();
            item.put("stage", row.get("stage"));
            item.put("label", row.get("stage"));
            Number count = (Number) row.get("count");
            long c = count != null ? count.longValue() : 0L;
            item.put("count", c);
            total += c;
            items.add(item);
        }
        Map<String, Object> data = new HashMap<>();
        data.put("items", items);
        data.put("sampleScope", "pending");
        data.put("total", total);
        return data;
    }

    @Override
    @DataScope(deptAlias = "d", userAlias = "u")
    public Map<String, Object> conversionByOwner(CrmLeadStatsQuery query)
    {
        validateRange(query);
        List<Map<String, Object>> rows = statsMapper.selectConversionByOwner(query);
        List<Map<String, Object>> items = new ArrayList<>();
        for (Map<String, Object> row : rows)
        {
            Map<String, Object> item = new HashMap<>();
            item.put("owner", row.get("owner"));
            item.put("userId", row.get("userid") != null ? row.get("userid") : row.get("userId"));
            item.put("convertedCount", row.get("convertedcount") != null
                    ? row.get("convertedcount") : row.get("convertedCount"));
            items.add(item);
        }
        Map<String, Object> data = new HashMap<>();
        data.put("items", items);
        return data;
    }

    @Override
    @DataScope(deptAlias = "d", userAlias = "u")
    public Map<String, Object> trend(CrmLeadStatsQuery query)
    {
        validateRange(query);
        List<Map<String, Object>> rows = statsMapper.selectTrendSeries(query);
        List<Map<String, Object>> series = new ArrayList<>();
        for (Map<String, Object> row : rows)
        {
            Map<String, Object> point = new HashMap<>();
            point.put("period", row.get("period"));
            point.put("newLeads", row.get("newleads") != null ? row.get("newleads") : row.get("newLeads"));
            point.put("convertedCount", row.get("convertedcount") != null
                    ? row.get("convertedcount") : row.get("convertedCount"));
            series.add(point);
        }
        Map<String, Object> data = new HashMap<>();
        data.put("series", series);
        return data;
    }

    private static void validateRange(CrmLeadStatsQuery query)
    {
        if (query == null || query.getFrom() == null || query.getTo() == null)
        {
            throw new ServiceException("from/to 必填", HttpStatus.BAD_REQUEST);
        }
    }

    private static long safeLong(Long value)
    {
        return value != null ? value : 0L;
    }
}
