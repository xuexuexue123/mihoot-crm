package com.ruoyi.crm.wechatmp.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.crm.wechatmp.domain.CrmWechatMpAutoReply;
import com.ruoyi.crm.wechatmp.mapper.CrmWechatMpAutoReplyMapper;
import com.ruoyi.crm.wechatmp.service.IWechatMpAutoReplyService;

@Service
public class WechatMpAutoReplyServiceImpl implements IWechatMpAutoReplyService
{
    @Autowired
    private CrmWechatMpAutoReplyMapper autoReplyMapper;

    @Override
    public List<CrmWechatMpAutoReply> selectList(CrmWechatMpAutoReply query)
    {
        return autoReplyMapper.selectList(query);
    }

    @Override
    public CrmWechatMpAutoReply getById(Long id)
    {
        CrmWechatMpAutoReply row = autoReplyMapper.selectById(id);
        if (row == null)
        {
            throw new ServiceException("自动回复规则不存在");
        }
        return row;
    }

    @Override
    public void add(CrmWechatMpAutoReply row)
    {
        row.setCreateBy(SecurityUtils.getUsername());
        row.setCreateTime(DateUtils.getNowDate());
        row.setUpdateTime(DateUtils.getNowDate());
        if (row.getEnabled() == null)
        {
            row.setEnabled(true);
        }
        if (row.getPriority() == null)
        {
            row.setPriority(0);
        }
        autoReplyMapper.insert(row);
    }

    @Override
    public void update(CrmWechatMpAutoReply row)
    {
        row.setUpdateTime(DateUtils.getNowDate());
        autoReplyMapper.update(row);
    }

    @Override
    public void delete(Long id)
    {
        autoReplyMapper.deleteById(id);
    }
}
