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
import com.ruoyi.crm.domain.CrmMarketEvent;
import com.ruoyi.crm.domain.dto.MarketEventBatchRequest;
import com.ruoyi.crm.domain.dto.MarketEventBatchResult;
import com.ruoyi.crm.service.ICrmMarketEventService;

/**
 * 市场活动库 Controller（Next /database）
 */
@RestController
@RequestMapping("/crm/market-event")
public class CrmMarketEventController extends BaseController
{
    private static final int DEFAULT_PAGE_SIZE = 100;
    private static final int MAX_PAGE_SIZE = 500;

    @Autowired
    private ICrmMarketEventService crmMarketEventService;

    /**
     * 分页列表（含筛选）
     */
    @PreAuthorize("@ss.hasPermi('crm:marketevent:list')")
    @GetMapping("/list")
    public TableDataInfo list(CrmMarketEvent query)
    {
        startPageCapped();
        List<CrmMarketEvent> list = crmMarketEventService.selectCrmMarketEventList(query);
        return getDataTable(list);
    }

    /**
     * 详情
     */
    @PreAuthorize("@ss.hasPermi('crm:marketevent:query')")
    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable("id") String id)
    {
        return success(crmMarketEventService.selectCrmMarketEventById(id));
    }

    /**
     * 新增
     */
    @PreAuthorize("@ss.hasPermi('crm:marketevent:add')")
    @Log(title = "市场活动", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody CrmMarketEvent body)
    {
        return success(crmMarketEventService.insertCrmMarketEvent(body));
    }

    /**
     * 修改
     */
    @PreAuthorize("@ss.hasPermi('crm:marketevent:edit')")
    @Log(title = "市场活动", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody CrmMarketEvent body)
    {
        return success(crmMarketEventService.updateCrmMarketEvent(body));
    }

    /**
     * 逻辑删除
     */
    @PreAuthorize("@ss.hasPermi('crm:marketevent:remove')")
    @Log(title = "市场活动", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public AjaxResult remove(@PathVariable("id") String id)
    {
        return toAjax(crmMarketEventService.deleteCrmMarketEventById(id));
    }

    /**
     * 批量导入（upsert / insert-skip）
     */
    @PreAuthorize("@ss.hasPermi('crm:marketevent:add')")
    @Log(title = "市场活动批量导入", businessType = BusinessType.INSERT)
    @PostMapping("/batch")
    public AjaxResult batch(@RequestBody MarketEventBatchRequest body)
    {
        MarketEventBatchResult result = crmMarketEventService.batch(body);
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
