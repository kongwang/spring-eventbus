package com.kong.spring.eventbus.test;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

/**
 * @auther:kong
 * @date :2019/6/2
 * @descption:
 */
public class EventBusBootstrapConfiguration implements ImportBeanDefinitionRegistrar {
    @Override
    public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry beanDefinitionRegistry) {
        if (!beanDefinitionRegistry.containsBeanDefinition(
                "com.kong.spring.eventbus.subscribeAnnotationBeanPostProcessor")) {

            beanDefinitionRegistry.registerBeanDefinition("com.kong.spring.eventbus.subscribeAnnotationBeanPostProcessor",
                    new RootBeanDefinition(SubscribeAnnotationBeanPostProcessor.class));
        }
    }
}
