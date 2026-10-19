package com.ruoyi.crm.service.recall;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import com.ruoyi.common.utils.StringUtils;

/**
 * 迁移期：将历史自由文本 / 中文别名归一为 crm_treatment_item.dict_value。
 * 新写入路径只允许字典 value；本类仅供匹配与 backfill 对照。
 */
public final class TreatmentItemCodeNormalizer
{
    private static final Map<String, String> ALIASES;

    static
    {
        Map<String, String> m = new HashMap<>();
        putAll(m, "implant",
                "implant", "种植", "种植牙", "种植体", "种牙");
        putAll(m, "preventive",
                "preventive", "clean", "cleaning", "洁牙", "洗牙", "预防");
        putAll(m, "restorative",
                "restorative", "补牙", "修复");
        putAll(m, "ortho",
                "ortho", "orthodontics", "正畸");
        putAll(m, "pediatric",
                "pediatric", "child", "儿牙");
        ALIASES = Collections.unmodifiableMap(m);
    }

    private TreatmentItemCodeNormalizer()
    {
    }

    /**
     * @return 标准 dict_value；无法识别则返回 trim 后的原文（可能为空串）
     */
    public static String normalize(String raw)
    {
        if (raw == null)
        {
            return "";
        }
        String trimmed = raw.trim();
        if (trimmed.isEmpty())
        {
            return "";
        }
        String byExact = ALIASES.get(trimmed);
        if (byExact != null)
        {
            return byExact;
        }
        String byLower = ALIASES.get(trimmed.toLowerCase(Locale.ROOT));
        if (byLower != null)
        {
            return byLower;
        }
        return trimmed;
    }

    public static boolean codesEqual(String left, String right)
    {
        if (StringUtils.isEmpty(left) || StringUtils.isEmpty(right))
        {
            return false;
        }
        return normalize(left).equals(normalize(right));
    }

    private static void putAll(Map<String, String> map, String canonical, String... aliases)
    {
        for (String a : aliases)
        {
            map.put(a, canonical);
            map.put(a.toLowerCase(Locale.ROOT), canonical);
        }
    }
}
