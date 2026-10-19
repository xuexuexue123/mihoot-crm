package com.ruoyi.web.controller.crm;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.PageDomain;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.core.page.TableSupport;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.crm.domain.dto.SegmentPreviewMembersRequest;
import com.ruoyi.crm.domain.dto.SegmentPreviewRequest;
import com.ruoyi.crm.domain.dto.SegmentSaveRequest;
import com.ruoyi.crm.domain.vo.SegmentMemberVo;
import com.ruoyi.crm.domain.vo.SegmentVo;
import com.ruoyi.crm.service.ICrmSegmentService;

/**
 * 客户分群
 */
@RestController
@RequestMapping("/crm/segment")
public class CrmSegmentController extends BaseController
{
    @Autowired
    private ICrmSegmentService crmSegmentService;

    @PreAuthorize("@ss.hasPermi('crm:segment:list')")
    @GetMapping("/list")
    public TableDataInfo list(@RequestHeader(value = "tenant", required = false) String tenant)
    {
        startPage();
        List<SegmentVo> list = crmSegmentService.selectList(tenant);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('crm:segment:query')")
    @GetMapping("/fields")
    public AjaxResult fields()
    {
        return success(crmSegmentService.listFields());
    }

    @PreAuthorize("@ss.hasPermi('crm:segment:query')")
    @PostMapping("/preview")
    public AjaxResult preview(@RequestBody SegmentPreviewRequest body,
            @RequestHeader(value = "tenant", required = false) String tenant)
    {
        return success(crmSegmentService.preview(tenant, body));
    }

    @PreAuthorize("@ss.hasPermi('crm:segment:query')")
    @PostMapping("/preview-members")
    public TableDataInfo previewMembers(@RequestBody SegmentPreviewMembersRequest body,
            @RequestHeader(value = "tenant", required = false) String tenant)
    {
        if (body == null)
        {
            TableDataInfo empty = new TableDataInfo();
            empty.setCode(HttpStatus.BAD_REQUEST);
            empty.setMsg("请求体不能为空");
            return empty;
        }
        int pageNum = body.getPageNum() == null ? 1 : body.getPageNum();
        int pageSize = body.getPageSize() == null ? 10 : body.getPageSize();
        List<SegmentMemberVo> list = crmSegmentService.previewMembers(tenant, body.getRuleJson(), pageNum, pageSize);
        TableDataInfo data = new TableDataInfo();
        data.setCode(HttpStatus.SUCCESS);
        data.setMsg("查询成功");
        data.setRows(list);
        data.setTotal(crmSegmentService.countByRuleJson(tenant, body.getRuleJson()));
        return data;
    }

    @PreAuthorize("@ss.hasPermi('crm:segment:query')")
    @GetMapping("/{id}")
    public AjaxResult getById(@PathVariable("id") Long id,
            @RequestHeader(value = "tenant", required = false) String tenant)
    {
        return success(crmSegmentService.getById(tenant, id));
    }

    @PreAuthorize("@ss.hasPermi('crm:segment:query')")
    @GetMapping("/{id}/members")
    public TableDataInfo members(@PathVariable("id") Long id,
            @RequestHeader(value = "tenant", required = false) String tenant)
    {
        PageDomain pageDomain = TableSupport.buildPageRequest();
        List<SegmentMemberVo> list = crmSegmentService.selectMembers(tenant, id,
                pageDomain.getPageNum(), pageDomain.getPageSize());
        TableDataInfo data = new TableDataInfo();
        data.setCode(HttpStatus.SUCCESS);
        data.setMsg("查询成功");
        data.setRows(list);
        data.setTotal(crmSegmentService.countMembers(tenant, id));
        return data;
    }

    @PreAuthorize("@ss.hasPermi('crm:segment:add')")
    @Log(title = "客户分群", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SegmentSaveRequest body,
            @RequestHeader(value = "tenant", required = false) String tenant)
    {
        crmSegmentService.add(tenant, body);
        return success();
    }

    @PreAuthorize("@ss.hasPermi('crm:segment:edit')")
    @Log(title = "客户分群", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult update(@RequestBody SegmentSaveRequest body,
            @RequestHeader(value = "tenant", required = false) String tenant)
    {
        crmSegmentService.update(tenant, body);
        return success();
    }

    @PreAuthorize("@ss.hasPermi('crm:segment:remove')")
    @Log(title = "客户分群", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public AjaxResult remove(@PathVariable("id") Long id,
            @RequestHeader(value = "tenant", required = false) String tenant)
    {
        crmSegmentService.remove(tenant, id);
        return success();
    }
}
