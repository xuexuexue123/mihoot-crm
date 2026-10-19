package com.ruoyi.crm.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.crm.mapper.WConnectionMapper;
import com.ruoyi.crm.domain.WConnection;
import com.ruoyi.crm.service.IWConnectionService;

/**
 * WConnectionService业务层处理
 * 
 * @author luohd
 * @date 2026-05-19
 */
@Service
public class WConnectionServiceImpl implements IWConnectionService
{
    @Autowired
    private WConnectionMapper wConnectionMapper;

    /**
     * 查询WConnection
     * 
     * @param id WConnection主键
     * @return WConnection
     */
    @Override
    public WConnection selectWConnectionById(String id)
    {
        return wConnectionMapper.selectWConnectionById(id);
    }

    /**
     * 查询WConnection列表
     * 
     * @param wConnection WConnection
     * @return WConnection
     */
    @Override
    public List<WConnection> selectWConnectionList(WConnection wConnection)
    {
        return wConnectionMapper.selectWConnectionList(wConnection);
    }

    /**
     * 新增WConnection
     * 
     * @param wConnection WConnection
     * @return 结果
     */
    @Override
    public int insertWConnection(WConnection wConnection)
    {
        return wConnectionMapper.insertWConnection(wConnection);
    }

    /**
     * 修改WConnection
     * 
     * @param wConnection WConnection
     * @return 结果
     */
    @Override
    public int updateWConnection(WConnection wConnection)
    {
        return wConnectionMapper.updateWConnection(wConnection);
    }

    /**
     * 批量删除WConnection
     * 
     * @param ids 需要删除的WConnection主键
     * @return 结果
     */
    @Override
    public int deleteWConnectionByIds(String[] ids)
    {
        return wConnectionMapper.deleteWConnectionByIds(ids);
    }

    /**
     * 删除WConnection信息
     * 
     * @param id WConnection主键
     * @return 结果
     */
    @Override
    public int deleteWConnectionById(String id)
    {
        return wConnectionMapper.deleteWConnectionById(id);
    }
}
