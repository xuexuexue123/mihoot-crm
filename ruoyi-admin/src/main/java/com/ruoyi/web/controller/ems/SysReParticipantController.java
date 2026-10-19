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
import com.ruoyi.ems.domain.SysReParticipant;
import com.ruoyi.ems.service.ISysReParticipantService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 项目干系人Controller
 * 
 * @author luohd
 * @date 2023-05-19
 */
@RestController
@RequestMapping("/ems/participant")
public class SysReParticipantController extends BaseController
{
    @Autowired
    private ISysReParticipantService sysReParticipantService;

    /**
     * 查询项目干系人列表
     */
    @PreAuthorize("@ss.hasPermi('ems:participant:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysReParticipant sysReParticipant)
    {
        startPage();
        List<SysReParticipant> list = sysReParticipantService.selectSysReParticipantList(sysReParticipant);
        return getDataTable(list);
    }


    /**
     * 查询项目团队成员列表
     */
    @PreAuthorize("@ss.hasPermi('ems:participant:list')")
    @GetMapping("/team/list/")
    public TableDataInfo listTeamMember(SysReParticipant sysReParticipant)
    {
        startPage();
        List<SysReParticipant> list = sysReParticipantService.selectAllTeamMemberList(sysReParticipant);
        return getDataTable(list);
    }


    /**
     * 查询项目干系人成员列表
     */
    @PreAuthorize("@ss.hasPermi('ems:participant:list')")
    @GetMapping("/all/list/")
    public TableDataInfo listAllParticipant(SysReParticipant sysReParticipant)
    {
        startPage();
        List<SysReParticipant> list = sysReParticipantService.selectOtherParticipantList(sysReParticipant);
        return getDataTable(list);
    }
    /**
     * 导出项目干系人列表
     */
    @PreAuthorize("@ss.hasPermi('ems:participant:export')")
    @Log(title = "项目干系人", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysReParticipant sysReParticipant)
    {
        List<SysReParticipant> list = sysReParticipantService.selectSysReParticipantList(sysReParticipant);
        ExcelUtil<SysReParticipant> util = new ExcelUtil<SysReParticipant>(SysReParticipant.class);
        util.exportExcel(response, list, "项目干系人数据");
    }

    /**
     * 获取项目干系人详细信息
     */
    @PreAuthorize("@ss.hasPermi('ems:participant:query')")
    @GetMapping(value = "/{partId}")
    public AjaxResult getInfo(@PathVariable("partId") String partId)
    {
        return success(sysReParticipantService.selectSysReParticipantByPartId(partId));
    }

    /**
     * 新增项目干系人
     */
    @PreAuthorize("@ss.hasPermi('ems:participant:add')")
    @Log(title = "项目干系人", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SysReParticipant sysReParticipant)
    {
        return toAjax(sysReParticipantService.insertSysReParticipant(sysReParticipant));
    }

    /**
     * 修改项目干系人
     */
    @PreAuthorize("@ss.hasPermi('ems:participant:edit')")
    @Log(title = "项目干系人", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SysReParticipant sysReParticipant)
    {
        return toAjax(sysReParticipantService.updateSysReParticipant(sysReParticipant));
    }

    /**
     * 删除项目干系人
     */
    @PreAuthorize("@ss.hasPermi('ems:participant:remove')")
    @Log(title = "项目干系人", businessType = BusinessType.DELETE)
	@DeleteMapping("/{partIds}")
    public AjaxResult remove(@PathVariable String[] partIds)
    {
        return toAjax(sysReParticipantService.deleteSysReParticipantByPartIds(partIds));
    }
}
