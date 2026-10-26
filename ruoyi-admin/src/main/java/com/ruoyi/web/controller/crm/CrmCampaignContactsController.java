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
import com.ruoyi.crm.domain.CrmCampaignContacts;
import com.ruoyi.crm.service.ICrmCampaignContactsService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 活动联系人Controller
 * 
 * @author luohd
 * @date 2025-04-06
 */
@RestController
@RequestMapping("/crm/campaigncontacts")
public class CrmCampaignContactsController extends BaseController
{
    @Autowired
    private ICrmCampaignContactsService crmCampaignContactsService;

    /**
     * 查询活动联系人列表
     */
    @PreAuthorize("@ss.hasPermi('crm:campaigncontacts:list')")
    @GetMapping("/list")
    public TableDataInfo list(CrmCampaignContacts crmCampaignContacts)
    {
        startPage();
        List<CrmCampaignContacts> list = crmCampaignContactsService.selectCrmCampaignContactsList(crmCampaignContacts);
        return getDataTable(list);
    }

    /**
     * 导出活动联系人列表
     */
    @PreAuthorize("@ss.hasPermi('crm:campaigncontacts:export')")
    @Log(title = "活动联系人", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, CrmCampaignContacts crmCampaignContacts)
    {
        List<CrmCampaignContacts> list = crmCampaignContactsService.selectCrmCampaignContactsList(crmCampaignContacts);
        ExcelUtil<CrmCampaignContacts> util = new ExcelUtil<CrmCampaignContacts>(CrmCampaignContacts.class);
        util.exportExcel(response, list, "活动联系人数据");
    }

    /**
     * 获取活动联系人详细信息
     */
    @PreAuthorize("@ss.hasPermi('crm:campaigncontacts:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") String id)
    {
        return success(crmCampaignContactsService.selectCrmCampaignContactsById(id));
    }

    /**
     * 新增活动联系人
     */
    @PreAuthorize("@ss.hasPermi('crm:campaigncontacts:add')")
    @Log(title = "活动联系人", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody CrmCampaignContacts crmCampaignContacts)
    {
        return toAjax(crmCampaignContactsService.insertCrmCampaignContacts(crmCampaignContacts));
    }

    /**
     * 修改活动联系人
     */
    @PreAuthorize("@ss.hasPermi('crm:campaigncontacts:edit')")
    @Log(title = "活动联系人", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody CrmCampaignContacts crmCampaignContacts)
    {
        return toAjax(crmCampaignContactsService.updateCrmCampaignContacts(crmCampaignContacts));
    }

    /**
     * 删除活动联系人
     */
    @PreAuthorize("@ss.hasPermi('crm:campaigncontacts:remove')")
    @Log(title = "活动联系人", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable String[] ids)
    {
        return toAjax(crmCampaignContactsService.deleteCrmCampaignContactsByIds(ids));
    }
}
