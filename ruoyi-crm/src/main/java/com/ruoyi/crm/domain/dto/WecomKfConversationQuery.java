package com.ruoyi.crm.domain.dto;

import java.util.List;

import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 微信客服坐席会话列表查询。
 */
public class WecomKfConversationQuery extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** open | mine | team */
    private String scope;
    private String corpId;
    private String openKfid;
    private List<String> openKfids;
    private String keyword;
    private Long currentUserId;
    private Long conversationId;

    public String getScope() { return scope; }
    public void setScope(String scope) { this.scope = scope; }

    public String getCorpId() { return corpId; }
    public void setCorpId(String corpId) { this.corpId = corpId; }

    public String getOpenKfid() { return openKfid; }
    public void setOpenKfid(String openKfid) { this.openKfid = openKfid; }

    public List<String> getOpenKfids() { return openKfids; }
    public void setOpenKfids(List<String> openKfids) { this.openKfids = openKfids; }

    public String getKeyword() { return keyword; }
    public void setKeyword(String keyword) { this.keyword = keyword; }

    public Long getCurrentUserId() { return currentUserId; }
    public void setCurrentUserId(Long currentUserId) { this.currentUserId = currentUserId; }

    public Long getConversationId() { return conversationId; }
    public void setConversationId(Long conversationId) { this.conversationId = conversationId; }
}
