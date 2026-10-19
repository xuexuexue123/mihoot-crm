package com.ruoyi.crm.domain.dto.wecom;

/**
 * 企业微信 userid → CRM 用户解析结果
 * 供 bot-server 调用 resolve-user 接口后获取真实用户身份
 */
public class ResolveUserResult
{
    private Long userId;
    private String userName;
    private String nickName;
    private Long deptId;
    private String avatar;

    public ResolveUserResult() {}

    public ResolveUserResult(Long userId, String userName, String nickName, Long deptId, String avatar)
    {
        this.userId = userId;
        this.userName = userName;
        this.nickName = nickName;
        this.deptId = deptId;
        this.avatar = avatar;
    }

    public Long getUserId()
    {
        return userId;
    }

    public void setUserId(Long userId)
    {
        this.userId = userId;
    }

    public String getUserName()
    {
        return userName;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    public String getNickName()
    {
        return nickName;
    }

    public void setNickName(String nickName)
    {
        this.nickName = nickName;
    }

    public Long getDeptId()
    {
        return deptId;
    }

    public void setDeptId(Long deptId)
    {
        this.deptId = deptId;
    }

    public String getAvatar()
    {
        return avatar;
    }

    public void setAvatar(String avatar)
    {
        this.avatar = avatar;
    }
}
