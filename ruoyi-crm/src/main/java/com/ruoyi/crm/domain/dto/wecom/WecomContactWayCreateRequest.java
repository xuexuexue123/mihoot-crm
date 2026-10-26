package com.ruoyi.crm.domain.dto.wecom;

import java.util.List;

/**
 * 对应企微 externalcontact/add_contact_way
 */
public class WecomContactWayCreateRequest
{
    private Integer type;
    private Integer scene;
    private String remark;
    /** 归因参数 */
    private String state;
    private List<String> user;
    private Boolean skipVerify;
    private String welcomeText;

    public Integer getType()
    {
        return type;
    }

    public void setType(Integer type)
    {
        this.type = type;
    }

    public Integer getScene()
    {
        return scene;
    }

    public void setScene(Integer scene)
    {
        this.scene = scene;
    }

    public String getRemark()
    {
        return remark;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
    }

    public String getState()
    {
        return state;
    }

    public void setState(String state)
    {
        this.state = state;
    }

    public List<String> getUser()
    {
        return user;
    }

    public void setUser(List<String> user)
    {
        this.user = user;
    }

    public Boolean getSkipVerify()
    {
        return skipVerify;
    }

    public void setSkipVerify(Boolean skipVerify)
    {
        this.skipVerify = skipVerify;
    }

    public String getWelcomeText()
    {
        return welcomeText;
    }

    public void setWelcomeText(String welcomeText)
    {
        this.welcomeText = welcomeText;
    }
}
