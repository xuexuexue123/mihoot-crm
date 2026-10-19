package com.ruoyi.crm.service.impl;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.uuid.IdUtils;
import com.ruoyi.crm.domain.CrmKbDocument;
import com.ruoyi.crm.domain.dto.KbRetrieveRequest;
import com.ruoyi.crm.domain.vo.KbRetrieveHitVo;
import com.ruoyi.crm.knowledge.CrmKnowledgeIngestService;
import com.ruoyi.crm.knowledge.EmbeddingClient;
import com.ruoyi.crm.knowledge.KbIndexer;
import com.ruoyi.crm.mapper.CrmKbChunkMapper;
import com.ruoyi.crm.mapper.CrmKbDocumentMapper;
import com.ruoyi.crm.service.ICrmKnowledgeService;

/**
 * 企业知识库 Service 实现（docs/crm-knowledge-base-backend-plan.md §四/§六）
 */
@Service
public class CrmKnowledgeServiceImpl implements ICrmKnowledgeService
{
    private static final Set<String> ALLOWED_CATEGORIES = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
            "company_intro", "business", "service", "brand", "faq", "case", "persona",
            "product", "marketing", "other")));

    private static final Set<String> ALLOWED_STATUS = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
            CrmKbDocument.STATUS_DRAFT, CrmKbDocument.STATUS_PUBLISHED, CrmKbDocument.STATUS_ARCHIVED)));

    private static final int DEFAULT_TOP_K = 8;
    private static final int MAX_TOP_K = 20;
    private static final double DEFAULT_MIN_SCORE = 0.2;

    @Autowired
    private CrmKbDocumentMapper documentMapper;

    @Autowired
    private CrmKbChunkMapper chunkMapper;

    @Autowired
    private KbIndexer kbIndexer;

    @Autowired
    private EmbeddingClient embeddingClient;

    @Autowired
    private CrmKnowledgeIngestService ingestService;

    @Override
    public List<CrmKbDocument> selectDocumentList(CrmKbDocument query)
    {
        if (query == null)
        {
            query = new CrmKbDocument();
        }
        if (StringUtils.isNotBlank(query.getKeyword()))
        {
            query.setKeyword(query.getKeyword().trim());
        }
        return documentMapper.selectList(query);
    }

    @Override
    public CrmKbDocument selectDocumentById(String documentId)
    {
        if (StringUtils.isBlank(documentId))
        {
            return null;
        }
        return documentMapper.selectByDocumentId(documentId.trim());
    }

    @Override
    public CrmKbDocument createDocument(CrmKbDocument body)
    {
        validate(body, false);
        String username = safeUsername();
        Date now = new Date();
        body.setDocumentId(IdUtils.fastUUID());
        body.setSourceType(CrmKbDocument.SOURCE_MANUAL);
        body.setSourceId(null);
        body.setIndexStatus(CrmKbDocument.INDEX_PENDING);
        body.setDelFlag("0");
        body.setCreateBy(username);
        body.setCreateTime(now);
        body.setUpdateBy(username);
        body.setUpdateTime(now);
        documentMapper.insert(body);
        if (CrmKbDocument.STATUS_PUBLISHED.equals(body.getStatus()))
        {
            kbIndexer.indexDocument(body.getDocumentId(), false);
        }
        return documentMapper.selectByDocumentId(body.getDocumentId());
    }

    @Override
    public CrmKbDocument updateDocument(CrmKbDocument body)
    {
        if (body == null || StringUtils.isBlank(body.getDocumentId()))
        {
            throw new ServiceException("documentId 不能为空", HttpStatus.BAD_REQUEST);
        }
        body.setDocumentId(body.getDocumentId().trim());
        CrmKbDocument existing = documentMapper.selectByDocumentId(body.getDocumentId());
        if (existing == null)
        {
            throw new ServiceException("知识文档不存在或已删除", HttpStatus.BAD_REQUEST);
        }
        validate(body, true);
        body.setSourceType(null);
        body.setSourceId(null);
        body.setUpdateBy(safeUsername());
        body.setUpdateTime(new Date());
        documentMapper.update(body);
        // 内容或状态变化后同步索引（published 重嵌；draft/archived 清 chunk）
        kbIndexer.indexDocument(body.getDocumentId(), false);
        return documentMapper.selectByDocumentId(body.getDocumentId());
    }

    @Override
    public int deleteDocuments(String[] documentIds)
    {
        if (documentIds == null || documentIds.length == 0)
        {
            return 0;
        }
        int rows = documentMapper.logicalDeleteByDocumentIds(documentIds, safeUsername());
        for (String documentId : documentIds)
        {
            // 软删后 indexer 会清 chunk + skipped
            kbIndexer.indexDocument(documentId, false);
        }
        return rows;
    }

    @Override
    public CrmKbDocument publishDocument(String documentId)
    {
        CrmKbDocument existing = requireDocument(documentId);
        CrmKbDocument patch = new CrmKbDocument();
        patch.setDocumentId(existing.getDocumentId());
        patch.setStatus(CrmKbDocument.STATUS_PUBLISHED);
        patch.setUpdateBy(safeUsername());
        patch.setUpdateTime(new Date());
        documentMapper.update(patch);
        kbIndexer.indexDocument(existing.getDocumentId(), false);
        return documentMapper.selectByDocumentId(existing.getDocumentId());
    }

    @Override
    public CrmKbDocument reindexDocument(String documentId)
    {
        CrmKbDocument existing = requireDocument(documentId);
        kbIndexer.indexDocument(existing.getDocumentId(), true);
        return documentMapper.selectByDocumentId(existing.getDocumentId());
    }

    @Override
    public Map<String, Object> reindexFailed()
    {
        List<CrmKbDocument> failed = documentMapper.selectFailed();
        int indexed = 0;
        int stillFailed = 0;
        for (CrmKbDocument doc : failed)
        {
            String result = kbIndexer.indexDocument(doc.getDocumentId(), true);
            if (CrmKbDocument.INDEX_FAILED.equals(result))
            {
                stillFailed++;
            }
            else
            {
                indexed++;
            }
        }
        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("total", failed.size());
        summary.put("indexed", indexed);
        summary.put("failed", stillFailed);
        return summary;
    }

    @Override
    public Map<String, Object> reindexSources()
    {
        return ingestService.syncAllSources();
    }

    @Override
    public List<KbRetrieveHitVo> retrieve(KbRetrieveRequest request)
    {
        if (request == null || StringUtils.isBlank(request.getQuery()))
        {
            throw new ServiceException("query 不能为空", HttpStatus.BAD_REQUEST);
        }
        int topK = request.getTopK() == null ? DEFAULT_TOP_K : request.getTopK();
        if (topK < 1)
        {
            topK = DEFAULT_TOP_K;
        }
        if (topK > MAX_TOP_K)
        {
            topK = MAX_TOP_K;
        }
        double minScore = request.getMinScore() == null ? DEFAULT_MIN_SCORE : request.getMinScore();

        List<String> categories = null;
        if (request.getCategories() != null && !request.getCategories().isEmpty())
        {
            categories = request.getCategories().stream()
                    .filter(StringUtils::isNotBlank)
                    .map(String::trim)
                    .filter(ALLOWED_CATEGORIES::contains)
                    .collect(Collectors.toList());
            if (categories.isEmpty())
            {
                categories = null;
            }
        }

        String queryVector = embeddingClient.embedOne(request.getQuery().trim());
        return chunkMapper.retrieve(queryVector, topK, categories, minScore);
    }

    private CrmKbDocument requireDocument(String documentId)
    {
        if (StringUtils.isBlank(documentId))
        {
            throw new ServiceException("documentId 不能为空", HttpStatus.BAD_REQUEST);
        }
        CrmKbDocument existing = documentMapper.selectByDocumentId(documentId.trim());
        if (existing == null)
        {
            throw new ServiceException("知识文档不存在或已删除", HttpStatus.BAD_REQUEST);
        }
        return existing;
    }

    private void validate(CrmKbDocument body, boolean isUpdate)
    {
        if (body == null)
        {
            throw new ServiceException("请求体不能为空", HttpStatus.BAD_REQUEST);
        }
        if (!isUpdate || body.getTitle() != null)
        {
            String title = body.getTitle() == null ? "" : body.getTitle().trim();
            if (StringUtils.isBlank(title))
            {
                throw new ServiceException("title 不能为空", HttpStatus.BAD_REQUEST);
            }
            if (title.length() > 512)
            {
                throw new ServiceException("title 不能超过 512 字", HttpStatus.BAD_REQUEST);
            }
            body.setTitle(title);
        }
        if (!isUpdate || body.getCategory() != null)
        {
            String category = body.getCategory() == null ? "" : body.getCategory().trim();
            if (StringUtils.isBlank(category))
            {
                category = "other";
            }
            if (!ALLOWED_CATEGORIES.contains(category))
            {
                throw new ServiceException("category 非法: " + category, HttpStatus.BAD_REQUEST);
            }
            body.setCategory(category);
        }
        if (!isUpdate && body.getContentMd() == null)
        {
            body.setContentMd("");
        }
        if (StringUtils.isBlank(body.getStatus()))
        {
            if (!isUpdate)
            {
                body.setStatus(CrmKbDocument.STATUS_DRAFT);
            }
        }
        else
        {
            String status = body.getStatus().trim();
            if (!ALLOWED_STATUS.contains(status))
            {
                throw new ServiceException("status 必须为 draft / published / archived", HttpStatus.BAD_REQUEST);
            }
            body.setStatus(status);
        }
    }

    private static String safeUsername()
    {
        try
        {
            return SecurityUtils.getUsername();
        }
        catch (Exception e)
        {
            return "system";
        }
    }
}
