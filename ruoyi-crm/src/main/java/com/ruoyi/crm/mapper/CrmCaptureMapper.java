package com.ruoyi.crm.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ruoyi.crm.domain.CrmCapture;

/**
 * Captures Mapper
 */
public interface CrmCaptureMapper
{
    CrmCapture selectByCaptureId(@Param("captureId") String captureId);

    CrmCapture selectByTemplateId(@Param("templateId") String templateId);

    List<CrmCapture> selectList(@Param("templateId") String templateId, @Param("status") String status);

    int insert(CrmCapture capture);

    int update(CrmCapture capture);
}
