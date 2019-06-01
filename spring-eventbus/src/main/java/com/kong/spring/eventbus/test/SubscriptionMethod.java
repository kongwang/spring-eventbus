package com.kong.spring.eventbus.test;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Method;

/**
 * @author: kong wang
 * @date: 2019/5/31
 */
@Getter
@Setter
@AllArgsConstructor
public class SubscriptionMethod {
    private Method method;
    private Class<?> eventType;
    private Subscribe subscribe;
}
