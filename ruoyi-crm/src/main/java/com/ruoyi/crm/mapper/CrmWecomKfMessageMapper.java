package com.ruoyi.crm.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ruoyi.crm.domain.CrmWecomKfMessage;

public interface CrmWecomKfMessageMapper
{
    int insert(CrmWecomKfMessage row);

    CrmWecomKfMessage selectByMsgid(@Param("corpId") String corpId, @Param("msgid") String msgid);

    List<CrmWecomKfMessage> selectByExternalUserid(
            @Param("corpId") String corpId,
            @Param("externalUserid") String externalUserid,
            @Param("openKfid") String openKfid,
            @Param("direction") Integer direction);

    List<CrmWecomKfMessage> selectByConversation(
            @Param("openKfid") String openKfid,
            @Param("externalUserid") String externalUserid);

    /**
     * 通过 crm_contact.wecom_external_userid 关联查询消息。
     */
    List<CrmWecomKfMessage> selectByContactId(
            @Param("corpId") String corpId,
            @Param("contactId") String contactId,
            @Param("openKfid") String openKfid);

    /**
     * 按日期范围分页查询消息记录（可选按客服账号筛选）。
     * send_time 闭区间 [startTime, endTime)，调用方需先执行 PageHelper.startPage()。
     */
    List<CrmWecomKfMessage> selectMessagesByDate(
            @Param("corpId") String corpId,
            @Param("openKfid") String openKfid,
            @Param("startTime") Long startTime,
            @Param("endTime") Long endTime);
}
