package com.ruoyi.crm.service;

import java.util.List;

import com.alibaba.fastjson2.JSONObject;

import com.ruoyi.crm.domain.CrmWecomContactWay;
import com.ruoyi.crm.domain.dto.wecom.WecomContactWayCreateRequest;
import com.ruoyi.crm.domain.dto.wecom.WecomContactWayUpdateRequest;

public interface ICrmWecomContactWayService
{
    JSONObject createContactWay(WecomContactWayCreateRequest req);

    List<CrmWecomContactWay> selectList(CrmWecomContactWay query);

    CrmWecomContactWay selectById(Long id);

    CrmWecomContactWay updateContactWay(Long id, WecomContactWayUpdateRequest req);

    void deleteContactWay(Long id);
}
