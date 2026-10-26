package com.ruoyi.crm.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.uuid.UUID;
import com.ruoyi.crm.domain.CrmCapture;
import com.ruoyi.crm.domain.dto.CapturePublishRequest;
import com.ruoyi.crm.domain.vo.CaptureDetailVo;
import com.ruoyi.crm.domain.vo.CaptureSummaryVo;
import com.ruoyi.crm.mapper.CrmCaptureMapper;
import com.ruoyi.crm.service.ICrmCaptureService;
import com.ruoyi.crm.service.support.CaptureOriginGuard;
import com.ruoyi.crm.service.support.CaptureVoSupport;
import com.ruoyi.ems.domain.SysReTemplate;
import com.ruoyi.ems.service.ISysReTemplateService;

@Service
public class CrmCaptureServiceImpl implements ICrmCaptureService
{
    private static final String SURVEY_CATEGORY = "Survey";

    @Autowired
    private CrmCaptureMapper captureMapper;

    @Autowired
    private ISysReTemplateService sysReTemplateService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CaptureDetailVo publish(CapturePublishRequest request)
    {
        if (request == null || StringUtils.isBlank(request.getTemplateId()))
        {
            throw new ServiceException("templateId 不能为空", HttpStatus.BAD_REQUEST);
        }
        String templateId = request.getTemplateId().trim();
        SysReTemplate template = sysReTemplateService.selectSysReTemplateByTemplateId(templateId);
        if (template == null)
        {
            throw new ServiceException("模版不存在", HttpStatus.NOT_FOUND);
        }
        if (!SURVEY_CATEGORY.equalsIgnoreCase(StringUtils.trim(template.getCategory())))
        {
            throw new ServiceException("仅支持 Survey 类模版", HttpStatus.BAD_REQUEST);
        }
        if (StringUtils.isBlank(template.getContent()))
        {
            throw new ServiceException("模版内容为空，无法发布", HttpStatus.BAD_REQUEST);
        }

        String operator = resolveOperator();
        Date now = DateUtils.getNowDate();
        String title = StringUtils.isNotBlank(template.getTemplateName())
                ? template.getTemplateName().trim() : "";
        boolean hasAllowedOrigins = request.getAllowedOrigins() != null;
        boolean hasSuccessMessage = request.getSuccessMessage() != null;
        String allowedOriginsJson = hasAllowedOrigins
                ? CaptureOriginGuard.serializeOrigins(request.getAllowedOrigins()) : null;
        String successMessage = hasSuccessMessage
                ? (StringUtils.isNotBlank(request.getSuccessMessage())
                        ? request.getSuccessMessage().trim() : "")
                : null;

        CrmCapture existing = captureMapper.selectByTemplateId(templateId);
        if (existing != null)
        {
            existing.setTitle(title);
            existing.setStatus(CrmCapture.STATUS_PUBLISHED);
            existing.setContentSnapshot(template.getContent());
            if (hasSuccessMessage)
            {
                existing.setSuccessMessage(successMessage);
            }
            if (hasAllowedOrigins)
            {
                existing.setAllowedOrigins(allowedOriginsJson != null ? allowedOriginsJson : "");
            }
            existing.setPublishedAt(now);
            existing.setUpdateBy(operator);
            existing.setUpdateTime(now);
            captureMapper.update(existing);
            return CaptureVoSupport.toDetailVo(captureMapper.selectByCaptureId(existing.getCaptureId()));
        }

        CrmCapture created = new CrmCapture();
        created.setCaptureId(UUID.randomUUID().toString());
        created.setTemplateId(templateId);
        created.setTitle(title);
        created.setStatus(CrmCapture.STATUS_PUBLISHED);
        created.setContentSnapshot(template.getContent());
        created.setSuccessMessage(successMessage);
        created.setAllowedOrigins(allowedOriginsJson);
        created.setPublishedAt(now);
        created.setCreateBy(operator);
        created.setCreateTime(now);
        created.setUpdateBy(operator);
        created.setUpdateTime(now);
        captureMapper.insert(created);
        return CaptureVoSupport.toDetailVo(created);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CaptureDetailVo unpublish(String captureId)
    {
        CrmCapture capture = requireCapture(captureId);
        Date now = DateUtils.getNowDate();
        capture.setStatus(CrmCapture.STATUS_UNPUBLISHED);
        capture.setUnpublishedAt(now);
        capture.setUpdateBy(resolveOperator());
        capture.setUpdateTime(now);
        captureMapper.update(capture);
        return CaptureVoSupport.toDetailVo(captureMapper.selectByCaptureId(capture.getCaptureId()));
    }

    @Override
    public CaptureDetailVo getDetail(String captureId)
    {
        return CaptureVoSupport.toDetailVo(requireCapture(captureId));
    }

    @Override
    public List<CaptureSummaryVo> list(String templateId, String status)
    {
        List<CrmCapture> rows = captureMapper.selectList(
                StringUtils.isNotBlank(templateId) ? templateId.trim() : null,
                StringUtils.isNotBlank(status) ? status.trim() : null);
        List<CaptureSummaryVo> result = new ArrayList<>(rows.size());
        for (CrmCapture row : rows)
        {
            result.add(CaptureVoSupport.toSummaryVo(row));
        }
        return result;
    }

    private CrmCapture requireCapture(String captureId)
    {
        if (StringUtils.isBlank(captureId))
        {
            throw new ServiceException("captureId 不能为空", HttpStatus.BAD_REQUEST);
        }
        CrmCapture capture = captureMapper.selectByCaptureId(captureId.trim());
        if (capture == null)
        {
            throw new ServiceException("Capture 不存在", HttpStatus.NOT_FOUND);
        }
        return capture;
    }

    private static String resolveOperator()
    {
        try
        {
            String username = SecurityUtils.getUsername();
            return StringUtils.isNotBlank(username) ? username : "System";
        }
        catch (Exception ignored)
        {
            return "System";
        }
    }
}
