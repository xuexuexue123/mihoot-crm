package com.ruoyi.crm.service.support;

import java.util.List;

import com.ruoyi.crm.domain.CrmCapture;
import com.ruoyi.crm.domain.vo.CaptureDetailVo;
import com.ruoyi.crm.domain.vo.CaptureSummaryVo;

/**
 * Captures VO 组装
 */
public final class CaptureVoSupport
{
    private CaptureVoSupport()
    {
    }

    public static String publicPath(String captureId)
    {
        return "/captures/" + captureId;
    }

    public static CaptureDetailVo toDetailVo(CrmCapture capture)
    {
        if (capture == null)
        {
            return null;
        }
        CaptureDetailVo vo = new CaptureDetailVo();
        vo.setCaptureId(capture.getCaptureId());
        vo.setTemplateId(capture.getTemplateId());
        vo.setTitle(capture.getTitle());
        vo.setStatus(capture.getStatus());
        vo.setContentSnapshot(capture.getContentSnapshot());
        vo.setSuccessMessage(capture.getSuccessMessage());
        List<String> origins = CaptureOriginGuard.parseOrigins(capture.getAllowedOrigins());
        vo.setAllowedOrigins(origins.isEmpty() ? null : origins);
        vo.setPublicPath(publicPath(capture.getCaptureId()));
        vo.setPublishedAt(capture.getPublishedAt());
        vo.setUnpublishedAt(capture.getUnpublishedAt());
        vo.setCreateTime(capture.getCreateTime());
        vo.setUpdateTime(capture.getUpdateTime());
        return vo;
    }

    public static CaptureSummaryVo toSummaryVo(CrmCapture capture)
    {
        if (capture == null)
        {
            return null;
        }
        CaptureSummaryVo vo = new CaptureSummaryVo();
        vo.setCaptureId(capture.getCaptureId());
        vo.setTemplateId(capture.getTemplateId());
        vo.setTitle(capture.getTitle());
        vo.setStatus(capture.getStatus());
        vo.setPublicPath(publicPath(capture.getCaptureId()));
        vo.setPublishedAt(capture.getPublishedAt());
        vo.setUpdateTime(capture.getUpdateTime());
        return vo;
    }
}
