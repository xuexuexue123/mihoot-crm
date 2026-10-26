package com.ruoyi.crm.service.support;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.domain.dto.AiPlaybookStepDto;

/**
 * Playbook DAG 校验
 */
public final class PlaybookDagValidator
{
    private static final Set<String> STEP_TYPES = new HashSet<>(
            Arrays.asList("sequential", "parallel", "merge", "terminal"));

    private PlaybookDagValidator()
    {
    }

    public static void validateSteps(List<AiPlaybookStepDto> steps)
    {
        if (steps == null || steps.isEmpty())
        {
            throw new ServiceException("Playbook steps 不能为空", HttpStatus.BAD_REQUEST);
        }

        Map<String, AiPlaybookStepDto> byKey = new HashMap<>();
        for (AiPlaybookStepDto step : steps)
        {
            if (step == null || StringUtils.isBlank(step.getStepKey()))
            {
                throw new ServiceException("stepKey 不能为空", HttpStatus.BAD_REQUEST);
            }
            String key = step.getStepKey().trim();
            if (byKey.containsKey(key))
            {
                throw new ServiceException("重复的 stepKey: " + key, HttpStatus.BAD_REQUEST);
            }
            byKey.put(key, step);

            String stepType = StringUtils.isNotBlank(step.getStepType()) ? step.getStepType().trim() : "sequential";
            if (!STEP_TYPES.contains(stepType))
            {
                throw new ServiceException("stepType 无效: " + stepType, HttpStatus.BAD_REQUEST);
            }
            step.setStepType(stepType);

            if (("merge".equals(stepType) || "terminal".equals(stepType))
                    && (step.getDependsOn() == null || step.getDependsOn().isEmpty()))
            {
                throw new ServiceException("step " + key + " 的 dependsOn 不能为空", HttpStatus.BAD_REQUEST);
            }

            if (step.getDependsOn() != null)
            {
                for (String dep : step.getDependsOn())
                {
                    if (StringUtils.isBlank(dep))
                    {
                        throw new ServiceException("dependsOn 包含空 stepKey", HttpStatus.BAD_REQUEST);
                    }
                    if (!byKey.containsKey(dep.trim()) && !containsKey(steps, dep.trim()))
                    {
                        // defer until all keys collected - second pass below
                    }
                }
            }
        }

        for (AiPlaybookStepDto step : steps)
        {
            if (step.getDependsOn() == null)
            {
                continue;
            }
            for (String dep : step.getDependsOn())
            {
                if (!byKey.containsKey(dep.trim()))
                {
                    throw new ServiceException("dependsOn 引用不存在的 stepKey: " + dep, HttpStatus.BAD_REQUEST);
                }
            }
        }

        boolean hasTerminal = steps.stream()
                .anyMatch(s -> "terminal".equals(s.getStepType()));
        if (!hasTerminal)
        {
            throw new ServiceException("Playbook 至少需要一个 terminal step", HttpStatus.BAD_REQUEST);
        }

        detectCycle(steps);
    }

    private static boolean containsKey(List<AiPlaybookStepDto> steps, String key)
    {
        for (AiPlaybookStepDto step : steps)
        {
            if (step != null && key.equals(step.getStepKey()))
            {
                return true;
            }
        }
        return false;
    }

    private static void detectCycle(List<AiPlaybookStepDto> steps)
    {
        Map<String, Integer> indegree = new HashMap<>();
        Map<String, List<String>> edges = new HashMap<>();

        for (AiPlaybookStepDto step : steps)
        {
            indegree.put(step.getStepKey().trim(), 0);
        }
        for (AiPlaybookStepDto step : steps)
        {
            String key = step.getStepKey().trim();
            if (step.getDependsOn() == null)
            {
                continue;
            }
            for (String dep : step.getDependsOn())
            {
                String depKey = dep.trim();
                edges.computeIfAbsent(depKey, k -> new ArrayList<>()).add(key);
                indegree.put(key, indegree.get(key) + 1);
            }
        }

        Deque<String> queue = new ArrayDeque<>();
        for (Map.Entry<String, Integer> entry : indegree.entrySet())
        {
            if (entry.getValue() == 0)
            {
                queue.add(entry.getKey());
            }
        }

        int visited = 0;
        while (!queue.isEmpty())
        {
            String node = queue.removeFirst();
            visited++;
            List<String> next = edges.get(node);
            if (next == null)
            {
                continue;
            }
            for (String child : next)
            {
                int deg = indegree.get(child) - 1;
                indegree.put(child, deg);
                if (deg == 0)
                {
                    queue.add(child);
                }
            }
        }

        if (visited != indegree.size())
        {
            throw new ServiceException("Playbook steps 存在环依赖", HttpStatus.BAD_REQUEST);
        }
    }
}
