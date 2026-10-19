package com.ruoyi.crm.domain.dto;

import java.util.Date;
import lombok.Data;

/**
 * Activity Event DTO — 统一事件传输对象
 * 各渠道回调经 EventProducer 映射后，统一由此 DTO 承载
 *
 * @author luohd
 * @date 2026-06-16
 */
@Data
public class ActivityEventDTO
{
    /** 渠道: wecom|wechat_mp|wechat_miniapp */
    private String channel;

    /** 渠道原始事件ID，用于幂等去重 */
    private String channelEventId;

    /** 事件类型: channel.wecom.added 等 */
    private String eventType;

    /** 事件分类: channel|crm|ai|engagement|conversion */
    private String eventCategory;

    /** 主体类型: Contact|Lead|... */
    private String subjectType;

    /** 主体ID: contact_id */
    private String subjectId;

    /** 重要性: 10-100 */
    private Integer importance;

    /** 事件发生时间 */
    private Date startedDate;

    /** 事件来源: wecom_callback|wechat_mp_callback|... */
    private String source;

    /** 结构化扩展信息 (JSON 字符串) */
    private String payload;

    /** 技术上下文元数据 (JSON 字符串) */
    private String context;

    /** 文本摘要 */
    private String messageData;

    /** 事件显示名（可选；缺省时 buildActivity 回退 messageData） */
    private String eventName;

    /** 操作人 */
    private String createBy;

    /** 活动归属用户（可选；供 KPI ownerId 映射；缺省由 insertTimelineActivity 填登录人） */
    private Long userId;

    /** 活动归属部门（可选） */
    private Long deptId;

    /** 兼容 pulse 幂等键（可选；缺省用 activityId） */
    private String pulseId;

    /** 兼容旧活动列表查询 */
    private String relateType;

    private String relateId;

    private String relateName;
}
