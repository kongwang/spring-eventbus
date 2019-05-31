package com.kong.spring.eventbus.memory;

import com.kong.spring.eventbus.EventBus;
import com.kong.spring.eventbus.EventBusException;
import com.kong.spring.eventbus.Subscription;
import com.kong.spring.eventbus.SubscriptionMethod;
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
        methods.add(subscription.getMethod());
        typesBySubscriber.put(subscription.getSubscriber(), methods);
    }

    @Override
    public void post(Object event) {
        for (Class<?> subscriber : typesBySubscriber.keySet()) {
            for (SubscriptionMethod method : typesBySubscriber.get(subscriber)) {
                asyncPoster.enqueue(new Subscription(subscriber, method), event);
            }
        }
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