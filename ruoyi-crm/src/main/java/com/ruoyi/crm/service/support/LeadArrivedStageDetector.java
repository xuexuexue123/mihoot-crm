package com.ruoyi.crm.service.support;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.ruoyi.common.core.domain.entity.SysDictData;
import com.ruoyi.system.service.ISysDictTypeService;

/**
 * 识别 Lead stage 是否表示「已到店」，与 Next lead-convert-stage.ts 对齐。
 */
@Component
public class LeadArrivedStageDetector
{
    private static final String DICT_TYPE_LEAD_STAGE = "crm_lead_stage";

    private static final Set<String> KNOWN_DICT_VALUES = new HashSet<>(Arrays.asList(
            "arrived", "arrived_at_clinic", "visited", "visited_clinic",
            "first_visit", "at_clinic", "in_clinic", "clinic_visit"));

    private static final Set<String> KNOWN_LABELS = new HashSet<>(Arrays.asList(
            "已到店", "到院", "已到院",
            "arrived", "arrived at clinic", "visited clinic", "at clinic", "in clinic",
            "first visit", "clinic visit",
            "đã đến", "đã tới", "đã đến cửa hàng", "đã đến phòng khám", "da den", "da toi",
            "มาถึงแล้ว", "มาที่คลินิกแล้ว"));

    @Autowired
    private ISysDictTypeService dictTypeService;

    @Value("${crm.lead.arrived-stage-values:}")
    private String extraArrivedStageValues;

    private final AtomicReference<Set<String>> dictArrivedTokens = new AtomicReference<>(new HashSet<>());

    @PostConstruct
    public void init()
    {
        refreshDictCache();
    }

    public void refreshDictCache()
    {
        Set<String> tokens = new HashSet<>();
        try
        {
            List<SysDictData> rows = dictTypeService.selectDictDataByType(DICT_TYPE_LEAD_STAGE);
            if (rows != null)
            {
                for (SysDictData row : rows)
                {
                    if (row == null)
                    {
                        continue;
                    }
                    if (matchesBuiltIn(row.getDictValue()) || matchesBuiltIn(row.getDictLabel()))
                    {
                        addToken(tokens, row.getDictValue());
                        addToken(tokens, row.getDictLabel());
                    }
                }
            }
        }
        catch (Exception ignored)
        {
            // 字典未配置时仅使用内置规则
        }
        addExtraConfigTokens(tokens);
        dictArrivedTokens.set(tokens);
    }

    public static String normalize(String stage)
    {
        if (!StringUtils.hasText(stage))
        {
            return "";
        }
        return stage.trim().toLowerCase()
                .replace(' ', '_')
                .replace('-', '_');
    }

    public boolean isArrivedStage(String stored)
    {
        if (!StringUtils.hasText(stored))
        {
            return false;
        }
        String trimmed = stored.trim();
        String normalized = normalize(trimmed);
        if (KNOWN_DICT_VALUES.contains(normalized))
        {
            return true;
        }
        if (KNOWN_LABELS.contains(trimmed.toLowerCase()))
        {
            return true;
        }
        Set<String> dictTokens = dictArrivedTokens.get();
        if (dictTokens.contains(normalized) || dictTokens.contains(trimmed.toLowerCase()))
        {
            return true;
        }
        return matchesBuiltIn(trimmed);
    }

    public boolean stageChangedToArrived(String oldStage, String newStage)
    {
        if (!StringUtils.hasText(newStage))
        {
            return false;
        }
        if (oldStage != null && oldStage.trim().equals(newStage.trim()))
        {
            return false;
        }
        if (isArrivedStage(oldStage))
        {
            return false;
        }
        return isArrivedStage(newStage);
    }

    private boolean matchesBuiltIn(String value)
    {
        if (!StringUtils.hasText(value))
        {
            return false;
        }
        String trimmed = value.trim();
        return KNOWN_DICT_VALUES.contains(normalize(trimmed))
                || KNOWN_LABELS.contains(trimmed.toLowerCase());
    }

    private void addExtraConfigTokens(Set<String> tokens)
    {
        if (!StringUtils.hasText(extraArrivedStageValues))
        {
            return;
        }
        for (String part : extraArrivedStageValues.split(","))
        {
            addToken(tokens, part);
        }
    }

    private static void addToken(Set<String> tokens, String raw)
    {
        if (!StringUtils.hasText(raw))
        {
            return;
        }
        String trimmed = raw.trim();
        tokens.add(trimmed.toLowerCase());
        tokens.add(normalize(trimmed));
    }
}
