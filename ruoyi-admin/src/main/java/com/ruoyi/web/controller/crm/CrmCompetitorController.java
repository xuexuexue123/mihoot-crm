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
import com.ruoyi.crm.domain.CrmCompetitor;
import com.ruoyi.crm.service.ICrmCompetitorService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 竞争对手Controller
 * 
 * @author luohd
 * @date 2024-02-07
 */
@RestController
@RequestMapping("/crm/competitor")
public class CrmCompetitorController extends BaseController
{
    @Autowired
    private ICrmCompetitorService crmCompetitorService;

    /**
     * 查询竞争对手列表
     */
    @PreAuthorize("@ss.hasPermi('crm:competitor:list')")
    @GetMapping("/list")
    public TableDataInfo list(CrmCompetitor crmCompetitor)
    {
        startPage();
        List<CrmCompetitor> list = crmCompetitorService.selectCrmCompetitorList(crmCompetitor);
        return getDataTable(list);
    }

    /**
     * 导出竞争对手列表
     */
    @PreAuthorize("@ss.hasPermi('crm:competitor:export')")
    @Log(title = "竞争对手", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, CrmCompetitor crmCompetitor)
    {
        List<CrmCompetitor> list = crmCompetitorService.selectCrmCompetitorList(crmCompetitor);
        ExcelUtil<CrmCompetitor> util = new ExcelUtil<CrmCompetitor>(CrmCompetitor.class);
        util.exportExcel(response, list, "竞争对手数据");
    }

    /**
     * 获取竞争对手详细信息
     */
    @PreAuthorize("@ss.hasPermi('crm:competitor:query')")
    @GetMapping(value = "/{competitorId}")
    public AjaxResult getInfo(@PathVariable("competitorId") String competitorId)
    {
        return success(crmCompetitorService.selectCrmCompetitorByCompetitorId(competitorId));
    }

    /**
     * 新增竞争对手
     */
    @PreAuthorize("@ss.hasPermi('crm:competitor:add')")
    @Log(title = "竞争对手", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody CrmCompetitor crmCompetitor)
    {
        return toAjax(crmCompetitorService.insertCrmCompetitor(crmCompetitor));
    }

    /**
     * 修改竞争对手
     */
    @PreAuthorize("@ss.hasPermi('crm:competitor:edit')")
    @Log(title = "竞争对手", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody CrmCompetitor crmCompetitor)
    {
        return toAjax(crmCompetitorService.updateCrmCompetitor(crmCompetitor));
    }

    /**
     * 删除竞争对手
     */
    @PreAuthorize("@ss.hasPermi('crm:competitor:remove')")
    @Log(title = "竞争对手", businessType = BusinessType.DELETE)
	@DeleteMapping("/{competitorIds}")
    public AjaxResult remove(@PathVariable String[] competitorIds)
    {
        return toAjax(crmCompetitorService.deleteCrmCompetitorByCompetitorIds(competitorIds));
    }
}
