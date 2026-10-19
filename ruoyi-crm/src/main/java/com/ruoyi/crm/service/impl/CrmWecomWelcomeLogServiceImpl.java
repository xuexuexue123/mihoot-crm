package com.ruoyi.crm.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruoyi.crm.domain.CrmWecomWelcomeLog;
import com.ruoyi.crm.mapper.CrmWecomWelcomeLogMapper;
import com.ruoyi.crm.service.ICrmWecomWelcomeLogService;

@Service
public class CrmWecomWelcomeLogServiceImpl implements ICrmWecomWelcomeLogService
{
    @Autowired
    private CrmWecomWelcomeLogMapper crmWecomWelcomeLogMapper;

    @Override
    public List<CrmWecomWelcomeLog> selectList(CrmWecomWelcomeLog query)
    {
        return crmWecomWelcomeLogMapper.selectList(query);
    }
}
