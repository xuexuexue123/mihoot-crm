package com.ruoyi.crm.domain.dto.wecom;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 企业微信「群聊数据统计」单日数据项。
 * 字段命名与企微 API items[] 一致。
 */
public class GroupChatDayItem
{
    /** 数据日期（Unix 秒级时间戳） */
    @JsonProperty("stat_time")
    private Long statTime;

    /** 当日指标数据 */
    private GroupChatData data;

    /**
     * 群聊每日指标数据。
     */
    public static class GroupChatData
    {
        /** 新增客户群数量 */
        @JsonProperty("new_chat_cnt")
        private Integer newChatCnt;

        /** 截至当日的客户群总数量 */
        @JsonProperty("chat_total")
        private Integer chatTotal;

        /** 截至当日有消息的客户群数量 */
        @JsonProperty("chat_has_msg")
        private Integer chatHasMsg;

        /** 当日新增客户群人数 */
        @JsonProperty("new_member_cnt")
        private Integer newMemberCnt;

        /** 截至当日的客户群总人数 */
        @JsonProperty("member_total")
        private Integer memberTotal;

        /** 截至当日有消息的客户群成员数 */
        @JsonProperty("member_has_msg")
        private Integer memberHasMsg;

        /** 截至当日客户群消息总数 */
        @JsonProperty("msg_total")
        private Integer msgTotal;

        /** 截至当日新增迁移群数（仅教培行业返回） */
        @JsonProperty("migrate_trainee_chat_cnt")
        private Integer migrateTraineeChatCnt;

        public Integer getNewChatCnt()
        {
            return newChatCnt;
        }

        public void setNewChatCnt(Integer newChatCnt)
        {
            this.newChatCnt = newChatCnt;
        }

        public Integer getChatTotal()
        {
            return chatTotal;
        }

        public void setChatTotal(Integer chatTotal)
        {
            this.chatTotal = chatTotal;
        }

        public Integer getChatHasMsg()
        {
            return chatHasMsg;
        }

        public void setChatHasMsg(Integer chatHasMsg)
        {
            this.chatHasMsg = chatHasMsg;
        }

        public Integer getNewMemberCnt()
        {
            return newMemberCnt;
        }

        public void setNewMemberCnt(Integer newMemberCnt)
        {
            this.newMemberCnt = newMemberCnt;
        }

        public Integer getMemberTotal()
        {
            return memberTotal;
        }

        public void setMemberTotal(Integer memberTotal)
        {
            this.memberTotal = memberTotal;
        }

        public Integer getMemberHasMsg()
        {
            return memberHasMsg;
        }

        public void setMemberHasMsg(Integer memberHasMsg)
        {
            this.memberHasMsg = memberHasMsg;
        }

        public Integer getMsgTotal()
        {
            return msgTotal;
        }

        public void setMsgTotal(Integer msgTotal)
        {
            this.msgTotal = msgTotal;
        }

        public Integer getMigrateTraineeChatCnt()
        {
            return migrateTraineeChatCnt;
        }

        public void setMigrateTraineeChatCnt(Integer migrateTraineeChatCnt)
        {
            this.migrateTraineeChatCnt = migrateTraineeChatCnt;
        }
    }

    @JsonProperty("stat_time")
    public Long getStatTime()
    {
        return statTime;
    }

    @JsonProperty("stat_time")
    public void setStatTime(Long statTime)
    {
        this.statTime = statTime;
    }

    public GroupChatData getData()
    {
        return data;
    }

    public void setData(GroupChatData data)
    {
        this.data = data;
    }
}
