package com.ruoyi.flowable.domain;

import lombok.Data;

@Data
public class RuleEntity {

    private Integer ruleId;

    private Integer parentId;

    private String label;

    private String expression;

    private String returnvalue;

    private String noaction;

    private String left;

    private String leftLabel;

    private String leftType;

    private String right;

    private String action;

    private String relation;

}
