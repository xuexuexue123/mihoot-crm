package com.ruoyi.common.flowable.spi;

import java.util.Map;

/**
 * 流程启动前对变量 Map 做扩展（由各业务模块实现并注册为 Spring Bean）。
 */
public interface FlowVariablePreprocessor
{
    /**
     * @param variables 启动参数，可为 null（实现类需判空）
     */
    void beforeProcessStart(Map<String, Object> variables);
}
