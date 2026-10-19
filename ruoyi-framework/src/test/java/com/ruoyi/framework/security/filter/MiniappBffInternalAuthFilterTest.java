package com.ruoyi.framework.security.filter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import com.ruoyi.common.core.domain.model.LoginUser;

class MiniappBffInternalAuthFilterTest
{
    private static final String SECRET = "test-internal-secret";

    private MiniappBffInternalAuthFilter filter;
    private FilterChain chain;

    @BeforeEach
    void setUp()
    {
        filter = new MiniappBffInternalAuthFilter();
        ReflectionTestUtils.setField(filter, "internalSecret", SECRET);
        chain = mock(FilterChain.class);
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown()
    {
        SecurityContextHolder.clearContext();
    }

    @Test
    void allowlistedPaths()
    {
        assertTrue(MiniappBffInternalAuthFilter.isAllowlisted("GET", "/crm/market-event/list"));
        assertTrue(MiniappBffInternalAuthFilter.isAllowlisted("GET", "/crm/wechat_miniapp/config/runtime"));
        assertTrue(MiniappBffInternalAuthFilter.isAllowlisted("GET", "/crm/lead/by-mobile"));
        assertTrue(MiniappBffInternalAuthFilter.isAllowlisted("POST", "/crm/lead/miniapp/upsert"));
        assertTrue(MiniappBffInternalAuthFilter.isAllowlisted("POST", "/crm/lead/miniapp/market-event-register"));
        assertTrue(MiniappBffInternalAuthFilter.isAllowlisted("POST", "/crm/knowledge/retrieve"));
        assertTrue(MiniappBffInternalAuthFilter.isAllowlisted("GET", "/crm/contact/list"));
        assertTrue(MiniappBffInternalAuthFilter.isAllowlisted("GET", "/crm/contact/c-123"));
        assertFalse(MiniappBffInternalAuthFilter.isAllowlisted("GET", "/crm/wechat_miniapp/config"));
        assertFalse(MiniappBffInternalAuthFilter.isAllowlisted("POST", "/crm/wechat_miniapp/config"));
        assertFalse(MiniappBffInternalAuthFilter.isAllowlisted("GET", "/crm/contact/c-123/clv"));
        assertFalse(MiniappBffInternalAuthFilter.isAllowlisted("DELETE", "/crm/lead/1"));
        assertFalse(MiniappBffInternalAuthFilter.isAllowlisted("GET", "/crm/lead/miniapp/market-event-register"));
    }

    @Test
    void servicePermissionsExcludeAdvisorConfigAndRemove()
    {
        assertTrue(MiniappBffInternalAuthFilter.servicePermissions().contains("crm:wechat_miniapp:runtime"));
        assertFalse(MiniappBffInternalAuthFilter.servicePermissions().contains("crm:wechat_miniapp:config"));
        assertFalse(MiniappBffInternalAuthFilter.servicePermissions().contains("crm:marketevent:remove"));
    }

    @Test
    void validSecretInjectsLoginUser() throws ServletException, IOException
    {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/crm/market-event/list");
        request.addHeader(MiniappBffInternalAuthFilter.HEADER_INTERNAL_SECRET, SECRET);
        request.addHeader("tenant", "tenant3");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, chain);

        assertEquals(200, response.getStatus());
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(auth);
        assertTrue(auth.getPrincipal() instanceof LoginUser);
        LoginUser user = (LoginUser) auth.getPrincipal();
        assertEquals(MiniappBffInternalAuthFilter.SERVICE_USER_NAME, user.getUsername());
        assertEquals("tenant3", user.getTenant());
        assertTrue(user.getPermissions().contains("crm:marketevent:list"));
        assertTrue(user.getPermissions().contains("crm:wechat_miniapp:runtime"));
        verify(chain).doFilter(request, response);
    }

    @Test
    void missingSecretReturns401() throws ServletException, IOException
    {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/crm/market-event/list");
        request.addHeader("tenant", "tenant3");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, chain);

        assertEquals(401, response.getStatus());
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void wrongSecretReturns401() throws ServletException, IOException
    {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/crm/wechat_miniapp/config/runtime");
        request.addHeader(MiniappBffInternalAuthFilter.HEADER_INTERNAL_SECRET, "wrong");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, chain);

        assertEquals(401, response.getStatus());
        assertTrue(response.getContentAsString().contains("401"));
    }

    @Test
    void bearerPresentSkipsInjection() throws ServletException, IOException
    {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/crm/market-event/list");
        request.addHeader("Authorization", "Bearer some-jwt");
        request.addHeader(MiniappBffInternalAuthFilter.HEADER_INTERNAL_SECRET, SECRET);
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, chain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(chain).doFilter(request, response);
    }

    @Test
    void existingAuthenticationNotOverwritten() throws ServletException, IOException
    {
        LoginUser existing = new LoginUser();
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(existing, null, existing.getAuthorities()));

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/crm/market-event/list");
        request.addHeader(MiniappBffInternalAuthFilter.HEADER_INTERNAL_SECRET, SECRET);
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, chain);

        assertSamePrincipal(existing);
        verify(chain).doFilter(request, response);
    }

    @Test
    void nonAllowlistedIgnoresSecret() throws ServletException, IOException
    {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/crm/wechat_miniapp/config");
        request.addHeader(MiniappBffInternalAuthFilter.HEADER_INTERNAL_SECRET, SECRET);
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, chain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(chain).doFilter(request, response);
    }

    private void assertSamePrincipal(LoginUser expected)
    {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(auth);
        assertEquals(expected, auth.getPrincipal());
    }
}
