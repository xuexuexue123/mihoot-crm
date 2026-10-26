package com.ruoyi.crm.service;

import java.util.List;

import com.ruoyi.crm.domain.CrmWecomKfMessage;

/**
 * 微信客服消息持久化服务。
 */
public interface IWecomKfMessageService
{
    /**
     * 幂等插入 incoming 消息。
     *
     * @return true 表示新消息已插入，false 表示 msgid 重复已忽略或插入失败
     */
    boolean insertMessage(CrmWecomKfMessage record);

    /**
     * 写入 outgoing 回复消息（不对 msgid 做唯一约束）。
     */
    void insertReply(CrmWecomKfMessage record);

    /**
     * 按 external_userid 分页查询消息列表。
     * 调用方需先执行 PageHelper.startPage()。
     */
    List<CrmWecomKfMessage> selectMessages(String corpId, String externalUserid, String openKfid, Integer direction);

    /**
     * 按 msgid 查询单条消息。
     */
    CrmWecomKfMessage selectByMsgid(String corpId, String msgid);

    /**
     * 通过 crm_contact.contact_id 关联查询消息。
     * 调用方需先执行 PageHelper.startPage()。
     */
    List<CrmWecomKfMessage> selectByContactId(String corpId, String contactId, String openKfid);

    /**
     * 按日期范围分页查询消息记录（可选按客服账号筛选）。
     * send_time 闭区间 [startTime, endTime)，调用方需先执行 PageHelper.startPage()。
     */
    List<CrmWecomKfMessage> selectMessagesByDate(
            String corpId, String openKfid, Long startTime, Long endTime);
}
