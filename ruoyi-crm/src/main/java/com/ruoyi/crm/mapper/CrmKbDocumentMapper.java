package com.ruoyi.crm.mapper;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.ruoyi.crm.domain.CrmKbDocument;

/**
 * 知识库文档 Mapper
 */
public interface CrmKbDocumentMapper
{
    CrmKbDocument selectByDocumentId(@Param("documentId") String documentId);

    /** 含软删行（source upsert 复活用） */
    CrmKbDocument selectBySourceAny(@Param("sourceType") String sourceType, @Param("sourceId") String sourceId);

    List<CrmKbDocument> selectList(CrmKbDocument query);

    /** index_status = failed 且未删 */
    List<CrmKbDocument> selectFailed();

    /** published 且未删（全量重建用） */
    List<CrmKbDocument> selectPublished();

    int insert(CrmKbDocument row);

    int update(CrmKbDocument row);

    int logicalDeleteByDocumentIds(@Param("documentIds") String[] documentIds, @Param("updateBy") String updateBy);

    int updateIndexStatus(@Param("documentId") String documentId,
            @Param("indexStatus") String indexStatus,
            @Param("indexError") String indexError,
            @Param("indexTime") Date indexTime,
            @Param("contentHash") String contentHash);

    /** 公司主档（sys_organization 指定行），避免 crm 模块依赖 system 模块 */
    Map<String, Object> selectOrganizationProfile(@Param("organizationId") String organizationId);
}
