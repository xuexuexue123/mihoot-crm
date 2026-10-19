package com.ruoyi.crm.service.impl;

import java.io.IOException;

import java.util.Base64;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.api.services.gmail.model.MessagePartBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import com.ruoyi.crm.mapper.CrmEmailAttachmentsMapper;
import com.ruoyi.crm.domain.CrmEmailAttachments;
import com.ruoyi.crm.service.ICrmEmailAttachmentsService;
import org.springframework.web.client.RestTemplate;

/**
 * 邮件附件Service业务层处理
 * 
 * @author luohd
 * @date 2025-05-16
 */
@Service
public class CrmEmailAttachmentsServiceImpl implements ICrmEmailAttachmentsService 
{
    @Autowired
    private CrmEmailAttachmentsMapper crmEmailAttachmentsMapper;

    /**
     * 查询邮件附件
     * 
     * @param id 邮件附件主键
     * @return 邮件附件
     */
    @Override
    public CrmEmailAttachments selectCrmEmailAttachmentsById(String id)
    {
        return crmEmailAttachmentsMapper.selectCrmEmailAttachmentsById(id);
    }

    /**
     * 查询邮件附件列表
     * 
     * @param crmEmailAttachments 邮件附件
     * @return 邮件附件
     */
    @Override
    public List<CrmEmailAttachments> selectCrmEmailAttachmentsList(CrmEmailAttachments crmEmailAttachments)
    {
        return crmEmailAttachmentsMapper.selectCrmEmailAttachmentsList(crmEmailAttachments);
    }

    /**
     * 新增邮件附件
     * 
     * @param crmEmailAttachments 邮件附件
     * @return 结果
     */
    @Override
    public int insertCrmEmailAttachments(CrmEmailAttachments crmEmailAttachments)
    {
        return crmEmailAttachmentsMapper.insertCrmEmailAttachments(crmEmailAttachments);
    }

    /**
     * batch insert
     * @param crmEmailAttachments
     * @return
     */
    public int batchInsertCrmEmailAttachments(List<CrmEmailAttachments> crmEmailAttachments) {
        return crmEmailAttachmentsMapper.batchInsertCrmEmailAttachments(crmEmailAttachments);

    }

    /**
     * 修改邮件附件
     * 
     * @param crmEmailAttachments 邮件附件
     * @return 结果
     */
    @Override
    public int updateCrmEmailAttachments(CrmEmailAttachments crmEmailAttachments)
    {
        return crmEmailAttachmentsMapper.updateCrmEmailAttachments(crmEmailAttachments);
    }

    /**
     * 批量删除邮件附件
     * 
     * @param ids 需要删除的邮件附件主键
     * @return 结果
     */
    @Override
    public int deleteCrmEmailAttachmentsByIds(String[] ids)
    {
        return crmEmailAttachmentsMapper.deleteCrmEmailAttachmentsByIds(ids);
    }

    /**
     * 删除邮件附件信息
     * 
     * @param id 邮件附件主键
     * @return 结果
     */
    @Override
    public int deleteCrmEmailAttachmentsById(String id)
    {
        return crmEmailAttachmentsMapper.deleteCrmEmailAttachmentsById(id);
    }
}
