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
import com.ruoyi.crm.domain.CrmPartner;
import com.ruoyi.crm.service.ICrmPartnerService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 合作伙伴Controller
 * 
 * @author luohd
 * @date 2024-03-28
 */
@RestController
@RequestMapping("/crm/partner")
public class CrmPartnerController extends BaseController
{
    @Autowired
    private ICrmPartnerService crmPartnerService;

    /**
     * 查询合作伙伴列表
     */
    @PreAuthorize("@ss.hasPermi('crm:partner:list')")
    @GetMapping("/list")
    public TableDataInfo list(CrmPartner crmPartner)
    {
        startPage();
        List<CrmPartner> list = crmPartnerService.selectCrmPartnerList(crmPartner);
        return getDataTable(list);
    }

    /**
     * 导出合作伙伴列表
     */
    @PreAuthorize("@ss.hasPermi('crm:partner:export')")
    @Log(title = "合作伙伴", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, CrmPartner crmPartner)
    {
        List<CrmPartner> list = crmPartnerService.selectCrmPartnerList(crmPartner);
        ExcelUtil<CrmPartner> util = new ExcelUtil<CrmPartner>(CrmPartner.class);
        util.exportExcel(response, list, "合作伙伴数据");
    }

    /**
     * 获取合作伙伴详细信息
     */
    @PreAuthorize("@ss.hasPermi('crm:partner:query')")
    @GetMapping(value = "/{partnerId}")
    public AjaxResult getInfo(@PathVariable("partnerId") String partnerId)
    {
        return success(crmPartnerService.selectCrmPartnerByPartnerId(partnerId));
    }

    /**
     * 新增合作伙伴
     */
    @PreAuthorize("@ss.hasPermi('crm:partner:add')")
    @Log(title = "合作伙伴", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody CrmPartner crmPartner)
    {
        return toAjax(crmPartnerService.insertCrmPartner(crmPartner));
    }

    /**
     * 修改合作伙伴
     */
    @PreAuthorize("@ss.hasPermi('crm:partner:edit')")
    @Log(title = "合作伙伴", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody CrmPartner crmPartner)
    {
        return toAjax(crmPartnerService.updateCrmPartner(crmPartner));
    }

    /**
     * 删除合作伙伴
     */
    @PreAuthorize("@ss.hasPermi('crm:partner:remove')")
    @Log(title = "合作伙伴", businessType = BusinessType.DELETE)
	@DeleteMapping("/{partnerIds}")
    public AjaxResult remove(@PathVariable String[] partnerIds)
    {
        return toAjax(crmPartnerService.deleteCrmPartnerByPartnerIds(partnerIds));
    }
}
