package com.kong.spring.eventbus;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author: kong wang
 * @date: 2019/5/31
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class MemoryEventBusTest {

    @Autowired
    EventBus eventBus;

    @Test
    public void post() {
        eventBus.post("hello world");
    }

}