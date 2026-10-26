package com.ruoyi.flowable.service;

import com.googlecode.aviator.Expression;
import com.ruoyi.ems.domain.SysAviatorExpression;

import java.util.List;
import java.util.Map;

public interface ISysBreAviatorService {

    /**
     * 获取给定的表达式里所有的变量
     *
     * @param expression case实例
     * @return case实例集合
     */
    public List<SysAviatorExpression> getVariableNames(SysAviatorExpression expression);


    /**
     * 获取给定的表达式编译后的对象
     * @param expression
     * @return
     */
    public Expression getCompiledExpression(String expression);


    /**
     * 获取给定的表达式编译后的对象
     * @param expression
     * @return
     */
    public Boolean getExpressionExecutedResult(String expression, Map<String, Object> variables);

    /**
     * 获取给定表达式的后缀模式
     * 1+3 等于 1 3 +
     * 1+3-2/5 等于 1 3 + 2 5 / -
     * 6>=3 && c==d.a 等于 6 3 >= c d.a == &&
     * 6>=3 && c==d.a || 0.3<4 = 等于 6 3 >= c d.a == && 0.3 4 < ||
     * !true 等于 true !
     * !a && 3==1 等于 a ! 3 1 == &&
     *
     * (3+1)/5 等于 3 1 + 5 /
     * 6>=3 && (c==d.a || 0.3<4)  等于  6 3 >= c d.a == 0.3 4 < || &&
     * @param expression
     * @return
     */
    public String getPostFixExpression(String expression);


}
