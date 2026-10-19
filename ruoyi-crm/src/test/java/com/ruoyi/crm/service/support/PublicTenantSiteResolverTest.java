package com.ruoyi.crm.service.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.tenant.domain.vo.PublicTenantSiteVo;
import com.ruoyi.tenant.service.IMasterTenantService;
import com.ruoyi.tenant.util.TenantWebsiteSlugUtils;

@ExtendWith(MockitoExtension.class)
class PublicTenantSiteResolverTest
{
    @Mock
    private IMasterTenantService masterTenantService;
    @Mock
    private TenantWebsiteSlugUtils tenantWebsiteSlugUtils;

    @InjectMocks
    private PublicTenantSiteResolver resolver;

    @Test
    void resolveActiveSite_returnsActiveSite()
    {
        when(tenantWebsiteSlugUtils.isReserved("demo")).thenReturn(false);
        PublicTenantSiteVo site = new PublicTenantSiteVo();
        site.setTenantName("tenant_demo");
        site.setWebsiteStatus("active");
        when(masterTenantService.selectPublicByWebsiteSlug("demo")).thenReturn(site);

        PublicTenantSiteVo result = resolver.resolveActiveSite("demo");

        assertEquals("tenant_demo", result.getTenantName());
    }

    @Test
    void resolveActiveSite_notFound()
    {
        when(tenantWebsiteSlugUtils.isReserved("missing")).thenReturn(false);
        when(masterTenantService.selectPublicByWebsiteSlug("missing")).thenReturn(null);

        ServiceException ex = assertThrows(ServiceException.class,
                () -> resolver.resolveActiveSite("missing"));
        assertEquals(HttpStatus.NOT_FOUND, ex.getCode());
    }

    @Test
    void resolveActiveSite_suspended()
    {
        when(tenantWebsiteSlugUtils.isReserved("paused")).thenReturn(false);
        PublicTenantSiteVo site = new PublicTenantSiteVo();
        site.setWebsiteStatus("suspended");
        when(masterTenantService.selectPublicByWebsiteSlug("paused")).thenReturn(site);

        ServiceException ex = assertThrows(ServiceException.class,
                () -> resolver.resolveActiveSite("paused"));
        assertEquals(HttpStatus.FORBIDDEN, ex.getCode());
    }
}
