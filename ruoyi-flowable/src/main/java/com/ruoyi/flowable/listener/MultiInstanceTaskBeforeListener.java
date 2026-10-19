package com.ruoyi.flowable.listener;

import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.ExecutionListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MultiInstanceTaskBeforeListener implements ExecutionListener {
    @Override
    public void notify(DelegateExecution execution) {

        log.info("进入多实例Before监听器------------------------Start---------------------->");

        Object nrOfCI = execution.getVariable("nrOfCompletedInstances");
        Integer nrOfCompletedInstances = 0;

        if (nrOfCI != null) {
            nrOfCompletedInstances = Integer.parseInt(nrOfCI.toString());
        }
//        if (nrOfCompletedInstances != null) {
//            log.info("nrOfCompletedInstances is null");
//            log.info("nrOfCompletedInstances = " + nrOfCompletedInstances);
////            nrOfCompletedInstances = nrOfCompletedInstances + 1;
////            execution.setVariable("nrOfCompletedInstances", nrOfCompletedInstances);
//        } else {
//            log.info("nrOfCompletedInstances is NOT null");
//            execution.setVariable("nrOfCompletedInstances", 0);
//        }
//        log.info("nrOfCompletedInstances = " + nrOfCompletedInstances);
//        log.info("variables" + execution.getVariables());

        log.info("进入多实例Before监听器------------------------End---------------------->");

    }
}
