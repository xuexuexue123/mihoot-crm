package com.ruoyi.flowable.listener;

import com.ruoyi.ems.domain.SysReDecisiontree;
import com.ruoyi.ems.service.ISysReDecisiontreeService;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.ExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ExampleExecutionListenerOne implements ExecutionListener {


    @Autowired
    private ISysReDecisiontreeService sysReDecisiontreeService;


    @Override
    public void notify(DelegateExecution execution) {
        log.info("################## notified ####################");

        String ruleType = execution.getVariable("ruleType").toString();
        String businessRule = execution.getVariable("businessRule").toString();
        String resultProperty = execution.getVariable("resultProperty").toString();

//        SysReDecisiontree sysReDecisiontree = sysReDecisiontreeService.selectSysReDecisiontreeByPyId(businessRule);



    }
}
