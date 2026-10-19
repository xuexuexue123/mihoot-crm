package com.ruoyi.web.controller.ems;

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
import com.ruoyi.ems.domain.SysReProject;
import com.ruoyi.ems.service.ISysReProjectService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 项目Controller
 * 
 * @author luohd
 * @date 2023-04-07
 */
@RestController
@RequestMapping("/ems/project")
public class SysReProjectController extends BaseController
{
    @Autowired
    private ISysReProjectService sysReProjectService;

    /**
     * 查询项目列表
     */
    @PreAuthorize("@ss.hasPermi('ems:project:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysReProject sysReProject)
    {
        startPage();
        List<SysReProject> list = sysReProjectService.selectSysReProjectList(sysReProject);
        return getDataTable(list);
    }

    /**
     * 查询项目列表
     */
    @PreAuthorize("@ss.hasPermi('ems:project:list')")
    @GetMapping("/listbypart/{partName}")
    public TableDataInfo listbypart(@PathVariable("partName") String partName)
    {
        startPage();
        List<SysReProject> list = sysReProjectService.selectSysReProjectListByPartName(partName);
        return getDataTable(list);
    }
    /**
     * 导出项目列表
     */
    @PreAuthorize("@ss.hasPermi('ems:project:export')")
    @Log(title = "项目", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysReProject sysReProject)
    {
        List<SysReProject> list = sysReProjectService.selectSysReProjectList(sysReProject);
        ExcelUtil<SysReProject> util = new ExcelUtil<SysReProject>(SysReProject.class);
        util.exportExcel(response, list, "项目数据");
    }

    /**
     * 获取项目详细信息
     */
    @PreAuthorize("@ss.hasPermi('ems:project:query')")
    @GetMapping(value = "/{pyId}")
    public AjaxResult getInfo(@PathVariable("pyId") String pyId)
    {
        return success(sysReProjectService.selectSysReProjectByPyId(pyId));
    }

    /**
     * 新增项目
     */
    @PreAuthorize("@ss.hasPermi('ems:project:add')")
    @Log(title = "项目", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SysReProject sysReProject)
    {
        return toAjax(sysReProjectService.insertSysReProject(sysReProject));
    }

    /**
     * 修改项目
     */
    @PreAuthorize("@ss.hasPermi('ems:project:edit')")
    @Log(title = "项目", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SysReProject sysReProject)
    {
        return toAjax(sysReProjectService.updateSysReProject(sysReProject));
    }

    /**
     * 删除项目
     */
    @PreAuthorize("@ss.hasPermi('ems:project:remove')")
    @Log(title = "项目", businessType = BusinessType.DELETE)
	@DeleteMapping("/{pyIds}")
    public AjaxResult remove(@PathVariable String[] pyIds)
    {
        if (pyIds.length == 1) {
            String pyId = pyIds[0];

            sysReProjectService.deleteSysReBoardByPyId(pyId);
            sysReProjectService.deleteSysReWorktaskByPyId(pyId);
            sysReProjectService.deleteSysReAcceptanceByPyId(pyId);
            sysReProjectService.deleteSysReParticipantByPyId(pyId);
            sysReProjectService.deleteSysRePmchangeByPyId(pyId);
            sysReProjectService.deleteSysRePmriskByPyId(pyId);
            sysReProjectService.deleteSysReTalkingplanByPyId(pyId);
            sysReProjectService.deleteSysCorresByPyId(pyId);
        }
        return toAjax(sysReProjectService.deleteSysReProjectByPyIds(pyIds));
    }
}
