package com.ruoyi.crm.wechatmp.dto;

/**
 * 微信公众号 user/info 用户资料快照。
 */
public class WechatMpUserProfile
{
    private String openId;
    private String unionId;
    private Boolean subscribe;
    private Long subscribeTime;
    private String subscribeScene;
    private String nickname;

    public String getOpenId()
    {
        return openId;
    }

    public void setOpenId(String openId)
    {
        this.openId = openId;
    }

    public String getUnionId()
    {
        return unionId;
    }

    public void setUnionId(String unionId)
    {
        this.unionId = unionId;
    }

    public Boolean getSubscribe()
    {
        return subscribe;
    }

    public void setSubscribe(Boolean subscribe)
    {
        this.subscribe = subscribe;
    }

    public Long getSubscribeTime()
    {
        return subscribeTime;
    }

    public void setSubscribeTime(Long subscribeTime)
    {
        this.subscribeTime = subscribeTime;
    }

    public String getSubscribeScene()
    {
        return subscribeScene;
    }

    public void setSubscribeScene(String subscribeScene)
    {
        this.subscribeScene = subscribeScene;
    }

    public String getNickname()
    {
        return nickname;
    }

    public void setNickname(String nickname)
    {
        this.nickname = nickname;
    }
}
