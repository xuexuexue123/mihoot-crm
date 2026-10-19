package com.ruoyi.crm.knowledge;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.uuid.IdUtils;
import com.ruoyi.crm.domain.CrmKbDocument;
import com.ruoyi.crm.domain.CrmMarketingContent;
import com.ruoyi.crm.domain.CrmProduct;
import com.ruoyi.crm.domain.CrmSalesScript;
import com.ruoyi.crm.domain.vo.MarketingContentListVo;
import com.ruoyi.crm.domain.dto.MarketingContentQuery;
import com.ruoyi.crm.mapper.CrmKbDocumentMapper;
import com.ruoyi.crm.mapper.CrmMarketingContentMapper;
import com.ruoyi.crm.mapper.CrmProductMapper;
import com.ruoyi.crm.mapper.CrmSalesScriptMapper;

/**
 * 源表 → 知识库同步（docs/crm-knowledge-base-backend-plan.md §五）。
 *
 * 范围：sys_organization(id=2) / crm_product / crm_sales_script(qa) / crm_marketing_content(ready)。
 * 所有入口都吞异常（只记日志 + index_status），不影响主业务写入。
 */
@Service
public class CrmKnowledgeIngestService
{
    private static final Logger log = LoggerFactory.getLogger(CrmKnowledgeIngestService.class);

    /** 租户注册时公司主档固定行 */
    public static final String COMPANY_ORGANIZATION_ID = "2";

    private static final String SYNC_USER = "system-kb-sync";

    @Autowired
    private CrmKbDocumentMapper documentMapper;

    @Autowired
    private CrmProductMapper productMapper;

    @Autowired
    private CrmSalesScriptMapper salesScriptMapper;

    @Autowired
    private CrmMarketingContentMapper marketingContentMapper;

    @Autowired
    private KbIndexer kbIndexer;

    // ------------------------------------------------------------------
    // hook 入口（主业务成功后调用；异常不外抛）
    // ------------------------------------------------------------------

    public void onOrganizationChanged(String organizationId)
    {
        if (!COMPANY_ORGANIZATION_ID.equals(organizationId))
        {
            return;
        }
        safely("organization", organizationId, this::syncOrganization);
    }

    public void onProductChanged(String productId)
    {
        safely("product", productId, () -> syncProductById(productId));
    }

    public void onProductDeleted(String productId)
    {
        safely("product-delete", productId,
                () -> archiveSource(CrmKbDocument.SOURCE_PRODUCT, productId));
    }

    public void onSalesScriptChanged(String scriptId)
    {
        safely("sales_script", scriptId, () -> syncSalesScriptById(scriptId));
    }

    public void onSalesScriptDeleted(String scriptId)
    {
        safely("sales_script-delete", scriptId,
                () -> archiveSource(CrmKbDocument.SOURCE_SALES_SCRIPT, scriptId));
    }

    public void onMarketingContentChanged(String contentId)
    {
        safely("marketing_content", contentId, () -> syncMarketingContentById(contentId));
    }

    public void onMarketingContentDeleted(String contentId)
    {
        safely("marketing_content-delete", contentId,
                () -> archiveSource(CrmKbDocument.SOURCE_MARKETING_CONTENT, contentId));
    }

    /**
     * 主业务在事务中时，延迟到提交后执行（保证读到已提交数据、不拖长事务）；
     * 无论何时执行，异常只记日志，不影响主业务。
     */
    private void safely(String scope, String id, Runnable task)
    {
        Runnable guarded = () -> {
            try
            {
                task.run();
            }
            catch (Exception e)
            {
                log.warn("kb ingest hook failed scope={} id={}: {}", scope, id, e.getMessage());
            }
        };
        if (TransactionSynchronizationManager.isSynchronizationActive())
        {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization()
            {
                @Override
                public void afterCommit()
                {
                    guarded.run();
                }
            });
        }
        else
        {
            guarded.run();
        }
    }

    // ------------------------------------------------------------------
    // 全量同步（POST /crm/knowledge/reindex/sources）
    // ------------------------------------------------------------------

    public Map<String, Object> syncAllSources()
    {
        Map<String, Object> summary = new LinkedHashMap<>();

        int organization = 0;
        try
        {
            syncOrganization();
            organization = 1;
        }
        catch (Exception e)
        {
            log.warn("kb sync organization failed: {}", e.getMessage());
        }
        summary.put("organization", organization);

        int products = 0;
        List<CrmProduct> productRows = productMapper.selectCrmProductList(new CrmProduct());
        if (productRows != null)
        {
            for (CrmProduct product : productRows)
            {
                try
                {
                    syncProduct(product);
                    products++;
                }
                catch (Exception e)
                {
                    log.warn("kb sync product failed productId={}: {}", product.getProductId(), e.getMessage());
                }
            }
        }
        summary.put("product", products);

        int scripts = 0;
        CrmSalesScript scriptQuery = new CrmSalesScript();
        scriptQuery.setScriptType("qa");
        List<CrmSalesScript> scriptRows = salesScriptMapper.selectCrmSalesScriptList(scriptQuery);
        if (scriptRows != null)
        {
            for (CrmSalesScript script : scriptRows)
            {
                try
                {
                    syncSalesScript(script);
                    scripts++;
                }
                catch (Exception e)
                {
                    log.warn("kb sync sales script failed scriptId={}: {}", script.getScriptId(), e.getMessage());
                }
            }
        }
        summary.put("salesScript", scripts);

        int marketing = 0;
        List<MarketingContentListVo> contentRows = marketingContentMapper.selectList(new MarketingContentQuery());
        if (contentRows != null)
        {
            for (MarketingContentListVo vo : contentRows)
            {
                try
                {
                    syncMarketingContentById(vo.getId());
                    marketing++;
                }
                catch (Exception e)
                {
                    log.warn("kb sync marketing content failed id={}: {}", vo.getId(), e.getMessage());
                }
            }
        }
        summary.put("marketingContent", marketing);
        return summary;
    }

    // ------------------------------------------------------------------
    // 各源同步实现
    // ------------------------------------------------------------------

    public void syncOrganization()
    {
        Map<String, Object> row = documentMapper.selectOrganizationProfile(COMPANY_ORGANIZATION_ID);
        if (row == null)
        {
            archiveSource(CrmKbDocument.SOURCE_ORGANIZATION, COMPANY_ORGANIZATION_ID);
            return;
        }
        String name = str(row.get("pyName"));
        String intro = str(row.get("intro"));
        String description = str(row.get("description"));
        String body = StringUtils.isNotBlank(intro) ? intro : description;

        StringBuilder content = new StringBuilder();
        appendLine(content, "公司名称", name);
        appendLine(content, "行业", str(row.get("industry")));
        appendLine(content, "定位", str(row.get("tagline")));
        appendLine(content, "官网", str(row.get("website")));
        appendLine(content, "电话", str(row.get("tel")));
        appendLine(content, "地址", str(row.get("address")));
        if (StringUtils.isNotBlank(body))
        {
            content.append('\n').append(body.trim()).append('\n');
        }

        if (content.length() == 0)
        {
            archiveSource(CrmKbDocument.SOURCE_ORGANIZATION, COMPANY_ORGANIZATION_ID);
            return;
        }
        String title = StringUtils.isNotBlank(name) ? name + " 企业介绍" : "企业介绍";
        upsertSourceDoc(CrmKbDocument.SOURCE_ORGANIZATION, COMPANY_ORGANIZATION_ID,
                title, "company_intro", content.toString());
    }

    private void syncProductById(String productId)
    {
        CrmProduct product = productMapper.selectCrmProductByProductId(productId);
        if (product == null)
        {
            archiveSource(CrmKbDocument.SOURCE_PRODUCT, productId);
            return;
        }
        syncProduct(product);
    }

    public void syncProduct(CrmProduct product)
    {
        if (product == null || StringUtils.isBlank(product.getProductId()))
        {
            return;
        }
        // 仅 active 且未删除进入知识库；兼容旧 status（1/已启用/空）
        if ("1".equals(product.getDelFlag()) || !isProductActive(product.getStatus()))
        {
            archiveSource(CrmKbDocument.SOURCE_PRODUCT, product.getProductId());
            return;
        }

        StringBuilder content = new StringBuilder();
        appendLine(content, "产品名称", product.getProductName());
        appendLine(content, "产品编码", product.getProductCode());
        String categoryLabel = StringUtils.isNotBlank(product.getCategoryName())
            ? product.getCategoryName() : product.getCategory();
        appendLine(content, "分类", categoryLabel);
        appendLine(content, "品牌", product.getBrand());
        appendLine(content, "型号", product.getModel());
        appendLine(content, "规格", product.getSpec());
        appendLine(content, "单位", product.getUnit());
        if (product.getOutboundPrice() != null)
        {
            appendLine(content, "标准售价", product.getOutboundPrice().toPlainString());
        }
        String shortDesc = StringUtils.isNotBlank(product.getShortDescription())
            ? product.getShortDescription() : product.getDescription();
        if (StringUtils.isNotBlank(shortDesc))
        {
            content.append('\n').append(shortDesc.trim()).append('\n');
        }
        if (StringUtils.isNotBlank(product.getSellingPoints()))
        {
            content.append("\n卖点：\n").append(product.getSellingPoints().trim()).append('\n');
        }
        if (StringUtils.isNotBlank(product.getDetailMd()))
        {
            content.append('\n').append(product.getDetailMd().trim()).append('\n');
        }
        if (content.length() == 0)
        {
            archiveSource(CrmKbDocument.SOURCE_PRODUCT, product.getProductId());
            return;
        }
        String title = StringUtils.isNotBlank(product.getProductName())
                ? product.getProductName().trim() : "产品 " + product.getProductId();
        if (StringUtils.isNotBlank(product.getProductCode()))
        {
            title = title + " (" + product.getProductCode().trim() + ")";
        }
        upsertSourceDoc(CrmKbDocument.SOURCE_PRODUCT, product.getProductId(),
                title, "product", content.toString());
    }

    private static boolean isProductActive(String status)
    {
        if (StringUtils.isBlank(status))
        {
            // 迁移前空状态视为在售；迁移后应全部为明确枚举
            return true;
        }
        String trimmed = status.trim();
        if ("active".equals(trimmed) || "1".equals(trimmed) || "已启用".equals(trimmed) || "启用".equals(trimmed))
        {
            return true;
        }
        if ("draft".equals(trimmed) || "inactive".equals(trimmed) || "archived".equals(trimmed)
            || "0".equals(trimmed) || "停用".equals(trimmed))
        {
            return false;
        }
        // 未知旧值保守视为在售，避免误归档
        return true;
    }

    private void syncSalesScriptById(String scriptId)
    {
        CrmSalesScript script = salesScriptMapper.selectCrmSalesScriptByScriptId(scriptId);
        if (script == null)
        {
            archiveSource(CrmKbDocument.SOURCE_SALES_SCRIPT, scriptId);
            return;
        }
        syncSalesScript(script);
    }

    public void syncSalesScript(CrmSalesScript script)
    {
        if (script == null || StringUtils.isBlank(script.getScriptId()))
        {
            return;
        }
        // 仅 qa 且启用的话术进知识库（P0 范围）
        boolean active = "qa".equals(script.getScriptType()) && "0".equals(script.getStatus());
        if (!active)
        {
            archiveSource(CrmKbDocument.SOURCE_SALES_SCRIPT, script.getScriptId());
            return;
        }
        String content = "问：" + StringUtils.defaultString(script.getTitle()).trim()
                + "\n\n答：" + StringUtils.defaultString(script.getContent()).trim();
        upsertSourceDoc(CrmKbDocument.SOURCE_SALES_SCRIPT, script.getScriptId(),
                StringUtils.defaultString(script.getTitle()).trim(), "faq", content);
    }

    public void syncMarketingContentById(String contentId)
    {
        CrmMarketingContent content = marketingContentMapper.selectById(contentId);
        if (content == null)
        {
            archiveSource(CrmKbDocument.SOURCE_MARKETING_CONTENT, contentId);
            return;
        }
        boolean active = CrmMarketingContent.STATUS_READY.equals(content.getStatus());
        if (!active)
        {
            archiveSource(CrmKbDocument.SOURCE_MARKETING_CONTENT, contentId);
            return;
        }
        StringBuilder body = new StringBuilder();
        if (StringUtils.isNotBlank(content.getDigest()))
        {
            body.append(content.getDigest().trim()).append("\n\n");
        }
        body.append(StringUtils.defaultString(content.getContentMd()).trim());
        upsertSourceDoc(CrmKbDocument.SOURCE_MARKETING_CONTENT, content.getId(),
                StringUtils.defaultString(content.getTitle()).trim(), "marketing", body.toString());
    }

    // ------------------------------------------------------------------
    // 通用 upsert / archive
    // ------------------------------------------------------------------

    private void upsertSourceDoc(String sourceType, String sourceId, String title, String category, String contentMd)
    {
        CrmKbDocument existing = documentMapper.selectBySourceAny(sourceType, sourceId);
        Date now = new Date();
        String documentId;
        if (existing == null)
        {
            CrmKbDocument row = new CrmKbDocument();
            documentId = IdUtils.fastUUID();
            row.setDocumentId(documentId);
            row.setTitle(title);
            row.setCategory(category);
            row.setContentMd(contentMd);
            row.setStatus(CrmKbDocument.STATUS_PUBLISHED);
            row.setSourceType(sourceType);
            row.setSourceId(sourceId);
            row.setIndexStatus(CrmKbDocument.INDEX_PENDING);
            row.setDelFlag("0");
            row.setCreateBy(SYNC_USER);
            row.setCreateTime(now);
            row.setUpdateBy(SYNC_USER);
            row.setUpdateTime(now);
            documentMapper.insert(row);
        }
        else
        {
            documentId = existing.getDocumentId();
            CrmKbDocument patch = new CrmKbDocument();
            patch.setDocumentId(documentId);
            patch.setTitle(title);
            patch.setCategory(category);
            patch.setContentMd(contentMd);
            patch.setStatus(CrmKbDocument.STATUS_PUBLISHED);
            patch.setDelFlag("0");
            patch.setUpdateBy(SYNC_USER);
            patch.setUpdateTime(now);
            documentMapper.update(patch);
        }
        kbIndexer.indexDocument(documentId, false);
    }

    private void archiveSource(String sourceType, String sourceId)
    {
        if (StringUtils.isBlank(sourceId))
        {
            return;
        }
        CrmKbDocument existing = documentMapper.selectBySourceAny(sourceType, sourceId);
        if (existing == null || "1".equals(existing.getDelFlag())
                || CrmKbDocument.STATUS_ARCHIVED.equals(existing.getStatus()))
        {
            return;
        }
        CrmKbDocument patch = new CrmKbDocument();
        patch.setDocumentId(existing.getDocumentId());
        patch.setStatus(CrmKbDocument.STATUS_ARCHIVED);
        patch.setUpdateBy(SYNC_USER);
        patch.setUpdateTime(new Date());
        documentMapper.update(patch);
        // archived → indexer 清 chunk + skipped
        kbIndexer.indexDocument(existing.getDocumentId(), false);
    }

    private static void appendLine(StringBuilder sb, String label, String value)
    {
        if (StringUtils.isNotBlank(value))
        {
            sb.append(label).append('：').append(value.trim()).append('\n');
        }
    }

    private static String str(Object value)
    {
        return value == null ? null : String.valueOf(value);
    }
}
