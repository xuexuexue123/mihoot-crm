package com.ruoyi.flowable.listener;

import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.uuid.UUID;
import com.ruoyi.ems.domain.SysAudittrail;
import com.ruoyi.ems.domain.SysProcess;
import com.ruoyi.ems.service.ISysAudittrailService;
import com.ruoyi.ems.service.ISysProcessService;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.event.AbstractFlowableEngineEventListener;
import org.flowable.engine.delegate.event.FlowableProcessStartedEvent;
import org.flowable.engine.delegate.event.impl.FlowableEntityEventImpl;
import org.flowable.engine.impl.persistence.entity.ExecutionEntityImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Slf4j
@Component
public class GlobalProcessStartedListener extends AbstractFlowableEngineEventListener {
//    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ISysAudittrailService sysAudittrailService;

    @Autowired
    private ISysProcessService sysProcessService;

    @Override
    protected void processStarted(FlowableProcessStartedEvent event) {
        log.info("进入流程开始监听器------------------------Start---------------------->");

        String eventName = event.getType().name();

        FlowableEntityEventImpl flowableEntityEvent = (FlowableEntityEventImpl) event;
        ExecutionEntityImpl processInstance = (ExecutionEntityImpl) flowableEntityEvent.getEntity();

        Date startTime = processInstance.getStartTime();
        String processDefinitionKey = processInstance.getProcessDefinitionKey();
        String processDefinitionId = processInstance.getProcessDefinitionId();
        String processInstanceId = processInstance.getProcessInstanceId();
        String processInstanceBusinessKey = processInstance.getProcessInstanceBusinessKey();
        int suspensionState = processInstance.getSuspensionState();

        log.info("流程事件类型->{}", eventName);
        log.info("流程开始时间->{}", startTime);
        log.info("流程定义Key->{}", processDefinitionKey);
        log.info("流程实例ID->{}", processInstanceId);
        log.info("流程业务key->{}", processInstanceBusinessKey);
        log.info("流程是否挂起标志->{}", suspensionState);

        SysProcess flow = sysProcessService.selectSysProcessByProcDefId(processDefinitionId);

        String processName = processDefinitionKey;

        if (flow!=null) {
            processName = flow.getProcessLabel();
        }

        SysAudittrail sysAudittrail = new SysAudittrail();
        sysAudittrail.setId(UUID.randomUUID().toString());
        sysAudittrail.setCreateTime(DateUtils.getNowDate());
        sysAudittrail.setProcInstId(processInstanceId);
        sysAudittrail.setPyCaseId(processInstanceBusinessKey);
        sysAudittrail.setType("PROCESS");
        sysAudittrail.setAction(processName);
        sysAudittrail.setMessage("已启动");

        sysAudittrailService.insertSysAudittrail(sysAudittrail);

        log.info("流程开始监听器------------------------End---------------------->");

    }
}
