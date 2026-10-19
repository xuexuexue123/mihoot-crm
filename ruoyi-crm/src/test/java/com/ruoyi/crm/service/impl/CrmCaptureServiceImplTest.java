package com.ruoyi.crm.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.crm.domain.CrmCapture;
import com.ruoyi.crm.domain.dto.CapturePublishRequest;
import com.ruoyi.crm.domain.vo.CaptureDetailVo;
import com.ruoyi.crm.mapper.CrmCaptureMapper;
import com.ruoyi.ems.domain.SysReTemplate;
import com.ruoyi.ems.service.ISysReTemplateService;

@ExtendWith(MockitoExtension.class)
class CrmCaptureServiceImplTest
{
    @Mock
    private CrmCaptureMapper captureMapper;
    @Mock
    private ISysReTemplateService sysReTemplateService;

    @InjectMocks
    private CrmCaptureServiceImpl service;

    @Test
    void publish_insertsNewCaptureWithSnapshot()
    {
        CapturePublishRequest request = new CapturePublishRequest();
        request.setTemplateId("tpl-1");
        request.setSuccessMessage("thanks");

        SysReTemplate template = surveyTemplate("tpl-1", "预约咨询", "{\"pages\":[]}");
        when(sysReTemplateService.selectSysReTemplateByTemplateId("tpl-1")).thenReturn(template);
        when(captureMapper.selectByTemplateId("tpl-1")).thenReturn(null);
        when(captureMapper.insert(any(CrmCapture.class))).thenReturn(1);

        CaptureDetailVo vo = service.publish(request);

        ArgumentCaptor<CrmCapture> captor = ArgumentCaptor.forClass(CrmCapture.class);
        verify(captureMapper).insert(captor.capture());
        CrmCapture inserted = captor.getValue();
        assertEquals("tpl-1", inserted.getTemplateId());
        assertEquals(CrmCapture.STATUS_PUBLISHED, inserted.getStatus());
        assertEquals("{\"pages\":[]}", inserted.getContentSnapshot());
        assertEquals("预约咨询", inserted.getTitle());
        assertEquals(inserted.getCaptureId(), vo.getCaptureId());
        assertEquals("/captures/" + inserted.getCaptureId(), vo.getPublicPath());
    }

    @Test
    void publish_republish_updatesSameCaptureId()
    {
        CapturePublishRequest request = new CapturePublishRequest();
        request.setTemplateId("tpl-1");

        SysReTemplate template = surveyTemplate("tpl-1", "新标题", "{\"v\":2}");
        when(sysReTemplateService.selectSysReTemplateByTemplateId("tpl-1")).thenReturn(template);

        CrmCapture existing = new CrmCapture();
        existing.setCaptureId("cap-stable");
        existing.setTemplateId("tpl-1");
        existing.setContentSnapshot("{\"v\":1}");
        existing.setStatus(CrmCapture.STATUS_PUBLISHED);
        existing.setPublishedAt(new Date(1L));
        when(captureMapper.selectByTemplateId("tpl-1")).thenReturn(existing);
        when(captureMapper.update(any(CrmCapture.class))).thenReturn(1);

        CrmCapture reloaded = new CrmCapture();
        reloaded.setCaptureId("cap-stable");
        reloaded.setTemplateId("tpl-1");
        reloaded.setTitle("新标题");
        reloaded.setStatus(CrmCapture.STATUS_PUBLISHED);
        reloaded.setContentSnapshot("{\"v\":2}");
        when(captureMapper.selectByCaptureId("cap-stable")).thenReturn(reloaded);

        CaptureDetailVo vo = service.publish(request);

        assertEquals("cap-stable", vo.getCaptureId());
        assertEquals("{\"v\":2}", vo.getContentSnapshot());
        verify(captureMapper, never()).insert(any());
        ArgumentCaptor<CrmCapture> captor = ArgumentCaptor.forClass(CrmCapture.class);
        verify(captureMapper).update(captor.capture());
        assertEquals("cap-stable", captor.getValue().getCaptureId());
        assertEquals("{\"v\":2}", captor.getValue().getContentSnapshot());
    }

    @Test
    void publish_rejectsNonSurveyCategory()
    {
        CapturePublishRequest request = new CapturePublishRequest();
        request.setTemplateId("tpl-1");
        SysReTemplate template = surveyTemplate("tpl-1", "x", "{}");
        template.setCategory("Email");
        when(sysReTemplateService.selectSysReTemplateByTemplateId("tpl-1")).thenReturn(template);

        assertThrows(ServiceException.class, () -> service.publish(request));
        verify(captureMapper, never()).insert(any());
    }

    @Test
    void unpublish_keepsSnapshot()
    {
        CrmCapture existing = new CrmCapture();
        existing.setCaptureId("cap-1");
        existing.setTemplateId("tpl-1");
        existing.setStatus(CrmCapture.STATUS_PUBLISHED);
        existing.setContentSnapshot("{\"keep\":true}");
        when(captureMapper.selectByCaptureId("cap-1")).thenReturn(existing);
        when(captureMapper.update(any(CrmCapture.class))).thenReturn(1);

        CrmCapture after = new CrmCapture();
        after.setCaptureId("cap-1");
        after.setStatus(CrmCapture.STATUS_UNPUBLISHED);
        after.setContentSnapshot("{\"keep\":true}");
        when(captureMapper.selectByCaptureId("cap-1")).thenReturn(existing, after);

        CaptureDetailVo vo = service.unpublish("cap-1");

        assertEquals(CrmCapture.STATUS_UNPUBLISHED, vo.getStatus());
        assertEquals("{\"keep\":true}", vo.getContentSnapshot());
    }

    private static SysReTemplate surveyTemplate(String id, String name, String content)
    {
        SysReTemplate template = new SysReTemplate();
        template.setTemplateId(id);
        template.setTemplateName(name);
        template.setCategory("Survey");
        template.setContent(content);
        return template;
    }
}
