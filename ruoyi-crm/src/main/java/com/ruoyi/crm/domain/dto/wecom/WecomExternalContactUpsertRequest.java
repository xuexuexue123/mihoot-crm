package com.ruoyi.crm.domain.dto.wecom;

import java.util.List;

/**
 * 从 KF get_customer_info 响应持久化外部联系人信息。
 * 由 Next.js 端调用，传递 WeChat API 原始响应数据。
 */
public class WecomExternalContactUpsertRequest
{
    private String externalUserid;
    private String followUserid;
    private String rawJson;

    /** follow_user 数组，用于提取 remark / add_way / tags 等跟进人维度信息 */
    private List<FollowUserItem> followUser;

    public String getExternalUserid()
    {
        return externalUserid;
    }

    public void setExternalUserid(String externalUserid)
    {
        this.externalUserid = externalUserid;
    }

    public String getFollowUserid()
    {
        return followUserid;
    }

    public void setFollowUserid(String followUserid)
    {
        this.followUserid = followUserid;
    }

    public String getRawJson()
    {
        return rawJson;
    }

    public void setRawJson(String rawJson)
    {
        this.rawJson = rawJson;
    }

    public List<FollowUserItem> getFollowUser()
    {
        return followUser;
    }

    public void setFollowUser(List<FollowUserItem> followUser)
    {
        this.followUser = followUser;
    }

    /**
     * follow_user 数组中的单个跟进人信息。
     */
    public static class FollowUserItem
    {
        private String userid;
        private String remark;
        private String description;
        private Long createtime;
        private Integer addWay;
        private String operUserid;
        private List<String> remarkMobiles;
        private Object tags;

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

        public Long getCreatetime()
        {
            return createtime;
        }

        public void setCreatetime(Long createtime)
        {
            this.createtime = createtime;
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

        public List<String> getRemarkMobiles()
        {
            return remarkMobiles;
        }

        public void setRemarkMobiles(List<String> remarkMobiles)
        {
            this.remarkMobiles = remarkMobiles;
        }

        public Object getTags()
        {
            return tags;
        }

        public void setTags(Object tags)
        {
            this.tags = tags;
        }
    }
}
