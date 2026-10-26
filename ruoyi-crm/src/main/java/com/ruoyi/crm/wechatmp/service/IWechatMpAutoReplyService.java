package com.ruoyi.crm.wechatmp.service;

import java.util.List;

import com.ruoyi.crm.wechatmp.domain.CrmWechatMpAutoReply;

public interface IWechatMpAutoReplyService
{
    List<CrmWechatMpAutoReply> selectList(CrmWechatMpAutoReply query);

    CrmWechatMpAutoReply getById(Long id);

    void add(CrmWechatMpAutoReply row);

    void update(CrmWechatMpAutoReply row);

    void delete(Long id);
}
