package com.kong.spring.eventbus.test;

import com.kong.spring.eventbus.test.memory.MemoryEventBus;
import com.kong.spring.eventbus.test.spring.broker.rabbitmq.RabbitBrokerEventBus;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @auther:kong
 * @date :2019/6/2
 * @descption:
 */
@Configuration
@SpringBootApplication
public class EventBusTestApplication {
    public static void main(String[] args) {
        SpringApplication.run(EventBusTestApplication.class, args);
    }

//    @Bean
//    public EventBus memoryEventBus() {
//        return new MemoryEventBus();
//    }

    @Bean
    public EventBus rabbitBrokerEventBus() {
        return new RabbitBrokerEventBus();
    }
}
