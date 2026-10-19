package com.ruoyi.web.controller.crm;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.crm.domain.CrmBulkemail;
import com.ruoyi.crm.domain.dto.BulkemailContactsBatchRequest;
import com.ruoyi.crm.domain.dto.BulkemailCreateRequest;
import com.ruoyi.crm.domain.dto.BulkemailAudiencePreviewRequest;
import com.ruoyi.crm.domain.dto.BulkemailAudiencePreviewResult;
import com.ruoyi.crm.domain.dto.BulkemailRenderPreviewRequest;
import com.ruoyi.crm.domain.dto.BulkemailRenderResult;
import com.ruoyi.crm.domain.dto.BulkemailScheduleRequest;
import com.ruoyi.crm.domain.dto.BulkemailTestSendRequest;
import com.ruoyi.crm.service.ICrmBulkemailService;
import com.ruoyi.crm.service.bulkemail.BulkemailAudienceService;
import com.ruoyi.crm.service.bulkemail.BulkemailContactsBatchService;
import com.ruoyi.crm.service.bulkemail.BulkemailLifecycleService;
import com.ruoyi.crm.service.bulkemail.BulkemailRenderPreviewService;

/**
 * 批量邮件Controller
 * 
 * @author luohd
 * @date 2024-09-24
 */
@RestController
@RequestMapping("/crm/bulkemail")
public class CrmBulkemailController extends BaseController
{
    @Autowired
    private ICrmBulkemailService crmBulkemailService;

    @Autowired
    private BulkemailLifecycleService bulkemailLifecycleService;

    @Autowired
    private BulkemailContactsBatchService bulkemailContactsBatchService;

    @Autowired
    private BulkemailAudienceService bulkemailAudienceService;

    @Autowired
    private BulkemailRenderPreviewService bulkemailRenderPreviewService;

    /**
     * 查询批量邮件列表
     */
    @PreAuthorize("@ss.hasPermi('crm:bulkemail:list')")
    @GetMapping("/list")
    public TableDataInfo list(CrmBulkemail crmBulkemail)
    {
        startPage();
        List<CrmBulkemail> list = bulkemailLifecycleService.listNormalized(crmBulkemail);
        return getDataTable(list);
    }

    /**
     * 导出批量邮件列表
     */
    @PreAuthorize("@ss.hasPermi('crm:bulkemail:export')")
    @Log(title = "批量邮件", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, CrmBulkemail crmBulkemail)
    {
        List<CrmBulkemail> list = bulkemailLifecycleService.listNormalized(crmBulkemail);
        ExcelUtil<CrmBulkemail> util = new ExcelUtil<CrmBulkemail>(CrmBulkemail.class);
        util.exportExcel(response, list, "批量邮件数据");
    }

    /**
     * 获取批量邮件详细信息
     */
    @PreAuthorize("@ss.hasPermi('crm:bulkemail:query')")
    @GetMapping(value = "/{bulkId}")
    public AjaxResult getInfo(@PathVariable("bulkId") String bulkId)
    {
        return success(bulkemailLifecycleService.getNormalized(bulkId));
    }

    /**
     * 新增批量邮件（P1：按 sendMode 决定生命周期，不再同步发信）
     */
    @PreAuthorize("@ss.hasPermi('crm:bulkemail:add')")
    @Log(title = "批量邮件", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody BulkemailCreateRequest request)
    {
        String tenant = getLoginUser().getTenant();
        String operator = getUsername();
        return toAjax(bulkemailLifecycleService.create(request, tenant, operator));
    }

    /**
     * 批量写入活动联系人
     */
    @PreAuthorize("@ss.hasPermi('crm:bulkemail:edit')")
    @Log(title = "批量邮件联系人", businessType = BusinessType.INSERT)
    @PostMapping("/{bulkId}/contacts/batch")
    public AjaxResult batchContacts(@PathVariable("bulkId") String bulkId,
            @RequestBody BulkemailContactsBatchRequest request)
    {
        return success(bulkemailContactsBatchService.batchInsert(bulkId, request, getUsername()));
    }

    /**
     * 设定定时发送
     */
    @PreAuthorize("@ss.hasPermi('crm:bulkemail:edit')")
    @Log(title = "批量邮件定时", businessType = BusinessType.UPDATE)
    @PutMapping("/{bulkId}/schedule")
    public AjaxResult schedule(@PathVariable("bulkId") String bulkId,
            @RequestBody BulkemailScheduleRequest request)
    {
        bulkemailLifecycleService.schedule(bulkId, request.getScheduledAt(), getUsername());
        return success();
    }

    /**
     * 立即发送
     */
    @PreAuthorize("@ss.hasPermi('crm:bulkemail:send')")
    @Log(title = "批量邮件发送", businessType = BusinessType.UPDATE)
    @PostMapping("/{bulkId}/send")
    public AjaxResult send(@PathVariable("bulkId") String bulkId)
    {
        bulkemailLifecycleService.sendNow(bulkId, getLoginUser().getTenant(), getUsername());
        return AjaxResult.success("queued");
    }

    /**
     * 取消发送
     */
    @PreAuthorize("@ss.hasPermi('crm:bulkemail:edit')")
    @Log(title = "批量邮件取消", businessType = BusinessType.UPDATE)
    @PostMapping("/{bulkId}/cancel")
    public AjaxResult cancel(@PathVariable("bulkId") String bulkId)
    {
        bulkemailLifecycleService.cancel(bulkId, getUsername());
        return success();
    }

    /**
     * 测试发送
     */
    @PreAuthorize("@ss.hasPermi('crm:bulkemail:send')")
    @Log(title = "批量邮件测试发送", businessType = BusinessType.OTHER)
    @PostMapping("/{bulkId}/test-send")
    public AjaxResult testSend(@PathVariable("bulkId") String bulkId,
            @RequestBody BulkemailTestSendRequest request)
    {
        bulkemailLifecycleService.testSend(bulkId, request.getToEmail(), getLoginUser().getTenant());
        return AjaxResult.success("sent");
    }

    /**
     * 受众筛选 preview（P2）
     */
    @PreAuthorize("@ss.hasPermi('crm:bulkemail:query')")
    @PostMapping("/audience/preview")
    public AjaxResult audiencePreview(@RequestBody BulkemailAudiencePreviewRequest request)
    {
        BulkemailAudiencePreviewResult result = bulkemailAudienceService.preview(
                getLoginUser().getTenant(), request);
        AjaxResult ajax = AjaxResult.success();
        ajax.put("total", result.getTotal());
        ajax.put("validEmailCount", result.getValidEmailCount());
        ajax.put("invalidCount", result.getInvalidCount());
        ajax.put("rows", result.getRows());
        return ajax;
    }

    /**
     * 渲染 preview（P2）
     */
    @PreAuthorize("@ss.hasPermi('crm:bulkemail:query')")
    @PostMapping("/render/preview")
    public AjaxResult renderPreview(@RequestBody BulkemailRenderPreviewRequest request)
    {
        BulkemailRenderResult result = bulkemailRenderPreviewService.preview(
                request, getLoginUser().getTenant());
        return success(result);
    }

    /**
     * 修改批量邮件
     */
    @PreAuthorize("@ss.hasPermi('crm:bulkemail:edit')")
    @Log(title = "批量邮件", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody CrmBulkemail crmBulkemail)
    {
        return toAjax(crmBulkemailService.updateCrmBulkemail(crmBulkemail));
    }

    /**
     * 删除批量邮件
     */
    @PreAuthorize("@ss.hasPermi('crm:bulkemail:remove')")
    @Log(title = "批量邮件", businessType = BusinessType.DELETE)
    @DeleteMapping("/{bulkIds}")
    public AjaxResult remove(@PathVariable String[] bulkIds)
    {
        return toAjax(crmBulkemailService.deleteCrmBulkemailByBulkIds(bulkIds));
    }
}
