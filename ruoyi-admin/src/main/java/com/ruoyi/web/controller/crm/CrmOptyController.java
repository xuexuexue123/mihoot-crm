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
import com.ruoyi.crm.domain.CrmOpty;
import com.ruoyi.crm.service.ICrmOptyService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 销售机会Controller
 * 
 * @author luohd
 * @date 2024-01-18
 */
@RestController
@RequestMapping("/crm/opty")
public class CrmOptyController extends BaseController
{
    @Autowired
    private ICrmOptyService crmOptyService;

    /**
     * 查询销售机会列表
     */
    @PreAuthorize("@ss.hasPermi('crm:opty:list')")
    @GetMapping("/list")
    public TableDataInfo list(CrmOpty crmOpty)
    {
        startPage();
        List<CrmOpty> list = crmOptyService.selectCrmOptyList(crmOpty);
        return getDataTable(list);
    }


    /**
     * 查询指定阶段的销售机会列表
     */
    @PreAuthorize("@ss.hasPermi('crm:opty:list')")
    @GetMapping("/liststages")
    public TableDataInfo liststages(CrmOpty crmOpty)
    {
        startPage();
        List<CrmOpty> list = crmOptyService.selectCrmOptyListByStages(crmOpty);
        return getDataTable(list);
    }

    /**
     * 查询本周销售机会列表
     */
    @PreAuthorize("@ss.hasPermi('crm:opty:list')")
    @GetMapping("/listweekly")
    public TableDataInfo listweekly(CrmOpty crmOpty)
    {
        startPage();
        List<CrmOpty> list = crmOptyService.selectCrmOptyListWeekly(crmOpty);
        return getDataTable(list);
    }


    /**
     * 查询本周已赢单销售机会列表
     */
    @PreAuthorize("@ss.hasPermi('crm:opty:list')")
    @GetMapping("/wonlistweekly")
    public TableDataInfo wonlistweekly(CrmOpty crmOpty)
    {
        startPage();
        List<CrmOpty> list = crmOptyService.selectWonCrmOptyListWeekly(crmOpty);
        return getDataTable(list);
    }

    /**
     * 导出销售机会列表
     */
    @PreAuthorize("@ss.hasPermi('crm:opty:export')")
    @Log(title = "销售机会", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, CrmOpty crmOpty)
    {
        List<CrmOpty> list = crmOptyService.selectCrmOptyList(crmOpty);
        ExcelUtil<CrmOpty> util = new ExcelUtil<CrmOpty>(CrmOpty.class);
        util.exportExcel(response, list, "销售机会数据");
    }

    /**
     * 获取销售机会详细信息
     */
    @PreAuthorize("@ss.hasPermi('crm:opty:query')")
    @GetMapping(value = "/{optyId}")
    public AjaxResult getInfo(@PathVariable("optyId") String optyId)
    {
        return success(crmOptyService.selectCrmOptyByOptyId(optyId));
    }

    /**
     * 根据基金ID查询基金投资的所有项目
     */
    @PreAuthorize("@ss.hasPermi('crm:opty:query')")
    @GetMapping(value = "/byaccountid/{accountId}")
    public TableDataInfo getOptyInfo(@PathVariable("accountId") String accountId)
    {
        startPage();
        List<CrmOpty> list = crmOptyService.selectCrmOptyListByAccountId(accountId);
        return getDataTable(list);
    }

    /**
     * 新增销售机会
     */
    @PreAuthorize("@ss.hasPermi('crm:opty:add')")
    @Log(title = "销售机会", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody CrmOpty crmOpty)
    {
        return toAjax(crmOptyService.insertCrmOpty(crmOpty));
    }

    /**
     * 修改销售机会
     */
    @PreAuthorize("@ss.hasPermi('crm:opty:edit')")
    @Log(title = "销售机会", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody CrmOpty crmOpty)
    {
        return toAjax(crmOptyService.updateCrmOpty(crmOpty));
    }

    /**
     * 删除销售机会
     */
    @PreAuthorize("@ss.hasPermi('crm:opty:remove')")
    @Log(title = "销售机会", businessType = BusinessType.DELETE)
	@DeleteMapping("/{optyIds}")
    public AjaxResult remove(@PathVariable String[] optyIds)
    {
        return toAjax(crmOptyService.deleteCrmOptyByOptyIds(optyIds));
    }
}
