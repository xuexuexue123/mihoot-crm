package com.ruoyi.flowable.service;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.domain.FlowProcDefDto;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * @author XuanXuan
 * @date 2021-04-03 14:41
 */
public interface IFlowDefinitionService {

    boolean exist(String processDefinitionKey);


    /**
     * 流程定义列表
     *
     * @param pageNum  当前页码
     * @param pageSize 每页条数
     * @return 流程定义分页列表数据
     */
    Page<FlowProcDefDto> list(String name,Integer pageNum, Integer pageSize);

    /**
     * 导入流程文件
     *
     * @param name
     * @param category
     * @param in
     */
    JSONObject importFile(String name, String category, InputStream in, String tenantId);

    /**
     * 读取xml
     * @param deployId
     * @return
     */
    AjaxResult readXml(String deployId) throws IOException;

    /**
     * 根据流程定义ID启动流程实例
     *
     * @param procDefId
     * @param variables
     * @return
     */

    AjaxResult startProcessInstanceByProcDefId(String procDefId, String caseId, Map<String, Object> variables);


    /**
     * @param caseId 业务案件 ID；可为 null/空白，此时流程实例无 businessKey，变量 pyCaseId 为空串
     */
    AjaxResult startProcessInstanceById(String procDefId, String caseId, Map<String, Object> variables);


    /**
     * 激活或挂起流程定义
     *
     * @param state    状态
     * @param deployId 流程部署ID
     */
    void updateState(Integer state, String deployId);


    /**
     * 删除流程定义
     *
     * @param deployId 流程部署ID act_ge_bytearray 表中 deployment_id值
     */
    void delete(String deployId);


    /**
     * 读取图片文件
     * @param deployId
     * @return
     */
    InputStream readImage(String deployId);

    /**
     * 获取部署ID
     * @param name
     * @return
     */
    FlowProcDefDto getDeploymentIdByName(String name);

    FlowProcDefDto getDeploymentIdByKey(String key);
}
