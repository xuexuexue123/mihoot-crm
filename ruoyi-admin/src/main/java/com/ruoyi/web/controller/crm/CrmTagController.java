package com.ruoyi.web.controller.crm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.crm.domain.dto.CrmTagCreateRequest;
import com.ruoyi.crm.domain.dto.CrmTagDispatchRequest;
import com.ruoyi.crm.domain.dto.CrmTagMappingPutRequest;
import com.ruoyi.crm.domain.dto.CrmTagMergeRequest;
import com.ruoyi.crm.domain.dto.CrmTagUpdateRequest;
import com.ruoyi.crm.domain.vo.CrmTagDispatchTaskVo;
import com.ruoyi.crm.domain.vo.CrmTagMemberVo;
import com.ruoyi.crm.service.ICrmTagService;

/**
 * CRM 标签集中治理
 */
@RestController
@RequestMapping("/crm/tag")
public class CrmTagController extends BaseController
{
    @Autowired
    private ICrmTagService crmTagService;

    @PreAuthorize("@ss.hasPermi('crm:tag:list')")
    @GetMapping("/list")
    public AjaxResult list()
    {
        return success(crmTagService.listAll());
    }

    @PreAuthorize("@ss.hasPermi('crm:tag:add')")
    @Log(title = "CRM 标签", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody CrmTagCreateRequest body)
    {
        return success(crmTagService.create(body));
    }

    @PreAuthorize("@ss.hasPermi('crm:tag:edit')")
    @Log(title = "CRM 标签", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody CrmTagUpdateRequest body)
    {
        return success(crmTagService.update(body));
    }

    @PreAuthorize("@ss.hasPermi('crm:tag:remove')")
    @Log(title = "CRM 标签", businessType = BusinessType.DELETE)
    @DeleteMapping("/{dictValue:.+}")
    public AjaxResult remove(@PathVariable("dictValue") String dictValue)
    {
        crmTagService.remove(dictValue);
        return success();
    }

    @PreAuthorize("@ss.hasPermi('crm:tag:usage') or @ss.hasPermi('crm:tag:query')")
    @GetMapping("/usage")
    public AjaxResult usage(@RequestParam(value = "dictValue", required = false) String dictValue)
    {
        return success(crmTagService.usage(dictValue));
    }

    /**
     * 打了某标签的联系人 / 线索分页列表（营销 /tags 钻取）。
     * entity = contact | lead
     */
    @PreAuthorize("@ss.hasPermi('crm:tag:usage') or @ss.hasPermi('crm:tag:query')")
    @GetMapping("/members")
    public TableDataInfo members(@RequestParam("dictValue") String dictValue,
            @RequestParam("entity") String entity,
            @RequestParam(value = "pageNum", required = false) Integer pageNum,
            @RequestParam(value = "pageSize", required = false) Integer pageSize)
    {
        int pn = pageNum == null ? 1 : pageNum;
        int ps = pageSize == null ? 50 : pageSize;
        List<CrmTagMemberVo> rows = crmTagService.listMembers(dictValue, entity, pn, ps);
        TableDataInfo data = new TableDataInfo();
        data.setCode(HttpStatus.SUCCESS);
        data.setMsg("查询成功");
        data.setRows(rows);
        data.setTotal(crmTagService.countMembers(dictValue, entity));
        return data;
    }

    @PreAuthorize("@ss.hasPermi('crm:tag:query')")
    @GetMapping("/orphans")
    public AjaxResult orphans()
    {
        return success(crmTagService.orphans());
    }

    @PreAuthorize("@ss.hasPermi('crm:tag:merge')")
    @Log(title = "CRM 标签合并", businessType = BusinessType.UPDATE)
    @PostMapping("/merge")
    public AjaxResult merge(@RequestBody CrmTagMergeRequest body)
    {
        return success(crmTagService.merge(body));
    }

    @PreAuthorize("@ss.hasPermi('crm:tag:mapping')")
    @GetMapping("/mapping")
    public AjaxResult listMapping()
    {
        return success(crmTagService.listMappings());
    }

    @PreAuthorize("@ss.hasPermi('crm:tag:mapping')")
    @Log(title = "CRM 标签渠道映射", businessType = BusinessType.UPDATE)
    @PutMapping("/mapping")
    public AjaxResult putMapping(@RequestBody CrmTagMappingPutRequest body)
    {
        crmTagService.putMapping(body);
        return success();
    }

    @PreAuthorize("@ss.hasPermi('crm:tag:dispatch')")
    @Log(title = "CRM 标签渠道下发", businessType = BusinessType.OTHER)
    @PostMapping("/dispatch")
    public ResponseEntity<AjaxResult> dispatch(@RequestBody CrmTagDispatchRequest body,
            @RequestHeader(value = "tenant", required = false) String tenant)
    {
        CrmTagDispatchTaskVo task = crmTagService.dispatch(tenant, body);
        Map<String, Object> data = new HashMap<>();
        data.put("taskId", task.getTaskId());
        AjaxResult result = AjaxResult.success("accepted", data);
        result.put(AjaxResult.CODE_TAG, 202);
        return ResponseEntity.accepted().body(result);
    }

    @PreAuthorize("@ss.hasPermi('crm:tag:dispatch') or @ss.hasPermi('crm:tag:query')")
    @GetMapping("/dispatch/{taskId}")
    public AjaxResult dispatchStatus(@PathVariable("taskId") Long taskId)
    {
        return success(crmTagService.getDispatchTask(taskId));
    }
}
