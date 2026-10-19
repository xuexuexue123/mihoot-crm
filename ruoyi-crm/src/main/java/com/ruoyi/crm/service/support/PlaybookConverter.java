package com.ruoyi.crm.service.support;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.domain.CrmAiPlaybookRun;
import com.ruoyi.crm.domain.CrmAiSkillPlaybook;
import com.ruoyi.crm.domain.CrmAiSkillPlaybookStep;
import com.ruoyi.crm.domain.vo.AiPlaybookDetailVo;
import com.ruoyi.crm.domain.vo.AiPlaybookListVo;
import com.ruoyi.crm.domain.vo.AiPlaybookRunVo;
import com.ruoyi.crm.domain.vo.AiPlaybookStepVo;

/**
 * Playbook 实体与 VO 转换
 */
public final class PlaybookConverter
{
    private PlaybookConverter()
    {
    }

    public static AiPlaybookListVo toListVo(CrmAiSkillPlaybook row, int stepCount, List<String> trackKeys)
    {
        if (row == null)
        {
            return null;
        }
        AiPlaybookListVo vo = new AiPlaybookListVo();
        vo.setId(row.getId());
        vo.setName(row.getName());
        vo.setDescription(row.getDescription());
        vo.setTriggerKeywords(parseStringList(row.getTriggerKeywords()));
        vo.setEntrySkillId(row.getEntrySkillId());
        vo.setOutputSchema(parseObjectMap(row.getOutputSchema()));
        vo.setEnabled(row.getEnabled() != null && row.getEnabled() == 1);
        vo.setVersion(row.getVersion());
        vo.setStepCount(stepCount);
        vo.setTrackKeys(trackKeys);
        vo.setUpdatedAt(row.getUpdateTime());
        return vo;
    }

    public static AiPlaybookDetailVo toDetailVo(CrmAiSkillPlaybook row, List<CrmAiSkillPlaybookStep> steps)
    {
        List<String> trackKeys = new ArrayList<>();
        if (steps != null)
        {
            for (CrmAiSkillPlaybookStep step : steps)
            {
                if (StringUtils.isNotBlank(step.getTrackKey()) && !trackKeys.contains(step.getTrackKey()))
                {
                    trackKeys.add(step.getTrackKey());
                }
            }
        }
        AiPlaybookListVo base = toListVo(row, steps != null ? steps.size() : 0, trackKeys);
        AiPlaybookDetailVo vo = new AiPlaybookDetailVo();
        copyListFields(base, vo);
        vo.setSteps(toStepVoList(steps));
        return vo;
    }

    public static List<AiPlaybookStepVo> toStepVoList(List<CrmAiSkillPlaybookStep> steps)
    {
        if (steps == null || steps.isEmpty())
        {
            return Collections.emptyList();
        }
        List<AiPlaybookStepVo> result = new ArrayList<>();
        for (CrmAiSkillPlaybookStep step : steps)
        {
            AiPlaybookStepVo vo = new AiPlaybookStepVo();
            vo.setStepKey(step.getStepKey());
            vo.setSkillId(step.getSkillId());
            vo.setSkillName(step.getSkillName());
            vo.setStepType(step.getStepType());
            vo.setParallelGroup(step.getParallelGroup());
            vo.setDependsOn(parseStringList(step.getDependsOn()));
            vo.setTrackKey(step.getTrackKey());
            vo.setSortOrder(step.getSortOrder());
            vo.setEnabled(step.getEnabled() == null || step.getEnabled() == 1);
            result.add(vo);
        }
        return result;
    }

    public static AiPlaybookRunVo toRunVo(CrmAiPlaybookRun row)
    {
        if (row == null)
        {
            return null;
        }
        AiPlaybookRunVo vo = new AiPlaybookRunVo();
        vo.setId(row.getId());
        vo.setPlaybookId(row.getPlaybookId());
        vo.setPlaybookVersion(row.getPlaybookVersion());
        vo.setSubjectType(row.getSubjectType());
        vo.setSubjectId(row.getSubjectId());
        vo.setTriggerSource(row.getTriggerSource());
        vo.setStatus(row.getStatus());
        vo.setContextJson(parseObjectMap(row.getContextJson()));
        vo.setFinalOutput(parseObjectMap(row.getFinalOutput()));
        vo.setErrorMessage(row.getErrorMessage());
        vo.setStartedAt(row.getStartedAt());
        vo.setFinishedAt(row.getFinishedAt());
        return vo;
    }

    public static String toJsonString(Object value)
    {
        if (value == null)
        {
            return null;
        }
        return JSON.toJSONString(value);
    }

    private static void copyListFields(AiPlaybookListVo from, AiPlaybookDetailVo to)
    {
        to.setId(from.getId());
        to.setName(from.getName());
        to.setDescription(from.getDescription());
        to.setTriggerKeywords(from.getTriggerKeywords());
        to.setEntrySkillId(from.getEntrySkillId());
        to.setOutputSchema(from.getOutputSchema());
        to.setEnabled(from.getEnabled());
        to.setVersion(from.getVersion());
        to.setStepCount(from.getStepCount());
        to.setTrackKeys(from.getTrackKeys());
        to.setUpdatedAt(from.getUpdatedAt());
    }

    private static List<String> parseStringList(String json)
    {
        if (StringUtils.isBlank(json))
        {
            return null;
        }
        return JSON.parseObject(json, new TypeReference<List<String>>()
        {
        });
    }

    private static Map<String, Object> parseObjectMap(String json)
    {
        if (StringUtils.isBlank(json))
        {
            return null;
        }
        return JSON.parseObject(json, new TypeReference<Map<String, Object>>()
        {
        });
    }
}
