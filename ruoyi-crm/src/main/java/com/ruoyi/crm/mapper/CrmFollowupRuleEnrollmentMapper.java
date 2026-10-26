package com.ruoyi.crm.mapper;

import java.util.Date;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.crm.domain.CrmFollowupRuleEnrollment;
import com.ruoyi.crm.domain.vo.FollowupRuleEnrollmentVo;

public interface CrmFollowupRuleEnrollmentMapper
{
    CrmFollowupRuleEnrollment selectById(@Param("enrollmentId") String enrollmentId);

    CrmFollowupRuleEnrollment selectByIdempotency(@Param("tenantId") String tenantId,
            @Param("idempotencyKey") String idempotencyKey);

    List<FollowupRuleEnrollmentVo> selectList(@Param("tenantId") String tenantId,
            @Param("ruleId") String ruleId,
            @Param("status") String status,
            @Param("subjectId") String subjectId);

    int countFiredBySubject(@Param("tenantId") String tenantId,
            @Param("ruleId") String ruleId,
            @Param("subjectType") String subjectType,
            @Param("subjectId") String subjectId);

    int countFiredSince(@Param("tenantId") String tenantId,
            @Param("ruleId") String ruleId,
            @Param("subjectType") String subjectType,
            @Param("subjectId") String subjectId,
            @Param("since") Date since);

    CrmFollowupRuleEnrollment selectPendingBySubjectAndRule(@Param("tenantId") String tenantId,
            @Param("ruleId") String ruleId,
            @Param("subjectType") String subjectType,
            @Param("subjectId") String subjectId);

    List<CrmFollowupRuleEnrollment> selectPendingBySubject(@Param("tenantId") String tenantId,
            @Param("subjectType") String subjectType,
            @Param("subjectId") String subjectId,
            @Param("triggerType") String triggerType);

    List<CrmFollowupRuleEnrollment> selectDuePending(@Param("tenantId") String tenantId,
            @Param("now") Date now,
            @Param("maxRetry") int maxRetry,
            @Param("limit") int limit);

    int insertIgnoreDuplicate(CrmFollowupRuleEnrollment row);

    int updateNextRunAt(@Param("enrollmentId") String enrollmentId, @Param("nextRunAt") Date nextRunAt);

    int markFired(@Param("enrollmentId") String enrollmentId, @Param("taskId") String taskId);

    int markSkipped(@Param("enrollmentId") String enrollmentId, @Param("skipReason") String skipReason);

    int incrementRetry(@Param("enrollmentId") String enrollmentId);

    int cancelPendingBySubject(@Param("tenantId") String tenantId,
            @Param("subjectType") String subjectType,
            @Param("subjectId") String subjectId,
            @Param("triggerType") String triggerType,
            @Param("cancelReason") String cancelReason);
}
