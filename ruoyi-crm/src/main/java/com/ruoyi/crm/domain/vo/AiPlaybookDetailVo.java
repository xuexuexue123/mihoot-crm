package com.ruoyi.crm.domain.vo;

import java.util.List;

/**
 * Playbook 详情 VO（含 steps）
 */
public class AiPlaybookDetailVo extends AiPlaybookListVo
{
    private List<AiPlaybookStepVo> steps;

    public List<AiPlaybookStepVo> getSteps()
    {
        return steps;
    }

    public void setSteps(List<AiPlaybookStepVo> steps)
    {
        this.steps = steps;
    }
}
