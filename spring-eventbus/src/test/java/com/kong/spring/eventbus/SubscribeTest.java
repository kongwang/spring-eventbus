package com.kong.spring.eventbus;

import org.springframework.stereotype.Component;

/**
 * @author: kong wang
 * @date: 2019/5/31
 */
@Component
public class SubscribeTest {
    @Subscribe
    public void handleMessage(String message) {
        System.out.println(message);
    }
}
