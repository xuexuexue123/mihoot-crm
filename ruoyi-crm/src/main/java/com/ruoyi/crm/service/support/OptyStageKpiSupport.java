package com.ruoyi.crm.service.support;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.ruoyi.common.core.domain.entity.SysDictData;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.service.followup.FollowupRuleHookSupport;
import com.ruoyi.system.service.ISysDictTypeService;

/**
 * 商机终态判定，供 KPI 赢单率等聚合复用。
 * 赢单对齐 CrmOptyServiceImpl 字典 crm_opty_stage_closed_won + fallback；
 * 输单复用 {@link FollowupRuleHookSupport#isClosedLost}。
 */
@Component
public class OptyStageKpiSupport
{
    public static final String DICT_CLOSED_WON_STAGES = "crm_opty_stage_closed_won";

    /** 字典为空且无配置时的兜底（含前端常见别名） */
    private static final List<String> DEFAULT_WON_ALIASES = Collections.unmodifiableList(Arrays.asList(
            "closed_won", "won", "赢单", "成交", "Closed Won", "Won"
    ));

    @Autowired
    private ISysDictTypeService dictTypeService;

    @Value("${crm.dental.closed-won-stages:}")
    private String closedWonStagesFallback;

    public boolean isClosedWonStage(String stage)
    {
        if (StringUtils.isEmpty(stage))
        {
            return false;
        }
        List<String> won = resolveClosedWonStages();
        for (String w : won)
        {
            if (stage.equals(w))
            {
                return true;
            }
        }
        // 宽松别名：字典未覆盖时仍识别常见展示值
        String lower = stage.trim().toLowerCase(Locale.ROOT);
        return "won".equals(lower) || "closed_won".equals(lower)
                || "赢单".equals(stage) || "成交".equals(stage);
    }

    public boolean isClosedLostStage(String stage, String closeReason)
    {
        return FollowupRuleHookSupport.isClosedLost(stage, closeReason);
    }

    /** 终端商机 = 赢单 + 输单 */
    public boolean isTerminalOpty(String stage, String closeReason)
    {
        return isClosedWonStage(stage) || isClosedLostStage(stage, closeReason);
    }

    /**
     * 供 SQL IN 子句使用的赢单 stage 列表（字典优先，否则 fallback / 默认别名）。
     */
    public List<String> resolveClosedWonStages()
    {
        List<SysDictData> dict = dictTypeService.selectDictDataByType(DICT_CLOSED_WON_STAGES);
        if (dict != null && !dict.isEmpty())
        {
            List<String> values = new ArrayList<>();
            for (SysDictData d : dict)
            {
                if ("0".equals(d.getStatus()) && StringUtils.isNotEmpty(d.getDictValue()))
                {
                    values.add(d.getDictValue());
                }
            }
            if (!values.isEmpty())
            {
                return values;
            }
        }
        if (StringUtils.isNotEmpty(closedWonStagesFallback))
        {
            List<String> fromConfig = new ArrayList<>();
            for (String p : closedWonStagesFallback.split(","))
            {
                String trimmed = p.trim();
                if (!trimmed.isEmpty())
                {
                    fromConfig.add(trimmed);
                }
            }
            if (!fromConfig.isEmpty())
            {
                return fromConfig;
            }
        }
        return new ArrayList<>(DEFAULT_WON_ALIASES);
    }

    public Set<String> resolveClosedWonStageSet()
    {
        return new HashSet<>(resolveClosedWonStages());
    }
}
