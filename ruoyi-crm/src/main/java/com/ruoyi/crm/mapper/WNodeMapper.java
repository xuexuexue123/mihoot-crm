package com.ruoyi.crm.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.crm.domain.WNode;

/**
 * WNodeMapper接口
 * 
 * @author luohd
 * @date 2026-05-19
 */
public interface WNodeMapper
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
     * 按工作流ID批量查询节点
     *
     * @param workflowIds 工作流ID集合
     * @return 节点集合
     */
    public List<WNode> selectWNodeListByWorkflowIds(@Param("workflowIds") List<String> workflowIds);

    /**
     * 按 CRM_RECORD_TRIGGER 配置匹配节点（tenant 库 w_node.data jsonb 过滤）
     */
    List<WNode> selectRecordTriggerMatchNodes(
            @Param("entityType") String entityType,
            @Param("eventTypes") List<String> eventTypes);

    /**
     * 按 CRM_EVENT_TRIGGER 配置匹配节点（tenant 库 w_node.data.eventType 精确匹配）
     */
    List<WNode> selectEventTriggerMatchNodes(@Param("eventType") String eventType);

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
     * 删除WNode
     * 
     * @param id WNode主键
     * @return 结果
     */
    public int deleteWNodeById(String id);

    /**
     * 批量删除WNode
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteWNodeByIds(String[] ids);

    /**
     * 按工作流ID删除节点
     *
     * @param workflowId 工作流ID
     * @return 结果
     */
    public int deleteWNodeByWorkflowId(String workflowId);
}
