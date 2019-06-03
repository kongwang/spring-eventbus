package com.kong.spring.eventbus.broker.rabbitmq;

import com.kong.spring.eventbus.EventBus;
import com.kong.spring.eventbus.Subscription;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListenerConfigurer;
import org.springframework.amqp.rabbit.config.RabbitListenerConfigUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.MethodRabbitListenerEndpoint;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistrar;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistry;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.messaging.handler.annotation.support.DefaultMessageHandlerMethodFactory;
import org.springframework.messaging.handler.annotation.support.MessageHandlerMethodFactory;
import org.springframework.messaging.handler.invocation.InvocableHandlerMethod;
import org.springframework.util.Assert;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static org.springframework.amqp.rabbit.annotation.RabbitListenerAnnotationBeanPostProcessor.DEFAULT_RABBIT_LISTENER_CONTAINER_FACTORY_BEAN_NAME;

/**
 * @author kong wang
 * @date :2019/6/1
 * @descption:
 */
public class RabbitBrokerEventBus implements EventBus, SmartInitializingSingleton {
    private final AtomicInteger counter = new AtomicInteger();
    private final RabbitListenerEndpointRegistrar registrar = new RabbitListenerEndpointRegistrar();
    private final RabbitHandlerMethodFactoryAdapter messageHandlerMethodFactory =
            new RabbitHandlerMethodFactoryAdapter();
    private final String RABBITMQ_EVENTBUS_PREFIX = "rabbitmq.eventbus";
    private final String RABBITMQ_EVENTBUS_SPLIT_SYMBOL = "_";
    private RabbitListenerEndpointRegistry endpointRegistry;
    private BeanFactory beanFactory;
    private String defaultContainerFactoryBeanName = DEFAULT_RABBIT_LISTENER_CONTAINER_FACTORY_BEAN_NAME;
    private int increment;

    private static String replaceProxy(String proxy) {
        int index = proxy.indexOf("$");
        if (index > -1) {
            return proxy.substring(0, index);
        }
        return proxy;
    }

    @Override
    public void subscribe(Subscription subscription) {
        MethodRabbitListenerEndpoint endpoint = new MethodRabbitListenerEndpoint();
        endpoint.setMethod(subscription.getSubscriptionMethod().getMethod());
        endpoint.setBean(this.beanFactory.getBean(subscription.getSubscriber()));
        endpoint.setMessageHandlerMethodFactory(this.messageHandlerMethodFactory);
        endpoint.setId("org.springframework.amqp.rabbit.RabbitListenerEndpointContainer#" + this.counter.getAndIncrement());
        endpoint.setQueueNames(resolveQueueName(subscription));
        endpoint.setBeanFactory(this.beanFactory);
        endpoint.setMessageConverter(new Jackson2JsonMessageConverter());
        this.registrar.registerEndpoint(endpoint);
    }

    @Override
    public void post(Object event) {
        RabbitTemplate rabbitTemplate = this.beanFactory.getBean(RabbitTemplate.class);
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
        rabbitTemplate.convertAndSend(getExchangeName(event.getClass()), null, event);
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    private String resolveQueueName(Subscription subscription) {
        String queueName = getQueueName(subscription);
        declareQueueAndExchangeAndBinding(subscription, queueName);
        return queueName;
    }

    private void declareQueueAndExchangeAndBinding(Subscription subscription, String queueName) {
        Queue queue = new Queue(queueName);
        queue.setShouldDeclare(true);
        ((ConfigurableBeanFactory) this.beanFactory).registerSingleton(queueName + ++this.increment, queue);

        String exchangeName = getExchangeName(subscription.getSubscriptionMethod().getEventType());
        FanoutExchange exchange = new FanoutExchange(exchangeName);
        exchange.setShouldDeclare(true);
        ((ConfigurableBeanFactory) this.beanFactory).registerSingleton(exchangeName + ++this.increment, exchange);

        Binding binding = BindingBuilder.bind(queue).to(exchange);
        binding.setShouldDeclare(true);
        ((ConfigurableBeanFactory) this.beanFactory).registerSingleton(exchangeName + "." + queueName, binding);
    }

    private String getQueueName(Subscription subscription) {
        StringBuilder queueName = new StringBuilder();
        queueName.append(RABBITMQ_EVENTBUS_PREFIX);
        queueName.append(RABBITMQ_EVENTBUS_SPLIT_SYMBOL);
        queueName.append(replaceProxy(subscription.getSubscriber().getSimpleName()));
        queueName.append(RABBITMQ_EVENTBUS_SPLIT_SYMBOL);
        queueName.append(subscription.getSubscriptionMethod().getMethod().getName());
        queueName.append(RABBITMQ_EVENTBUS_SPLIT_SYMBOL);
        queueName.append(subscription.getSubscriptionMethod().getEventType().getSimpleName());
        return queueName.toString();
    }

    private String getExchangeName(Class<?> event) {
        StringBuilder exchangeName = new StringBuilder();
        exchangeName.append(RABBITMQ_EVENTBUS_PREFIX);
        exchangeName.append(RABBITMQ_EVENTBUS_SPLIT_SYMBOL);
        exchangeName.append(event.getName());
        return exchangeName.toString();
    }

    @Override
    public void afterSingletonsInstantiated() {
        this.registrar.setBeanFactory(this.beanFactory);

        if (this.beanFactory instanceof ListableBeanFactory) {
            Map<String, RabbitListenerConfigurer> instances =
                    ((ListableBeanFactory) this.beanFactory).getBeansOfType(RabbitListenerConfigurer.class);
            for (RabbitListenerConfigurer configurer : instances.values()) {
                configurer.configureRabbitListeners(this.registrar);
            }
        }

        if (this.registrar.getEndpointRegistry() == null) {
            if (this.endpointRegistry == null) {
                Assert.state(this.beanFactory != null,
                        "BeanFactory must be set to find endpoint registry by bean name");
                this.endpointRegistry = this.beanFactory.getBean(
                        RabbitListenerConfigUtils.RABBIT_LISTENER_ENDPOINT_REGISTRY_BEAN_NAME,
                        RabbitListenerEndpointRegistry.class);
            }
            this.registrar.setEndpointRegistry(this.endpointRegistry);
        }

        if (this.defaultContainerFactoryBeanName != null) {
            this.registrar.setContainerFactoryBeanName(this.defaultContainerFactoryBeanName);
        }

        // Set the custom handler method factory once resolved by the configurer
        MessageHandlerMethodFactory handlerMethodFactory = this.registrar.getMessageHandlerMethodFactory();
        if (handlerMethodFactory != null) {
            this.messageHandlerMethodFactory.setMessageHandlerMethodFactory(handlerMethodFactory);
        }

        // Actually register all listeners
        this.registrar.afterPropertiesSet();

    }

    /**
     * An {@link MessageHandlerMethodFactory} adapter that offers a configurable underlying
     * instance to use. Useful if the factory to use is determined once the endpoints
     * have been registered but not created yet.
     *
     * @see RabbitListenerEndpointRegistrar#setMessageHandlerMethodFactory
     */
    private class RabbitHandlerMethodFactoryAdapter implements MessageHandlerMethodFactory {

        private MessageHandlerMethodFactory messageHandlerMethodFactory;

        RabbitHandlerMethodFactoryAdapter() {
            super();
        }

        @Override
        public InvocableHandlerMethod createInvocableHandlerMethod(Object bean, Method method) {
            return getMessageHandlerMethodFactory().createInvocableHandlerMethod(bean, method);
        }

        private MessageHandlerMethodFactory getMessageHandlerMethodFactory() {
            if (this.messageHandlerMethodFactory == null) {
                this.messageHandlerMethodFactory = createDefaultMessageHandlerMethodFactory();
            }
            return this.messageHandlerMethodFactory;
        }

        public void setMessageHandlerMethodFactory(MessageHandlerMethodFactory rabbitHandlerMethodFactory1) {
            this.messageHandlerMethodFactory = rabbitHandlerMethodFactory1;
        }

        private MessageHandlerMethodFactory createDefaultMessageHandlerMethodFactory() {
            DefaultMessageHandlerMethodFactory defaultFactory = new DefaultMessageHandlerMethodFactory();
            defaultFactory.setBeanFactory(RabbitBrokerEventBus.this.beanFactory);
            defaultFactory.afterPropertiesSet();
            return defaultFactory;
        }

    }
}
