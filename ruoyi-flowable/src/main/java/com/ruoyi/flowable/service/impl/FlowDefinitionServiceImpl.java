package com.ruoyi.flowable.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ruoyi.flowable.common.constant.ProcessConstants;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.flowable.common.enums.FlowComment;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.domain.FlowProcDefDto;
import com.ruoyi.flowable.factory.FlowServiceFactory;
import com.ruoyi.flowable.service.IFlowDefinitionService;
import com.ruoyi.common.flowable.spi.FlowVariablePreprocessor;
import com.ruoyi.flowable.service.ISysDeployFormService;
import com.ruoyi.system.domain.SysForm;
import com.ruoyi.system.mapper.FlowDeployMapper;
import com.ruoyi.system.service.ISysDeptService;
import com.ruoyi.system.service.ISysPostService;
import com.ruoyi.system.service.ISysUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.io.IOUtils;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.repository.ProcessDefinitionQuery;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.image.impl.DefaultProcessDiagramGenerator;
import org.flowable.task.api.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * 流程定义
 *
 * @author XuanXuan
 * @date 2021-04-03
 */
@Service
@Slf4j
public class FlowDefinitionServiceImpl extends FlowServiceFactory implements IFlowDefinitionService {

    @Resource
    private ISysDeployFormService sysDeployFormService;

    @Resource
    private ISysUserService sysUserService;

    @Resource
    private ISysDeptService sysDeptService;

    @Resource
    private ISysPostService postService;

    @Resource
    private FlowDeployMapper flowDeployMapper;

    @Autowired
    private FlowServiceFactory flowServiceFactory;

    @Autowired(required = false)
    private List<FlowVariablePreprocessor> flowVariablePreprocessors;

    private static final String BPMN_FILE_SUFFIX = ".bpmn";

    private void applyFlowVariablePreprocessors(Map<String, Object> variables)
    {
        if (variables == null || flowVariablePreprocessors == null)
        {
            return;
        }
        for (FlowVariablePreprocessor preprocessor : flowVariablePreprocessors)
        {
            preprocessor.beforeProcessStart(variables);
        }
    }

    @Override
    public boolean exist(String processDefinitionKey) {
        ProcessDefinitionQuery processDefinitionQuery
                = flowServiceFactory.getRepositoryService().createProcessDefinitionQuery().processDefinitionKey(processDefinitionKey);
        long count = processDefinitionQuery.count();
        return count > 0 ? true : false;
    }


    /**
     * 流程定义列表
     *
     * @param pageNum  当前页码
     * @param pageSize 每页条数
     * @return 流程定义分页列表数据
     */
    @Override
    public Page<FlowProcDefDto> list(String name, Integer pageNum, Integer pageSize) {
        Page<FlowProcDefDto> page = new Page<>();
//        // 流程定义列表数据查询
//        final ProcessDefinitionQuery processDefinitionQuery = flowServiceFactory.getRepositoryService().createProcessDefinitionQuery();
//        if (StringUtils.isNotEmpty(name)) {
//            processDefinitionQuery.processDefinitionNameLike(name);
//        }
////        processDefinitionQuery.orderByProcessDefinitionKey().asc();
//        page.setTotal(processDefinitionQuery.count());
//        List<ProcessDefinition> processDefinitionList = processDefinitionQuery.listPage(pageSize * (pageNum - 1), pageSize);
//
//        List<FlowProcDefDto> dataList = new ArrayList<>();
//        for (ProcessDefinition processDefinition : processDefinitionList) {
//            String deploymentId = processDefinition.getDeploymentId();
//            Deployment deployment = flowServiceFactory.getRepositoryService().createDeploymentQuery().deploymentId(deploymentId).singleResult();
//            FlowProcDefDto reProcDef = new FlowProcDefDto();
//            BeanUtils.copyProperties(processDefinition, reProcDef);
//            SysForm sysForm = sysDeployFormService.selectSysDeployFormByDeployId(deploymentId);
//            if (Objects.nonNull(sysForm)) {
//                reProcDef.setFormName(sysForm.getFormName());
//                reProcDef.setFormId(sysForm.getFormId());
//            }
//            // 流程定义时间
//            reProcDef.setDeploymentTime(deployment.getDeploymentTime());
//            dataList.add(reProcDef);
//        }
        PageHelper.startPage(pageNum, pageSize);
        final List<FlowProcDefDto> dataList = flowDeployMapper.selectDeployList(name);
        // 加载挂表单
        for (FlowProcDefDto procDef : dataList) {
            SysForm sysForm = sysDeployFormService.selectSysDeployFormByDeployId(procDef.getDeploymentId());
            if (Objects.nonNull(sysForm)) {
                procDef.setFormName(sysForm.getFormName());
                procDef.setFormId(sysForm.getFormId());
            }
        }
        page.setTotal(new PageInfo(dataList).getTotal());
        page.setRecords(dataList);
        return page;
    }


    /**
     * 导入流程文件
     *  @param name
     * @param category
     * @param in
     * @return
     */
    @Override
    public JSONObject importFile(String name, String category, InputStream in, String tenantId) {
        Deployment deploy = flowServiceFactory.getRepositoryService().createDeployment().addInputStream(name + BPMN_FILE_SUFFIX, in)
                .name(name)
                .category(category)
                .tenantId(tenantId) // 多租户关键
                .deploy();
        ProcessDefinition definition = flowServiceFactory.getRepositoryService().createProcessDefinitionQuery().deploymentId(deploy.getId()).singleResult();
//        log.info("definition.getId() = " + definition.getId());
//        log.info("deploy.getId() = " + deploy.getId());
//        log.info("deploy.getKey() = " + definition.getKey());
//        log.info("deploy.getName() = " + deploy.getName());
        flowServiceFactory.getRepositoryService().setProcessDefinitionCategory(definition.getId(), category);

        JSONObject json = new JSONObject();

        json.put("deploymentName", deploy.getName());
        json.put("deploymentKey", definition.getKey());
        json.put("deploymentId", deploy.getId());
        json.put("procDefId", definition.getId());

        return json;
    }

    /**
     * 读取xml
     *
     * @param deployId
     * @return
     */
    @Override
    public AjaxResult readXml(String deployId) throws IOException {
        ProcessDefinition definition = flowServiceFactory.getRepositoryService().createProcessDefinitionQuery().deploymentId(deployId).singleResult();
        InputStream inputStream = flowServiceFactory.getRepositoryService().getResourceAsStream(definition.getDeploymentId(), definition.getResourceName());
        String result = IOUtils.toString(inputStream, StandardCharsets.UTF_8.name());
        return AjaxResult.success("", result);
    }

    /**
     * 读取xml
     *
     * @param deployId
     * @return
     */
    @Override
    public InputStream readImage(String deployId) {
        ProcessDefinition processDefinition = flowServiceFactory.getRepositoryService().createProcessDefinitionQuery().deploymentId(deployId).singleResult();
        //获得图片流
        DefaultProcessDiagramGenerator diagramGenerator = new DefaultProcessDiagramGenerator();
        BpmnModel bpmnModel = flowServiceFactory.getRepositoryService().getBpmnModel(processDefinition.getId());
        //输出为图片
        return diagramGenerator.generateDiagram(
                bpmnModel,
                "png",
                Collections.emptyList(),
                Collections.emptyList(),
                "宋体",
                "宋体",
                "宋体",
                null,
                1.0,
                false);
    }

    @Override
    public FlowProcDefDto getDeploymentIdByName(String name) {

        final List<FlowProcDefDto> dataList = flowDeployMapper.selectDeploymentIdByName(name);
        FlowProcDefDto procDefResult = null;
        int i = 1;
        for (FlowProcDefDto procDef : dataList) {
            if (i > 1) {
                break;
            }
            procDefResult = procDef;

            i = i + 1;
        }
        return procDefResult;
    }
    @Override
    public FlowProcDefDto getDeploymentIdByKey(String key) {

        final List<FlowProcDefDto> dataList = flowDeployMapper.selectDeploymentIdByKey(key);

        FlowProcDefDto procDefResult = null;
        int i = 1;
        for (FlowProcDefDto procDef : dataList) {
            if (i > 1) {
                break;
            }
            procDefResult = procDef;

            i = i + 1;
        }
        return procDefResult;
    }
    /**
     * 根据流程定义ID启动流程实例
     *
     * @param procDefId 流程定义Id
     * @param variables 流程变量
     * @return
     */
    @Override
    public AjaxResult startProcessInstanceByProcDefId(String procDefId, String caseId, Map<String, Object> variables) {
        try {
            ProcessDefinition processDefinition = flowServiceFactory.getRepositoryService().createProcessDefinitionQuery().processDefinitionId(procDefId)
                    .latestVersion().singleResult();
            if (Objects.nonNull(processDefinition) && processDefinition.isSuspended()) {
                return AjaxResult.error("Process suspended. ");
            }

            applyFlowVariablePreprocessors(variables);
            variables.put("approved", true);
            ProcessInstance processInstance = flowServiceFactory.getRuntimeService().startProcessInstanceById(procDefId, variables);

            return AjaxResult.success("Process started.");
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.error("Failed to start process");
        }
    }
    /**
     * 根据流程定义ID启动流程实例
     *
     * @param procDefId 流程定义Id
     * @param variables 流程变量
     * @return
     */
    @Override
    public AjaxResult startProcessInstanceById(String procDefId, String caseId, Map<String, Object> variables) {
        try {
            if (variables == null) {
                variables = new HashMap<>();
            }
            ProcessDefinition processDefinition = flowServiceFactory.getRepositoryService().createProcessDefinitionQuery().processDefinitionId(procDefId)
                    .latestVersion().singleResult();
            if (Objects.nonNull(processDefinition) && processDefinition.isSuspended()) {
                return AjaxResult.error("流程已被挂起,请先激活流程");
            }
            String businessKey = StringUtils.trimToNull(caseId);
            log.info("caseId (business key) = {}", businessKey);
//           variables.put("skip", true);
//           variables.put(ProcessConstants.FLOWABLE_SKIP_EXPRESSION_ENABLED, true);
            // 设置流程发起人Id到流程中
            SysUser sysUser = SecurityUtils.getLoginUser().getUser();
            flowServiceFactory.getIdentityService().setAuthenticatedUserId(sysUser.getUserId().toString());
//            variables.put(ProcessConstants.PROCESS_INITIATOR, sysUser.getUserName());
            variables.put(ProcessConstants.PROCESS_INITIATOR, sysUser.getUserId());
            variables.put("pyCaseId", businessKey != null ? businessKey : "");
            variables.put("approved", true);

            applyFlowVariablePreprocessors(variables);
            ProcessInstance processInstance = businessKey != null
                    ? flowServiceFactory.getRuntimeService().startProcessInstanceById(procDefId, businessKey, variables)
                    : flowServiceFactory.getRuntimeService().startProcessInstanceById(procDefId, variables);

            // 给第一步申请人节点设置任务执行人和意见 todo:第一个节点不设置为申请人节点有点问题？
            Task task = flowServiceFactory.getTaskService().createTaskQuery().processInstanceId(processInstance.getProcessInstanceId()).singleResult();
            if (Objects.nonNull(task)) {
                flowServiceFactory.getTaskService().addComment(task.getId(), processInstance.getProcessInstanceId(), FlowComment.NORMAL.getType(), "启动流程");
//                flowServiceFactory.getTaskService().setAssignee(task.getId(), sysUser.getUserId().toString());
//                flowServiceFactory.getTaskService().complete(task.getId(), variables);
            }
            return AjaxResult.success("流程启动成功");
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.error("流程启动错误");
        }
    }


    /**
     * 激活或挂起流程定义
     *
     * @param state    状态
     * @param deployId 流程部署ID
     */
    @Override
    public void updateState(Integer state, String deployId) {
        ProcessDefinition procDef = flowServiceFactory.getRepositoryService().createProcessDefinitionQuery().deploymentId(deployId).singleResult();
        // 激活
        if (state == 1) {
            flowServiceFactory.getRepositoryService().activateProcessDefinitionById(procDef.getId(), true, null);
        }
        // 挂起
        if (state == 2) {
            flowServiceFactory.getRepositoryService().suspendProcessDefinitionById(procDef.getId(), true, null);
        }
    }


    /**
     * 删除流程定义
     *
     * @param deployId 流程部署ID act_ge_bytearray 表中 deployment_id值
     */
    @Override
    public void delete(String deployId) {
        // true 允许级联删除 ,不设置会导致数据库外键关联异常

        flowServiceFactory.getRepositoryService().deleteDeployment(deployId, true);
    }


}
