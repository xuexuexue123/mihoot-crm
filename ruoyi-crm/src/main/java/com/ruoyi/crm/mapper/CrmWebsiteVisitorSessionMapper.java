package com.ruoyi.crm.mapper;

import org.apache.ibatis.annotations.Param;

import com.ruoyi.crm.domain.CrmWebsiteVisitorSession;

/**
 * 官网访客会话 Mapper
 */
public interface CrmWebsiteVisitorSessionMapper
{
    CrmWebsiteVisitorSession selectBySessionId(@Param("sessionId") String sessionId);

    /**
     * 插入会话行；若 session_id 已存在则仅刷新 update_time（不覆盖已绑定主体）
     */
    int upsertSession(CrmWebsiteVisitorSession session);

    /**
     * 绑定主体；仅当原先未绑定或同主体时更新 bound_at（首次绑定写 now）
     */
    int bindSubject(@Param("sessionId") String sessionId,
            @Param("subjectType") String subjectType,
            @Param("subjectId") String subjectId);

    /**
     * Lead 转 Contact 时迁移 session 绑定主体
     */
    int migrateSubjectLeadToContact(@Param("oldLeadId") String oldLeadId,
            @Param("newContactId") String newContactId);
}
