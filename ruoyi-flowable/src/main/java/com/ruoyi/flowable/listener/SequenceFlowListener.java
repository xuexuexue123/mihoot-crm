package com.ruoyi.flowable.listener;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.ExecutionListener;
import org.flowable.engine.delegate.TaskListener;
import org.flowable.task.service.delegate.DelegateTask;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class SequenceFlowListener implements ExecutionListener {

    @Override
    public void notify(DelegateExecution execution) {
        log.info("####################");
        log.info("event name = " + execution.getEventName());
        Map<String, Object> processVariables = execution.getVariables();

//        processVariables.get(execution.getSuperExecutionId());


        List<String> assigneeList = new ArrayList<>();
        assigneeList.add("1");
        assigneeList.add("2");
        assigneeList.add("100");

        execution.setVariable("assigneeList", assigneeList);

//        Map<String, Object> processVariables = taskService.getVariables(mytask.getId());
        log.info("process variables = " + processVariables.toString());
        log.info(processVariables.get("variables") + " wants ");
        log.info("approved = " + processVariables.get("approved"));


        String jsonStr = JSON.toJSONString(processVariables.get("variables"));

        JSONObject json = JSONObject.parseObject(jsonStr);
        log.info("approvers = " + json.getString("approvers"));

//        json.fluentPut
        boolean approved = true;
//        processVariables = new HashMap<String, Object>();
        json.fluentPut("Applicant", "approved");


        for(Map.Entry<String, Object> entry : processVariables.entrySet()){
//            log.info("key = " + entry.getKey() + ", value = " + entry.getValue());
            String varKey = entry.getKey();

            if (varKey=="variables") {
                execution.setVariable("variables", json);
            } else {
                execution.setVariable(varKey, entry.getValue());
            }
        }


    }
}
