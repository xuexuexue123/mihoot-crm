package com.ruoyi.crm.feishu.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.crm.feishu.FeishuOpenApiClient;
import com.ruoyi.crm.feishu.FeishuTokenService;
import com.ruoyi.crm.feishu.domain.CrmFeishuConfig;
import com.ruoyi.crm.feishu.dto.FeishuBitableExportResult;
import com.ruoyi.crm.feishu.dto.FeishuBitableRequest;
import com.ruoyi.crm.feishu.service.ICrmFeishuConfigService;

@ExtendWith(MockitoExtension.class)
class CrmFeishuBitableServiceImplTest
{
    @Mock
    private ICrmFeishuConfigService feishuConfigService;

    @Mock
    private FeishuTokenService feishuTokenService;

    @Mock
    private FeishuOpenApiClient apiClient;

    @InjectMocks
    private CrmFeishuBitableServiceImpl service;

    private CrmFeishuConfig connectedConfig()
    {
        CrmFeishuConfig cfg = new CrmFeishuConfig();
        cfg.setAppId("cli_xxx");
        cfg.setConnected(true);
        cfg.setAppSecretEnc("enc");
        return cfg;
    }

    @Test
    void listTables_mapsItems()
    {
        CrmFeishuConfig cfg = connectedConfig();
        when(feishuConfigService.requireConnected()).thenReturn(cfg);
        when(feishuTokenService.getTenantAccessToken(cfg)).thenReturn("t-ok");

        JSONObject res = new JSONObject();
        res.put("code", 0);
        JSONObject data = new JSONObject();
        JSONArray items = new JSONArray();
        JSONObject table = new JSONObject();
        table.put("table_id", "tbl1");
        table.put("name", "客户表");
        items.add(table);
        data.put("items", items);
        data.put("has_more", false);
        res.put("data", data);
        when(apiClient.listTables(eq("t-ok"), eq("bascn"), isNull(), eq(100))).thenReturn(res);

        FeishuBitableRequest req = new FeishuBitableRequest();
        req.setAppToken("bascn");
        Map<String, Object> mapped = service.listTables(req);

        @SuppressWarnings("unchecked")
        java.util.List<Map<String, Object>> list = (java.util.List<Map<String, Object>>) mapped.get("items");
        assertEquals(1, list.size());
        assertEquals("tbl1", list.get(0).get("tableId"));
        assertEquals("客户表", list.get(0).get("name"));
    }

    @Test
    void createRecord_retriesOnceOnTokenInvalid()
    {
        CrmFeishuConfig cfg = connectedConfig();
        when(feishuConfigService.requireConnected()).thenReturn(cfg);
        when(feishuTokenService.getTenantAccessToken(cfg)).thenReturn("t-old", "t-new");

        JSONObject bad = new JSONObject();
        bad.put("code", FeishuOpenApiClient.CODE_TOKEN_INVALID);
        bad.put("msg", "token invalid");

        JSONObject ok = new JSONObject();
        ok.put("code", 0);
        JSONObject data = new JSONObject();
        JSONObject record = new JSONObject();
        record.put("record_id", "rec1");
        JSONObject fields = new JSONObject();
        fields.put("姓名", "张三");
        record.put("fields", fields);
        data.put("record", record);
        ok.put("data", data);

        JSONObject body = new JSONObject();
        body.put("fields", fields);

        when(apiClient.createRecord(eq("t-old"), eq("bas"), eq("tbl"), any(JSONObject.class))).thenReturn(bad);
        when(apiClient.createRecord(eq("t-new"), eq("bas"), eq("tbl"), any(JSONObject.class))).thenReturn(ok);

        FeishuBitableRequest req = new FeishuBitableRequest();
        req.setAppToken("bas");
        req.setTableId("tbl");
        req.setFields(fields);

        Map<String, Object> mapped = service.createRecord(req);
        assertEquals("rec1", mapped.get("recordId"));
        verify(feishuTokenService).invalidate("cli_xxx");
        verify(apiClient, times(2)).createRecord(anyString(), eq("bas"), eq("tbl"), any(JSONObject.class));
    }

    @Test
    void searchRecords_capsPageSizeAt500()
    {
        CrmFeishuConfig cfg = connectedConfig();
        when(feishuConfigService.requireConnected()).thenReturn(cfg);
        when(feishuTokenService.getTenantAccessToken(cfg)).thenReturn("t-ok");

        JSONObject res = new JSONObject();
        res.put("code", 0);
        JSONObject data = new JSONObject();
        data.put("items", new JSONArray());
        data.put("has_more", false);
        res.put("data", data);
        when(apiClient.searchRecords(eq("t-ok"), eq("bas"), eq("tbl"), any(JSONObject.class))).thenAnswer(inv -> {
            JSONObject body = inv.getArgument(3);
            assertEquals(500, body.getIntValue("page_size"));
            return res;
        });

        FeishuBitableRequest req = new FeishuBitableRequest();
        req.setAppToken("bas");
        req.setTableId("tbl");
        req.setPageSize(9999);
        service.searchRecords(req);
    }

    @Test
    void listTables_mapsFeishuErrorWithCode()
    {
        CrmFeishuConfig cfg = connectedConfig();
        when(feishuConfigService.requireConnected()).thenReturn(cfg);
        when(feishuTokenService.getTenantAccessToken(cfg)).thenReturn("t-ok");

        JSONObject res = new JSONObject();
        res.put("code", 91403);
        res.put("msg", "Forbidden");
        when(apiClient.listTables(eq("t-ok"), eq("bas"), isNull(), eq(100))).thenReturn(res);

        FeishuBitableRequest req = new FeishuBitableRequest();
        req.setAppToken("bas");
        ServiceException ex = assertThrows(ServiceException.class, () -> service.listTables(req));
        assertTrue(ex.getMessage().contains("协作者"));
        assertEquals("feishuCode=91403", ex.getDetailMessage());
    }

    @Test
    void createApp_mapsAppToken()
    {
        CrmFeishuConfig cfg = connectedConfig();
        when(feishuConfigService.requireConnected()).thenReturn(cfg);
        when(feishuTokenService.getTenantAccessToken(cfg)).thenReturn("t-ok");

        JSONObject res = new JSONObject();
        res.put("code", 0);
        JSONObject app = new JSONObject();
        app.put("app_token", "basNew");
        app.put("name", "demo");
        app.put("default_table_id", "tbl0");
        app.put("url", "https://example.feishu.cn/base/basNew");
        JSONObject data = new JSONObject();
        data.put("app", app);
        res.put("data", data);
        when(apiClient.createApp(eq("t-ok"), any(JSONObject.class))).thenReturn(res);

        FeishuBitableRequest req = new FeishuBitableRequest();
        req.setName("demo");
        Map<String, Object> mapped = service.createApp(req);
        assertEquals("basNew", mapped.get("appToken"));
        assertEquals("tbl0", mapped.get("defaultTableId"));
    }

    @Test
    void batchCreateRecords_rejectsOverLimit()
    {
        FeishuBitableRequest req = new FeishuBitableRequest();
        req.setAppToken("bas");
        req.setTableId("tbl");
        JSONObject[] arr = new JSONObject[1001];
        for (int i = 0; i < arr.length; i++)
        {
            JSONObject row = new JSONObject();
            row.put("fields", Collections.singletonMap("a", i));
            arr[i] = row;
        }
        req.setRecords(Arrays.asList(arr));
        ServiceException ex = assertThrows(ServiceException.class, () -> service.batchCreateRecords(req));
        assertTrue(ex.getMessage().contains("1000"));
    }

    @Test
    void batchDeleteRecords_mapsDeleted()
    {
        CrmFeishuConfig cfg = connectedConfig();
        when(feishuConfigService.requireConnected()).thenReturn(cfg);
        when(feishuTokenService.getTenantAccessToken(cfg)).thenReturn("t-ok");

        JSONObject res = new JSONObject();
        res.put("code", 0);
        JSONObject data = new JSONObject();
        data.put("records", new JSONArray(Arrays.asList("rec1", "rec2")));
        res.put("data", data);
        when(apiClient.batchDeleteRecords(eq("t-ok"), eq("bas"), eq("tbl"), any(JSONObject.class))).thenReturn(res);

        FeishuBitableRequest req = new FeishuBitableRequest();
        req.setAppToken("bas");
        req.setTableId("tbl");
        req.setRecordIds(Arrays.asList("rec1", "rec2"));
        Map<String, Object> mapped = service.batchDeleteRecords(req);
        assertEquals(2, mapped.get("deleted"));
    }

    @Test
    void exportRecords_buildsCsv()
    {
        CrmFeishuConfig cfg = connectedConfig();
        when(feishuConfigService.requireConnected()).thenReturn(cfg);
        when(feishuTokenService.getTenantAccessToken(cfg)).thenReturn("t-ok");

        JSONObject fieldsRes = new JSONObject();
        fieldsRes.put("code", 0);
        JSONObject fieldsData = new JSONObject();
        JSONObject field = new JSONObject();
        field.put("field_id", "fld1");
        field.put("field_name", "姓名");
        fieldsData.put("items", new JSONArray(Collections.singletonList(field)));
        fieldsData.put("has_more", false);
        fieldsRes.put("data", fieldsData);
        when(apiClient.listFields(eq("t-ok"), eq("bas"), eq("tbl"), isNull(), anyInt())).thenReturn(fieldsRes);

        JSONObject listRes = new JSONObject();
        listRes.put("code", 0);
        JSONObject listData = new JSONObject();
        JSONObject rec = new JSONObject();
        rec.put("record_id", "rec1");
        JSONObject fields = new JSONObject();
        fields.put("姓名", "张三");
        rec.put("fields", fields);
        listData.put("items", new JSONArray(Collections.singletonList(rec)));
        listData.put("has_more", false);
        listRes.put("data", listData);
        when(apiClient.listRecords(eq("t-ok"), eq("bas"), eq("tbl"), isNull(), anyInt(), isNull()))
                .thenReturn(listRes);

        FeishuBitableRequest req = new FeishuBitableRequest();
        req.setAppToken("bas");
        req.setTableId("tbl");
        req.setFormat("csv");
        FeishuBitableExportResult file = service.exportRecords(req);
        assertTrue(file.getFilename().endsWith(".csv"));
        assertTrue(file.getContentType().contains("csv"));
        String text = new String(file.getContent());
        assertTrue(text.contains("姓名"));
        assertTrue(text.contains("张三"));
    }
}
