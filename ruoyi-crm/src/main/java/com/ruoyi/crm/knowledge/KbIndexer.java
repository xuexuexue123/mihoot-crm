package com.ruoyi.crm.knowledge;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.uuid.IdUtils;
import com.ruoyi.crm.domain.CrmKbChunk;
import com.ruoyi.crm.domain.CrmKbDocument;
import com.ruoyi.crm.mapper.CrmKbChunkMapper;
import com.ruoyi.crm.mapper.CrmKbDocumentMapper;

/**
 * 知识库索引管道：切块 → embedding → 替换 crm_kb_chunk。
 *
 * 同一 document 串行（进程内锁）；content_hash 未变且已 indexed 时跳过；
 * 失败保留上一版 chunk（避免检索空洞），写 index_status=failed。
 */
@Component
public class KbIndexer
{
    private static final Logger log = LoggerFactory.getLogger(KbIndexer.class);

    @Autowired
    private CrmKbDocumentMapper documentMapper;

    @Autowired
    private CrmKbChunkMapper chunkMapper;

    @Autowired
    private EmbeddingClient embeddingClient;

    private final ConcurrentHashMap<String, Object> documentLocks = new ConcurrentHashMap<>();

    /**
     * @param force true 时忽略 content_hash 强制重嵌
     * @return 最终 index_status
     */
    public String indexDocument(String documentId, boolean force)
    {
        if (StringUtils.isBlank(documentId))
        {
            return CrmKbDocument.INDEX_SKIPPED;
        }
        Object lock = documentLocks.computeIfAbsent(documentId, k -> new Object());
        synchronized (lock)
        {
            try
            {
                return doIndex(documentId, force);
            }
            finally
            {
                documentLocks.remove(documentId, lock);
            }
        }
    }

    private String doIndex(String documentId, boolean force)
    {
        CrmKbDocument doc = documentMapper.selectByDocumentId(documentId);
        if (doc == null || !CrmKbDocument.STATUS_PUBLISHED.equals(doc.getStatus()))
        {
            // draft / archived / 软删：清空 chunk，标记 skipped
            chunkMapper.deleteByDocumentId(documentId);
            documentMapper.updateIndexStatus(documentId, CrmKbDocument.INDEX_SKIPPED, null, new Date(), null);
            return CrmKbDocument.INDEX_SKIPPED;
        }

        String hash = contentHash(doc);
        if (!force && hash.equals(doc.getContentHash())
                && CrmKbDocument.INDEX_INDEXED.equals(doc.getIndexStatus()))
        {
            return CrmKbDocument.INDEX_INDEXED;
        }

        try
        {
            List<String> chunkTexts = KbTextChunker.chunk(buildIndexText(doc));
            if (chunkTexts.isEmpty())
            {
                chunkMapper.deleteByDocumentId(documentId);
                documentMapper.updateIndexStatus(documentId, CrmKbDocument.INDEX_SKIPPED,
                        "正文为空，无可索引内容", new Date(), hash);
                return CrmKbDocument.INDEX_SKIPPED;
            }

            List<String> vectors = embeddingClient.embedBatch(chunkTexts);
            List<CrmKbChunk> chunks = new ArrayList<>(chunkTexts.size());
            for (int i = 0; i < chunkTexts.size(); i++)
            {
                CrmKbChunk chunk = new CrmKbChunk();
                chunk.setChunkId(IdUtils.fastUUID());
                chunk.setDocumentId(documentId);
                chunk.setChunkIndex(i);
                chunk.setChunkText(chunkTexts.get(i));
                chunk.setTokenEstimate(estimateTokens(chunkTexts.get(i)));
                chunk.setMetadata(doc.getMetadata());
                chunk.setEmbedding(vectors.get(i));
                chunks.add(chunk);
            }

            // embedding 全部成功后再替换，失败时保留旧 chunk
            chunkMapper.deleteByDocumentId(documentId);
            chunkMapper.insertBatch(chunks);
            documentMapper.updateIndexStatus(documentId, CrmKbDocument.INDEX_INDEXED, null, new Date(), hash);
            return CrmKbDocument.INDEX_INDEXED;
        }
        catch (Exception e)
        {
            String error = StringUtils.substring(String.valueOf(e.getMessage()), 0, 1000);
            log.warn("kb index failed documentId={}: {}", documentId, error);
            documentMapper.updateIndexStatus(documentId, CrmKbDocument.INDEX_FAILED, error, new Date(), null);
            return CrmKbDocument.INDEX_FAILED;
        }
    }

    /** 标题并入首块上下文，提升短问句召回 */
    private static String buildIndexText(CrmKbDocument doc)
    {
        String title = StringUtils.isBlank(doc.getTitle()) ? "" : doc.getTitle().trim();
        String content = doc.getContentMd() == null ? "" : doc.getContentMd().trim();
        if (title.isEmpty())
        {
            return content;
        }
        return title + "\n\n" + content;
    }

    private String contentHash(CrmKbDocument doc)
    {
        String material = embeddingClient.getModel() + "|" + embeddingClient.getDimensions()
                + "|" + StringUtils.defaultString(doc.getTitle())
                + "|" + StringUtils.defaultString(doc.getContentMd());
        return sha256Hex(material);
    }

    private static String sha256Hex(String value)
    {
        try
        {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest(value.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(bytes.length * 2);
            for (byte b : bytes)
            {
                sb.append(Character.forDigit((b >> 4) & 0xF, 16));
                sb.append(Character.forDigit(b & 0xF, 16));
            }
            return sb.toString();
        }
        catch (Exception e)
        {
            throw new IllegalStateException("SHA-256 unavailable", e);
        }
    }

    private static Integer estimateTokens(String text)
    {
        // 粗估：中文约 1 字 1 token，直接取字符数
        return text == null ? 0 : text.length();
    }
}
