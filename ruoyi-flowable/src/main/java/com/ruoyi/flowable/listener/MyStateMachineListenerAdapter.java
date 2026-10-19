package com.ruoyi.flowable.listener;

import com.ruoyi.flowable.domain.ConfigEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;

import java.util.LinkedList;
import java.util.List;

@Slf4j
public class MyStateMachineListenerAdapter extends StateMachineListenerAdapter<String, String> {

    private final LinkedList<String> messages = new LinkedList<String>();

    public List<String> getMessages() {
        return messages;
    }

    public void resetMessages() {
        messages.clear();
    }

    @Override
    public void stateEntered(State<String, String> state) {
        log.info("进入： " + state.getId());

    }

    @Override
    public void stateMachineStarted(StateMachine<String, String> stateMachine) {
//        log.info("状态机已启动: " + stateMachine.getState());
    }

    @Override
    public void stateMachineStopped(StateMachine<String, String> stateMachine) {

//        log.info("状态机已停止: " + stateMachine.getState());
    }

    @Override
    public void stateContext(StateContext<String, String> stateContext) {

        if (stateContext.getStage() == StateContext.Stage.STATE_ENTRY) {
//            messages.addFirst("Enter " + stateContext.getTarget().getId());

            ConfigEntity order = (ConfigEntity)stateContext.getMessageHeader("order");
//            log.info("stateContext = " +stateContext.getMessageHeaders());

//            log.info("order = " + order.toString());

            log.info("Enter " + stateContext.getTarget().getId());
        } else if (stateContext.getStage() == StateContext.Stage.STATE_EXIT) {
//            messages.addFirst("Exit " + stateContext.getSource().getId());
            log.info("Exit " + stateContext.getSource().getId());
        } else if (stateContext.getStage() == StateContext.Stage.STATEMACHINE_START) {
//            messages.addLast("Machine started");
            log.info("Machine started");
        } else if (stateContext.getStage() == StateContext.Stage.STATEMACHINE_STOP) {
//            messages.addFirst("Machine stopped");
            log.info("Machine stopped");
        }
    }
}
