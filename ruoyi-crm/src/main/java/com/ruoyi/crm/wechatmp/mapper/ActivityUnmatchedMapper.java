package com.ruoyi.crm.wechatmp.mapper;

import com.ruoyi.crm.wechatmp.domain.ActivityUnmatched;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Activity 未匹配身份记录 Mapper
 *
 * @author luohd
 * @date 2026-06-18
 */
@Mapper
public interface ActivityUnmatchedMapper
{
    @Select("SELECT * FROM crm_activity_unmatched " +
            "WHERE next_retry_at <= #{now} AND abandoned = false " +
            "ORDER BY create_time ASC LIMIT #{limit}")
    List<ActivityUnmatched> selectPendingRetry(
        @Param("now") LocalDateTime now,
        @Param("limit") int limit
    );

    @Insert("INSERT INTO crm_activity_unmatched " +
            "(pulse_id, identity_type, identity_value, union_id, union_id_source, app_id, " +
            " retry_count, max_retries, next_retry_at, raw_payload) " +
            "VALUES (#{pulseId}, #{identityType}, #{identityValue}, #{unionId}, #{unionIdSource}, #{appId}, " +
            " 0, 12, #{nextRetryAt}, #{rawPayload})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ActivityUnmatched record);

    @Update("UPDATE crm_activity_unmatched " +
            "SET retry_count = #{retryCount}, next_retry_at = #{nextRetryAt}, update_time = now() " +
            "WHERE id = #{id}")
    int updateRetry(
        @Param("id") Long id,
        @Param("retryCount") int retryCount,
        @Param("nextRetryAt") LocalDateTime nextRetryAt
    );

    @Update("UPDATE crm_activity_unmatched " +
            "SET abandoned = true, update_time = now() WHERE id = #{id}")
    int markAbandoned(@Param("id") Long id);

    @Update("UPDATE crm_activity_unmatched " +
            "SET union_id = #{unionId}, union_id_source = #{unionIdSource}, update_time = now() " +
            "WHERE id = #{id}")
    int updateUnionId(@Param("id") Long id, @Param("unionId") String unionId,
            @Param("unionIdSource") String unionIdSource);

    @Select("SELECT COUNT(*) FROM crm_activity_unmatched " +
            "WHERE abandoned = false AND matched_at IS NULL")
    int countPending();

    @Update("UPDATE crm_activity_unmatched " +
            "SET matched_at = now(), contact_id = #{contactId}, update_time = now() " +
            "WHERE id = #{id}")
    int markMatched(@Param("id") Long id, @Param("contactId") String contactId);

    @Select("SELECT * FROM crm_activity_unmatched " +
            "WHERE union_id = #{unionId} AND abandoned = false AND matched_at IS NULL " +
            "ORDER BY create_time ASC")
    List<ActivityUnmatched> selectPendingByUnionId(@Param("unionId") String unionId);

    @Select("SELECT * FROM crm_activity_unmatched " +
            "WHERE identity_type = 'wechat_mp_openid' AND identity_value = #{openId} " +
            "AND abandoned = false AND matched_at IS NULL " +
            "AND (#{appId} IS NULL OR app_id = #{appId}) " +
            "ORDER BY create_time ASC")
    List<ActivityUnmatched> selectPendingByOpenId(
            @Param("openId") String openId, @Param("appId") String appId);
}
