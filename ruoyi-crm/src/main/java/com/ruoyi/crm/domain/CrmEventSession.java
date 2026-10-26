package com.ruoyi.crm.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;

public class CrmEventSession extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private String sessionId;
    private String eventId;
    private String tenantId;
    private String name;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;

    private String location;
    private Integer capacity;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date registrationDeadline;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date checkInOpenAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date checkInCloseAt;

    private Integer sortOrder;
    private String status;

    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }
    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Date getStartTime() { return startTime; }
    public void setStartTime(Date startTime) { this.startTime = startTime; }
    public Date getEndTime() { return endTime; }
    public void setEndTime(Date endTime) { this.endTime = endTime; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }
    public Date getRegistrationDeadline() { return registrationDeadline; }
    public void setRegistrationDeadline(Date registrationDeadline) { this.registrationDeadline = registrationDeadline; }
    public Date getCheckInOpenAt() { return checkInOpenAt; }
    public void setCheckInOpenAt(Date checkInOpenAt) { this.checkInOpenAt = checkInOpenAt; }
    public Date getCheckInCloseAt() { return checkInCloseAt; }
    public void setCheckInCloseAt(Date checkInCloseAt) { this.checkInCloseAt = checkInCloseAt; }
    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
