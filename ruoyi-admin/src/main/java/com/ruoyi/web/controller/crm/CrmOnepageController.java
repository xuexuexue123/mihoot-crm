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
import com.ruoyi.crm.domain.CrmOnepage;
import com.ruoyi.crm.service.ICrmOnepageService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 一页Controller
 * 
 * @author luohd
 * @date 2024-08-15
 */
@RestController
@RequestMapping("/crm/onepage")
public class CrmOnepageController extends BaseController
{
    @Autowired
    private ICrmOnepageService crmOnepageService;

    /**
     * 查询一页列表
     */
    @PreAuthorize("@ss.hasPermi('crm:onepage:list')")
    @GetMapping("/list")
    public TableDataInfo list(CrmOnepage crmOnepage)
    {
        startPage();
        List<CrmOnepage> list = crmOnepageService.selectCrmOnepageList(crmOnepage);
        return getDataTable(list);
    }

    /**
     * 导出一页列表
     */
    @PreAuthorize("@ss.hasPermi('crm:onepage:export')")
    @Log(title = "一页", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, CrmOnepage crmOnepage)
    {
        List<CrmOnepage> list = crmOnepageService.selectCrmOnepageList(crmOnepage);
        ExcelUtil<CrmOnepage> util = new ExcelUtil<CrmOnepage>(CrmOnepage.class);
        util.exportExcel(response, list, "一页数据");
    }

    /**
     * 获取一页详细信息
     */
    @PreAuthorize("@ss.hasPermi('crm:onepage:query')")
    @GetMapping(value = "/{pageId}")
    public AjaxResult getInfo(@PathVariable("pageId") String pageId)
    {
        return success(crmOnepageService.selectCrmOnepageByPageId(pageId));
    }

    /**
     * 获取一页详细信息
     */
    @PreAuthorize("@ss.hasPermi('crm:onepage:query')")
    @GetMapping(value = "/relateid/{relateId}")
    public AjaxResult getRelateInfo(@PathVariable("relateId") String relateId)
    {
        return success(crmOnepageService.selectCrmOnepageByRelateId(relateId));
    }
    /**
     * 新增一页
     */
    @PreAuthorize("@ss.hasPermi('crm:onepage:add')")
    @Log(title = "一页", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody CrmOnepage crmOnepage)
    {
        return toAjax(crmOnepageService.insertCrmOnepage(crmOnepage));
    }

    /**
     * 修改一页
     */
    @PreAuthorize("@ss.hasPermi('crm:onepage:edit')")
    @Log(title = "一页", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody CrmOnepage crmOnepage)
    {
        return toAjax(crmOnepageService.updateCrmOnepage(crmOnepage));
    }

    /**
     * 删除一页
     */
    @PreAuthorize("@ss.hasPermi('crm:onepage:remove')")
    @Log(title = "一页", businessType = BusinessType.DELETE)
	@DeleteMapping("/{pageIds}")
    public AjaxResult remove(@PathVariable String[] pageIds)
    {
        return toAjax(crmOnepageService.deleteCrmOnepageByPageIds(pageIds));
    }
}
