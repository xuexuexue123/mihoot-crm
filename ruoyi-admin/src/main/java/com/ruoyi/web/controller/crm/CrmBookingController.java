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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.domain.CrmBooking;
import com.ruoyi.crm.service.ICrmBookingService;

/**
 * 诊所上门预约 Controller（与会议 /crm/appointment 分离）
 */
@RestController
@RequestMapping("/crm/booking")
public class CrmBookingController extends BaseController
{
    @Autowired
    private ICrmBookingService crmBookingService;

    /**
     * 查询预约列表
     */
    @PreAuthorize("@ss.hasPermi('crm:booking:list')")
    @GetMapping("/list")
    public TableDataInfo list(CrmBooking crmBooking,
            @RequestParam(value = "leadIds", required = false) String leadIds)
    {
        if (StringUtils.isNotBlank(leadIds))
        {
            crmBooking.getParams().put("leadIds", leadIds);
        }
        startPage();
        List<CrmBooking> list = crmBookingService.selectCrmBookingList(crmBooking);
        return getDataTable(list);
    }

    /**
     * 按日查询（Asia/Shanghai 日界）
     */
    @PreAuthorize("@ss.hasPermi('crm:booking:list')")
    @GetMapping("/listbyday")
    public TableDataInfo listByDay(CrmBooking crmBooking,
            @RequestParam("date") String date,
            @RequestParam(value = "includeCancelled", required = false) Boolean includeCancelled)
    {
        if (includeCancelled != null)
        {
            crmBooking.setIncludeCancelled(includeCancelled);
        }
        List<CrmBooking> list = crmBookingService.selectCrmBookingListByDay(crmBooking, date);
        return getDataTable(list);
    }

    /**
     * 获取预约详情（DataScope）
     */
    @PreAuthorize("@ss.hasPermi('crm:booking:query')")
    @GetMapping("/{bookingId}")
    public AjaxResult getInfo(@PathVariable("bookingId") String bookingId)
    {
        CrmBooking query = new CrmBooking();
        query.setBookingId(bookingId);
        return success(crmBookingService.selectCrmBookingByBookingIdScoped(query));
    }

    /**
     * 新增预约（仅 leadId 或仅 contactId 均可；不隐式建 Lead）
     * 成功返回 Booking 实体（含 bookingId），便于助手/前端改期前持有主键。
     */
    @PreAuthorize("@ss.hasPermi('crm:booking:add')")
    @Log(title = "上门预约", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody CrmBooking crmBooking)
    {
        crmBookingService.insertCrmBooking(crmBooking);
        return success(crmBooking);
    }

    /**
     * 修改预约
     */
    @PreAuthorize("@ss.hasPermi('crm:booking:edit')")
    @Log(title = "上门预约", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody CrmBooking crmBooking)
    {
        return toAjax(crmBookingService.updateCrmBooking(crmBooking));
    }

    /**
     * 取消预约（软取消 status=cancelled）
     */
    @PreAuthorize("@ss.hasPermi('crm:booking:remove')")
    @Log(title = "上门预约", businessType = BusinessType.DELETE)
    @DeleteMapping("/{bookingId}")
    public AjaxResult remove(@PathVariable("bookingId") String bookingId)
    {
        return toAjax(crmBookingService.cancelCrmBookingByBookingId(bookingId));
    }
}
