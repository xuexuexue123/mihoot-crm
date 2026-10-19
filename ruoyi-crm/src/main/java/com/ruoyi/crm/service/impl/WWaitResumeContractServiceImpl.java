package com.ruoyi.crm.service.impl;

import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.uuid.IdUtils;
import com.ruoyi.crm.domain.WWaitResume;
import com.ruoyi.crm.domain.dto.WaitResumePayloadRequest;
import com.ruoyi.crm.domain.dto.WaitResumePreRegisterRequest;
import com.ruoyi.crm.domain.vo.WaitResumeVO;
import com.ruoyi.crm.enums.WaitResumeMode;
import com.ruoyi.crm.mapper.WWaitResumeMapper;
import com.ruoyi.crm.service.IWWaitResumeContractService;
import com.ruoyi.crm.service.support.WaitResumeConverter;
import com.ruoyi.crm.service.support.TenantDataSourceSupport;

@Service
public class WWaitResumeContractServiceImpl implements IWWaitResumeContractService
{
    @Autowired
    private WWaitResumeMapper wWaitResumeMapper;

    @Override
    public WaitResumeVO preRegister(WaitResumePreRegisterRequest request)
    {
        validatePreRegister(request);
        TenantDataSourceSupport.requireTenantKey();

        WWaitResume existing = wWaitResumeMapper.selectWWaitResumeByResumeKey(request.getResumeKey());
        if (existing != null)
        {
            throw new ServiceException("resumeKey 已存在", HttpStatus.CONFLICT);
        }

        WWaitResume row = new WWaitResume();
        row.setId(StringUtils.isNotBlank(request.getId()) ? request.getId() : IdUtils.fastSimpleUUID());
        row.setResumeKey(request.getResumeKey());
        row.setWorkflowId(request.getWorkflowId());
        row.setExecutionId(request.getExecutionId());
        row.setInngestEventId(request.getInngestEventId());
        row.setNodeId(request.getNodeId());
        row.setNodeName(request.getNodeName());
        row.setMode(request.getMode());
        row.setHttpMethod(request.getHttpMethod());
        row.setAuthConfig(request.getAuthConfig());
        row.setResponseConfig(request.getResponseConfig());
        row.setFormSchema(request.getFormSchema());
        row.setExpiresAt(request.getExpiresAt());
        Date now = DateUtils.getNowDate();
        row.setCreatedAt(now);
        row.setUpdatedAt(now);

        try
        {
            wWaitResumeMapper.insertWWaitResume(row);
        }
        catch (DuplicateKeyException e)
        {
            throw new ServiceException("resumeKey 已存在", HttpStatus.CONFLICT);
        }

        WaitResumeVO vo = WaitResumeConverter.toVo(wWaitResumeMapper.selectWWaitResumeById(row.getId()));
        return vo;
    }

    @Override
    public WaitResumeVO getByResumeKey(String resumeKey)
    {
        if (StringUtils.isBlank(resumeKey))
        {
            throw new ServiceException("resumeKey 不能为空", HttpStatus.BAD_REQUEST);
        }
        TenantDataSourceSupport.requireTenantKey();
        WWaitResume row = wWaitResumeMapper.selectWWaitResumeByResumeKey(resumeKey);
        if (row == null)
        {
            throw new ServiceException("wait resume 不存在", HttpStatus.NOT_FOUND);
        }
        return WaitResumeConverter.toVo(row);
    }

    @Override
    public WaitResumeVO resume(String resumeKey, WaitResumePayloadRequest request)
    {
        if (StringUtils.isBlank(resumeKey))
        {
            throw new ServiceException("resumeKey 不能为空", HttpStatus.BAD_REQUEST);
        }
        TenantDataSourceSupport.requireTenantKey();

        WWaitResume row = wWaitResumeMapper.selectWWaitResumeByResumeKey(resumeKey);
        if (row == null)
        {
            throw new ServiceException("wait resume 不存在", HttpStatus.NOT_FOUND);
        }

        if (row.getResumedAt() != null)
        {
            throw new ServiceException("wait resume 已恢复", HttpStatus.GONE);
        }

        if (row.getExpiresAt() != null && !row.getExpiresAt().after(DateUtils.getNowDate()))
        {
            throw new ServiceException("wait resume 已过期", HttpStatus.GONE);
        }

        String payload = resolvePayloadJson(request);
        Date resumedAt = DateUtils.getNowDate();
        int updated = wWaitResumeMapper.updateResumeByKey(resumeKey, payload, resumedAt);
        if (updated == 0)
        {
            throw new ServiceException("wait resume 已恢复或已过期", HttpStatus.GONE);
        }

        return WaitResumeConverter.toVo(wWaitResumeMapper.selectWWaitResumeByResumeKey(resumeKey));
    }

    @Override
    public WaitResumeVO resumeWithPayloadJson(String resumeKey, String payloadJson)
    {
        WaitResumePayloadRequest request = new WaitResumePayloadRequest();
        request.setPayload(payloadJson);
        return resume(resumeKey, request);
    }

    private void validatePreRegister(WaitResumePreRegisterRequest request)
    {
        if (request == null)
        {
            throw new ServiceException("请求体不能为空", HttpStatus.BAD_REQUEST);
        }
        if (StringUtils.isBlank(request.getResumeKey()))
        {
            throw new ServiceException("resumeKey 不能为空", HttpStatus.BAD_REQUEST);
        }
        if (StringUtils.isBlank(request.getWorkflowId()))
        {
            throw new ServiceException("workflowId 不能为空", HttpStatus.BAD_REQUEST);
        }
        if (StringUtils.isBlank(request.getNodeId()))
        {
            throw new ServiceException("nodeId 不能为空", HttpStatus.BAD_REQUEST);
        }
        if (StringUtils.isBlank(request.getMode()))
        {
            throw new ServiceException("mode 不能为空", HttpStatus.BAD_REQUEST);
        }

        WaitResumeMode mode = WaitResumeMode.fromValue(request.getMode());
        if (mode == null)
        {
            throw new ServiceException("mode 必须为 on_webhook_call 或 on_form_submitted", HttpStatus.BAD_REQUEST);
        }

        if (mode == WaitResumeMode.ON_WEBHOOK_CALL)
        {
            if (StringUtils.isBlank(request.getHttpMethod()))
            {
                throw new ServiceException("httpMethod 不能为空", HttpStatus.BAD_REQUEST);
            }
            if (StringUtils.isBlank(request.getResponseConfig()))
            {
                throw new ServiceException("responseConfig 不能为空", HttpStatus.BAD_REQUEST);
            }
        }

        if (mode == WaitResumeMode.ON_FORM_SUBMITTED && StringUtils.isBlank(request.getFormSchema()))
        {
            throw new ServiceException("formSchema 不能为空", HttpStatus.BAD_REQUEST);
        }
    }

    private String resolvePayloadJson(WaitResumePayloadRequest request)
    {
        if (request == null || StringUtils.isBlank(request.getPayload()))
        {
            return "{}";
        }
        return request.getPayload();
    }
}
