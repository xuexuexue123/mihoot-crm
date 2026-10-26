package org.springframework.statemachine.support;

import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.state.State;

public abstract class MyStateMachineUtils extends StateMachineUtils {

    /**
     * 设置状态机为指定的状态
     * @param stateMachine
     * @param state
     * @param <S>
     * @param <E>
     */
    public static <S, E> void setCurrentState(StateMachine<S, E> stateMachine, S state) {
        if (stateMachine instanceof AbstractStateMachine) {
            setCurrentState((AbstractStateMachine<S, E>) stateMachine, state);

            System.out.println("StateMachine Current:" + stateMachine);
        } else {
            throw new IllegalArgumentException("Provided StateMachine is not a valid type");
        }
    }

    public static <S, E> void setCurrentState(AbstractStateMachine<S, E> stateMachine, S state) {
        System.out.println("设置当前状态111111");
        stateMachine.setCurrentState(findState(stateMachine, state), null, null, false, stateMachine);
    }

    private static <S, E> State<S, E> findState(AbstractStateMachine<S, E> stateMachine, S stateId) {
        for (State<S, E> state : stateMachine.getStates()) {
            if (state.getId() == stateId) {
                return state;
            }
        }

        throw new IllegalArgumentException("Specified State ID is not valid");
    }
}