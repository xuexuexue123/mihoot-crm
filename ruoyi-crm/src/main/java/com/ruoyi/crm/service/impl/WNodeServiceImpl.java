package com.ruoyi.crm.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.crm.mapper.WNodeMapper;
import com.ruoyi.crm.domain.WNode;
import com.ruoyi.crm.service.IWNodeService;

/**
 * WNodeService业务层处理
 * 
 * @author luohd
 * @date 2026-05-19
 */
@Service
public class WNodeServiceImpl implements IWNodeService
{
    @Autowired
    private WNodeMapper wNodeMapper;

    /**
     * 查询WNode
     * 
     * @param id WNode主键
     * @return WNode
     */
    @Override
    public WNode selectWNodeById(String id)
    {
        return wNodeMapper.selectWNodeById(id);
    }

    /**
     * 查询WNode列表
     * 
     * @param wNode WNode
     * @return WNode
     */
    @Override
    public List<WNode> selectWNodeList(WNode wNode)
    {
        return wNodeMapper.selectWNodeList(wNode);
    }

    /**
     * 新增WNode
     * 
     * @param wNode WNode
     * @return 结果
     */
    @Override
    public int insertWNode(WNode wNode)
    {
        return wNodeMapper.insertWNode(wNode);
    }

    /**
     * 修改WNode
     * 
     * @param wNode WNode
     * @return 结果
     */
    @Override
    public int updateWNode(WNode wNode)
    {
        return wNodeMapper.updateWNode(wNode);
    }

    /**
     * 批量删除WNode
     * 
     * @param ids 需要删除的WNode主键
     * @return 结果
     */
    @Override
    public int deleteWNodeByIds(String[] ids)
    {
        return wNodeMapper.deleteWNodeByIds(ids);
    }

    /**
     * 删除WNode信息
     * 
     * @param id WNode主键
     * @return 结果
     */
    @Override
    public int deleteWNodeById(String id)
    {
        return wNodeMapper.deleteWNodeById(id);
    }
}
