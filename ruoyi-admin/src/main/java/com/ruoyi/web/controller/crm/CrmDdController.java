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
import com.ruoyi.crm.domain.CrmDd;
import com.ruoyi.crm.service.ICrmDdService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 尽职调查Controller
 * 
 * @author luohd
 * @date 2024-05-01
 */
@RestController
@RequestMapping("/crm/dd")
public class CrmDdController extends BaseController
{
    @Autowired
    private ICrmDdService crmDdService;

    /**
     * 查询尽职调查列表
     */
    @PreAuthorize("@ss.hasPermi('crm:dd:list')")
    @GetMapping("/list")
    public TableDataInfo list(CrmDd crmDd)
    {
        startPage();
        List<CrmDd> list = crmDdService.selectCrmDdList(crmDd);
        return getDataTable(list);
    }

    /**
     * 导出尽职调查列表
     */
    @PreAuthorize("@ss.hasPermi('crm:dd:export')")
    @Log(title = "尽职调查", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, CrmDd crmDd)
    {
        List<CrmDd> list = crmDdService.selectCrmDdList(crmDd);
        ExcelUtil<CrmDd> util = new ExcelUtil<CrmDd>(CrmDd.class);
        util.exportExcel(response, list, "尽职调查数据");
    }

    /**
     * 获取尽职调查详细信息
     */
    @PreAuthorize("@ss.hasPermi('crm:dd:query')")
    @GetMapping(value = "/{ddId}")
    public AjaxResult getInfo(@PathVariable("ddId") String ddId)
    {
        return success(crmDdService.selectCrmDdByDdId(ddId));
    }

    /**
     * 新增尽职调查
     */
    @PreAuthorize("@ss.hasPermi('crm:dd:add')")
    @Log(title = "尽职调查", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody CrmDd crmDd)
    {
        return toAjax(crmDdService.insertCrmDd(crmDd));
    }

    /**
     * 修改尽职调查
     */
    @PreAuthorize("@ss.hasPermi('crm:dd:edit')")
    @Log(title = "尽职调查", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody CrmDd crmDd)
    {
        return toAjax(crmDdService.updateCrmDd(crmDd));
    }

    /**
     * 删除尽职调查
     */
    @PreAuthorize("@ss.hasPermi('crm:dd:remove')")
    @Log(title = "尽职调查", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ddIds}")
    public AjaxResult remove(@PathVariable String[] ddIds)
    {
        return toAjax(crmDdService.deleteCrmDdByDdIds(ddIds));
    }
}
