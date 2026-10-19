package com.ruoyi.crm.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Date;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.crm.domain.CrmMarketingContent;
import com.ruoyi.crm.domain.vo.MarketingContentDetailVo;
import com.ruoyi.crm.mapper.CrmMarketingContentMapper;
import com.ruoyi.crm.mapper.CrmMarketingContentPublishMapper;
import com.ruoyi.crm.wechatmp.mapper.CrmWechatMpConfigMapper;
import com.ruoyi.crm.wechatmp.mapper.CrmWechatMpMaterialMapper;
import com.ruoyi.crm.wechatmp.mapper.CrmWechatMpPublishTaskMapper;
import com.ruoyi.crm.wechatmp.service.IWechatMpArticleDraftService;

@ExtendWith(MockitoExtension.class)
class CrmMarketingContentWebsiteVisibleTest
{
    private static final String CONTENT_ID = "22222222-2222-2222-2222-222222222222";

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
    private CrmMarketingContentServiceImpl adminService;

    @Test
    void publishToWebsite_setsVisibleAndTimestamp()
    {
        CrmMarketingContent existing = baseContent(false);
        when(contentMapper.selectById(CONTENT_ID)).thenReturn(existing, withVisible(existing));
        when(publishMapper.selectByContentId(CONTENT_ID)).thenReturn(Collections.emptyList());

        MarketingContentDetailVo vo = adminService.publishToWebsite(CONTENT_ID);

        ArgumentCaptor<CrmMarketingContent> captor = ArgumentCaptor.forClass(CrmMarketingContent.class);
        verify(contentMapper).update(captor.capture());
        assertTrue(Boolean.TRUE.equals(captor.getValue().getWebsiteVisible()));
        assertNotNull(captor.getValue().getWebsitePublishedAt());
        assertTrue(Boolean.TRUE.equals(vo.getWebsiteVisible()));
    }

    @Test
    void unpublishFromWebsite_clearsVisibleAndTimestamp()
    {
        CrmMarketingContent existing = baseContent(true);
        existing.setWebsitePublishedAt(new Date());
        when(contentMapper.selectById(CONTENT_ID)).thenReturn(existing, withInvisible(existing));
        when(publishMapper.selectByContentId(CONTENT_ID)).thenReturn(Collections.emptyList());

        MarketingContentDetailVo vo = adminService.unpublishFromWebsite(CONTENT_ID);

        ArgumentCaptor<CrmMarketingContent> captor = ArgumentCaptor.forClass(CrmMarketingContent.class);
        verify(contentMapper).update(captor.capture());
        assertEquals(Boolean.FALSE, captor.getValue().getWebsiteVisible());
        assertNull(captor.getValue().getWebsitePublishedAt());
        assertEquals(Boolean.FALSE, vo.getWebsiteVisible());
    }

    @Test
    void update_websiteVisibleTrue_setsPublishedAt()
    {
        CrmMarketingContent existing = baseContent(false);
        when(contentMapper.selectById(CONTENT_ID)).thenReturn(existing, withVisible(existing));
        when(publishMapper.selectByContentId(CONTENT_ID)).thenReturn(Collections.emptyList());

        CrmMarketingContent request = new CrmMarketingContent();
        request.setId(CONTENT_ID);
        request.setWebsiteVisible(true);
        adminService.update(request);

        ArgumentCaptor<CrmMarketingContent> captor = ArgumentCaptor.forClass(CrmMarketingContent.class);
        verify(contentMapper).update(captor.capture());
        assertTrue(Boolean.TRUE.equals(captor.getValue().getWebsiteVisible()));
        assertNotNull(captor.getValue().getWebsitePublishedAt());
    }

    private static CrmMarketingContent baseContent(boolean visible)
    {
        CrmMarketingContent c = new CrmMarketingContent();
        c.setId(CONTENT_ID);
        c.setTitle("Test");
        c.setContentMd("");
        c.setStatus(CrmMarketingContent.STATUS_DRAFT);
        c.setWebsiteVisible(visible);
        if (visible)
        {
            c.setWebsitePublishedAt(new Date());
        }
        return c;
    }

    private static CrmMarketingContent withVisible(CrmMarketingContent src)
    {
        CrmMarketingContent c = baseContent(true);
        c.setTitle(src.getTitle());
        return c;
    }

    private static CrmMarketingContent withInvisible(CrmMarketingContent src)
    {
        CrmMarketingContent c = baseContent(false);
        c.setTitle(src.getTitle());
        return c;
    }
}
