package com.ruoyi.flowable.factory;

import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.flowable.config.CustomStateMachineModelFactory;
import com.ruoyi.flowable.config.SpeakerStateMachineModelFactory;
import com.ruoyi.flowable.listener.MyStateMachineListenerAdapter;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineBuilder;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.HashSet;

@Configuration
public class CaseStateMachineBuilder {

    @Resource
    CustomStateMachineModelFactory modelFactory;

    @Resource
    SpeakerStateMachineModelFactory speakerModelFactory;

    @Autowired
    private RedisCache redisCache;


    public StateMachine<String, String> getMachine(BeanFactory beanFactory, String machineId) throws Exception {
        System.out.println("我们开始使用builder了 = " + machineId);
        StateMachineBuilder.Builder<String, String> builder = StateMachineBuilder.builder();

        builder.configureModel().withModel().factory(modelFactory);
        builder.configureConfiguration()
                .withConfiguration()
                .machineId(machineId)
                .beanFactory(beanFactory);


        return builder.build();
    }


    public StateMachine<String, String> getSpeakerMachine(BeanFactory beanFactory, String machineId) throws Exception {
        System.out.println("我们开始使用builder了 = " + machineId);
        StateMachineBuilder.Builder<String, String> builder = StateMachineBuilder.builder();

//        builder.configureModel().withModel().factory(speakerModelFactory);
//        builder.configureConfiguration()
//                .withConfiguration()
//                .machineId(machineId)
//                .beanFactory(beanFactory);


        builder.configureStates()
                .withStates()
                .initial("NEW")
                .end("RESOLVE_COMPLETED")
                .states(new HashSet<String>(Arrays.asList("SPEAKER_CREATED","SPEAKER_APPROVED")));

        builder.configureTransitions()
                .withExternal()
                .source("NEW").target("SPEAKER_CREATED")
                .event("CREATE")
                .and()

                .withExternal()
                .source("SPEAKER_CREATED").target("SPEAKER_APPROVED")
                .event("GETAPPROVAL")
                .and()

                .withExternal()
                .source("SPEAKER_APPROVED").target("RESOLVE_COMPLETED")
                .event("GETDONE");

        StateMachine<String, String> stateMachine = builder.build();
        stateMachine.addStateListener(new MyStateMachineListenerAdapter());
        return stateMachine;
    }




}
