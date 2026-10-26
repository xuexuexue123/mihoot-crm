package com.ruoyi.crm.service.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.lang.reflect.Field;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.ruoyi.tenant.config.TenantWebsiteProperties;

class TenantWebsitePublicUrlBuilderTest
{
    private TenantWebsitePublicUrlBuilder builder;

    @BeforeEach
    void setUp() throws Exception
    {
        builder = new TenantWebsitePublicUrlBuilder();
        TenantWebsiteProperties props = new TenantWebsiteProperties();
        props.setPublicBaseDomain("mycrm.com");
        props.setPublicBaseProtocol("https");
        Field field = TenantWebsitePublicUrlBuilder.class.getDeclaredField("tenantWebsiteProperties");
        field.setAccessible(true);
        field.set(builder, props);
    }

    @Test
    void build_composesPublicUrl()
    {
        assertEquals(
                "https://my-clinic.mycrm.com/_assets/550e8400-e29b-41d4-a716-446655440000",
                builder.build("my-clinic", "550e8400-e29b-41d4-a716-446655440000"));
    }

    @Test
    void build_returnsNullWhenSlugMissing()
    {
        assertNull(builder.build("", "550e8400-e29b-41d4-a716-446655440000"));
    }

    @Test
    void buildPublicPath_usesAssetPrefix()
    {
        assertEquals("/_assets/abc", builder.buildPublicPath("abc"));
    }
}
