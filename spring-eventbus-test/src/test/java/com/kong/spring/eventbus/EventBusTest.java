package com.kong.spring.eventbus;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @auther:kong
 * @date :2019/6/2
 * @descption:
 */
@EnableEventBus
@SpringBootTest
@RunWith(SpringRunner.class)
public class EventBusTest {

    @Autowired
    private EventBus eventBus;


    @Test
    public void memoryEventBusPostTest() {
        eventBus.post("memory event bus , hello world");
    }

    @Test
    public void rabbitBrokerEventBusPostTest() {
        eventBus.post("rabbit broker event bus , hello world");
    }

}
