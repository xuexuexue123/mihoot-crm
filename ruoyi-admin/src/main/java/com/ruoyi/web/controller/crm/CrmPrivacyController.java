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
import com.ruoyi.crm.domain.CrmPrivacy;
import com.ruoyi.crm.service.ICrmPrivacyService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 专属信息Controller
 * 
 * @author luohd
 * @date 2024-02-22
 */
@RestController
@RequestMapping("/crm/privacy")
public class CrmPrivacyController extends BaseController
{
    @Autowired
    private ICrmPrivacyService crmPrivacyService;

    /**
     * 查询专属信息列表
     */
    @PreAuthorize("@ss.hasPermi('crm:privacy:list')")
    @GetMapping("/list")
    public TableDataInfo list(CrmPrivacy crmPrivacy)
    {
        startPage();
        List<CrmPrivacy> list = crmPrivacyService.selectCrmPrivacyList(crmPrivacy);
        return getDataTable(list);
    }

    /**
     * 导出专属信息列表
     */
    @PreAuthorize("@ss.hasPermi('crm:privacy:export')")
    @Log(title = "专属信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, CrmPrivacy crmPrivacy)
    {
        List<CrmPrivacy> list = crmPrivacyService.selectCrmPrivacyList(crmPrivacy);
        ExcelUtil<CrmPrivacy> util = new ExcelUtil<CrmPrivacy>(CrmPrivacy.class);
        util.exportExcel(response, list, "专属信息数据");
    }

    /**
     * 获取专属信息详细信息
     */
    @PreAuthorize("@ss.hasPermi('crm:privacy:query')")
    @GetMapping(value = "/{privacyId}")
    public AjaxResult getInfo(@PathVariable("privacyId") String privacyId)
    {
        return success(crmPrivacyService.selectCrmPrivacyByPrivacyId(privacyId));
    }

    /**
     * 新增专属信息
     */
    @PreAuthorize("@ss.hasPermi('crm:privacy:add')")
    @Log(title = "专属信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody CrmPrivacy crmPrivacy)
    {
        return toAjax(crmPrivacyService.insertCrmPrivacy(crmPrivacy));
    }

    /**
     * 修改专属信息
     */
    @PreAuthorize("@ss.hasPermi('crm:privacy:edit')")
    @Log(title = "专属信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody CrmPrivacy crmPrivacy)
    {
        return toAjax(crmPrivacyService.updateCrmPrivacy(crmPrivacy));
    }

    /**
     * 删除专属信息
     */
    @PreAuthorize("@ss.hasPermi('crm:privacy:remove')")
    @Log(title = "专属信息", businessType = BusinessType.DELETE)
	@DeleteMapping("/{privacyIds}")
    public AjaxResult remove(@PathVariable String[] privacyIds)
    {
        return toAjax(crmPrivacyService.deleteCrmPrivacyByPrivacyIds(privacyIds));
    }
}
