package com.ruoyi.flowable.listener;

import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.ExecutionListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MultiInstanceTaskAfterListener implements ExecutionListener {
    @Override
    public void notify(DelegateExecution execution) {

        log.info("进入多实例After监听器------------------------Start---------------------->");

        Integer nrOfCompletedInstances = 0;

        Object instanceCountObj = execution.getVariable("nrOfCompletedInstances");

        if (instanceCountObj != null) {
            nrOfCompletedInstances = Integer.parseInt(instanceCountObj.toString());
            log.info("nrOfCompletedInstances = " + nrOfCompletedInstances);
            nrOfCompletedInstances = nrOfCompletedInstances + 1;
            execution.setVariable("nrOfCompletedInstances", nrOfCompletedInstances);
        } else {
            log.info("nrOfCompletedInstances is null ");
            nrOfCompletedInstances = 1;
            execution.setVariable("nrOfCompletedInstances", nrOfCompletedInstances);

        }

        log.info("variables" + execution.getVariables());

        log.info("进入多实例After监听器------------------------End---------------------->");
    }
}
