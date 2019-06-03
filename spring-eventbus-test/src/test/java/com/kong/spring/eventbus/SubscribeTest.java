package com.kong.spring.eventbus;

import org.junit.Assert;
import org.springframework.stereotype.Component;

/**
 * @author:kong
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

    @Subscribe
    public void handleTestMessage(TestMessage testMessage) {
        Assert.assertNotNull(testMessage);
        System.out.println(testMessage.getName());
    }
}
