package com.ruoyi.crm.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class CrmOutreachlead extends CrmLead {

    private static final long serialVersionUID = 1L;

    private String tenantId;

  /** 活动报名签名 token，仅请求反序列化用，不入库 */
  @JsonProperty("registrationToken")
  private transient String registrationToken;

  /** H5 报名目标场次，仅请求反序列化用，不入库 */
  @JsonProperty("sessionId")
  private transient String sessionId;

  /** 浏览器 Cookie _sess（访客会话），与场次 sessionId 不同；仅请求用，不入库 */
  @JsonProperty("_sessionId")
  private transient String _sessionId;

  /** Cookie _pv_hist 兜底浏览历史；仅请求用，不入库 */
  @JsonProperty("_browseHistory")
  private transient String _browseHistory;

  public String getSessionId()
  {
    return sessionId;
  }

  public void setSessionId(String sessionId)
  {
    this.sessionId = sessionId;
  }

  public String get_sessionId()
  {
    return _sessionId;
  }

  public void set_sessionId(String _sessionId)
  {
    this._sessionId = _sessionId;
  }

  public String get_browseHistory()
  {
    return _browseHistory;
  }

  public void set_browseHistory(String _browseHistory)
  {
    this._browseHistory = _browseHistory;
  }

  public String getRegistrationToken()
  {
    return registrationToken;
  }

  public void setRegistrationToken(String registrationToken)
  {
    this.registrationToken = registrationToken;
  }


    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }


    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("leadId", getLeadId())
                .append("leadType", getLeadType())
                .append("optyId", getOptyId())
                .append("optyName", getOptyName())
                .append("orgId", getOrgId())
                .append("leadName", getLeadName())
                .append("fullName", getFullName())
                .append("owner", getOwner())
                .append("contactId", getContactId())
                .append("contactName", getContactName())
                .append("stage", getStage())
                .append("workPhone", getWorkPhone())
                .append("mobilePhone", getMobilePhone())
                .append("workEmail", getWorkEmail())
                .append("leadSource", getLeadSource())
                .append("leadRating", getLeadRating())
                .append("website", getWebsite())
                .append("territory", getTerritory())
                .append("industry", getIndustry())
                .append("employeeCount", getEmployeeCount())
                .append("revenue", getRevenue())
                .append("description", getDescription())
                .append("daysInactive", getDaysInactive())
                .append("partner", getPartner())
                .append("convertTime", getConvertTime())
                .append("createBy", getCreateBy())
                .append("createTime", getCreateTime())
                .append("updateBy", getUpdateBy())
                .append("updateTime", getUpdateTime())
                .append("tenantId", getTenantId())
                .toString();
    }
}
