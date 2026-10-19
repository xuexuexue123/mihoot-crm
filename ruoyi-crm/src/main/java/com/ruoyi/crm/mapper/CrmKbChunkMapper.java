package com.ruoyi.crm.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ruoyi.crm.domain.CrmKbChunk;
import com.ruoyi.crm.domain.vo.KbRetrieveHitVo;

/**
 * 知识库切块 Mapper（pgvector）
 */
public interface CrmKbChunkMapper
{
    int deleteByDocumentId(@Param("documentId") String documentId);

    int insertBatch(@Param("list") List<CrmKbChunk> list);

    int countByDocumentId(@Param("documentId") String documentId);

    List<KbRetrieveHitVo> retrieve(@Param("queryVector") String queryVector,
            @Param("topK") int topK,
            @Param("categories") List<String> categories,
            @Param("minScore") double minScore);
}
