package com.ruoyi.crm.domain.dto;

/**
 * 活动效果查询参数
 */
public class EventPerformanceQuery
{
    private Integer pageNum = 1;
    private Integer pageSize = 20;
    private String stage;
    private String dateFrom;
    private String dateTo;
    private String sortBy = "createTime";
    private String sortOrder = "desc";
    private Integer attributionWindowDays;
    private String roiBasis = "lifetime";
    private Boolean fresh = false;
    private String sessionId;

    public Integer getPageNum() { return pageNum; }
    public void setPageNum(Integer pageNum) { this.pageNum = pageNum; }
    public Integer getPageSize() { return pageSize; }
    public void setPageSize(Integer pageSize) { this.pageSize = pageSize; }
    public String getStage() { return stage; }
    public void setStage(String stage) { this.stage = stage; }
    public String getDateFrom() { return dateFrom; }
    public void setDateFrom(String dateFrom) { this.dateFrom = dateFrom; }
    public String getDateTo() { return dateTo; }
    public void setDateTo(String dateTo) { this.dateTo = dateTo; }
    public String getSortBy() { return sortBy; }
    public void setSortBy(String sortBy) { this.sortBy = sortBy; }
    public String getSortOrder() { return sortOrder; }
    public void setSortOrder(String sortOrder) { this.sortOrder = sortOrder; }
    public Integer getAttributionWindowDays() { return attributionWindowDays; }
    public void setAttributionWindowDays(Integer attributionWindowDays) { this.attributionWindowDays = attributionWindowDays; }
    public String getRoiBasis() { return roiBasis; }
    public void setRoiBasis(String roiBasis) { this.roiBasis = roiBasis; }
    public Boolean getFresh() { return fresh; }
    public void setFresh(Boolean fresh) { this.fresh = fresh; }
    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
}
