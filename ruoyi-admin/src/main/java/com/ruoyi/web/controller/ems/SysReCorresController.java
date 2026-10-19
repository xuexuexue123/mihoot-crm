package com.ruoyi.web.controller.ems;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.http.HttpRequest;
import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.aliyun.OCRImage;
import com.ruoyi.common.utils.file.FileUploadUtils;
import com.ruoyi.common.utils.moonshot.Message;
import com.ruoyi.common.utils.moonshot.MoonshotAiUtils;
import com.ruoyi.common.utils.moonshot.RoleEnum;
import com.ruoyi.web.controller.common.CommonController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.ems.domain.SysReCorres;
import com.ruoyi.ems.service.ISysReCorresService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文档Controller
 * 
 * @author luohd
 * @date 2023-05-20
 */
@RestController
@RequestMapping("/ems/SysCorres")
public class SysReCorresController extends BaseController
{
    private static final Logger log = LoggerFactory.getLogger(CommonController.class);

    @Autowired
    private ISysReCorresService sysReCorresService;


    /**
     * 获取文档详细信息
     */
    @PreAuthorize("@ss.hasPermi('ems:SysCorres:query')")
    @GetMapping(value = "/recognize/{corresId}")
    public AjaxResult recognizeGeneral(@PathVariable("corresId") String corresId)
    {
        SysReCorres sysReCorres = sysReCorresService.selectSysReCorresByCorresId(corresId);
        String result = "{}";
                // 本地资源路径
        String localPath = RuoYiConfig.getProfile();//这里的getProfile不
        // 数据库资源地址
        String downloadPath = localPath + StringUtils.substringAfter(sysReCorres.getFileName(), Constants.RESOURCE_PREFIX);

        log.info("recognizeGeneral downloadPath = " + downloadPath);
        try {
            result = OCRImage.recognizeBasic(downloadPath);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return success(result);
    }


    @PostMapping("/upload")
    @ResponseBody
    public AjaxResult addSave(HttpServletRequest request, @RequestParam("file") MultipartFile file, String projectId, String category) throws IOException
    {
        SysReCorres fileInfo = new SysReCorres();
//        System.out.println("file info caseid = " + fileInfo.getCaseId());
        UUID corresId = UUID.randomUUID();
        fileInfo.setCorresId(corresId.toString());
        // 上传文件路径
        String filePath = RuoYiConfig.getUploadPath();
        String tenant= request.getHeader("tenant");
        filePath = filePath + "/" + tenant;

        // 上传并返回新文件名称
        fileInfo.setFileSize(file.getSize());
        fileInfo.setFileTitle(file.getOriginalFilename());
        fileInfo.setFileType(file.getContentType());
        fileInfo.setCreateBy(SecurityUtils.getUsername());
        fileInfo.setCaseId(projectId);
        fileInfo.setCategory(category);

        String fileName = FileUploadUtils.upload(filePath, file);
        fileInfo.setFileName(fileName);
//        return toAjax();
        int result = sysReCorresService.insertSysReCorres(fileInfo);


        List<Message> messages = CollUtil.newArrayList(
                new Message(RoleEnum.system.name(), "你是 Kimi，由 Moonshot AI 提供的人工智能助手，你更擅长中文和英文的对话。你会为用户提供安全，有帮助，准确的回答。同时，你会拒绝一切涉及恐怖主义，种族歧视，黄色暴力等问题的回答。Moonshot AI 为专有名词，不可翻译成其他语言。"),
                new Message(RoleEnum.user.name(), "请问3加上5等于多少")
        );

//        String aiview = MoonshotAiUtils.chat("moonshot-v1-8k",messages);
//        System.out.println("aiview = " + aiview);

//        String uploadPath = RuoYiConfig.getUploadPath();
//        uploadPath = uploadPath + "/upload/sample/2024/08/06/5939e728bc7be5790bd6fc8314fdfda7.pdf";
//        File sfile = new File(uploadPath);
//        File sfile = new File("/Users/luohd/Documents/GitHub/mega-ems/uploadPath/upload/sample/2024/08/06/5939e728bc7be5790bd6fc8314fdfda7.pdf");

//        MoonshotAiUtils.uploadFile(sfile);
//        MoonshotAiUtils.deleteFile("cqop2bec2kuh161i0om0");


//        System.out.println("model list : " + MoonshotAiUtils.getFileList());

//        分析行业
//        List<Message> messages = CollUtil.newArrayList(
//                new Message(RoleEnum.system.name(), "你是 Kimi，由 Moonshot AI 提供的人工智能助手，你更擅长中文和英文的对话。你会为用户提供安全，有帮助，准确的回答。同时，你会拒绝一切涉及恐怖主义，种族歧视，黄色暴力等问题的回答。Moonshot AI 为专有名词，不可翻译成其他语言。"),
//                new Message(RoleEnum.system.name(), MoonshotAiUtils.getFileContent("cqoqulilnl95ajq1jv60")),
//                new Message(RoleEnum.system.name(), "你是一位煤炭行业的研究员，请你分析一下文档5939e728bc7be5790bd6fc8314fdfda7.pdf，并且按照如下格式输出行业的投资前景：1. 产业链总体情况；2. 细分行业情况；3. 产业链趋势；4. 提醒需要关注的风险")
//        );

//        new Message(RoleEnum.system.name(), "请提取文件5939e728bc7be5790bd6fc8314fdfda7.pdf的目录结构")
//        MoonshotAiUtils.chat("moonshot-v1-32k",messages);
//        分析行业


        if (result > 0) {
            return AjaxResult.success(fileInfo);

        } else {
            return AjaxResult.error();
        }
    }
    /**
     * 查询文档列表
     */
    @PreAuthorize("@ss.hasPermi('ems:SysCorres:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysReCorres sysReCorres)
    {
        startPage();
        List<SysReCorres> list = sysReCorresService.selectSysReCorresList(sysReCorres);
        return getDataTable(list);
    }

    /**
     * 查询文档列表
     */
    @PreAuthorize("@ss.hasPermi('ems:SysCorres:list')")
    @GetMapping("/listbyfolderid")
    public TableDataInfo listbyfolderid(SysReCorres sysReCorres)
    {
        startPage();
        List<SysReCorres> list = sysReCorresService.selectSysReCorresListByFolderId(sysReCorres);
        return getDataTable(list);
    }
    /**
     * 导出文档列表
     */
    @PreAuthorize("@ss.hasPermi('ems:SysCorres:export')")
    @Log(title = "文档", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysReCorres sysReCorres)
    {
        List<SysReCorres> list = sysReCorresService.selectSysReCorresList(sysReCorres);
        ExcelUtil<SysReCorres> util = new ExcelUtil<SysReCorres>(SysReCorres.class);
        util.exportExcel(response, list, "文档数据");
    }

    /**
     * 获取文档详细信息
     */
    @PreAuthorize("@ss.hasPermi('ems:SysCorres:query')")
    @GetMapping(value = "/{corresId}")
    public AjaxResult getInfo(@PathVariable("corresId") String corresId)
    {
        return success(sysReCorresService.selectSysReCorresByCorresId(corresId));
    }

    /**
     * 获取文档详细信息
     */
    @PreAuthorize("@ss.hasPermi('ems:SysCorres:query')")
    @GetMapping(value = "/bytitle/{fileTitle}")
    public AjaxResult getInfoByName(@PathVariable("fileTitle") String fileTitle)
    {
        return success(sysReCorresService.selectSysReCorresByFileTitle(fileTitle));
    }

    /**
     * 新增文档
     */
    @PreAuthorize("@ss.hasPermi('ems:SysCorres:add')")
    @Log(title = "文档", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SysReCorres sysReCorres)
    {
        return toAjax(sysReCorresService.insertSysReCorres(sysReCorres));
    }

    /**
     * 修改文档
     */
    @PreAuthorize("@ss.hasPermi('ems:SysCorres:edit')")
    @Log(title = "文档", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SysReCorres sysReCorres)
    {
        return toAjax(sysReCorresService.updateSysReCorres(sysReCorres));
    }

    /**
     * 删除文档
     */
    @PreAuthorize("@ss.hasPermi('ems:SysCorres:remove')")
    @Log(title = "文档", businessType = BusinessType.DELETE)
	@DeleteMapping("/{corresIds}")
    public AjaxResult remove(@PathVariable String[] corresIds)
    {
        return toAjax(sysReCorresService.deleteSysReCorresByCorresIds(corresIds));
    }
}
