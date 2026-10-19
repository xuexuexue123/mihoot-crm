package com.ruoyi.crm.service.support;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.domain.vo.EventAudienceRowVo;

/**
 * 受众行 audienceKey 别名查找键（精确 key + contact/mobile/mp 别名）
 */
public final class AudienceKeyAliasSupport
{
    private AudienceKeyAliasSupport()
    {
    }

    public static List<String> lookupKeys(EventAudienceRowVo row)
    {
        if (row == null)
        {
            return Collections.emptyList();
        }
        List<String> keys = new ArrayList<>();
        if (StringUtils.isNotEmpty(row.getAudienceKey()))
        {
            keys.add(row.getAudienceKey());
        }
        String contactKey = contactAudienceKey(row.getContactId());
        if (StringUtils.isNotEmpty(contactKey) && !keys.contains(contactKey))
        {
            keys.add(contactKey);
        }
        String mobileKey = mobileAudienceKey(row.getMobilePhone());
        if (StringUtils.isNotEmpty(mobileKey) && !keys.contains(mobileKey))
        {
            keys.add(mobileKey);
        }
        String mpKey = mpAudienceKey(row.getWechatMpOpenid());
        if (StringUtils.isNotEmpty(mpKey) && !keys.contains(mpKey))
        {
            keys.add(mpKey);
        }
        return keys;
    }

    public static String contactAudienceKey(String contactId)
    {
        if (StringUtils.isEmpty(contactId))
        {
            return null;
        }
        return "contact:" + contactId;
    }

    public static String mobileAudienceKey(String mobilePhone)
    {
        String mobile = PhoneNormalizer.normalize(mobilePhone);
        if (StringUtils.isEmpty(mobile))
        {
            return null;
        }
        return "mobile:" + mobile;
    }

    public static String mpAudienceKey(String openId)
    {
        if (StringUtils.isEmpty(openId))
        {
            return null;
        }
        return "mp:" + openId;
    }
}
