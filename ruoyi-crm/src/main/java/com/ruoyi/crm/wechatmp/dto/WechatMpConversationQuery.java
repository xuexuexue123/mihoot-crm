package com.ruoyi.crm.wechatmp.dto;

import java.util.List;

import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 公众号坐席会话列表查询（scope = open | mine | team）
 */
public class WechatMpConversationQuery extends BaseEntity
{
    /** open | mine | team；缺省由服务层处理 */
    private String scope;
    private String appId;
    private List<String> appIds;
    private String keyword;
    private Long currentUserId;
    /** 校验团队数据权限时按 id 过滤 */
    private Long conversationId;
    /** true = 旧营销页兼容：不按 scope 过滤归属 */
    private boolean legacyAll;

    public String getScope()
    {
        return scope;
    }

    public void setScope(String scope)
    {
        this.scope = scope;
    }

    public String getAppId()
    {
        return appId;
    }

    public void setAppId(String appId)
    {
        this.appId = appId;
    }

    public List<String> getAppIds()
    {
        return appIds;
    }

    public void setAppIds(List<String> appIds)
    {
        this.appIds = appIds;
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

    public boolean isLegacyAll()
    {
        return legacyAll;
    }

    public void setLegacyAll(boolean legacyAll)
    {
        this.legacyAll = legacyAll;
    }
}
