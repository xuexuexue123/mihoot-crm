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
import com.ruoyi.crm.domain.CrmTicket;
import com.ruoyi.crm.service.ICrmTicketService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 工单Controller
 * 
 * @author luohd
 * @date 2025-04-08
 */
@RestController
@RequestMapping("/crm/serviceticket")
public class CrmTicketController extends BaseController
{
    @Autowired
    private ICrmTicketService crmTicketService;

    /**
     * 查询工单列表
     */
    @PreAuthorize("@ss.hasPermi('crm:ticket:list')")
    @GetMapping("/list")
    public TableDataInfo list(CrmTicket crmTicket)
    {
        startPage();
        List<CrmTicket> list = crmTicketService.selectCrmTicketList(crmTicket);
        return getDataTable(list);
    }

    /**
     * 导出工单列表
     */
    @PreAuthorize("@ss.hasPermi('crm:ticket:export')")
    @Log(title = "工单", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, CrmTicket crmTicket)
    {
        List<CrmTicket> list = crmTicketService.selectCrmTicketList(crmTicket);
        ExcelUtil<CrmTicket> util = new ExcelUtil<CrmTicket>(CrmTicket.class);
        util.exportExcel(response, list, "工单数据");
    }

    /**
     * 获取工单详细信息
     */
    @PreAuthorize("@ss.hasPermi('crm:ticket:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") String id)
    {
        return success(crmTicketService.selectCrmTicketById(id));
    }

    /**
     * 新增工单
     */
    @PreAuthorize("@ss.hasPermi('crm:ticket:add')")
    @Log(title = "工单", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody CrmTicket crmTicket)
    {
        return toAjax(crmTicketService.insertCrmTicket(crmTicket));
    }

    /**
     * 修改工单
     */
    @PreAuthorize("@ss.hasPermi('crm:ticket:edit')")
    @Log(title = "工单", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody CrmTicket crmTicket)
    {
        return toAjax(crmTicketService.updateCrmTicket(crmTicket));
    }

    /**
     * 删除工单
     */
    @PreAuthorize("@ss.hasPermi('crm:ticket:remove')")
    @Log(title = "工单", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable String[] ids)
    {
        return toAjax(crmTicketService.deleteCrmTicketByIds(ids));
    }
}
