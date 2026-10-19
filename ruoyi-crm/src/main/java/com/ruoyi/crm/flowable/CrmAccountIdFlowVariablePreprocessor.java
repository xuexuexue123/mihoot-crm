package com.ruoyi.crm.flowable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.mapper.CrmContactMapper;
import com.ruoyi.common.flowable.spi.FlowVariablePreprocessor;

/**
 * 流程变量中含 {@code accountId} / {@code serviceAccountId} 时，展开为 {@code contactIds}、{@code contactIdsCsv}。
 */
@Component
public class CrmAccountIdFlowVariablePreprocessor implements FlowVariablePreprocessor
{
    @Autowired
    private CrmContactMapper crmContactMapper;

    @Override
    public void beforeProcessStart(Map<String, Object> variables)
    {
        if (variables == null || variables.isEmpty())
        {
            return;
        }
        String accountId = firstString(variables.get("accountId"));
        if (StringUtils.isEmpty(accountId))
        {
            accountId = firstString(variables.get("serviceAccountId"));
        }
        if (StringUtils.isEmpty(accountId))
        {
            return;
        }
        List<String> ids = crmContactMapper.selectContactIdsByAccountId(accountId.trim());
        if (ids == null)
        {
            ids = new ArrayList<>();
        }
        variables.put("contactIds", ids);
        variables.put("contactIdsCsv", String.join(",", ids));
    }

    private static String firstString(Object o)
    {
        if (o == null)
        {
            return null;
        }
        String s = String.valueOf(o).trim();
        return s.isEmpty() ? null : s;
    }
}
