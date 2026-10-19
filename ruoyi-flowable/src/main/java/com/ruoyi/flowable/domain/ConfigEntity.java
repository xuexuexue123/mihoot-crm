package com.ruoyi.flowable.domain;

import lombok.Data;
import org.springframework.statemachine.StateMachine;

@Data
public class ConfigEntity {
    /**
     * 业务 id 号
     */
    private String id;

    private String code;

    private StateMachine stateMachine;
    /**
     * 源状态
     */
    private String source;
    /**
     * 目标状态
     */
    private String target;
    /**
     * 触发的事件
     */
    private String event;
    /**
     * 备注信息
     */
    private String info;

    /**
     * 业务类型
     */
    private int type;


    @Override
    public String toString() {
        return "ConfigEntity{" +
                "id=" + id +
                ", source='" + source + '\'' +
                ", target='" + target + '\'' +
                ", event='" + event + '\'' +
                ", info='" + info + '\'' +
                ", type=" + type +
                '}';
    }
}
