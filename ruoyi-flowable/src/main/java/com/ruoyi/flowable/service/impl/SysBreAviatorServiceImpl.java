package com.ruoyi.flowable.service.impl;

import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.AviatorEvaluatorInstance;
import com.googlecode.aviator.Expression;
import com.googlecode.aviator.code.CodeGenerator;
import com.googlecode.aviator.lexer.ExpressionLexer;
import com.googlecode.aviator.parser.ExpressionParser;
import com.ruoyi.ems.domain.SysAviatorExpression;
import com.ruoyi.flowable.flow.FakeCodeGenerator;
import com.ruoyi.flowable.service.ISysBreAviatorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class SysBreAviatorServiceImpl implements ISysBreAviatorService {


    @Override
    public List<SysAviatorExpression> getVariableNames(SysAviatorExpression expression) {

        Expression compiledExp = AviatorEvaluator.compile(expression.getExpression());

        List<String> vars = compiledExp.getVariableNames();
        List<SysAviatorExpression> result = new ArrayList<>();
        Stack<String> stackA = new Stack<>();   //表达式元素
        Stack<SysAviatorExpression> stackB = new Stack<>();   //表达式关系操作符

        String postFixString = this.getPostFixExpression(expression.getExpression());
        String[] postFixArray = postFixString.split ("\\s+");

        int leftSidePosition = -1;

        for (int i = 0; i < postFixArray.length; i++) {
            String item = postFixArray[i];
            SysAviatorExpression aviatorExpression = new SysAviatorExpression();

            //speakerlevel national == age 22 <= gender male != && ||
            //speakerlevel national == age 22 <= ||
            //验证是否当前元素仅为英文字母、数字和汉字
            if (item.matches("^[a-z0-9A-Z\u4e00-\u9fa5]+$")) {
                stackA.push(item);
            } else if (item.equals("==")
                    ||item.equals("!=")
                    ||item.equals(">")
                    ||item.equals("<")
                    ||item.equals("<=")
                    ||item.equals(">=")) {

                aviatorExpression.setExpression(expression.getExpression());
                aviatorExpression.setRightSide(stackA.pop());
                aviatorExpression.setLeftSide(stackA.pop());
                aviatorExpression.setOperator(item);
                stackB.push(aviatorExpression);
                result.add(aviatorExpression);
            } else if (item.equals("&&") || item.equals("||")) {
                aviatorExpression = stackB.pop();
                aviatorExpression.setRelationWithPrevious(item);
            }


        }

        return result;
    }


    @Override
    public Expression getCompiledExpression(String expression) {
        Expression compiledExp = AviatorEvaluator.compile(expression);

        return compiledExp;
    }

    @Override
    public Boolean getExpressionExecutedResult(String expression, Map<String, Object> variables) {

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

    @Override
    public String getPostFixExpression(String expression) {

        FakeCodeGenerator codeGenerator = new FakeCodeGenerator();
        AviatorEvaluatorInstance aviatorEvaluatorInstance = AviatorEvaluator.newInstance();


        ExpressionParser parser = new ExpressionParser(aviatorEvaluatorInstance, new ExpressionLexer(aviatorEvaluatorInstance, expression), codeGenerator);
        Expression exp = parser.parse();
        log.info("exp = " + exp);

        String result = codeGenerator.getPostFixExpression();
        log.info("result = " + result);

        return result;
    }
}

