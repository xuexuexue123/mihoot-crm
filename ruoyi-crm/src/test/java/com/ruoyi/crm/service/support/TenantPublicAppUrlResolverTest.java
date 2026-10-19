package com.ruoyi.crm.service.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.ruoyi.tenant.config.TenantWebsiteProperties;
import com.ruoyi.tenant.domain.MasterTenant;
import com.ruoyi.tenant.service.IMasterTenantService;

@ExtendWith(MockitoExtension.class)
class TenantPublicAppUrlResolverTest
{
    private static final String TENANT = "tenant3";

    @Mock
    private IMasterTenantService masterTenantService;

    @Mock
    private TenantWebsiteProperties tenantWebsiteProperties;

    @InjectMocks
    private TenantPublicAppUrlResolver resolver;

    @BeforeEach
    void setUp()
    {
        ReflectionTestUtils.setField(resolver, "fallbackPublicAppUrl", "https://crm.example.com/");
    }

    @Test
    void resolve_usesTenantSubdomainWhenSlugPresent()
    {
        MasterTenant tenant = new MasterTenant();
        tenant.setWebsiteSlug("tenant3");
        when(masterTenantService.selectMasterTenant(TENANT)).thenReturn(tenant);
        when(tenantWebsiteProperties.getPublicBaseProtocol()).thenReturn("https");
        when(tenantWebsiteProperties.getEffectivePublicBaseDomain()).thenReturn("mycrm.com");

        assertEquals("https://tenant3.mycrm.com", resolver.resolve(TENANT));
    }

    @Test
    void resolve_fallsBackToGlobalPublicAppUrlWhenSlugMissing()
    {
        when(masterTenantService.selectMasterTenant(TENANT)).thenReturn(null);

        assertEquals("https://crm.example.com", resolver.resolve(TENANT));
    }

    @Test
    void resolve_fallsBackWhenWebsiteSlugBlank()
    {
        MasterTenant tenant = new MasterTenant();
        tenant.setWebsiteSlug("");
        when(masterTenantService.selectMasterTenant(TENANT)).thenReturn(tenant);

        assertEquals("https://crm.example.com", resolver.resolve(TENANT));
    }
}
