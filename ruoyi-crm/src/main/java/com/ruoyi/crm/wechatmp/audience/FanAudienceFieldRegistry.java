package com.ruoyi.crm.wechatmp.audience;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.ruoyi.crm.service.support.SegmentOperator;
import com.ruoyi.crm.wechatmp.audience.vo.FanAudienceFieldMetaVo;

public final class FanAudienceFieldRegistry
{
    public static final String FIELD_SUBSCRIBED = "subscribed";
    public static final String FIELD_SUBSCRIBE_TIME = "subscribeTime";
    public static final String FIELD_MENU_CLICKED = "menuClicked";
    public static final String FIELD_ARTICLE_READ = "articleRead";
    public static final String FIELD_HAS_CONTACT = "hasContact";
    public static final String FIELD_SCAN_FROM_QRCODE = "scanFromQrcode";
    public static final String FIELD_LAST_INTERACTION_WITHIN_48H = "lastInteractionWithin48h";
    public static final String FIELD_TAG_ID = "tagId";

    public static final String TYPE_BOOLEAN = "boolean";
    public static final String TYPE_DATETIME = "datetime";
    public static final String TYPE_WECHAT_MP_MENU = "wechat_mp_menu";
    public static final String TYPE_WECHAT_MP_ARTICLE = "wechat_mp_article";
    public static final String TYPE_WECHAT_MP_QRCODE = "wechat_mp_qrcode";
    public static final String TYPE_WECHAT_MP_TAG = "wechat_mp_tag";

    private static final List<String> BOOLEAN_OPS = Arrays.asList(
            SegmentOperator.EXISTS, SegmentOperator.NOT_EXISTS);

    private static final List<String> DATETIME_OPS = Arrays.asList(
            SegmentOperator.WITHIN_LAST_DAYS, SegmentOperator.BEFORE_DAYS_AGO, SegmentOperator.BETWEEN);

    private static final Map<String, FanAudienceFieldDefinition> FIELDS = new LinkedHashMap<>();

    static
    {
        register(FIELD_SUBSCRIBED, "当前已关注", TYPE_BOOLEAN, BOOLEAN_OPS);
        register(FIELD_SUBSCRIBE_TIME, "关注时间", TYPE_DATETIME, DATETIME_OPS);
        register(FIELD_MENU_CLICKED, "点击过菜单", TYPE_WECHAT_MP_MENU, BOOLEAN_OPS);
        register(FIELD_ARTICLE_READ, "阅读过图文", TYPE_WECHAT_MP_ARTICLE, BOOLEAN_OPS);
        register(FIELD_HAS_CONTACT, "已关联联系人", TYPE_BOOLEAN, BOOLEAN_OPS);
        register(FIELD_SCAN_FROM_QRCODE, "通过渠道码扫码", TYPE_WECHAT_MP_QRCODE, BOOLEAN_OPS);
        register(FIELD_LAST_INTERACTION_WITHIN_48H, "48小时内有过互动", TYPE_BOOLEAN, BOOLEAN_OPS);
        register(FIELD_TAG_ID, "粉丝包含标签", TYPE_WECHAT_MP_TAG, BOOLEAN_OPS);
    }

    private FanAudienceFieldRegistry()
    {
    }

    private static void register(String field, String label, String type, List<String> operators)
    {
        FIELDS.put(field, new FanAudienceFieldDefinition(field, label, type, operators));
    }

    public static FanAudienceFieldDefinition get(String field)
    {
        return FIELDS.get(field);
    }

    public static List<FanAudienceFieldMetaVo> allFieldMeta()
    {
        List<FanAudienceFieldMetaVo> list = new ArrayList<>();
        for (FanAudienceFieldDefinition def : FIELDS.values())
        {
            FanAudienceFieldMetaVo vo = new FanAudienceFieldMetaVo();
            vo.setField(def.getField());
            vo.setLabel(def.getLabel());
            vo.setType(def.getType());
            vo.setOperators(def.getOperators());
            list.add(vo);
        }
        return Collections.unmodifiableList(list);
    }
}
