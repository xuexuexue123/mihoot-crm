package com.ruoyi.crm.service.support;

import com.ruoyi.common.utils.StringUtils;

/**
 * 手机号规范化（大陆 11 位）
 */
public final class PhoneNormalizer
{
    private PhoneNormalizer()
    {
    }

    public static String normalize(String phone)
    {
        if (StringUtils.isBlank(phone))
        {
            return null;
        }
        String digits = phone.replaceAll("[\\s\\-()]", "");
        if (digits.startsWith("+86"))
        {
            digits = digits.substring(3);
        }
        else if (digits.startsWith("86") && digits.length() == 13)
        {
            digits = digits.substring(2);
        }
        if (digits.matches("^1\\d{10}$"))
        {
            return digits;
        }
        return null;
    }
}
