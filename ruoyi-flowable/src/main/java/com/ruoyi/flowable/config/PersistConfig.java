package com.ruoyi.flowable.config;
import com.ruoyi.flowable.domain.ConfigEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.statemachine.StateMachineContext;
import org.springframework.statemachine.StateMachinePersist;
import org.springframework.statemachine.persist.DefaultStateMachinePersister;
import org.springframework.statemachine.persist.RepositoryStateMachinePersist;
import org.springframework.statemachine.persist.StateMachinePersister;
import org.springframework.statemachine.data.redis.RedisStateMachineContextRepository;
import org.springframework.statemachine.data.redis.RedisStateMachinePersister;
import org.springframework.statemachine.support.DefaultStateMachineContext;

@Configuration
public class PersistConfig {
	
	@Autowired
	private RedisConnectionFactory redisConnectionFactory;


	/**
	 * 注入RedisStateMachinePersister对象
	 * 
	 * @return
	 */
	@Bean(name = "caseRedisPersister")
	public RedisStateMachinePersister<String, String> redisPersister() {
		return new RedisStateMachinePersister<>(redisPersist());
	}

	/**
	 * 通过redisConnectionFactory创建StateMachinePersist
	 * 
	 * @return
	 */
	public StateMachinePersist<String, String, String> redisPersist() {
		RedisStateMachineContextRepository<String, String> repository = new RedisStateMachineContextRepository<>(redisConnectionFactory);
		return new RepositoryStateMachinePersist<>(repository);
	}

	/**
	 * 持久化配置
	 * 实际使用中，可以配合redis等，进行持久化操作
	 *
	 * @return
	 */


	@Bean
	public DefaultStateMachinePersister persister() {
		return new DefaultStateMachinePersister<>(new StateMachinePersist<Object, Object, ConfigEntity>() {
			@Override
			public void write(StateMachineContext<Object, Object> context, ConfigEntity order) throws Exception {
				//此处并没有进行持久化操作
				System.out.println("这里是持久化write操作");
			}

			@Override
			public StateMachineContext<Object, Object> read(ConfigEntity order) throws Exception {
				//此处直接获取order中的状态，其实并没有进行持久化读取操作
				System.out.println("这里是持久化read操作: " + order.getId());
				return new DefaultStateMachineContext(order.getSource(), order.getEvent(), null, null);
			}
		});
	}
}