package com.ruoyi.flowable.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.Expression;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.ems.domain.*;
import com.ruoyi.ems.service.*;
import com.ruoyi.flowable.domain.ConfigEntity;
import com.ruoyi.flowable.factory.CaseStateMachineBuilder;
import com.ruoyi.flowable.factory.FlowServiceFactory;
import com.ruoyi.flowable.service.ICaseService;
import com.ruoyi.flowable.service.IFlowDefinitionService;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.task.api.Task;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.persist.StateMachinePersister;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

@Slf4j
@Service
public class CaseServiceImpl  extends FlowServiceFactory implements ICaseService {


    @Autowired
    private IFlowDefinitionService flowDefinitionService;

    @Autowired
    private ISysCaseinstanceService sysCaseinstanceService;

    @Autowired
    private ISysCasetypeService sysCasetypeService;

    @Autowired
    private ISysStageService sysStageService;

    @Autowired
    private ISysProcessService sysProcessService;

    @Autowired
    private ISysDatatypeService sysDatatypeService;

    @Autowired
    private ISysReActitemService sysReActitemService;

    @Autowired
    private ISysReDataobjService sysReDataobjService;

    @Autowired
    private CaseStateMachineBuilder mybuilder;


    @Autowired
    private BeanFactory beanFactory;

    @Autowired
    private FlowServiceFactory flowServiceFactory;

    @Autowired
    private StateMachinePersister<String, String, ConfigEntity> persister;


    @Resource(name="caseRedisPersister")
    private StateMachinePersister<String, String, String> caseRedisPersister;


    @Override
    public int create(String caseTypeId)  {

        StateMachine<String, String> stateMachine = null;

        try {

            stateMachine = mybuilder.getMachine(beanFactory, caseTypeId);

            stateMachine.startReactively().block();

            ConfigEntity order = new ConfigEntity();
            order.setId(caseTypeId);
            order.setSource("NEW");
            order.setEvent("CREATE");

            Message message = MessageBuilder.withPayload("CREATE").setHeader("order", order).build();

            if (!stateMachine.sendEvent(message)) {
                System.out.println("线程名称：" + Thread.currentThread().getName() + " 支付失败, 状态异常，订单号：");
            }

            caseRedisPersister.persist(stateMachine, order.getId());

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 1;
    }

    public void next(int id) {

        id = 111333222;

        StateMachine<String, String> stateMachine = null;

        try {
            stateMachine = mybuilder.getSpeakerMachine(beanFactory, "speakerMachine");

            stateMachine.startReactively().block();

            ConfigEntity order = new ConfigEntity();
            order.setId(String.valueOf(id));
            order.setSource("NEW");
            order.setEvent("CREATE");

            Message message = MessageBuilder.withPayload("CREATE").setHeader("order", order).build();

            if (!stateMachine.sendEvent(message)) {
                System.out.println("线程名称：" + Thread.currentThread().getName() + " 支付失败, 状态异常，订单号：");
            }

            caseRedisPersister.persist(stateMachine, order.getId());


        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    @Override
    public AjaxResult startCaseInstanceById(String caseDefId, String caseId, Map<String, Object> variables) {

        /**
         * 1. 找到指定的case定义
         * 2、找到case定义下的所有stage
         * 3、找到第一个stage下的所有流程
         * 4、找到每个流程的procDefId
         */

//        caseDefId = "a4761de2-bb31-4e59-a5ed-1e293aaf3d01";

        String initialStage = "";
        String initialProcessDefId = "";

        SysCaseinstance sysCaseinstance = new SysCaseinstance();
        //设置生成的当前的case id
        sysCaseinstance.setPyCaseId(caseId);
        //case type的id
        sysCaseinstance.setCaseTypeId(caseDefId);

        JSONArray caseDataArray = new JSONArray();


        for(Map.Entry<String, Object> entry : variables.entrySet()){
//            caseDataObj.put(entry.getKey(), entry.getValue());

            JSONObject caseDataObj = new JSONObject();
            caseDataObj.put("key", entry.getKey());
            caseDataObj.put("value", entry.getValue());

            caseDataArray.add(caseDataObj);
        }

        //当前case实例的数据
        sysCaseinstance.setPyWorkPage(caseDataArray.toJSONString());

        SysUser sysUser = SecurityUtils.getLoginUser().getUser();
        sysCaseinstance.setPxCreateBy(sysUser.getUserName());
        sysCaseinstance.setPxCreateTime(DateUtils.getNowDate());
        sysCaseinstance.setPyUpdateBy(sysUser.getUserName());
        sysCaseinstance.setPyUpdateTime(DateUtils.getNowDate());

        SysCasetype casetype = new SysCasetype();
        casetype.setCaseTypeId(caseDefId);

        SysCasetype casse = sysCasetypeService.selectSysCasetypeByCaseTypeId(caseDefId);


        SysStage stageFilter = new SysStage();
        stageFilter.setCaseTypeId(caseDefId);
        List<SysStage> stages = sysStageService.selectSysStageList(stageFilter);

        JSONArray stageArray = new JSONArray();

        int i = 0;
        for (SysStage item: stages) {

//            log.info("stage = " + item.getStageName());

            SysProcess sysProcess = new SysProcess();
            sysProcess.setCaseTypeId(caseDefId);
            sysProcess.setStageId(item.getStageId().toString());

            List<SysProcess> processList = sysProcessService.selectSysProcessList(sysProcess);


            JSONObject stageItem = new JSONObject();
            stageItem.put("name", item.getStageName());
            stageItem.put("label", item.getStageLabel());
            if (i==0) {
                sysCaseinstance.setPyCurrentStage(item.getStageName());
                sysCaseinstance.setPyStatusWork(item.getStageStatusWork());
                stageItem.put("current", "true");
            } else {
                stageItem.put("current", "false");
            }

            JSONArray processArray = new JSONArray();

            int j = 0;
            for (SysProcess processItem: processList) {

                JSONObject process = new JSONObject();
                process.put("name", processItem.getProcessName());
                process.put("id", processItem.getProcessId());
                process.put("skip", processItem.getSkipProcess());
                process.put("condition", processItem.getSkipCondition());
                process.put("deploymentName", processItem.getDeploymentName());
                process.put("deploymentId", processItem.getDeploymentId());
                process.put("procDefId", processItem.getProcDefId());
                process.put("processType", processItem.getProcessType());

                if (i==0&&j==0) {

                    process.put("executionStatus", "running");
                    initialProcessDefId = processItem.getProcDefId();

                } else {

                    process.put("executionStatus", "pending");
                }

                processArray.add(process);
                j++;
            }
            stageItem.put("processes", processArray);

            stageArray.add(stageItem);
            i++;

        }

//        log.info("stage = " + stageArray.toJSONString());

        sysCaseinstance.setPyName(casse.getCaseName());
        sysCaseinstance.setPyLabel(casse.getCaseLabel());
        sysCaseinstance.setPyCaseStructure(stageArray.toJSONString());
        sysCaseinstanceService.insertSysCaseinstance(sysCaseinstance);

//        this.create(caseId);

        flowDefinitionService.startProcessInstanceById(initialProcessDefId, caseId, variables);

        return AjaxResult.success("Case启动成功");
    }

    @Override
    public List<Task> queryListByInstanceId(String instanceId) {
        return null;
    }

//    @Override
//    public void stopCaseInstance(OrderVo vo) {
//
//    }

    public AjaxResult moveToNextStage_Wait(String pyCaseId) {

        SysCaseinstance caseinstance = sysCaseinstanceService.selectSysCaseinstanceByCaseId(pyCaseId);
        JSONArray caseStages = JSONArray.parseArray(caseinstance.getPyCaseStructure());

        for (Object stage: caseStages ) {
            log.info("stage = " + ((JSONObject)stage).toJSONString());
        }

//        StateMachine<String, String> stateMachine = null;
//
//        try {
//            stateMachine = mybuilder.getSpeakerMachine(beanFactory, "speakerMachine");
//
//            stateMachine.startReactively().block();
//
//            ConfigEntity order = new ConfigEntity();
//            order.setId(pyCaseId);
//            order.setSource("NEW");
//            order.setEvent("CREATE");
//
//            Message message = MessageBuilder.withPayload("CREATE").setHeader("order", order).build();
//
//            if (!stateMachine.sendEvent(message)) {
//                System.out.println("线程名称：" + Thread.currentThread().getName() + " 支付失败, 状态异常，订单号：");
//            }
//
//            caseRedisPersister.persist(stateMachine, order.getId());
//
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        return AjaxResult.success("Case更新成功");
    }

    /**
     *      * 1.找到pyCaseId对应的流程实例数据
     *      * 2、获取里面的case structure
     *      * 3、移动到下一个状态
     *      * 4、启动下一个状态下的流程
     *
     * @param pyCaseId
     * @return
     */
    @Override
    public void postActionAfterProcessCompleted(String pyCaseId, String flowExecutionId) {
        Boolean reentry = false;
        SysCaseinstance sysCaseinstance = sysCaseinstanceService.selectSysCaseinstanceByCaseId(pyCaseId);

        if (sysCaseinstance == null) {
            return;
        }
        Map<String, Object> flowVariables = flowServiceFactory.getRuntimeService().getVariables(flowExecutionId);

//        log.info("syscase instance = " + sysCaseinstance.getPyCaseStructure());

        JSONArray stages = JSONArray.parseArray(sysCaseinstance.getPyCaseStructure());

        Object jumpTo = flowServiceFactory.getRuntimeService().getVariable(flowExecutionId, "jumpTo");

        log.info("jumpTo = " + jumpTo);

        if (jumpTo != null) {
            stages = moveToAssignedStage(stages, pyCaseId, jumpTo.toString(), flowExecutionId);
            flowServiceFactory.getRuntimeService().removeVariable(flowExecutionId, "jumpTo");

//            flowServiceFactory.getRuntimeService().setVariable(flowExecutionId, "reentry", "true");
//            flowVariables.put("reentry", "true");
            reentry = true;

//            for(Map.Entry<String, Object> entry : flowVariables.entrySet()){
//
//                String varKey = entry.getKey();
//                if (varKey.equalsIgnoreCase("jumpTo")) {
//                    flowServiceFactory.getRuntimeService().setVariable(flowExecutionId, "jumpTo", "nextstage");
//                    flowVariables.put("jumpTo", "nextstage");
//                    break;
//                }
//            }
        } else {

//            flowServiceFactory.getRuntimeService().setVariable(flowExecutionId, "reentry", "false");
            reentry = false;
        }
//        log.info("#######reentry = " + reentry);

//        log.info("#######updated stages = " + stages.toJSONString());

        for(Map.Entry<String, Object> entry : flowVariables.entrySet()){

            log.info("key = " + entry.getKey() + " value = " + entry.getValue());
        }

//        String nextProcDefId = this.findNextPendingFlowInStage(stages, flowVariables, reentry);
        ////////*****************************
        Stack<JSONObject> nextProcs = this.findNextPendingFlows(stages, flowVariables, reentry);

//        log.info("#################flow = " + nextProcs.toString());
//        if (nextProcDefId.equalsIgnoreCase("notfound")) {
//            this.moveToNextStage(sysCaseinstance, stages);
//        }

        ////////*****************************
        int currentStagePosition = 0;
        for (int i = 0; i < stages.size(); i++) {
            JSONObject stage = stages.getJSONObject(i);

            String currentStatus = stage.getString("current");

            if (currentStatus.equalsIgnoreCase("true")) {
                currentStagePosition = i;
            }

        }

        int i = 1;
        ////////*****************************
        while(nextProcs.isEmpty()) {
            if ( i > stages.size()-1) {
                break;
            } else {

                if (currentStagePosition < stages.size()-1) {
                    this.moveToNextStage(sysCaseinstance, stages);
                }

                nextProcs = this.findNextPendingFlows(stages, flowVariables, reentry);

            }

            i++;
        }
        ////////*****************************
//        while(nextProcDefId.equalsIgnoreCase("notfound")){
//            if ( i > stages.size()-1) {
////                log.info("break 了哦");
//                break;
//            } else {
//
//                if (currentStagePosition < stages.size()-1) {
//                    this.moveToNextStage(sysCaseinstance, stages);
//                }
//
//                nextProcDefId = this.findNextPendingFlowInStage(stages, flowVariables, reentry);
//
//            }
//
//            i++;
//        }
        ////////*****************************
        if(!nextProcs.isEmpty()) {
            JSONArray pyWorkPage = JSONArray.parseArray(sysCaseinstance.getPyWorkPage());

            Map<String, Object> variables = new HashMap<>();
            pyWorkPage.forEach(object -> {
                if (object == null) {
                    return;
                }
                JSONObject jsonObject = (JSONObject) object;


                if (jsonObject.getString("value").startsWith("{")) {

                    variables.put(jsonObject.getString("key"), JSONObject.parseObject(jsonObject.getString("value")));

                } else {


                    variables.put(jsonObject.getString("key"), jsonObject.getString("value"));
                }
            });

//            for (int j = 0; j < nextProcs.size(); j++) {
//                JSONObject flow = nextProcs.pop();
//                String procDefId = flow.getString("procDefId");
//
//                flowDefinitionService.startProcessInstanceById(procDefId, pyCaseId, variables);
//
//            }


            for (JSONObject flow : nextProcs) {

                String procDefId = flow.getString("procDefId");

                flowDefinitionService.startProcessInstanceById(procDefId, pyCaseId, variables);
            }
//            log.info("stages stages = " + stages.toJSONString());
//            log.info("nextProcDefId = " + nextProcDefId);

            sysCaseinstance.setPyCaseStructure(stages.toJSONString());
            sysCaseinstanceService.updateSysCaseinstance(sysCaseinstance);
        }


//        if (!nextProcDefId.equalsIgnoreCase("notfound")) {
//
//            JSONArray pyWorkPage = JSONArray.parseArray(sysCaseinstance.getPyWorkPage());
//
//            Map<String, Object> variables = new HashMap<>();
//            pyWorkPage.forEach(object -> {
//                if (object == null) {
//                    return;
//                }
//                JSONObject jsonObject = (JSONObject) object;
//
//
//                if (jsonObject.getString("value").startsWith("{")) {
//
//                    variables.put(jsonObject.getString("key"), JSONObject.parseObject(jsonObject.getString("value")));
//
//                } else {
//
//
//                    variables.put(jsonObject.getString("key"), jsonObject.getString("value"));
//                }
//            });
//
////                        log.info("variables = " + variables.toString());
//            flowDefinitionService.startProcessInstanceById(nextProcDefId, pyCaseId, variables);
//
//        }

//            log.info("stages stages = " + stages.toJSONString());
//            log.info("nextProcDefId = " + nextProcDefId);

//        sysCaseinstance.setPyCaseStructure(stages.toJSONString());
//        sysCaseinstanceService.updateSysCaseinstance(sysCaseinstance);

    }

    @Override
    public boolean updateProcessStatus(String pyCaseId, String procDefId) {

        log.info("####### pyCaseId = " + pyCaseId + " procDefId=" + procDefId);

        SysCaseinstance sysCaseinstance = sysCaseinstanceService.selectSysCaseinstanceByCaseId(pyCaseId);

        if (sysCaseinstance == null) {
            return false;
        }
        JSONArray stages = JSONArray.parseArray(sysCaseinstance.getPyCaseStructure());
        boolean allCompleted = true;
        int currentStagePosition = 0;

        OUT:
        for (int i = 0; i < stages.size(); i++) {
            JSONObject stage = stages.getJSONObject(i);

            //轮训当前阶段下的所有流程
            //找到当前阶段下指定的流程，将流程的状态更新为完成
            JSONArray flows = stage.getJSONArray("processes");
            for (int j = 0; j < flows.size(); j++) {

                JSONObject flow = flows.getJSONObject(j);

                if (flow.getString("executionStatus").equalsIgnoreCase("running")) {

                    if (flow.getString("procDefId").equalsIgnoreCase(procDefId)) {

                        flow.put("executionStatus", "completed");

                        sysCaseinstance.setPyCaseStructure(stages.toJSONString());
                        sysCaseinstanceService.updateSysCaseinstance(sysCaseinstance);

                        currentStagePosition = i;
                        break OUT;
                    }

                }

            }

        }

        COMPLETEDOUT:
        for (int i = currentStagePosition; i < stages.size(); i++) {
            JSONObject stage = stages.getJSONObject(i);

            //轮训所有的阶段下的所有流程
            JSONArray flows = stage.getJSONArray("processes");
            for (int j = 0; j < flows.size(); j++) {

                JSONObject flow = flows.getJSONObject(j);

                if (!flow.getString("executionStatus").equalsIgnoreCase("completed")) {
                    allCompleted = false;

                    break COMPLETEDOUT;
                }

            }

        }

        return allCompleted;


    }

    @Override
    public boolean executeProcessActivity(String pyId, String flowExecutionId) {

        //获取表名tableName
        SysReDataobj sysReDataobj = sysReDataobjService.selectSysReDataobjByActivityTarget(pyId);

        if (sysReDataobj.getTableName() != null && sysReDataobj.getTableName() != "") {

            //获取activity明细
            SysReActitem sysReActitem = new SysReActitem();
            sysReActitem.setActId(pyId);

            List<SysReActitem> sysReActitemList = sysReActitemService.selectSysReActitemList(sysReActitem);
            if (sysReActitemList.size() > 0) {

                String fields = "";
                String values = "";
                String propertyValue = "";
                String tableName = sysReDataobj.getTableName();
                int flag = 0;


                Map<String, Object> flowVariables = flowServiceFactory.getRuntimeService().getVariables(flowExecutionId);

//                JSONObject jsonObject = new JSONObject();
//                jsonObject.put("tableName", sysReDataobj.getTableName());

                for (int i = 0; i < sysReActitemList.size(); i++) {

                    SysReActitem item = sysReActitemList.get(i);
                    if (i==0 && !item.getAction().equalsIgnoreCase("Page-New")) {
                        return false;
                    } else {
                        if (item.getAction().equalsIgnoreCase("Page-New")) {
                            flag = 1;
                        }
                        if (flag == 1 && item.getAction().equalsIgnoreCase("Property-Set")) {

                            fields = fields + item.getPropertyName() + ",";

                            //如果这个赋值是从流程变量里获取
                            if (item.getPropertyValue().startsWith("\"") && item.getPropertyValue().endsWith("\"")) {
                                propertyValue = item.getPropertyValue().substring(1, item.getPropertyValue().length()-1);
                            } else {
                                propertyValue = getPropertyValueFromVariables(item.getPropertyValue(), flowVariables);
                            }
                            values = values + "'" + propertyValue + "',";

                        }
                        if (flag == 1 && item.getAction().equalsIgnoreCase("Obj-Save")) {
                            flag = 2;
                        }

                        if (flag == 2) {
                            fields = fields.substring(0, fields.length()-1);
                            values = values.substring(0, values.length()-1);

//                            jsonObject.put("fields", fields);
//                            jsonObject.put("values", values);
//                            String jsonStr = JSONObject.toJSONString(jsonObject);
//                            log.info("jsonStr = " + jsonStr);

                            int result = sysReDataobjService.insertTemplateTableData(tableName, fields, values);
                            log.info("result = " + result);
                            fields = "";
                            values = "";
                        }
                    }
                }

            }
        }

        return false;
    }

    private String getPropertyValueFromVariables(String propertyVar, Map<String, Object> variables) {

        String result = "";

        for(Map.Entry<String, Object> entry : variables.entrySet()){

            String varKey = entry.getKey();

            if (propertyVar.contains(varKey)) {
                result = entry.getValue().toString();

                return result;

            }
        }

        return propertyVar;
    }

    private JSONArray moveToAssignedStage(JSONArray stages, String pyCaseId, String assignedStage, String flowExecutionId) {

        log.info("####### move to assigned stage = " + assignedStage);


        for (int i = 0; i < stages.size(); i++) {
            JSONObject stage = stages.getJSONObject(i);

            String currentStatus = stage.getString("current");

            if (currentStatus.equalsIgnoreCase("true")) {
                stage.put("current", "false");
            }

        }

        log.info("before stages.toJSONString() = " + stages.toJSONString());
        for (int i = 0; i < stages.size(); i++) {
            JSONObject stage = stages.getJSONObject(i);

            if (stage.getString("name").equalsIgnoreCase(assignedStage)) {
                stage.put("current", "true");

                //这是当前的阶段

                //轮训当前阶段下的所有流程
                JSONArray flows = stage.getJSONArray("processes");
                for (int j = 0; j < flows.size(); j++) {

                    JSONObject flow = flows.getJSONObject(j);
                    flow.put("executionStatus", "pending");

                }

                log.info("after stages.toJSONString() = " + stages.toJSONString());

                return stages;
            }


        }


        return stages;
    }

    private boolean moveToNextStage(SysCaseinstance sysCaseinstance, JSONArray stages) {

        int currentPosition = 0;
        for (int i = 0; i < stages.size(); i++) {
            JSONObject stage = stages.getJSONObject(i);

            String currentStatus = stage.getString("current");

            if (currentStatus.equalsIgnoreCase("true")) {
                stage.put("current", "false");
                currentPosition = i;
            }
        }

        log.info("currentPosition = " + currentPosition);
        log.info("stages.size() = " + stages.size());

        if (currentPosition < stages.size()) {

            if (currentPosition == stages.size()-1) {

                JSONObject stage = stages.getJSONObject(currentPosition);
                stage.put("current", "true");
            } else {

                JSONObject stage = stages.getJSONObject(currentPosition+1);
                stage.put("current", "true");

                log.info("updated stages = " + stages.toJSONString());

            }

            //把当前stage下的所有流程的执行状态设置为pending
            for (int i = 0; i < stages.size(); i++) {
                JSONObject stage = stages.getJSONObject(i);

                String currentStatus = stage.getString("current");
                //这是当前的阶段
                if (currentStatus.equalsIgnoreCase("true")) {
                    //轮训当前阶段下的所有流程
                    JSONArray flows = stage.getJSONArray("processes");
                    for (int j = 0; j < flows.size(); j++) {

                        JSONObject flow = flows.getJSONObject(j);
                        flow.put("executionStatus", "pending");

                    }
                }

            }

            sysCaseinstance.setPyCaseStructure(stages.toJSONString());
            sysCaseinstanceService.updateSysCaseinstance(sysCaseinstance);
            return true;
        }


        return false;
    }

    private Stack<JSONObject> findNextPendingFlows(JSONArray stages, Map<String, Object> flowVariables, Boolean reentry) {
        Stack<JSONObject> results = new Stack<JSONObject>();


        for (int i = 0; i < stages.size(); i++) {
            JSONObject stage = stages.getJSONObject(i);

//            log.info("stage name = " + stage.getString("name"));
//            log.info("current = " + stage.getString("current"));
//            log.info("processes = " + stage.getJSONArray("processes").toJSONString());

            String currentStatus = stage.getString("current");
            //这是当前的阶段
            if (currentStatus.equalsIgnoreCase("true")) {

                JSONArray flows = stage.getJSONArray("processes");
                for (int j = 0; j < flows.size(); j++) {

                    JSONObject flow = flows.getJSONObject(j);

                    if (flow.getString("skip").equalsIgnoreCase("never") || checkConditionResult(flow.getString("condition"), flowVariables)) {

                        String executionStatus = flow.getString("executionStatus");
                        if (executionStatus.equalsIgnoreCase("pending") || (!executionStatus.equalsIgnoreCase("pending") && reentry)) {

//                            String procDefId = flow.getString("procDefId");
//                            log.info("我们要启动流程: " + procDefId);

                            if (!results.isEmpty() && flow.getString("processType").equalsIgnoreCase("sequential")) {
                                return results;
                            } else {

                                flow.put("executionStatus", "running");
                                results.push(flow);
                            }



                        }

                    }

                }
            }
        }

        return results;

    }

    private String findNextPendingFlowInStage(JSONArray stages, Map<String, Object> flowVariables, Boolean reentry) {


        for (int i = 0; i < stages.size(); i++) {
            JSONObject stage = stages.getJSONObject(i);

//            log.info("stage name = " + stage.getString("name"));
//            log.info("current = " + stage.getString("current"));
//            log.info("processes = " + stage.getJSONArray("processes").toJSONString());

            String currentStatus = stage.getString("current");
            //这是当前的阶段
            if (currentStatus.equalsIgnoreCase("true")) {
                //轮训当前阶段下的所有流程
                JSONArray flows = stage.getJSONArray("processes");
                for (int j = 0; j < flows.size(); j++) {

                    JSONObject flow = flows.getJSONObject(j);
//                    log.info("flow name = " + flow.getString("name"));
//                    log.info("deploymentId = " + flow.getString("deploymentId"));
//                    log.info("procDefId = " + flow.getString("procDefId"));
//                    log.info("processType = " + flow.getString("processType"));
//                    log.info("executionStatus = " + flow.getString("executionStatus"));
                    log.info("skip" + flow.getString("skip"));

                    if (flow.getString("skip").equalsIgnoreCase("never")) {

                        String executionStatus = flow.getString("executionStatus");
                        log.info("<findNextPendingFlowInStage> jumpto = " + reentry);

                        if (executionStatus.equalsIgnoreCase("pending") || (!executionStatus.equalsIgnoreCase("pending") && reentry)) {

                            flow.put("executionStatus", "started");
                            String procDefId = flow.getString("procDefId");
                            log.info("我们要启动流程: " + procDefId);

                            return procDefId;

                        }
                    } else {
                        //判断跳过的条件，看是否为true

                        log.info("condition = " + flow.getString("condition"));

                        //.gender='male'
                        boolean result = checkConditionResult(flow.getString("condition"), flowVariables);
                        log.info("checked result = " + result);
                        if (result) {

                            String executionStatus = flow.getString("executionStatus");

                            if (executionStatus.equalsIgnoreCase("pending") || (!executionStatus.equalsIgnoreCase("pending") && reentry)) {

                                flow.put("executionStatus", "started");
                                String procDefId = flow.getString("procDefId");
                                log.info("我们要启动流程: " + procDefId);

                                return procDefId;

                            }
                        }
                    }

                }
//                log.info("flows = " + flows.toJSONString());
            }

        }

        return "notfound";
    }

    @Override
    public void updateCaseVariables(String flowInstanceId, String flowExecutionId) {

        log.info("开始更新变量了");
        Map<String, Object> variables = flowServiceFactory.getRuntimeService().getVariables(flowExecutionId);
        String pyCaseId = "";

        for(Map.Entry<String, Object> entry : variables.entrySet()){

            String varKey = entry.getKey();
            if (varKey.equalsIgnoreCase("pyCaseId")) {
                pyCaseId = entry.getValue().toString();


                break;
            }
        }

        if (pyCaseId != "" && pyCaseId != null) {

            SysCaseinstance sysCaseinstance = sysCaseinstanceService.selectSysCaseinstanceByCaseId(pyCaseId);


            JSONArray caseDataArray = new JSONArray();


            for(Map.Entry<String, Object> item : variables.entrySet()){
//            caseDataObj.put(entry.getKey(), entry.getValue());
//                log.info("item.key = " + item.getKey());
//                log.info("item.value = " + item.getValue());

                JSONObject caseDataObj = new JSONObject();
                caseDataObj.put("key", item.getKey());
                caseDataObj.put("value", item.getValue());

                caseDataArray.add(caseDataObj);
            }

            log.info(caseDataArray.toJSONString());

            //当前case实例的数据
            sysCaseinstance.setPyWorkPage(caseDataArray.toJSONString());
            sysCaseinstanceService.updateSysCaseinstance(sysCaseinstance);

        }

    }

    @Override
    public void delete(String instanceId, String deleteReason) {

    }

    @Override
    public HistoricProcessInstance getHistoricCaseInstanceById(String caseInstanceId) {
        return null;
    }

    private boolean checkConditionResult(String expression, Map<String, Object> variables) {

//        String expression = "a-(b-c)>100";
        //gender='male'

        Map<String, Object> env = new HashMap<>();
        Boolean result = false;

        Expression compiledExp = AviatorEvaluator.compile(expression);

        List<String> vars = compiledExp.getVariableNames();


        for(Map.Entry<String, Object> entry : variables.entrySet()){

            String varKey = entry.getKey();

            if (vars.contains(varKey)) {
                env.put(varKey, entry.getValue());

                result = (Boolean) compiledExp.execute(env);

                return result;

            }
        }

        return false;
    }
}
