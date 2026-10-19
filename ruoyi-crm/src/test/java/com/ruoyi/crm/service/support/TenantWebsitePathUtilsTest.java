package com.ruoyi.crm.service.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.reflect.Field;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.tenant.config.TenantWebsiteProperties;

class TenantWebsitePathUtilsTest
{
    private TenantWebsitePathUtils pathUtils;

    @BeforeEach
    void setUp() throws Exception
    {
        pathUtils = new TenantWebsitePathUtils();
        TenantWebsiteProperties props = new TenantWebsiteProperties();
        Field field = TenantWebsitePathUtils.class.getDeclaredField("tenantWebsiteProperties");
        field.setAccessible(true);
        field.set(pathUtils, props);
    }

    @Test
    void normalizePath_defaultsToRoot()
    {
        assertEquals("/", pathUtils.normalizePath(null));
        assertEquals("/", pathUtils.normalizePath(""));
        assertEquals("/", pathUtils.normalizePath("   "));
    }

    @Test
    void normalizePath_trimsTrailingSlash()
    {
        assertEquals("/about", pathUtils.normalizePath("/about/"));
        assertEquals("/about/team", pathUtils.normalizePath("about/team"));
    }

    @Test
    void validatePathForCreate_allowsRoot()
    {
        pathUtils.validatePathForCreate("/");
    }

    @Test
    void validatePathForCreate_rejectsReserved()
    {
        ServiceException ex = assertThrows(ServiceException.class,
                () -> pathUtils.validatePathForCreate("/dashboard"));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getCode());
        assertEquals("PATH_RESERVED", ex.getMessage());
    }

    @Test
    void normalizePath_lowercasesSegments()
    {
        assertEquals("/about", pathUtils.normalizePath("/About"));
        assertEquals("/about", pathUtils.normalizePath("About"));
    }

    @Test
    void normalizePath_decodesPercentEncodedRoot()
    {
        assertEquals("/", pathUtils.normalizePath("%2F"));
        assertEquals("/", pathUtils.normalizePath("%252F"));
    }

    @Test
    void validatePathQuery_allowsDecodedRoot()
    {
        pathUtils.validatePathQuery("%252F");
        pathUtils.validatePathQuery("/About");
    }

    @Test
    void validatePathForCreate_rejectsInvalidCharacters()
    {
        assertThrows(ServiceException.class, () -> pathUtils.validatePathForCreate("/../secret"));
        assertThrows(ServiceException.class, () -> pathUtils.validatePathForCreate("/foo//bar"));
    }
}
