package com.ruoyi.crm.domain.dto.wecom;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 企业微信「客户群详情」API 响应体。
 * 对应 externalcontact/groupchat/get 接口的完整返回。
 */
public class WecomGroupChatDetailResponse
{
    private Integer errcode;

    private String errmsg;

    @JsonProperty("group_chat")
    private WecomGroupChatDetail groupChat;

    /**
     * 客户群详情。
     */
    public static class WecomGroupChatDetail
    {
        @JsonProperty("chat_id")
        private String chatId;

        private String name;

        private String owner;

        @JsonProperty("create_time")
        private Long createTime;

        private String notice;

        @JsonProperty("member_list")
        private List<GroupChatMember> memberList;

        @JsonProperty("admin_list")
        private List<GroupChatAdmin> adminList;

        @JsonProperty("member_version")
        private String memberVersion;

        public String getChatId()
        {
            return chatId;
        }

        public void setChatId(String chatId)
        {
            this.chatId = chatId;
        }

        public String getName()
        {
            return name;
        }

        public void setName(String name)
        {
            this.name = name;
        }

        public String getOwner()
        {
            return owner;
        }

        public void setOwner(String owner)
        {
            this.owner = owner;
        }

        public Long getCreateTime()
        {
            return createTime;
        }

        public void setCreateTime(Long createTime)
        {
            this.createTime = createTime;
        }

        public String getNotice()
        {
            return notice;
        }

        public void setNotice(String notice)
        {
            this.notice = notice;
        }

        public List<GroupChatMember> getMemberList()
        {
            return memberList;
        }

        public void setMemberList(List<GroupChatMember> memberList)
        {
            this.memberList = memberList;
        }

        public List<GroupChatAdmin> getAdminList()
        {
            return adminList;
        }

        public void setAdminList(List<GroupChatAdmin> adminList)
        {
            this.adminList = adminList;
        }

        public String getMemberVersion()
        {
            return memberVersion;
        }

        public void setMemberVersion(String memberVersion)
        {
            this.memberVersion = memberVersion;
        }
    }

    /**
     * 群成员。
     */
    public static class GroupChatMember
    {
        private String userid;

        private Integer type;

        @JsonProperty("join_time")
        private Long joinTime;

        @JsonProperty("join_scene")
        private Integer joinScene;

        private GroupChatInvitor invitor;

        @JsonProperty("group_nickname")
        private String groupNickname;

        private String name;

        private String unionid;

        public String getUserid()
        {
            return userid;
        }

        public void setUserid(String userid)
        {
            this.userid = userid;
        }

        public Integer getType()
        {
            return type;
        }

        public void setType(Integer type)
        {
            this.type = type;
        }

        public Long getJoinTime()
        {
            return joinTime;
        }

        public void setJoinTime(Long joinTime)
        {
            this.joinTime = joinTime;
        }

        public Integer getJoinScene()
        {
            return joinScene;
        }

        public void setJoinScene(Integer joinScene)
        {
            this.joinScene = joinScene;
        }

        public GroupChatInvitor getInvitor()
        {
            return invitor;
        }

        public void setInvitor(GroupChatInvitor invitor)
        {
            this.invitor = invitor;
        }

        public String getGroupNickname()
        {
            return groupNickname;
        }

        public void setGroupNickname(String groupNickname)
        {
            this.groupNickname = groupNickname;
        }

        public String getName()
        {
            return name;
        }

        public void setName(String name)
        {
            this.name = name;
        }

        public String getUnionid()
        {
            return unionid;
        }

        public void setUnionid(String unionid)
        {
            this.unionid = unionid;
        }
    }

    /**
     * 邀请者信息。
     */
    public static class GroupChatInvitor
    {
        private String userid;

        public String getUserid()
        {
            return userid;
        }

        public void setUserid(String userid)
        {
            this.userid = userid;
        }
    }

    /**
     * 群管理员。
     */
    public static class GroupChatAdmin
    {
        private String userid;

        public String getUserid()
        {
            return userid;
        }

        public void setUserid(String userid)
        {
            this.userid = userid;
        }
    }

    public Integer getErrcode()
    {
        return errcode;
    }

    public void setErrcode(Integer errcode)
    {
        this.errcode = errcode;
    }

    public String getErrmsg()
    {
        return errmsg;
    }

    public void setErrmsg(String errmsg)
    {
        this.errmsg = errmsg;
    }

    public WecomGroupChatDetail getGroupChat()
    {
        return groupChat;
    }

    public void setGroupChat(WecomGroupChatDetail groupChat)
    {
        this.groupChat = groupChat;
    }
}
