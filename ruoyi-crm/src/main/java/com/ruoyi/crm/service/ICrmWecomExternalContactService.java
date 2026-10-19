package com.ruoyi.crm.service;

import java.util.List;

import com.ruoyi.crm.domain.CrmWecomExternalContact;
import com.ruoyi.crm.domain.dto.wecom.WecomExternalContactImportResult;
import com.ruoyi.crm.domain.dto.wecom.WecomExternalContactUpsertRequest;

public interface ICrmWecomExternalContactService
{
    /**
     * 处理解密后的 XML（外部联系人变更事件）。
     */
    void handlePlainXml(String plainXml);

    List<CrmWecomExternalContact> selectList(CrmWecomExternalContact query);

    CrmWecomExternalContact selectById(Long id);

    /**
     * 从企业微信拉取客户并写入本地映射与线索；restrictUserids 为空则同步全部配置了客户联系的成员。
     */
    WecomExternalContactImportResult importFromWecom(List<String> restrictUserids);

    /**
     * 从 KF get_customer_info 响应执行纯数据 UPSERT（不创建 Lead）。
     */
    Long upsertFromKfGetInfo(WecomExternalContactUpsertRequest request);

    /**
     * CRM 绑定/创建时 upsert 外部联系人映射（不重复建 Lead）。
     *
     * @param externalUserid 企微 external_userid
     * @param followUserid 跟进人 userid，为空时尝试当前登录用户 snUserid
     * @param contactId 关联 Contact，可为空
     * @param leadId 关联 Lead，可为空
     */
    void upsertFromCrmBinding(String externalUserid, String followUserid, String contactId, String leadId);
}
