package com.ruoyi.crm.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ruoyi.crm.domain.CrmWecomExternalContact;

public interface CrmWecomExternalContactMapper
{
    List<CrmWecomExternalContact> selectByExternalUserid(@Param("externalUserid") String externalUserid);

    List<CrmWecomExternalContact> selectByExternalUserids(@Param("externalUserids") List<String> externalUserids);

    CrmWecomExternalContact selectByExternalAndFollow(@Param("externalUserid") String externalUserid, @Param("followUserid") String followUserid);

    CrmWecomExternalContact selectById(@Param("id") Long id);

    List<CrmWecomExternalContact> selectList(CrmWecomExternalContact query);

    int insert(CrmWecomExternalContact row);

    int update(CrmWecomExternalContact row);

    /**
     * 从 KF get_customer_info 响应执行 UPSERT（不创建 Lead，纯数据写入）。
     */
    int upsertKfInfo(CrmWecomExternalContact row);
}
