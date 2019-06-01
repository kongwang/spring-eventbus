package com.kong.spring.eventbus.test;

import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author: kong wang
 * @date: 2019/5/31
 */
public class SubscribeAnnotationBeanPostProcessor implements BeanPostProcessor, Ordered, BeanFactoryAware {

    private BeanFactory beanFactory;

    private ConcurrentMap<Class<?>, TypeMetadata> typeCache = new ConcurrentHashMap<>();

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> targetClass = AopUtils.getTargetClass(bean);
        final TypeMetadata metadata = this.typeCache.computeIfAbsent(targetClass, this::buildMetadata);
        EventBus eventBus = beanFactory.getBean(EventBus.class);
        for (Subscription subscription : metadata.subscriptions) {
            eventBus.subscribe(subscription);
        }
        return bean;
    }

    private TypeMetadata buildMetadata(Class<?> targetClass) {
        final List<Subscription> methods = new ArrayList<>();
        ReflectionUtils.doWithMethods(targetClass, method -> {
            if (method.getParameterCount() != 1) {
                return;
            }
            if (!method.isAnnotationPresent(Subscribe.class)) {
                return;
            }
            Subscribe subscribe = AnnotationUtils.findAnnotation(method, Subscribe.class);
            SubscriptionMethod subscriptionMethod = new SubscriptionMethod(method, method.getParameterTypes()[0], subscribe);
            methods.add(new Subscription(targetClass, subscriptionMethod));
        });
        if (CollectionUtils.isEmpty(methods)) {
            return TypeMetadata.EMPTY;
        }
        return new TypeMetadata(methods.toArray(new Subscription[methods.size()]));
    }

    private static class TypeMetadata {
        static final TypeMetadata EMPTY = new TypeMetadata();
        final Subscription[] subscriptions;

        private TypeMetadata() {
            this.subscriptions = new Subscription[0];
        }

        TypeMetadata(Subscription[] subscriptions) {
            this.subscriptions = subscriptions;
        }
    }
}
