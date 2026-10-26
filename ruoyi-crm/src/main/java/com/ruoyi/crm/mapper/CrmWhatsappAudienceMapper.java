package com.ruoyi.crm.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ruoyi.crm.domain.CrmCustomerIdentity;

/**
 * WhatsApp 营销受众解析相关查询
 */
public interface CrmWhatsappAudienceMapper
{
    /**
     * 命中任一 tagId（dict_value）的联系人 id + mobile。
     */
    List<java.util.Map<String, Object>> selectContactsByAnyTagIds(@Param("tagIds") List<String> tagIds);

    /**
     * 批量查联系人的 WhatsApp / 手机身份。
     */
    List<CrmCustomerIdentity> selectWhatsappIdentitiesByContactIds(
            @Param("contactIds") List<String> contactIds);
}
