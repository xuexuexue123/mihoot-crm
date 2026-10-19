package com.ruoyi.crm.domain.dto;

import com.ruoyi.common.core.domain.BaseEntity;

/**
 * WhatsApp 会话列表查询（scope = open | mine | team）
 */
public class CrmWhatsappConversationQuery extends BaseEntity
{
    private String tenantId;
    /** open | mine | team；默认 open */
    private String scope = "open";
    /** 兼容旧客户端：仅当未传 scope 语义时使用 */
    private String status;
    private String phoneNumberId;
    private String keyword;
    private Long currentUserId;
    /** 校验团队数据权限时按 id 过滤 */
    private Long conversationId;

    public String getTenantId()
    {
        return tenantId;
    }

    public void setTenantId(String tenantId)
    {
        this.tenantId = tenantId;
    }

    public String getScope()
    {
        return scope;
    }

    public void setScope(String scope)
    {
        this.scope = scope;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getPhoneNumberId()
    {
        return phoneNumberId;
    }

    public void setPhoneNumberId(String phoneNumberId)
    {
        this.phoneNumberId = phoneNumberId;
    }

    public String getKeyword()
    {
        return keyword;
    }

    public void setKeyword(String keyword)
    {
        this.keyword = keyword;
    }

    public Long getCurrentUserId()
    {
        return currentUserId;
    }

    public void setCurrentUserId(Long currentUserId)
    {
        this.currentUserId = currentUserId;
    }

    public Long getConversationId()
    {
        return conversationId;
    }

    public void setConversationId(Long conversationId)
    {
        this.conversationId = conversationId;
    }
}
