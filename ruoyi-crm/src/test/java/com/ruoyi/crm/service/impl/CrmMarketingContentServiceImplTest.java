package com.ruoyi.crm.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.crm.domain.CrmMarketingContent;
import com.ruoyi.crm.domain.CrmMarketingContentPublish;
import com.ruoyi.crm.domain.dto.MarketingContentPublishRequest;
import com.ruoyi.crm.domain.vo.MarketingContentPublishResultVo;
import com.ruoyi.crm.mapper.CrmMarketingContentMapper;
import com.ruoyi.crm.mapper.CrmMarketingContentPublishMapper;
import com.ruoyi.crm.wechatmp.domain.CrmWechatMpConfig;
import com.ruoyi.crm.wechatmp.domain.CrmWechatMpMaterial;
import com.ruoyi.crm.wechatmp.domain.CrmWechatMpPublishTask;
import com.ruoyi.crm.wechatmp.dto.WechatMpDraftAddRequest;
import com.ruoyi.crm.wechatmp.dto.WechatMpDraftSyncRequest;
import com.ruoyi.crm.wechatmp.mapper.CrmWechatMpConfigMapper;
import com.ruoyi.crm.wechatmp.mapper.CrmWechatMpMaterialMapper;
import com.ruoyi.crm.wechatmp.mapper.CrmWechatMpPublishTaskMapper;
import com.ruoyi.crm.wechatmp.service.IWechatMpArticleDraftService;

@ExtendWith(MockitoExtension.class)
class CrmMarketingContentServiceImplTest
{
    private static final String CONTENT_ID = "11111111-1111-1111-1111-111111111111";
    private static final String APP_ID = "wx_test_app";
    private static final String MEDIA_ID = "media-local-1";
    private static final String MEDIA_ID_WX = "media-wx-1";
    private static final String PAPER_HTML = "<section>paper-html</section>";

    @Mock
    private CrmMarketingContentMapper contentMapper;

    @Mock
    private CrmMarketingContentPublishMapper publishMapper;

    @Mock
    private IWechatMpArticleDraftService draftService;

    @Mock
    private CrmWechatMpConfigMapper wechatMpConfigMapper;

    @Mock
    private CrmWechatMpMaterialMapper materialMapper;

    @Mock
    private CrmWechatMpPublishTaskMapper publishTaskMapper;

    @InjectMocks
    private CrmMarketingContentServiceImpl service;

    @Test
    void create_rejectsBlankTitle()
    {
        CrmMarketingContent request = new CrmMarketingContent();
        request.setTitle("  ");

        ServiceException ex = assertThrows(ServiceException.class, () -> service.create(request));
        assertEquals(Integer.valueOf(HttpStatus.BAD_REQUEST), ex.getCode());
    }

    @Test
    void delete_removesRowWithoutCallingWechat()
    {
        when(contentMapper.selectById(CONTENT_ID)).thenReturn(baseContent());
        when(contentMapper.deleteById(CONTENT_ID)).thenReturn(1);

        service.delete(CONTENT_ID);

        verify(contentMapper).deleteById(CONTENT_ID);
        verify(draftService, never()).deleteDraft(any(), any(), anyBoolean());
    }

    @Test
    void publish_requiresAppId()
    {
        MarketingContentPublishRequest request = new MarketingContentPublishRequest();
        request.setContent(PAPER_HTML);

        ServiceException ex = assertThrows(ServiceException.class, () -> service.publish(CONTENT_ID, request));
        assertEquals("no_app_id", ex.getMessage());
    }

    @Test
    void publish_requiresContent()
    {
        when(wechatMpConfigMapper.selectByAppId(APP_ID)).thenReturn(new CrmWechatMpConfig());
        when(contentMapper.selectById(CONTENT_ID)).thenReturn(baseContent());

        MarketingContentPublishRequest request = new MarketingContentPublishRequest();
        request.setAppId(APP_ID);

        ServiceException ex = assertThrows(ServiceException.class, () -> service.publish(CONTENT_ID, request));
        assertEquals("no_content", ex.getMessage());
    }

    @Test
    void publish_passesPaperHtmlToSync_withoutFlexmarkOverride()
    {
        stubHappyPathFirstPublish();

        MarketingContentPublishRequest request = new MarketingContentPublishRequest();
        request.setAppId(APP_ID);
        request.setContent(PAPER_HTML);
        request.setContentMd("# md should not replace html");
        request.setThumbMediaId("thumb-1");

        MarketingContentPublishResultVo result = service.publish(CONTENT_ID, request);

        assertNotNull(result);
        assertEquals(CONTENT_ID, result.getContentId());
        assertEquals(CrmMarketingContentPublish.STATUS_PUBLISHING, result.getPublish().getStatus());

        ArgumentCaptor<WechatMpDraftSyncRequest> syncCaptor = ArgumentCaptor.forClass(WechatMpDraftSyncRequest.class);
        verify(draftService).syncDraft(eq(APP_ID), eq(MEDIA_ID), syncCaptor.capture());
        assertEquals(PAPER_HTML, syncCaptor.getValue().getContent());

        ArgumentCaptor<CrmMarketingContent> contentPatch = ArgumentCaptor.forClass(CrmMarketingContent.class);
        verify(contentMapper).update(contentPatch.capture());
        assertEquals(PAPER_HTML, contentPatch.getValue().getContentHtml());
        assertEquals("# md should not replace html", contentPatch.getValue().getContentMd());

        ArgumentCaptor<WechatMpDraftAddRequest> addCaptor = ArgumentCaptor.forClass(WechatMpDraftAddRequest.class);
        verify(draftService).createDraft(eq(APP_ID), addCaptor.capture(), eq(true));
        assertEquals(PAPER_HTML, addCaptor.getValue().getArticles().get(0).getContent());
    }

    @Test
    void publish_secondTime_updatesSamePublishRow()
    {
        CrmMarketingContentPublish existing = new CrmMarketingContentPublish();
        existing.setId("pub-1");
        existing.setContentId(CONTENT_ID);
        existing.setChannel(CrmMarketingContentPublish.CHANNEL_WECHAT_MP);
        existing.setAppId(APP_ID);
        existing.setMediaId(MEDIA_ID_WX);
        existing.setStatus(CrmMarketingContentPublish.STATUS_PUBLISHED);

        when(wechatMpConfigMapper.selectByAppId(APP_ID)).thenReturn(new CrmWechatMpConfig());
        when(contentMapper.selectById(CONTENT_ID)).thenReturn(baseContent());
        when(publishMapper.selectByContentChannelApp(CONTENT_ID, CrmMarketingContentPublish.CHANNEL_WECHAT_MP, APP_ID))
                .thenReturn(existing);
        when(draftService.syncDraft(eq(APP_ID), eq(MEDIA_ID_WX), any()))
                .thenReturn(AjaxResult.success("ok", MEDIA_ID_WX));
        when(draftService.publishDraft(APP_ID, MEDIA_ID_WX)).thenReturn(AjaxResult.success("ok"));
        when(publishTaskMapper.selectByMediaId(APP_ID, MEDIA_ID_WX)).thenReturn(Collections.emptyList());

        MarketingContentPublishRequest request = new MarketingContentPublishRequest();
        request.setAppId(APP_ID);
        request.setContent(PAPER_HTML);
        request.setThumbMediaId("thumb-1");

        service.publish(CONTENT_ID, request);

        verify(draftService, never()).createDraft(any(), any(), anyBoolean());
        ArgumentCaptor<CrmMarketingContentPublish> updateCaptor =
                ArgumentCaptor.forClass(CrmMarketingContentPublish.class);
        verify(publishMapper).update(updateCaptor.capture());
        assertEquals("pub-1", updateCaptor.getValue().getId());
        assertEquals(MEDIA_ID_WX, updateCaptor.getValue().getMediaId());
        assertEquals(CrmMarketingContentPublish.STATUS_PUBLISHING, updateCaptor.getValue().getStatus());
        verify(publishMapper, never()).insert(any());
    }

    @Test
    void publish_usesDefaultThumbWhenMissing()
    {
        when(wechatMpConfigMapper.selectByAppId(APP_ID)).thenReturn(new CrmWechatMpConfig());
        when(contentMapper.selectById(CONTENT_ID)).thenReturn(baseContent());
        when(publishMapper.selectByContentChannelApp(CONTENT_ID, CrmMarketingContentPublish.CHANNEL_WECHAT_MP, APP_ID))
                .thenReturn(null);
        CrmWechatMpMaterial def = new CrmWechatMpMaterial();
        def.setMediaId("default-thumb");
        when(materialMapper.selectDefaultByAppId(APP_ID)).thenReturn(def);
        when(draftService.createDraft(eq(APP_ID), any(), eq(true)))
                .thenReturn(AjaxResult.success("ok", MEDIA_ID));
        when(draftService.syncDraft(eq(APP_ID), eq(MEDIA_ID), any()))
                .thenReturn(AjaxResult.success("ok", MEDIA_ID_WX));
        when(draftService.publishDraft(APP_ID, MEDIA_ID_WX)).thenReturn(AjaxResult.success("ok"));
        when(publishTaskMapper.selectByMediaId(APP_ID, MEDIA_ID_WX)).thenReturn(Collections.emptyList());

        MarketingContentPublishRequest request = new MarketingContentPublishRequest();
        request.setAppId(APP_ID);
        request.setContent(PAPER_HTML);

        service.publish(CONTENT_ID, request);

        ArgumentCaptor<WechatMpDraftSyncRequest> syncCaptor = ArgumentCaptor.forClass(WechatMpDraftSyncRequest.class);
        verify(draftService).syncDraft(eq(APP_ID), eq(MEDIA_ID), syncCaptor.capture());
        assertEquals("default-thumb", syncCaptor.getValue().getThumbMediaId());

        ArgumentCaptor<CrmMarketingContentPublish> insertCaptor =
                ArgumentCaptor.forClass(CrmMarketingContentPublish.class);
        verify(publishMapper).insert(insertCaptor.capture());
        assertEquals("default-thumb", insertCaptor.getValue().getThumbMediaId());
    }

    @Test
    void refreshPublish_notFound()
    {
        when(contentMapper.selectById(CONTENT_ID)).thenReturn(baseContent());
        when(publishMapper.selectByContentChannelApp(CONTENT_ID, CrmMarketingContentPublish.CHANNEL_WECHAT_MP, APP_ID))
                .thenReturn(null);

        com.ruoyi.crm.domain.dto.MarketingContentPublishRefreshRequest req =
                new com.ruoyi.crm.domain.dto.MarketingContentPublishRefreshRequest();
        req.setAppId(APP_ID);

        ServiceException ex = assertThrows(ServiceException.class, () -> service.refreshPublish(CONTENT_ID, req));
        assertEquals("publish_not_found", ex.getMessage());
        assertEquals(Integer.valueOf(HttpStatus.NOT_FOUND), ex.getCode());
    }

    @Test
    void publish_marksFailedAndKeepsMediaIdWhenSyncFailsAfterLocalCreate()
    {
        when(wechatMpConfigMapper.selectByAppId(APP_ID)).thenReturn(new CrmWechatMpConfig());
        when(contentMapper.selectById(CONTENT_ID)).thenReturn(baseContent());
        when(publishMapper.selectByContentChannelApp(CONTENT_ID, CrmMarketingContentPublish.CHANNEL_WECHAT_MP, APP_ID))
                .thenReturn(null);
        when(draftService.createDraft(eq(APP_ID), any(), eq(true)))
                .thenReturn(AjaxResult.success("ok", MEDIA_ID));
        when(draftService.syncDraft(eq(APP_ID), eq(MEDIA_ID), any()))
                .thenReturn(AjaxResult.error("同步草稿失败: invalid"));

        MarketingContentPublishRequest request = new MarketingContentPublishRequest();
        request.setAppId(APP_ID);
        request.setContent(PAPER_HTML);
        request.setThumbMediaId("thumb-1");

        ServiceException ex = assertThrows(ServiceException.class, () -> service.publish(CONTENT_ID, request));
        assertTrue(ex.getMessage().contains("同步") || ex.getMessage().contains("sync")
                || ex.getMessage().contains("失败"));

        ArgumentCaptor<CrmMarketingContentPublish> failed =
                ArgumentCaptor.forClass(CrmMarketingContentPublish.class);
        verify(publishMapper).insert(failed.capture());
        assertEquals(CrmMarketingContentPublish.STATUS_FAILED, failed.getValue().getStatus());
        assertEquals(MEDIA_ID, failed.getValue().getMediaId());
    }

    private void stubHappyPathFirstPublish()
    {
        when(wechatMpConfigMapper.selectByAppId(APP_ID)).thenReturn(new CrmWechatMpConfig());
        when(contentMapper.selectById(CONTENT_ID)).thenReturn(baseContent());
        when(publishMapper.selectByContentChannelApp(CONTENT_ID, CrmMarketingContentPublish.CHANNEL_WECHAT_MP, APP_ID))
                .thenReturn(null);
        when(draftService.createDraft(eq(APP_ID), any(), eq(true)))
                .thenReturn(AjaxResult.success("ok", MEDIA_ID));
        when(draftService.syncDraft(eq(APP_ID), eq(MEDIA_ID), any()))
                .thenReturn(AjaxResult.success("ok", MEDIA_ID_WX));
        when(draftService.publishDraft(APP_ID, MEDIA_ID_WX)).thenReturn(AjaxResult.success("ok"));

        CrmWechatMpPublishTask task = new CrmWechatMpPublishTask();
        task.setPublishId("publish-1");
        task.setMediaId(MEDIA_ID_WX);
        task.setPublishStatus(1);
        when(publishTaskMapper.selectByMediaId(APP_ID, MEDIA_ID_WX))
                .thenReturn(Collections.singletonList(task));
        when(draftService.queryPublishStatus(APP_ID, "publish-1")).thenReturn(AjaxResult.success("ok"));
        when(publishTaskMapper.selectByPublishId("publish-1")).thenReturn(task);
    }

    private static CrmMarketingContent baseContent()
    {
        CrmMarketingContent c = new CrmMarketingContent();
        c.setId(CONTENT_ID);
        c.setTitle("Test Title");
        c.setContentMd("# hello");
        c.setStatus(CrmMarketingContent.STATUS_DRAFT);
        return c;
    }
}
