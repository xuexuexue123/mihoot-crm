package com.ruoyi.crm.service.impl;

import com.ruoyi.crm.domain.CrmSubjectMap;
import com.ruoyi.crm.mapper.CrmSubjectMapMapper;
import com.ruoyi.crm.service.ICrmSubjectMapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * Subject 继承映射服务实现
 *
 * @author luohd
 * @date 2026-06-10
 */
@Service
public class CrmSubjectMapServiceImpl implements ICrmSubjectMapService
{
    @Autowired
    private CrmSubjectMapMapper crmSubjectMapMapper;

    @Override
    public CrmSubjectMap upsert(CrmSubjectMap map)
    {
        map.setCreateTime(new Date());
        crmSubjectMapMapper.upsert(map);
        return map;
    }

    @Override
    public CrmSubjectMap findByChild(String childSubjectType, String childSubjectId)
    {
        return crmSubjectMapMapper.selectByChild(childSubjectType, childSubjectId);
    }

    @Override
    public List<CrmSubjectMap> findByParent(String parentSubjectType, String parentSubjectId)
    {
        return crmSubjectMapMapper.selectByParent(parentSubjectType, parentSubjectId);
    }
}
