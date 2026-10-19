package com.ruoyi.crm.service;

import java.util.List;

import com.ruoyi.crm.domain.dto.CapturePublishRequest;
import com.ruoyi.crm.domain.vo.CaptureDetailVo;
import com.ruoyi.crm.domain.vo.CaptureSummaryVo;

/**
 * Captures 鉴权管理 API
 */
public interface ICrmCaptureService
{
    CaptureDetailVo publish(CapturePublishRequest request);

    CaptureDetailVo unpublish(String captureId);

    CaptureDetailVo getDetail(String captureId);

    List<CaptureSummaryVo> list(String templateId, String status);
}
