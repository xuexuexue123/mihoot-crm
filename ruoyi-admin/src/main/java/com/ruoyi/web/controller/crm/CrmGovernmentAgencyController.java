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
import com.ruoyi.crm.domain.CrmGovernmentAgency;
import com.ruoyi.crm.domain.dto.GovernmentAgencyBatchRequest;
import com.ruoyi.crm.domain.dto.GovernmentAgencyBatchResult;
import com.ruoyi.crm.service.ICrmGovernmentAgencyService;

/**
 * 政府招商机构 Controller（Next /database）
 */
@RestController
@RequestMapping("/crm/government-agency")
public class CrmGovernmentAgencyController extends BaseController
{
    private static final int DEFAULT_PAGE_SIZE = 100;
    private static final int MAX_PAGE_SIZE = 500;

    @Autowired
    private ICrmGovernmentAgencyService crmGovernmentAgencyService;

    /**
     * 分页列表（含筛选）
     */
    @PreAuthorize("@ss.hasPermi('crm:governmentagency:list')")
    @GetMapping("/list")
    public TableDataInfo list(CrmGovernmentAgency query)
    {
        startPageCapped();
        List<CrmGovernmentAgency> list = crmGovernmentAgencyService.selectCrmGovernmentAgencyList(query);
        return getDataTable(list);
    }

    /**
     * 详情
     */
    @PreAuthorize("@ss.hasPermi('crm:governmentagency:query')")
    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable("id") String id)
    {
        return success(crmGovernmentAgencyService.selectCrmGovernmentAgencyById(id));
    }

    /**
     * 新增
     */
    @PreAuthorize("@ss.hasPermi('crm:governmentagency:add')")
    @Log(title = "政府招商机构", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody CrmGovernmentAgency body)
    {
        return success(crmGovernmentAgencyService.insertCrmGovernmentAgency(body));
    }

    /**
     * 修改
     */
    @PreAuthorize("@ss.hasPermi('crm:governmentagency:edit')")
    @Log(title = "政府招商机构", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody CrmGovernmentAgency body)
    {
        return success(crmGovernmentAgencyService.updateCrmGovernmentAgency(body));
    }

    /**
     * 逻辑删除
     */
    @PreAuthorize("@ss.hasPermi('crm:governmentagency:remove')")
    @Log(title = "政府招商机构", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public AjaxResult remove(@PathVariable("id") String id)
    {
        return toAjax(crmGovernmentAgencyService.deleteCrmGovernmentAgencyById(id));
    }

    /**
     * 批量导入（upsert / insert-skip）
     */
    @PreAuthorize("@ss.hasPermi('crm:governmentagency:add')")
    @Log(title = "政府招商机构批量导入", businessType = BusinessType.INSERT)
    @PostMapping("/batch")
    public AjaxResult batch(@RequestBody GovernmentAgencyBatchRequest body)
    {
        GovernmentAgencyBatchResult result = crmGovernmentAgencyService.batch(body);
        return success(result);
    }

    private void startPageCapped()
    {
        PageDomain pageDomain = TableSupport.buildPageRequest();
        Integer pageNum = pageDomain.getPageNum();
        Integer pageSize = pageDomain.getPageSize();
        if (pageNum == null || pageNum < 1)
        {
            pageNum = 1;
        }
        if (pageSize == null || pageSize < 1)
        {
            pageSize = DEFAULT_PAGE_SIZE;
        }
        else if (pageSize > MAX_PAGE_SIZE)
        {
            pageSize = MAX_PAGE_SIZE;
        }
        String orderBy = SqlUtil.escapeOrderBySql(pageDomain.getOrderBy());
        PageHelper.startPage(pageNum, pageSize, orderBy).setReasonable(pageDomain.getReasonable());
    }
}
