package com.ruoyi.ems.domain;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class SysAviatorExpression  extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private String expression;

    private String leftSide;

    private String rightSide;

    private String operator;

    private String relationWithPrevious;

    public String getRelationWithPrevious()
    {
        return relationWithPrevious;
    }
    public void setRelationWithPrevious(String relationWithPrevious)
    {
        this.relationWithPrevious = relationWithPrevious;
    }

    public String getOperator()
    {
        return operator;
    }
    public void setOperator(String operator)
    {
        this.operator = operator;
    }


    public String getLeftSide()
    {
        return leftSide;
    }
    public void setLeftSide(String leftSide)
    {
        this.leftSide = leftSide;
    }


    public String getRightSide()
    {
        return rightSide;
    }
    public void setRightSide(String rightSide)
    {
        this.rightSide = rightSide;
    }

    public String getExpression()
    {
        return expression;
    }
    public void setExpression(String expression)
    {
        this.expression = expression;
    }


    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("expression", getExpression())
                .append("operator", getLeftSide())
                .append("rightSide", getRightSide())
                .append("expression", getOperator())
                .append("relationWithPrevious", getRelationWithPrevious())
                .toString();
    }

}
