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
import com.ruoyi.crm.domain.WCredential;
import com.ruoyi.crm.service.IWCredentialService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * WCredentialController
 * 
 * @author luohd
 * @date 2026-05-19
 */
@RestController
@RequestMapping("/crm/wcredential")
public class WCredentialController extends BaseController
{
    @Autowired
    private IWCredentialService wCredentialService;

    /**
     * 查询WCredential列表
     */
    @PreAuthorize("@ss.hasPermi('crm:wcredential:list')")
    @GetMapping("/list")
    public TableDataInfo list(WCredential wCredential)
    {
        startPage();
        List<WCredential> list = wCredentialService.selectWCredentialList(wCredential);
        return getDataTable(list);
    }

    /**
     * 导出WCredential列表
     */
    @PreAuthorize("@ss.hasPermi('crm:wcredential:export')")
    @Log(title = "WCredential", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, WCredential wCredential)
    {
        List<WCredential> list = wCredentialService.selectWCredentialList(wCredential);
        ExcelUtil<WCredential> util = new ExcelUtil<WCredential>(WCredential.class);
        util.exportExcel(response, list, "WCredential数据");
    }

    /**
     * 获取WCredential详细信息
     */
    @PreAuthorize("@ss.hasPermi('crm:wcredential:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") String id)
    {
        return success(wCredentialService.selectWCredentialById(id));
    }

    /**
     * 新增WCredential
     */
    @PreAuthorize("@ss.hasPermi('crm:wcredential:add')")
    @Log(title = "WCredential", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody WCredential wCredential)
    {
        return toAjax(wCredentialService.insertWCredential(wCredential));
    }

    /**
     * 修改WCredential
     */
    @PreAuthorize("@ss.hasPermi('crm:wcredential:edit')")
    @Log(title = "WCredential", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody WCredential wCredential)
    {
        return toAjax(wCredentialService.updateWCredential(wCredential));
    }

    /**
     * 删除WCredential
     */
    @PreAuthorize("@ss.hasPermi('crm:wcredential:remove')")
    @Log(title = "WCredential", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable String[] ids)
    {
        return toAjax(wCredentialService.deleteWCredentialByIds(ids));
    }
}
