package com.ruoyi.crm.domain.dto.wecom;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 企业微信「客户群列表」API 响应体。
 * 对应 externalcontact/groupchat/list 接口的完整返回。
 */
public class WecomGroupChatListResponse
{
    private Integer errcode;

    private String errmsg;

    @JsonProperty("group_chat_list")
    private List<GroupChatItem> groupChatList;

    @JsonProperty("next_cursor")
    private String nextCursor;

    /**
     * 客户群列表项。
     */
    public static class GroupChatItem
    {
        @JsonProperty("chat_id")
        private String chatId;

        private Integer status;

        public String getChatId()
        {
            return chatId;
        }

        public void setChatId(String chatId)
        {
            this.chatId = chatId;
        }

        public Integer getStatus()
        {
            return status;
        }

        public void setStatus(Integer status)
        {
            this.status = status;
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

    public List<GroupChatItem> getGroupChatList()
    {
        return groupChatList;
    }

    public void setGroupChatList(List<GroupChatItem> groupChatList)
    {
        this.groupChatList = groupChatList;
    }

    public String getNextCursor()
    {
        return nextCursor;
    }

    public void setNextCursor(String nextCursor)
    {
        this.nextCursor = nextCursor;
    }
}
