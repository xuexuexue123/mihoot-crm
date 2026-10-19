package com.ruoyi.crm.service;

import java.util.List;
import com.ruoyi.crm.domain.WWorkflow;

/**
 * WWorkflowService接口
 * 
 * @author luohd
 * @date 2026-05-19
 */
public interface IWWorkflowService 
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
     * 仅修改工作流名称（不触及节点与连线）
     *
     * @param wWorkflow 须包含 id、name
     * @return 结果
     */
    public int updateWWorkflowName(WWorkflow wWorkflow);

    /**
     * 仅更新元数据 name / description（不触及节点与连线）
     *
     * @param id 工作流 id
     * @param name 非 blank 时更新名称；null 表示不改
     * @param description 非 null 时更新说明（含空串）；null 表示不改
     * @return 更新后的完整流程
     */
    public WWorkflow updateWWorkflowMeta(String id, String name, String description);

    /**
     * 批量删除WWorkflow
     * 
     * @param ids 需要删除的WWorkflow主键集合
     * @return 结果
     */
    public int deleteWWorkflowByIds(String[] ids);

    /**
     * 删除WWorkflow信息
     * 
     * @param id WWorkflow主键
     * @return 结果
     */
    public int deleteWWorkflowById(String id);

    /**
     * 根据 w_node 重算全表 publish_assistant / publish_sidebar（一次性 backfill）
     *
     * @return 更新的工作流行数
     */
    public int backfillPublishFlags();

    /**
     * 更新模板标记；设为模板时强制清发布与 Webhook publicPath
     *
     * @param id 工作流 id
     * @param isTemplate 是否模板
     * @return 更新后的完整流程
     */
    public WWorkflow updateTemplateFlag(String id, Boolean isTemplate);

    /**
     * 从任意流程深拷贝为新实例（isTemplate=false）
     *
     * @param id 源流程 id
     * @param name 可选新名称
     * @param description 可选覆盖说明；null 表示复制源 description
     * @return 新实例完整 DTO
     */
    public WWorkflow cloneAsInstance(String id, String name, String description);

    /**
     * 从任意流程深拷贝为新模板（isTemplate=true）
     *
     * @param id 源流程 id
     * @param name 可选新名称
     * @param description 可选覆盖说明；null 表示复制源 description
     * @return 新模板完整 DTO
     */
    public WWorkflow saveAsTemplate(String id, String name, String description);
}
