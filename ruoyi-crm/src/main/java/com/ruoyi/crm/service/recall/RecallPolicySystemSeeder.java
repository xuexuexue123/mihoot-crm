package com.ruoyi.crm.service.recall;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.domain.CrmRecallPolicy;
import com.ruoyi.crm.mapper.CrmRecallPolicyMapper;

/**
 * 为当前租户写入四条系统预置复诊策略（幂等）；已存在的系统策略 match_json 升级为字典 value。
 */
@Component
public class RecallPolicySystemSeeder
{
    private static final String MATCH_IMPLANT = "{\"treatmentItemCodes\":[\"implant\"]}";
    private static final String MATCH_PREVENTIVE = "{\"treatmentItemCodes\":[\"preventive\"]}";
    private static final String MATCH_RESTORATIVE = "{\"treatmentItemCodes\":[\"restorative\"]}";
    private static final String MATCH_DEFAULT = "{\"treatmentItemCodes\":[]}";

    @Autowired
    private CrmRecallPolicyMapper policyMapper;

    public synchronized void ensure(String tenantId)
    {
        if (StringUtils.isBlank(tenantId))
        {
            return;
        }
        seed(tenantId, RecallPolicyConstants.SYS_IMPLANT, "种植牙复诊",
                "种植项目赢单后 90 天复诊", 10,
                MATCH_IMPLANT,
                90, RecallPolicyConstants.UNIT_DAYS);
        seed(tenantId, RecallPolicyConstants.SYS_PREVENTIVE, "洁牙预防复诊",
                "洁牙/预防项目赢单后 180 天复诊", 20,
                MATCH_PREVENTIVE,
                180, RecallPolicyConstants.UNIT_DAYS);
        seed(tenantId, RecallPolicyConstants.SYS_RESTORATIVE, "补牙修复复诊",
                "补牙/修复项目赢单后 180 天复诊", 30,
                MATCH_RESTORATIVE,
                180, RecallPolicyConstants.UNIT_DAYS);
        seed(tenantId, RecallPolicyConstants.SYS_DEFAULT, "默认复诊",
                "未匹配治疗项目时 180 天复诊", 100,
                MATCH_DEFAULT,
                180, RecallPolicyConstants.UNIT_DAYS);
    }

    private void seed(String tenantId, String policyId, String name, String description, int priority,
            String matchJson, int intervalValue, String intervalUnit)
    {
        CrmRecallPolicy existing = policyMapper.selectEntityById(tenantId, policyId);
        if (existing != null)
        {
            upgradeSystemMatchJsonIfNeeded(existing, matchJson);
            return;
        }
        CrmRecallPolicy row = new CrmRecallPolicy();
        row.setPolicyId(policyId);
        row.setTenantId(tenantId);
        row.setName(name);
        row.setDescription(description);
        row.setEnabled(RecallPolicyConstants.ENABLED);
        row.setIsSystem(RecallPolicyConstants.IS_SYSTEM);
        row.setPriority(priority);
        row.setMatchJson(matchJson);
        row.setIntervalValue(intervalValue);
        row.setIntervalUnit(intervalUnit);
        row.setDelFlag("0");
        row.setCreateBy("system");
        row.setCreateTime(DateUtils.getNowDate());
        row.setUpdateBy("system");
        row.setUpdateTime(DateUtils.getNowDate());
        try
        {
            policyMapper.insertCrmRecallPolicy(row);
        }
        catch (Exception ignored)
        {
            // concurrent seed
        }
    }

    /**
     * 系统策略若仍含中文/别名 match_json，升级为仅字典 value（不改间隔与其它字段）。
     */
    private void upgradeSystemMatchJsonIfNeeded(CrmRecallPolicy existing, String canonicalMatchJson)
    {
        if (existing == null || !RecallPolicyConstants.IS_SYSTEM.equals(existing.getIsSystem()))
        {
            return;
        }
        if (canonicalMatchJson.equals(existing.getMatchJson()))
        {
            return;
        }
        // 仅当旧值仍含迁移别名或与规范不一致时升级
        if (!needsMatchJsonUpgrade(existing.getMatchJson(), canonicalMatchJson))
        {
            return;
        }
        existing.setMatchJson(canonicalMatchJson);
        existing.setUpdateBy("system");
        existing.setUpdateTime(DateUtils.getNowDate());
        try
        {
            policyMapper.updateCrmRecallPolicy(existing);
        }
        catch (Exception ignored)
        {
        }
    }

    static boolean needsMatchJsonUpgrade(String current, String canonical)
    {
        return !canonical.equals(current);
    }
}
