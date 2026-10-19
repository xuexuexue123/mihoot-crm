package com.ruoyi.flowable.listener;

import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.ExecutionListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class MultiInstanceTaskListener implements ExecutionListener {
    @Override
    public void notify(DelegateExecution execution) {

        log.info("进入多实例监听器------------------------Start---------------------->");

        List<String> assigneeList = new ArrayList<>();
        assigneeList.add("1");
        assigneeList.add("2");
        assigneeList.add("100");

        execution.setVariable("assigneeList", assigneeList);
//        execution.setVariable("nrOfCompletedInstances", 0);

        log.info("variables" + execution.getVariables());

        log.info("进入多实例监听器------------------------End---------------------->");

    }
}
