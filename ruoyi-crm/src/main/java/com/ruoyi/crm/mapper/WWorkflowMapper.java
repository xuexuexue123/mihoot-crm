package com.ruoyi.crm.mapper;

import java.util.List;
import com.ruoyi.crm.domain.WWorkflow;

/**
 * WWorkflowMapper接口
 * 
 * @author luohd
 * @date 2026-05-19
 */
public interface WWorkflowMapper 
{
    /**
     * 查询WWorkflow
     * 
     * @param id WWorkflow主键
     * @return WWorkflow
     */
    public WWorkflow selectWWorkflowById(String id);

    /**
     * 查询WWorkflow列表
     * 
     * @param wWorkflow WWorkflow
     * @return WWorkflow集合
     */
    public List<WWorkflow> selectWWorkflowList(WWorkflow wWorkflow);

    /**
     * 新增WWorkflow
     * 
     * @param wWorkflow WWorkflow
     * @return 结果
     */
    public int insertWWorkflow(WWorkflow wWorkflow);

    /**
     * 修改WWorkflow
     * 
     * @param wWorkflow WWorkflow
     * @return 结果
     */
    public int updateWWorkflow(WWorkflow wWorkflow);

    /**
     * 仅修改工作流名称
     *
     * @param wWorkflow 须包含 id、name
     * @return 结果
     */
    public int updateWWorkflowName(WWorkflow wWorkflow);

    /**
     * 仅更新元数据 name / description（不碰节点与连线）
     *
     * @param wWorkflow 须包含 id；name 非空时更新；description 非 null 时更新（含空串）
     * @return 结果
     */
    public int updateWWorkflowMeta(WWorkflow wWorkflow);

    /**
     * 仅更新双渠道发布派生标志
     *
     * @param wWorkflow 须包含 id、publishAssistant、publishSidebar
     * @return 结果
     */
    public int updateWWorkflowPublishFlags(WWorkflow wWorkflow);

    /**
     * 更新模板标记，并可同时强制清零发布派生列
     *
     * @param wWorkflow 须包含 id、isTemplate、publishAssistant、publishSidebar
     * @return 结果
     */
    public int updateWWorkflowTemplateFlag(WWorkflow wWorkflow);

    /**
     * 删除WWorkflow
     * 
     * @param id WWorkflow主键
     * @return 结果
     */
    public int deleteWWorkflowById(String id);

    /**
     * 批量删除WWorkflow
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteWWorkflowByIds(String[] ids);
}
