package com.ruoyi.crm.service;

import java.util.List;
import java.util.Map;

import com.ruoyi.crm.domain.CrmKbDocument;
import com.ruoyi.crm.domain.dto.KbRetrieveRequest;
import com.ruoyi.crm.domain.vo.KbRetrieveHitVo;

/**
 * 企业知识库 Service（文档 CRUD / 发布 / 索引 / 检索）
 */
public interface ICrmKnowledgeService
{
    List<CrmKbDocument> selectDocumentList(CrmKbDocument query);

    CrmKbDocument selectDocumentById(String documentId);

    CrmKbDocument createDocument(CrmKbDocument body);

    CrmKbDocument updateDocument(CrmKbDocument body);

    int deleteDocuments(String[] documentIds);

    /** 置 published 并同步索引，返回最新文档（含 index_status） */
    CrmKbDocument publishDocument(String documentId);

    /** 强制重建单文档索引 */
    CrmKbDocument reindexDocument(String documentId);

    /** 重试所有 index_status=failed 的文档，返回 {total, indexed, failed} */
    Map<String, Object> reindexFailed();

    /** 源表全量同步（organization/product/sales_script/marketing_content） */
    Map<String, Object> reindexSources();

    List<KbRetrieveHitVo> retrieve(KbRetrieveRequest request);
}
