package com.ruoyi.crm.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.crm.domain.CrmWecomContactGroup;

public interface CrmWecomContactGroupMapper
{
    CrmWecomContactGroup selectByContactId(@Param("tenantId") String tenantId, @Param("contactId") String contactId);

    CrmWecomContactGroup selectByChatId(@Param("tenantId") String tenantId, @Param("chatId") String chatId);

    /**
     * 按 chatId 查询全部绑定行（无 LIMIT；用于 duplicate_binding 检测）。
     */
    List<CrmWecomContactGroup> selectListByChatId(@Param("tenantId") String tenantId, @Param("chatId") String chatId);

    CrmWecomContactGroup selectById(@Param("id") String id);

    List<CrmWecomContactGroup> selectPendingOrCreatedByOwnerAndExternal(
            @Param("tenantId") String tenantId,
            @Param("ownerUserid") String ownerUserid);

    int insert(CrmWecomContactGroup row);

    int update(CrmWecomContactGroup row);

    int upsertByContact(CrmWecomContactGroup row);
}
