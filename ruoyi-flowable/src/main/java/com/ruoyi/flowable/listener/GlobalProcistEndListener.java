package com.ruoyi.flowable.listener;

import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.uuid.UUID;
import com.ruoyi.ems.domain.SysAudittrail;
import com.ruoyi.ems.domain.SysProcess;
import com.ruoyi.ems.domain.SysReActitem;
import com.ruoyi.ems.domain.SysReActivity;
import com.ruoyi.ems.service.*;
import com.ruoyi.flowable.service.ICaseService;
import com.ruoyi.flowable.service.impl.CaseServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.flowable.common.engine.api.delegate.event.FlowableEngineEntityEvent;
import org.flowable.engine.delegate.event.AbstractFlowableEngineEventListener;
import org.flowable.engine.delegate.event.impl.FlowableEntityEventImpl;
import org.flowable.engine.impl.persistence.entity.ExecutionEntityImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Slf4j
@Component
public class GlobalProcistEndListener extends AbstractFlowableEngineEventListener {
//    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ICaseService caseService;

    @Autowired
    private ISysAudittrailService sysAudittrailService;

    @Autowired
    private ISysProcessService sysProcessService;


    @Override
    protected void processCompleted(FlowableEngineEntityEvent event) {
        log.info("进入流程完成监听器------------------------Start---------------------->");
        String eventName = event.getType().name();

        FlowableEntityEventImpl flowableEntityEvent = (FlowableEntityEventImpl) event;
        ExecutionEntityImpl processInstance = (ExecutionEntityImpl) flowableEntityEvent.getEntity();

        Date startTime = processInstance.getStartTime();
        String processDefinitionKey = processInstance.getProcessDefinitionKey();
        String procDefId = processInstance.getProcessDefinitionId();
        String processInstanceId = processInstance.getProcessInstanceId();

        String processInstanceBusinessKey = processInstance.getProcessInstanceBusinessKey();
        String processDefinitionId = processInstance.getProcessDefinitionId();
        int suspensionState = processInstance.getSuspensionState();


        log.info("流程事件类型->{}", eventName);
        log.info("流程开始时间->{}", startTime);
        log.info("流程定义Id->{}", procDefId);
        log.info("流程定义Key->{}", processDefinitionKey);
        log.info("流程实例ID->{}", processInstanceId);
        log.info("流程业务key->{}", processInstanceBusinessKey);
        log.info("流程是否挂起标志->{}", suspensionState);

        //todo: 检查是否当前的stage下还有别的流程，如果有，就应该启动
//
        Object jumpTo = processInstance.getVariable("jumpTo");
        log.info("jumpTo = " + jumpTo);
//
//
//        if (jumpTo != null && !String.valueOf(jumpTo).equalsIgnoreCase("nextstage")) {
//            processInstance.setVariable("jumpTo", "nextstage");
//            caseService.moveToAssignedStage(processInstanceBusinessKey, String.valueOf(jumpTo), event.getExecutionId());
//        }

        SysProcess flow = sysProcessService.selectSysProcessByProcDefId(processDefinitionId);

        String processName = processDefinitionKey;

        if (flow!=null) {
            processName = flow.getProcessLabel();
            //todo：运行after process activity
            log.info("activity = " + flow.getAfterActivity());
            if (flow.getAfterActivity() != null && flow.getAfterActivity() != "") {

                //获取activity明细
//                SysReActitem sysReActitem = new SysReActitem();
//                sysReActitem.setActId(Long.parseLong(flow.getAfterActivity()));
//
//                List<SysReActitem> sysReActitemList = sysReActitemService.selectSysReActitemList(sysReActitem);
//                log.info("sysReActitemList = " + sysReActitemList.size());


//                Long pyId = Long.parseLong(flow.getAfterActivity());
                String pyId = flow.getAfterActivity();

                caseService.executeProcessActivity(pyId, event.getExecutionId());


            }
        }



        //todo: 发送通知到下一个stage
        //如果这不是一个跳转，那么就去找下一个启动的流程
        //更新当前流程的case状态
        boolean caseCompleted = caseService.updateProcessStatus(processInstanceBusinessKey, procDefId);

//        SysAudittrail sysAudittrail = new SysAudittrail();
//        sysAudittrail.setId(UUID.randomUUID().toString());
//        sysAudittrail.setCreateTime(DateUtils.getNowDate());
//        sysAudittrail.setProcInstId(processInstanceId);
//        sysAudittrail.setPyCaseId(processInstanceBusinessKey);
//        sysAudittrail.setType("PROCESS");
//        sysAudittrail.setAction(processName);
//        sysAudittrail.setMessage("流程已完成");
//
//        sysAudittrailService.insertSysAudittrail(sysAudittrail);

        if (caseCompleted) {

            SysAudittrail sysAudittrailCompleted = new SysAudittrail();
            sysAudittrailCompleted.setId(UUID.randomUUID().toString());
            sysAudittrailCompleted.setCreateTime(DateUtils.getNowDate());
            sysAudittrailCompleted.setProcInstId(processInstanceId);
            sysAudittrailCompleted.setPyCaseId(processInstanceBusinessKey);
            sysAudittrailCompleted.setAction("CASE_COMPLETED");
            sysAudittrailCompleted.setType("CASE");
            sysAudittrailCompleted.setMessage("案件已完成");

            sysAudittrailService.insertSysAudittrail(sysAudittrailCompleted);
        } else {

            caseService.postActionAfterProcessCompleted(processInstanceBusinessKey, event.getExecutionId());
        }


        log.info("流程完成监听器------------------------End---------------------->");
    }
}
