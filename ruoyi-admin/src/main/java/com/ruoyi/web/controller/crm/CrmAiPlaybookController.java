package com.ruoyi.web.controller.crm;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.crm.domain.dto.AiPlaybookRunCreateDto;
import com.ruoyi.crm.domain.dto.AiPlaybookUpsertDto;
import com.ruoyi.crm.domain.vo.AiPlaybookDetailVo;
import com.ruoyi.crm.domain.vo.AiPlaybookListVo;
import com.ruoyi.crm.domain.vo.AiPlaybookRunVo;
import com.ruoyi.crm.service.ICrmAiPlaybookRunService;
import com.ruoyi.crm.service.ICrmAiPlaybookService;

/**
 * AI Playbook API
 */
@RestController
@RequestMapping("/crm/ai/playbook")
public class CrmAiPlaybookController extends BaseController
{
    @Autowired
    private ICrmAiPlaybookService crmAiPlaybookService;

    @Autowired
    private ICrmAiPlaybookRunService crmAiPlaybookRunService;

    @PreAuthorize("@ss.hasPermi('crm:ai:playbook:list')")
    @GetMapping("/list")
    public AjaxResult list(@RequestHeader(value = "tenant", required = false) String tenant)
    {
        List<AiPlaybookListVo> data = crmAiPlaybookService.list(tenant);
        return success(data);
    }

    @PreAuthorize("@ss.hasPermi('crm:ai:playbook:list')")
    @GetMapping("/{id}")
    public AjaxResult detail(@PathVariable("id") Long id,
            @RequestHeader(value = "tenant", required = false) String tenant)
    {
        AiPlaybookDetailVo data = crmAiPlaybookService.getDetail(tenant, id);
        return success(data);
    }

    @PreAuthorize("@ss.hasPermi('crm:ai:playbook:edit')")
    @Log(title = "AI Playbook", businessType = BusinessType.UPDATE)
    @PostMapping
    public AjaxResult upsert(@RequestBody AiPlaybookUpsertDto body,
            @RequestHeader(value = "tenant", required = false) String tenant)
    {
        AiPlaybookDetailVo data = crmAiPlaybookService.upsert(tenant, body);
        return success(data);
    }

    @PreAuthorize("@ss.hasPermi('crm:ai:playbook:edit')")
    @Log(title = "AI Playbook", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public AjaxResult delete(@PathVariable("id") Long id,
            @RequestHeader(value = "tenant", required = false) String tenant)
    {
        crmAiPlaybookService.delete(tenant, id);
        return success();
    }

    @PreAuthorize("@ss.hasPermi('crm:ai:playbook:run')")
    @Log(title = "AI Playbook Run", businessType = BusinessType.INSERT)
    @PostMapping("/run")
    public AjaxResult recordRun(@RequestBody AiPlaybookRunCreateDto body,
            @RequestHeader(value = "tenant", required = false) String tenant)
    {
        crmAiPlaybookRunService.recordRun(tenant, body);
        return success();
    }

    @PreAuthorize("@ss.hasPermi('crm:ai:playbook:run:list')")
    @GetMapping("/run/list")
    public TableDataInfo listRuns(@RequestHeader(value = "tenant", required = false) String tenant,
            @RequestParam(value = "subjectType", required = false) String subjectType,
            @RequestParam(value = "subjectId", required = false) String subjectId,
            @RequestParam(value = "playbookId", required = false) Long playbookId)
    {
        startPage();
        List<AiPlaybookRunVo> list = crmAiPlaybookRunService.listRuns(tenant, subjectType, subjectId, playbookId);
        return getDataTable(list);
    }
}
