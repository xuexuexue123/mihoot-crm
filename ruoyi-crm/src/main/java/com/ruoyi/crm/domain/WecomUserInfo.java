package com.ruoyi.crm.domain;

/**
 * 企业微信成员信息（来自 user/get 接口）
 *
 * @author ruoyi
 */
public class WecomUserInfo
{
    /** 成员UserID */
    private String userid;

    /** 成员名称 */
    private String name;

    /** 手机号码 */
    private String mobile;

    /** 邮箱 */
    private String email;

    /** 所在部门ID列表 */
    private Long[] department;

    /** 职位 */
    private String position;

    /** 性别 */
    private String gender;

    /** 头像URL */
    private String avatar;

    public String getUserid()
    {
        return userid;
    }

    public void setUserid(String userid)
    {
        this.userid = userid;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getMobile()
    {
        return mobile;
    }

    public void setMobile(String mobile)
    {
        this.mobile = mobile;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public Long[] getDepartment()
    {
        return department;
    }

    public void setDepartment(Long[] department)
    {
        this.department = department;
    }

    public String getPosition()
    {
        return position;
    }

    public void setPosition(String position)
    {
        this.position = position;
    }

    public String getGender()
    {
        return gender;
    }

    public void setGender(String gender)
    {
        this.gender = gender;
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
