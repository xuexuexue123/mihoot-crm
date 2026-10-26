package com.ruoyi.crm.feishu.dto;

import java.util.List;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;

/**
 * Bitable 代理请求体（各操作共用；按接口取所需字段）。
 */
public class FeishuBitableRequest
{
    private String appToken;
    private String tableId;
    private String recordId;
    private JSONObject fields;
    private Object filter;
    private Integer pageSize;
    private String pageToken;
    private String viewId;

    /** 创建 Base / 数据表名称 */
    private String name;
    /** 创建 Base 时目标文件夹 token（可选） */
    private String folderToken;
    /** 创建 Base 时区，如 Asia/Shanghai */
    private String timeZone;
    /** 新增数据表时的默认视图名（可选） */
    private String defaultViewName;
    /** 新增数据表时的初始字段定义（飞书 fields 原样） */
    private JSONArray tableFields;
    /** 批量新建：[{ "fields": {...} }, ...] */
    private List<JSONObject> records;
    /** 批量删除：recordId 列表 */
    private List<String> recordIds;
    /** 导出格式：csv | xlsx，默认 xlsx */
    private String format;
    /** 导出最大行数（含表头外的数据行），默认 10000，上限 20000 */
    private Integer maxRows;

    public String getAppToken()
    {
        return appToken;
    }

    public void setAppToken(String appToken)
    {
        this.appToken = appToken;
    }

    public String getTableId()
    {
        return tableId;
    }

    public void setTableId(String tableId)
    {
        this.tableId = tableId;
    }

    public String getRecordId()
    {
        return recordId;
    }

    public void setRecordId(String recordId)
    {
        this.recordId = recordId;
    }

    public JSONObject getFields()
    {
        return fields;
    }

    public void setFields(JSONObject fields)
    {
        this.fields = fields;
    }

    public Object getFilter()
    {
        return filter;
    }

    public void setFilter(Object filter)
    {
        this.filter = filter;
    }

    public Integer getPageSize()
    {
        return pageSize;
    }

    public void setPageSize(Integer pageSize)
    {
        this.pageSize = pageSize;
    }

    public String getPageToken()
    {
        return pageToken;
    }

    public void setPageToken(String pageToken)
    {
        this.pageToken = pageToken;
    }

    public String getViewId()
    {
        return viewId;
    }

    public void setViewId(String viewId)
    {
        this.viewId = viewId;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getFolderToken()
    {
        return folderToken;
    }

    public void setFolderToken(String folderToken)
    {
        this.folderToken = folderToken;
    }

    public String getTimeZone()
    {
        return timeZone;
    }

    public void setTimeZone(String timeZone)
    {
        this.timeZone = timeZone;
    }

    public String getDefaultViewName()
    {
        return defaultViewName;
    }

    public void setDefaultViewName(String defaultViewName)
    {
        this.defaultViewName = defaultViewName;
    }

    public JSONArray getTableFields()
    {
        return tableFields;
    }

    public void setTableFields(JSONArray tableFields)
    {
        this.tableFields = tableFields;
    }

    public List<JSONObject> getRecords()
    {
        return records;
    }

    public void setRecords(List<JSONObject> records)
    {
        this.records = records;
    }

    public List<String> getRecordIds()
    {
        return recordIds;
    }

    public void setRecordIds(List<String> recordIds)
    {
        this.recordIds = recordIds;
    }

    public String getFormat()
    {
        return format;
    }

    public void setFormat(String format)
    {
        this.format = format;
    }

    public Integer getMaxRows()
    {
        return maxRows;
    }

    public void setMaxRows(Integer maxRows)
    {
        this.maxRows = maxRows;
    }
}
