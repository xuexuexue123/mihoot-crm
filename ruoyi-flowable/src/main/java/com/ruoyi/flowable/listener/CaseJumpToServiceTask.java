package com.ruoyi.flowable.listener;

import com.ruoyi.flowable.factory.BeanUtils;
import com.ruoyi.flowable.service.ICaseService;
import com.ruoyi.flowable.service.impl.CaseServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.flowable.common.engine.api.delegate.Expression;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class CaseJumpToServiceTask implements JavaDelegate {
    Expression jumpTo;

//    @Autowired
//    private ICaseService caseService;

    @Override
    public void execute(DelegateExecution execution) {

        log.info("#########################这里是跳转ServiceTask############################");

        log.info("jumpTo.getExpressionText() = " + jumpTo.getExpressionText());
        log.info("jumpTo.getValue(execution) = " + jumpTo.getValue(execution));

        log.info("business key = " + execution.getProcessInstanceBusinessKey());
        log.info("super execution id = " + execution.getSuperExecutionId());

//        Map<String, Object> variables = execution.getVariables();
        String pyCaseId = execution.getProcessInstanceBusinessKey();

//        for(Map.Entry<String, Object> entry : variables.entrySet()){
//
//            String varKey = entry.getKey();
//            if (varKey.equalsIgnoreCase("pyCaseId")) {
//                pyCaseId = entry.getValue().toString();
//
//                break;
//            }
//        }
        log.info("pyCaseId = " + pyCaseId);
        execution.setVariable("jumpTo", jumpTo.getValue(execution).toString());

//        CaseServiceImpl caseService = BeanUtils.getBean(CaseServiceImpl.class);

//        caseService.moveToAssignedStage(pyCaseId, String.valueOf(jumpTo.getValue(execution)), execution.getSuperExecutionId());
//        caseService.moveToAssignedStage(pyCaseId, jumpTo.getValue(execution).toString());
        log.info("#########################这里是跳转ServiceTask############################");
    }
}
