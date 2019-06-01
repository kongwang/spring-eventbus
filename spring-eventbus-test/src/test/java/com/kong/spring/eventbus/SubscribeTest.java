package com.kong.spring.eventbus;

import org.springframework.stereotype.Component;

/**
 * @auther:kong
 * @date :2019/6/2
 * @descption:
 */
@Component
public class SubscribeTest {

    @Subscribe
    public void handleMessage(String message) {
        System.out.println("message is  " + message);
    }

    @Subscribe
    public void handleMessage2(String message) {
        System.out.println("message2 is  " + message);
    }
}
