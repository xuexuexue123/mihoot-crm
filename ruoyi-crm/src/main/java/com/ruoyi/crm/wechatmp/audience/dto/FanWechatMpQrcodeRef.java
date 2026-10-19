package com.ruoyi.crm.wechatmp.audience.dto;

public class FanWechatMpQrcodeRef
{
    private Long qrcodeId;
    private String sceneStr;
    private String name;

    public Long getQrcodeId()
    {
        return qrcodeId;
    }

    public void setQrcodeId(Long qrcodeId)
    {
        this.qrcodeId = qrcodeId;
    }

    public String getSceneStr()
    {
        return sceneStr;
    }

    public void setSceneStr(String sceneStr)
    {
        this.sceneStr = sceneStr;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }
}
