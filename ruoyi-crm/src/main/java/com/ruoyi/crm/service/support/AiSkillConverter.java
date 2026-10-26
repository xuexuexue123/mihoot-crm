package com.ruoyi.crm.service.support;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.domain.CrmAiSkill;
import com.ruoyi.crm.domain.vo.AiSkillVo;

/**
 * AI Skill 实体与 VO 转换
 */
public final class AiSkillConverter
{
    private AiSkillConverter()
    {
    }

    public static AiSkillVo toVo(CrmAiSkill row)
    {
        if (row == null)
        {
            return null;
        }
        AiSkillVo vo = new AiSkillVo();
        vo.setId(row.getId());
        vo.setName(row.getName());
        vo.setDescription(row.getDescription());
        vo.setInstruction(row.getInstruction());
        vo.setTriggerKeywords(parseStringList(row.getTriggerKeywords()));
        vo.setTriggerPatterns(parseStringList(row.getTriggerPatterns()));
        vo.setScope(row.getScope());
        vo.setPriority(row.getPriority());
        vo.setEnabled(row.getEnabled() != null && row.getEnabled() == 1);
        vo.setSkillType(row.getSkillType());
        vo.setDomain(row.getDomain());
        vo.setTrackKey(row.getTrackKey());
        vo.setToolBindings(parseObjectList(row.getToolBindings()));
        vo.setInputContext(parseStringList(row.getInputContext()));
        vo.setOutputKey(row.getOutputKey());
        vo.setOutputSchema(parseObjectMap(row.getOutputSchema()));
        vo.setMcpServerIds(parseLongList(row.getMcpServerIds()));
        vo.setUpdatedAt(row.getUpdateTime());
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

    private static List<Long> parseLongList(String json)
    {
        if (StringUtils.isBlank(json))
        {
            return null;
        }
        return JSON.parseObject(json, new TypeReference<List<Long>>()
        {
        });
    }

    private static List<Map<String, Object>> parseObjectList(String json)
    {
        if (StringUtils.isBlank(json))
        {
            return null;
        }
        return JSON.parseObject(json, new TypeReference<List<Map<String, Object>>>()
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

    public static List<AiSkillVo> toVoList(List<CrmAiSkill> rows)
    {
        if (rows == null || rows.isEmpty())
        {
            return Collections.emptyList();
        }
        return rows.stream().map(AiSkillConverter::toVo).collect(java.util.stream.Collectors.toList());
    }
}
