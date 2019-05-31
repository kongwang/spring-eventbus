package com.kong.spring.eventbus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * @author: kong wang
 * @date: 2019/5/31
 */
@Getter
@Setter
@AllArgsConstructor
public class Subscription {
    private Class<?> subscriber;

    private SubscriptionMethod method;
}
