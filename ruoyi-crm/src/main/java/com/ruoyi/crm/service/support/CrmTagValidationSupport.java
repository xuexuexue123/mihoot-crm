package com.ruoyi.crm.service.support;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;

/**
 * CRM 标签词表校验（dict_type=crm_tag）
 */
public final class CrmTagValidationSupport
{
    public static final String DICT_TYPE = "crm_tag";

    public static final String CHANNEL_WECOM = "wecom";
    public static final String CHANNEL_WECHAT_MP = "wechat_mp";

    public static final Pattern DICT_VALUE_PATTERN =
            Pattern.compile("^[a-z][a-z0-9_]*\\.[a-z][a-z0-9_]*$");

    /** remark 分类 → dict_sort 基数 */
    public static final Map<String, Integer> CATEGORY_BASE;

    static
    {
        Map<String, Integer> map = new LinkedHashMap<>();
        map.put("成交机会", 10);
        map.put("竞争分析", 20);
        map.put("决策特征", 30);
        map.put("心理诉求", 40);
        map.put("成交风险", 50);
        map.put("跟进建议", 60);
        CATEGORY_BASE = Collections.unmodifiableMap(map);
    }

    public static final List<String> CATEGORIES =
            Collections.unmodifiableList(Arrays.asList(CATEGORY_BASE.keySet().toArray(new String[0])));

    private CrmTagValidationSupport()
    {
    }

    public static void validateDictValue(String dictValue)
    {
        if (StringUtils.isBlank(dictValue) || !DICT_VALUE_PATTERN.matcher(dictValue.trim()).matches())
        {
            throw new ServiceException("dictValue 格式须为 维度.编码（如 risk.price_sensitive）",
                    HttpStatus.BAD_REQUEST);
        }
    }

    public static String requireCategory(String remark)
    {
        if (StringUtils.isBlank(remark) || !CATEGORY_BASE.containsKey(remark.trim()))
        {
            throw new ServiceException("remark 必须为业务分类：" + String.join("、", CATEGORIES),
                    HttpStatus.BAD_REQUEST);
        }
        return remark.trim();
    }

    public static int nextDictSort(String category, int existingInCategory)
    {
        Integer base = CATEGORY_BASE.get(category);
        if (base == null)
        {
            throw new ServiceException("未知分类", HttpStatus.BAD_REQUEST);
        }
        return base + Math.max(existingInCategory, 0) + 1;
    }
}
