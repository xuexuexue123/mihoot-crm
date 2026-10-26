package com.ruoyi.crm.service.support;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.reflect.Field;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.tenant.config.TenantWebsiteProperties;

class TenantWebsiteSchemaValidatorTest
{
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private TenantWebsiteSchemaValidator validator;

    @BeforeEach
    void setUp() throws Exception
    {
        validator = new TenantWebsiteSchemaValidator();
        TenantWebsiteProperties props = new TenantWebsiteProperties();
        props.setMaxSchemaBytes(1024);
        Field field = TenantWebsiteSchemaValidator.class.getDeclaredField("tenantWebsiteProperties");
        field.setAccessible(true);
        field.set(validator, props);
    }

    @Test
    void validateSchemaLight_acceptsMinimalSchema()
    {
        ObjectNode schema = MAPPER.createObjectNode();
        schema.put("version", 1);
        schema.set("meta", MAPPER.createObjectNode().put("path", "/"));
        schema.set("blocks", MAPPER.createArrayNode());
        assertDoesNotThrow(() -> validator.validateSchemaLight(schema, "/"));
    }

    @Test
    void validateSchemaLight_rejectsHtmlKeys()
    {
        ObjectNode schema = MAPPER.createObjectNode();
        schema.put("version", 1);
        schema.set("meta", MAPPER.createObjectNode().put("path", "/"));
        schema.set("blocks", MAPPER.createArrayNode());
        schema.put("htmlContent", "<p>x</p>");

        ServiceException ex = assertThrows(ServiceException.class,
                () -> validator.validateSchemaLight(schema, "/"));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getCode());
        assertEquals("HTML_NOT_ALLOWED", ex.getMessage());
    }

    @Test
    void validateSchemaLight_rejectsMetaPathMismatch()
    {
        ObjectNode schema = MAPPER.createObjectNode();
        schema.put("version", 1);
        schema.set("meta", MAPPER.createObjectNode().put("path", "/about"));
        schema.set("blocks", MAPPER.createArrayNode());

        ServiceException ex = assertThrows(ServiceException.class,
                () -> validator.validateSchemaLight(schema, "/"));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getCode());
    }

    @Test
    void validateSchemaLight_rejectsOversizedPayload()
    {
        ObjectNode schema = MAPPER.createObjectNode();
        schema.put("version", 1);
        schema.set("meta", MAPPER.createObjectNode().put("path", "/"));
        schema.set("blocks", MAPPER.createArrayNode());
        StringBuilder big = new StringBuilder();
        for (int i = 0; i < 2000; i++)
        {
            big.append('x');
        }
        schema.put("padding", big.toString());

        ServiceException ex = assertThrows(ServiceException.class,
                () -> validator.validateSchemaLight(schema, "/"));
        assertEquals(413, ex.getCode());
    }

    @Test
    void validateSchemaLight_acceptsAllowedBlockType()
    {
        ObjectNode schema = MAPPER.createObjectNode();
        schema.put("version", 1);
        schema.set("meta", MAPPER.createObjectNode().put("path", "/"));
        ArrayNode blocks = MAPPER.createArrayNode();
        ObjectNode hero = MAPPER.createObjectNode();
        hero.put("id", "hero-1");
        hero.put("type", "hero");
        hero.set("props", MAPPER.createObjectNode().put("title", "欢迎"));
        blocks.add(hero);
        schema.set("blocks", blocks);
        assertDoesNotThrow(() -> validator.validateSchemaLight(schema, "/"));
    }

    @Test
    void validateSchemaLight_acceptsContactWidgetBlockType()
    {
        ObjectNode schema = MAPPER.createObjectNode();
        schema.put("version", 1);
        schema.set("meta", MAPPER.createObjectNode().put("path", "/"));
        ArrayNode blocks = MAPPER.createArrayNode();
        ObjectNode block = MAPPER.createObjectNode();
        block.put("id", "contact-widget-1");
        block.put("type", "contactWidget");
        block.set("props", MAPPER.createObjectNode());
        blocks.add(block);
        schema.set("blocks", blocks);
        assertDoesNotThrow(() -> validator.validateSchemaLight(schema, "/"));
    }

    @Test
    void validateSchemaLight_acceptsVideoCardBlockType()
    {
        ObjectNode schema = MAPPER.createObjectNode();
        schema.put("version", 1);
        schema.set("meta", MAPPER.createObjectNode().put("path", "/"));
        ArrayNode blocks = MAPPER.createArrayNode();
        ObjectNode block = MAPPER.createObjectNode();
        block.put("id", "video-card-1");
        block.put("type", "videoCard");
        block.set("props", MAPPER.createObjectNode());
        blocks.add(block);
        schema.set("blocks", blocks);
        assertDoesNotThrow(() -> validator.validateSchemaLight(schema, "/"));
    }

    @Test
    void validateSchemaLight_acceptsVideoGridBlockType()
    {
        ObjectNode schema = MAPPER.createObjectNode();
        schema.put("version", 1);
        schema.set("meta", MAPPER.createObjectNode().put("path", "/"));
        ArrayNode blocks = MAPPER.createArrayNode();
        ObjectNode block = MAPPER.createObjectNode();
        block.put("id", "video-grid-1");
        block.put("type", "videoGrid");
        block.set("props", MAPPER.createObjectNode());
        blocks.add(block);
        schema.set("blocks", blocks);
        assertDoesNotThrow(() -> validator.validateSchemaLight(schema, "/"));
    }

    @Test
    void validateSchemaLight_acceptsDoctorGridBlockType()
    {
        ObjectNode schema = MAPPER.createObjectNode();
        schema.put("version", 1);
        schema.set("meta", MAPPER.createObjectNode().put("path", "/"));
        ArrayNode blocks = MAPPER.createArrayNode();
        ObjectNode block = MAPPER.createObjectNode();
        block.put("id", "doctor-grid-1");
        block.put("type", "doctorGrid");
        block.set("props", MAPPER.createObjectNode());
        blocks.add(block);
        schema.set("blocks", blocks);
        assertDoesNotThrow(() -> validator.validateSchemaLight(schema, "/"));
    }

    @Test
    void validateSchemaLight_acceptsClinicCardBlockType()
    {
        ObjectNode schema = MAPPER.createObjectNode();
        schema.put("version", 1);
        schema.set("meta", MAPPER.createObjectNode().put("path", "/"));
        ArrayNode blocks = MAPPER.createArrayNode();
        ObjectNode block = MAPPER.createObjectNode();
        block.put("id", "clinic-card-1");
        block.put("type", "clinicCard");
        block.set("props", MAPPER.createObjectNode());
        blocks.add(block);
        schema.set("blocks", blocks);
        assertDoesNotThrow(() -> validator.validateSchemaLight(schema, "/"));
    }

    @Test
    void validateSchemaLight_acceptsPictureGridBlockType()
    {
        ObjectNode schema = MAPPER.createObjectNode();
        schema.put("version", 1);
        schema.set("meta", MAPPER.createObjectNode().put("path", "/"));
        ArrayNode blocks = MAPPER.createArrayNode();
        ObjectNode block = MAPPER.createObjectNode();
        block.put("id", "picture-grid-1");
        block.put("type", "pictureGrid");
        block.set("props", MAPPER.createObjectNode());
        blocks.add(block);
        schema.set("blocks", blocks);
        assertDoesNotThrow(() -> validator.validateSchemaLight(schema, "/"));
    }

    @Test
    void validateSchemaLight_acceptsQandACardBlockType()
    {
        ObjectNode schema = MAPPER.createObjectNode();
        schema.put("version", 1);
        schema.set("meta", MAPPER.createObjectNode().put("path", "/"));
        ArrayNode blocks = MAPPER.createArrayNode();
        ObjectNode block = MAPPER.createObjectNode();
        block.put("id", "qanda-card-1");
        block.put("type", "qandACard");
        block.set("props", MAPPER.createObjectNode());
        blocks.add(block);
        schema.set("blocks", blocks);
        assertDoesNotThrow(() -> validator.validateSchemaLight(schema, "/"));
    }

    @Test
    void validateSchemaLight_acceptsOnlineRegisterBlockType()
    {
        ObjectNode schema = MAPPER.createObjectNode();
        schema.put("version", 1);
        schema.set("meta", MAPPER.createObjectNode().put("path", "/"));
        ArrayNode blocks = MAPPER.createArrayNode();
        ObjectNode block = MAPPER.createObjectNode();
        block.put("id", "online-register-1");
        block.put("type", "onlineRegister");
        block.set("props", MAPPER.createObjectNode());
        blocks.add(block);
        schema.set("blocks", blocks);
        assertDoesNotThrow(() -> validator.validateSchemaLight(schema, "/"));
    }

    @Test
    void validateSchemaLight_acceptsEventListBlockType()
    {
        ObjectNode schema = MAPPER.createObjectNode();
        schema.put("version", 1);
        schema.set("meta", MAPPER.createObjectNode().put("path", "/"));
        ArrayNode blocks = MAPPER.createArrayNode();
        ObjectNode block = MAPPER.createObjectNode();
        block.put("id", "event-list-1");
        block.put("type", "eventList");
        block.set("props", MAPPER.createObjectNode());
        blocks.add(block);
        schema.set("blocks", blocks);
        assertDoesNotThrow(() -> validator.validateSchemaLight(schema, "/"));
    }

    @Test
    void validateSchemaLight_acceptsContentListAndContentDetailBlockTypes()
    {
        ObjectNode schema = MAPPER.createObjectNode();
        schema.put("version", 1);
        schema.set("meta", MAPPER.createObjectNode().put("path", "/blog"));
        ArrayNode blocks = MAPPER.createArrayNode();
        ObjectNode list = MAPPER.createObjectNode();
        list.put("id", "content-list-1");
        list.put("type", "contentList");
        list.set("props", MAPPER.createObjectNode().put("limit", 12));
        blocks.add(list);
        ObjectNode detail = MAPPER.createObjectNode();
        detail.put("id", "content-detail-1");
        detail.put("type", "contentDetail");
        detail.set("props", MAPPER.createObjectNode());
        blocks.add(detail);
        schema.set("blocks", blocks);
        assertDoesNotThrow(() -> validator.validateSchemaLight(schema, "/blog"));
    }

    @Test
    void validateSchemaLight_rejectsInvalidBlockType()
    {
        ObjectNode schema = MAPPER.createObjectNode();
        schema.put("version", 1);
        schema.set("meta", MAPPER.createObjectNode().put("path", "/"));
        ArrayNode blocks = MAPPER.createArrayNode();
        ObjectNode block = MAPPER.createObjectNode();
        block.put("id", "div-1");
        block.put("type", "div");
        blocks.add(block);
        schema.set("blocks", blocks);

        ServiceException ex = assertThrows(ServiceException.class,
                () -> validator.validateSchemaLight(schema, "/"));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getCode());
        assertEquals("INVALID_BLOCK_TYPE", ex.getMessage());
    }

    @Test
    void validateSchemaLight_rejectsHtmlInPropsString()
    {
        ObjectNode schema = MAPPER.createObjectNode();
        schema.put("version", 1);
        schema.set("meta", MAPPER.createObjectNode().put("path", "/"));
        ArrayNode blocks = MAPPER.createArrayNode();
        ObjectNode block = MAPPER.createObjectNode();
        block.put("id", "hero-1");
        block.put("type", "hero");
        block.set("props", MAPPER.createObjectNode().put("body", "<script>alert(1)</script>"));
        blocks.add(block);
        schema.set("blocks", blocks);

        ServiceException ex = assertThrows(ServiceException.class,
                () -> validator.validateSchemaLight(schema, "/"));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getCode());
        assertEquals("HTML_NOT_ALLOWED", ex.getMessage());
    }
}
