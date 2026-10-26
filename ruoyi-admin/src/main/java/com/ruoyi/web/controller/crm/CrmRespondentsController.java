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
import com.ruoyi.crm.domain.CrmRespondents;
import com.ruoyi.crm.service.ICrmRespondentsService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 受访者Controller
 * 
 * @author luohd
 * @date 2025-03-29
 */
@RestController
@RequestMapping("/crm/respondents")
public class CrmRespondentsController extends BaseController
{
    @Autowired
    private ICrmRespondentsService crmRespondentsService;

    /**
     * 查询受访者列表
     */
    @PreAuthorize("@ss.hasPermi('crm:respondents:list')")
    @GetMapping("/list")
    public TableDataInfo list(CrmRespondents crmRespondents)
    {
        startPage();
        List<CrmRespondents> list = crmRespondentsService.selectCrmRespondentsList(crmRespondents);
        return getDataTable(list);
    }

    /**
     * 导出受访者列表
     */
    @PreAuthorize("@ss.hasPermi('crm:respondents:export')")
    @Log(title = "受访者", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, CrmRespondents crmRespondents)
    {
        List<CrmRespondents> list = crmRespondentsService.selectCrmRespondentsList(crmRespondents);
        ExcelUtil<CrmRespondents> util = new ExcelUtil<CrmRespondents>(CrmRespondents.class);
        util.exportExcel(response, list, "受访者数据");
    }

    /**
     * 获取受访者详细信息
     */
    @PreAuthorize("@ss.hasPermi('crm:respondents:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") String id)
    {
        return success(crmRespondentsService.selectCrmRespondentsById(id));
    }

    /**
     * 新增受访者
     */
    @PreAuthorize("@ss.hasPermi('crm:respondents:add')")
    @Log(title = "受访者", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody CrmRespondents crmRespondents)
    {
        return toAjax(crmRespondentsService.insertCrmRespondents(crmRespondents));
    }

    /**
     * 修改受访者
     */
    @PreAuthorize("@ss.hasPermi('crm:respondents:edit')")
    @Log(title = "受访者", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody CrmRespondents crmRespondents)
    {
        return toAjax(crmRespondentsService.updateCrmRespondents(crmRespondents));
    }

    /**
     * 删除受访者
     */
    @PreAuthorize("@ss.hasPermi('crm:respondents:remove')")
    @Log(title = "受访者", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable String[] ids)
    {
        return toAjax(crmRespondentsService.deleteCrmRespondentsByIds(ids));
    }
}
