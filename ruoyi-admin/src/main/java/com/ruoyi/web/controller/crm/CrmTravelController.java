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
import com.ruoyi.crm.domain.CrmTravel;
import com.ruoyi.crm.service.ICrmTravelService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 差旅Controller
 * 
 * @author luohd
 * @date 2024-03-13
 */
@RestController
@RequestMapping("/crm/travel")
public class CrmTravelController extends BaseController
{
    @Autowired
    private ICrmTravelService crmTravelService;

    /**
     * 查询差旅列表
     */
    @PreAuthorize("@ss.hasPermi('crm:travel:list')")
    @GetMapping("/list")
    public TableDataInfo list(CrmTravel crmTravel)
    {
        startPage();
        List<CrmTravel> list = crmTravelService.selectCrmTravelList(crmTravel);
        return getDataTable(list);
    }

    /**
     * 导出差旅列表
     */
    @PreAuthorize("@ss.hasPermi('crm:travel:export')")
    @Log(title = "差旅", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, CrmTravel crmTravel)
    {
        List<CrmTravel> list = crmTravelService.selectCrmTravelListForExport(crmTravel);
        ExcelUtil<CrmTravel> util = new ExcelUtil<CrmTravel>(CrmTravel.class);
        util.exportExcel(response, list, "Travel");
    }

    /**
     * 获取差旅详细信息
     */
    @PreAuthorize("@ss.hasPermi('crm:travel:query')")
    @GetMapping(value = "/{travelId}")
    public AjaxResult getInfo(@PathVariable("travelId") String travelId)
    {
        return success(crmTravelService.selectCrmTravelByTravelId(travelId));
    }

    /**
     * 新增差旅
     */
    @PreAuthorize("@ss.hasPermi('crm:travel:add')")
    @Log(title = "差旅", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody CrmTravel crmTravel)
    {
        return toAjax(crmTravelService.insertCrmTravel(crmTravel));
    }

    /**
     * 修改差旅
     */
    @PreAuthorize("@ss.hasPermi('crm:travel:edit')")
    @Log(title = "差旅", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody CrmTravel crmTravel)
    {
        return toAjax(crmTravelService.updateCrmTravel(crmTravel));
    }

    /**
     * 删除差旅
     */
    @PreAuthorize("@ss.hasPermi('crm:travel:remove')")
    @Log(title = "差旅", businessType = BusinessType.DELETE)
	@DeleteMapping("/{travelIds}")
    public AjaxResult remove(@PathVariable String[] travelIds)
    {
        return toAjax(crmTravelService.deleteCrmTravelByTravelIds(travelIds));
    }
}
