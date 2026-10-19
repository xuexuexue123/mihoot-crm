package com.ruoyi.crm.service;

import com.ruoyi.crm.domain.dto.CaptureSubmitRequest;
import com.ruoyi.crm.domain.vo.CaptureSubmitResultVo;
import com.ruoyi.crm.domain.vo.PublicCaptureDefinitionVo;

/**
 * Captures 公开 API
 */
public interface ICrmPublicCaptureService
{
    PublicCaptureDefinitionVo getPublished(String captureId, String tenantSlug, String requestOrigin);

    CaptureSubmitResultVo submit(String captureId, CaptureSubmitRequest request, String requestOrigin);
}
