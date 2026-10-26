package com.ruoyi.flowable.listener;

import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.uuid.UUID;
import com.ruoyi.ems.domain.SysAudittrail;
import com.ruoyi.ems.service.ISysAudittrailService;
import com.ruoyi.flowable.service.ICaseService;
import lombok.extern.slf4j.Slf4j;
import org.flowable.common.engine.api.delegate.event.FlowableEngineEntityEvent;
import org.flowable.engine.delegate.event.AbstractFlowableEngineEventListener;
import org.flowable.engine.delegate.event.impl.FlowableEntityEventImpl;
import org.flowable.engine.impl.persistence.entity.ExecutionEntityImpl;
import org.flowable.task.service.impl.persistence.entity.TaskEntityImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Slf4j
@Component
public class GlobalTaskCompletedListener extends AbstractFlowableEngineEventListener {

    @Autowired
    private ICaseService caseService;


    @Autowired
    private ISysAudittrailService sysAudittrailService;

    @Override
    protected void taskCreated(FlowableEngineEntityEvent event) {
        log.info("进入Task开始监听器------------------------Start---------------------->");

        String eventName = event.getType().name();

        TaskEntityImpl processInstance = (TaskEntityImpl) event.getEntity();
        String pyCaseId = processInstance.getVariable("pyCaseId").toString();
//        Date startTime = processInstance.getStartTime();
//        String processDefinitionKey = processInstance.getProcessDefinitionKey();
//        String processInstanceId = processInstance.getProcessInstanceId();
//        String processInstanceBusinessKey = processInstance.get
//        int suspensionState = processInstance.getSuspensionState();

        log.info("流程Definition ID->{}", event.getProcessDefinitionId());
        log.info("流程Instance ID->{}", event.getProcessInstanceId());
        log.info("流程Execution ID->{}", event.getExecutionId());

        log.info("流程事件类型->{}", eventName);

//        SysAudittrail sysAudittrail = new SysAudittrail();
//        sysAudittrail.setId(UUID.randomUUID().toString());
//        sysAudittrail.setCreateTime(DateUtils.getNowDate());
//        sysAudittrail.setProcInstId(event.getProcessInstanceId());
//        sysAudittrail.setPyCaseId(pyCaseId);
//        sysAudittrail.setUserId(processInstance.getAssignee());
//        sysAudittrail.setAction(processInstance.getName());
//        sysAudittrail.setType("TASK");
//        sysAudittrail.setMessage("任务"+processInstance.getName()+"已完成");

//        sysAudittrailService.insertSysAudittrail(sysAudittrail);

        log.info("Task开始监听器------------------------End---------------------->");
    }

    @Override
    protected void taskAssigned(FlowableEngineEntityEvent event) {
        log.info("指派任务监听器------------------------Start---------------------->");

        String eventName = event.getType().name();

        TaskEntityImpl processInstance = (TaskEntityImpl) event.getEntity();
        String pyCaseId = processInstance.getVariable("pyCaseId").toString();

        log.info("流程Definition ID->{}", event.getProcessDefinitionId());
        log.info("流程Instance ID->{}", event.getProcessInstanceId());
        log.info("流程Execution ID->{}", event.getExecutionId());

        log.info("流程事件类型->{}", eventName);


        SysAudittrail sysAudittrail = new SysAudittrail();
        sysAudittrail.setId(UUID.randomUUID().toString());
        sysAudittrail.setCreateTime(DateUtils.getNowDate());
        sysAudittrail.setProcInstId(event.getProcessInstanceId());
        sysAudittrail.setPyCaseId(pyCaseId);
        sysAudittrail.setUserId(processInstance.getAssignee());
        sysAudittrail.setAction(processInstance.getName());
        sysAudittrail.setType("TASK");
        sysAudittrail.setMessage("已指派");

        sysAudittrailService.insertSysAudittrail(sysAudittrail);


        log.info("指派任务监听器------------------------End---------------------->");
    }

    @Override
    protected void taskCompleted(FlowableEngineEntityEvent event) {
        log.info("进入Task完成监听器------------------------Start---------------------->");

        String eventName = event.getType().name();

        TaskEntityImpl processInstance = (TaskEntityImpl) event.getEntity();
        String pyCaseId = processInstance.getVariable("pyCaseId").toString();

        log.info("流程Definition ID->{}", event.getProcessDefinitionId());
        log.info("流程Instance ID->{}", event.getProcessInstanceId());
        log.info("流程Execution ID->{}", event.getExecutionId());

        log.info("流程事件类型->{}", eventName);

        SysAudittrail sysAudittrail = new SysAudittrail();
        sysAudittrail.setId(UUID.randomUUID().toString());
        sysAudittrail.setCreateTime(DateUtils.getNowDate());
        sysAudittrail.setProcInstId(event.getProcessInstanceId());
        sysAudittrail.setPyCaseId(pyCaseId);
        sysAudittrail.setUserId(processInstance.getAssignee());
        sysAudittrail.setAction(processInstance.getName());
        sysAudittrail.setType("TASK");
        sysAudittrail.setMessage("已完成");

        sysAudittrailService.insertSysAudittrail(sysAudittrail);
        //获取所有的当前任务的变量，然后更新caseinstance表

        caseService.updateCaseVariables(event.getProcessInstanceId(), event.getExecutionId());

        log.info("Task完成监听器------------------------End---------------------->");
    }


}
