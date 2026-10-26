package com.ruoyi.flowable.listener;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.googlecode.aviator.AviatorEvaluator;
import com.ruoyi.ems.domain.SysCaseinstance;
import com.ruoyi.ems.domain.SysReDecisiontree;
import com.ruoyi.ems.service.ISysCaseinstanceService;
import com.ruoyi.ems.service.ISysReDecisiontreeService;
import com.ruoyi.flowable.domain.RuleEntity;
import lombok.extern.slf4j.Slf4j;
import org.flowable.common.engine.api.delegate.Expression;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Service("GlobalBusinessRuleListener")
public class GlobalBusinessRuleListener implements JavaDelegate {

    @Autowired
    private ISysReDecisiontreeService sysReDecisiontreeService;

    @Autowired
    private ISysCaseinstanceService sysCaseinstanceService;


    Expression ruleType;
    Expression businessRule;
    Expression resultProperty;

    Stack<Integer> ruleStack = new Stack<>();
    int[] ruleArray = new int[]{};
//    Deque<RuleEntity> ruleDeque = new ArrayDeque<RuleEntity>();
    Deque<RuleEntity> ruleDeque = new LinkedList<RuleEntity>();

    String finalResult = "";
    String resultVar = "";

    @Override
    public void execute(DelegateExecution execution) {

        log.info("######################### 这里是Business Rule Task ############################");


        if (ruleType.getExpressionText() != null) {
            String pyId = businessRule.getExpressionText();

            resultVar = resultProperty.getExpressionText();


            execution.setVariable("ruleType", ruleType.getExpressionText());
            execution.setVariable("businessRule", businessRule.getExpressionText());
            execution.setVariable("resultProperty", resultProperty.getExpressionText());

            log.info("pyId = " + pyId);

            log.info("resultVar = " + resultVar);

            switch (ruleType.getExpressionText()) {
                case "decisiontree":

                    SysReDecisiontree sysReDecisiontree = sysReDecisiontreeService.selectSysReDecisiontreeByPyId(pyId);
                    String decisionTree = sysReDecisiontree.getDecisionTree();
                    JSONArray dtArray = JSONArray.parseArray(decisionTree);

                    finalResult = executeDecisionTreeRule(dtArray,  execution);


//                    try {
//                        finalResult = executeRuleInJSON(dtArray, false, execution);
//                    } catch (RuntimeException re) {
//
//                    }
                    log.info("finalResult = " + finalResult);

//                    Map<String, Object> variables = execution.getVariables();
                    execution.setVariable(resultVar, finalResult);
//                    execution.setVariable("organization", finalResult);


                case "decisiontable":
                case "when":
                case "expression":
            }

            //更新变量

            Map<String, Object> variables = execution.getVariables();
            String pyCaseId = (String) execution.getVariable("pyCaseId");

            SysCaseinstance sysCaseinstance = sysCaseinstanceService.selectSysCaseinstanceByCaseId(pyCaseId);


            JSONArray caseDataArray = new JSONArray();
            String varString = "{";

            for(Map.Entry<String, Object> item : variables.entrySet()){
//            caseDataObj.put(entry.getKey(), entry.getValue());
//                log.info("item.key = " + item.getKey());
//                log.info("item.value = " + item.getValue());
                String key = item.getKey();
                Object value = item.getValue();

                if (key.equalsIgnoreCase(resultVar)) {
                    value = finalResult;
                }

                JSONObject caseDataObj = new JSONObject();
                caseDataObj.put("key", key);
                caseDataObj.put("value", value);

                if (key.equalsIgnoreCase("variables")) {

                } else {
                    if (value instanceof String) {
                        varString = varString + "\"" + key + "\":\"" + value + "\",";
                    } else {
                        varString = varString + "\"" + key + "\":" + value + ",";
                    }

                    caseDataArray.add(caseDataObj);
                }

            }
            varString = varString.substring(0, varString.length()-1) + "}";
            log.info("varString = " + varString);

            JSONObject caseDataObj = new JSONObject();
            caseDataObj.put("key", "variables");
            caseDataObj.put("value", JSON.parse(varString));

            caseDataArray.add(caseDataObj);

            log.info(caseDataArray.toJSONString());

            //当前case实例的数据
            sysCaseinstance.setPyWorkPage(caseDataArray.toJSONString());
            sysCaseinstanceService.updateSysCaseinstance(sysCaseinstance);

        }

//        log.info("businessRule = " + businessRule.getExpressionText());
//        log.info("businessRule = " + businessRule.getValue(execution));
//        log.info("resultProperty = " + resultProperty.getExpressionText());
//        log.info("resultProperty = " + resultProperty.getValue(execution));


        log.info("######################### 这里是Business Rule Task ############################");
    }

    private String executeRuleInJSON(JSONArray dtArray, boolean onlyFindOtherwise, DelegateExecution execution) {

        String result = "";
        boolean findOtherwise = onlyFindOtherwise;

        for (int i = 0; i < dtArray.size(); i++) {
            JSONObject node = (JSONObject) dtArray.get(i);
            log.info("node = " + node.getInteger("id"));

            RuleEntity rule = new RuleEntity();
            rule.setRuleId(node.getInteger("id"));
            rule.setLabel(node.getString("label"));
            rule.setLeft(node.getString("left"));
            rule.setLeftLabel(node.getString("leftLabel"));
            rule.setLeftType(node.getString("leftType"));
            rule.setRelation(node.getString("relation"));
            rule.setRight(node.getString("right"));
            rule.setAction(node.getString("action"));
            rule.setReturnvalue(node.getString("returnvalue"));
            rule.setNoaction(node.getString("noaction"));

            String rightPart = rule.getRight();

            if (rule.getLeftType() != null) {
                switch (rule.getLeftType()) {
                    case "text":
                    case "date":
                        rightPart = "'" + rightPart + "'";
                        break;
                    case "number":
                    case "boolean":
                }


                String expression = rule.getLeft() + rule.getRelation() + rightPart;

                rule.setExpression(expression);

                boolean expressionResult = matchRule(rule, rule.getLeft(), execution);

                if (expressionResult) {
                    if (rule.getAction().equalsIgnoreCase("continue")) {

                        JSONArray children = node.getJSONArray("children");
                        if (children != null) {

                            if (children.size() > 0) {

                                executeRuleInJSON(children, false, execution);
                            }

                        }
                    } else {

                        finalResult = rule.getReturnvalue();
                        throw new RuntimeException();

                    }

                } else {
                    //do nothing
                    break;
                }
            } else {
                if (rule.getLabel().equalsIgnoreCase("otherwise")) {
                    finalResult = rule.getReturnvalue();
                    throw new RuntimeException();
                }
            }


        }

        return result;

    }

    private String executeDecisionTreeRule(JSONArray dtArray, DelegateExecution execution) {
        String result = "";

        this.treeForeach(dtArray, 0);

        log.info(this.ruleDeque.toString());

//        result = ruleDeque.pollLast().getReturnvalue();

        while(!ruleDeque.isEmpty()) {
//            log.info("ruleDeque size = " + ruleDeque.size());
            RuleEntity rule = ruleDeque.pollFirst();
            String leftPart = rule.getLeft();
            boolean expressionResult = false;

            expressionResult = matchRule(rule, leftPart, execution);

            log.info(rule.getExpression() + " = " + expressionResult);
            switch (rule.getLabel()) {
                //那就返回当前设定值returnvalue
                case "Otherwise":
                    return rule.getReturnvalue();
                case "如果":
            }
            //如果表达式返回值为真true
            if (expressionResult == true) {
                //同时如果这个表达式的action是return
                switch (rule.getAction()) {
                    //那就返回当前设定值returnvalue
                    case "otherwise":
                    case "return":
                        return rule.getReturnvalue();

                    case "continue":
                }

                //如果最后一个元素是Otherwise，那就需要删除
//                RuleEntity ruleOtherwise = ruleDeque.getLast();
//                String ruleLabel = ruleOtherwise.getLabel();
//                if (ruleLabel.equalsIgnoreCase("Otherwise")) {
//                    log.info("last one = " + ruleOtherwise.getLabel());
//                    ruleDeque.removeLast();
//                }

            } else {

//                log.info("before removeChildNode = " + ruleDeque.size());
//                    findOtherwise = true;
                ruleStack.removeAllElements();
//                ruleArray = new int[]{};
                removeChildNode(rule.getRuleId());

//                log.info("after removeChildNode = " + ruleDeque.size());
//                log.info("删除所有的子节点之后的");
//                log.info(ruleDeque.toString());

//                RuleEntity ruleOtherwise = ruleDeque.pollFirst();
//                return ruleOtherwise.getReturnvalue();
                //如果当前的表达式为false，那就去找后面的otherwise，找到就返回，否则继续找
            }

        }
        return result;

    }

    private boolean matchRule(RuleEntity rule, String leftPart, DelegateExecution execution) {
        boolean result = false;


        Map<String, Object> variables = execution.getVariables();

        for(Map.Entry<String, Object> entry : variables.entrySet()){

            String varKey = entry.getKey();
            String varValue = entry.getValue().toString();
//            log.info("key = " + varKey + " ::: value = " + varValue);

            if (varKey.equalsIgnoreCase(leftPart)) {
                String leftPartValue = entry.getValue().toString();

                Map<String, Object> leftMap = new HashMap<>();
                leftMap.put(leftPart, leftPartValue);

                if(leftPartValue.equalsIgnoreCase("true")) {
                    leftMap.clear();
                    leftMap.put(leftPart, true);
                } else if (leftPartValue.equalsIgnoreCase("false")) {

                    leftMap.clear();
                    leftMap.put(leftPart, false);
                }


                com.googlecode.aviator.Expression compiledExp6 = AviatorEvaluator.compile(rule.getExpression(), true);
                result = (boolean) compiledExp6.execute(leftMap);

//                log.info("expression = " + rule.getExpression());
//                log.info("result = " + result);
                break;
            }
        }

        return result;
    }

    private void treeForeach(JSONArray tree, Integer parentId) {
        for (int i = 0; i < tree.size(); i++) {
            JSONObject node = (JSONObject) tree.get(i);
//            log.info("node = " + node.getInteger("id"));

            RuleEntity rule = new RuleEntity();
            rule.setRuleId(node.getInteger("id"));
            rule.setLabel(node.getString("label"));
            rule.setLeft(node.getString("left"));
            rule.setLeftLabel(node.getString("leftLabel"));
            rule.setLeftType(node.getString("leftType"));
            rule.setRelation(node.getString("relation"));
            rule.setRight(node.getString("right"));
            rule.setAction(node.getString("action"));
            rule.setReturnvalue(node.getString("returnvalue"));
            rule.setNoaction(node.getString("noaction"));
            rule.setParentId(parentId);

            String rightPart = rule.getRight();

            if (rule.getLeftType() != null) {
                switch (rule.getLeftType()) {
                    case "text":
                    case "date":
                        rightPart = "'" + rightPart + "'";
                        break;
                    case "number":
                    case "boolean":
                }
            }

            String expression = rule.getLeft() + rule.getRelation() + rightPart;

            rule.setExpression(expression);

//            log.info("expression = " + expression);

            ruleDeque.add(rule);

            JSONArray children = node.getJSONArray("children");
            if (children != null) {

                if (children.size() > 0) {

                    treeForeach(children, rule.getRuleId());

                }

            }
        }
    }

    private void removeChildNode(Integer parentId) {

        for (int i = 0; i < ruleDeque.size(); i++) {

            ruleStack.push(parentId);

            RuleEntity rule = ruleDeque.getFirst();

            if (ruleStack.contains(rule.getParentId())) {
//                log.info("找到了一个: " + rule.getParentId());
                ruleDeque.removeFirst();

                parentId = rule.getRuleId();
            }
        }

//        log.info("rule stack = " + ruleStack.toString());


    }

}
