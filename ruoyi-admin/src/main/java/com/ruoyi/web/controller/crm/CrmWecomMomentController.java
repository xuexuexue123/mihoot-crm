package com.ruoyi.web.controller.crm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.crm.domain.CrmWecomConfig;
import com.ruoyi.crm.domain.CrmWecomMaterial;
import com.ruoyi.crm.domain.dto.wecom.WecomMomentCancelTaskRequest;
import com.ruoyi.crm.domain.dto.wecom.WecomMomentCommentsRequest;
import com.ruoyi.crm.domain.dto.wecom.WecomMomentCommentsResponse;
import com.ruoyi.crm.domain.dto.wecom.WecomMomentCreateTaskRequest;
import com.ruoyi.crm.domain.dto.wecom.WecomMomentCreateTaskResponse;
import com.ruoyi.crm.domain.dto.wecom.WecomMomentCustomerListRequest;
import com.ruoyi.crm.domain.dto.wecom.WecomMomentCustomerListResponse;
import com.ruoyi.crm.domain.dto.wecom.WecomMomentListRequest;
import com.ruoyi.crm.domain.dto.wecom.WecomMomentListResponse;
import com.ruoyi.crm.domain.dto.wecom.WecomMomentTaskRequest;
import com.ruoyi.crm.domain.dto.wecom.WecomMomentTaskResponse;
import com.ruoyi.crm.mapper.CrmWecomConfigMapper;
import com.ruoyi.crm.service.ICrmWecomMaterialService;
import com.ruoyi.crm.service.ICrmWecomMomentService;
import com.ruoyi.crm.domain.vo.wecom.WecomMaterialVO;
import com.ruoyi.crm.wecom.WecomAccessTokenService;

/**
 * 企业微信客户朋友圈管理接口。
 * 封装8个企微朋友圈 API 的代理调用。
 */
@RestController
@RequestMapping("/crm/wecom/moment")
public class CrmWecomMomentController extends BaseController
{
    @Autowired
    private ICrmWecomMomentService momentService;

    @Autowired
    private ICrmWecomMaterialService materialService;

    @Autowired
    private CrmWecomConfigMapper wecomConfigMapper;

    @Autowired
    private WecomAccessTokenService wecomAccessTokenService;

    /* ---- token 获取 ---- */

    private String getAccessToken()
    {
        CrmWecomConfig cfg = wecomConfigMapper.selectFirst();
        if (cfg == null || cfg.getCorpId() == null)
        {
            return null;
        }
        return wecomAccessTokenService.getAgentAccessToken(cfg);
    }

    /* ---- #1 获取朋友圈发表列表 ---- */

    @PreAuthorize("@ss.hasPermi('crm:wecom:moment')")
    @PostMapping("/list")
    public AjaxResult list(@RequestBody WecomMomentListRequest request)
    {
        String token = getAccessToken();
        if (token == null)
        {
            return AjaxResult.error("企业微信未配置");
        }
        WecomMomentListResponse resp = momentService.getMomentList(token, request);
        return checkResult(resp);
    }

    /* ---- #2 获取企业发表成员执行情况 ---- */

    @PreAuthorize("@ss.hasPermi('crm:wecom:moment')")
    @PostMapping("/task")
    public AjaxResult task(@RequestBody WecomMomentTaskRequest request)
    {
        String token = getAccessToken();
        if (token == null)
        {
            return AjaxResult.error("企业微信未配置");
        }
        WecomMomentTaskResponse resp = momentService.getMomentTask(token, request);
        return checkResult(resp);
    }

    /* ---- #3 获取发表时选择的可见客户范围 ---- */

    @PreAuthorize("@ss.hasPermi('crm:wecom:moment')")
    @PostMapping("/customer-list")
    public AjaxResult customerList(@RequestBody WecomMomentCustomerListRequest request)
    {
        String token = getAccessToken();
        if (token == null)
        {
            return AjaxResult.error("企业微信未配置");
        }
        WecomMomentCustomerListResponse resp = momentService.getMomentCustomerList(token, request);
        return checkResult(resp);
    }

    /* ---- #4 获取发表后可见的客户列表 ---- */

    @PreAuthorize("@ss.hasPermi('crm:wecom:moment')")
    @PostMapping("/send-result")
    public AjaxResult sendResult(@RequestBody WecomMomentCustomerListRequest request)
    {
        String token = getAccessToken();
        if (token == null)
        {
            return AjaxResult.error("企业微信未配置");
        }
        WecomMomentCustomerListResponse resp = momentService.getMomentSendResult(token, request);
        return checkResult(resp);
    }

    /* ---- #5 获取互动数据（评论+点赞） ---- */

    @PreAuthorize("@ss.hasPermi('crm:wecom:moment')")
    @PostMapping("/comments")
    public AjaxResult comments(@RequestBody WecomMomentCommentsRequest request)
    {
        String token = getAccessToken();
        if (token == null)
        {
            return AjaxResult.error("企业微信未配置");
        }
        WecomMomentCommentsResponse resp = momentService.getMomentComments(token, request);
        return checkResult(resp);
    }

    /* ---- #6 创建发表任务 ---- */

    @PreAuthorize("@ss.hasPermi('crm:wecom:moment')")
    @PostMapping("/create-task")
    public AjaxResult createTask(@RequestBody WecomMomentCreateTaskRequest request)
    {
        String token = getAccessToken();
        if (token == null)
        {
            return AjaxResult.error("企业微信未配置");
        }
        WecomMomentCreateTaskResponse resp = momentService.addMomentTask(token, request);
        return checkResult(resp);
    }

    /* ---- #7 查询任务创建结果 ---- */

    @PreAuthorize("@ss.hasPermi('crm:wecom:moment')")
    @GetMapping("/task-result")
    public AjaxResult taskResult(@RequestParam("jobid") String jobid)
    {
        String token = getAccessToken();
        if (token == null)
        {
            return AjaxResult.error("企业微信未配置");
        }
        WecomMomentCreateTaskResponse resp = momentService.getMomentTaskResult(token, jobid);
        return checkResult(resp);
    }

    /* ---- #8 停止未发送的发表任务 ---- */

    @PreAuthorize("@ss.hasPermi('crm:wecom:moment')")
    @PostMapping("/cancel-task")
    public AjaxResult cancelTask(@RequestBody WecomMomentCancelTaskRequest request)
    {
        String token = getAccessToken();
        if (token == null)
        {
            return AjaxResult.error("企业微信未配置");
        }
        WecomMomentCreateTaskResponse resp = momentService.cancelMomentTask(token, request);
        return checkResult(resp);
    }

    /* ---- 上传朋友圈附件（写入素材库并获取 media_id） ---- */

    @PreAuthorize("@ss.hasPermi('crm:wecom:moment')")
    @PostMapping("/upload-media")
    public AjaxResult uploadMedia(@RequestParam("file") MultipartFile file,
            @RequestParam(value = "media_type", defaultValue = "image") String mediaType)
    {
        if (file == null || file.isEmpty())
        {
            return AjaxResult.error("文件不能为空");
        }
        // 1. 素材上传到服务器并写入 crm_wecom_material 表（attachment_type=1 朋友圈）
        CrmWecomMaterial material = materialService.upload(file, file.getOriginalFilename(), mediaType, 1);
        // 2. 上传素材到企业微信获取 media_id
        material = materialService.uploadToWecom(material.getId());

        WecomMaterialVO vo = toMaterialVO(material);
        return AjaxResult.success(vo);
    }

    private WecomMaterialVO toMaterialVO(CrmWecomMaterial m)
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

    /* ---- 通用错误处理 ---- */

    private AjaxResult checkResult(Object resp)
    {
        if (resp == null)
        {
            return AjaxResult.error("企业微信 API 返回空");
        }
        Integer errcode = getErrcode(resp);
        if (errcode != null && errcode != 0)
        {
            return AjaxResult.error("企业微信返回错误: errcode=" + errcode);
        }
        return AjaxResult.success(resp);
    }

    private Integer getErrcode(Object resp)
    {
        if (resp instanceof WecomMomentListResponse)
            return ((WecomMomentListResponse) resp).getErrcode();
        if (resp instanceof WecomMomentTaskResponse)
            return ((WecomMomentTaskResponse) resp).getErrcode();
        if (resp instanceof WecomMomentCustomerListResponse)
            return ((WecomMomentCustomerListResponse) resp).getErrcode();
        if (resp instanceof WecomMomentCommentsResponse)
            return ((WecomMomentCommentsResponse) resp).getErrcode();
        if (resp instanceof WecomMomentCreateTaskResponse)
            return ((WecomMomentCreateTaskResponse) resp).getErrcode();
        return null;
    }
}
