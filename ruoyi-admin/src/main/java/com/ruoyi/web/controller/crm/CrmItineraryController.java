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
import com.ruoyi.crm.domain.CrmItinerary;
import com.ruoyi.crm.service.ICrmItineraryService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 行程Controller
 * 
 * @author luohd
 * @date 2024-03-13
 */
@RestController
@RequestMapping("/crm/itinerary")
public class CrmItineraryController extends BaseController
{
    @Autowired
    private ICrmItineraryService crmItineraryService;

    /**
     * 查询行程列表
     */
    @PreAuthorize("@ss.hasPermi('crm:itinerary:list')")
    @GetMapping("/list")
    public TableDataInfo list(CrmItinerary crmItinerary)
    {
        startPage();
        List<CrmItinerary> list = crmItineraryService.selectCrmItineraryList(crmItinerary);
        return getDataTable(list);
    }

    /**
     * 导出行程列表
     */
    @PreAuthorize("@ss.hasPermi('crm:itinerary:export')")
    @Log(title = "行程", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, CrmItinerary crmItinerary)
    {
        List<CrmItinerary> list = crmItineraryService.selectCrmItineraryList(crmItinerary);
        ExcelUtil<CrmItinerary> util = new ExcelUtil<CrmItinerary>(CrmItinerary.class);
        util.exportExcel(response, list, "行程数据");
    }

    /**
     * 获取行程详细信息
     */
    @PreAuthorize("@ss.hasPermi('crm:itinerary:query')")
    @GetMapping(value = "/{itinId}")
    public AjaxResult getInfo(@PathVariable("itinId") String itinId)
    {
        return success(crmItineraryService.selectCrmItineraryByItinId(itinId));
    }

    /**
     * 新增行程
     */
    @PreAuthorize("@ss.hasPermi('crm:itinerary:add')")
    @Log(title = "行程", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody CrmItinerary crmItinerary)
    {
        return toAjax(crmItineraryService.insertCrmItinerary(crmItinerary));
    }

    /**
     * 修改行程
     */
    @PreAuthorize("@ss.hasPermi('crm:itinerary:edit')")
    @Log(title = "行程", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody CrmItinerary crmItinerary)
    {
        return toAjax(crmItineraryService.updateCrmItinerary(crmItinerary));
    }

    /**
     * 删除行程
     */
    @PreAuthorize("@ss.hasPermi('crm:itinerary:remove')")
    @Log(title = "行程", businessType = BusinessType.DELETE)
	@DeleteMapping("/{itinIds}")
    public AjaxResult remove(@PathVariable String[] itinIds)
    {
        return toAjax(crmItineraryService.deleteCrmItineraryByItinIds(itinIds));
    }
}
