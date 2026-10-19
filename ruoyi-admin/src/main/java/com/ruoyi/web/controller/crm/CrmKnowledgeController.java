package com.ruoyi.web.controller.crm;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.pagehelper.PageHelper;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.PageDomain;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.core.page.TableSupport;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.sql.SqlUtil;
import com.ruoyi.crm.domain.CrmKbDocument;
import com.ruoyi.crm.domain.dto.KbRetrieveRequest;
import com.ruoyi.crm.domain.vo.KbRetrieveHitVo;
import com.ruoyi.crm.service.ICrmKnowledgeService;

/**
 * 企业知识库 Controller（pgvector RAG）
 * 契约：docs/crm-knowledge-base-backend-plan.md §六
 */
@RestController
@RequestMapping("/crm/knowledge")
public class CrmKnowledgeController extends BaseController
{
    private static final int MAX_PAGE_SIZE = 100;

    @Autowired
    private ICrmKnowledgeService crmKnowledgeService;

    /**
     * 分页列表；支持 category / status / sourceType / indexStatus / keyword
     */
    @PreAuthorize("@ss.hasPermi('crm:knowledge:list')")
    @GetMapping("/document/list")
    public TableDataInfo list(CrmKbDocument query)
    {
        startPageCapped();
        List<CrmKbDocument> list = crmKnowledgeService.selectDocumentList(query);
        return getDataTable(list);
    }

    /**
     * 详情
     */
    @PreAuthorize("@ss.hasPermi('crm:knowledge:query')")
    @GetMapping("/document/{documentId}")
    public AjaxResult getInfo(@PathVariable("documentId") String documentId)
    {
        return success(crmKnowledgeService.selectDocumentById(documentId));
    }

    /**
     * 手工创建；默认 draft
     */
    @PreAuthorize("@ss.hasPermi('crm:knowledge:add')")
    @Log(title = "企业知识库", businessType = BusinessType.INSERT)
    @PostMapping("/document")
    public AjaxResult add(@RequestBody CrmKbDocument body)
    {
        return success(crmKnowledgeService.createDocument(body));
    }

    /**
     * 更新；若已 published 则重嵌
     */
    @PreAuthorize("@ss.hasPermi('crm:knowledge:edit')")
    @Log(title = "企业知识库", businessType = BusinessType.UPDATE)
    @PutMapping("/document")
    public AjaxResult edit(@RequestBody CrmKbDocument body)
    {
        return success(crmKnowledgeService.updateDocument(body));
    }

    /**
     * 逻辑删 + 清 chunk
     */
    @PreAuthorize("@ss.hasPermi('crm:knowledge:remove')")
    @Log(title = "企业知识库", businessType = BusinessType.DELETE)
    @DeleteMapping("/document/{documentIds}")
    public AjaxResult remove(@PathVariable String[] documentIds)
    {
        return toAjax(crmKnowledgeService.deleteDocuments(documentIds));
    }

    /**
     * 置 published 并索引
     */
    @PreAuthorize("@ss.hasPermi('crm:knowledge:edit')")
    @Log(title = "企业知识库", businessType = BusinessType.UPDATE)
    @PostMapping("/document/{documentId}/publish")
    public AjaxResult publish(@PathVariable("documentId") String documentId)
    {
        return success(crmKnowledgeService.publishDocument(documentId));
    }

    /**
     * 强制重嵌单文档
     */
    @PreAuthorize("@ss.hasPermi('crm:knowledge:edit')")
    @Log(title = "企业知识库", businessType = BusinessType.UPDATE)
    @PostMapping("/document/{documentId}/reindex")
    public AjaxResult reindex(@PathVariable("documentId") String documentId)
    {
        return success(crmKnowledgeService.reindexDocument(documentId));
    }

    /**
     * RAG 检索（Assistant search_knowledge）
     */
    @PreAuthorize("@ss.hasPermi('crm:knowledge:retrieve')")
    @PostMapping("/retrieve")
    public AjaxResult retrieve(@RequestBody KbRetrieveRequest request)
    {
        List<KbRetrieveHitVo> hits = crmKnowledgeService.retrieve(request);
        return success(Collections.singletonMap("hits", hits));
    }

    /**
     * 从源表全量同步（organization / product / sales_script / marketing_content）
     */
    @PreAuthorize("@ss.hasPermi('crm:knowledge:edit')")
    @Log(title = "企业知识库", businessType = BusinessType.UPDATE)
    @PostMapping("/reindex/sources")
    public AjaxResult reindexSources()
    {
        return success(crmKnowledgeService.reindexSources());
    }

    /**
     * 重试 index_status=failed 的文档
     */
    @PreAuthorize("@ss.hasPermi('crm:knowledge:edit')")
    @Log(title = "企业知识库", businessType = BusinessType.UPDATE)
    @PostMapping("/reindex/failed")
    public AjaxResult reindexFailed()
    {
        return success(crmKnowledgeService.reindexFailed());
    }

    private void startPageCapped()
    {
        PageDomain pageDomain = TableSupport.buildPageRequest();
        Integer pageNum = pageDomain.getPageNum();
        Integer pageSize = pageDomain.getPageSize();
        if (pageSize != null && pageSize > MAX_PAGE_SIZE)
        {
            pageSize = MAX_PAGE_SIZE;
        }
        String orderBy = SqlUtil.escapeOrderBySql(pageDomain.getOrderBy());
        PageHelper.startPage(pageNum, pageSize, orderBy).setReasonable(pageDomain.getReasonable());
    }
}
