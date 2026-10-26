package com.ruoyi.crm.service.support;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.domain.CrmSurveys;

/**
 * 问卷 H5 Outreach 链接签发辅助
 */
public final class SurveyOutreachSupport
{
    private static final Set<String> NON_ISSUABLE_STATUSES = new HashSet<>(Arrays.asList(
            "draft", "closed", "archived"));

    private SurveyOutreachSupport()
    {
    }

    public static boolean isIssuable(CrmSurveys survey)
    {
        if (survey == null)
        {
            return false;
        }
        String status = survey.getStatus();
        if (StringUtils.isBlank(status))
        {
            return false;
        }
        return !NON_ISSUABLE_STATUSES.contains(status.toLowerCase(Locale.ROOT));
    }
}
