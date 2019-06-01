package com.kong.spring.eventbus.test.memory;

import com.kong.spring.eventbus.test.EventBus;
import com.kong.spring.eventbus.test.EventBusException;
import com.kong.spring.eventbus.test.Subscription;
import com.kong.spring.eventbus.test.SubscriptionMethod;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: kong wang
 * @date: 2019/5/31
 */
@Slf4j
public class MemoryEventBus implements EventBus {
    private final Map<Class<?>, List<SubscriptionMethod>> typesBySubscriber;
    private final Poster asyncPoster;
    private BeanFactory beanFactory;

    public MemoryEventBus() {
        typesBySubscriber = new HashMap<>();
        asyncPoster = new BackgroundPoster(this);
    }

    @Override
    public void subscribe(Subscription subscription) {
        List<SubscriptionMethod> methods = typesBySubscriber.get(subscription.getSubscriber());
        if (methods == null) {
            methods = new ArrayList<>();
        }
        methods.add(subscription.getSubscriptionMethod());
        typesBySubscriber.put(subscription.getSubscriber(), methods);
    }

    @Override
    public void post(Object event) {
        asyncPoster.enqueue(event);
    }

    void invokeSubscriber(Object event) {
        for (Class<?> subscriber : typesBySubscriber.keySet()) {
            for (SubscriptionMethod method : typesBySubscriber.get(subscriber)) {
                try {
                    method.getMethod().invoke(beanFactory.getBean(subscriber), event);
                } catch (InvocationTargetException e) {
                    throw new EventBusException("Invoking subscriber failed", e.getCause());
                } catch (Exception e) {
                    throw new IllegalStateException("Unexpected exception", e);
                }
            }
        }
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }
}