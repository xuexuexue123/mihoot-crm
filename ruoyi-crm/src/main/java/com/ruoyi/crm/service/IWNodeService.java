package com.ruoyi.crm.service;

import java.util.List;
import com.ruoyi.crm.domain.WNode;

/**
 * WNodeService接口
 * 
 * @author luohd
 * @date 2026-05-19
 */
public interface IWNodeService
{
    /**
     * 查询WNode
     * 
     * @param id WNode主键
     * @return WNode
     */
    public WNode selectWNodeById(String id);

    /**
     * 查询WNode列表
     * 
     * @param wNode WNode
     * @return WNode集合
     */
    public List<WNode> selectWNodeList(WNode wNode);

    /**
     * 新增WNode
     * 
     * @param wNode WNode
     * @return 结果
     */
    public int insertWNode(WNode wNode);

    /**
     * 修改WNode
     * 
     * @param wNode WNode
     * @return 结果
     */
    public int updateWNode(WNode wNode);

    /**
     * 批量删除WNode
     * 
     * @param ids 需要删除的WNode主键集合
     * @return 结果
     */
    public int deleteWNodeByIds(String[] ids);

    /**
     * 删除WNode信息
     * 
     * @param id WNode主键
     * @return 结果
     */
    public int deleteWNodeById(String id);
}
