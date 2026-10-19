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
import com.ruoyi.crm.domain.CrmAttendee;
import com.ruoyi.crm.service.ICrmAttendeeService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 参与者Controller
 * 
 * @author luohd
 * @date 2024-02-16
 */
@RestController
@RequestMapping("/crm/attendee")
public class CrmAttendeeController extends BaseController
{
    @Autowired
    private ICrmAttendeeService crmAttendeeService;

    /**
     * 查询参与者列表
     */
    @PreAuthorize("@ss.hasPermi('crm:attendee:list')")
    @GetMapping("/list")
    public TableDataInfo list(CrmAttendee crmAttendee)
    {
        startPage();
        List<CrmAttendee> list = crmAttendeeService.selectCrmAttendeeList(crmAttendee);
        return getDataTable(list);
    }

    /**
     * 导出参与者列表
     */
    @PreAuthorize("@ss.hasPermi('crm:attendee:export')")
    @Log(title = "参与者", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, CrmAttendee crmAttendee)
    {
        List<CrmAttendee> list = crmAttendeeService.selectCrmAttendeeList(crmAttendee);
        ExcelUtil<CrmAttendee> util = new ExcelUtil<CrmAttendee>(CrmAttendee.class);
        util.exportExcel(response, list, "参与者数据");
    }

    /**
     * 获取参与者详细信息
     */
    @PreAuthorize("@ss.hasPermi('crm:attendee:query')")
    @GetMapping(value = "/{attendeeId}")
    public AjaxResult getInfo(@PathVariable("attendeeId") String attendeeId)
    {
        return success(crmAttendeeService.selectCrmAttendeeByAttendeeId(attendeeId));
    }

    /**
     * 新增参与者
     */
    @PreAuthorize("@ss.hasPermi('crm:attendee:add')")
    @Log(title = "参与者", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody CrmAttendee crmAttendee)
    {
        return toAjax(crmAttendeeService.insertCrmAttendee(crmAttendee));
    }

    /**
     * 修改参与者
     */
    @PreAuthorize("@ss.hasPermi('crm:attendee:edit')")
    @Log(title = "参与者", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody CrmAttendee crmAttendee)
    {
        return toAjax(crmAttendeeService.updateCrmAttendee(crmAttendee));
    }

    /**
     * 删除参与者
     */
    @PreAuthorize("@ss.hasPermi('crm:attendee:remove')")
    @Log(title = "参与者", businessType = BusinessType.DELETE)
	@DeleteMapping("/{attendeeIds}")
    public AjaxResult remove(@PathVariable String[] attendeeIds)
    {
        return toAjax(crmAttendeeService.deleteCrmAttendeeByAttendeeIds(attendeeIds));
    }
}
