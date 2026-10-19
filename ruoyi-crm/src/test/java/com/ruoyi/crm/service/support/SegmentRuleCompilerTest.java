package com.ruoyi.crm.service.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.crm.domain.vo.SegmentFieldMetaVo;
import com.ruoyi.crm.wechatmp.domain.CrmWechatMpConfig;
import com.ruoyi.crm.wechatmp.mapper.CrmWechatMpConfigMapper;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class SegmentRuleCompilerTest
{
    @Mock
    private CrmWechatMpConfigMapper wechatMpConfigMapper;

    private WechatMpMenuClickSegmentValidator menuClickSegmentValidator;

    private WechatMpMenuClickSegmentResolver menuClickSegmentResolver;

    private SegmentRuleCompiler compiler;

    @BeforeEach
    public void setUp() throws Exception
    {
        menuClickSegmentValidator = new WechatMpMenuClickSegmentValidator();
        injectField(menuClickSegmentValidator, "wechatMpConfigMapper", wechatMpConfigMapper);
        menuClickSegmentResolver = new WechatMpMenuClickSegmentResolver();
        injectField(menuClickSegmentResolver, "menuClickSegmentValidator", menuClickSegmentValidator);
        compiler = new SegmentRuleCompiler();
        injectField(compiler, "menuClickSegmentValidator", menuClickSegmentValidator);
        injectField(compiler, "menuClickSegmentResolver", menuClickSegmentResolver);
        injectField(compiler, "tagSegmentResolver", new TagSegmentResolver());

        CrmWechatMpConfig config = new CrmWechatMpConfig();
        config.setAppId("wxTest");
        when(wechatMpConfigMapper.selectByAppId("wxTest")).thenReturn(config);
    }

    private static void injectField(Object target, String fieldName, Object value) throws Exception
    {
        java.lang.reflect.Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    @Test
    public void compilesMetricInCondition()
    {
        JSONObject rule = leaf("metric", "lifecycleStage", "in", new String[] { "lead", "opportunity" });
        CompiledSegmentQuery query = compiler.compile(rule);

        assertTrue(query.getWhereClause().contains("m.lifecycle_stage IN (?, ?)"));
        assertEquals(2, query.getParams().size());
        assertEquals("lead", query.getParams().get(0));
        assertEquals("opportunity", query.getParams().get(1));
    }

    @Test
    public void compilesAttributeOriginalSource()
    {
        JSONObject rule = leaf("attribute", "originalSource", "in", new String[] { "mp", "wecom" });
        CompiledSegmentQuery query = compiler.compile(rule);

        assertTrue(query.getWhereClause().contains("crm_contact c"));
        assertTrue(query.getWhereClause().contains("c.original_source IN (?, ?)"));
        assertEquals(2, query.getParams().size());
    }

    @Test
    public void compilesAttributeLocationContains()
    {
        JSONObject rule = leaf("attribute", "location", "contains", "上海");
        CompiledSegmentQuery query = compiler.compile(rule);

        assertTrue(query.getWhereClause().contains("c.location ILIKE ?"));
        assertEquals("%上海%", query.getParams().get(0));
    }

    @Test
    public void compilesAttributeUserIdComparison()
    {
        JSONObject rule = leaf("attribute", "userId", ">=", 100);
        CompiledSegmentQuery query = compiler.compile(rule);

        assertTrue(query.getWhereClause().contains("c.user_id >= ?"));
        assertEquals(100, ((Number) query.getParams().get(0)).intValue());
    }

    @Test
    public void compilesAttributeOwnerEquals()
    {
        JSONObject rule = leaf("attribute", "owner", "=", "张三");
        CompiledSegmentQuery query = compiler.compile(rule);

        assertTrue(query.getWhereClause().contains("c.owner = ?"));
        assertEquals("张三", query.getParams().get(0));
    }

    @Test
    public void compilesReachabilityExists()
    {
        JSONObject rule = leaf("reachability", "wecom_external_userid", "exists", null);
        CompiledSegmentQuery query = compiler.compile(rule);

        assertTrue(query.getWhereClause().contains("crm_customer_identity i"));
        assertTrue(query.getWhereClause().contains("i.identity_type = ?"));
        assertEquals("wecom_external_userid", query.getParams().get(0));
    }

    @Test
    public void compilesNestedAndRule()
    {
        JSONObject rule = new JSONObject();
        rule.put("op", "AND");
        rule.put("children", new Object[] {
                leaf("metric", "totalRevenue", ">=", 5000),
                leaf("reachability", "wecom_external_userid", "exists", null)
        });

        CompiledSegmentQuery query = compiler.compile(rule);

        assertTrue(query.getWhereClause().contains("m.total_revenue >= ?"));
        assertTrue(query.getWhereClause().contains("crm_customer_identity i"));
        assertEquals(2, query.getParams().size());
    }

    @Test
    public void compilesDatetimeWithinLastDays()
    {
        JSONObject rule = leaf("metric", "lastVisitAt", "within_last_days", 90);
        CompiledSegmentQuery query = compiler.compile(rule);

        assertTrue(query.getWhereClause().contains("m.last_visit_at >= now() - (? || ' days')::interval"));
        assertEquals(90, query.getParams().get(0));
    }

    @Test
    public void compilesNotBranch()
    {
        JSONObject child = leaf("metric", "lifecycleStage", "in", new String[] { "churned" });
        JSONObject rule = new JSONObject();
        rule.put("op", "NOT");
        rule.put("children", new Object[] { child });

        CompiledSegmentQuery query = compiler.compile(rule);
        assertTrue(query.getWhereClause().startsWith("NOT ("));
    }

    @Test
    public void rejectsLegacyLeadSourceField()
    {
        JSONObject rule = leaf("attribute", "leadSource", "in", new String[] { "mp" });
        assertThrows(ServiceException.class, () -> compiler.compile(rule));
    }

    @Test
    public void rejectsLegacyCityField()
    {
        JSONObject rule = leaf("attribute", "city", "contains", "上海");
        assertThrows(ServiceException.class, () -> compiler.compile(rule));
    }

    @Test
    public void rejectsLegacyOwnerIdField()
    {
        JSONObject rule = leaf("attribute", "ownerId", "=", "1");
        assertThrows(ServiceException.class, () -> compiler.compile(rule));
    }

    @Test
    public void rejectsSqlInjectionInFieldName()
    {
        JSONObject rule = leaf("metric", "totalRevenue; DROP TABLE crm_contact;", ">=", 1);
        assertThrows(ServiceException.class, () -> compiler.compile(rule));
    }

    @Test
    public void rejectsInvalidOperator()
    {
        JSONObject rule = leaf("metric", "totalRevenue", "contains", 1);
        assertThrows(ServiceException.class, () -> compiler.compile(rule));
    }

    @Test
    public void emptyRuleDefaultsToTrue()
    {
        CompiledSegmentQuery query = compiler.compile(new JSONObject());
        assertEquals("TRUE", query.getWhereClause());
        assertTrue(query.getParams().isEmpty());
    }

    @Test
    public void fieldRegistryExposesRealAttributeFields()
    {
        assertTrue(SegmentFieldRegistry.get("attribute", "originalSource") != null);
        assertTrue(SegmentFieldRegistry.get("attribute", "location") != null);
        assertTrue(SegmentFieldRegistry.get("attribute", "owner") != null);
        assertTrue(SegmentFieldRegistry.get("attribute", "userId") != null);
        assertTrue(SegmentFieldRegistry.get("attribute", "leadSource") == null);
    }

    @Test
    public void fieldRegistryExposesWechatMpMenuArticleViewed()
    {
        SegmentFieldDefinition definition = SegmentFieldRegistry.get("metric",
                SegmentFieldRegistry.FIELD_WECHAT_MP_MENU_ARTICLE_VIEWED);
        assertTrue(definition != null);
        assertEquals(SegmentFieldRegistry.TYPE_WECHAT_MP_MENU, definition.getFieldType());
        assertTrue(definition.getOperators().contains(SegmentOperator.EXISTS));
        assertTrue(definition.getOperators().contains(SegmentOperator.NOT_EXISTS));

        SegmentFieldMetaVo meta = SegmentFieldRegistry.allFieldMeta().stream()
                .filter(f -> SegmentFieldRegistry.FIELD_WECHAT_MP_MENU_ARTICLE_VIEWED.equals(f.getField()))
                .findFirst()
                .orElse(null);
        assertTrue(meta != null);
        assertEquals("wechat_mp_menu", meta.getType());
        assertEquals("查看过公众号菜单文章", meta.getLabel());
    }

    @Test
    public void compilesWechatMpMenuArticleExists()
    {
        JSONObject value = new JSONObject();
        value.put("appId", "wxTest");
        value.put("menuKey", "MENU_KEY_1");

        JSONObject rule = leaf("metric", SegmentFieldRegistry.FIELD_WECHAT_MP_MENU_ARTICLE_VIEWED, "exists", value);
        CompiledSegmentQuery query = compiler.compile(rule);

        assertTrue(query.getWhereClause().contains("crm_activity a"));
        assertTrue(query.getWhereClause().contains("channel.wechat_mp.menu.click"));
        assertTrue(query.getWhereClause().contains("crm_customer_identity i"));
        assertTrue(query.getParams().contains("wxTest"));
        assertTrue(query.getParams().contains("MENU_KEY_1"));
    }

    @Test
    public void compilesWechatMpMenuArticleNotExists()
    {
        JSONObject value = new JSONObject();
        value.put("appId", "wxTest");
        value.put("menuKey", "MENU_KEY_1");

        JSONObject rule = leaf("metric", SegmentFieldRegistry.FIELD_WECHAT_MP_MENU_ARTICLE_VIEWED,
                "not_exists", value);
        CompiledSegmentQuery query = compiler.compile(rule);

        assertTrue(query.getWhereClause().contains("NOT EXISTS"));
    }

    @Test
    public void compilesWechatMpMenuWithMetricAnd()
    {
        JSONObject value = new JSONObject();
        value.put("appId", "wxTest");
        value.put("menuKey", "MENU_KEY_1");

        JSONObject rule = new JSONObject();
        rule.put("op", "AND");
        rule.put("children", new Object[] {
                leaf("metric", "totalRevenue", ">=", 1000),
                leaf("metric", SegmentFieldRegistry.FIELD_WECHAT_MP_MENU_ARTICLE_VIEWED, "exists", value)
        });

        CompiledSegmentQuery query = compiler.compile(rule);

        assertTrue(query.getWhereClause().contains("m.total_revenue >= ?"));
        assertTrue(query.getWhereClause().contains("crm_activity a"));
    }

    @Test
    public void rejectsMissingMenuKey()
    {
        JSONObject value = new JSONObject();
        value.put("appId", "wxTest");

        JSONObject rule = leaf("metric", SegmentFieldRegistry.FIELD_WECHAT_MP_MENU_ARTICLE_VIEWED, "exists", value);
        ServiceException ex = assertThrows(ServiceException.class, () -> compiler.compile(rule));
        assertTrue(ex.getMessage().contains("SEGMENT_INVALID_RULE"));
    }

    @Test
    public void fieldRegistryExposesCrmTag()
    {
        SegmentFieldDefinition definition = SegmentFieldRegistry.get(SegmentFieldRegistry.SOURCE_TAG,
                SegmentFieldRegistry.FIELD_CRM_TAG);
        assertTrue(definition != null);
        assertTrue(definition.getOperators().contains(SegmentOperator.CONTAINS_ALL));
        assertTrue(definition.getOperators().contains(SegmentOperator.IN));
    }

    @Test
    public void compilesTagIn()
    {
        JSONObject rule = leaf("tag", "crmTag", "in", new String[] { "risk.price_sensitive", "opportunity.high_intent" });
        CompiledSegmentQuery query = compiler.compile(rule);

        assertTrue(query.getWhereClause().contains("crm_tag_ids(c.tag::text)"));
        assertTrue(query.getWhereClause().contains("&&"));
        assertEquals(2, query.getParams().size());
        assertEquals("risk.price_sensitive", query.getParams().get(0));
    }

    @Test
    public void compilesTagContainsAll()
    {
        JSONObject rule = leaf("tag", "crmTag", "contains_all", new String[] { "a.b", "c.d" });
        CompiledSegmentQuery query = compiler.compile(rule);
        assertTrue(query.getWhereClause().contains("@>"));
    }

    @Test
    public void compilesTagIsNull()
    {
        JSONObject rule = leaf("tag", "crmTag", "is_null", null);
        CompiledSegmentQuery query = compiler.compile(rule);
        assertTrue(query.getWhereClause().contains("cardinality(crm_tag_ids(c.tag::text)) = 0"));
    }

    @Test
    public void compilesTagNotIn()
    {
        JSONObject rule = leaf("tag", "crmTag", "not_in", new String[] { "risk.price_sensitive" });
        CompiledSegmentQuery query = compiler.compile(rule);
        assertTrue(query.getWhereClause().startsWith("NOT ("));
    }

    @Test
    public void rejectsNonStringTagValue()
    {
        JSONObject rule = leaf("tag", "crmTag", "in", new Object[] { 1, 2 });
        ServiceException ex = assertThrows(ServiceException.class, () -> compiler.compile(rule));
        assertTrue(ex.getMessage().contains("SEGMENT_INVALID_RULE"));
    }

    private JSONObject leaf(String source, String field, String operator, Object value)
    {
        JSONObject node = new JSONObject();
        node.put("source", source);
        node.put("field", field);
        node.put("operator", operator);
        if (value != null)
        {
            node.put("value", value);
        }
        return node;
    }
}
