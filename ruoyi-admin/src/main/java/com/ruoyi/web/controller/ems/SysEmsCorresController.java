package com.ruoyi.web.controller.ems;

import java.io.IOException;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.file.FileUploadUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.ems.domain.SysEmsCorres;
import com.ruoyi.ems.service.ISysEmsCorresService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件信息Controller
 * 
 * @author luohd
 * @date 2022-11-30
 */
@RestController
@RequestMapping("/ems/corres")
public class SysEmsCorresController extends BaseController
{
    @Autowired
    private ISysEmsCorresService sysEmsCorresService;

    @PostMapping("/upload")
    @ResponseBody
    public AjaxResult addSave(HttpServletRequest request, @RequestParam("file") MultipartFile file, String caseId) throws IOException
    {
        SysEmsCorres fileInfo = new SysEmsCorres();
//        logger.info("file info caseid = " + fileInfo.getCaseId());
        String tenant= request.getHeader("tenant");
        // 上传文件路径
        String filePath = RuoYiConfig.getUploadPath();
        //添加tenant到上传目录中

        filePath = filePath + "/" + tenant;
//        logger.info("file path filePath = " + filePath);

        // 上传并返回新文件名称
        fileInfo.setFileSize(file.getSize());
        fileInfo.setFileTitle(file.getOriginalFilename());
        fileInfo.setFileType(file.getContentType());
        fileInfo.setCreateBy(SecurityUtils.getUsername());
        fileInfo.setCaseId(caseId);

        String fileName = FileUploadUtils.upload(filePath, file);
        fileInfo.setFileName(fileName);
        return toAjax(sysEmsCorresService.insertSysEmsCorres(fileInfo));
    }
    /**
     * 查询文件信息列表
     */
    @PreAuthorize("@ss.hasPermi('ems:corres:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysEmsCorres sysEmsCorres)
    {
        startPage();
        List<SysEmsCorres> list = sysEmsCorresService.selectSysEmsCorresList(sysEmsCorres);
        return getDataTable(list);
    }

    /**
     * 查询文件信息列表By CaseId
     */
    @PreAuthorize("@ss.hasPermi('ems:corres:list')")
    @GetMapping("/list/{caseId}")
    public TableDataInfo listByCaseId(@PathVariable("caseId") String caseId)
    {
        startPage();
        List<SysEmsCorres> list = sysEmsCorresService.selectSysEmsCorresListByCaseId(caseId);
        return getDataTable(list);
    }
    /**
     * 导出文件信息列表
     */
    @PreAuthorize("@ss.hasPermi('ems:corres:export')")
    @Log(title = "文件信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysEmsCorres sysEmsCorres)
    {
        List<SysEmsCorres> list = sysEmsCorresService.selectSysEmsCorresList(sysEmsCorres);
        ExcelUtil<SysEmsCorres> util = new ExcelUtil<SysEmsCorres>(SysEmsCorres.class);
        util.exportExcel(response, list, "文件信息数据");
    }

    /**
     * 获取文件信息详细信息
     */
    @PreAuthorize("@ss.hasPermi('ems:corres:query')")
    @GetMapping(value = "/{corresId}")
    public AjaxResult getInfo(@PathVariable("corresId") Long corresId)
    {
        return success(sysEmsCorresService.selectSysEmsCorresByCorresId(corresId));
    }

    /**
     * 新增文件信息
     */
    @PreAuthorize("@ss.hasPermi('ems:corres:add')")
    @Log(title = "文件信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SysEmsCorres sysEmsCorres)
    {
        return toAjax(sysEmsCorresService.insertSysEmsCorres(sysEmsCorres));
    }

    /**
     * 修改文件信息
     */
    @PreAuthorize("@ss.hasPermi('ems:corres:edit')")
    @Log(title = "文件信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SysEmsCorres sysEmsCorres)
    {
        return toAjax(sysEmsCorresService.updateSysEmsCorres(sysEmsCorres));
    }

    /**
     * 删除文件信息
     */
    @PreAuthorize("@ss.hasPermi('ems:corres:remove')")
    @Log(title = "文件信息", businessType = BusinessType.DELETE)
	@DeleteMapping("/{corresIds}")
    public AjaxResult remove(@PathVariable Long[] corresIds)
    {
        return toAjax(sysEmsCorresService.deleteSysEmsCorresByCorresIds(corresIds));
    }
}
