package com.ruoyi.crm.domain.dto.wecom;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 朋友圈发表执行者列表。
 */
public class WecomMomentSenderList
{
    @JsonProperty("user_list")
    private List<String> userList;

    @JsonProperty("department_list")
    private List<Integer> departmentList;

    public List<String> getUserList()
    {
        return userList;
    }

    public void setUserList(List<String> userList)
    {
        this.userList = userList;
    }

    public List<Integer> getDepartmentList()
    {
        return departmentList;
    }

    public void setDepartmentList(List<Integer> departmentList)
    {
        this.departmentList = departmentList;
    }
}
