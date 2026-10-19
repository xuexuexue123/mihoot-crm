package com.ruoyi.crm.service.support;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.domain.CrmContact;

/**
 * 消息模版变量解析器，替换 {{variable}} 占位符为联系人实际数据。
 */
public final class MessageTemplateResolver
{
    private static final Pattern VAR_PATTERN = Pattern.compile("\\{\\{(\\w+)\\}\\}");

    private MessageTemplateResolver()
    {
    }

    /**
     * 用联系人数据替换模版中的变量占位符。
     *
     * @param template 消息模版，如 "{{name}} 今天过生日，电话：{{phone}}"
     * @param contact  联系人数据
     * @return 替换后的消息文本
     */
    public static String resolve(String template, CrmContact contact)
    {
        if (StringUtils.isBlank(template) || contact == null)
        {
            return "";
        }

        Map<String, String> vars = buildVariableMap(contact);

        Matcher matcher = VAR_PATTERN.matcher(template);
        StringBuffer sb = new StringBuffer();
        while (matcher.find())
        {
            String key = matcher.group(1);
            String value = vars.getOrDefault(key, "");
            matcher.appendReplacement(sb, Matcher.quoteReplacement(value));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    private static Map<String, String> buildVariableMap(CrmContact contact)
    {
        Map<String, String> vars = new HashMap<>();
        vars.put("name", defaultString(contact.getName()));
        vars.put("phone", defaultString(
                StringUtils.isNotBlank(contact.getMobilePhone())
                        ? contact.getMobilePhone()
                        : contact.getWorkPhone()));
        vars.put("birthdate", formatDate(contact.getBirthdate(), "yyyy-MM-dd"));
        vars.put("age", String.valueOf(computeAge(contact.getBirthdate())));
        vars.put("company", defaultString(contact.getAccountName()));
        vars.put("gender", defaultString(contact.getGender()));
        return vars;
    }

    private static int computeAge(Date birthdate)
    {
        if (birthdate == null)
        {
            return 0;
        }
        Calendar birth = Calendar.getInstance();
        birth.setTime(birthdate);
        Calendar today = Calendar.getInstance();
        int age = today.get(Calendar.YEAR) - birth.get(Calendar.YEAR);
        if (today.get(Calendar.DAY_OF_YEAR) < birth.get(Calendar.DAY_OF_YEAR))
        {
            age--;
        }
        return age;
    }

    private static String formatDate(Date date, String pattern)
    {
        if (date == null)
        {
            return "";
        }
        return new SimpleDateFormat(pattern).format(date);
    }

    private static String defaultString(String value)
    {
        return StringUtils.isNotBlank(value) ? value : "";
    }
}
