package com.ruoyi.web.controller.crm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.crm.domain.CrmWecomMaterial;
import com.ruoyi.crm.domain.dto.wecom.WecomMaterialQueryRequest;
import com.ruoyi.crm.domain.vo.wecom.WecomMaterialVO;
import com.ruoyi.crm.service.ICrmWecomMaterialService;

@RestController
@RequestMapping("/crm/wecom/material")
public class CrmWecomMaterialController extends BaseController
{
    @Autowired
    private ICrmWecomMaterialService crmWecomMaterialService;

    /**
     * 上传素材到服务器
     */
    @PreAuthorize("@ss.hasPermi('crm:wecom:material:upload')")
    @Log(title = "企业微信素材管理", businessType = BusinessType.INSERT)
    @PostMapping("/upload")
    public AjaxResult upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "fileTitle", required = false) String fileTitle,
            @RequestParam("mediaType") String mediaType,
            @RequestParam(value = "attachmentType", defaultValue = "0") Integer attachmentType)
    {
        CrmWecomMaterial material = crmWecomMaterialService.upload(file, fileTitle, mediaType, attachmentType);
        return AjaxResult.success(toVO(material));
    }

    /**
     * 素材列表查询
     */
    @PreAuthorize("@ss.hasPermi('crm:wecom:material:list')")
    @GetMapping("/list")
    public TableDataInfo list(WecomMaterialQueryRequest query)
    {
        startPage();
        List<CrmWecomMaterial> list = crmWecomMaterialService.list(query);
        List<WecomMaterialVO> voList = list.stream().map(this::toVO).collect(Collectors.toList());
        return getDataTable(voList);
    }

    /**
     * 上传素材到企业微信
     */
    @PreAuthorize("@ss.hasPermi('crm:wecom:material:upload')")
    @Log(title = "企业微信素材管理", businessType = BusinessType.UPDATE)
    @PostMapping("/{id}/upload-to-wecom")
    public AjaxResult uploadToWecom(@PathVariable("id") Long id)
    {
        try
        {
            CrmWecomMaterial material = crmWecomMaterialService.uploadToWecom(id);
            return AjaxResult.success(toVO(material));
        }
        catch (ServiceException e)
        {
            return AjaxResult.error(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * 获取素材下载URL
     */
    @PreAuthorize("@ss.hasPermi('crm:wecom:material:list')")
    @GetMapping("/{id}/download-url")
    public AjaxResult downloadUrl(@PathVariable("id") Long id)
    {
        CrmWecomMaterial material = crmWecomMaterialService.getById(id);
        String downloadUrl = crmWecomMaterialService.getDownloadUrl(id);
        Map<String, Object> data = new HashMap<>();
        data.put("id", id);
        data.put("fileTitle", material.getFileTitle());
        data.put("originalName", material.getOriginalName());
        data.put("downloadUrl", downloadUrl);
        return AjaxResult.success(data);
    }

    /**
     * 删除素材
     */
    @PreAuthorize("@ss.hasPermi('crm:wecom:material:delete')")
    @Log(title = "企业微信素材管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public AjaxResult delete(@PathVariable("id") Long id)
    {
        crmWecomMaterialService.delete(id);
        return AjaxResult.success("删除成功");
    }

    private WecomMaterialVO toVO(CrmWecomMaterial m)
    {
        WecomMaterialVO vo = new WecomMaterialVO();
        vo.setId(m.getId());
        vo.setFileTitle(m.getFileTitle());
        vo.setOriginalName(m.getOriginalName());
        vo.setFileName(m.getFileName());
        vo.setFileType(m.getFileType());
        vo.setFileSize(m.getFileSize());
        vo.setMediaType(m.getMediaType());
        vo.setMediaId(m.getMediaId());
        vo.setStatus(m.getStatus());
        vo.setErrmsg(m.getErrmsg());
        vo.setCreateBy(m.getCreateBy());
        vo.setCreateTime(m.getCreateTime());
        vo.setUpdateTime(m.getUpdateTime());
        vo.setWecomUploadedAt(m.getWecomUploadedAt());
        vo.setMediaCreatedAt(m.getMediaCreatedAt());
        vo.setExpiresAt(m.getExpiresAt());
        vo.setAttachmentType(m.getAttachmentType());
        return vo;
    }
}
