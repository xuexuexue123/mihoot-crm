package com.ruoyi.crm.service;

import java.util.List;
import com.ruoyi.crm.domain.WConnection;

/**
 * WConnectionService接口
 * 
 * @author luohd
 * @date 2026-05-19
 */
public interface IWConnectionService
{
    /**
     * 查询WConnection
     * 
     * @param id WConnection主键
     * @return WConnection
     */
    public WConnection selectWConnectionById(String id);

    /**
     * 查询WConnection列表
     * 
     * @param wConnection WConnection
     * @return WConnection集合
     */
    public List<WConnection> selectWConnectionList(WConnection wConnection);

    /**
     * 新增WConnection
     * 
     * @param wConnection WConnection
     * @return 结果
     */
    public int insertWConnection(WConnection wConnection);

    /**
     * 修改WConnection
     * 
     * @param wConnection WConnection
     * @return 结果
     */
    public int updateWConnection(WConnection wConnection);

    /**
     * 批量删除WConnection
     * 
     * @param ids 需要删除的WConnection主键集合
     * @return 结果
     */
    public int deleteWConnectionByIds(String[] ids);

    /**
     * 删除WConnection信息
     * 
     * @param id WConnection主键
     * @return 结果
     */
    public int deleteWConnectionById(String id);
}
