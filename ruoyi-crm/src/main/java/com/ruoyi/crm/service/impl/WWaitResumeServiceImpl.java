package com.ruoyi.crm.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.crm.mapper.WWaitResumeMapper;
import com.ruoyi.crm.domain.WWaitResume;
import com.ruoyi.crm.service.IWWaitResumeService;

/**
 * WWaitResumeService业务层处理
 * 
 * @author luohd
 * @date 2026-05-19
 */
@Service
public class WWaitResumeServiceImpl implements IWWaitResumeService
{
    @Autowired
    private WWaitResumeMapper wWaitResumeMapper;

    @Override
    public WWaitResume selectWWaitResumeById(String id)
    {
        return wWaitResumeMapper.selectWWaitResumeById(id);
    }

    @Override
    public List<WWaitResume> selectWWaitResumeList(WWaitResume wWaitResume)
    {
        return wWaitResumeMapper.selectWWaitResumeList(wWaitResume);
    }

    @Override
    public int insertWWaitResume(WWaitResume wWaitResume)
    {
        return wWaitResumeMapper.insertWWaitResume(wWaitResume);
    }

    @Override
    public int updateWWaitResume(WWaitResume wWaitResume)
    {
        return wWaitResumeMapper.updateWWaitResume(wWaitResume);
    }

    @Override
    public int deleteWWaitResumeByIds(String[] ids)
    {
        return wWaitResumeMapper.deleteWWaitResumeByIds(ids);
    }

    @Override
    public int deleteWWaitResumeById(String id)
    {
        return wWaitResumeMapper.deleteWWaitResumeById(id);
    }
}
