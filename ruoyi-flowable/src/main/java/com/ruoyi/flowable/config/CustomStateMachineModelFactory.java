package com.ruoyi.flowable.config;


import com.ruoyi.flowable.listener.MyStateMachineListenerAdapter;
import org.springframework.statemachine.config.model.*;
import org.springframework.statemachine.listener.StateMachineListener;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.transition.Transition;

import java.util.ArrayList;
import java.util.Collection;

public class CustomStateMachineModelFactory implements StateMachineModelFactory<String, String> {


    @Override
    public StateMachineModel<String, String> build() {
        System.out.println("这里是Custom状态机工厂");
        ConfigurationData<String, String> configurationData = new ConfigurationData<>();

        configurationData.getStateMachineListeners().add(listener());

        Collection<StateData<String, String>> stateData = new ArrayList<>();
        stateData.add(new StateData<String, String>("NEW", true));
        stateData.add(new StateData<String, String>("CREATED"));
        stateData.add(new StateData<String, String>("PENDING_APPROVAL"));
        stateData.add(new StateData<String, String>("RESOLVED_COMPLETED"));
        StatesData<String, String> statesData = new StatesData<>(stateData);

        Collection<TransitionData<String, String>> transitionData = new ArrayList<>();
        transitionData.add(new TransitionData<String, String>("NEW", "CREATED", "CREATE"));
        transitionData.add(new TransitionData<String, String>("CREATED", "PENDING_APPROVAL", "GETAPPROVAL"));
        transitionData.add(new TransitionData<String, String>("PENDING_APPROVAL", "RESOLVED_COMPLETED", "GETDONE"));
        TransitionsData<String, String> transitionsData = new TransitionsData<>(transitionData);

        StateMachineModel<String, String> stateMachineModel = new DefaultStateMachineModel<String, String>(configurationData,
                statesData, transitionsData);

        return stateMachineModel;
    }

    @Override
    public StateMachineModel<String, String> build(String machineId) {

        return build();
    }


    public StateMachineListener<String, String> listener() {
        return new MyStateMachineListenerAdapter();
    }
}