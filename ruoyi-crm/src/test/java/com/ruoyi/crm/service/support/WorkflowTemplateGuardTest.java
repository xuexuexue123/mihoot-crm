package com.ruoyi.crm.service.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.crm.domain.WWorkflow;

class WorkflowTemplateGuardTest
{
    @Test
    void parseIsTemplateFlag_acceptsZeroOneTrueFalse()
    {
        assertEquals(Boolean.TRUE, WorkflowTemplateGuard.parseIsTemplateFlag("1"));
        assertEquals(Boolean.FALSE, WorkflowTemplateGuard.parseIsTemplateFlag("0"));
        assertEquals(Boolean.TRUE, WorkflowTemplateGuard.parseIsTemplateFlag("true"));
        assertEquals(Boolean.FALSE, WorkflowTemplateGuard.parseIsTemplateFlag("FALSE"));
        assertNull(WorkflowTemplateGuard.parseIsTemplateFlag(null));
        assertNull(WorkflowTemplateGuard.parseIsTemplateFlag(""));
    }

    @Test
    void parseIsTemplateFlag_rejectsUnknown()
    {
        assertThrows(ServiceException.class, () -> WorkflowTemplateGuard.parseIsTemplateFlag("yes"));
    }

    @Test
    void assertExecutable_rejectsTemplate()
    {
        WWorkflow template = new WWorkflow();
        template.setIsTemplate(Boolean.TRUE);
        ServiceException ex = assertThrows(ServiceException.class,
                () -> WorkflowTemplateGuard.assertExecutable(template));
        assertEquals(WorkflowTemplateGuard.TEMPLATE_NOT_EXECUTABLE, ex.getMessage());
        assertEquals(400, ex.getCode());
    }

    @Test
    void isTemplate_falseForInstance()
    {
        WWorkflow instance = new WWorkflow();
        instance.setIsTemplate(Boolean.FALSE);
        assertFalse(WorkflowTemplateGuard.isTemplate(instance));
        assertFalse(WorkflowTemplateGuard.isTemplate(null));
        WorkflowTemplateGuard.assertExecutable(instance);
        assertTrue(true);
    }
}
