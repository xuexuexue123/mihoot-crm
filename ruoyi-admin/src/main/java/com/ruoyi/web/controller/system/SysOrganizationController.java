package com.ruoyi.web.controller.system;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.ruoyi.common.core.domain.entity.SysOrganization;
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
import com.ruoyi.crm.knowledge.CrmKnowledgeIngestService;
import com.ruoyi.system.service.ISysOrganizationService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 组织管理Controller
 * 
 * @author luohd
 * @date 2024-02-28
 */
@RestController
@RequestMapping("/system/organization")
public class SysOrganizationController extends BaseController
{
    @Autowired
    private ISysOrganizationService sysOrganizationService;

    @Autowired
    private CrmKnowledgeIngestService knowledgeIngestService;

    /**
     * 查询组织管理列表
     */
    @PreAuthorize("@ss.hasPermi('system:organization:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysOrganization sysOrganization)
    {
        startPage();
        List<SysOrganization> list = sysOrganizationService.selectSysOrganizationList(sysOrganization);
        return getDataTable(list);
    }

    /**
     * 导出组织管理列表
     */
    @PreAuthorize("@ss.hasPermi('system:organization:export')")
    @Log(title = "组织管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysOrganization sysOrganization)
    {
        List<SysOrganization> list = sysOrganizationService.selectSysOrganizationList(sysOrganization);
        ExcelUtil<SysOrganization> util = new ExcelUtil<SysOrganization>(SysOrganization.class);
        util.exportExcel(response, list, "组织管理数据");
    }

    /**
     * 获取组织管理详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:organization:query')")
    @GetMapping(value = "/{organizationId}")
    public AjaxResult getInfo(@PathVariable("organizationId") String organizationId)
    {
        return success(sysOrganizationService.selectSysOrganizationByOrganizationId(organizationId));
    }

    /**
     * 新增组织管理
     */
    @PreAuthorize("@ss.hasPermi('system:organization:add')")
    @Log(title = "组织管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SysOrganization sysOrganization)
    {
        return toAjax(sysOrganizationService.insertSysOrganization(sysOrganization));
    }

    /**
     * 修改组织管理
     */
    @PreAuthorize("@ss.hasPermi('system:organization:edit')")
    @Log(title = "组织管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SysOrganization sysOrganization)
    {
        int rows = sysOrganizationService.updateSysOrganization(sysOrganization);
        if (rows > 0)
        {
            // 公司主档（id=2）变更 → 同步企业知识库；失败只记日志
            knowledgeIngestService.onOrganizationChanged(sysOrganization.getOrganizationId());
        }
        return toAjax(rows);
    }

    /**
     * 删除组织管理
     */
    @PreAuthorize("@ss.hasPermi('system:organization:remove')")
    @Log(title = "组织管理", businessType = BusinessType.DELETE)
	@DeleteMapping("/{organizationIds}")
    public AjaxResult remove(@PathVariable String[] organizationIds)
    {
        return toAjax(sysOrganizationService.deleteSysOrganizationByOrganizationIds(organizationIds));
    }
}
