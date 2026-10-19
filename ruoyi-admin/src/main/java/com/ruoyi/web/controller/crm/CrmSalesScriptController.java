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
import org.springframework.web.bind.annotation.RestController;

import com.github.pagehelper.PageHelper;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.PageDomain;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.core.page.TableSupport;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.sql.SqlUtil;
import com.ruoyi.crm.domain.CrmSalesScript;
import com.ruoyi.crm.service.ICrmSalesScriptService;

/**
 * 话术库 Controller（侧边栏 + 管理端）
 */
@RestController
@RequestMapping("/crm/salesscript")
public class CrmSalesScriptController extends BaseController
{
    private static final int MAX_PAGE_SIZE = 100;

    @Autowired
    private ICrmSalesScriptService crmSalesScriptService;

    /**
     * 分页列表；侧边栏建议传 status=0
     */
    @PreAuthorize("@ss.hasPermi('crm:salesscript:list')")
    @GetMapping("/list")
    public TableDataInfo list(CrmSalesScript query)
    {
        startPageCapped();
        List<CrmSalesScript> list = crmSalesScriptService.selectCrmSalesScriptList(query);
        return getDataTable(list);
    }

    /**
     * 去重分类名（筛选器）
     */
    @PreAuthorize("@ss.hasPermi('crm:salesscript:list')")
    @GetMapping("/categories")
    public AjaxResult categories()
    {
        return success(crmSalesScriptService.selectDistinctCategories());
    }

    /**
     * 详情
     */
    @PreAuthorize("@ss.hasPermi('crm:salesscript:query')")
    @GetMapping("/{scriptId}")
    public AjaxResult getInfo(@PathVariable("scriptId") String scriptId)
    {
        return success(crmSalesScriptService.selectCrmSalesScriptByScriptId(scriptId));
    }

    /**
     * 新增
     */
    @PreAuthorize("@ss.hasPermi('crm:salesscript:add')")
    @Log(title = "话术库", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody CrmSalesScript body)
    {
        return toAjax(crmSalesScriptService.insertCrmSalesScript(body));
    }

    /**
     * 修改
     */
    @PreAuthorize("@ss.hasPermi('crm:salesscript:edit')")
    @Log(title = "话术库", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody CrmSalesScript body)
    {
        return toAjax(crmSalesScriptService.updateCrmSalesScript(body));
    }

    /**
     * 逻辑删除
     */
    @PreAuthorize("@ss.hasPermi('crm:salesscript:remove')")
    @Log(title = "话术库", businessType = BusinessType.DELETE)
    @DeleteMapping("/{scriptId}")
    public AjaxResult remove(@PathVariable("scriptId") String scriptId)
    {
        return toAjax(crmSalesScriptService.deleteCrmSalesScriptByScriptId(scriptId));
    }

    private void startPageCapped()
    {
        PageDomain pageDomain = TableSupport.buildPageRequest();
        Integer pageNum = pageDomain.getPageNum();
        Integer pageSize = pageDomain.getPageSize();
        if (pageSize != null && pageSize > MAX_PAGE_SIZE)
        {
            pageSize = MAX_PAGE_SIZE;
        }
        String orderBy = SqlUtil.escapeOrderBySql(pageDomain.getOrderBy());
        PageHelper.startPage(pageNum, pageSize, orderBy).setReasonable(pageDomain.getReasonable());
    }
}
