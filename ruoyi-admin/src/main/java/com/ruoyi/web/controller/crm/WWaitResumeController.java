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
import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.crm.domain.WWaitResume;
import com.ruoyi.crm.domain.dto.WaitResumePayloadRequest;
import com.ruoyi.crm.domain.dto.WaitResumePreRegisterRequest;
import com.ruoyi.crm.domain.vo.WaitResumeVO;
import com.ruoyi.crm.service.IWWaitResumeContractService;
import com.ruoyi.crm.service.IWWaitResumeService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * WWaitResumeController
 *
 * @author luohd
 * @date 2026-05-19
 */
@RestController
@RequestMapping("/crm/wwaitresume")
public class WWaitResumeController extends BaseController
{
    @Autowired
    private IWWaitResumeService wWaitResumeService;

    @Autowired
    private IWWaitResumeContractService wWaitResumeContractService;

    /**
     * 查询WWaitResume列表
     */
    @PreAuthorize("@ss.hasPermi('crm:wwaitresume:list')")
    @GetMapping("/list")
    public TableDataInfo list(WWaitResume wWaitResume)
    {
        startPage();
        List<WWaitResume> list = wWaitResumeService.selectWWaitResumeList(wWaitResume);
        return getDataTable(list);
    }

    /**
     * 导出WWaitResume列表
     */
    @PreAuthorize("@ss.hasPermi('crm:wwaitresume:export')")
    @Log(title = "WWaitResume", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, WWaitResume wWaitResume)
    {
        List<WWaitResume> list = wWaitResumeService.selectWWaitResumeList(wWaitResume);
        ExcelUtil<WWaitResume> util = new ExcelUtil<WWaitResume>(WWaitResume.class);
        util.exportExcel(response, list, "WWaitResume数据");
    }

    /**
     * 按 resumeKey 查询（匿名 + tenant header；供 notify / 公开回调查 CRM）
     */
    @Anonymous
    @GetMapping("/key/{resumeKey}")
    public AjaxResult getByResumeKey(@PathVariable("resumeKey") String resumeKey)
    {
        WaitResumeVO data = wWaitResumeContractService.getByResumeKey(resumeKey);
        return success(data);
    }

    /**
     * 获取WWaitResume详细信息
     */
    @PreAuthorize("@ss.hasPermi('crm:wwaitresume:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") String id)
    {
        return success(wWaitResumeService.selectWWaitResumeById(id));
    }

    /**
     * 预注册 wait resume（Inngest 工作流启动时）
     */
    @PreAuthorize("@ss.hasPermi('crm:wwaitresume:add')")
    @Log(title = "WWaitResume", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult preRegister(@RequestBody WaitResumePreRegisterRequest request)
    {
        WaitResumeVO data = wWaitResumeContractService.preRegister(request);
        return success(data);
    }

    /**
     * 标记恢复并写入 payload（匿名 + tenant header；供 sevencrm resume / notify）
     */
    @Anonymous
    @Log(title = "WWaitResume", businessType = BusinessType.UPDATE)
    @PutMapping("/key/{resumeKey}")
    public AjaxResult resume(@PathVariable("resumeKey") String resumeKey,
            @RequestBody(required = false) WaitResumePayloadRequest request)
    {
        WaitResumeVO data = wWaitResumeContractService.resume(resumeKey, request);
        return success(data);
    }

    /**
     * 修改WWaitResume（管理页）
     */
    @PreAuthorize("@ss.hasPermi('crm:wwaitresume:edit')")
    @Log(title = "WWaitResume", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody WWaitResume wWaitResume)
    {
        return toAjax(wWaitResumeService.updateWWaitResume(wWaitResume));
    }

    /**
     * 删除WWaitResume
     */
    @PreAuthorize("@ss.hasPermi('crm:wwaitresume:remove')")
    @Log(title = "WWaitResume", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable String[] ids)
    {
        return toAjax(wWaitResumeService.deleteWWaitResumeByIds(ids));
    }
}
