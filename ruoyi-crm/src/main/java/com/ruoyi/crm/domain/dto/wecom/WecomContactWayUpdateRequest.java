package com.ruoyi.crm.domain.dto.wecom;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 活码更新请求（PUT /crm/wecom/contact-way/{id}）。
 */
public class WecomContactWayUpdateRequest
{
    private String remark;

    private String welcomeText;

    @JsonIgnore
    private boolean welcomeTextSet;

    public String getRemark()
    {
        return remark;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
    }

    public String getWelcomeText()
    {
        return welcomeText;
    }

    @JsonProperty("welcomeText")
    public void setWelcomeText(String welcomeText)
    {
        this.welcomeText = welcomeText;
        this.welcomeTextSet = true;
    }

    public boolean isWelcomeTextSet()
    {
        return welcomeTextSet;
    }
}
