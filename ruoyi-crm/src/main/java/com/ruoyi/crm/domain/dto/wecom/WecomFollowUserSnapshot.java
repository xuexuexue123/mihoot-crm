package com.ruoyi.crm.domain.dto.wecom;

import com.alibaba.fastjson2.JSONArray;

/**
 * 企微 {@code follow_user} / {@code follow_info} 中与当前跟进人相关的字段快照。
 */
public class WecomFollowUserSnapshot
{
    private String userid;
    private String remark;
    private String description;
    private Integer addWay;
    private String operUserid;
    private Long createtime;
    private JSONArray tags;
    private JSONArray remarkMobiles;

    public String getUserid()
    {
        return userid;
    }

    public void setUserid(String userid)
    {
        this.userid = userid;
    }

    public String getRemark()
    {
        return remark;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public Integer getAddWay()
    {
        return addWay;
    }

    public void setAddWay(Integer addWay)
    {
        this.addWay = addWay;
    }

    public String getOperUserid()
    {
        return operUserid;
    }

    public void setOperUserid(String operUserid)
    {
        this.operUserid = operUserid;
    }

    public Long getCreatetime()
    {
        return createtime;
    }

    public void setCreatetime(Long createtime)
    {
        this.createtime = createtime;
    }

    public JSONArray getTags()
    {
        return tags;
    }

    public void setTags(JSONArray tags)
    {
        this.tags = tags;
    }

    public JSONArray getRemarkMobiles()
    {
        return remarkMobiles;
    }

    public void setRemarkMobiles(JSONArray remarkMobiles)
    {
        this.remarkMobiles = remarkMobiles;
    }
}
