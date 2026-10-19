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
import com.ruoyi.ems.domain.SysReBoard;
import com.ruoyi.ems.service.ISysReBoardService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 项目阶段Controller
 * 
 * @author luohd
 * @date 2023-04-07
 */
@RestController
@RequestMapping("/ems/board")
public class SysReBoardController extends BaseController
{
    @Autowired
    private ISysReBoardService sysReBoardService;

    /**
     * 查询项目阶段列表
     */
    @PreAuthorize("@ss.hasPermi('ems:board:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysReBoard sysReBoard)
    {
        startPage();
        List<SysReBoard> list = sysReBoardService.selectSysReBoardList(sysReBoard);
        return getDataTable(list);
    }

    /**
     * 查询项目阶段列表
     */
    @PreAuthorize("@ss.hasPermi('ems:board:list')")
    @GetMapping("/only/list")
    public TableDataInfo listBoardOnly(SysReBoard sysReBoard)
    {
        startPage();
        List<SysReBoard> list = sysReBoardService.selectSysReBoardOnlyList(sysReBoard);
        return getDataTable(list);
    }
    /**
     * 导出项目阶段列表
     */
    @PreAuthorize("@ss.hasPermi('ems:board:export')")
    @Log(title = "项目阶段", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysReBoard sysReBoard)
    {
        List<SysReBoard> list = sysReBoardService.selectSysReBoardList(sysReBoard);
        ExcelUtil<SysReBoard> util = new ExcelUtil<SysReBoard>(SysReBoard.class);
        util.exportExcel(response, list, "项目阶段数据");
    }

    /**
     * 获取项目阶段详细信息
     */
    @PreAuthorize("@ss.hasPermi('ems:board:query')")
    @GetMapping(value = "/{pyId}")
    public AjaxResult getInfo(@PathVariable("pyId") String pyId)
    {
        return success(sysReBoardService.selectSysReBoardByPyId(pyId));
    }
    /**
     * 获取项目阶段详细信息
     */
    @PreAuthorize("@ss.hasPermi('ems:board:query')")
    @GetMapping(value = "/project/{projectId}")
    public AjaxResult getProjectBoardInfo(@PathVariable("projectId") String projectId)
    {
        return success(sysReBoardService.selectSysReBoardByProjectId(projectId));
    }

    /**
     * 新增项目阶段
     */
    @PreAuthorize("@ss.hasPermi('ems:board:add')")
    @Log(title = "项目阶段", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SysReBoard sysReBoard)
    {
        return toAjax(sysReBoardService.insertSysReBoard(sysReBoard));
    }

    /**
     * 修改项目阶段
     */
    @PreAuthorize("@ss.hasPermi('ems:board:edit')")
    @Log(title = "项目阶段", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SysReBoard sysReBoard)
    {
        return toAjax(sysReBoardService.updateSysReBoard(sysReBoard));
    }

    /**
     * 删除项目阶段
     */
    @PreAuthorize("@ss.hasPermi('ems:board:remove')")
    @Log(title = "项目阶段", businessType = BusinessType.DELETE)
	@DeleteMapping("/{pyIds}")
    public AjaxResult remove(@PathVariable String[] pyIds)
    {
        return toAjax(sysReBoardService.deleteSysReBoardByPyIds(pyIds));
    }


    /**
     * 对传入的stage进行重新排序
     * @param sysReBoards
     * @return
     */
    @PreAuthorize("@ss.hasPermi('ems:board:edit')")
    @Log(title = "项目阶段", businessType = BusinessType.UPDATE)
    @PutMapping(value = "/reorder")
    public AjaxResult reorderSysReBoard(@RequestBody SysReBoard[] sysReBoards) {
        for (int i = 0; i < sysReBoards.length; i++) {
            SysReBoard board = sysReBoards[i];
            sysReBoardService.updateSysReBoard(board);
        }
        return AjaxResult.success("Reorder successfully");
    }
}
