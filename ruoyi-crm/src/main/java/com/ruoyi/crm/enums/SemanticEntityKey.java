package com.ruoyi.crm.enums;

import com.ruoyi.common.utils.StringUtils;

/**
 * 语义层 entityKey 枚举（契约 §3，与前端 catalog 同步）
 */
public enum SemanticEntityKey
{
    LEAD("lead"),
    CONTACT("contact"),
    ACCOUNT("account"),
    ORGANIZATION("organization"),
    OPPORTUNITY("opportunity"),
    CONTRACT("contract"),
    TASK("task"),
    PULSE("pulse"),
    PATIENT_VALUE("patientValue"),
    BULKEMAIL("bulkemail"),
    EVENT("event");

    private final String key;

    SemanticEntityKey(String key)
    {
        this.key = key;
    }

    public String getKey()
    {
        return key;
    }

    public static SemanticEntityKey fromKey(String entityKey)
    {
        if (StringUtils.isBlank(entityKey))
        {
            return null;
        }
        for (SemanticEntityKey item : values())
        {
            if (item.key.equals(entityKey))
            {
                return item;
            }
        }
        return null;
    }
}
