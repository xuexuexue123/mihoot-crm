package com.ruoyi.web.controller.crm;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.crm.domain.dto.WebsiteAssetQuery;
import com.ruoyi.crm.service.ICrmTenantWebsiteAssetService;

@RestController
@RequestMapping("/crm/tenant-website/assets")
public class CrmTenantWebsiteAssetController extends BaseController
{
    @Autowired
    private ICrmTenantWebsiteAssetService tenantWebsiteAssetService;

    @PreAuthorize("@ss.hasPermi('crm:tenantwebsite:asset:upload')")
    @Log(title = "官网素材", businessType = BusinessType.INSERT)
    @PostMapping("/upload")
    public AjaxResult upload(
            HttpServletRequest request,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "fileTitle", required = false) String fileTitle)
    {
        String tenant = request.getHeader("tenant");
        return AjaxResult.success(tenantWebsiteAssetService.upload(file, fileTitle, tenant));
    }

    @PreAuthorize("@ss.hasPermi('crm:tenantwebsite:asset:list')")
    @GetMapping("/list")
    public TableDataInfo list(HttpServletRequest request, WebsiteAssetQuery query)
    {
        startPage();
        String tenant = request.getHeader("tenant");
        List<?> rows = tenantWebsiteAssetService.list(query, tenant);
        return getDataTable(rows);
    }

    @PreAuthorize("@ss.hasPermi('crm:tenantwebsite:asset:list')")
    @GetMapping("/{id}")
    public AjaxResult get(HttpServletRequest request, @PathVariable String id)
    {
        String tenant = request.getHeader("tenant");
        return AjaxResult.success(tenantWebsiteAssetService.getById(id, tenant));
    }

    @PreAuthorize("@ss.hasPermi('crm:tenantwebsite:asset:delete')")
    @Log(title = "官网素材", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public AjaxResult delete(@PathVariable String id)
    {
        tenantWebsiteAssetService.softDelete(id);
        return AjaxResult.success();
    }
}
