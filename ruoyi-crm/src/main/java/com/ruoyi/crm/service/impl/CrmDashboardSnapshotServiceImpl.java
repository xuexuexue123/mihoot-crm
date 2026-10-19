package com.ruoyi.crm.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.crm.domain.CrmDashboardSnapshot;
import com.ruoyi.crm.domain.dto.DashboardSnapshotResponse;
import com.ruoyi.crm.domain.dto.wecom.WecomCustomerTotalResponse;
import com.ruoyi.crm.domain.dto.wecom.WecomStaffCustomerItem;
import com.ruoyi.crm.mapper.CrmDashboardSnapshotMapper;
import com.ruoyi.crm.service.ICrmDashboardSnapshotService;

@Service
public class CrmDashboardSnapshotServiceImpl implements ICrmDashboardSnapshotService
{
    private static final Logger log = LoggerFactory.getLogger(CrmDashboardSnapshotServiceImpl.class);
    private static final String METRIC_TYPE_WECOM_CUSTOMER_TOTAL = "wecom_customer_total";

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private CrmDashboardSnapshotMapper dashboardSnapshotMapper;

    @Override
    public void saveCustomerTotalToday(String corpId, WecomCustomerTotalResponse data, String rawJson)
    {
        try
        {
            String metricDataJson = objectMapper.writeValueAsString(data);
            String detailDataJson = (data.getItems() != null && !data.getItems().isEmpty())
                    ? objectMapper.writeValueAsString(data.getItems()) : null;

            CrmDashboardSnapshot row = new CrmDashboardSnapshot();
            row.setCorpId(corpId);
            row.setSnapshotDate(new Date());
            row.setMetricType(METRIC_TYPE_WECOM_CUSTOMER_TOTAL);
            row.setMetricData(metricDataJson);
            row.setDetailData(detailDataJson);
            row.setRawJson(rawJson);

            dashboardSnapshotMapper.upsert(row);
        }
        catch (Exception e)
        {
            log.warn("Failed to save customer total snapshot: {}", e.getMessage());
        }
    }

    @Override
    public WecomCustomerTotalResponse getCustomerTotalToday()
    {
        try
        {
            CrmDashboardSnapshot row = dashboardSnapshotMapper.selectByDateAndType(
                    new Date(), METRIC_TYPE_WECOM_CUSTOMER_TOTAL);
            if (row == null || row.getMetricData() == null)
            {
                return null;
            }

            WecomCustomerTotalResponse resp = objectMapper.readValue(
                    row.getMetricData(), WecomCustomerTotalResponse.class);

            if (row.getDetailData() != null && !row.getDetailData().isEmpty())
            {
                List<WecomStaffCustomerItem> items = objectMapper.readValue(
                        row.getDetailData(), new TypeReference<List<WecomStaffCustomerItem>>() {});
                resp.setItems(items);
                // total_staff 应基于实际返回的 items 数量
                resp.setTotalStaff(items.size());
            }
            return resp;
        }
        catch (Exception e)
        {
            log.warn("Failed to read customer total snapshot: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public DashboardSnapshotResponse getTodaySnapshot()
    {
        DashboardSnapshotResponse resp = new DashboardSnapshotResponse();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        resp.setSnapshotDate(dateFormat.format(new Date()));

        WecomCustomerTotalResponse customerTotal = getCustomerTotalToday();
        resp.setWecomCustomerTotal(customerTotal);

        // 未来扩展：
        // resp.setStoreVisit(getStoreVisitToday());
        // resp.setWechatMpFans(getWechatMpFansToday());
        // ...

        return resp;
    }
}
