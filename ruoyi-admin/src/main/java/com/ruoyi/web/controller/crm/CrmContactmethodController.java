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
import com.ruoyi.crm.domain.CrmContactmethod;
import com.ruoyi.crm.service.ICrmContactmethodService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 联系方式Controller
 * 
 * @author luohd
 * @date 2024-03-16
 */
@RestController
@RequestMapping("/crm/contactmethod")
public class CrmContactmethodController extends BaseController
{
    @Autowired
    private ICrmContactmethodService crmContactmethodService;

    /**
     * 查询联系方式列表
     */
    @PreAuthorize("@ss.hasPermi('crm:contactmethod:list')")
    @GetMapping("/list")
    public TableDataInfo list(CrmContactmethod crmContactmethod)
    {
        startPage();
        List<CrmContactmethod> list = crmContactmethodService.selectCrmContactmethodList(crmContactmethod);
        return getDataTable(list);
    }

    /**
     * 导出联系方式列表
     */
    @PreAuthorize("@ss.hasPermi('crm:contactmethod:export')")
    @Log(title = "联系方式", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, CrmContactmethod crmContactmethod)
    {
        List<CrmContactmethod> list = crmContactmethodService.selectCrmContactmethodList(crmContactmethod);
        ExcelUtil<CrmContactmethod> util = new ExcelUtil<CrmContactmethod>(CrmContactmethod.class);
        util.exportExcel(response, list, "联系方式数据");
    }

    /**
     * 获取联系方式详细信息
     */
    @PreAuthorize("@ss.hasPermi('crm:contactmethod:query')")
    @GetMapping(value = "/{methodId}")
    public AjaxResult getInfo(@PathVariable("methodId") String methodId)
    {
        return success(crmContactmethodService.selectCrmContactmethodByMethodId(methodId));
    }

    /**
     * 新增联系方式
     */
    @PreAuthorize("@ss.hasPermi('crm:contactmethod:add')")
    @Log(title = "联系方式", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody CrmContactmethod crmContactmethod)
    {
        return toAjax(crmContactmethodService.insertCrmContactmethod(crmContactmethod));
    }

    /**
     * 修改联系方式
     */
    @PreAuthorize("@ss.hasPermi('crm:contactmethod:edit')")
    @Log(title = "联系方式", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody CrmContactmethod crmContactmethod)
    {
        return toAjax(crmContactmethodService.updateCrmContactmethod(crmContactmethod));
    }

    /**
     * 删除联系方式
     */
    @PreAuthorize("@ss.hasPermi('crm:contactmethod:remove')")
    @Log(title = "联系方式", businessType = BusinessType.DELETE)
	@DeleteMapping("/{methodIds}")
    public AjaxResult remove(@PathVariable String[] methodIds)
    {
        return toAjax(crmContactmethodService.deleteCrmContactmethodByMethodIds(methodIds));
    }
}
