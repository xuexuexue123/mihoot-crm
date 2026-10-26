package com.ruoyi.crm.service.support;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.framework.security.filter.WorkflowIntranetAuthenticationFilter;
import com.ruoyi.framework.security.intranet.IntranetRequestAccess;

public class IntranetRequestAccessTest
{
    private final IntranetRequestAccess access = new IntranetRequestAccess(true,
            "127.0.0.1/32,::1/128,10.0.0.0/8,172.16.0.0/12,192.168.0.0/16");

    @AfterEach
    public void clearSecurityContext()
    {
        SecurityContextHolder.clearContext();
    }

    @Test
    public void acceptsPrivateAndLoopbackAddresses()
    {
        assertTrue(access.isTrusted(request("127.0.0.1", "/crm/wscheduledtask/claim-due")));
        assertTrue(access.isTrusted(request("10.20.30.40", "/crm/wworkflow/wf-1")));
        assertTrue(access.isTrusted(request("172.31.2.3", "/crm/wexecution")));
        assertTrue(access.isTrusted(request("192.168.8.9", "/crm/wnode")));
    }

    @Test
    public void rejectsPublicAddressAndIgnoresForwardedHeader()
    {
        MockHttpServletRequest request = request("8.8.8.8", "/crm/wscheduledtask/claim-due");
        request.addHeader("X-Forwarded-For", "10.0.0.1");
        assertFalse(access.isTrusted(request));
        assertThrows(ServiceException.class, () -> access.requireTrusted(request));
    }

    @Test
    public void injectsIdentityForIntranetCrmCallWithoutBearer() throws Exception
    {
        WorkflowIntranetAuthenticationFilter filter = filter();
        MockHttpServletRequest request = request("10.0.0.2", "/crm/wworkflow/wf-1");
        request.addHeader("tenant", "clinic_a");

        filter.doFilter(request, new MockHttpServletResponse(), new MockFilterChain());

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertTrue(SecurityContextHolder.getContext().getAuthentication().isAuthenticated());
    }

    @Test
    public void scheduledTaskCrudStillRequiresLogin() throws Exception
    {
        WorkflowIntranetAuthenticationFilter filter = filter();
        MockHttpServletRequest request = request("10.0.0.2", "/crm/wscheduledtask/list");

        filter.doFilter(request, new MockHttpServletResponse(), new MockFilterChain());

        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    public void bearerRequestIsNeverReplaced() throws Exception
    {
        WorkflowIntranetAuthenticationFilter filter = filter();
        MockHttpServletRequest request = request("10.0.0.2", "/crm/wworkflow/wf-1");
        request.addHeader("Authorization", "Bearer user-token");

        filter.doFilter(request, new MockHttpServletResponse(), new MockFilterChain());

        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    private WorkflowIntranetAuthenticationFilter filter()
    {
        WorkflowIntranetAuthenticationFilter filter = new WorkflowIntranetAuthenticationFilter();
        ReflectionTestUtils.setField(filter, "intranetRequestAccess", access);
        return filter;
    }

    private MockHttpServletRequest request(String remoteAddress, String path)
    {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", path);
        request.setRemoteAddr(remoteAddress);
        request.setRequestURI(path);
        return request;
    }
}
