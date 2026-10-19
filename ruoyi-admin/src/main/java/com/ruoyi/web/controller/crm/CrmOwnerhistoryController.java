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
import com.ruoyi.crm.domain.CrmOwnerhistory;
import com.ruoyi.crm.service.ICrmOwnerhistoryService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * owner记录Controller
 * 
 * @author luohd
 * @date 2024-01-20
 */
@RestController
@RequestMapping("/crm/ownerhistory")
public class CrmOwnerhistoryController extends BaseController
{
    @Autowired
    private ICrmOwnerhistoryService crmOwnerhistoryService;

    /**
     * 查询owner记录列表
     */
    @PreAuthorize("@ss.hasPermi('crm:ownerhistory:list')")
    @GetMapping("/list")
    public TableDataInfo list(CrmOwnerhistory crmOwnerhistory)
    {
        startPage();
        List<CrmOwnerhistory> list = crmOwnerhistoryService.selectCrmOwnerhistoryList(crmOwnerhistory);
        return getDataTable(list);
    }
    /**
     * 查询owner记录列表
     */
    @PreAuthorize("@ss.hasPermi('crm:ownerhistory:list')")
    @GetMapping("/targetlist")
    public TableDataInfo targetlist(CrmOwnerhistory crmOwnerhistory)
    {
        startPage();
        List<CrmOwnerhistory> list = crmOwnerhistoryService.selectCrmOwnerhistoryListByTarget(crmOwnerhistory);
        return getDataTable(list);
    }

    /**
     * 导出owner记录列表
     */
    @PreAuthorize("@ss.hasPermi('crm:ownerhistory:export')")
    @Log(title = "owner记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, CrmOwnerhistory crmOwnerhistory)
    {
        List<CrmOwnerhistory> list = crmOwnerhistoryService.selectCrmOwnerhistoryList(crmOwnerhistory);
        ExcelUtil<CrmOwnerhistory> util = new ExcelUtil<CrmOwnerhistory>(CrmOwnerhistory.class);
        util.exportExcel(response, list, "owner记录数据");
    }

    /**
     * 获取owner记录详细信息
     */
    @PreAuthorize("@ss.hasPermi('crm:ownerhistory:query')")
    @GetMapping(value = "/{ohId}")
    public AjaxResult getInfo(@PathVariable("ohId") String ohId)
    {
        return success(crmOwnerhistoryService.selectCrmOwnerhistoryByOhId(ohId));
    }

    /**
     * 新增owner记录
     */
    @PreAuthorize("@ss.hasPermi('crm:ownerhistory:add')")
    @Log(title = "owner记录", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody CrmOwnerhistory crmOwnerhistory)
    {
        return toAjax(crmOwnerhistoryService.insertCrmOwnerhistory(crmOwnerhistory));
    }

    /**
     * 修改owner记录
     */
    @PreAuthorize("@ss.hasPermi('crm:ownerhistory:edit')")
    @Log(title = "owner记录", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody CrmOwnerhistory crmOwnerhistory)
    {
        return toAjax(crmOwnerhistoryService.updateCrmOwnerhistory(crmOwnerhistory));
    }

    /**
     * 删除owner记录
     */
    @PreAuthorize("@ss.hasPermi('crm:ownerhistory:remove')")
    @Log(title = "owner记录", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ohIds}")
    public AjaxResult remove(@PathVariable String[] ohIds)
    {
        return toAjax(crmOwnerhistoryService.deleteCrmOwnerhistoryByOhIds(ohIds));
    }
}
