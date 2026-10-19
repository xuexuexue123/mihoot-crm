package com.ruoyi.crm.feishu.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.crm.feishu.FeishuOpenApiClient;
import com.ruoyi.crm.feishu.FeishuTokenService;
import com.ruoyi.crm.feishu.domain.CrmFeishuConfig;
import com.ruoyi.crm.feishu.dto.FeishuBitableExportResult;
import com.ruoyi.crm.feishu.dto.FeishuBitableRequest;
import com.ruoyi.crm.feishu.service.ICrmFeishuBitableService;
import com.ruoyi.crm.feishu.service.ICrmFeishuConfigService;

@Service
public class CrmFeishuBitableServiceImpl implements ICrmFeishuBitableService
{
    private static final int DEFAULT_PAGE_SIZE = 100;
    private static final int MAX_PAGE_SIZE = 500;
    private static final int DEFAULT_EXPORT_MAX_ROWS = 10_000;
    private static final int HARD_EXPORT_MAX_ROWS = 20_000;
    private static final int BATCH_CREATE_MAX = 1000;
    private static final int BATCH_DELETE_MAX = 500;

    @Autowired
    private ICrmFeishuConfigService feishuConfigService;

    @Autowired
    private FeishuTokenService feishuTokenService;

    @Autowired
    private FeishuOpenApiClient apiClient;

    @Override
    public Map<String, Object> createApp(FeishuBitableRequest request)
    {
        if (request == null || request.getName() == null || request.getName().trim().isEmpty())
        {
            throw new ServiceException("name 不能为空");
        }
        JSONObject body = new JSONObject();
        body.put("name", request.getName().trim());
        if (request.getFolderToken() != null && !request.getFolderToken().isEmpty())
        {
            body.put("folder_token", request.getFolderToken());
        }
        if (request.getTimeZone() != null && !request.getTimeZone().isEmpty())
        {
            body.put("time_zone", request.getTimeZone());
        }
        return executeWithTokenRetry(cfg -> apiClient.createApp(
                feishuTokenService.getTenantAccessToken(cfg), body), this::mapCreateApp);
    }

    @Override
    public Map<String, Object> createTable(FeishuBitableRequest request)
    {
        requireAppToken(request);
        if (request.getName() == null || request.getName().trim().isEmpty())
        {
            throw new ServiceException("name 不能为空");
        }
        JSONObject table = new JSONObject();
        table.put("name", request.getName().trim());
        if (request.getDefaultViewName() != null && !request.getDefaultViewName().isEmpty())
        {
            table.put("default_view_name", request.getDefaultViewName());
        }
        if (request.getTableFields() != null && !request.getTableFields().isEmpty())
        {
            table.put("fields", request.getTableFields());
        }
        JSONObject body = new JSONObject();
        body.put("table", table);
        return executeWithTokenRetry(cfg -> apiClient.createTable(
                feishuTokenService.getTenantAccessToken(cfg),
                request.getAppToken(),
                body), this::mapCreateTable);
    }

    @Override
    public Map<String, Object> listTables(FeishuBitableRequest request)
    {
        requireAppToken(request);
        return executeWithTokenRetry(cfg -> apiClient.listTables(
                feishuTokenService.getTenantAccessToken(cfg),
                request.getAppToken(),
                request.getPageToken(),
                normalizePageSize(request.getPageSize())), this::mapTablesList);
    }

    @Override
    public Map<String, Object> listFields(FeishuBitableRequest request)
    {
        requireAppToken(request);
        requireTableId(request);
        return executeWithTokenRetry(cfg -> apiClient.listFields(
                feishuTokenService.getTenantAccessToken(cfg),
                request.getAppToken(),
                request.getTableId(),
                request.getPageToken(),
                normalizePageSize(request.getPageSize())), this::mapFieldsList);
    }

    @Override
    public Map<String, Object> listRecords(FeishuBitableRequest request)
    {
        requireAppToken(request);
        requireTableId(request);
        return executeWithTokenRetry(cfg -> apiClient.listRecords(
                feishuTokenService.getTenantAccessToken(cfg),
                request.getAppToken(),
                request.getTableId(),
                request.getPageToken(),
                normalizePageSize(request.getPageSize()),
                request.getViewId()), this::mapSearchRecords);
    }

    @Override
    public Map<String, Object> createRecord(FeishuBitableRequest request)
    {
        requireAppToken(request);
        requireTableId(request);
        if (request.getFields() == null)
        {
            throw new ServiceException("fields 不能为空");
        }
        JSONObject body = new JSONObject();
        body.put("fields", request.getFields());
        return executeWithTokenRetry(cfg -> apiClient.createRecord(
                feishuTokenService.getTenantAccessToken(cfg),
                request.getAppToken(),
                request.getTableId(),
                body), this::mapRecord);
    }

    @Override
    public Map<String, Object> batchCreateRecords(FeishuBitableRequest request)
    {
        requireAppToken(request);
        requireTableId(request);
        if (request.getRecords() == null || request.getRecords().isEmpty())
        {
            throw new ServiceException("records 不能为空");
        }
        if (request.getRecords().size() > BATCH_CREATE_MAX)
        {
            throw new ServiceException("records 单次最多 " + BATCH_CREATE_MAX + " 条");
        }
        JSONArray records = new JSONArray();
        for (JSONObject item : request.getRecords())
        {
            if (item == null)
            {
                continue;
            }
            JSONObject row = new JSONObject();
            Object fields = item.get("fields");
            if (fields == null)
            {
                fields = item;
            }
            row.put("fields", fields);
            records.add(row);
        }
        if (records.isEmpty())
        {
            throw new ServiceException("records 不能为空");
        }
        JSONObject body = new JSONObject();
        body.put("records", records);
        return executeWithTokenRetry(cfg -> apiClient.batchCreateRecords(
                feishuTokenService.getTenantAccessToken(cfg),
                request.getAppToken(),
                request.getTableId(),
                body), this::mapBatchCreate);
    }

    @Override
    public Map<String, Object> updateRecord(FeishuBitableRequest request)
    {
        requireAppToken(request);
        requireTableId(request);
        requireRecordId(request);
        if (request.getFields() == null)
        {
            throw new ServiceException("fields 不能为空");
        }
        JSONObject body = new JSONObject();
        body.put("fields", request.getFields());
        return executeWithTokenRetry(cfg -> apiClient.updateRecord(
                feishuTokenService.getTenantAccessToken(cfg),
                request.getAppToken(),
                request.getTableId(),
                request.getRecordId(),
                body), this::mapRecord);
    }

    @Override
    public Map<String, Object> searchRecords(FeishuBitableRequest request)
    {
        requireAppToken(request);
        requireTableId(request);
        JSONObject body = new JSONObject();
        int pageSize = normalizePageSize(request.getPageSize());
        body.put("page_size", pageSize);
        if (request.getPageToken() != null && !request.getPageToken().isEmpty())
        {
            body.put("page_token", request.getPageToken());
        }
        if (request.getViewId() != null && !request.getViewId().isEmpty())
        {
            body.put("view_id", request.getViewId());
        }
        if (request.getFilter() != null)
        {
            body.put("filter", request.getFilter());
        }
        return executeWithTokenRetry(cfg -> apiClient.searchRecords(
                feishuTokenService.getTenantAccessToken(cfg),
                request.getAppToken(),
                request.getTableId(),
                body), this::mapSearchRecords);
    }

    @Override
    public Map<String, Object> deleteRecord(FeishuBitableRequest request)
    {
        requireAppToken(request);
        requireTableId(request);
        requireRecordId(request);
        String recordId = request.getRecordId();
        return executeWithTokenRetry(cfg -> apiClient.deleteRecord(
                feishuTokenService.getTenantAccessToken(cfg),
                request.getAppToken(),
                request.getTableId(),
                recordId), res -> mapDelete(res, recordId));
    }

    @Override
    public Map<String, Object> batchDeleteRecords(FeishuBitableRequest request)
    {
        requireAppToken(request);
        requireTableId(request);
        if (request.getRecordIds() == null || request.getRecordIds().isEmpty())
        {
            throw new ServiceException("recordIds 不能为空");
        }
        if (request.getRecordIds().size() > BATCH_DELETE_MAX)
        {
            throw new ServiceException("recordIds 单次最多 " + BATCH_DELETE_MAX + " 条");
        }
        JSONObject body = new JSONObject();
        body.put("records", request.getRecordIds());
        return executeWithTokenRetry(cfg -> apiClient.batchDeleteRecords(
                feishuTokenService.getTenantAccessToken(cfg),
                request.getAppToken(),
                request.getTableId(),
                body), this::mapBatchDelete);
    }

    @Override
    public FeishuBitableExportResult exportRecords(FeishuBitableRequest request)
    {
        requireAppToken(request);
        requireTableId(request);

        boolean csv = request.getFormat() != null && "csv".equalsIgnoreCase(request.getFormat().trim());
        int maxRows = normalizeExportMaxRows(request.getMaxRows());

        List<String> headers = resolveExportHeaders(request);
        List<Map<String, Object>> rows = fetchAllRecordsForExport(request, maxRows);

        try
        {
            if (csv)
            {
                byte[] bytes = buildCsv(headers, rows);
                String filename = "feishu-bitable-" + request.getTableId() + ".csv";
                return new FeishuBitableExportResult(bytes, filename, "text/csv; charset=UTF-8");
            }
            byte[] bytes = buildXlsx(headers, rows);
            String filename = "feishu-bitable-" + request.getTableId() + ".xlsx";
            return new FeishuBitableExportResult(bytes, filename,
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        }
        catch (ServiceException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            throw new ServiceException("导出失败: " + e.getMessage());
        }
    }

    private List<String> resolveExportHeaders(FeishuBitableRequest request)
    {
        Set<String> headers = new LinkedHashSet<>();
        headers.add("recordId");
        String pageToken = null;
        do
        {
            FeishuBitableRequest pageReq = new FeishuBitableRequest();
            pageReq.setAppToken(request.getAppToken());
            pageReq.setTableId(request.getTableId());
            pageReq.setPageToken(pageToken);
            pageReq.setPageSize(MAX_PAGE_SIZE);
            Map<String, Object> page = listFields(pageReq);
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> items = (List<Map<String, Object>>) page.get("items");
            if (items != null)
            {
                for (Map<String, Object> f : items)
                {
                    Object name = f.get("fieldName");
                    if (name != null && !name.toString().isEmpty())
                    {
                        headers.add(name.toString());
                    }
                }
            }
            Boolean hasMore = (Boolean) page.get("hasMore");
            pageToken = (String) page.get("pageToken");
            if (!Boolean.TRUE.equals(hasMore) || pageToken == null || pageToken.isEmpty())
            {
                break;
            }
        }
        while (true);
        return new ArrayList<>(headers);
    }

    private List<Map<String, Object>> fetchAllRecordsForExport(FeishuBitableRequest request, int maxRows)
    {
        List<Map<String, Object>> all = new ArrayList<>();
        String pageToken = null;
        boolean useFilter = request.getFilter() != null;
        do
        {
            FeishuBitableRequest pageReq = new FeishuBitableRequest();
            pageReq.setAppToken(request.getAppToken());
            pageReq.setTableId(request.getTableId());
            pageReq.setPageToken(pageToken);
            pageReq.setPageSize(MAX_PAGE_SIZE);
            pageReq.setViewId(request.getViewId());
            pageReq.setFilter(request.getFilter());

            Map<String, Object> page = useFilter ? searchRecords(pageReq) : listRecords(pageReq);
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> items = (List<Map<String, Object>>) page.get("items");
            if (items != null)
            {
                for (Map<String, Object> item : items)
                {
                    if (all.size() >= maxRows)
                    {
                        return all;
                    }
                    all.add(item);
                }
            }
            Boolean hasMore = (Boolean) page.get("hasMore");
            pageToken = (String) page.get("pageToken");
            if (!Boolean.TRUE.equals(hasMore) || pageToken == null || pageToken.isEmpty())
            {
                break;
            }
        }
        while (true);
        return all;
    }

    private static byte[] buildCsv(List<String> headers, List<Map<String, Object>> rows) throws Exception
    {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        // Excel 友好 UTF-8 BOM
        bos.write(0xEF);
        bos.write(0xBB);
        bos.write(0xBF);
        try (Writer w = new OutputStreamWriter(bos, StandardCharsets.UTF_8))
        {
            writeCsvLine(w, headers);
            for (Map<String, Object> row : rows)
            {
                List<String> cells = new ArrayList<>(headers.size());
                for (String h : headers)
                {
                    cells.add(cellValue(row, h));
                }
                writeCsvLine(w, cells);
            }
            w.flush();
        }
        return bos.toByteArray();
    }

    private static void writeCsvLine(Writer w, List<String> cells) throws Exception
    {
        for (int i = 0; i < cells.size(); i++)
        {
            if (i > 0)
            {
                w.write(',');
            }
            w.write(escapeCsv(cells.get(i)));
        }
        w.write('\n');
    }

    private static String escapeCsv(String raw)
    {
        if (raw == null)
        {
            return "";
        }
        boolean needQuote = raw.indexOf(',') >= 0 || raw.indexOf('"') >= 0
                || raw.indexOf('\n') >= 0 || raw.indexOf('\r') >= 0;
        String v = raw.replace("\"", "\"\"");
        return needQuote ? "\"" + v + "\"" : v;
    }

    private static byte[] buildXlsx(List<String> headers, List<Map<String, Object>> rows) throws Exception
    {
        try (SXSSFWorkbook wb = new SXSSFWorkbook(200); ByteArrayOutputStream bos = new ByteArrayOutputStream())
        {
            Sheet sheet = wb.createSheet("records");
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.size(); i++)
            {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers.get(i));
            }
            int r = 1;
            for (Map<String, Object> row : rows)
            {
                Row excelRow = sheet.createRow(r++);
                for (int i = 0; i < headers.size(); i++)
                {
                    excelRow.createCell(i).setCellValue(cellValue(row, headers.get(i)));
                }
            }
            wb.write(bos);
            wb.dispose();
            return bos.toByteArray();
        }
    }

    @SuppressWarnings("unchecked")
    private static String cellValue(Map<String, Object> row, String header)
    {
        if ("recordId".equals(header))
        {
            Object id = row.get("recordId");
            return id == null ? "" : String.valueOf(id);
        }
        Object fieldsObj = row.get("fields");
        if (!(fieldsObj instanceof Map))
        {
            return "";
        }
        Object val = ((Map<String, Object>) fieldsObj).get(header);
        return stringifyFieldValue(val);
    }

    private static String stringifyFieldValue(Object val)
    {
        if (val == null)
        {
            return "";
        }
        if (val instanceof String || val instanceof Number || val instanceof Boolean)
        {
            return String.valueOf(val);
        }
        return JSON.toJSONString(val);
    }

    private Map<String, Object> executeWithTokenRetry(
            Function<CrmFeishuConfig, JSONObject> call,
            Function<JSONObject, Map<String, Object>> mapper)
    {
        CrmFeishuConfig cfg = feishuConfigService.requireConnected();
        JSONObject res = call.apply(cfg);
        int code = res.getIntValue("code");
        if (FeishuOpenApiClient.isAccessTokenInvalid(code))
        {
            feishuTokenService.invalidate(cfg.getAppId());
            res = call.apply(cfg);
            code = res.getIntValue("code");
        }
        if (code != 0)
        {
            throwFeishuError(res);
        }
        return mapper.apply(res);
    }

    private void throwFeishuError(JSONObject res)
    {
        int code = res.getIntValue("code");
        String msg = res.getString("msg");
        if (msg == null || msg.isEmpty())
        {
            msg = "飞书接口调用失败";
        }
        if (code == 403 || (msg != null && (msg.contains("403") || msg.contains("Forbidden"))))
        {
            if (!msg.contains("协作者"))
            {
                msg = msg + "；请将应用加为多维表格协作者";
            }
        }
        ServiceException ex = new ServiceException(msg);
        ex.setDetailMessage("feishuCode=" + code);
        throw ex;
    }

    private Map<String, Object> mapCreateApp(JSONObject res)
    {
        Map<String, Object> data = new LinkedHashMap<>();
        JSONObject payload = res.getJSONObject("data");
        JSONObject app = payload != null ? payload.getJSONObject("app") : null;
        if (app != null)
        {
            data.put("appToken", app.getString("app_token"));
            data.put("name", app.getString("name"));
            data.put("folderToken", app.getString("folder_token"));
            data.put("url", app.getString("url"));
            data.put("defaultTableId", app.getString("default_table_id"));
            data.put("timeZone", app.getString("time_zone"));
        }
        return data;
    }

    private Map<String, Object> mapCreateTable(JSONObject res)
    {
        Map<String, Object> data = new LinkedHashMap<>();
        JSONObject payload = res.getJSONObject("data");
        if (payload != null)
        {
            data.put("tableId", payload.getString("table_id"));
            data.put("defaultViewId", payload.getString("default_view_id"));
            data.put("fieldIdList", payload.get("field_id_list"));
        }
        return data;
    }

    private Map<String, Object> mapTablesList(JSONObject res)
    {
        Map<String, Object> data = new LinkedHashMap<>();
        List<Map<String, Object>> items = new ArrayList<>();
        JSONObject payload = res.getJSONObject("data");
        if (payload != null)
        {
            JSONArray arr = payload.getJSONArray("items");
            if (arr != null)
            {
                for (int i = 0; i < arr.size(); i++)
                {
                    JSONObject t = arr.getJSONObject(i);
                    if (t == null)
                    {
                        continue;
                    }
                    Map<String, Object> item = new LinkedHashMap<>();
                    item.put("tableId", firstNonBlank(t.getString("table_id"), t.getString("tableId")));
                    item.put("name", t.getString("name"));
                    items.add(item);
                }
            }
            data.put("hasMore", payload.getBooleanValue("has_more"));
            data.put("pageToken", payload.getString("page_token"));
        }
        data.put("items", items);
        return data;
    }

    private Map<String, Object> mapFieldsList(JSONObject res)
    {
        Map<String, Object> data = new LinkedHashMap<>();
        List<Map<String, Object>> items = new ArrayList<>();
        JSONObject payload = res.getJSONObject("data");
        if (payload != null)
        {
            JSONArray arr = payload.getJSONArray("items");
            if (arr != null)
            {
                for (int i = 0; i < arr.size(); i++)
                {
                    JSONObject f = arr.getJSONObject(i);
                    if (f == null)
                    {
                        continue;
                    }
                    Map<String, Object> item = new LinkedHashMap<>();
                    item.put("fieldId", firstNonBlank(f.getString("field_id"), f.getString("fieldId")));
                    item.put("fieldName", firstNonBlank(f.getString("field_name"), f.getString("fieldName")));
                    item.put("type", f.get("type"));
                    item.put("uiType", firstNonBlank(f.getString("ui_type"), f.getString("uiType")));
                    item.put("property", f.get("property"));
                    items.add(item);
                }
            }
            data.put("hasMore", payload.getBooleanValue("has_more"));
            data.put("pageToken", payload.getString("page_token"));
        }
        data.put("items", items);
        return data;
    }

    private Map<String, Object> mapRecord(JSONObject res)
    {
        Map<String, Object> data = new LinkedHashMap<>();
        JSONObject payload = res.getJSONObject("data");
        JSONObject record = payload != null ? payload.getJSONObject("record") : null;
        if (record == null && payload != null)
        {
            record = payload;
        }
        if (record != null)
        {
            data.put("recordId", firstNonBlank(record.getString("record_id"), record.getString("recordId")));
            Object fields = record.get("fields");
            data.put("fields", fields != null ? fields : new JSONObject());
        }
        else
        {
            data.put("recordId", null);
            data.put("fields", new JSONObject());
        }
        return data;
    }

    private Map<String, Object> mapBatchCreate(JSONObject res)
    {
        Map<String, Object> data = new LinkedHashMap<>();
        List<Map<String, Object>> items = new ArrayList<>();
        JSONObject payload = res.getJSONObject("data");
        if (payload != null)
        {
            JSONArray arr = payload.getJSONArray("records");
            if (arr != null)
            {
                for (int i = 0; i < arr.size(); i++)
                {
                    JSONObject r = arr.getJSONObject(i);
                    if (r == null)
                    {
                        continue;
                    }
                    Map<String, Object> item = new LinkedHashMap<>();
                    item.put("recordId", firstNonBlank(r.getString("record_id"), r.getString("recordId")));
                    item.put("fields", r.get("fields") != null ? r.get("fields") : new JSONObject());
                    items.add(item);
                }
            }
        }
        data.put("items", items);
        data.put("created", items.size());
        return data;
    }

    private Map<String, Object> mapBatchDelete(JSONObject res)
    {
        Map<String, Object> data = new LinkedHashMap<>();
        JSONObject payload = res.getJSONObject("data");
        List<String> records = new ArrayList<>();
        if (payload != null)
        {
            JSONArray arr = payload.getJSONArray("records");
            if (arr != null)
            {
                for (int i = 0; i < arr.size(); i++)
                {
                    Object v = arr.get(i);
                    if (v instanceof JSONObject)
                    {
                        JSONObject o = (JSONObject) v;
                        String id = firstNonBlank(o.getString("record_id"), o.getString("id"));
                        if (id != null)
                        {
                            records.add(id);
                        }
                    }
                    else if (v != null)
                    {
                        records.add(String.valueOf(v));
                    }
                }
            }
        }
        data.put("recordIds", records);
        data.put("deleted", records.size());
        return data;
    }

    private Map<String, Object> mapSearchRecords(JSONObject res)
    {
        Map<String, Object> data = new LinkedHashMap<>();
        List<Map<String, Object>> items = new ArrayList<>();
        JSONObject payload = res.getJSONObject("data");
        if (payload != null)
        {
            JSONArray arr = payload.getJSONArray("items");
            if (arr != null)
            {
                for (int i = 0; i < arr.size(); i++)
                {
                    JSONObject r = arr.getJSONObject(i);
                    if (r == null)
                    {
                        continue;
                    }
                    Map<String, Object> item = new LinkedHashMap<>();
                    item.put("recordId", firstNonBlank(r.getString("record_id"), r.getString("recordId")));
                    item.put("fields", r.get("fields") != null ? r.get("fields") : new JSONObject());
                    items.add(item);
                }
            }
            data.put("hasMore", payload.getBoolean("has_more") != null
                    ? payload.getBoolean("has_more")
                    : payload.getBooleanValue("has_more"));
            data.put("pageToken", payload.getString("page_token"));
            if (payload.containsKey("total"))
            {
                data.put("total", payload.get("total"));
            }
            else
            {
                data.put("total", null);
            }
        }
        else
        {
            data.put("hasMore", false);
            data.put("pageToken", null);
            data.put("total", null);
        }
        data.put("items", items);
        return data;
    }

    private Map<String, Object> mapDelete(JSONObject res, String recordId)
    {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("deleted", 1);
        data.put("recordId", recordId);
        JSONObject payload = res.getJSONObject("data");
        if (payload != null)
        {
            Boolean deleted = payload.getBoolean("deleted");
            if (deleted != null && !deleted)
            {
                data.put("deleted", 0);
            }
            String rid = firstNonBlank(payload.getString("record_id"), payload.getString("recordId"));
            if (rid != null)
            {
                data.put("recordId", rid);
            }
        }
        return data;
    }

    private static int normalizePageSize(Integer pageSize)
    {
        if (pageSize == null || pageSize <= 0)
        {
            return DEFAULT_PAGE_SIZE;
        }
        return Math.min(pageSize, MAX_PAGE_SIZE);
    }

    private static int normalizeExportMaxRows(Integer maxRows)
    {
        if (maxRows == null || maxRows <= 0)
        {
            return DEFAULT_EXPORT_MAX_ROWS;
        }
        return Math.min(maxRows, HARD_EXPORT_MAX_ROWS);
    }

    private static void requireAppToken(FeishuBitableRequest request)
    {
        if (request == null || request.getAppToken() == null || request.getAppToken().trim().isEmpty())
        {
            throw new ServiceException("appToken 不能为空");
        }
    }

    private static void requireTableId(FeishuBitableRequest request)
    {
        if (request.getTableId() == null || request.getTableId().trim().isEmpty())
        {
            throw new ServiceException("tableId 不能为空");
        }
    }

    private static void requireRecordId(FeishuBitableRequest request)
    {
        if (request.getRecordId() == null || request.getRecordId().trim().isEmpty())
        {
            throw new ServiceException("recordId 不能为空");
        }
    }

    private static String firstNonBlank(String a, String b)
    {
        if (a != null && !a.isEmpty())
        {
            return a;
        }
        if (b != null && !b.isEmpty())
        {
            return b;
        }
        return null;
    }
}
