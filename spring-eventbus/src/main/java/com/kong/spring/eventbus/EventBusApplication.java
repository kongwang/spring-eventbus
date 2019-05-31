package com.kong.spring.eventbus;

import com.kong.spring.eventbus.memory.MemoryEventBus;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author: kong wang
 * @date: 2019/5/31
 */
@SpringBootApplication
@Configuration
public class EventBusApplication {
    public static void main(String[] args) {
        SpringApplication.run(EventBusApplication.class, args);
    }

    @Bean
    public EventBus eventBus() {
        return new MemoryEventBus();
    }
}
