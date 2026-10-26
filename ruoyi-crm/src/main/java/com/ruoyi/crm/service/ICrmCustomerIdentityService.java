package com.ruoyi.crm.service;

import java.util.List;
import java.util.Map;
import com.ruoyi.crm.domain.CrmCustomerIdentity;
import com.ruoyi.crm.domain.vo.IdentityResolveResultVo;

/**
 * 客户身份Service接口
 *
 * @author luohd
 * @date 2026-06-16
 */
public interface ICrmCustomerIdentityService
{
    /**
     * 通过任意身份标识查找 Contact。
     * 优先查 crm_customer_identity，未命中时 fallback 到 crm_contact 直查。
     *
     * @param identityType mobile_phone | email | wecom_external_userid | ...
     * @param identityValue 身份值
     * @return contactId，未找到返回 null
     */
    String findContactByIdentity(String identityType, String identityValue);

    /**
     * 通过任意身份标识查找 Contact，并返回匹配来源。
     *
     * @param identityType mobile_phone | email | wecom_external_userid | ...
     * @param identityValue 身份值
     * @return contactId 与 matchedVia（identity_table | contact_fallback），未找到时 contactId 为 null
     */
    IdentityResolveResultVo resolveIdentity(String identityType, String identityValue);

    /**
     * 批量按身份标识查找 Contact。
     *
     * @param identities List of {identityType, identityValue}
     * @return Map: key = "type:value", value = contactId (null if not found)
     */
    Map<String, String> batchResolveIdentities(List<CrmCustomerIdentity> identities);

    /**
     * 批量 resolve，返回完整结果（含 leadId、matchedVia）。
     */
    List<IdentityResolveResultVo> batchResolveIdentitiesExtended(List<CrmCustomerIdentity> identities);

    /**
     * 绑定一个身份标识到 Contact（幂等：重复绑定不抛异常）。
     * 若该身份已绑定到其他 Contact，抛出业务异常。
     *
     * @param contactId 目标 Contact
     * @param identityType 身份类型
     * @param identityValue 身份值
     * @param source 来源（manual | wecom_callback | import | contact_form ...）
     * @throws RuntimeException 若身份已绑定到其他联系人
     */
    void bindIdentity(String contactId, String identityType, String identityValue, String source);

    /**
     * 解绑一个身份标识。
     * 不会修改 crm_contact 上的对应字段。
     */
    void unbindIdentity(String contactId, String identityType, String identityValue);

    /**
     * 记录一条「身份冲突待人工合并」。
     * 当某身份值已绑定到其他 Contact、无法自动绑定到 incomingContactId 时调用（最佳努力，幂等）。
     * 内部自行归一化并查出当前绑定的 existingContactId；仅当确实冲突（existing != incoming）时写入。
     *
     * @param identityType 身份类型
     * @param identityValue 身份值（原始或归一化均可）
     * @param incomingContactId 本次尝试绑定的 Contact（可为空）
     * @param source 来源（wecom_callback | mp_callback | event_registration ...）
     */
    void recordIdentityConflict(String identityType, String identityValue,
            String incomingContactId, String source);

    /**
     * 将 sourceContactId 的所有 identity 记录合并到 targetContactId。
     * 冲突记录跳过。
     *
     * @return 合并统计
     */
    Map<String, Integer> mergeIdentity(String sourceContactId, String targetContactId);

    /**
     * 查询 Contact 的所有身份标识列表。
     */
    List<CrmCustomerIdentity> listIdentitiesByContact(String contactId);

    /**
     * 将指定身份设为该 Contact 的主身份（同一 contact 仅保留一条 is_primary=true）。
     *
     * @throws RuntimeException 若身份记录不存在或不属于该 contact
     */
    void setPrimaryIdentity(String contactId, String identityType, String identityValue);
}
