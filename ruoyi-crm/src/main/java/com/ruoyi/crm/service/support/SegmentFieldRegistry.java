package com.ruoyi.crm.service.support;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import com.ruoyi.crm.domain.vo.SegmentEnumValueVo;
import com.ruoyi.crm.domain.vo.SegmentFieldMetaVo;

/**
 * 分群字段白名单（真实字段名，无别名映射）
 */
public final class SegmentFieldRegistry
{
    public static final String SOURCE_METRIC = "metric";
    public static final String SOURCE_ATTRIBUTE = "attribute";
    public static final String SOURCE_REACHABILITY = "reachability";
    public static final String SOURCE_TAG = "tag";

    public static final String TYPE_NUMBER = "number";
    public static final String TYPE_DATETIME = "datetime";
    public static final String TYPE_STRING = "string";
    public static final String TYPE_ENUM = "enum";
    public static final String TYPE_REACHABILITY = "reachability";
    public static final String TYPE_WECHAT_MP_MENU = "wechat_mp_menu";

    public static final String FIELD_WECHAT_MP_MENU_ARTICLE_VIEWED = "wechatMpMenuArticleViewed";
    public static final String FIELD_CRM_TAG = "crmTag";

    private static final List<String> NUMBER_OPS = Arrays.asList(
            SegmentOperator.EQ, SegmentOperator.NE, SegmentOperator.GT, SegmentOperator.GTE,
            SegmentOperator.LT, SegmentOperator.LTE, SegmentOperator.BETWEEN,
            SegmentOperator.IS_NULL, SegmentOperator.IS_NOT_NULL);

    private static final List<String> DATETIME_OPS = Arrays.asList(
            SegmentOperator.EQ, SegmentOperator.NE, SegmentOperator.GT, SegmentOperator.GTE,
            SegmentOperator.LT, SegmentOperator.LTE, SegmentOperator.BETWEEN,
            SegmentOperator.IS_NULL, SegmentOperator.IS_NOT_NULL,
            SegmentOperator.WITHIN_LAST_DAYS, SegmentOperator.BEFORE_DAYS_AGO);

    private static final List<String> STRING_OPS = Arrays.asList(
            SegmentOperator.EQ, SegmentOperator.NE, SegmentOperator.IN, SegmentOperator.NOT_IN,
            SegmentOperator.CONTAINS, SegmentOperator.IS_NULL, SegmentOperator.IS_NOT_NULL);

    private static final List<String> ENUM_OPS = Arrays.asList(
            SegmentOperator.IN, SegmentOperator.NOT_IN,
            SegmentOperator.IS_NULL, SegmentOperator.IS_NOT_NULL);

    private static final List<String> TAG_OPS = Arrays.asList(
            SegmentOperator.IN, SegmentOperator.NOT_IN, SegmentOperator.CONTAINS_ALL,
            SegmentOperator.IS_NULL, SegmentOperator.IS_NOT_NULL);

    private static final List<String> REACHABILITY_OPS = Arrays.asList(
            SegmentOperator.EXISTS, SegmentOperator.NOT_EXISTS);

    private static final List<SegmentEnumValueVo> LIFECYCLE_ENUM = Arrays.asList(
            new SegmentEnumValueVo("lead", "线索"),
            new SegmentEnumValueVo("opportunity", "商机"),
            new SegmentEnumValueVo("customer", "客户"),
            new SegmentEnumValueVo("churned", "流失"));

    private static final List<SegmentEnumValueVo> AI_RISK_ENUM = Arrays.asList(
            new SegmentEnumValueVo("low", "低"),
            new SegmentEnumValueVo("medium", "中"),
            new SegmentEnumValueVo("high", "高"));

    private static final List<SegmentEnumValueVo> GENDER_ENUM = Arrays.asList(
            new SegmentEnumValueVo("男", "男"),
            new SegmentEnumValueVo("女", "女"));

    private static final Map<String, SegmentFieldDefinition> FIELDS = new LinkedHashMap<>();
    private static final List<SegmentFieldDefinition> FIELD_LIST;

    static
    {
        registerMetric("lifecycleStage", "lifecycle_stage", TYPE_ENUM, "生命周期阶段", LIFECYCLE_ENUM);
        registerMetric("totalRevenue", "total_revenue", TYPE_NUMBER, "累计消费金额", null);
        registerMetric("avgDealAmount", "avg_deal_amount", TYPE_NUMBER, "平均客单价", null);
        registerMetric("appointmentCount", "appointment_count", TYPE_NUMBER, "预约次数", null);
        registerMetric("appointmentNoshowCount", "appointment_noshow_count", TYPE_NUMBER, "预约爽约次数", null);
        registerMetric("visitCount", "visit_count", TYPE_NUMBER, "到店次数", null);
        registerMetric("opportunityCount", "opportunity_count", TYPE_NUMBER, "商机数", null);
        registerMetric("opportunityWonCount", "opportunity_won_count", TYPE_NUMBER, "成交商机数", null);
        registerMetric("leadCount", "lead_count", TYPE_NUMBER, "线索数", null);
        registerMetric("totalEventCount", "total_event_count", TYPE_NUMBER, "事件总数", null);
        registerMetric("aiScoreLatest", "ai_score_latest", TYPE_NUMBER, "AI 评分", null);
        registerMetric("churnRiskScore", "churn_risk_score", TYPE_NUMBER, "流失风险分", null);
        registerMetric("aiRiskLevel", "ai_risk_level", TYPE_ENUM, "AI 风险等级", AI_RISK_ENUM);
        registerMetric("aiIntentLatest", "ai_intent_latest", TYPE_STRING, "AI 意图", null);
        registerMetric("firstVisitAt", "first_visit_at", TYPE_DATETIME, "首次到店时间", null);
        registerMetric("lastVisitAt", "last_visit_at", TYPE_DATETIME, "最近到店时间", null);
        registerMetric("lastEventAt", "last_event_at", TYPE_DATETIME, "最近事件时间", null);
        registerMetric("lastPaymentAt", "last_payment_at", TYPE_DATETIME, "最近支付时间", null);
        registerMetric("wechatMpFollowedAt", "wechat_mp_followed_at", TYPE_DATETIME, "公众号关注时间", null);
        registerMetric("wecomAddedAt", "wecom_added_at", TYPE_DATETIME, "企微添加时间", null);
        registerMetric("leadCreatedAt", "lead_created_at", TYPE_DATETIME, "线索创建时间", null);
        registerMetric("wechatMpArticleRead30d", "wechat_mp_article_read_30d", TYPE_NUMBER, "公众号30天阅读数", null);
        registerMetric("wecomActiveDays30d", "wecom_active_days_30d", TYPE_NUMBER, "企微30天活跃天数", null);
        registerMetric("wecomMessageCount", "wecom_message_count", TYPE_NUMBER, "企微消息数", null);
        register(new SegmentFieldDefinition(SOURCE_METRIC, FIELD_WECHAT_MP_MENU_ARTICLE_VIEWED, null,
                TYPE_WECHAT_MP_MENU, "查看过公众号菜单文章", REACHABILITY_OPS, null));

        registerAttribute("originalSource", "original_source", TYPE_STRING, "终身初始来源", null);
        registerAttribute("gender", "gender", TYPE_ENUM, "性别", GENDER_ENUM);
        registerAttribute("location", "location", TYPE_STRING, "来源地", null);
        registerAttribute("owner", "owner", TYPE_STRING, "负责人", null);
        registerAttribute("userId", "user_id", TYPE_NUMBER, "负责人用户ID", null);

        registerReachability("wecom_external_userid", "企微可触达");
        registerReachability("wechat_mp_openid", "公众号 OpenID");
        registerReachability("wechat_unionid", "微信 UnionID");
        registerReachability("wechat_miniapp_openid", "小程序 OpenID");
        registerReachability("wechat_kf_openid", "微信客服 OpenID");
        registerReachability("mobile_phone", "手机号可触达");

        register(new SegmentFieldDefinition(SOURCE_TAG, FIELD_CRM_TAG, "tag", TYPE_ENUM, "客户标签",
                TAG_OPS, Collections.emptyList()));

        FIELD_LIST = Collections.unmodifiableList(new ArrayList<>(FIELDS.values()));
    }

    private SegmentFieldRegistry()
    {
    }

    private static void registerMetric(String field, String column, String type, String label,
            List<SegmentEnumValueVo> enumValues)
    {
        List<String> ops = opsForType(type);
        register(new SegmentFieldDefinition(SOURCE_METRIC, field, column, type, label, ops, enumValues));
    }

    private static void registerAttribute(String field, String column, String type, String label,
            List<SegmentEnumValueVo> enumValues)
    {
        List<String> ops = opsForType(type);
        register(new SegmentFieldDefinition(SOURCE_ATTRIBUTE, field, column, type, label, ops, enumValues));
    }

    private static void registerReachability(String identityType, String label)
    {
        register(new SegmentFieldDefinition(SOURCE_REACHABILITY, identityType, identityType, TYPE_REACHABILITY,
                label, REACHABILITY_OPS, null));
    }

    private static void register(SegmentFieldDefinition definition)
    {
        FIELDS.put(definition.registryKey(), definition);
    }

    private static List<String> opsForType(String type)
    {
        switch (type)
        {
            case TYPE_NUMBER:
                return NUMBER_OPS;
            case TYPE_DATETIME:
                return DATETIME_OPS;
            case TYPE_STRING:
                return STRING_OPS;
            case TYPE_ENUM:
                return ENUM_OPS;
            case TYPE_REACHABILITY:
            case TYPE_WECHAT_MP_MENU:
                return REACHABILITY_OPS;
            default:
                return Collections.emptyList();
        }
    }

    public static SegmentFieldDefinition get(String source, String field)
    {
        if (source == null || field == null)
        {
            return null;
        }
        return FIELDS.get(source.trim() + ":" + field.trim());
    }

    public static List<SegmentFieldDefinition> allFields()
    {
        return FIELD_LIST;
    }

    public static List<SegmentFieldMetaVo> allFieldMeta()
    {
        List<SegmentFieldMetaVo> result = new ArrayList<>(FIELD_LIST.size());
        for (SegmentFieldDefinition field : FIELD_LIST)
        {
            SegmentFieldMetaVo vo = new SegmentFieldMetaVo();
            vo.setSource(field.getSource());
            vo.setField(field.getField());
            vo.setLabel(field.getLabel());
            vo.setType(field.getFieldType());
            vo.setOperators(new ArrayList<>(field.getOperators()));
            if (!field.getEnumValues().isEmpty())
            {
                vo.setEnumValues(new ArrayList<>(field.getEnumValues()));
            }
            result.add(vo);
        }
        return result;
    }
}
