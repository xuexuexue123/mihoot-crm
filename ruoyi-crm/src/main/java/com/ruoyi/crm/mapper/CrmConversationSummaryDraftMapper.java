package com.ruoyi.crm.mapper;

import org.apache.ibatis.annotations.Param;
import com.ruoyi.crm.domain.CrmConversationSummaryDraft;

public interface CrmConversationSummaryDraftMapper
{
    CrmConversationSummaryDraft selectOne(@Param("tenantId") String tenantId,
            @Param("channel") String channel, @Param("conversationKey") String conversationKey);

    int upsert(CrmConversationSummaryDraft row);

    int delete(@Param("tenantId") String tenantId, @Param("channel") String channel,
            @Param("conversationKey") String conversationKey);
}
