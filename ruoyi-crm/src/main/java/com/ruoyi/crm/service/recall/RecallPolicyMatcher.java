package com.ruoyi.crm.service.recall;

import java.util.List;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.domain.CrmRecallPolicy;

/**
 * 按 priority 升序匹配治疗项目；无命中则走 catch-all（空 treatmentItemCodes）。
 * 匹配前对机会码与策略码做 {@link TreatmentItemCodeNormalizer} 归一（迁移期别名）。
 */
public final class RecallPolicyMatcher
{
    private RecallPolicyMatcher()
    {
    }

    public static CrmRecallPolicy match(List<CrmRecallPolicy> policies, String treatmentItemCode)
    {
        if (policies == null || policies.isEmpty())
        {
            return null;
        }
        String code = TreatmentItemCodeNormalizer.normalize(treatmentItemCode);
        CrmRecallPolicy catchAll = null;
        for (CrmRecallPolicy policy : policies)
        {
            if (isCatchAll(policy))
            {
                if (catchAll == null)
                {
                    catchAll = policy;
                }
                continue;
            }
            if (StringUtils.isNotEmpty(code) && containsCode(policy, code))
            {
                return policy;
            }
        }
        return catchAll;
    }

    public static boolean isCatchAll(CrmRecallPolicy policy)
    {
        JSONArray codes = treatmentItemCodes(policy);
        return codes == null || codes.isEmpty();
    }

    static boolean containsCode(CrmRecallPolicy policy, String normalizedCode)
    {
        JSONArray codes = treatmentItemCodes(policy);
        if (codes == null)
        {
            return false;
        }
        for (int i = 0; i < codes.size(); i++)
        {
            String item = codes.getString(i);
            if (item != null && normalizedCode.equals(TreatmentItemCodeNormalizer.normalize(item)))
            {
                return true;
            }
        }
        return false;
    }

    static JSONArray treatmentItemCodes(CrmRecallPolicy policy)
    {
        if (policy == null || StringUtils.isBlank(policy.getMatchJson()))
        {
            return null;
        }
        try
        {
            JSONObject json = JSON.parseObject(policy.getMatchJson());
            if (json == null)
            {
                return null;
            }
            return json.getJSONArray("treatmentItemCodes");
        }
        catch (Exception ex)
        {
            return null;
        }
    }
}
