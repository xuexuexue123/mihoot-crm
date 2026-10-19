package com.ruoyi.crm.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.crm.domain.WConnection;

/**
 * WConnectionMapper接口
 * 
 * @author luohd
 * @date 2026-05-19
 */
public interface WConnectionMapper
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
     * 按工作流ID批量查询连接
     *
     * @param workflowIds 工作流ID集合
     * @return 连接集合
     */
    public List<WConnection> selectWConnectionListByWorkflowIds(@Param("workflowIds") List<String> workflowIds);

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
     * 删除WConnection
     * 
     * @param id WConnection主键
     * @return 结果
     */
    public int deleteWConnectionById(String id);

    /**
     * 批量删除WConnection
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteWConnectionByIds(String[] ids);

    /**
     * 按工作流ID删除连接
     *
     * @param workflowId 工作流ID
     * @return 结果
     */
    public int deleteWConnectionByWorkflowId(String workflowId);
}
