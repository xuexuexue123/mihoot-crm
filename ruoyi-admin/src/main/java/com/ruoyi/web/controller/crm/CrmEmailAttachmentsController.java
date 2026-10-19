package com.ruoyi.web.controller.crm;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.crm.domain.CrmEmailAttachments;
import com.ruoyi.crm.service.ICrmEmailAttachmentsService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 邮件附件Controller
 * 
 * @author luohd
 * @date 2025-05-16
 */
@RestController
@RequestMapping("/crm/emailattachments")
public class CrmEmailAttachmentsController extends BaseController
{
    @Autowired
    private ICrmEmailAttachmentsService crmEmailAttachmentsService;

    /**
     * 查询邮件附件列表
     */
    @PreAuthorize("@ss.hasPermi('crm:emailattachments:list')")
    @GetMapping("/list")
    public TableDataInfo list(CrmEmailAttachments crmEmailAttachments)
    {
        startPage();
        List<CrmEmailAttachments> list = crmEmailAttachmentsService.selectCrmEmailAttachmentsList(crmEmailAttachments);
        return getDataTable(list);
    }

    /**
     * 导出邮件附件列表
     */
    @PreAuthorize("@ss.hasPermi('crm:emailattachments:export')")
    @Log(title = "邮件附件", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, CrmEmailAttachments crmEmailAttachments)
    {
        List<CrmEmailAttachments> list = crmEmailAttachmentsService.selectCrmEmailAttachmentsList(crmEmailAttachments);
        ExcelUtil<CrmEmailAttachments> util = new ExcelUtil<CrmEmailAttachments>(CrmEmailAttachments.class);
        util.exportExcel(response, list, "邮件附件数据");
    }

    /**
     * 获取邮件附件详细信息
     */
    @PreAuthorize("@ss.hasPermi('crm:emailattachments:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") String id)
    {
        return success(crmEmailAttachmentsService.selectCrmEmailAttachmentsById(id));
    }

    /**
     * 新增邮件附件
     */
    @PreAuthorize("@ss.hasPermi('crm:emailattachments:add')")
    @Log(title = "邮件附件", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody CrmEmailAttachments crmEmailAttachments)
    {
        return toAjax(crmEmailAttachmentsService.insertCrmEmailAttachments(crmEmailAttachments));
    }

    /**
     * 修改邮件附件
     */
    @PreAuthorize("@ss.hasPermi('crm:emailattachments:edit')")
    @Log(title = "邮件附件", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody CrmEmailAttachments crmEmailAttachments)
    {
        return toAjax(crmEmailAttachmentsService.updateCrmEmailAttachments(crmEmailAttachments));
    }

    /**
     * 删除邮件附件
     */
    @PreAuthorize("@ss.hasPermi('crm:emailattachments:remove')")
    @Log(title = "邮件附件", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable String[] ids)
    {
        return toAjax(crmEmailAttachmentsService.deleteCrmEmailAttachmentsByIds(ids));
    }
}
