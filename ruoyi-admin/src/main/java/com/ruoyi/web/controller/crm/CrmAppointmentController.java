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
import com.ruoyi.crm.domain.CrmAppointment;
import com.ruoyi.crm.service.ICrmAppointmentService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 预约Controller
 * 
 * @author luohd
 * @date 2024-02-16
 */
@RestController
@RequestMapping("/crm/appointment")
public class CrmAppointmentController extends BaseController
{
    @Autowired
    private ICrmAppointmentService crmAppointmentService;

    /**
     * 查询预约列表
     */
    @PreAuthorize("@ss.hasPermi('crm:appointment:list')")
    @GetMapping("/list")
    public TableDataInfo list(CrmAppointment crmAppointment)
    {
        startPage();
        List<CrmAppointment> list = crmAppointmentService.selectCrmAppointmentList(crmAppointment);
        return getDataTable(list);
    }
    /**
     * 查询本月预约列表
     */
    @PreAuthorize("@ss.hasPermi('crm:appointment:list')")
    @GetMapping("/listmonthly")
    public TableDataInfo listmonthly(CrmAppointment crmAppointment)
    {
        startPage();
        List<CrmAppointment> list = crmAppointmentService.selectCrmAppointmentListMonthly(crmAppointment);
        return getDataTable(list);
    }
    /**
     * 导出预约列表
     */
    @PreAuthorize("@ss.hasPermi('crm:appointment:export')")
    @Log(title = "预约", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, CrmAppointment crmAppointment)
    {
        List<CrmAppointment> list = crmAppointmentService.selectCrmAppointmentList(crmAppointment);
        ExcelUtil<CrmAppointment> util = new ExcelUtil<CrmAppointment>(CrmAppointment.class);
        util.exportExcel(response, list, "预约数据");
    }

    /**
     * 获取预约详细信息
     */
    @PreAuthorize("@ss.hasPermi('crm:appointment:query')")
    @GetMapping(value = "/{appointmentId}")
    public AjaxResult getInfo(@PathVariable("appointmentId") String appointmentId)
    {
        return success(crmAppointmentService.selectCrmAppointmentByAppointmentId(appointmentId));
    }

    /**
     * 新增预约
     */
    @PreAuthorize("@ss.hasPermi('crm:appointment:add')")
    @Log(title = "预约", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody CrmAppointment crmAppointment)
    {
        return toAjax(crmAppointmentService.insertCrmAppointment(crmAppointment));
    }

    /**
     * 修改预约
     */
    @PreAuthorize("@ss.hasPermi('crm:appointment:edit')")
    @Log(title = "预约", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody CrmAppointment crmAppointment)
    {
        return toAjax(crmAppointmentService.updateCrmAppointment(crmAppointment));
    }

    /**
     * 删除预约
     */
    @PreAuthorize("@ss.hasPermi('crm:appointment:remove')")
    @Log(title = "预约", businessType = BusinessType.DELETE)
	@DeleteMapping("/{appointmentIds}")
    public AjaxResult remove(@PathVariable String[] appointmentIds)
    {
        return toAjax(crmAppointmentService.deleteCrmAppointmentByAppointmentIds(appointmentIds));
    }
}
