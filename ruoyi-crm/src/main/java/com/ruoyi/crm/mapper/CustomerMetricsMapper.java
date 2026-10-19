package com.ruoyi.crm.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.crm.domain.CrmContact;
import com.ruoyi.crm.domain.CustomerMetrics;

/**
 * 客户聚合指标 Mapper 接口
 *
 * @author luohd
 * @date 2026-06-16
 */
public interface CustomerMetricsMapper
{
    /**
     * 按 contactId 查询指标快照
     */
    CustomerMetrics selectByContactId(String contactId);

    /**
     * 插入或更新指标（ON CONFLICT DO UPDATE）
     */
    int upsert(CustomerMetrics metrics);

    /**
     * 批量插入或更新指标
     */
    int batchUpsert(List<CustomerMetrics> list);

    /**
     * 批量覆写式插入或更新指标（全量重算用，直接覆写已有行）
     * 与 batchUpsert 不同：不使用 COALESCE 保留旧值，全部以新值覆盖
     */
    int batchUpsertOverwrite(List<CustomerMetrics> list);

    /**
     * 查询所有 contact_id（用于全量重算）
     */
    List<String> selectAllContactIds();

    /**
     * 查询最近一次企微消息超过指定天数的联系人（wecom_last_message_at IS NULL 或 早于阈值）
     */
    List<CrmContact> selectContactsWithNoRecentWecomMessage(@Param("daysThreshold") Integer daysThreshold);

    /**
     * 查询企微添加周年庆联系人（wecom_added_at 月日与今天相同，但不是今年）
     */
    List<CrmContact> selectContactsByWecomAddedAnniversary();

    /**
     * 查询N天前添加企微的联系人（wecom_added_at::date = CURRENT_DATE - N 天）
     */
    List<CrmContact> selectContactsByWecomAddedDaysAgo(@Param("daysAfter") Integer daysAfter);

    /**
     * 查询N天前关注公众号的联系人（wechat_mp_followed_at::date = CURRENT_DATE - N 天）
     */
    List<CrmContact> selectContactsByWechatMpFollowedDaysAgo(@Param("daysAfter") Integer daysAfter);
}
