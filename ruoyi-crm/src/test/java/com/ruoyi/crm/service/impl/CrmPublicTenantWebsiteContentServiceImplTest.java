package com.ruoyi.crm.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Date;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.crm.domain.CrmMarketingContent;
import com.ruoyi.crm.domain.vo.PublicWebsiteContentCardVo;
import com.ruoyi.crm.domain.vo.PublicWebsiteContentDetailVo;
import com.ruoyi.crm.domain.vo.PublicWebsiteContentListVo;
import com.ruoyi.crm.mapper.CrmMarketingContentMapper;
import com.ruoyi.crm.service.support.PublicTenantSiteResolver;
import com.ruoyi.crm.service.support.WaitResumeTenantBootstrap;
import com.ruoyi.tenant.domain.vo.PublicTenantSiteVo;

@ExtendWith(MockitoExtension.class)
class CrmPublicTenantWebsiteContentServiceImplTest
{
    private static final String CONTENT_ID = "22222222-2222-2222-2222-222222222222";
    private static final String SLUG = "demo";

    @Mock
    private PublicTenantSiteResolver publicTenantSiteResolver;
    @Mock
    private WaitResumeTenantBootstrap waitResumeTenantBootstrap;
    @Mock
    private CrmMarketingContentMapper contentMapper;

    @InjectMocks
    private CrmPublicTenantWebsiteContentServiceImpl publicService;

    @Test
    void listContents_returnsVisibleCards()
    {
        PublicTenantSiteVo site = new PublicTenantSiteVo();
        site.setTenantName("tenant_demo");
        when(publicTenantSiteResolver.resolveActiveSite(SLUG)).thenReturn(site);
        PublicWebsiteContentCardVo card = new PublicWebsiteContentCardVo();
        card.setId(CONTENT_ID);
        card.setTitle("可见文章");
        when(contentMapper.selectPublicWebsiteVisible(20)).thenReturn(Collections.singletonList(card));

        PublicWebsiteContentListVo list = publicService.listContents(SLUG, null);

        assertEquals(1, list.getItems().size());
        assertEquals(CONTENT_ID, list.getItems().get(0).getId());
        verify(waitResumeTenantBootstrap).bindTenantDataSource("tenant_demo");
        verify(contentMapper).selectPublicWebsiteVisible(20);
    }

    @Test
    void listContents_capsLimitAt50()
    {
        PublicTenantSiteVo site = new PublicTenantSiteVo();
        site.setTenantName("tenant_demo");
        when(publicTenantSiteResolver.resolveActiveSite(SLUG)).thenReturn(site);
        when(contentMapper.selectPublicWebsiteVisible(50)).thenReturn(Collections.emptyList());

        publicService.listContents(SLUG, 999);

        verify(contentMapper).selectPublicWebsiteVisible(50);
    }

    @Test
    void getContent_notFoundWhenInvisible()
    {
        PublicTenantSiteVo site = new PublicTenantSiteVo();
        site.setTenantName("tenant_demo");
        when(publicTenantSiteResolver.resolveActiveSite(SLUG)).thenReturn(site);
        when(contentMapper.selectPublicById(CONTENT_ID)).thenReturn(null);

        ServiceException ex = assertThrows(ServiceException.class,
                () -> publicService.getContent(SLUG, CONTENT_ID));
        assertEquals("content_not_found", ex.getMessage());
        assertEquals(Integer.valueOf(HttpStatus.NOT_FOUND), ex.getCode());
    }

    @Test
    void getContent_returnsBodyWhenVisible()
    {
        PublicTenantSiteVo site = new PublicTenantSiteVo();
        site.setTenantName("tenant_demo");
        when(publicTenantSiteResolver.resolveActiveSite(SLUG)).thenReturn(site);
        CrmMarketingContent row = new CrmMarketingContent();
        row.setId(CONTENT_ID);
        row.setTitle("T");
        row.setContentMd("# hi");
        row.setContentHtml("<p>hi</p>");
        row.setWebsiteVisible(true);
        row.setWebsitePublishedAt(new Date());
        when(contentMapper.selectPublicById(CONTENT_ID)).thenReturn(row);

        PublicWebsiteContentDetailVo detail = publicService.getContent(SLUG, CONTENT_ID);

        assertEquals("# hi", detail.getContentMd());
        assertEquals("<p>hi</p>", detail.getContentHtml());
    }
}
