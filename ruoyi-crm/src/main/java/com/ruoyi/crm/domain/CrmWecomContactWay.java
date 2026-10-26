package com.ruoyi.crm.domain;

import com.ruoyi.common.core.domain.BaseEntity;

public class CrmWecomContactWay extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long id;
    private String configId;
    private String state;
    private Integer scene;
    private Integer type;
    private Boolean skipVerify;
    private String userListJson;
    private String qrCode;
    private String welcomeText;
    private String rawRequestJson;
    private String rawResponseJson;
    /** 列表查询：state 前缀 */
    private String statePrefix;
    /** 列表查询 / 冗余归因 */
    private String crmEventId;

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getConfigId()
    {
        return configId;
    }

    public void setConfigId(String configId)
    {
        this.configId = configId;
    }

    public String getState()
    {
        return state;
    }

    public void setState(String state)
    {
        this.state = state;
    }

    public Integer getScene()
    {
        return scene;
    }

    public void setScene(Integer scene)
    {
        this.scene = scene;
    }

    public Integer getType()
    {
        return type;
    }

    public void setType(Integer type)
    {
        this.type = type;
    }

    public Boolean getSkipVerify()
    {
        return skipVerify;
    }

    public void setSkipVerify(Boolean skipVerify)
    {
        this.skipVerify = skipVerify;
    }

    public String getUserListJson()
    {
        return userListJson;
    }

    public void setUserListJson(String userListJson)
    {
        this.userListJson = userListJson;
    }

    public String getQrCode()
    {
        return qrCode;
    }

    public void setQrCode(String qrCode)
    {
        this.qrCode = qrCode;
    }

    public String getWelcomeText()
    {
        return welcomeText;
    }

    public void setWelcomeText(String welcomeText)
    {
        this.welcomeText = welcomeText;
    }

    public String getRawRequestJson()
    {
        return rawRequestJson;
    }

    public void setRawRequestJson(String rawRequestJson)
    {
        this.rawRequestJson = rawRequestJson;
    }

    public String getRawResponseJson()
    {
        return rawResponseJson;
    }

    public void setRawResponseJson(String rawResponseJson)
    {
        this.rawResponseJson = rawResponseJson;
    }

    public String getStatePrefix()
    {
        return statePrefix;
    }

    public void setStatePrefix(String statePrefix)
    {
        this.statePrefix = statePrefix;
    }

    public String getCrmEventId()
    {
        return crmEventId;
    }

    public void setCrmEventId(String crmEventId)
    {
        this.crmEventId = crmEventId;
    }
}
