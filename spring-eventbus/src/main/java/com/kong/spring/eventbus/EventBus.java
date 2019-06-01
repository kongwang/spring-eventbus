package com.kong.spring.eventbus;

import org.springframework.beans.factory.BeanFactoryAware;

/**
 * @author: kong wang
 * @date: 2019/5/31
 */
public interface EventBus extends BeanFactoryAware {
    void subscribe(Subscription subscription);

    void post(Object event);
}
