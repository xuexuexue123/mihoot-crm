package com.ruoyi.crm.service.support;

/**
 * 身份值归一化：写入 / 查询 crm_customer_identity 前统一处理，保证同一人不同渠道/不同格式的
 * 身份值能真正落到同一行，配合唯一约束 uk_crm_identity_type_value 完成去重。
 *
 * <p>规则：
 * <ul>
 *   <li>mobile_phone / whatsapp_phone：去空格/连字符/括号，去 +86 / 前导 0086 / 86 区号，仅保留数字。</li>
 *   <li>email：trim + toLowerCase。</li>
 *   <li>腾讯侧 ID / whatsapp_wa_id / messenger_psid / webchat_visitor_id（wecom_external_userid / wechat_unionid / wechat_mp_openid 等）：仅 trim（大小写敏感）。</li>
 * </ul>
 */
public final class IdentityNormalizer
{
    public static final String TYPE_MOBILE_PHONE = "mobile_phone";
    public static final String TYPE_EMAIL = "email";
    public static final String TYPE_WHATSAPP_WA_ID = "whatsapp_wa_id";
    public static final String TYPE_WHATSAPP_PHONE = "whatsapp_phone";
    public static final String TYPE_MESSENGER_PSID = "messenger_psid";
    public static final String TYPE_WEBCHAT_VISITOR_ID = "webchat_visitor_id";

    private IdentityNormalizer()
    {
    }

    /**
     * 按身份类型归一化身份值。null 输入返回 null。
     *
     * @param identityType 身份类型（大小写不敏感）
     * @param identityValue 原始身份值
     * @return 归一化后的身份值
     */
    public static String normalize(String identityType, String identityValue)
    {
        if (identityValue == null)
        {
            return null;
        }
        if (identityType == null)
        {
            return identityValue.trim();
        }
        switch (identityType.trim().toLowerCase())
        {
            case TYPE_MOBILE_PHONE:
            case TYPE_WHATSAPP_PHONE:
                return normalizeMobile(identityValue);
            case TYPE_EMAIL:
                return normalizeEmail(identityValue);
            case TYPE_MESSENGER_PSID:
            case TYPE_WEBCHAT_VISITOR_ID:
            case TYPE_WHATSAPP_WA_ID:
            default:
                return identityValue.trim();
        }
    }

    /**
     * 手机号归一化：仅保留数字并去掉常见区号前缀。
     */
    public static String normalizeMobile(String value)
    {
        if (value == null)
        {
            return null;
        }
        String digits = value.replaceAll("[^0-9]", "");
        if (digits.startsWith("0086"))
        {
            digits = digits.substring(4);
        }
        else if (digits.length() > 11 && digits.startsWith("86"))
        {
            // 仅当长度超过本地号码位数时才认为 86 是区号，避免误删以 86 开头的号码
            digits = digits.substring(2);
        }
        return digits;
    }

    /**
     * 邮箱归一化：trim + 小写。
     */
    public static String normalizeEmail(String value)
    {
        if (value == null)
        {
            return null;
        }
        return value.trim().toLowerCase();
    }
}
