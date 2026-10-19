package com.ruoyi.flowable.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.recipes.persist.PersistStateMachineHandler;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.transition.Transition;

@Slf4j
public class LocalPersistStateChangeListener implements PersistStateMachineHandler.PersistStateChangeListener {

    @Override
    public void onPersist(State<String, String> state, Message<String> message,
                          Transition<String, String> transition, StateMachine<String, String> stateMachine) {

        log.info("开始持久化了: " + stateMachine.getUuid());
        if (message != null && message.getHeaders().containsKey("order")) {

            Integer order = message.getHeaders().get("order", Integer.class);

        }
    }

}
