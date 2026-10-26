package com.ruoyi.crm.service.support;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import com.ruoyi.common.utils.StringUtils;

/**
 * SurveyJS surveyData → Lead 标准字段映射
 */
public final class CaptureSurveyDataMapper
{
    private static final Set<String> RESERVED_KEYS;

    static
    {
        Set<String> keys = new LinkedHashSet<>();
        keys.add("leadName");
        keys.add("name");
        keys.add("mobilePhone");
        keys.add("phone");
        keys.add("workEmail");
        keys.add("email");
        keys.add("fullName");
        keys.add("organization");
        RESERVED_KEYS = Collections.unmodifiableSet(keys);
    }

    private CaptureSurveyDataMapper()
    {
    }

    public static final class MappedLeadFields
    {
        private final String leadName;
        private final String mobilePhone;
        private final String workEmail;
        private final String fullName;
        private final Map<String, Object> extraFields;

        public MappedLeadFields(String leadName, String mobilePhone, String workEmail, String fullName,
                Map<String, Object> extraFields)
        {
            this.leadName = leadName;
            this.mobilePhone = mobilePhone;
            this.workEmail = workEmail;
            this.fullName = fullName;
            this.extraFields = extraFields != null ? extraFields : Collections.emptyMap();
        }

        public String getLeadName()
        {
            return leadName;
        }

        public String getMobilePhone()
        {
            return mobilePhone;
        }

        public String getWorkEmail()
        {
            return workEmail;
        }

        public String getFullName()
        {
            return fullName;
        }

        public Map<String, Object> getExtraFields()
        {
            return extraFields;
        }
    }

    public static MappedLeadFields map(Map<String, Object> surveyData)
    {
        Map<String, Object> data = surveyData != null ? surveyData : Collections.emptyMap();
        String leadName = firstNonBlank(data, "leadName", "name");
        String mobilePhone = firstNonBlank(data, "mobilePhone", "phone");
        String workEmail = firstNonBlank(data, "workEmail", "email");
        String fullName = firstNonBlank(data, "fullName", "organization");

        Map<String, Object> extras = new LinkedHashMap<>();
        for (Map.Entry<String, Object> entry : data.entrySet())
        {
            if (entry.getKey() == null || RESERVED_KEYS.contains(entry.getKey()))
            {
                continue;
            }
            if (entry.getValue() != null && StringUtils.isNotBlank(String.valueOf(entry.getValue())))
            {
                extras.put(entry.getKey(), entry.getValue());
            }
        }
        return new MappedLeadFields(leadName, mobilePhone, workEmail, fullName, extras);
    }

    private static String firstNonBlank(Map<String, Object> data, String... keys)
    {
        for (String key : keys)
        {
            Object value = data.get(key);
            if (value != null)
            {
                String text = String.valueOf(value).trim();
                if (StringUtils.isNotBlank(text))
                {
                    return text;
                }
            }
        }
        return null;
    }
}
